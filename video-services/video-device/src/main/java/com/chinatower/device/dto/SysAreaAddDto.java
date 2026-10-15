package com.chinatower.device.dto;

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
public class SysAreaAddDto implements Serializable {

    private long id;

    private String townCode;

    private String township;

    private String adCode;

    private String district;

}