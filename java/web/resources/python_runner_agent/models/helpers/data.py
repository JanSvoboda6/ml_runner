from glob import glob
import numpy as np
from sklearn.model_selection import train_test_split

ROOT_DIRECTORY = 'files/'
TEST_DATA_RATIO = 0.2
RANDOM_STATE = 1


def load_data(classification_labels):
    samples = []
    labels = []
    for idx, label in enumerate(classification_labels):
        for file in glob(ROOT_DIRECTORY + label['folderPath'] + '*.npy'):
            sample = np.load(file, allow_pickle=True)
            samples.append(sample)
            labels.append(label['labelName'])
    return np.array(labels), np.array(samples)


def split_data(labels, samples):
    return train_test_split(samples, labels, test_size=TEST_DATA_RATIO, random_state=RANDOM_STATE)
