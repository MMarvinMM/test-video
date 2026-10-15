package com.chinatower.video.modules.videocontrol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 云台令牌.
 *
 * @author: fangjun
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LockTokenData implements Serializable {

    private String token;

    private boolean enabled;

    private Customer customer;
}