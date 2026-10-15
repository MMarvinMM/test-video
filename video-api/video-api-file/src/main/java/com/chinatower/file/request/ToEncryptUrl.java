package com.chinatower.file.request;

import lombok.Data;

@Data
public class ToEncryptUrl {
    private String originalUrl;
    private String resultUrl;
}
