package com.chinatower.sync.consumer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author yy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceSyncErrorDto implements Serializable {

    private String id;

    private String date;

    private List<String> list;

    private List<String> deviceCodes;

    private Integer retryCount;
}
