package com.chinatower.bdm.common;

public final class Constants {
    //机器人获取ticket凭证
    public static final String GRANT_TYPE = "client_credentials";
    //机器人获取ticket入参凭证
    public static final String COMP_CODE = "comp-ai";
    //机器人获取ticket入参凭证
    public static final String ACCESS_TYPE = "APP";
    //机器人获取ticket入参凭证
    public static final String GRANT_TYPE_PARAM = "?grant_type=";
    //机器人获取ticket入参凭证
    public static final String COMP_CODE_PARAM = "&comp_code=";
    //机器人获取ticket入参凭证
    public static final String AUTHORIZATION_PARAM = "&authorization=";
    //机器人获取ticket入参凭证
    public static final String ACCESS_TYPE_PARAM = "&access_type=";
    //机器人获取tokenURL
    public static final String TOKEN_URL="/auth/oauth/token";
    //机器人获取ticketURL
    public static final String TICKET_URL="/sso/grant";
    //流式机器人普通模式url
    public static final String ORDINARY_URL = "/ai/chat/generate";
    //流式机器人对话模式url
    public static final String DIALOGUE_URL = "/webAi/dialogue";

    public static final String CHAT_COMPLETIONS = "/ai/v1/chat/completions";
    //人工机器人token缓存key
    public static final String TOKEN_CACHE_KEY = "video:bdm:token:cache:key";
    public static final String ERROR_MSG = "不好意思，您的问题我还没有理解，或者换一种方式和我再讲一下呢？";

    public static final String DEFAULT_MSG_ONE = "什么是全域感知";

    public static final String DEFAULT_MSG_TWO = "如何进行算法配置";

    public static final String DEFAULT_MSG_THREE = "告警太多了怎么办";

    public static final String DEFAULT_MSG_FOUR = "如何导出统计分析报告";

    public static final String SUCCESS = "接口执行成功。";
    public static final String GOOD = "操作成功";
    public static final String FAIL = "接口执行失败。";
    public static final String ADD_GRID_KEEPER_ERRO = "新增网格员异常";
    public static final String ADD_GRID_INFO_ERRO = "新增网格";
    public static final String ADD_FIRST_GRID_INFO_ERRO = "新增最高级网格";
    public static final String UPDATE_GRID_INFO_ERRO = "修改网格";
    public static final String DELETE_GRID_INFO_ERRO = "删除网格";
    public static final String EMPTY_STR = "";
    public static final String EMPTY_SPACE = " ";
    public static final String COMMA = ",";
    public static final String COMMA_CH = "，";
    public static final String ZERO = "0";
    public static final String ONE = "1";
    public static final String TWO = "2";
    public static final String THREE = "3";
    public static final String FOUR = "4";
    public static final String FIVE = "5";
    public static final String SIX = "6";
    public static final String SEVEN = "7";
    public static final String EIGHT = "8";

