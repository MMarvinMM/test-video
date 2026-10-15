package com.chinatower.sync.consumer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author yy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskIdAndStatusDto implements Serializable {

    private String taskId;

    private String algorithmStatus;

    private String pushStatus;

    private Long ts;
}
