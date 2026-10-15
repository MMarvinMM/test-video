package com.chinatower.event.entity.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @Name EventAlarmNumberVO
 * @Description 统计告警数据返回实体
 * @Author Chensw
 * @Date 2022/6/13
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventAlarmNumberVO {

    Integer yesterdayAll;
    Integer todayAll;
    Integer todayComplete;
    Integer falseCompleteNum;
    Integer judgeNum;
    Integer dispatchNum;
    Integer grabNum;
    Integer handleNum;
    Integer approveNum;
    Integer orderCompleteNum;
    Integer repeatCompleteNum;
    Integer falseNum;
    Integer realNum;
    Integer allNum;
    Integer todayAINum;
    Integer todayManNum;
    Integer todayKeyNum;
    String increasePer;

//    Integer AINum;
    Integer manNum;
    Integer keyNum;

    String typeId;
    String typeName;
    Integer num;
    Integer aiNum;

    String label;

    String longitude;
    String latitude;
}
