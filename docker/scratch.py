import os
from pathlib import Path
from flask import Flask, jsonify, request, send_file, make_response
import math
import subprocess
from shutil import rmtree
import zipfile
import io


ROOT_DIRECTORY = 'files/'

def compress(keys):
    file_folder_keys = keys
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
    response = make_response(archive.read())
    response.headers.set('Content-Type', 'zip')
    response.headers.set('Content-Disposition', 'attachment', filename='files.zip')
    return response


def list_files_in_directory(directory):
    file_names = []
    for path, subdirs, files in os.walk(directory):
        for name in files:
            file_names.append(os.path.join(path, name).replace(ROOT_DIRECTORY, '', 1))
    return file_names


if __name__ == "__main__":
    print(compress(['AAA/', 'AAA/CCC/ccc.txt']))
