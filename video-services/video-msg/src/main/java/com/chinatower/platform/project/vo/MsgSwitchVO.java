package com.chinatower.platform.project.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MsgSwitchVO implements Serializable {

    /**
     * 超期提醒 1-开启  0-关闭
     */
    private Integer alarmOverdueSwitch;

    /**
     * 公告 1-开启  0-关闭
     */
    private Integer  noticeSwitch;

    /**
     * 其他 1-开启  0-关闭
     */
    private Integer  otherSwitch;

    private Integer alarmSwitch;
}
