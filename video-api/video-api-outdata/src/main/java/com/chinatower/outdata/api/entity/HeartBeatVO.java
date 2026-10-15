package com.chinatower.outdata.api.entity;

import lombok.Data;

import java.util.List;

@Data
public class HeartBeatVO {
    private List<String> deviceCodeList;
}
