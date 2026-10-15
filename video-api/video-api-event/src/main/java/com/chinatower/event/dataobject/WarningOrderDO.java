package com.chinatower.event.dataobject;

import com.baomidou.mybatisplus.annotation.TableId;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-20
 */
@Getter
@Setter
public class WarningOrderDO extends BaseWarningOrderDO {

    /**
     * 形成告警时间
     */
    private Date alarmTime;

}