    public static final String NINE = "9";
    public static final String LGT = ">";
    public static final Integer MINUS_ONE = -1;
    public static final String GRID_NOT_NULL = "网格id不能为空。";
    public static final String GRID_NAME_NOT_NULL = "责任网格不能为空。";
    public static final String ENTRY_TIME_NOT_NULL = "适用日期不能为空。";
    public static final String GRID_NAME_NOT_EXIST = "责任网格不存在。";
    public static final String LOGIN_NAME_NOT_EXIST = "登录名不存在。";
    public static final String LOCATION_FLAG_NOT_EXIST = "是否开启定位不能为空。";
    public static final String KEEPER_NAME_NOT_EXIST = "姓名不能为空。";
    public static final String PHONE_NUMBER_NOT_EXIST = "手机号码不能为空。";
    public static final String IDCARD_NUMBER_NOT_EXIST = "身份证号不能为空。";
    public static final String KEEPER_TYPE_NOT_EXIST = "网格员类型不能为空。";
    public static final String SEX_NOT_SAME = "性别不一致。";
    public static final String KEEPER_NAME_NOT_SAME = "姓名不一致。";
    public static final String PHONE_NUMBER_NOT_SAME ="手机号不一致。";
    public static final String EMAIL_NOT_SAME ="邮箱不一致。";
    public static final String DATE_FORMAT_ERROR ="日期格式错误。";
    public static final String GRID_NOT_DEL = "该网格下存在网格员，禁止删除。";
    public static final String GRID_HAS_CHILDREN = "该网格下存在子网格，禁止删除。";
    public static final String ID_CARD_NOT_NULL = "身份证号码已存在，请重新输入。";
    public static final String PHONE_NOT_NULL = "联系电话已存在，请重新输入。";
    public static final String GRID_ID_NOT_NULL = "请选择网格。";
    public static final String NO_DATA = "没有查询到数据。";
    public static final String ERROR_PARAM = "参数错误。";
    public static final String DUPLICAT_GRID = "该租户行业下计划名称已存在，请修改计划名称。";
    public static final String MORE_TIME = "当天任务派发时间需大于当前时间。";
    public static final String NO_STOP_TASK = "已完成的巡查计划不可删除。";
    public static final String INSPECTION_NO_STOP_TASK = "该计划产生的任务均未执行时才可以删除。";
    public static final String NO_EDIT = "已完成状态的巡查任务不可编辑。";
    public static final String IMPORT_DATA_NOT_NULL = "导入数据不能为空。";
    public static final String NET_CODE = "网格编码";
    public static final String NET_CODE_NULL = "网格编码为空。";
    public static final String NET_NAME_NULL = "网格名称为空。";
    public static final String PARENT_NET_CODE_NULL = "上级网格编码为空。";
    public static final String ORG_NAME_NULL = "所属组织为空。";
    public static final String PARENT_NET_NOT_FROM_OWN = "上级网格不属于该租户，无法导入。";
    public static final String CURLY_BRACES = "\\[";
    public static final String ENCLOSED_CHECK = "]";
    public static final String GRID_ID = "gridId";
    public static final String GRID_NAME = "gridName";
    public static final String GRID_LEVEL = "gridLevel";
    public static final String GRID_LEVEL_FIELD = "grid_level";
    public static final String GRID_LEVEL_NAME = "gridLevelName";
    public static final String NOT_GRID_KEEPER = "当前账号不是网格员。";
    public static final String GRID_RANGE = "gridRange";
    public static final String ONE_SIGN_IN = "上班打卡时段内只可打卡一次";
    public static final String NOT_ALLOW_SIGN_IN = "未在上班打卡时段，不允许签到。";
    public static final String NOT_ALLOW_SIGN_OUT = "未在下班打卡时段，不允许签退。";
    public static final String KEEPER_LEADER_EXIST = "该网格已有网格长。";
    public static final String USER_EXIST = "该用户已为网格员。";
    public static final String MAP_TYPE_GD = "01";
    public static final String ONE_ZERO_ONE = "101";
    public static final String PROVINCE_CODE = "provinceCode";
    public static final String LOGIN_USER = "loginUser";
    public static final String CITY_CODE = "cityCode";
    public static final String PROVINCE = "province";
    public static final String CITY = "city";
    public static final String COUNTRY_CODE = "districtCode";
    public static final String DATE_FORMATE = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMATE_DAY = "yyyy-MM-dd";
    public static final String FORMATTED_ADDRESS = "formattedAddress";
    public static final int NUMBER_ZERO = 0;
    public static final int NUMBER_ONE = 1;
    public static final int NUMBER_SIXTY = 60;
    public static final String RES_TRAIL_FAIL = "轨迹保存失败。";
    public static final String RES_TRAIL_SUCCESS = "轨迹保存成功。";
    public static final String KEEPER_ID = "keeperId";
    public static final String KEEPER_NAME = "keeperName";
    public static final String KEEPER_TYPE = "keeper_type";
    public static final String INDUSTRY_CODE = "industryCode";
    public static final String CREATE_TIME = "createTime";
    public static final String GRID_KEEPER_TRAIL = "gridKeeperTrail";
    public static final String CHECK_EXEL_MSG_1 = "经度不能为空。";
    public static final String CHECK_EXEL_MSG_2 = "纬度不能为空。";
    public static final String CHECK_EXEL_MSG_3 = "资源名称不能为空。";
    public static final String CHECK_EXEL_MSG_4 = "资源名称已存在。";
    public static final String CHECK_EXEL_MSG_5 = "资源类型名称不能为空。";
    public static final String CHECK_EXEL_MSG_6 = "资源类型名称不存在。";
    public static final String CHECK_EXEL_MSG_7 = "经纬度转化失败。";
    public static final String CHECK_EXEL_MSG_8 = "详细地址不能为空。";
    public static final String CHECK_EXEL_MSG_9 = "资源属性名称不能为空并且必须是点资源。";
    public static final String CHECK_EXEL_MSG_10 = "点资源";

