package com.chinatower.video.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoDownloadDTO implements Serializable {

    private String deviceCode;

    private String channelCode;

    /**
     * 批次id
     */
    private String batchId;

    private String returnCode;

    private String returnMsg;

    private List<DownloadProcessDTO> downloadProcessDTO;
}
