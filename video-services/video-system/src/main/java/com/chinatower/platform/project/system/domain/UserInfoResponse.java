package com.chinatower.platform.project.system.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserInfoResponse extends LanXinBaseResponse<UserInfoResponse.UserInfoData> {
    @Data
    public static class UserInfoData {
        private String staffId;
        private String name;
        private String orgId;
        private String orgName;
        private String avatarId;
        private String avatarUrl;
        private MobilePhone mobilePhone;
        private String email;
        private String employeeNumber;
        private String loginName;
        private String externalId;
        private List<Department> department;
    }

    @Data
    public static class MobilePhone {
        private String countryCode;
        private String number;
    }

    @Data
    public static class Department {
        private String id;
        private String name;
    }

}