from sklearn.metrics import classification_report, confusion_matrix, accuracy_score


def print_metrics(classification_label_names, predicted_labels, testing_labels):
    accuracy = accuracy_score(testing_labels, predicted_labels)
    print('ACCURACY: ' + str(accuracy))
    print()

    print('CLASSIFICATION REPORT:')
    print(classification_report(testing_labels, predicted_labels, labels=classification_label_names))

    print('CONFUSION MATRIX: ')
    print_confusion_matrix(
        confusion_matrix(y_true=testing_labels, y_pred=predicted_labels, labels=classification_label_names),
        classification_label_names)


def print_confusion_matrix(cm, labels, hide_zeroes=False, hide_diagonal=False, hide_threshold=None):
    column_width = max([len(x) for x in labels] + [5])  # 5 is value length
    empty_cell = " " * column_width

    fst_empty_cell = (column_width - 3) // 2 * " " + "t/p" + (column_width - 3) // 2 * " "

    if len(fst_empty_cell) < len(empty_cell):
        fst_empty_cell = " " * (len(empty_cell) - len(fst_empty_cell)) + fst_empty_cell
    print("    " + fst_empty_cell, end=" ")

    for label in labels:
        print("%{0}s".format(column_width) % label, end=" ")

    print()
    for i, label1 in enumerate(labels):
        print("    %{0}s".format(column_width) % label1, end=" ")
        for j in range(len(labels)):
            cell = "%{0}.1f".format(column_width) % cm[i, j]
            if hide_zeroes:
                cell = cell if float(cm[i, j]) != 0 else empty_cell
            if hide_diagonal:
                cell = cell if i != j else empty_cell
            if hide_threshold:
                cell = cell if cm[i, j] > hide_threshold else empty_cell
            print(cell, end=" ")
        print()
