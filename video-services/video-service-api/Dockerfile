FROM swr.cn-north-4.myhuaweicloud.com/sec-baseline/openjdk:8u342-slim-buster
ENV TZ=PRC

COPY video-service-api.jar /app/video-platform/video-service/

WORKDIR /app
EXPOSE 8080
CMD ["/bin/bash", "-c",\
"java -XX:+UnlockExperimentalVMOptions \
 -XX:+UseCGroupMemoryLimitForHeap \
 -jar /app/video-platform/video-service/${APP}.jar"]
