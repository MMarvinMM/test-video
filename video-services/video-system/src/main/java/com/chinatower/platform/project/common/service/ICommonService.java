package com.chinatower.platform.project.common.service;

import java.util.List;

/**
 * @author WANGZIC
 */
public interface ICommonService {

    /**
     * 获取序列id
     * @param name 序列名
     * @param prefix 追加前缀
     * @param suffix 追加后缀
     * @return 序列id
     */
    String getSeqByName(String name,String prefix,String suffix);

    /**
     * 获取序列id
     * @param name 序列名
     * @param number 数量
     * @return 序列id
     */
    String getSeqByName(String name,String number);


    /**
     * 获取序列id
     * @param name 序列名
     * @return 序列id
     */
    String getSeqByName(String name);


    int isExistTable(String tableName);

    /**
     * 新增收藏
     * @param collObjCode,collObjType
     */
    void addCollections(List<String> collObjCode, String collObjType);

    /**
     * 取消收藏
     * @param collObjCode,collObjType
     */
    void cancelCollections(List<String> collObjCode, String collObjType);

}
