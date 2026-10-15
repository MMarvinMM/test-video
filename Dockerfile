FROM swr.cn-north-4.myhuaweicloud.com/sec-baseline/openjdk:8u342-slim-buster
ENV TZ=PRC

COPY video-analysis.jar /app/video-platform/video-service/
COPY video-auth.jar /app/video-platform/video-service/
COPY video-bdm.jar /app/video-platform/video-service/
COPY video-device.jar /app/video-platform/video-service/
COPY video-device-sync.jar /app/video-platform/video-service/
COPY video-event.jar /app/video-platform/video-service/
COPY video-file.jar /app/video-platform/video-service/
COPY video-gateway.jar /app/video-platform/video-service/
COPY video-gis.jar /app/video-platform/video-service/
COPY video-manage.jar /app/video-platform/video-service/
COPY video-message.jar /app/video-platform/video-service/
COPY video-msg.jar /app/video-platform/video-service/
COPY video-order.jar /app/video-platform/video-service/
COPY video-video.jar /app/video-platform/video-service/
COPY video-task.jar /app/video-platform/video-service/
COPY video-task-data.jar /app/video-platform/video-service/
# COPY video-out-data.jar /app/video-platform/video-service/  out-data服务里需要调用python，所以需要单独打包
COPY video-system.jar /app/video-platform/video-service/
COPY video-doc.jar /app/video-platform/video-service/
COPY video-export.jar /app/video-platform/video-service/
COPY tower-log.jar /app/video-platform/video-service/
COPY video-service-api.jar /app/video-platform/video-service/
COPY video-cascade-config.jar /app/video-platform/video-service/
COPY video-stdu.jar /app/video-platform/video-service/
COPY video-receive.jar /app/video-platform/video-service/
COPY video-emergency.jar /app/video-platform/video-service/

WORKDIR /app
EXPOSE 8080
CMD ["/bin/bash", "-c",\
"java -XX:+UnlockExperimentalVMOptions \
 -XX:+UseCGroupMemoryLimitForHeap \
 -XX:+HeapDumpOnOutOfMemoryError  \
 -jar /app/video-platform/video-service/${APP}.jar"]
