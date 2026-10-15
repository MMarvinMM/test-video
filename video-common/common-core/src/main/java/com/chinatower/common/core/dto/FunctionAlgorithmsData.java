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

package com.chinatower.common.core.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 北向接口文档：5.5. 功能与算法关系信息推送，请求报文消息体.
 *
 * @author: fangjun
 */
@Data
public class FunctionAlgorithmsData implements Serializable {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -8286145277042767071L;

    /**
     * 功能编码 Y.
     */
    private String functionCode;

    /**
     * 功能名称 N 新增/修改时为必填.
     */
    private String functionName;

    /**
     * 算法信息 N 新增/修改时为必填；一个功能可能包含对个算法.
     */
    private List<AlgorithmData> algorithmDatas;

    /**
     * 操作类型 Y int 1-新增；2-修改；3-删除.
     */
    private Integer optType;

}
