package com.chinatower.platform.project.system.domain.vo;

import java.util.HashMap;

/**
 * 路由显示信息
 *
 * @author iwhalecloud
 */
public class MetaVo extends HashMap<String, Object> {

    /** 设置该路由在侧边栏和面包屑中展示的名字 */
    public static final String TITLE = "title";

    /** 设置该路由的图标，对应路径src/icons/svg */
    public static final String ICON = "icon";

    /** 如果设置为true，则不会被 <keep-alive> 缓存(默认 false) */
    public static final String NO_CACHE = "noCache";

    public MetaVo() {}

    public MetaVo(String title, String icon) {
        super.put(TITLE, title);
        super.put(ICON, icon);
        super.put(NO_CACHE, false);
    }

}
