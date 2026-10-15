package com.chinatower.video.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelsTenantDTO implements Serializable {

    private List<String> channels;

    private String tenantId;

    private String industryCode;
}
