#FROM ffmpeg:export4
FROM swr.cn-north-4.myhuaweicloud.com/sec-baseline/rockylinux:9-cuda-12.2.2-ffmpeg7.0-multiarch
ENV TZ=PRC

#ENV LANG="zh_CN.UTF-8"
#RUN echo "export LC_ALL=zh_CN.UTF-8"  >>  /etc/profile &&  echo "export LC_ALL=zh_CN.UTF-8" >>/root/.bashrc
#RUN localedef -c -f UTF-8 -i zh_CN zh_CN.utf8

COPY video-export.jar /app/video-platform/video-service-export/

WORKDIR /app
EXPOSE 8080
CMD ["/bin/bash", "-c",\
"java -XX:+UnlockExperimentalVMOptions \
 -XX:+UseCGroupMemoryLimitForHeap \
 -XX:+HeapDumpOnOutOfMemoryError  \
 -jar /app/video-platform/video-service-export/${APP}.jar"]
