package com.chinatower.event.entity.dto;

import com.chinatower.event.api.model.ApiInterceptOrderEntity;
import com.chinatower.event.api.model.ApiWarningOrderEntity;
import com.chinatower.event.api.model.LocalImgReq;
import com.chinatower.event.entity.vo.WarningOrderListVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AlarmLocalDownloadReq implements Serializable {

    /**
     * 告警工单集合
     * @required true
     */
    private List<String> warningOrderIds;

    /**
     * 租户名称
     * @required false
     */
    private String name;

    /**
     * 查询下载类型（0:非搜索框 1：事件查询搜索框下载 2：回收站查询）
     * @required true
     */
    private String downLoadType;

    /**
     * 查询下载类型（1：事件查询搜索框下载 2：回收站查询 ）
     * @required true
     */
    private String orderIdDownLoadType;

    /**
     * 查询工单实体
     * @required true
     */
    private ApiWarningOrderEntity downLoadOrderDTO;

    /**
     * 查询回收站请求实体
     * @required true
     */
    private ApiInterceptOrderEntity downLoadWarningInterceptDTO;

    private List<WarningOrderListVO> searchOrderList;

    private List<LocalImgReq> imgReq;
}
