# 操作步骤：
# 1. 打包前端项目，复制dis到data目录
# 2. 打包java项目
# 3. docker build -t iot-iita .
# 4. docker tag iot-iita iotkits/iot-iita
# 5. docker push iotkits/iot-iita

# 使用包含Java的基础镜像
FROM adoptopenjdk:11-jre-hotspot
# 设置工作目录
WORKDIR /app

# 复制后端Java应用的JAR文件
COPY ./iot-starter/target/iot-starter-0.5.2-SNAPSHOT.jar app.jar
COPY ./data/init/* data/init/

# 安装Nginx
RUN apt-get update && apt-get install -y nginx

# 复制Nginx配置文件
COPY ./data/nginx.conf /etc/nginx/nginx.conf

# 复制静态资源
COPY ./data/dist/ /usr/share/nginx/html

# 暴露端口
EXPOSE 8082 8086 1883 1884
# 插件预留端口
EXPOSE 8130-8140

# 设置容器启动命令
CMD ["/bin/bash", "-c", "java -jar /app/app.jar & nginx -g 'daemon off;'"]

