import os
from pathlib import Path
from flask import Flask, jsonify, request, send_file, make_response, Response
import math
import subprocess
from shutil import rmtree
import zipfile
import io
import json

app = Flask(__name__)

ROOT_DIRECTORY = 'files/'


@app.route('/upload', methods=['POST'])
def upload():
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
    for folder_name in folder_names:
        rmtree(ROOT_DIRECTORY + folder_name)
    return ""


@app.route('/files/move', methods=['POST'])
def move_file():
    move(request.get_json()['key'], request.get_json()['newKey'])
    return ""


@app.route('/folders/move', methods=['POST'])
def move_folder():
    move(request.get_json()['key'], request.get_json()['newKey'])
    return ""


@app.route('/download', methods=['POST'])
def download():
    file_folder_keys = request.get_json()['keys']
    files_to_zip = []
    for path in file_folder_keys:
        full_path = os.path.join(ROOT_DIRECTORY, path)
        if full_path.endswith('/'):
            files = list_files_in_directory(full_path)
            for file in files:
                if file not in files_to_zip:
                    files_to_zip.append(file)
        else:
            if path not in files_to_zip:
                files_to_zip.append(path)

    archive = io.BytesIO()
    with zipfile.ZipFile(archive, 'w') as zip_file:
        for file_to_zip in files_to_zip:
            zip_file.write(os.path.join(ROOT_DIRECTORY, file_to_zip))

    archive.seek(0)

    return Response(archive.getvalue(),
                    mimetype='application/zip',
                    headers={'Content-Disposition': 'attachment;filename=files.zip'})


def list_files_in_directory(directory):
    file_names = []
    for path, subdirs, files in os.walk(directory):
        for name in files:
            file_names.append(os.path.join(path, name).replace(ROOT_DIRECTORY, '', 1))
    return file_names


def move(old_key, new_key):
    os.rename(os.path.join(ROOT_DIRECTORY, old_key), os.path.join(ROOT_DIRECTORY, new_key))


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

    Path('runners_info/' + str(runner['runnerId'])).mkdir(parents=True, exist_ok=True)

    with open('runners_info/' + str(runner['runnerId']) + '/configuration.json', 'w') as json_file:
        json.dump(runner, json_file)

    with open('runners_info/' + str(runner['runnerId']) + '/status.txt', 'w') as status_file:
        status_file.write('INITIAL' + '\n')

    log_file = open('runners_info/' + str(runner['runnerId']) + '/log.txt', 'w')

    if runner['selectedModel'] == "Support Vector Machines":
        subprocess.Popen(['nohup', 'python3', 'models/svm.py', str(runner['runnerId'])],
                         stdout=log_file,
                         stderr=log_file,
                         preexec_fn=os.setpgrp)
    return ""


@app.route('/project/runner/status', methods=['POST'])
def get_status():
    runner_id = request.get_json()['runnerId']
    statuses = []
    with open('runners_info/' + str(runner_id) + '/status.txt', 'r') as status_file:
        for line in status_file:
            statuses.append(line.replace('\n', ''))
    return jsonify({'chronologicalStatuses': statuses})


@app.route('/project/runner/finished', methods=['POST'])
def is_finished():
    runner = request.get_json()
    finished = False
    with open('runners_info/' + str(runner['runnerId']) + '/log.txt', 'r') as log_file:
        for line in log_file:
            if 'FINISHED' in line.split():
                finished = True

    return jsonify({'isFinished': finished})


@app.route('/project/runner/result', methods=['POST'])
def get_result():
    runner = request.get_json()
    first_label_accuracy = 0
    second_label_accuracy = 0
    accuracy = 0
    with open('runners_info/' + str(runner['runnerId']) + '/log.txt', 'r') as log_file:
        for line in log_file:
            if 'FIRST_LABEL_ACCURACY:' in line.split():
                first_label_accuracy = line.split()[1]
            elif 'SECOND_LABEL_ACCURACY:' in line.split():
                second_label_accuracy = line.split()[1]
            elif 'ACCURACY:' in line.split():
                accuracy = line.split()[1]

    return jsonify({'firstLabelResult': first_label_accuracy, 'secondLabelResult': second_label_accuracy, 'accuracy': accuracy})


if __name__ == "__main__":
    app.run(host='0.0.0.0', port=9999, debug=True)
