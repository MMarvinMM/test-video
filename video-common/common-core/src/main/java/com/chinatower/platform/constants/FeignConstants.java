package com.chinatower.platform.constants;

/**
 * 功能说明:
 *
 * @author 李峥
 * @date 2021-12-05 10:20
 * @requestID 需求单号及需求名称
 */
public class FeignConstants {

    /**
     *header 中userId
     */
    public static final String HEADER_USERID = "userId";


    /**
     *header 中clientId:userId的key
     */
    public static final String HEADER_USER_KEY = "userKey";

    /**
     * warning:建议采用全小写，header传过来时处理成了全小写，其他服务都上线了，这次就不变了
     * 行业编码
     */
    public static final String HEADER_INDUSTRY_CODE = "Cur-Industry-Code";

    /**
     * warning:建议采用全小写，header传过来时处理成了全小写
     * 预留字段，json串
     */
    public static final String HEADER_INDUSTRY_COMMON_JSON = "industry-params";
    /**
     * 操作系统
     */
    public static final String HEADER_CLIENT_OS = "client_os";
    /**
     * 手机型号
     */
    public static final String HEADER_CLIENT_MODEL = "client_model";
    /**
     * 手机版本号
     */
    public static final String HEADER_CLIENT_VERSION = "client_version";
    /**
     * 小程序标识 client_type
     */
    public static final String HEADER_CLIENT_TYPE = "client_type";
    /**
     * app标识 client_name
     */
    public static final String HEADER_CLIENT_NAME = "client_name";

    public static final String HEADER_CLIENT_PC = "pc";
}
