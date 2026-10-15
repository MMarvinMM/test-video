package com.chinatower.video.modules.query.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author yy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryPresentPointVo implements Serializable {

    private String deviceCode;

    private String channelCode;

    private String configId;
}
