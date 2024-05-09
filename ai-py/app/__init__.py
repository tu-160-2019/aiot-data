from flask_moment import Moment
from flask_sqlalchemy import SQLAlchemy
from flask import Flask, render_template, request, Response, jsonify
import flask_excel as excel

from config import config

import json
from datetime import datetime, date
# from flask_login import current_user, LoginManager
from flask import jsonify
from functools import wraps

# loginmanager = LoginManager()
# loginmanager.session_protection = 'strong'
# loginmanager.login_view = 'base.login'

# 简化日期和时间操作
moment = Moment()
# 初始化orm
db = SQLAlchemy()

# def permission(permission_id):
#     def need_permission(func):
#         @wraps(func)
#         def inner(*args, **kargs):
#             if not current_user.ID:
#                 return jsonify(401, {"msg": "认证失败，无法访问系统资源"})
#             else:
#                 resources = []
#                 resourceTree = []
#
#                 resources += [res for org in current_user.organizations for res in org.resources if org.resources]
#                 resources += [res for role in current_user.roles for res in role.resources if role.resources]
#
#                 # remove repeat
#                 new_dict = dict()
#                 for obj in resources:
#                     if obj.ID not in new_dict:
#                         new_dict[obj.ID] = obj
#
#                 for resource in new_dict.values():
#                     resourceTree.append(resource.PERMS)
#                 if permission_id in resourceTree:
#                     return func(*args, **kargs)
#                 else:
#                     return jsonify({'msg': '当前操作没有权限', 'code': 403})
#
#         return inner
#
#     return need_permission


JSONEncoder = json.JSONEncoder

class CustomJSONEncoder(JSONEncoder):
    def default(self, obj):
        if isinstance(obj, datetime):
            return obj.strftime('%Y-%m-%d %H:%M:%S')
        elif isinstance(obj, date):
            return obj.strftime('%Y-%m-%d')
        else:
            return JSONEncoder.default(self, obj)

def create_server(config_name):
    # server = Flask(__name__, template_folder='') # 指定模板的目录
    server = Flask(__name__)  # 指定模板的目录
    #  替换默认的json编码器
    server.json_encoder = CustomJSONEncoder
    server.config.from_object(config[config_name])
    config[config_name].init_app(server)

    moment.init_app(server)
    db.init_app(server)
    # loginmanager.init_app(app)

    from app.web.base import base as base_blueprint
    server.register_blueprint(base_blueprint)
    excel.init_excel(server)

    @server.errorhandler(404)
    def page_not_found(e):
        return render_template('errors/404.html'), 404

    @server.before_request
    def before():
        # print("请求地址：" + str(request.path))
        # print("请求方法：" + str(request.method))
        # print("---请求headers--start--")
        # print(str(request.headers).rstrip())
        # print("---请求headers--end----")
        # print("GET参数：" + str(request.args))
        # print("POST参数：" + str(request.form))

        url = request.path  # 当前请求的URL
        passUrl = ["/login"]
        if url in passUrl:
            pass
        # else:
        #     return jsonify({'msg': '当前操作没有权限', 'code': 403})
        #     _id = session.get("_id", None)
        #     if not _id:
        #         return jsonify(401, {"msg": "认证失败，无法访问系统资源"})
        #     else:
        #         pass

    return server