    public static final String CHECK_EXCEL_ADMIN_LENGTH_ERR_MSG = "管理员长度超出限制，最多为20字符";

    public static final String CHECK_EXCEL_REMARK_ERR_MSG = "描述长度超出限制，最多为200字符";


    public static final String CHECK_EXCEL_TEL_ERR_MSG = "联系电话格式错误";

    public static final String CHECK_EXEL_MSG_11 = "资源属性名称和资源类型不匹配。";

    public static final String USER_NOT_LOGIN_MSG = "用户未登录";
    public static final String LINE_CENTER = "-";
    public static final String ZERO_STR = "0";
    public static final int ONE_THOUSAND = 1000;
    public static final int EXCEL_EXPORT_LIMIT = 5000;
    public static final String EXCEL_MORE = "导出数据量最多5000条，请按照查询条件导出。";
    public static final String CHECK_ERROR_MSG = "处理异常:";
    public static final String DATA_ERROR_MSG = "数据存储数据库失败。";
    public static final String ZERO_ONE_STR = "01";
    public static final String LOCATIONS_STR = "locations";
    public static final String CHECKTWICEGRIDNAME = "网格名称已存在";
    public static final String EXIST_FIRST_GRID = "同一租户下已存在最高级网格。";
    public static final String NOT_EXIST_TENANT_ID = "没有找到该租户。";
    public static final String NOT_EXIST_INDUSTRY = "没有找到该行业。";
    public static final String NOT_EQUALS_TENANT = "请添加本租户本行业下的网格。";
    public static final String NO_ALLOW_ADD = "不允许手动添加最高级网格。";
    public static final String NOT_PARENT_GRID = "没有查询到父级网格。";
    public static final String SYS_WEEK_DAY = "sys_week_day";
    public static final String INSPECTION_TASK_STATUS = "insoection_task_status";
    public static final String CHECK_IN_ARG_1 = "tenantId不能为空。";
    public static final String CHECK_IN_ARG_2 = "tenantName不能为空。";
    public static final String CHECK_IN_ARG_3 = "industryCode不能为空。";
    public static final String CHECK_IN_ARG_4 = "industryName不能为空。";
    public static final String CHECK_IN_ARG_5 = "appVerCode不能为空。";
    public static final String CHECK_IN_ARG_6 = "gridEmCenter不能为空。";
    public static final String CHECK_IN_ARG = "请求参数不能为空。";
    public static final String DEL_EXIT_TASK = "网格员存在代办项，不可删除。";
    public static final String DEL_EXIT_TASK_SEND = "网格员存在待完成的巡查任务，请处理完成后再进行删除";
    public static final String DEL_EXIT_TASK_NOT_SEND = "网格员存在尚未下发的巡查任务，请先从巡查计划的巡查人中移除该网格员后再进行删除";
    public static final String STOP_EXIT_TASK = "网格员存在代办项，不可停用。";
    public static final String STOP_EXIT_TASK_SEND = "网格员存在待完成的巡查任务，请处理完成后再进行停用。";
    public static final String STOP_EXIT_TASK_NOT_SEND = "网格员存在尚未下发的巡查任务，请先从巡查计划的巡查人中移除该网格员后再进行停用。";
    public static final String EDIT_EXIT_TASK_SEND = "网格员存在待完成的巡查任务，请处理完成后再进行网格修改。";
    public static final String EDIT_EXIT_TASK_NOT_SEND = "网格员存在尚未下发的巡查任务，请先从巡查计划的巡查人中移除该网格员后再进行网格修改。";
    public static final String NOT_EMPTY_KEEPER_ID = "网格员id不能为空。";
    public static final String TENANT_ID = "tenantId";
    public static final String LEFT_BRACKETS = "[[";
    public static final String RIGHT_BRACKETS = "]]";
    public static final String TASK_JOB = "task_job";
    public static final String FORMAT_ADDRESS = "formattedAddress";
    public static final String DOSIGNINSTATUS = "doSignIn打卡状态:";
    public static final int fu_ONE_NUMBER = -1;
    public static final String HORIZON_LINE = "-";
    public static final String POLITICAL_STATUS = "political_status";
    public static final String SYS_USER_SEX = "sys_user_sex";

