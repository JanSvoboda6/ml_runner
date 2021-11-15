import os
from pathlib import Path
from flask import Flask, request
from flask import jsonify
import math
import subprocess

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
    print(files)
    return jsonify({'directories': directories, 'files': files})


def walk_directory(root_directory):
    directories_list = []
    files_list = []
    for top, directories, files in os.walk(root_directory):
        for directory in directories:
            directories_list.append(directory + '/')
        for file in files:
            shifted_root_directory = top.replace(root_directory, '', 1) + '/'
            if(shifted_root_directory == '/'):
                shifted_root_directory = ''

            file_information = {'key': shifted_root_directory + file,
                                'size': os.path.getsize(os.path.join(top, file)),
                                'modified': math.floor(os.path.getmtime(os.path.join(top, file)))
                                }

            files_list.append(file_information)

    return directories_list, files_list


@app.route('/runproject', methods=['POST'])
def run_project():
    project = request.get_json()
    print(project)
    log_file = open('log_' + f"{project['id']}" + '.txt', 'w')
    if(project['selectedModel'] == "Support Vector Machines"):
        subprocess.Popen(['nohup', 'python3', 'models/svm.py', project['name'], project['firstLabel'],
              project['secondLabel'], project['firstLabelFolder'], project['secondLabelFolder']],
              stdout=log_file, 
              stderr=log_file,
              preexec_fn=os.setpgrp)
    return ""


@app.route('/project/finished', methods=['POST'])
def isFinished():
    isFinished = False
    with open('log_1.txt', 'r') as log_file:
        for line in log_file:
            if 'FINISHED' in line.split():
                isFinished = True
    
    print(isFinished)
    return jsonify({'isFinished' : isFinished})


@app.route('/project/result', methods=['POST'])
def getResult():
    print(request.get_json()['id'])
    return jsonify({'firstLabelResult': 1.0, 'secondLabelResult': 0.9})



if __name__ == "__main__":
    app.run(host='0.0.0.0', port=9999, debug=True)

