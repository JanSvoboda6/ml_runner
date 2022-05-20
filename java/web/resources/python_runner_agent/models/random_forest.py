import sys
import traceback
from sklearn.ensemble import RandomForestClassifier
from helpers.status import status_change, Status
from helpers.data import load_data, split_data
from helpers.result_printer import print_metrics
from helpers.configuration_reader import read_configuration, get_random_forest_hyper_parameters, \
    get_classification_labels_with_name


def run(runner_id):
    configuration = read_configuration(runner_id)
    criterion, n_estimators, max_depth = get_random_forest_hyper_parameters(configuration)
    classification_labels, classification_label_names = get_classification_labels_with_name(configuration)

    status_change(runner_id, Status.LOADING_DATA)
    labels, samples = load_data(classification_labels)
    testing_labels, testing_samples, training_labels, training_samples = split_data(labels, samples)

    status_change(runner_id, Status.TRAINING)
    classifier = RandomForestClassifier(verbose=0, criterion=criterion, n_estimators=n_estimators, max_depth=max_depth)
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

