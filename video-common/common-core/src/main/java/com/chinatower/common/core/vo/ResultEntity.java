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

package com.chinatower.common.core.vo;

import com.chinatower.common.core.constant.CommonErrorEnum;
import com.chinatower.common.core.exception.CommonErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * ResultEntity.
 *
 * @author zyp
 */
@Data
@Builder
@AllArgsConstructor
public class ResultEntity {

    /**
     * Y 返回编码->请求成功：0000 /请求失败：8888.
     */
    private String returnCode;

    /**
     * Y 返回信息.  ->请求成功：0000 /请求失败：8888.
     */
    private String returnMsg;

    /**
     * 错误编码. retureCode为8888时必须写明错误代码.
     */

    private String errorCode;

    /**
     * 错误消息->retureCode为8888时必须写明错误代码.
     */
    private String errorMsg;

    private Object data;

    private Object rows;

    private String total;

    private String code;

    private String message;


    /**
     * 〈构建函数〉.
     *
     * @param data .
     * @author zyp
     */
    public ResultEntity(final Object data) {
        this.returnMsg = CommonErrorCode.SUCCESSFUL_MSG;
        this.returnCode = CommonErrorCode.SUCCESSFUL_CODE;
        this.data = data;
    }

    /**
     * 〈构建函数〉.
     *
     * @author zyp
     */
    public ResultEntity() {
        this.returnMsg = CommonErrorCode.SUCCESSFUL_MSG;
        this.returnCode = CommonErrorCode.SUCCESSFUL_CODE;
    }

    /**
     * 〈构建函数〉.
     *
     * @param errorCode .
     * @param errorMsg  .
     * @author zyp
     */
    public ResultEntity(final String errorCode, final String errorMsg) {
        this.returnMsg = CommonErrorCode.ERROR_MSG;
        this.returnCode = CommonErrorCode.ERROR_CODE;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    /**
     * 〈构建函数〉.
     *
     * @param code .
     * @author zyp
     */
    public ResultEntity(final CommonErrorEnum code) {
        this.returnMsg = CommonErrorCode.ERROR_MSG;
        this.returnCode = CommonErrorCode.ERROR_CODE;
        this.errorCode = code.getErrorCode();
        this.errorMsg = code.getErrorMsg();
    }

    /**
     * return success.
     *
     * @param data data
     * @return {@linkplain ResultEntity}
     */
    public static ResultEntity success(final Object data) {
        return new ResultEntity(data);
    }

    /**
     * return success.
     *
     * @return {@linkplain ResultEntity}
     */
    public static ResultEntity success() {
        return new ResultEntity();
    }

    /**
     * return success.
     *
     * @param code code
     * @return {@linkplain ResultEntity}
     */
    public static ResultEntity error(final CommonErrorEnum code) {

        return new ResultEntity(code);
    }

}
