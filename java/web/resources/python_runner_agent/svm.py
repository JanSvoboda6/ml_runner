import sys
import time
import traceback
from glob import glob
import numpy as np
from sklearn import svm
from sklearn.metrics import accuracy_score
from sklearn.model_selection import train_test_split
import math
import json

ROOT_DIRECTORY = 'files/'


class Status:
    INITIAL = 'INITIAL'
    LOADING_DATA = 'PREPARING_DATA'
    TRAINING = 'TRAINING'
    PREDICTING = 'PREDICTING'
    FINISHED = 'FINISHED'
    FAILED = 'FAILED'
    CANCELLED = 'CANCELLED'


def inform_on_status_change(runner_identifier, status):
    with open('runners_info/' + str(runner_identifier) + '/status.txt', 'a') as status_file:
        status_file.write(status + '\n')


def run():
    with open('runners_info/' + str(runner_id) + '/configuration.json', 'r') as json_file:
        configuration = json.load(json_file)

    gamma = configuration['gammaParameter']
    c = configuration['cParameter']

    inform_on_status_change(runner_id, Status.LOADING_DATA)

    classification_labels = configuration['classificationLabels']

    samples = []
    labels = []
    for idx, label in enumerate(classification_labels):
        for file in glob(ROOT_DIRECTORY + label['folderPath'] + '*.npy'):
            sample = np.load(file)
            samples.append(sample)
            labels.append(idx)

    samples = np.array(samples)
    labels = np.array(labels)

    training_samples, testing_samples, training_labels, testing_labels = train_test_split(samples, labels,
                                                                                          test_size=0.2)

    inform_on_status_change(runner_id, Status.TRAINING)
    classifier = svm.SVC(verbose=0, gamma=gamma, C=c)
    classifier.fit(training_samples, training_labels)

    inform_on_status_change(runner_id, Status.PREDICTING)

    accuracy = accuracy_score(testing_labels, classifier.predict(testing_samples))

    print("FIRST_LABEL_ACCURACY: " + str(accuracy))
    print("SECOND_LABEL_ACCURACY: " + str(accuracy))

    print('ACCURACY: ' + str(accuracy))

    inform_on_status_change(runner_id, Status.FINISHED)
    print('FINISHED')


if __name__ == "__main__":
    try:
        runner_id = int(sys.argv[1])
        run()
    except:
        traceback.print_exc()
        inform_on_status_change(runner_id, Status.FAILED)

