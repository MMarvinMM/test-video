package com.chinatower.auth.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserIdLoginVo implements Serializable {

    private String code;

    private String status;

    private String userId;

    private String token;
}
