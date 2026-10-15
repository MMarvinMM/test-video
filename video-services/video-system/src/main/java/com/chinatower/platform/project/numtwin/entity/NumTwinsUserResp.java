package com.chinatower.platform.project.numtwin.entity;

import lombok.Data;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2025/9/12 16:50
 */

@Data
public class NumTwinsUserResp {

    private List<NumTwinsUser> list;

    private long total;
}
