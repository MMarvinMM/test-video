package com.chinatower.video.api.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
public class VideoPageInfo<T> {
    private int total;
    private List<T> list;

    public VideoPageInfo(List<T> resultList,int pageNum,int pageSize){
        this.total = resultList.size();
        List<T> newList = new ArrayList<>();
        if (this.total != 0){
            int startRow = (pageNum - 1)*pageSize;
            int endRow = pageNum*pageSize - 1;
            log.info("s"+startRow+"e"+endRow+"this.total"+this.total);
            if (endRow >= this.total){
                endRow = resultList.size() - 1;
            }
            for (int i = startRow;i<=endRow;i++){
                newList.add(resultList.get(i));
            }
            this.list = newList;
        }
    }
}
