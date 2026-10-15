package com.chinatower.gis.api.entity;

public final class Constants {
    public static final  String NOT_EMPTY_KEY = "调用地图key不可为空";
    public static final  String NOT_EMPTY_MAP_TYPE = "调用地图类型不可为空";
    public static final  String BAIDU = "百度地图";
    public static final  String GAODE = "高德地图";
    public static final  String TIAN = "天地图";
    public static final  String KEYWORD = "keyWordQuery";
    public static final  String BDKEYWORD = "bdKeyWordQuery";
    public static final  String GDKEYWORD = "gdKeyWordQuery";
    public static final  String TKEYWORD = "tianKeyWordQuery";

    public static final  String KEY_WORD_OP = "keyWordQuery";
    public static final  String KEY_WORD_ERROR = "关键字搜索行政区域方法失败";
    public static final String REQUEST_LOG = "请求报文:";
    public static final String RESPONSE_LOG = "返回报文:";
    public static final String SUCCESS_MSG = "成功";
    public static final String SELECT_LOG = "查询结果:";

    public static final String PARAMS_ERROR = "请求参数有误!!";
    public static final String GD_MAP_URL = "gd.map.url";
    public static final String GD_MAP_KEY = "gd.map.key";
    public static final String BD_MAP_URL = "bd.map.url";
    public static final String BD_MAP_KEY = "bd.map.key";
    public static final String TD_MAP_URL = "td.map.url";
    public static final String TD_MAP_KEY = "td.map.key";
    public static final String WEATHER_ERROR = "调用获取天气方法失败";
    public static final String WEATHER_QUERY = "查询目标区域当前/未来的天气情况 : weatherQuery ";
    public static final String COORDINATE_TRANSFORMATION_ERROR = "调用84坐标转换为当前地图可用坐标方法失败";
    public static final String KEY_WORD_QUERY_ERROR = "关键词搜索方法失败";
    public static final String KEY_WORD_QUERY_EMPTY = "POI关键词搜索结果为空";
    public static final String CODE_TO_ADDRESS_ERROR = "经纬度转地址方法失败";
    public static final String TAOHE_ANALYSIS_LAYERTABLE_ERROR = "套合明细的结果列表方法失败";
    public static final String TAOHE_ANALYSIS_SET_OPTIONS_ERROR = "套合明细的设置参数结果列表方法失败";
    public static final String TAOHE_ANALYSIS_SET_OPTIONS_SUCCESS = "设置成功！";
    public static final String TAOHE_ANALYSIS_DETAILS_PIECHART_ERROR = "套合明细的饼图方法失败";
    public static final String TAOHE_ANALYSIS_ALL_ERROR = "套合分析方法失败";
    public static final String GIS_CALCULATE_AREA_ERROR = "计算多边形的面积方法失败";
    public static final String DELETE_LAYER_ERROR = "PG库中删除图层方法失败";;
    public static final String ADD_LAYER_ERROR = "PG库中新增图层方法失败";
    public static final String GET_LAYER_AREA_ERROR = "获得图层面积方法失败";
    public static final String CREATE_LAYERINFO_ERROR = "新增模板表方法失败";
    public static final String STATISTIC_GDXZMJ_ERROR = "耕地现状统计方法失败";
    public static final String GET_XLSJ_FEATURES_ERROR = "获取下拉数据要素列表方法失败";
    public static final String STATISTIC_DKMJ_ERROR = "图斑面积统计方法失败";
    public static final String GET_XJXL_FEATURES_BY_IDS_ERROR = "根据ids查询巡检路线的 Features 的方法失败";
    public static final String STATISTIC_EACH_TYPE_ERROR = "根据图层ID，统计指定类型的面积，图斑数，占比等信息的方法失败";
    public static final String GET_FEATURES_BY_FILTER_API_ERROR = "根据过滤条件查询 Features 的 API 方法失败";
    public static final String GET_FEATURES_BY_FILTER_ERROR = "根据过滤条件查询 Features 的方法失败";
    public static final String GET_FEATURES_BY_WKT_ERROR = "根据空间范围过滤条件查询 Features 的方法失败";
    public static final String SEARCH_XZTB_ERROR = "土地利用现状查询方法失败";
    public static final String SEARCH_JBNT_ERROR = "基本农田查询方法失败";
    public static final String SEARCH_STBHHX_ERROR = "生态保护红线查询方法失败";
    public static final String SEARCH_JSYDGZQ_ERROR = "建设用地管制区查询方法失败";
    public static final String SEARCH_TDBP_ERROR = "土地报批查询方法失败";
    public static final String SEARCH_OTHER_ERROR = "其他类型查询方法失败";
    public static final String SEARCH_TDBPSTATE_ERROR = "查询土地报批状态方法失败";
    public static final String CALCULATE_INOUTBALANCE_ERROR = "计算耕地的进出平衡失败";
    public static final String SEARCH_INOUTBALANCE_ERROR = "获取耕地的进出平衡的计算结果失败";
    public static final String CALCULATE_AUTOCOMPARELAYERFEATURES_ERROR = "自动比较图层图斑的不同的处理失败";
    public static final String CALCULATE_LAYERHASCUSTOMBSMFIELD_ERROR = "查询表里是否有bsm字段过程失败";
    public static final String CALCULATE_INTERSECTINGPLOT_ERROR = "查询相交图斑的过程失败";
    public static final String CALCULATE_INTERSECTINGLAYERS_ERROR = "查询有相交图斑关系的图层的过程失败";
    public static final String ADDRESS_TO_CODE_ERROR = "地址转经纬度方法失败";
    public static final String AREA_ERROR = "根据坐标查行政区域方法失败";
    public static final String ACOUNTRY_AREA_QUERY_ERROR = "将坐标信息转化行政区域信息方法失败";
    public static final String COORDINATE_CONVERSION = "84坐标转换方法 : coordinateTransformation ";
    public static final String COORDINATE_CONVERSION_ERROR = "84坐标转换方法失败";
    public static final String CODE_TO_ADDRESS = "经纬度转地址方法 : codeToAdress ";
    public static final String ADDRESS_TO_CODE= "地址转经纬度方法  : adressToCode";
    public static final String KEY_WORD_QUERY= "关键词搜索方法  : keyWordQuery";
    public static final String COUNTRY_AREA_QUERY= "坐标信息转化行政区域信息方法  : countryAreaQuery";
    public static final String REQ_KEY = "key";
    public static final String REQ_AK = "ak";
    public static final String REQ_TK = "tk";
    public static final String REQ_CITY = "city";
    public static final String SYS_VALUE = "value";
    public static final String WEATHER_LIVES = "lives";
    public static final String WEATHER_STATUS = "status";
    public static final String REGEO_CODE = "regeocode";
    public static final String ADDRESS_COMPONENT = "addressComponent";
    public static final String RES_FORMATTED_ADDRESS = "formattedAddress";
    public static final String FORMATTED_ADDRESS = "formatted_address";
    public static final String PROVINCE = "province";
    public static final String PROVINCE_CODE = "provinceCode";
    public static final String CITY = "city";
    public static final String DISTRICT = "district";
    public static final String DISTRICT_CODE = "districtCode";
    public static final String TOWNSHIP = "township";
    public static final String STREET_NUMBER = "streetNumber";
    public static final String CITY_CODE = "citycode";
    public static final String TOWN_CODE = "towncode";
    public static final String AD_CODE = "adcode";

    public static final String KEYWORDS = "keywords";
    public static final String SUBDISTRICT = "subdistrict";
    public static final String EXTENSIONS = "extensions";
    public static final String DISTRICTS = "districts";
    public static final String DISTRICT_ERROR = "行政区域查询方法失败";

    public static final String TARGETFOLLOW = "targetFollow";
    public static final String ERROR_PRE = "错误详情:";
    public static final String REDIS_URL_ERROR ="接口调用地址异常";
    public static final String RESPONSE_INTERFACE_LOG = "接口返回报文:";
    public static final String TARGETFOLLOW_OP = "光电联动信令下发";
    public static final String ALLOCATION = "访问配置权限下发";

    public static final String GLOBAL_MAP_TYPE = "global_map_type";
    // 地理编码/逆地理配置 前面开关，是否启用单独配置，后面是配置的使用什么方式
    public static final String gis_geocoding_config = "gis_geocoding_config";


    public static final  String NOT_EMPTY_PARAM = "参数不能为空";
    public static final  String REQUEST_GLOBAL_MAP_TYPE_ERROR = "请求全局地图类型错误";


}
