package com.chinatower.product.api.modules.common;

import io.jsonwebtoken.Claims;

/**
 * 常量类
 */
public final class Constants {

	public static final String XAPPKEY = "X-AppKey";
	
	public static final String CUSTOMER_CODE = "X-CustomerCode";

	public static final String USER_CODE = "X-UserCode";

	public static final String ORDER_CODE = "X-OrderCode";

	public static final String APP_CODE = "X-AppCode";

	public static final String X_TOKEN = "X-Token";
	
	public static final String CURRENT_PAGE = "currentPage";
	
	public static final String PAGE_SIZE = "pageSize";
	
	public static final String STORAGE_TYPE = "storageType";
	
	public static final String RECORD_TYPE = "recordType";
	
	public static final String START_TIME = "startTime";
	
	public static final String END_TIME = "endTime";
	
	public static final String REQUEST_FROM_API = "api";

	public static final String MESS_1 = "请求异常,进入fallback，异常信息{}";

	public static final String UNKNOWN = "unknown";
	
	public static final String ERROR_CODE_3006 = "3006";
	public static final String SUCCESS = "SUCCESS";
	public static final String ERROR_CODE = "errorCode";
	public static final String ERROR_CODE_1003 = "1003";
	public static final String PRESETID = "presetId";
	public static final String PRESETNAME = "presetName";

	/**
	 * UTF-8 字符集
	 */
	public static final String UTF8 = "UTF-8";

	/**
	 * 通用成功标识
	 */
//	public static final String SUCCESS = "0";

	/**
	 * 通用失败标识
	 */
//	public static final String FAIL = "1";

	/**
	 * 登录成功
	 */
	public static final String LOGIN_SUCCESS = "Success";

	/**
	 * 注销
	 */
	public static final String LOGOUT = "Logout";

	/**
	 * 登录失败
	 */
	public static final String LOGIN_FAIL = "Error";

	/**
	 * 验证码 redis key
	 */
	public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

	/**
	 * 登录用户 redis key
	 */
	public static final String LOGIN_TOKEN_KEY = "login_tokens:";

	/**
	 * 验证码有效期（分钟）
	 */
	public static final Integer CAPTCHA_EXPIRATION = 2;

	/**
	 * 令牌
	 */
	public static final String TOKEN = "token";

	/**
	 * 令牌前缀
	 */
	public static final String TOKEN_PREFIX = "Bearer ";

	/**
	 * 令牌前缀
	 */
	public static final String LOGIN_USER_KEY = "login_user_key";

	/**
	 * 用户ID
	 */
	public static final String JWT_USERID = "userid";

	/**
	 * 用户名称
	 */
	public static final String JWT_USERNAME = Claims.SUBJECT;

	/**
	 * 用户头像
	 */
	public static final String JWT_AVATAR = "avatar";

	/**
	 * 创建时间
	 */
	public static final String JWT_CREATED = "created";

	/**
	 * 用户权限
	 */
	public static final String JWT_AUTHORITIES = "authorities";

	/**
	 * 资源映射路径 前缀
	 */
	public static final String RESOURCE_PREFIX = "/profile";

	/**
	 * 渠道级别 一级渠道
	 */
	public static final String CHANNEL_LEVEL_FIRST = "1";

	/**
	 * 渠道级别 二级渠道
	 */
	public static final String CHANNEL_LEVEL_SECOND = "2";

	/**
	 * 0系统用户
	 */
	public static final String SYS_USER_TYPE_0 = "0";
	/**
	 * 1集团用户
	 */
	public static final String SYS_USER_TYPE_1 = "1";

	public static final String BATTERY_ORG_TYPE_0 = "0";

	public static final String BATTERY_ORG_TYPE_1 = "1";

	public static final String BATTERY_ORG_TYPE_2 = "2";

	public static final String BATTERY_ORG_TYPE_4 = "4";

	public static final String ORG_TYPE_4 = "4";

	public static final String SYS_DEPT_1 = "1";
	public static final String SYS_DEPT_2 = "2";
	public static final String SYS_DEPT_3 = "3";

	public static final String SQL = "sql";

	public static final String TYPE_PERSON = "0";

	public static final String TYPE_CHANNEL = "1";

	public static final String TYPE_ENTERPRISE = "2";

	public static final String TYPE_POSTAL = "3";

	public static final String TYPE_BLOC = "4";

	public static final String PAGE_NUM  = "pageNum";

	public static final String DEPT_ROOT_ID = "100000";

	public static final String ZERO = "0";

	public static final String ONE = "1";

	public static final String TWO = "2";

	public static final String THREE = "3";

	public static final String FOUR = "4";

	public static final String FIVE = "5";

	public static final String SIX = "6";

	public static final String SEVEN = "7";

	public static final String EIGHT = "8";

	public static final String AREA_FORMAT = "areaFormat";

	public static final String DEPT_FORMAT = "deptFormat";


	public static final String COUNTRY_DEPT = "COUNTRY";

	public static final String PROVINCE_DEPT = "PROVINCE";

	public static final String CITY_DEPT = "CITY";


	public static final String CID = "cityId";

	public static final String PID = "provinceId";

	public static final String DEL_FLAG = "delFlag";

	public static final String M_CTLY ="110000,120000,310000,500000";

	/**
	 * 资源池消费 ASSETS_PREEMPT:占用,ASSETS_EXPENSE:消费,ASSETS_REFUND:退还
	 */
	public static final String ASSETS_PREEMPT ="PREEMPT";
	public static final String ASSETS_EXPENSE ="EXPENSE";
	public static final String ASSETS_REFUND ="REFUND";

	/**
	 * 成功
	 */
	public static final String S ="S";
	/**
	 * 失败
	 */
	public static final String F ="F";
	/**
	 * 是
	 */
	public static final String Y ="Y";
	/**
	 * 否
	 */
	public static final String N ="N";

	/**
	 * 否
	 */
	public static final String USER_REFUND_MSG_TOPIC ="userRefundMsg";
	public static final String BLANK_STR ="";


	/**
	 * 新增类型 1-pws未录入 2-pws已录入
	 */
	public static final String stationAddType1="1";
	public static final String stationAddType2="2";
	public static final String QUESTION_MARK = "?";
	public static final String QUESTION_MARK_QUES = "问号";
	public static final String QUESTION_MARK_NEW =  "[?]";

	/**
	 * 菜单类型
	 */
	public static final String DIRECTORY = "M";
	public static final String MENU = "C";
	public static final String BUTTON = "F";

	public static final String USER_NAME = "ADMIN_REPORT";



	public static final String LOGIN_FAIL_NUM_KEY = "login_fail_num:";

	public static final String MESSAGE_SYSTEM = "500";
	public static final String MESSAGE_NOTICE = "501";
	public static final String MESSAGE_ACTIVITY = "502";

	public static final int ONE_NUM = 1;

	public static final String UTF8S = "utf-8";
//	public static final String MESS_1 = "] 中，没有找到 [";
	public static String imgType = "img";
	public static String videoType = "video";
	
	public static final String ERROR_QQSB = "请求失败";
	public static final String ERROR_CODE_1002 = "1002";
	public static final String ERROR_WQFW = "无权访问";
	public static final String SDK_TO_API = "sdkToApi_";
	public static final String INDUSTRY_CODE = "industryCode";
}
