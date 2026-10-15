package com.chinatower.airline.dto.response;

import lombok.Data;

/**
 * @author WLF
 * @description
 * @date 2025/1/2 10:54
 */
@Data
public class AirLineSnapshotRsp {
    private String id;
    private String flouterId;
    private String extraField;
    private String kmzAccessUrl;
}
