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
 * 设备通道算法信息.
 *
 * @author: fangjun
 */
@Data
public class DeviceAlgorithms implements Serializable {

    /**
     * 设备编码.
     */
    private String deviceCode;

    /**
     * 设备名称.
     */
    private String deviceName;

    /**
     * 省份编码.
     */
    private String proCode;

    /**
     * 省份名称.
     */
    private String proName;

    /**
     * 地市编码.
     */
    private String orgCode;

    /**
     * 地市名称.
     */
    private String orgName;

    /**
     * 通道算法列表.
     */
    private List<ChannelAlgorithm> channelList;
}
