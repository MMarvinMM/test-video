package com.chinatower.video.modules.query.entity;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
public class HabitVO {

    //习惯key
    private String habitKey;

    //习惯value
    private String habitValue;
    //习惯value集合
    private List<String> habitKeyList;

}