    public static final String DATE_FORMATE_HMS = "HH:mm:ss";
    public static final String INSPECTION_PERIOD_ERROR = "addConfInfo接口巡航时段转换异常";
    public static final String ADD_CONF_INFO_ERROR = "addConfInfo接口异常";

    public static final int NUMBER_TEN = 10;
    public static final String CREATE_TIME_NOT_NULL_INFO = "任务下发时间不能为空";
    public static final String TRAIL_TIME_NOT_NULL_INFO = "轨迹开始时间和结束时间不能为空";
    public static final String H_M_S = " 23:59:59";
    public static final String APP_URL_PREFIX = "/pages/work-inspect/work-inspect-detail?taskId=";
    public static final String APP_URL_SUFFIX = "&&taskStatus=";
    public static final String TASK_ID_NOT_NULL_INFO = "巡查任务id不能为空";
    public static final String TASK_COMPLETED_INFO = "该巡查任务已完成";
    public static final String NO_ATTENDANCE_RULE = "暂无考勤规则";

    public static final String ATTENDANCE_KEY = "attendance";
    public static final String UNDERLINE = "_";
    public static final int CACHE_KEY_TIME = 86400;
    public static final String NO_USER = "当前账号不存在。";
    public static final String _PARAM_LOG_REQUEST = " 请求报文=={}";
    public static final String _PARAM_LOG_RESPONSE = " 响应报文=={}";
    public static final int CODE_SUCCESS = 200;
    public static final String DISTANCE = "distance";
    public static final String LONGITUDE = "longitude";
    public static final String LATITUDE = "latitude";
    public static final String ALARMBODY = "alarmBody";
    public static final String ADDRESS = "address";
    public static final String WARNING_ORDER_ID = "warningOrderId";
    public static final String STRING_FORMAT_1F = "%.1f";
    public static final int THOUSAND_NUM = 1000;
    public static final String ZERO_ZERO_FLAG = "0.0";
    public static final String ZERO_ONE_FLAG = "0.1";

    public static final int ROUTE_STATUS_IDLE = 0;

    public static final int ROUTE_STATUS_WORK = 1;

    public static final int ROUTE_STATUS_PAUSE = 2;

    public static final String PATROL_PLAN_PAUSE_STATUS = "3";

    public static final String PATROL_PLAN_RESUME_STATUS = "4";

    public static final int INSPECTION_ROUTE_PROCESS_START = 1;

    public static final int INSPECTION_ROUTE_PROCESS_UPDATE = 2;

    public static final int INSPECTION_ROUTE_PROCESS_PAUSE = 3;

    public static final int INSPECTION_ROUTE_PROCESS_RECOVER = 4;

    public static final int INSPECTION_ROUTE_PROCESS_RESET = 5;

    public static final int INSPECTION_ROUTE_PROCESS_FINISH = 6;

    public static final String CONFIG_INSPECTION_RESET_TIME = "inspection_reset_time";

    public static final String CONFIG_VIDEO_INSPECTION_RESET_TIME = "video_inspection_reset_time";

    public static final String SUCCESS_CODE = "200";

    public static final String START_TIME_SUFFIX = " 00:00:00";
    public static final String END_TIME_SUFFIX = " 23:59:59";

    public static final int ZERO_NUM = 0;
    public static final int ONE_NUM = 1;
    public static final int TWO_NUM = 2;
    public static final int THREE_NUM = 3;
    public static final int FOUR_NUM = 4;
    public static final int FIVE_NUM = 5;
    public static final int SEVEN_NUM = 7;
    public static final int MILLISECONDS_PER_DAY = 86400000;

