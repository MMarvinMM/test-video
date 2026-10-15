package com.chinatower.cascade.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllPlatformQueryVO implements Serializable {

    private String randomKey;

    private String time;

    private String sk;

    /**
     * token = AES(randomKey-time-sk)
     */
    private String token;
}
