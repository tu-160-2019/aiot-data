# -*- coding: utf-8 -*-

import time

import grpc
from concurrent import futures

_ONE_DAY_IN_SECONDS = 60 * 60 * 24

class GrpcServer:

    def __init__(self):
        pass

    def createServer(self, port):
        self.grpcServer = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
        # server.add_insecure_port('[::]:5000')
        self.grpcServer.add_insecure_port('[::]:' + port)
        # self.grpcServer.add_insecure_port('0.0.0.0:' + port)
        # self.grpcServer.add_insecure_port('127.0.0.1:' + port)
        # self.grpcServer.add_secure_port() # tls
        return self.grpcServer

    def start(self):
        self.grpcServer.start()
        try:
            self.grpcServer.wait_for_termination()
        except Exception as e:
            self.grpcServer.stop(0)

    def stop(self):
        self.grpcServer.stop(0)

