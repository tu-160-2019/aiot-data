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


@base.route('/system/config/list', methods=['GET'])
# @login_required
# @permission('system:config:list')
def sys_config_list():
    filters = []
    if 'configName' in request.args:
        filters.append(DemoEntity.config_name.like('%' + request.args['configName'] + '%'))
    if 'configKey' in request.args:
        filters.append(DemoEntity.config_key.like('%' + request.args['configKey'] + '%'))
    if 'configType' in request.args:
        filters.append(DemoEntity.config_type.like('%' + request.args['configType'] + '%'))


    if 'params[beginTime]' in request.args and 'params[endTime]' in request.args:
        filters.append(DemoEntity.create_time >  request.args['params[beginTime]'])
        filters.append(DemoEntity.create_time <  request.args['params[endTime]'])

    page = request.args.get('pageNum', 1, type=int)
    rows = request.args.get('pageSize', 10, type=int)
    pagination = DemoEntity.query.filter(*filters).paginate(page=page, per_page=rows, error_out=False)
    config_list = pagination.items

    return jsonify({'msg': '操作成功', 'code': 200, 'rows': [config.to_json() for config in config_list], 'total': pagination.total})

@base.route('/system/config/<id>', methods=['GET'])
# @login_required
# @permission('system:config:query')
def sysconfig_get_by_id(id):
    config = DemoEntity.query.get(id)

    return jsonify({'msg': '操作成功', 'code': 200, 'data': config.to_json()})

@base.route('/system/config', methods=['PUT'])
# @login_required
# @permission('system:config:edit')
def sysconfig_update():
    config = DemoEntity.query.get(request.json['configId'])

    if 'configKey' in request.json: config.config_key = request.json['configKey']
    if 'configName' in request.json: config.config_name = request.json['configName']
    if 'configType' in request.json: config.config_type = request.json['configType']
    if 'configValue' in request.json: config.config_value = request.json['configValue']
    if 'remark' in request.json: config.remark = request.json['remark']

    config.UPDATEDATETIME = datetime.now()
    # config.update_by = current_user.NAME

    db.session.add(config)

    return jsonify({'msg': '操作成功', 'code': 200})

@base.route('/system/config/<string:ids>', methods=['DELETE'])
# @login_required
# @permission('system:config:remove')
def syconfig_delete(ids):
    idList = ids.split(',')
    for id in idList:
        config = DemoEntity.query.get(id)
        if config:
            db.session.delete(config)

    return jsonify({'code': 200, 'msg': '操作成功'})

@base.route('/system/config/export', methods=['POST'])
# @login_required
# @permission('system:config:export')
def config_export():
    rows = []
    rows.append(['参数主键', '参数名称', '参数键名', '参数键值', '系统内置', '备注', '创建时间'])

    configs = DemoEntity.query.all()
    for config in configs:
        row = []
        row.append(config.config_id)
        row.append(config.config_name)
        row.append(config.config_key)
        row.append(config.config_value)
        if config.config_type == 'Y':
            row.append('是')
        elif config.config_type == 'N':
            row.append('否')
        row.append(config.remark)
        row.append(config.create_time)

        rows.append(row)

    return excel.make_response_from_array(rows, "xlsx",  file_name="config")
