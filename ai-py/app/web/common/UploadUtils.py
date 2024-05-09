from werkzeug.utils import secure_filename
import time

from flask import Flask, render_template, request

def upload_file(request):
    if request.method == 'POST':
        #如果请求方法是POST
        f = request.files['file']
        f.save("./images_rec/" + secure_filename(f.filename))
        return 'file uploaded successfully'
    return 'file uploaded fail'