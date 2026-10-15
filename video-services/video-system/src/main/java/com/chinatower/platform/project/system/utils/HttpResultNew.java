package com.chinatower.platform.project.system.utils;

import com.chinatower.platform.project.system.constants.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: ffzs
 * @Date: 2020/8/16 下午10:23
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HttpResultNew {

    private String returnCode;
    private String returnMsg;
    private Object data;
}
