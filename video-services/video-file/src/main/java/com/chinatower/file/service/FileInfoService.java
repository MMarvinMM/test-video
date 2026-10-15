package com.chinatower.file.service;

public interface FileInfoService {

    /**
     * 获取序列id
     * @param name 序列名
     * @param number 数量
     * @return 序列id
     */
    String getSeqByName(String name,String number);

}
