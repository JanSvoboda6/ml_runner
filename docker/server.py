import os
from flask import Flask, request, render_template
import requests

app = Flask(__name__)

@app.route('/', methods=['POST'])
def handle_form():
    print("Posted file: {}".format(request.files['file']))
    file = request.files['file']
    f = open("text.txt", "wb")
    f.write(file.read())
    f.close()
    print(request.form['field1'])
    return ""
    
@app.route('/')
def hello_world():
    return 'Hello, Docker!'

if __name__ == "__main__":
    app.run(host='0.0.0.0', port=9999, debug=True)
