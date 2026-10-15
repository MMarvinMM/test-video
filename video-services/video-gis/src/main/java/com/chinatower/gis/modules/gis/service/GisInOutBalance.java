package com.chinatower.gis.modules.gis.service;

import com.chinatower.gis.api.entity.Constants;
import com.chinatower.gis.api.entity.layer.CalInOutBalanceReq;
import com.chinatower.gis.api.entity.layer.GetXlsjFeatureRes;
import com.chinatower.gis.modules.gis.mapper.LayerTaoheMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.List;

import static com.chinatower.gis.utils.LayerTaoheUtil.getNewTmpTbName;

@Service
@Slf4j
public class GisInOutBalance {

    @Autowired
    private LayerTaoheMapper layerTaoheMapper;


    @Async("threadPoolTaskExecutor")
    public void asyncCalInOutBalance(CalInOutBalanceReq calInOutBalanceReq, String guid) {

        System.out.println("启动耕地的进出平衡计算");
        StopWatch st = new StopWatch("启动耕地的进出平衡计算");

        String preG = null;     // 之前的耕地图斑的临时表
        String preF = null;     // 之前的非耕农用地的临时表
        String curG = null;     // 当前的耕地图斑的临时表
        String curF = null;     // 当前的非耕农用地的临时表
        String gdlc = null;     // 耕地流出的临时表
        String gdlr = null;     // 耕地流入的临时表
        try {

            st.start("向管理表中插入记录");
            String tenantId = calInOutBalanceReq.getTenantId();
            String industryCode = calInOutBalanceReq.getIndustryCode();
            String userId = calInOutBalanceReq.getUserId();
            String appVerCode = calInOutBalanceReq.getAppVerCode();
            System.out.println("向管理表中插入记录");
            layerTaoheMapper.createjcphitem(tenantId, industryCode, userId, appVerCode, guid);
            st.stop();

            String preLayerName = calInOutBalanceReq.getPreLayerName();
            String curLayerName = calInOutBalanceReq.getCurLayerName();
            double areaTolerance = 1;               // 面积容差
            String gWhere = "'0101', '0102', '0103'";   // 耕地的地类编码
            // 非耕农用地的地理编码
            String fWhere = "'0201', '0202', '0203', '0204', '0301', '0302', '0303', '0304', '0305', '0306', '0307', '0401', '0402', '0403', '1006', '1103', '1104', '1107', '1202', '1203'";

            preG = createNewTableAndInsert(preLayerName, gWhere, st);
            curG = createNewTableAndInsert(curLayerName, gWhere, st);
            preF = createNewTableAndInsert(preLayerName, fWhere, st);
            curF = createNewTableAndInsert(curLayerName, fWhere, st);

            System.out.println("计算 耕地流出");
            // 使用sql，求 preG 和 curF 的相交图形，计算图形面积，过滤图形面积 大于 1 为有效相交图形
            gdlc = createAndInsertInOutBalanceResult(preG, curF, areaTolerance, st);

            System.out.println("计算 耕地流入");
            // 使用sql，求 preF 和 curG 的相交图形，计算图形面积，过滤图形面积 大于 1 为有效相交图形
            gdlr = createAndInsertInOutBalanceResult(preF, curG, areaTolerance, st);

            st.start("计算进出平衡结果，更新进出平衡管理表");
            // 查询 耕地流出
            List<GetXlsjFeatureRes> gdlcDatas = layerTaoheMapper.selectJcphResult(gdlc, "dlbm1", gWhere);
            // 查询 耕地流入
            List<GetXlsjFeatureRes> gdlrDatas = layerTaoheMapper.selectJcphResult(gdlr, "dlbm2", gWhere);

            double lc0101 = getDlbmValue(gdlcDatas, "0101");
            double lc0102 = getDlbmValue(gdlcDatas, "0102");
            double lc0103 = getDlbmValue(gdlcDatas, "0103");
            double lr0101 = getDlbmValue(gdlrDatas, "0101");
            double lr0102 = getDlbmValue(gdlrDatas, "0102");
            double lr0103 = getDlbmValue(gdlrDatas, "0103");

            layerTaoheMapper.updateJcphmanageInfo(preLayerName, curLayerName, preG, preF, curG, curF, gdlc, gdlr, lc0101, lc0102, lc0103, lr0101, lr0102, lr0103, guid);
            st.stop();
        } catch (Exception e) {
            System.out.println(Constants.CALCULATE_INOUTBALANCE_ERROR);
            log.error(Constants.CALCULATE_INOUTBALANCE_ERROR + e);
        }
        finally {
            st.start("删除6个临时图层");
            deleteTableByName(preG);
            deleteTableByName(preF);
            deleteTableByName(curG);
            deleteTableByName(curF);
            deleteTableByName(gdlc);
            deleteTableByName(gdlr);
            layerTaoheMapper.updateJcphmanageDeleteTable(guid);
            st.stop();
        }

        System.out.println(st.prettyPrint());
        System.out.println("完成耕地的进出平衡计算");
    }

    /**
     * 根据表名删除表，会判断表名是否为空，表是不是存在数据库里
     * @param tableName 表名
     * @return Result
     * */
    private void deleteTableByName(String tableName) {
        if(StringUtils.isNotBlank(tableName))
            layerTaoheMapper.deleteInOutBalanceTable(tableName);
    }

    /**
     * 获取指定 地类编码 的值
     * @param gdlcDatas 集合
     * @param dlbm 地类编码
     * @return Result
     * */
    private double getDlbmValue(List<GetXlsjFeatureRes> gdlcDatas, String dlbm) {
        for (GetXlsjFeatureRes s : gdlcDatas) {
            if(s.getBsm().equals(dlbm))
                return s.getArea();
        }
        return 0.0;
    }

    /**
     * 根据 WHERE 条件，创建临时表，然后插入值
     * @param pre 之前的耕地/非耕农用地表
     * @param cur 当前的非耕农用地/耕地表
     * @param areaTolerance 面积容差
     * @return Result
     * */
    private String createAndInsertInOutBalanceResult(String pre, String cur, double areaTolerance, StopWatch st) {
        String gdlc = getNewTmpTbName("gdlc");
        st.start("创建表：" + gdlc);
        layerTaoheMapper.createInOutBalanceResultTable(gdlc);
        st.stop();

        st.start("插入表：" + gdlc);
        layerTaoheMapper.insertInOutBalanceResultTable(gdlc, pre, cur, areaTolerance);
        st.stop();
        return gdlc;
    }

    /**
     * 根据 WHERE 条件，创建临时表，然后插入值
     * @param tableName 入参
     * @return Result
     * */
    private String createNewTableAndInsert(String tableName, String gWhere, StopWatch st) {
        String g1 = getNewTmpTbName("g1");
        st.start("创建表：" + g1);

        System.out.println("创建表：" + g1);
        layerTaoheMapper.createInOutBalanceSourceTable(g1);
        st.stop();

        st.start("插入表：" + g1);
        layerTaoheMapper.insertInOutBalanceTable(g1, tableName, gWhere);
        st.stop();
        return g1;
    }
}
