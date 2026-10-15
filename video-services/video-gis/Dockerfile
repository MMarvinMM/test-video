FROM swr.cn-north-4.myhuaweicloud.com/sec-baseline/openjdk:8u342-slim-buster
ENV TZ=PRC

COPY video-gis.jar /app/video-platform/video-service/

WORKDIR /app
EXPOSE 8080
CMD ["/bin/bash", "-c",\
"java -XX:+UnlockExperimentalVMOptions \
 -XX:+UseCGroupMemoryLimitForHeap \
 -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintTenuringDistribution -Xloggc:log/gc.log \
 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/app/heapdump.hprof \
 -jar /app/video-platform/video-service/${APP}.jar"]