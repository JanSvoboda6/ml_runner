import sys
from PIL import Image
from glob import glob
import numpy as np
from sklearn import svm
from sklearn.metrics import accuracy_score

if __name__ == "__main__":

    print('Number of arguments:', len(sys.argv), 'arguments.')
    print('Argument List:', str(sys.argv))

    # zeros = []
    # ones = []

    # for filename in glob('/Users/jan/app_files/zeros/*.jpg'):
    #     image = Image.open(filename)
    #     zeros.append(image.getdata())

    # for filename in glob('/Users/jan/app_files/ones/*.jpg'):
    #     image = Image.open(filename)
    #     ones.append(image.getdata())

    # zeros = np.array(zeros)
    # ones = np.array(ones)

    # number_of_zeros = len(zeros)
    # number_of_ones = len(ones)

    # zeros_training = zeros[:number_of_zeros - 30]
    # zeros_testing = zeros[-30:]

    # ones_training = ones[:number_of_ones - 30]
    # ones_testing = ones[-30:]

    # labels_training_zeros = np.zeros(len(zeros_training))
    # labels_training_ones = np.ones(len(ones_training))

    # labels_testing_zeros = np.zeros(len(zeros_testing))
    # labels_testing_ones = np.ones(len(ones_testing))

    # training_data = np.concatenate((zeros_training, ones_training), axis=0)
    # training_labels = np.concatenate(
    #     (labels_training_zeros, labels_training_ones))

    # classifier = svm.SVC(verbose=0)

    # classifier.fit(training_data, training_labels)

    # accuracy_of_predicting_zeros = accuracy_score(
    #     labels_testing_zeros, classifier.predict(zeros_testing))

    # accuracy_of_predicting_ones = accuracy_score(
    #     labels_testing_ones, classifier.predict(ones_testing))

    # print(accuracy_of_predicting_zeros, accuracy_of_predicting_ones)
