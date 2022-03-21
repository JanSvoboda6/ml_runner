import sys
import time
from glob import glob
import numpy as np
from sklearn import svm
from sklearn.metrics import accuracy_score
import math
import requests

ROOT_DIRECTORY = 'files/'


class Status:
    LOADING_DATA = 'PREPARING_DATA'
    TRAINING = 'TRAINING'
    PREDICTING = 'PREDICTING'
    FINISHED = 'FINISHED'
    FAILED = 'FAILED'


def inform_on_status_change(runner_identifier, status):
    with open(str(runner_identifier) + '_status.txt', 'a') as status_file:
        status_file.write(status + '\n')

    # print(status)
    # request_json = '{"runnerId":' + str(runner_identifier) + ', "status":"' + status + '"}'
    # print(request_json)
    # requests.post("http://127.0.0.1/api/runner/status", headers={"Content-Type": "application/json"}, data=request_json)


if __name__ == "__main__":
    first_label_folder = sys.argv[4]
    second_label_folder = sys.argv[5]
    gamma = float(sys.argv[6])
    c = float(sys.argv[7])
    runner_id = int(sys.argv[8])

    inform_on_status_change(runner_id, Status.LOADING_DATA)

    first_class_samples = []
    second_class_samples = []

    print(first_label_folder)
    print(second_label_folder)

    for filename in glob(ROOT_DIRECTORY + first_label_folder + '*.npy'):
        sample = np.load(filename)
        first_class_samples.append(sample)

    for filename in glob(ROOT_DIRECTORY + second_label_folder + '*.npy'):
        sample = np.load(filename)
        second_class_samples.append(sample)

    first_class_samples = np.array(first_class_samples)
    second_class_samples = np.array(second_class_samples)

    number_of_first_class_samples = len(first_class_samples)
    number_of_second_class_samples = len(second_class_samples)

    ten_percent_of_first_class_samples = math.floor(number_of_first_class_samples * 0.1)
    first_class_training = first_class_samples[: number_of_first_class_samples - ten_percent_of_first_class_samples]
    first_class_testing = first_class_samples[-ten_percent_of_first_class_samples:]

    ten_percent_of_second_class_samples = math.floor(number_of_second_class_samples * 0.1)
    second_class_training = second_class_samples[:number_of_second_class_samples - ten_percent_of_second_class_samples]
    second_class_testing = second_class_samples[-ten_percent_of_second_class_samples:]

    labels_training_first_class = np.ones(len(first_class_training))
    labels_training_second_class = np.zeros(len(second_class_training))

    labels_testing_first_class = np.ones(len(first_class_testing))
    labels_testing_second_class = np.zeros(len(second_class_testing))

    training_samples = np.concatenate((first_class_training, second_class_training), axis=0)
    training_labels = np.concatenate((labels_training_first_class, labels_training_second_class))

    inform_on_status_change(runner_id, Status.TRAINING)
    classifier = svm.SVC(verbose=0, gamma=gamma, C=c)
    classifier.fit(training_samples, training_labels)

    inform_on_status_change(runner_id, Status.PREDICTING)
    accuracy_of_predicting_first_class = accuracy_score(
        labels_testing_first_class, classifier.predict(first_class_testing))

    accuracy_of_predicting_second_class = accuracy_score(
        labels_testing_second_class, classifier.predict(second_class_testing))

    print("FIRST_LABEL_ACCURACY: " + str(accuracy_of_predicting_first_class))
    print("SECOND_LABEL_ACCURACY: " + str(accuracy_of_predicting_second_class))

    inform_on_status_change(runner_id, Status.FINISHED)
    print('FINISHED')