    public static final String VIDEO_AND_URL_MSG_1 = "周围暂无可用设备，请重新选点";

    public static final String U4E00 = "[\\u4E00-\\u9FA5]+";
    public static final String PINYIN_ERROR = "获取拼音首字母异常";

    public static final String PARAM_FEATURES = "features";

    public static final String PARAM_PROPERTIES = "properties";

    public static final String PARAM_BSM = "bsm";

    public static final String PARAM_DLMC = "dlmc";

    public static final String PARAM_ZLDWMC = "zldwmc";

    public static final String PARAM_MODEL = "model";

    public static final String PARAM_MESSAGES = "messages";

    public static final String PARAM_MESSAGE = "message";

    public static final String PARAM_ROLE = "role";

    public static final String PARAM_CONTENT = "content";

    public static final String PARAM_PIC_ID = "pic_id";

    public static final String PARAM_TYPE = "type";

    public static final String PARAM_TEXT = "text";

    public static final String PARAM_URL = "url";

    public static final String PARAM_IMAGE_URL = "image_url";

    public static final String PARAM_IMG_URL = "img_url";

    public static final String LIMIT_COUNT = "limitCount";
    public static final String POI_CONTRARY_GEOCODE_PARAM = "逆地理编码查询接口入参：";
    public static final String POI_CONTRARY_GEOCODE_RETURN = "逆地理编码查询接口返回：";
    public static final String NOT_LOCATION = "铁塔切换客户视角不记录用户手机位置信息";
    public static final String SAVE_LNG_LAT_ERROR = "网格员打点接口异常";

    public static final String VEHICLE_LICENSE_PLATE_NULL = "车牌号码不能为空";
    public static final String VEHICLE_LICENSE_PLATE_ERROR = "车牌号输入有误";
    public static final String VEHICLE_LICENSE_PLATE_HAVED = "车牌号已经存在";

    public static final String VALID_PERIOD_FORMAT_ERROR = "生效时间段格式不正确";
    public static final String VALID_PERIOD_OVERLAP_ERROR = "您输入的时间段存在重叠";
    public static final String VALID_PERIOD_STARTGTEND_ERROR = "您输入的时间段存在起始时间大于结束时间";

    // 用于出入口管理车牌号导入时间段校验的正则表达式  2024-2-21 feng.ty 修改 支持中英文冒号
    public static final String VALID_PERIOD_REGEX = "(?:[01][0-9]|2[0-3]):[0-5][0-9]~(?:[01][0-9]|2[0-3]):[0-5][0-9](?:,(?:[01][0-9]|2[0-3]):[0-5][0-9]~(?:[01][0-9]|2[0-3]):[0-5][0-9])*";

    //普通车牌车牌号验证正则表达式
    public static final String ORDINARY_PATTERN = "^[京津冀晋蒙辽吉黑沪苏浙皖闽赣鲁豫鄂湘粤桂琼川贵云渝藏陕甘青宁新]{1}[ABCDEFGHJKLMNPQRSTUVWXY]{1}[A-Z0-9]{5}$";
    // 新能源车牌车牌号验证正则表达式
    public static final String NEW_ENERGY_PATTERN = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[DF]{1}[1-9A-HJ-NP-Z]{1}[0-9]{4}$";
    // 特殊车牌车牌号验证正则表达式
    public static final String SPECIAL_PATTERN = "^[京津冀晋蒙辽吉黑沪苏浙皖闽赣鲁豫鄂湘粤桂琼川贵云渝藏陕甘青宁新]{1}[ABCDEFGHJKLMNPQRSTUVWXY]{1}[A-Z0-9]{4}[学挂领试超练警]{1}$";
    public static final String LAYER_NEST_KAFKA_TOPIC = "nest_layer";

    public static final String TWO_SQUARE_BRACKETS = "],[";
    public static final String VERTICAL_LINE = "|";
    public static final String LEFT_SQUARE_BRACKET = "[";
    public static final String RIGHT_SQUARE_BRACKET = "]";


