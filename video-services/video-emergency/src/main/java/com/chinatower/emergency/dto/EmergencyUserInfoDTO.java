package com.chinatower.emergency.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmergencyUserInfoDTO implements Serializable {

    private String userId;

    private String userName;

    private String nickName;

    private String phoneNumber;

    private String emergencyDeptName;

    private String deptId;

}
