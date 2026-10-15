package com.chinatower.bdm.api.constants;

import com.chinatower.platform.constants.Constants;

/**
 * 卫片执法数据-图层模板基础数据常量类
 * @date 2024/05/08 17:12
 * @version v1.6.60
 */
public class WpzfConstants {

    //耕地卫片执法
    public static final String[] GDWPZF_CSHTCMB = {Constants.NUMBER, "tbbh", Constants.COUNTRY, Constants.COUNTY, "town", Constants.VILLAGE,
            "wply", "qlx", "hlx", "qsx", "hsx", "tbmj", "tbzc", "lon", "lat"};
    //矿产卫片执法
    public static final String[] KCWPZF_CSHTCMB = {Constants.NUMBER, "tbbh", Constants.COUNTRY, Constants.COUNTY, "town", Constants.VILLAGE,
            "wply", "tbmj", "tbzc", "lon", "lat"};
    //非农化卫片执法
    public static final String[] FNHWPZF_CSHTCMB = {Constants.NUMBER, Constants.COUNTRY, Constants.COUNTY, "town", Constants.VILLAGE,
            "bhlx", "lon", "lat", "area", "dlbm", "tdlx", "yxsj", "qs"};
    //非粮化卫片执法
    public static final String[] FLHWPZF_CSHTCMB = {Constants.NUMBER, Constants.COUNTRY, Constants.COUNTY, "town", Constants.VILLAGE,
            "bhlx", "lon", "lat", "area", "dlbm", "tdlx", "yxsj", "qs"};
}
