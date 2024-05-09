import socket
import sys
from threading import Thread
from tkinter import *
'''
socket client
'''
# 配置服务器参数
HOST = 'localhost'  # 设置服务器IP地址
PORT = 5000  # 设置服务器端口
BUFFER_SIZE = 1024  # 设置缓冲区大小

stop = False

client_socket = None

# def get_message():
#     while True:
#         if not client_socket or not client_socket.fileno() == client_socket.fileno():
#             break
#         try:
#             response = client_socket.recv(BUFFER_SIZE)
#             if not response:
#                 break
#             print('receive server message===' + response.decode('utf-8'))
#             return response.decode('utf-8')
#         except OSError as e:
#             print(f"Error receiving message: {e}")
#             break
#     return ''
def get_message():
    try:
        response = client_socket.recv(BUFFER_SIZE)
        if not response:
            return ''
        print('receive server message===' + response.decode('utf-8'))
        return response.decode('utf-8')
    except OSError as e:
        print(f"Error receiving message: {e}")

    return ''

def send_message(message):
    try:
        if not client_socket or not client_socket.fileno() == client_socket.fileno():
            pass
        client_socket.sendall(message.encode('utf-8'))
    except OSError as e:
        print(f"Error sending message: {e}")

def start(host, port):
    try:
        global client_socket
        client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        client_socket.connect((host, port))
        print(f"Connected to server {host}:{port}")

        # sm_thread = Thread(target=send_message, args=('my client',))
        # sm_thread.start()
        # gm_thread = Thread(target=get_message)
        # gm_thread.start()
        # sm_thread.join()
        # gm_thread.join()

    except ConnectionRefusedError:
        print(f"Connection to socket server1111111111111 {host}:{port} refused.")
    except KeyboardInterrupt:
        stop = True
        print("Exiting the client due to user interrupt...")
    except Exception as e:
        print(f"An unexpected error occurred: {e}")


if __name__ == '__main__':
    start('127.0.0.1', 5000)

    send_message('my client')
    print(get_message())
