FROM swr.cn-north-4.myhuaweicloud.com/sec-baseline/openjdk:8u342-slim-buster
#FROM chinatower/python3.8-jdk8:latest
#FROM test1:20220630

ENV TZ=PRC

COPY video-out-data.jar /app/video-platform/video-service/

RUN localedef -c -f UTF-8 -i zh_CN zh_CN.utf8
# 设置环境变量
ENV LC_ALL zh_CN.utf8

WORKDIR /app
EXPOSE 8080
CMD ["/bin/bash", "-c",\
"java -XX:+UnlockExperimentalVMOptions \
 -XX:+UseCGroupMemoryLimitForHeap \
 -XX:+HeapDumpOnOutOfMemoryError  \
 -jar /app/video-platform/video-service/${APP}.jar"]



