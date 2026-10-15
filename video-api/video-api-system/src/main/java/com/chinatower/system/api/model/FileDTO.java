package com.chinatower.system.api.model;

import lombok.Data;

@Data
public class FileDTO {
    private String fileName;
    private String fileUrl;
    private String originUrl;
    private String platformCode;
}
