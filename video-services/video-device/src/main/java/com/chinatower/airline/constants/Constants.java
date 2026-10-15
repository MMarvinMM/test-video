package com.chinatower.airline.constants;

/**
 * 常量类
 *
 * @author lsy
 */
public class Constants {
    //航线编码
    public static final String FLIGHT_ROUTE_ID = "flightRouteId";
    //设备编码
    public static final String DEVICE_CODE = "deviceCode";
    public static final String CHANNEL_CODE = "channelCode";
    public static final String CHANNEL_TYPE = "channelType";
    //基站概括状态  -1：离线  0：在线  1：飞行中  2：异常  3：运行中
    public static final String NEST_STATUS = "nestStatus";
    //基站调试状态  0：正常  1：异常
    public static final String NEST_DEBUG = "nestDebug";
    public static final String LOCK = "lock";
    //断点续飞缓存 key
    public static final String UAV_TASK_CONTINUE_KEY = "uavTaskContinueKey_";
    public static final String UAV_MAP_KEY = "uavMapKey_add_";
    //飞机执行断点任务异常
    public static final String UAV_TASK_ERROR_JSON = "飞机当前不可执行本次断点任务";
    public static final String UAV_FLY_CONFIG_KEY = "pointToFly";
    public static final String UAV_TASK_ERROR_JSON2 = "指点飞行功能已关闭";
    public static final String VIRTUAL_PHOTO_URL = "virtualPhotoUrl";
    public static final String ORIENTED_SHOOT = "orientedShoot";
    public static final String WEATHER_WIND = "windspeed";
    public static final String PHOTO = "photo";
    public static final String VIDEO = "video";
    public static final String IS_COLLECT = "isCollect";
    public static final String SHOW_TIME = "showTime";
    public static final String DATE_YMDHMS = "yyyy-MM-dd HH:mm:ss";
    public static final String COORDINATES = "coordinates";
    public static final String POINT = "Point";
    public static final String KML = "kml";
    public static final String PLACEMARK = "Placemark";
    public static final String DOCUMENT = "Document";
    public static final String WAYLINE_WPML = "wayline.wpml";
    public static final String FOLDER = "Folder";
    public static final String UOSINFO = "uosInfo";
    public static final String TIME_STAMP = "timestamp";
    public static final String YYYYMMDDHHMM = "yyyyMMddHHmm";
    /**
     * Redis 缓存 key 前缀
     */
    public static final String REDIS_KEY_PREFIX = "uav:task:mileage:";

    /**
     * 缓存过期时间：1周
     */
    public static final long CACHE_EXPIRE_DAYS = 7;

    public static final String HORN_VOLUMN = "volumn";

    public static final String HORN_SINGLE = "single";

    public static final String HORN_CYCLE = "cycle";

    public static final String BRIGHTNESS = "brightness";

    ;

    public static final String ZONE = "0";


    public static final String ONE = "1";

    public static final String TWO = "2";

    public static final String FOUR = "4";

    public static final String FIVE = "5";

    public static final String SET_SEARCHLIGHT_STATUS_KEY = "setSearchlightStatusKey";

    public static final String UAV_FLIGHT_TASKS = "uav_flight_tasks";


}
