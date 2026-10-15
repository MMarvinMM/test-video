package com.chinatower.platform.project.custom.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
public class PushOrderVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String eventId;
    private List<PushDevVO> data;
}
