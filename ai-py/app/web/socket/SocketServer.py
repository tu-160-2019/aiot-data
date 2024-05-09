# 导入所需的库
import socket
import threading
import concurrent.futures

'''
tcp socket server
'''

# 配置服务器参数
HOST = '127.0.0.1'  # 设置服务器IP地址
PORT = 5000  # 设置服务器端口
BUFFER_SIZE = 1024  # 设置缓冲区大小
# 创建客户端套接字列表
clients = []
server_socket = None

# 处理客户端连接的函数
def handle_client(client_socket):
    # 添加客户端套接字到列表
    clients.append(client_socket)
    try:
        while True:
            # 接收客户端发来的数据
            data = client_socket.recv(BUFFER_SIZE)

            if not data:
                # 客户端断开连接
                break

            message = data.decode('utf-8')
            print(f"Socket server received message from client: {message}")

            # 将消息广播给所有客户端
            for sock in clients:
                if sock != client_socket:  # 不要将消息发送回发送者
                    sock.sendall(data)
                else: # 发送回执
                    sock.sendall('200'.encode('utf-8'))

    except ConnectionResetError:
        # 客户端强制关闭连接
        print("Client disconnected unexpectedly.")

    finally:
        # 从列表中移除客户端套接字并关闭连接
        clients.remove(client_socket)
        client_socket.close()

def is_client_disconnected():
    for client in clients:
        try:
            client.send(b'')
        except Exception:
            clients.remove(client)
            client.close()

def destory_thread():
    executor = concurrent.futures.ThreadPoolExecutor(max_workers=5)
    future_to_thread = {executor.submit(handle_client, f"Thread-{i}"): f"Thread-{i}" for i in range(5)}
    for future in concurrent.futures.as_completed(future_to_thread):
        thread_name = future_to_thread[future]
        try:
            data = future.result()
        except Exception as exc:
            print(f"{thread_name} generated an exception: {exc}")
        else:
            print(f"{thread_name} returned {data}")

def start():
    try:
        global server_socket
        server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        server_socket.bind((HOST, PORT))
        # server_socket.listen(10) # 允许的连接数
        server_socket.listen()

        print(f"Server is listening on {HOST}:{PORT}")

        while True:
            client_socket, client_address = server_socket.accept()

            print(f"Accepted connection client from {client_address}")

            # 为每个客户端创建一个新线程来处理请求
            client_thread = threading.Thread(target=handle_client, args=(client_socket,))
            client_thread.start()

    except KeyboardInterrupt:
        print("Server stopped by user interrupt.")
        # 关闭所有客户端套接字
        for sock in clients:
            sock.close()


if __name__ == '__main__':
    server_thread = threading.Thread(target=start)
    server_thread.start()
