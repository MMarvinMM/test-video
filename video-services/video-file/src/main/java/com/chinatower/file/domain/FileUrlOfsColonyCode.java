package com.chinatower.file.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUrlOfsColonyCode implements Serializable {

    private String url;

    /**
     * 0-obs 1-ofs 2- ofsV2  3-ofs V2 1.9.21
     */
    private Integer strategy;

    private String colonyCode;
}
