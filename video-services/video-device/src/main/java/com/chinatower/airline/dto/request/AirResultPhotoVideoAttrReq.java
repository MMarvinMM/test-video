package com.chinatower.airline.dto.request;

import lombok.Data;

import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2025/3/12 14:41
 */
@Data
public class AirResultPhotoVideoAttrReq {
    private List<String> videoIds;
    private List<String> photoIds;
}
