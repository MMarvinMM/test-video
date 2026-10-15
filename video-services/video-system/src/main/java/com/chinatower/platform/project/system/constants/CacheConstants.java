package com.chinatower.platform.project.system.constants;

import com.chinatower.platform.common.utils.IdUtils;
import com.chinatower.platform.utils.AESUtil;

public class CacheConstants {

    public static final String CODE_STATUS = "code:status:";

    public static final String SMS_FAIL_NUM_KEY = "sms_login_fail_num:";

    /**
     * 验证码错误次数
     */
    public static final Integer CAPTCHA_FAIL_MAX = 5;
    /**
     * 省市区区域树 redis key
     */
    public static final String AREA_TREE_PROV_CITY_COUNTY = "area_tree_prov_city_county:";

    public static final String CACHE_AREA_ = "CACHE_AREA3_%d";

    /**
     * 验证码校验key
     * @param phone
     * @return
     */
    public static String getCodeKey(String phone){
        String key = CacheConstants.CODE_STATUS+ AESUtil.encrypt(phone) +":"+ IdUtils.fastSimpleUUID();
        return key;
    }
}
