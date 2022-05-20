import json


def read_configuration(runner_id):
    with open('runners_info/' + str(runner_id) + '/configuration.json', 'r') as json_file:
        configuration = json.load(json_file)
    return configuration


def get_hyper_parameter_value(hyper_parameters, hyper_parameter_name):
    for hyper_parameter in hyper_parameters:
        if hyper_parameter['name'] == hyper_parameter_name:
            return hyper_parameter['value']


def get_classification_labels_with_name(configuration):
    classification_labels = configuration['classificationLabels']
    classification_label_names = [label['labelName'] for label in classification_labels]
    return classification_labels, classification_label_names


def get_svm_hyper_parameters(configuration):
    gamma = float(get_hyper_parameter_value(configuration['hyperParameters'], 'gamma'))
    c = float(get_hyper_parameter_value(configuration['hyperParameters'], 'c'))
    kernel = get_hyper_parameter_value(configuration['hyperParameters'], 'kernel')
    return c, gamma, kernel


def get_random_forest_hyper_parameters(configuration):
    criterion = get_hyper_parameter_value(configuration['hyperParameters'], 'criterion')
    n_estimators = int(get_hyper_parameter_value(configuration['hyperParameters'], 'numberOfEstimators'))
    max_depth = int(get_hyper_parameter_value(configuration['hyperParameters'], 'maximumDepth'))
    return criterion, n_estimators, max_depth
