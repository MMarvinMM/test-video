package com.chinatower.platform.project.system.domain;

import lombok.Data;

import java.util.List;

@Data
public class AreaInfoResultVO {

    private List<AreaDetailInfo> firstLetterList;

    private List<AreaDetailInfo> areaInfoList;


    public AreaInfoResultVO(List<AreaDetailInfo> groupedData, List<AreaDetailInfo> flatList) {
        this.firstLetterList = groupedData;
        this.areaInfoList = flatList;
    }

}
