package com.chinatower.platform.project.entity.request;

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
public class JpushResDto implements Serializable {

    private int code;

    private String msg;
}
