import os
import re
import platform

from setuptools import find_packages, setup


def get_version():
    """Get package version from app_info.py file"""
    filename = "app_info.py"
    with open(filename, encoding="utf-8") as f:
        match = re.search(
            r"""^__version__ = ['"]([^'"]*)['"]""", f.read(), re.M
        )
    if not match:
        raise RuntimeError(f"{filename} doesn't contain __version__")
    version = match.groups()[0]
    return version


def get_preferred_device():
    """Get preferred device from app_info.py file: CPU or GPU"""
    filename = "app_info.py"
    with open(filename, encoding="utf-8") as f:
        match = re.search(
            r"""^__preferred_device__ = ['"]([^'"]*)['"]""", f.read(), re.M
        )
    if not match:
        raise RuntimeError(f"{filename} doesn't contain __preferred_device__")
    device = match.groups()[0]
    return device


def get_package_name():
    """Get package name based on context"""
    package_name = "aiot-data-py"
    preferred_device = get_preferred_device()
    if preferred_device == "GPU" and platform.system() != "Darwin":
        package_name = "aiot-data-py-gpu"
    return package_name


def get_install_requires():
    """Get python requirements based on context"""
    install_requires = [
        "numpy",
        "grpcio",
        "grpcio-tools",
        "protobuf",
        "flask_cors",
        "Flask",
        "websocket-client",
        "Flask-SQLAlchemy",
        "Flask-moment",
        "MySQL-connector-python",
        "Flask-Excel",
        "pyexcel-xlsx",
        "Werkzeug",
        "setproctitle",
        "selenium",
        # 深度学习
        "opencv-python",
        # 大模型
        "tokenizers",
    ]

    # Add onnxruntime-gpu if GPU is preferred
    # otherwise, add onnxruntime.
    # Note: onnxruntime-gpu is not available on macOS
    preferred_device = get_preferred_device()
    if preferred_device == "GPU" and platform.system() != "Darwin":
        install_requires.append("onnxruntime-gpu==1.16.0")
        print("Building aiot-data-py with GPU support")
    else:
        install_requires.append("onnxruntime==1.16.0")
        print("Building aiot-data-py without GPU support")

    return install_requires


def get_long_description():
    """Read long description from README"""
    with open("README.md", encoding="utf-8") as f:
        long_description = f.read()
    return long_description


setup(
    name=get_package_name(),
    version=get_version(),
    packages=find_packages(),
    description="aiot-data 实现Java与Python结合的应用",
    long_description=get_long_description(),
    long_description_content_type="text/markdown",
    author="CVHub",
    author_email="382392596@qq.com",
    url="https://gitee.com/wangmingf83/aiot-data",
    install_requires=get_install_requires(),
    license="Apache 2.0",
    keywords="Machine Learning, Deep Learning",
    classifiers=[
        "Intended Audience :: Developers",
        "Intended Audience :: Science/Research",
        "Natural Language :: English",
        "Operating System :: OS Independent",
        "Programming Language :: Python",
        "Programming Language :: Python :: 3.8",
        "Programming Language :: Python :: 3.9",
        "Programming Language :: Python :: 3.10",
        "Programming Language :: Python :: 3 :: Only",
    ],
    include_package_data=True,
    entry_points={
        "console_scripts": [
            "aiotdata=aiot-data-py:start",
        ],
    },
)
