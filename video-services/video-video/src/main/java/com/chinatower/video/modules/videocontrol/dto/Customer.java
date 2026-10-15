package com.chinatower.video.modules.videocontrol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: Customer
 * @Description:
 * @Author: linrg
 * @Date: 2022/11/21 14:16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    private String level;

    private String userCode;

    private String customerCode;

    private String deptCode;
}
