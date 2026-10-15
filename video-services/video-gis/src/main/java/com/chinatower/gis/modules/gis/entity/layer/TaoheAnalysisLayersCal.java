package com.chinatower.gis.modules.gis.entity.layer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.chinatower.gis.api.entity.layer.LayerConstants.*;

@Slf4j
public class TaoheAnalysisLayersCal {
    public List<List<XztbCal>> xztbCals = new ArrayList<>();
    public List<List<XztbCal>> jsydgzqCals = new ArrayList<>();
    public List<List<YjjbntCal>> yjjbntCals = new ArrayList<>();
    public List<List<YjjbntCal>> stbhhxCals = new ArrayList<>();
    public List<List<YjjbntCal>> tdbpCals = new ArrayList<>();


    public List<List<XztbCal>> xztbCals_2 = new ArrayList<>();
    public List<List<XztbCal>> jsydgzqCals_2 = new ArrayList<>();
    public List<List<XztbCal>> yjjbntCals_2 = new ArrayList<>();
    public List<List<XztbCal>> stbhhxCals_2 = new ArrayList<>();
    public List<List<XztbCal>> tdbpCals_2 = new ArrayList<>();

    public ArrayList<Future<ArrayList<XztbCal>>> xztbFuture = new ArrayList<>();
    public ArrayList<Future<ArrayList<XztbCal>>> jsydgzqFuture = new ArrayList<>();
    public ArrayList<Future<ArrayList<YjjbntCal>>> yjjbntFuture = new ArrayList<>();
    public ArrayList<Future<ArrayList<YjjbntCal>>> stbhhxFuture = new ArrayList<>();
    public ArrayList<Future<ArrayList<YjjbntCal>>> tdbpFuture = new ArrayList<>();

    public void calFutures() throws ExecutionException, InterruptedException {
        log.info("执行 calFutures()");

        for (Future<ArrayList<XztbCal>> item : this.xztbFuture) {
            this.xztbCals.add(item.get());
        }
        for (Future<ArrayList<XztbCal>> item : this.jsydgzqFuture) {
            this.jsydgzqCals.add(item.get());
        }
        for (Future<ArrayList<YjjbntCal>> item : this.yjjbntFuture) {
            this.yjjbntCals.add(item.get());
        }
        for (Future<ArrayList<YjjbntCal>> item : this.stbhhxFuture) {
            this.stbhhxCals.add(item.get());
        }
        for (Future<ArrayList<YjjbntCal>> item : this.tdbpFuture) {
            this.tdbpCals.add(item.get());
        }
        log.info("执行完毕 calFutures()");
    }

    public void insertResult(Future<Tuple2<String, ArrayList>> future) throws ExecutionException, InterruptedException {
        Tuple2<String, ArrayList> tuple = future.get();
        if (CollectionUtils.isEmpty(tuple._2)) {
            return;
        }
        switch (tuple._1) {
            case CONST_XZTB:    // 土地利用现状 XZTB - 图斑面积	TBMJ
                this.xztbCals.add(tuple._2);
                break;
            case CONST_JSYDGZQ:    // 土地利用总体规划（建设用地管制区）JSYDGZQ -管制区面积 GZQMJ
                this.jsydgzqCals.add(tuple._2);
                break;
            case CONST_YJJBNT:      // 永久基本农田 YJJBNT -永久基本农田图斑面积 YJJBNTTBMJ
                this.yjjbntCals.add(tuple._2);
                break;
            case CONST_STBHHX:      // 生态保护红线 stbhhx- 面积 mj
                this.stbhhxCals.add(tuple._2);
                break;
            case CONST_TDBP:      // 土地报批 tdbp- 用地面积 YDMJ ydmj
                this.tdbpCals.add(tuple._2);
                break;
            default:
                break;
        }
    }


