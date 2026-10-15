package com.chinatower.video.api.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
public class GroupByNodeVO {
    private List params;
    private String itemExpression;
    private Function<List<String>, Map<String,String>> batchQuery;
}
