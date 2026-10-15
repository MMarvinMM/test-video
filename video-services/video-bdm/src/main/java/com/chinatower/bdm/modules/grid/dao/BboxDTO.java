package com.chinatower.bdm.modules.grid.dao;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class BboxDTO {
    private double minx;
    private double maxx;
    private double miny;
    private double maxy;

    private String shpAnalysis;
    private String wgs84Root;
    private String filePath;
    private List<Map<String, Object>> list;
}
