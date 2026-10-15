package com.chinatower.bdm.modules.unionTargetJs.utils;

import com.chinatower.bdm.modules.unionTargetJs.entity.ResultEntity;
import org.springframework.stereotype.Component;

/**
 * video模块工具类
 * Created by zhujun on 2022/1/14.
 */
@Component
public class UnionTargetJsCommonUtil {

    private static final String SUCCESS_CODE = "0";
    private static final String ERROR_CODE = "1";


    /**
     * 判断接口返回是否为成功状态
     *
     * @param cr
     * @return
     */
    public static boolean crIsSuccess(ResultEntity cr) {
        return cr != null && SUCCESS_CODE.equals(cr.getCode());
    }


}
