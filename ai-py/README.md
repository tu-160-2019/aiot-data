# aiot-data-py



## 基础环境

建议用python3.8的环境

conda create --name aiotpy python=3.8

注意：默认安装CPU版本torch：
  此句不要执行 pip install torch==1.7.0+cpu torchvision==0.8.0 torchaudio==0.7.0 -f https://download.pytorch.org/whl/torch_stable.html

pip install torch==2.0.1+cu118 torchaudio==2.0.2+cu118 torchvision==0.15.2+cu118 -f https://download.pytorch.org/whl/torch_stable.html

pip install -r requirements.txt -i https://mirrors.aliyun.com/pypi/simple/

## 本地开发模式安装
   pip install -v -e .

## 运行命令
   python3.x -m flask --app start run

   或
   
   python3.x -m flask --app start run -h 0.0.0.0 -p 8080

## grpc 根据 proto生成需要的文件

  python -m grpc_tools.protoc -I. --python_out=. --grpc_python_out=. helloworld.proto --include_imports

  protoc helloworld.proto --python_out=. --descriptor_set_out=helloworld.desc --include_imports

  这条命令会生成文件 xxx_pb2.py 和 xxx_pb2_grpc.py



