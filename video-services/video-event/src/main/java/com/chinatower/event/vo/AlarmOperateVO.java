package com.chinatower.event.vo;

import com.chinatower.event.entity.vo.OrderInfoVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * <p>
 * 类名称: AlarmOperateVO.java
 *
 * @date 2022/3/12 16:42
 */
@Data
public class AlarmOperateVO extends OrderInfoVO implements Serializable {

    private String orderId;
    private String imgUrl;
    private String judgeResultName;
    private String judgeUser;
    private String judgeUserName;
    private String judgeTime;
    private String source;
    private String operateType;
    private String icon;
    private List<String> imgs;
    private String judgeUserAccount;
    /**
     * 用户距离事发地距离
     */
    private String userDistance;
    private String userLongitude;
    private String userLatitude;
    private String resultTab;
}
