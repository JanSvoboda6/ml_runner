import os
from pathlib import Path
from flask import Flask, request
from flask import jsonify
import math
import subprocess
from random import uniform


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
def createDirectory():
    directory_name = request.get_json()['key']
    Path(ROOT_DIRECTORY + directory_name).mkdir(parents=True, exist_ok=True)
    return ""


@app.route('/getfiles')
def hello_world():
    directories, files = walk_directory(ROOT_DIRECTORY)
    return jsonify({'directories': directories, 'files': files})


def walk_directory(root_directory):
    directories_list = []
    files_list = []
    for top, directories, files in os.walk(root_directory):
        shifted_root_directory = top.replace(root_directory, '', 1)
        for directory in directories:
            directories_list.append(os.path.join(shifted_root_directory, directory + '/'))
        for file in files:
            file_information = {'key': shifted_root_directory + file,
                                'size': os.path.getsize(os.path.join(top, file)),
                                'modified': math.floor(os.path.getmtime(os.path.join(top, file)))
                                }

            files_list.append(file_information)

    return directories_list, files_list


@app.route('/runproject', methods=['POST'])
def run_project():
    runner = request.get_json()

    log_file = open('log_' + str(f"{runner['projectId']}") + '_' + str(f"{runner['runnerId']}") + '.txt', 'w')
    if(runner['selectedModel'] == "Support Vector Machines"):
        subprocess.Popen(['nohup', 'python3', 'models/svm.py', runner['name'], runner['firstLabel'],
              runner['secondLabel'], runner['firstLabelFolder'], runner['secondLabelFolder'], str(runner['gammaParameter']), str(runner['cParameter'])],
              stdout=log_file,
              stderr=log_file,
              preexec_fn=os.setpgrp)
    return ""


@app.route('/project/runner/finished', methods=['POST'])
def isFinished():
    runner = request.get_json()
    isFinished = False
    with open('log_' + f"{runner['projectId']}" + '_' + f"{runner['runnerId']}" + '.txt', 'r') as log_file:
        for line in log_file:
            if 'FINISHED' in line.split():
                isFinished = True

    return jsonify({'isFinished' : isFinished})


@app.route('/project/runner/result', methods=['POST'])
def getResult():
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
