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

package com.chinatower.common.core.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 异常类.
 *
 * @author zyp
 */
@RequiredArgsConstructor
@Getter
public enum CommonErrorEnum {

    /**
     * SYSTEM_CODE param type enum.
     */
    SYSTEM_CODE("9999", "系统繁忙，请稍后再试."),

    /**
     * CUSTOMER_CODE param type enum.
     */
    CUSTOMER_CODE("1001", "客户编号customerCode不能为空."),

    /**
     * param format error.
     */
    PARAM_FORMAT_ERROR("1001", "参数错误"),

    /**
     * ADM_CODE param type enum.
     */
    ADM_CODE("1002", "查询指定行政区域admCode不能为空."),

    /**
     * ADM_CODE param type enum.
     */
    ADM_NO_EXIST("1003", "无此行政区域."),

    BLANK_CHANNELS("1004", "设备通道不能为空"),

    BLANK_DEVICE_CODE("1005", "设备编码不能为空"),

    BLANK_CHANNEL_CODE("1005", "通道编码不能为空"),

    ADM_AREA_NO_EXIST("1003", "该管理区域与行政区域关系不存在.");


    private final String errorCode;

    private final String errorMsg;


}
