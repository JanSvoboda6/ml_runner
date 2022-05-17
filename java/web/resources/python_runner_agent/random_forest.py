import json
import sys
import traceback
from glob import glob

import numpy as np
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score, classification_report, confusion_matrix
from sklearn.model_selection import train_test_split

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

    criterion = get_hyper_parameter_value(configuration['hyperParameters'], 'criterion')
    n_estimators = int(get_hyper_parameter_value(configuration['hyperParameters'], 'numberOfEstimators'))
    max_depth = int(get_hyper_parameter_value(configuration['hyperParameters'], 'maximumDepth'))

    inform_on_status_change(runner_id, Status.LOADING_DATA)

    classification_labels = configuration['classificationLabels']
    classification_label_names = [label['labelName'] for label in classification_labels]

    samples = []
    labels = []
    for idx, label in enumerate(classification_labels):
        for file in glob(ROOT_DIRECTORY + label['folderPath'] + '*.npy'):
            sample = np.load(file, allow_pickle=True)
            samples.append(sample)
            labels.append(label['labelName'])

    samples = np.array(samples)
    labels = np.array(labels)

    training_samples, testing_samples, training_labels, testing_labels = train_test_split(samples, labels,
                                                                                          test_size=0.2, random_state=1)
    inform_on_status_change(runner_id, Status.TRAINING)
    classifier = RandomForestClassifier(verbose=0, criterion=criterion, n_estimators=n_estimators, max_depth=max_depth)
    classifier.fit(training_samples, training_labels)

    inform_on_status_change(runner_id, Status.PREDICTING)

    predicted_labels = classifier.predict(testing_samples)
    accuracy = accuracy_score(testing_labels, predicted_labels)

    print('ACCURACY: ' + str(accuracy))
    print()

    print('CLASSIFICATION REPORT:')
    print(classification_report(testing_labels, predicted_labels, labels=classification_label_names))

    print('CONFUSION MATRIX: ')
    print_cm(confusion_matrix(y_true=testing_labels, y_pred=predicted_labels, labels=classification_label_names), classification_label_names)
    print()

    inform_on_status_change(runner_id, Status.FINISHED)


def print_cm(cm, labels, hide_zeroes=False, hide_diagonal=False, hide_threshold=None):
    """pretty print for confusion matrixes"""
    columnwidth = max([len(x) for x in labels] + [5])  # 5 is value length
    empty_cell = " " * columnwidth

    # Begin CHANGES
    fst_empty_cell = (columnwidth - 3) // 2 * " " + "t/p" + (columnwidth - 3) // 2 * " "

    if len(fst_empty_cell) < len(empty_cell):
        fst_empty_cell = " " * (len(empty_cell) - len(fst_empty_cell)) + fst_empty_cell
    # Print header
    print("    " + fst_empty_cell, end=" ")
    # End CHANGES

    for label in labels:
        print("%{0}s".format(columnwidth) % label, end=" ")

    print()
    # Print rows
    for i, label1 in enumerate(labels):
        print("    %{0}s".format(columnwidth) % label1, end=" ")
        for j in range(len(labels)):
            cell = "%{0}.1f".format(columnwidth) % cm[i, j]
            if hide_zeroes:
                cell = cell if float(cm[i, j]) != 0 else empty_cell
            if hide_diagonal:
                cell = cell if i != j else empty_cell
            if hide_threshold:
                cell = cell if cm[i, j] > hide_threshold else empty_cell
            print(cell, end=" ")
        print()


def get_hyper_parameter_value(hyper_parameters, hyper_parameter_name):
    for hyper_parameter in hyper_parameters:
        if hyper_parameter['name'] == hyper_parameter_name:
            return hyper_parameter['value']


if __name__ == "__main__":
    try:
        runner_id = int(sys.argv[1])
        run()
    except:
        traceback.print_exc()
        inform_on_status_change(runner_id, Status.FAILED)

