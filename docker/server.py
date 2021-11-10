import os
from flask import Flask, request
from flask import jsonify
import math

app = Flask(__name__)

ROOT_DIRECTORY = '/Users/jan/app_files'


@app.route('/', methods=['POST'])
def handle_form():
    print("Posted file: {}".format(request.files['file']))
    file = request.files['file']
    f = open("files/text.txt", "wb")
    f.write(file.read())
    f.close()
    print(request.form['field1'])
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
            directories_list.append(directory)
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


if __name__ == "__main__":
    app.run(host='0.0.0.0', port=9999, debug=True)
