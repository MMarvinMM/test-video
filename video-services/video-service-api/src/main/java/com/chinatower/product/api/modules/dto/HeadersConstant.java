/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chinatower.product.api.modules.dto;

/**
 * http headers参数常量类.
 *
 * @author: fangjun
 */
public class HeadersConstant {

    /**
     * dispose服务headers参数：应用编码.
     */
    public static final String DISPOSE_APP_ID = "X-AppCode";

    /**
     * headers参数：APPKey.
     */
    public static final String HEADER_KEY_APP_KEY = "X-AppKey";

    /**
     * headers参数：签名值.
     */
    public static final String HEADER_KEY_SIGN = "X-Signature";

    /**
     * 通用header MsgId.
     */
    public static final String HEADER_KEY_MSG_ID = "X-MsgId";

    /**
     * 通用header 请求时间 .
     */
    public static final String HEADER_KEY_REQUES_TTIME = "X-RequestTime";

    /**
     * 通用header 应用编码.
     */
    public static final String HEADER_KEY_APP_CODE = "X-AppCode";


    /**
     * 通用header 应用编码.
     */
    public static final String HEADER_KEY_INDUSTRY_CODE= "X-IndustryCode";


    /**
     * headers参数，请求的客户唯一编码.
     */
    public static final String HEADER_KEY_CUSTOMER_CODE = "X-CustomerCode";

    /**
     * headers参数订购关系编码.
     */
    public static final String HEADER_KEY_ORDER_CODE = "X-OrderCode";

    /**
     * headers参数，请求的用户编码.
     */
    public static final String HEADER_KEY_USER_CODE = "X-UserCode";

    /**
     * headers参数：云台控制令牌.
     */
    //public static final String HEADER_KEY_LOCK_TOKEN = "X-LockToken";

    /**
     * headers参数：流UUID（视频流的唯一编码，对流进中断/限制带宽等操作）.
     */
    public static final String HEADER_KEY_STREAM_UUID = "X-StreamUuid";

    /**
     * 南向服务headers参数，认证令牌(值：APPCODE + “空格” + 应用的AppCode值).
     */
    public static final String SOUTH_AUTHORIZATION = "Authorization";

    /**
     * 南向服务headers参数，服务端接口名称.
     */
    public static final String SOUTH_SERVER_CODE = "X-ServerCode";

    /**
     * 南向服务headers参数，应用平台的编码.
     */
    public static final String SOUTH_APP_CODE = "X-AppCode";

    /**
     * 南向服务headers参数，AppKey的值.
     */
    public static final String SOUTH_APP_KEY = "X-AppKey";

    /**
     * 南向服务headers参数，请求签名计算值.
     */
    public static final String SOUTH_SIGNATURE = "X-Signature";

    /**
     * 南向服务headers参数，云台控制令牌.
     */
    public static final String SOUTH_TOKEN = "X-Token";

    /**
     * 南向服务headers参数，Accept.
     */
    public static final String ACCEPT = "Accept";

    /**
     * 向服务headers参数，CONTENT_TYPE.
     */
    public static final String CONTENT_TYPE = " Content-Type";

    /**
     * 用于铁塔用户鉴权，铁塔账号请求时为必填项，传入铁塔人员4A登录账号.
     */
    public static final String HEADER_KEY_TOWER_CODE = "X-TowerCode";

    /**
     * 当获取控制权时为必填，单位秒
     */
    public static final String HEADER_KEY_DURATION = "X-Duration";

    /**
     * 当前用户的优先级，数字越大，优先级越高，优先级高的会抢占锁
     */
    public static final String HEADER_KEY_LEVEL = "X-level";

    /**
     * 申请锁用户的归属组织
     */
    public static final String HEADER_KEY_DEPTCODE = "X-deptCode";
}
