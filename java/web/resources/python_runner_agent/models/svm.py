import sys
import traceback
from sklearn import svm
from helpers.status import status_change, Status
from helpers.data import load_data, split_data
from helpers.result_printer import print_metrics
from helpers.configuration_reader import read_configuration, get_svm_hyper_parameters, \
    get_classification_labels_with_name


def run(runner_id):
    configuration = read_configuration(runner_id)
    c, gamma, kernel = get_svm_hyper_parameters(configuration)
    classification_labels, classification_label_names = get_classification_labels_with_name(configuration)

    status_change(runner_id, Status.LOADING_DATA)
    samples, labels = load_data(classification_labels)
    training_samples, testing_samples, training_labels, testing_labels = split_data(samples, labels)

    status_change(runner_id, Status.TRAINING)
    classifier = svm.SVC(verbose=0, gamma=gamma, C=c, kernel=kernel)
    classifier.fit(training_samples, training_labels)

    status_change(runner_id, Status.PREDICTING)
    predicted_labels = classifier.predict(testing_samples)
    print_metrics(classification_label_names, predicted_labels, testing_labels)

    status_change(runner_id, Status.FINISHED)


if __name__ == "__main__":
    try:
        runner_identifier = int(sys.argv[1])
        run(runner_identifier)
    except:
        traceback.print_exc()
        status_change(runner_identifier, Status.FAILED)
