package com.chinatower.platform.project.system.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndustryQueryVO implements Serializable {

    private String keyword;

    private String industryCode;

    private String acceptNode;
}
