#!usr/bin/env python
# -*- coding: utf-8 -*-

import os
import argparse
from setproctitle import setproctitle
from pathlib import Path


from flask_cors import CORS  # 引用CORS，VUE支持跨域访问会用到
from flask import Flask, render_template
from config import config
import flask_excel as excel
from app import CustomJSONEncoder, moment, db, create_server


def set_args():
    """设置所需参数"""
    parser = argparse.ArgumentParser()
    parser.add_argument('--device', default='_', type=str, help='设置预测时使用的显卡,使用CPU设置成_即可')
    parser.add_argument('--host', type=str, default="0.0.0.0", help='IP地址')
    parser.add_argument('--port', type=int, default=7000, help='端口号')
    parser.add_argument('--processes', type=int, default=1, help='进程数')
    parser.add_argument('--threaded', type=int, default=1, help='是否开启线程')
    parser.add_argument('-c', '--config', type=str, default="./configs/base.json", help='JSON file for configuration')
    # parser.add_argument('-m', '--model', type=str, required=True, help='Model name')
    return parser.parse_args()

if __name__ == '__main__':
    setproctitle(Path(__file__).stem)
    args = set_args()

    server = create_server(os.getenv('FLASK_CONFIG') or 'default')
    CORS(server, resources=r'/*')  # 全局配置允许跨域的API接口

    os.environ['CUDA_VISIBLE_DEVICES'] = '-1' if args.device in {'_', '-1'} else args.device

    # server.run(host=args.host, port=args.port, debug=False, processes=args.processes, threaded=args.threaded)
    server.run(host='0.0.0.0', port=8000)
    # 多进程
    # server.start()
    # for i in range(6):
    #     # Process(target=serve_forever, args=(server,)).start()
    #     p = Process(target=serve_forever, args=(server,))
    #     p.start()