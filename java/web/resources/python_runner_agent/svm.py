import sys
import time
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

if __name__ == "__main__":
    runner_id = int(sys.argv[1])
    with open('runners_info/' + str(runner_id) + '/configuration.json', 'r') as json_file:
        configuration = json.load(json_file)

    first_label_folder = configuration['classificationLabels'][0]['folderPath']
    second_label_folder = configuration['classificationLabels'][1]['folderPath']
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

    training_samples, testing_samples, training_labels, testing_labels = train_test_split(samples, labels, test_size=0.2)

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
