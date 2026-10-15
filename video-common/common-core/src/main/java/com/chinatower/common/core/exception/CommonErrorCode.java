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

package com.chinatower.common.core.exception;

/**
 * CommonErrorCode.
 *
 * @author zyp
 */
public class CommonErrorCode {

    /**
     * The constant SUCCESSFUL.
     */
    public static final String SUCCESSFUL_CODE = "0000";

    /**
     * The constant ERROR.
     */
    public static final String ERROR_CODE = "8888";

    /**
     * The constant SUCCESSFUL_MSG.
     */
    public static final String SUCCESSFUL_MSG = "OK";

    /**
     * The constant ERROR_MSG.
     */
    public static final String ERROR_MSG = "请求失败";

    /**
     * 消息服务-用户与消息服务通道连接失败
     */
    public static final String MESSAGE_CHANNEL_CONNECT_FAIL_CODE = "9001";

    /**
     * 消息服务-用户与消息服务通道连接失败
     */
    public static final String MESSAGE_CHANNEL_CONNECT_FAIL_MSG = "用户与消息服务通道连接已断开";

}
