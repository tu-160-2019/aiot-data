from datetime import datetime
from distutils.command.config import config

from ..base import base
from ..entitys import DemoEntity

from flask import render_template, request, jsonify, Response
# from flask_login import login_required, current_user
import flask_excel as excel
from sqlalchemy import asc
from sqlalchemy import desc
from app import  db

# from app import permission

# @base.route('/uploader', methods=['GET', 'POST'])#设置请求路由
# def upload_file(request):
#     if request.method == 'POST':
#         #如果请求方法是POST
#         f = request.files['file']
#         f.save("./images_rec/"+secure_filename(f.filename))
#         #保存请求上来的文件
#         t0 = time.time()
#         res = recognize("./images_rec/"+secure_filename(f.filename))
#         print("识别时间",time.time() - t0)
#         return res
#         #返回识别结果
#
#         # return 'file uploaded successfully'
#     return render_template('upload.html')
