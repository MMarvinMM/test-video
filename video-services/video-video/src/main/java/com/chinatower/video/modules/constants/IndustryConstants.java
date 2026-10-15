package com.chinatower.video.modules.constants;

/**
 * @Name IndustryConstants
 * @Description
 * @Author Chensw
 * @Date 2022/03/2022/3/23 11 : 13
 */
public class IndustryConstants {

    /**
     * 收藏
     */
    public static final Integer COLLECTION = 1;
    /**
     * 不收藏
     */
    public static final Integer NO_COLLECTION = 0;
    /**
     * 视频树正常展示
     */
    public static final Integer NORMAL = 1;
    /**
     * 视频树组织展示
     */
    public static final Integer ORGANIZE = 2;
    /**
     * 视频树标签展示
     */
    public static final Integer LABEL = 3;
    /**
     * 通道在线状态
     */
    public static final Integer CHANNEL_ONLINE = 0;
    /**
     * 通道离线状态
     */
    public static final Integer CHANNEL_OFFLINE = 1;
    /**
     * 通道2未配置设备状态
     */
    public static final Integer CHANNEL_ON_CONFIG = 2;
    
    /**
     * 是否正在巡航：1-是；0-否；；
     */
    public static final int CRUISE_START = 1;
    /**
     * 是否正在巡航：1-是；0-否；
     */
    public static final int CRUISE_STOP = 0;
    
    /**
     * 巡航启动状态：1-停用；2-启用；
     */
    public static final String PATROL_CRUISE_START = "2";
    /**
     * 巡航启动状态：1-停用；2-启用；
     */
    public static final String PATROL_CRUISE_STOP = "1";
    
    /**
     * 预约启动状态：1-停用；2-启用；
     */
    public static final String PATROL_INFO_START = "2";
    /**
     * 预约启动状态：1-停用；2-启用；
     */
    public static final String PATROL_INFO_STOP = "1";
    
    /**
     * 算法任务状态：0:停用 1:启用
     */
    public static final String ALGORITHM_TASK_START = "1";
    public static final String ALGORITHM_TASK_STOP = "0";
    
    /**
     * 1公有预案；
     */
    public static final String PLAN_PUBLIC_TYPE = "1";
    /**
     * 2私有预案
     */
    public static final String PLAN_PRIVATE_TYPE = "2";

    public static final String TWO_FLAG = "2";
    public static final String VIDEO_MEMORY_KEY = "_real_";

}