    public void insertResult_2(Future<Tuple2<String, ArrayList>> future) throws ExecutionException, InterruptedException {
        Tuple2<String, ArrayList> tuple = future.get();
        if (CollectionUtils.isEmpty(tuple._2)) {
            return;
        }
        switch (tuple._1) {
            case CONST_XZTB:    // 土地利用现状 XZTB - 图斑面积	TBMJ
                this.xztbCals_2.add(tuple._2);
                break;
            case CONST_JSYDGZQ:    // 土地利用总体规划（建设用地管制区）JSYDGZQ -管制区面积 GZQMJ
                this.jsydgzqCals_2.add(tuple._2);
                break;
            case CONST_YJJBNT:      // 永久基本农田 YJJBNT -永久基本农田图斑面积 YJJBNTTBMJ
                this.yjjbntCals_2.add(tuple._2);
                break;
            case CONST_STBHHX:      // 生态保护红线 stbhhx- 面积 mj
                this.stbhhxCals_2.add(tuple._2);
                break;
            case CONST_TDBP:      // 土地报批 tdbp- 用地面积 YDMJ ydmj
                this.tdbpCals_2.add(tuple._2);
                break;
            default:
                break;
        }
    }

    public TableRowCountList calTableRowCounts() {
        Map<String, Integer> map = new HashMap();
        countXztbByName(map, this.xztbCals);
        countXztbByName(map, this.jsydgzqCals);
        countYjjbntByName(map, this.yjjbntCals);
        countYjjbntByName(map, this.stbhhxCals);
        countYjjbntByName(map, this.tdbpCals);

        List<TableRowCount> tableRowCounts = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            TableRowCount tableRowCount = new TableRowCount();
            tableRowCounts.add(tableRowCount);
            tableRowCount.setTableName(entry.getKey());
            tableRowCount.setRowCount(entry.getValue());
        }

        TableRowCountList rowCountList = new TableRowCountList();
        rowCountList.setRows(tableRowCounts);

        return rowCountList;
    }


    public TableRowCountList calTableRowCounts_2() {
        Map<String, Integer> map = new HashMap();
        countXztbByName(map, this.xztbCals_2);
        countXztbByName(map, this.jsydgzqCals_2);
        countXztbByName(map, this.yjjbntCals_2);
        countXztbByName(map, this.stbhhxCals_2);
        countXztbByName(map, this.tdbpCals_2);

        List<TableRowCount> tableRowCounts = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            TableRowCount tableRowCount = new TableRowCount();
            tableRowCounts.add(tableRowCount);
            tableRowCount.setTableName(entry.getKey());
            tableRowCount.setRowCount(entry.getValue());
        }

        TableRowCountList rowCountList = new TableRowCountList();
        rowCountList.setRows(tableRowCounts);

        return rowCountList;
    }

    private void countYjjbntByName(Map<String, Integer> map, List<List<YjjbntCal>> yjjbntCals) {
        Map<String, Map<String, YjjbntCal>> tablebsms = new HashMap<>();
        for (List<YjjbntCal> xztbCalLst : yjjbntCals) {
            for (YjjbntCal cal : xztbCalLst) {
                if (!tablebsms.containsKey(cal.getTbName())) {
                    tablebsms.put(cal.getTbName(), new HashMap<>());
                }
                Map<String, YjjbntCal> bsms = tablebsms.get(cal.getTbName());
                if (!bsms.containsKey(cal.getBsm())) {
                    bsms.put(cal.getBsm(), cal);
                }
            }
        }

        for (Map.Entry<String, Map<String, YjjbntCal>> entry : tablebsms.entrySet()) {
            map.put(entry.getKey(), entry.getValue().size());
        }
    }

    private void countXztbByName(Map<String, Integer> map, List<List<XztbCal>> yjjbntCals) {
        Map<String, Map<String, XztbCal>> tablebsms = new HashMap<>();
        for (List<XztbCal> xztbCalLst : yjjbntCals) {
            for (XztbCal cal : xztbCalLst) {
                if (!tablebsms.containsKey(cal.getTbName())) {
                    tablebsms.put(cal.getTbName(), new HashMap<>());
                }
                Map<String, XztbCal> bsms = tablebsms.get(cal.getTbName());
                if (!bsms.containsKey(cal.getBsm())) {
                    bsms.put(cal.getBsm(), cal);
                }
            }
        }

        for (Map.Entry<String, Map<String, XztbCal>> entry : tablebsms.entrySet()) {
            map.put(entry.getKey(), entry.getValue().size());
        }
    }
}

