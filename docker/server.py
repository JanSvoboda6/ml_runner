import os
from pathlib import Path
from flask import Flask, request
from flask import jsonify
import math
import subprocess
from shutil import rmtree

app = Flask(__name__)

ROOT_DIRECTORY = 'files/'


@app.route('/upload', methods=['POST'])
def handle_form():
    for key in request.files.keys():
        file = request.files[key]
        f = open(ROOT_DIRECTORY + file.name, "wb")
        f.write(file.read())
        f.close()
    return ""


@app.route('/createdirectory', methods=['POST'])
def create_directory():
    directory_name = request.get_json()['key']
    Path(ROOT_DIRECTORY + directory_name).mkdir(parents=True, exist_ok=True)
    return ""


@app.route('/getfiles')
def get_files():
    directories, files = walk_directory(ROOT_DIRECTORY)
    return jsonify({'directories': directories, 'files': files})


# @app.route('/files<key>', methods=['DELETE'])
# def delete_file(key):
#     os.remove(ROOT_DIRECTORY + key)
#
#
# @app.route('/folders<key>', methods=['DELETE'])
# def delete_folder(key):
#     rmtree(ROOT_DIRECTORY + key)


@app.route('/files/delete', methods=['POST'])
def batch_delete_files():
    file_names = request.get_json()['keys']
    for file_name in file_names:
        os.remove(ROOT_DIRECTORY + file_name)
    return ""

@app.route('/folders/delete', methods=['POST'])
def batch_delete_folders():
    folder_names = request.get_json()['keys']
    print(folder_names)
    for folder_name in folder_names:
        print(ROOT_DIRECTORY + folder_name)
        rmtree(ROOT_DIRECTORY + folder_name)
    return ""


def walk_directory(root_directory):
    directories_list = []
    files_list = []
    for top, directories, files in os.walk(root_directory):
        shifted_root_directory = top.replace(root_directory, '', 1)
        for directory in directories:
            directories_list.append(os.path.join(shifted_root_directory, directory + '/'))
        for file in files:
            file_information = {'key': os.path.join(shifted_root_directory, file),
                                'size': os.path.getsize(os.path.join(top, file)),
                                'modified': math.floor(os.path.getmtime(os.path.join(top, file)))
                                }

            files_list.append(file_information)

    return directories_list, files_list


@app.route('/runproject', methods=['POST'])
def run_project():
    runner = request.get_json()

    log_file = open('log_' + str(f"{runner['projectId']}") + '_' + str(f"{runner['runnerId']}") + '.txt', 'w')
    if runner['selectedModel'] == "Support Vector Machines":
        subprocess.Popen(['nohup', 'python3', 'models/svm.py', runner['name'], runner['firstLabel'],
                          runner['secondLabel'], runner['firstLabelFolder'], runner['secondLabelFolder'],
                          str(runner['gammaParameter']), str(runner['cParameter'])],
                         stdout=log_file,
                         stderr=log_file,
                         preexec_fn=os.setpgrp)
    return ""


@app.route('/project/runner/finished', methods=['POST'])
def is_finished():
    runner = request.get_json()
    finished = False
    with open('log_' + f"{runner['projectId']}" + '_' + f"{runner['runnerId']}" + '.txt', 'r') as log_file:
        for line in log_file:
            if 'FINISHED' in line.split():
                finished = True

    return jsonify({'isFinished': finished})


@app.route('/project/runner/result', methods=['POST'])
def get_result():
    runner = request.get_json()
    first_label_accuracy = 0
    second_label_accuracy = 0
    with open('log_' + f"{runner['projectId']}" + '_' + f"{runner['runnerId']}" + '.txt', 'r') as log_file:
        for line in log_file:
            if 'FIRST_LABEL_ACCURACY:' in line.split():
                first_label_accuracy = line.split()[1]
            elif 'SECOND_LABEL_ACCURACY:' in line.split():
                second_label_accuracy = line.split()[1]

    return jsonify({'firstLabelResult': first_label_accuracy, 'secondLabelResult': second_label_accuracy})


if __name__ == "__main__":
    app.run(host='0.0.0.0', port=9999, debug=True)
