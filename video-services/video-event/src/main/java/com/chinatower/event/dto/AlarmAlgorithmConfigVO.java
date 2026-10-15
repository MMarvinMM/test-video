package com.chinatower.event.dto;

import com.chinatower.platform.entity.device.AlgorithmConfigVO;
import lombok.Data;

import java.io.Serializable;

/**
 *
 *  扩展原有算法字段
 *
 * @author lisy
 */
@Data
public class AlarmAlgorithmConfigVO extends AlgorithmConfigVO implements Serializable {

    private static final long serialVersionUID = -659703402030310570L;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 行业应用版本编码
     */
    private String appVerCode;
}
