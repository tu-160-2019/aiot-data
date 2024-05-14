# aiot-data

## 注意
由于在开发阶段，频繁提交，可能会有提交遗漏代码或是数据库脚本未及时同步的情况。如遇到请添加微信入群交流或提交Issues。

## 介绍
AIOT人工智能物联网平台是一站式物联网开发基础平台，帮助企业快速实现数字化、精细化数据管理。核心系统为：物联网平台 + 数据中台（数据底座）+ AI。

同时支持文生图、语音合成等。大模型支持陆续也会慢慢开发。

欢迎大家添加最下方的微信入群交流。

## 物联系统介绍
  系统包含了品类、物模型、消息转换、插件管理（mqtt插件、EMQX插件、http插件、tcp插件、modbus插件等）、设备管理、设备分组、规则引擎、数据流转（http/mqtt/kafka）、数据可视化、告警中心等模块。

## 数据中台介绍
  系统包含了数据集成，数据开发，数据治理，数据资产，数据服务，数据集市六大模块，解决数据孤岛问题，实现数据统一口径标准，自定义数据开发任务。

## AI平台介绍
  系统包含了缺陷检测、烟火检测、安全帽检测、车流检测、车辆检测、入侵检测、车牌检测、花检测、动物检测、口罩检测、人流检测等等AI模型。并且支持训练、标注等功能。

  增加支持文生图、语音合成等。大模型支持陆续也会慢慢开发。
  

## 大屏设计
  系统支持自定义报表、大屏设计功能，全拖拽式实现，满足不同自定义演示需求。

大屏设计器使用的dataroom，由于dataroom为vue2开发，本系统基于vue3，无法直接集成到系统内部。要使用大屏设计器，需要部署部署dataroom。

## 软件架构说明
  本系统采用springboot、spring cloud、vertx、redis、H2、mysql、elasticsearch、TDEngine、RocketMq、flink、hadoop、kafka、Vue3、Mybatis-Plus、minio、k8s等框架和第三方软件，中间件采用模块化无侵入集成非常容易扩展和替换。


## 系统准备说明
1、数据库脚本在db目录下

2、部署需要放到nacos的相关配置文件在 deploy 目录下

3、对于一些代码拉不下来的情况，在对应的工程上有相应的jar包，手动安装即可。

如：DmJdbcDriver18.jar 不存在

在 srt-cloud-framework\srt-cloud-dbswitch\lib 目录下可以找到

执行 mvn install:install-file -DgroupId=com.dameng -DartifactId=dm-jdbc -Dversion=1.0.0 -Dpackaging=jar -Dfile=DmJdbcDriver18.jar 安装

4、系统默认账号密码：admin/Aa@123456


## 系统启动说明
1、导入idea

2、启动 GatewayApplication

3、启动 SystemApplication

4、启动 DataIntegrateApplication

5、启动 IotApplication


## 系统部分截图
![alt text](https://gitee.com/wangmingf83/aiot-data/raw/master/images/iot1.jpg)

![alt text](https://gitee.com/wangmingf83/aiot-data/raw/master/images/iot2.jpg)

![alt text](https://gitee.com/wangmingf83/aiot-data/raw/master/images/data1.jpg)

![alt text](https://gitee.com/wangmingf83/aiot-data/raw/master/images/data2.jpg)

![alt text](https://gitee.com/wangmingf83/aiot-data/raw/master/images/AI1.jpg)

![alt text](https://gitee.com/wangmingf83/aiot-data/raw/master/images/ai93227.jpg)

![alt text](https://gitee.com/wangmingf83/aiot-data/raw/master/images/screen1.jpg)

![alt text](https://gitee.com/wangmingf83/aiot-data/raw/master/images/screen2.jpg)


## 微信群(添加我微信备注"进群"):
![alt text](https://gitee.com/wangmingf83/aiot-data/raw/master/20240325121359.png)


## 模型下载地址:
链接: https://pan.baidu.com/s/1Pj0VD79lHuTQKNe5_El9_g 提取码: r89y


## FAQ:
1、目前摄像头部分为调用本地摄像头，仅供参考，后期会修改为调用rtmp、rtsp实时视频。目前前端暂未处理。后台可供参考

2、配置文件位置

       数据库脚本在 db 目录

       数据源配置在 deploy 目录


## 鸣谢声明

该项目使用借鉴了以下优秀开源项目的源码进行改造集成，特此鸣谢声明。

**数据平台**

[数睿通数据中台](https://gitee.com/zrxjava/srt-data-center)

**物联网**

[铱塔智联](https://gitee.com/open-iita/iotkit-parent)