    public static final String TASK_INFO_VO = "获取巡查任务详情vo:";
    public static final String TASK_INFO_ID = "获取巡查任务详情taskId:";

    public static final String IMPORTANCE_LEVEL = "importanceLevel";

    public static final String IMPORTANCE_LEVEL_NAME = "importanceLevelName";

    public static final String NO_ALARM = "图片分析无告警信息";
    public static final String SECOND_JUDGE_RESULT = "secondJudgeResult";
    public static final String EXTEND_FIELD = "extendField";
    public static final String ATTR_LIST = "attrList";
    public static final String ATTR_VALUE = "attrValue";
    public static final String SUCCESS_MSG = "success";
    public static final String MESSAGE = "message";
    public static final String TIME_LINE_NOT_EXIST = "时间轴不存在";
    public static final String TIME_LINE_ID_NOT_EXIST = "时间轴ID不能为空";
    public static final String SORT_NUM_NOT_EXIST = "排序号不能为空";
    public static final String SORT_NUM_FORMAT_ERROR = "排序号格式错误";
    public static final String DEFAULT_TIME_LINE_NOT_EXIST = "不存在默认时间轴";

    /**
     * 时间轴相关异常消息常量
     */
    public static class TIME_LINE_EXCEPTION {
        /** 不存在当前查询数据 */
        public static final String TIMELINE_NOT_FOUND = "不存在当前查询数据";
        /** 时间轴配置不能为空 */
        public static final String TIMELINE_CONFIG_EMPTY = "时间轴配置不能为空";
        /** 时间轴配置详情列表不能为空 */
        public static final String TIMELINE_DETAIL_LIST_EMPTY = "时间轴配置详情列表不能为空";
        /** 时间轴名称不能为空 */
        public static final String TIMELINE_NAME_EMPTY = "时间轴名称不能为空";
        /** 第%d行对应的图层信息不能为空 */
        public static final String LAYER_INFO_EMPTY = "第%d行对应的图层信息不能为空";
        /** 第%d行对应的年份信息不能为空 */
        public static final String YEAR_INFO_EMPTY = "第%d行对应的年份信息不能为空";
        /** 第%d行对应的年份信息:%s不正确，请输入正确年份 */
        public static final String YEAR_FORMAT_ERROR = "第%d行对应的年份信息:%s不正确，请输入正确年份";
        /** 第%d行对应的月份信息:%s不正确，请输入正确月份 */
        public static final String MONTH_FORMAT_ERROR = "第%d行对应的月份信息:%s不正确，请输入正确月份";
        /** 第%d行对应的天信息:%s不正确，请输入正确天 */
        public static final String DAY_FORMAT_ERROR = "第%d行对应的天信息:%s不正确，请输入正确天";
        /** 修改数据失败，主键为空 */
        public static final String UPDATE_ID_EMPTY = "修改数据失败，主键为空";
        /** 修改失败，数据不存在 */
        public static final String UPDATE_DATA_NOT_EXIST = "修改失败，数据不存在";
    }

    /**
     * 时间轴校验常量
     */
    public static final class TIME_LIME_CONSTANT {
        /**
         * 年份校验
         */
        public static final String PATTERN_YEAR = "(?:(?:19|20)[0-9]{2})";
        /**
         * 月份校验
         */
        public static final String PATTERN_MONTH = "^(0?[1-9]|1[0-2])$";
        /**
         * 天数校验
         */
        public static final String PATTERN_DAY = "^((0?[1-9])|((1|2)[0-9])|30|31)$";

        /**
         * 新增时间轴
         */
        public static final String ADD = "add";

        /**
         * 修改时间轴
         */
        public static final String UPDATE = "update";

        /**
         * 是否展示时间轴详情 Y-展示 N-不展示
         */
        public static final String SHOW_DETAIL_Y = "Y";

        /**
         * 修改时间轴
         */
        public static final String SHOW_DETAIL_N = "N";

        // 正则表达式
        public static final String PHONE_REGEX = "^1[3-9]\\d{9}$";
        public static final String LANDLINE_REGEX = "^(0\\d{2,3}-)?\\d{7,8}$";


    }
}
