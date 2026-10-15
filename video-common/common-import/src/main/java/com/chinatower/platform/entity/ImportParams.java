package com.chinatower.platform.entity;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author WANGZIC
 */
@Data
public class ImportParams<T> {

    private String importId;

    private List<T> list;

    private Integer splitSize;

    private Integer startRowNum;

    private Integer endRowNum;

    private Integer batchNum;

    private String importType;

    private String fileName;

    private String fileUrl;

    private Map<String,Object> extraParams = new HashMap<>();

    public ImportParams() {
    }

    public ImportParams(List<T> list, String importType, String fileName, String fileUrl) {
        this.importType = importType;
        this.fileName = fileName;
        this.fileUrl = fileUrl;

        this.list = list;
        this.splitSize = 100;
        this.batchNum = 1;
        this.startRowNum = 2;
        this.endRowNum = this.list.size()+1;
    }

    public ImportParams(List<T> list, String importId, Map<String, Object> extraParams, int batchNum, int splitSize) {
        this.importId = importId;
        this.list = list;
        this.extraParams = extraParams;
        this.batchNum = batchNum+1;
        this.splitSize = splitSize;
        this.startRowNum = batchNum * splitSize +2;
        this.endRowNum = this.startRowNum + this.list.size()-1;
    }

    public ImportParams(List<T> list, String importId, Map<String, Object> extraParams, int batchNum, int splitSize, String importType) {
        this.importId = importId;
        this.list = list;
        this.extraParams = extraParams;
        this.batchNum = batchNum+1;
        this.splitSize = splitSize;
        this.startRowNum = batchNum * splitSize +2;
        this.endRowNum = this.startRowNum + this.list.size()-1;
        this.importType = importType;
    }

    public ImportParams<T> put(String key,Object value){
        this.extraParams.put(key,value);
        return this;
    }

}
