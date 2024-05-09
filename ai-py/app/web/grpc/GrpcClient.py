#! /usr/bin/env python
# -*- coding: utf-8 -*-
# from __future__ import print_function
import sys

import grpc

# 仅供测试用的客户端代码
def create(host, port):
    channel = grpc.insecure_channel(host + ':' + port)
    try:
        grpc.channel_ready_future(channel).result(timeout=10)
    except grpc.FutureTimeoutError:
        sys.exit('Error connecting to server')
    return channel

