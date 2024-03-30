#!/bin/bash

# linux系统 清除数据脚本

# bin目录绝对路径
BIN_DIR=`pwd`

# 返回到上一级项目根目录路径
cd ..
# 打印项目根目录绝对路径

# `pwd` 执行系统命令并获得结果
DEPLOY_DIR=`pwd`

# 清除.init文件,清除es数据库,h2数据库
# 清除后,项目启动时数控库会重新初始化

rm -rf .init $DEPLOY_DIR/data/elasticsearch $DEPLOY_DIR/data/iotkit.mv.db


# TODO:根据数据库类型,清除数据库
# mysql