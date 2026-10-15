package com.chinatower.airline.dto.response;

import lombok.Data;

/**
 * @author WLF
 * @description
 * @date 2025/7/29 23:41
 */
@Data
public class AlgorithmResultDTO {
    private String algorithmType;
    private String algorithmCode;
    private String longitude;
    private String latitude;
    private String alarmPictureUrl;
}
