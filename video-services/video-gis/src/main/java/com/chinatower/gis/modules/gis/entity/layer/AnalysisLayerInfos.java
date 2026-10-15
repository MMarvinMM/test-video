package com.chinatower.gis.modules.gis.entity.layer;

import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.gis.api.entity.layer.TaoheAnalysisAllReq;
import com.chinatower.gis.api.entity.layer.TaoheAnalysisLayerItem;
import com.chinatower.gis.api.entity.layer.TaoheAnalysisSerial;
import com.chinatower.gis.modules.gis.mapper.LayerTaoheMapper;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.chinatower.gis.api.entity.layer.LayerConstants.*;
import static com.chinatower.gis.modules.gis.service.GisLayerTaoheService.globalMaxIntersectsCount;
import static com.chinatower.gis.modules.gis.service.GisLayerTaoheService.globalPerThreadCount;

/**
 * 套合分析的有效图层
 *
 * @desription: 套合分析的有效图层
 *
 */
@Data
@Slf4j
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnalysisLayerInfos extends ArrayList<AnalysisLayerInfo> {

    private ArrayList<TaoheLayerInfo> taoheLayerInfos;

    /**
     * 计算套合分析需要的图层
     *
     * @param analysisType
     * @return
     */
    public void calUsedLayers2(int analysisType) {
        switch(analysisType) {
            case 2:
                this.add(new AnalysisLayerInfo(2, 1, CONST_TKQ, CONST_TKQFX, null, CONST_WZYTKQ, CONST_TXMJ, CONST_XMMC, CONST_YSDM));
                this.add(new AnalysisLayerInfo(2, 2, CONST_CKQ, CONST_CKQFX, null, CONST_WZYCKQ, CONST_TXMJ, CONST_KSMC, CONST_YSDM));
                this.add(new AnalysisLayerInfo(2, 3, CONST_CKZDKCQ, CONST_CKZDKCQFX, CONST_ZYCKZDKCQ, CONST_WZYCKZDKCQ, CONST_TXMJ, null, CONST_YSDM));
                this.add(new AnalysisLayerInfo(2, 4, CONST_GJGHKQ, CONST_KCGJGHKQFX, CONST_ZYKCGJGHKQ, CONST_WZYKCGJGHKQ, CONST_TXMJ, null, CONST_YSDM));
                this.add(new AnalysisLayerInfo(2, 5, CONST_KCZDKCQ, CONST_KCZDKCQFX, CONST_ZYKCZDKCQ, CONST_WZYKCZDKCQ, CONST_TXMJ, null, CONST_YSDM));
                break;
            case 1:
            default:
                AnalysisLayerInfo anl = new AnalysisLayerInfo(1, 1, CONST_YJJBNT, "基本农田10", CONST_ZYJBNT, CONST_WZYJBNT, CONST_YJJBNTTBMJ, null, CONST_TBBH);
                anl.setIntersectsCount(10);
                anl.setTableName("polygon_yjjbnt_1716260133637_ebn9xb");
                this.add(anl);
                anl = new AnalysisLayerInfo(1, 2, CONST_YJJBNT, "基本农田100", CONST_ZYJBNT, CONST_WZYJBNT, CONST_YJJBNTTBMJ, null, CONST_TBBH);
                anl.setIntersectsCount(100);
                anl.setTableName("polygon_yjjbnt_1716260160347_6yg7r3");
                this.add(anl);
                anl = new AnalysisLayerInfo(1, 3, CONST_YJJBNT, "基本农田1000", CONST_ZYJBNT, CONST_WZYJBNT, CONST_YJJBNTTBMJ, null, CONST_TBBH);
                anl.setIntersectsCount(1000);
                anl.setTableName("polygon_yjjbnt_1716260183785_ribmcg");
                this.add(anl);
                anl = new AnalysisLayerInfo(1, 4, CONST_YJJBNT, "基本农田10000", CONST_ZYJBNT, CONST_WZYJBNT, CONST_YJJBNTTBMJ, null, CONST_TBBH);
                anl.setIntersectsCount(10000);
                anl.setTableName("polygon_yjjbnt_1716260211204_xupzlb");
                this.add(anl);
                anl = new AnalysisLayerInfo(1, 5, CONST_YJJBNT, "基本农田50729", CONST_ZYJBNT, CONST_WZYJBNT, CONST_YJJBNTTBMJ, null, CONST_TBBH);
                anl.setIntersectsCount(50729);
                anl.setTableName("polygon_yjjbnt_1716171287546_wmr9hh");
                this.add(anl);
                break;
        }
    }

    /**
     * 计算使用的图层信息设置
     *
     * @param analysisType 分析类型，默认1，基本农田；2，矿业
     * @return
     */
    public void calUsedLayers(int analysisType) {
        int analysisTypeStr = analysisType;
        switch(analysisTypeStr) {
            case 2:
                this.add(new AnalysisLayerInfo(2, 1, CONST_TKQ, CONST_TKQFX, null, CONST_WZYTKQ, CONST_TXMJ, CONST_XMMC, CONST_YSDM));
                this.add(new AnalysisLayerInfo(2, 2, CONST_CKQ, CONST_CKQFX, null, CONST_WZYCKQ, CONST_TXMJ, CONST_KSMC, CONST_YSDM));
                this.add(new AnalysisLayerInfo(2, 3, CONST_CKZDKCQ, CONST_CKZDKCQFX, CONST_ZYCKZDKCQ, CONST_WZYCKZDKCQ, CONST_TXMJ, null, CONST_YSDM));
                this.add(new AnalysisLayerInfo(2, 4, CONST_GJGHKQ, CONST_KCGJGHKQFX, CONST_ZYKCGJGHKQ, CONST_WZYKCGJGHKQ, CONST_TXMJ, null, CONST_YSDM));
                this.add(new AnalysisLayerInfo(2, 5, CONST_KCZDKCQ, CONST_KCZDKCQFX, CONST_ZYKCZDKCQ, CONST_WZYKCZDKCQ, CONST_TXMJ, null, CONST_YSDM));
                break;
            case 1:
            default:
                this.add(new AnalysisLayerInfo(1, 1, CONST_XZTB, "土地利用现状分析", null, CONST_ZANWU, CONST_TBMJ, CONST_DLMC, CONST_TBBH));
                this.add(new AnalysisLayerInfo(1, 2, CONST_JSYDGZQ, "土地利用总体规划分析", null, CONST_ZANWU, CONST_GZQMJ, CONST_GZQLXDM, CONST_BSM));
                this.add(new AnalysisLayerInfo(1, 3, CONST_YJJBNT, "基本农田分析", CONST_ZYJBNT, CONST_WZYJBNT, CONST_YJJBNTTBMJ, null, CONST_TBBH));
                this.add(new AnalysisLayerInfo(1, 4, CONST_STBHHX, "生态保护红线分析", CONST_ZYSTBHHX, CONST_WZYSTBHHX, CONST_MJ, null, CONST_HXBM));
                this.add(new AnalysisLayerInfo(1, 5, CONST_TDBP, "土地报批分析", CONST_YBP, CONST_WBP, CONST_YDMJ, null, CONST_DKID));
                break;
        }
    }

    /**
     * 计算每一类的相交图层，带验证类型设置数量
     *
     * @param layerTaoheMapper  pg库查询入口
     * @param wkt   几何图形
     * @param distance  缓冲区半径
     * @param maxIntersectsCount    最大相交数量
     *
     * @return
     */
    public void calIntersects2(LayerTaoheMapper layerTaoheMapper, String wkt, double distance, int maxIntersectsCount) throws NoAreaColNameException {
        for(AnalysisLayerInfo analysisLayerInfo: this) {
            String typeSimplesString = analysisLayerInfo.getLayerTypeSimple();
            List<TaoheLayerInfo> taoheLayerInfos = getTaoheLayerInfoBySimplesString(typeSimplesString);
            if(taoheLayerInfos.size() > 1) {
                throw new NoAreaColNameException("类型 " + analysisLayerInfo.getLayerTypeName() + "(" + typeSimplesString + ") 设置了 "+ taoheLayerInfos.size() +" 条记录，请设置不多于1个！");
            }
            for(TaoheLayerInfo taoheLayerInfo: taoheLayerInfos) {
                String tbName = taoheLayerInfo.getLayerName();
                int intersectsCount = layerTaoheMapper.selectCalIntersectsCount(tbName, wkt, distance, maxIntersectsCount);
                if(intersectsCount > 0) {
                    analysisLayerInfo.setIntersectsCount(intersectsCount);
                    analysisLayerInfo.setTableName(tbName);
                    break;
                }
            }
        }
    }

    /*
     * 删除掉没有相交图层的类型
     * */
    public void ClearNoIntersectsNode(TaoheAnalysisAllReq taoheAnalysisAllReq) {
        List<TaoheAnalysisLayerItem> layerItems = taoheAnalysisAllReq.getLayerItems();
        if(layerItems == null || layerItems.size() == 0)
            return;

        List<String> abbreviations = new ArrayList<>();
        for (TaoheAnalysisLayerItem layerItem: layerItems) {
            abbreviations.add(layerItem.getAbbreviation());
        }

        for (int i = this.size()-1; i>=0; i--) {
            AnalysisLayerInfo info = this.get(i);
            if(abbreviations.contains(info.getLayerTypeSimple()))
                continue;
            String tableName = info.getTableName();
            if(tableName == null)
                this.remove(i);
        }
    }

    /**
     * 计算每一类的相交图层
     *
     * @param layerTaoheMapper  pg库查询入口
     * @param wkt   几何图形
     * @param distance  缓冲区半径
     * @param maxIntersectsCount    最大相交数量
     *
     * @return
     */
    public void calIntersects(LayerTaoheMapper layerTaoheMapper, String wkt, double distance, int maxIntersectsCount) {
        for(AnalysisLayerInfo analysisLayerInfo: this) {
            String typeSimplesString = analysisLayerInfo.getLayerTypeSimple();
            List<TaoheLayerInfo> taoheLayerInfos = getTaoheLayerInfoBySimplesString(typeSimplesString);
            for(TaoheLayerInfo taoheLayerInfo: taoheLayerInfos) {
                String tbName = taoheLayerInfo.getLayerName();
                int intersectsCount = layerTaoheMapper.selectCalIntersectsCount(tbName, wkt, distance, maxIntersectsCount);
                if(intersectsCount > 0) {
                    analysisLayerInfo.setIntersectsCount(intersectsCount);
                    analysisLayerInfo.setTableName(tbName);
                    break;
                }
            }
        }
    }

    /**
     * 计算简拼字符串的组合，用逗号分隔
     *
     * @return  简拼字符串的组合
     */
    public String getTypeSimplesString() {
        StringBuilder sb = new StringBuilder();

        int size = this.size();
        for (int i=0; i<size; i++) {
            AnalysisLayerInfo layerInfo = this.get(i);
            String typeSimplesString = layerInfo.getLayerTypeSimple();
            if(i == 0) {
                sb.append(String.format("'%s'", typeSimplesString));
            } else {
                sb.append(String.format(" ,'%s'", typeSimplesString));
            }
        }

        String rs = sb.toString();
        return rs;
    }

    /**
     * 使用类型简称，收集所有该类型简称的 TaoheLayerInfo
     *
     * @return  所有的所有该类型简称的 TaoheLayerInfo 数组
     */
    private List<TaoheLayerInfo> getTaoheLayerInfoBySimplesString(String typeSimplesString) {
        List<TaoheLayerInfo> infos = new ArrayList<>();
        for(TaoheLayerInfo item: taoheLayerInfos) {
            if(typeSimplesString.equals(item.getLayerTypeSimple())) {
                infos.add(item);
            }
        }
        return infos;
    }

    /**
     * 套盒概要分析
     *
     * @param wkt   几何图形
     * @param distance  缓冲区半径
     * @param overArea    输入多边形的面积，单位平方米
     * @param layerTaoheMapper  pg库查询入口
     *
     * @return
     */
    public void TaoheAnalysisLayersSummary(String wkt, double distance, double overArea, LayerTaoheMapper layerTaoheMapper) {
        StopWatch st = new StopWatch("TaoheAnalysisLayersSummary 函数");

        st.start("整理套盒分析计算前的参数");
        List<TaoheAnalysisParam> taoheAnalysisParams = new ArrayList<>();
        int size = this.size();
        for (int i=0; i<size; i++) {
            AnalysisLayerInfo analysisLayerInfo = this.get(i);
            int intersectsCount =  analysisLayerInfo.getIntersectsCount();
            String areaColName = analysisLayerInfo.getAreaColName();
            String serialColName = analysisLayerInfo.getSerialColName();
            if(analysisLayerInfo.isUsedSerial()) {
                threadCalXZTBSummary(taoheAnalysisParams, analysisLayerInfo, intersectsCount, wkt, distance, overArea, areaColName, serialColName, layerTaoheMapper);
            } else {
                threadCalYJJBNTSummary(taoheAnalysisParams, analysisLayerInfo, intersectsCount, wkt, distance, overArea, areaColName, layerTaoheMapper);
            }
        }
        st.stop();

        st.start("线程池进行套盒分析");
        Function<TaoheAnalysisParam, Integer> fun1 = this::threadCalselectPolygon;
        List<Integer> list = ThreadParallelUtil.run(taoheAnalysisParams, taoheAnalysisParam -> {
            return fun1.apply(taoheAnalysisParam);
        }, ThreadParallelUtil.ConfigFactory.TAOHE_ANALYSIS);
        st.stop();

        System.out.println(st.prettyPrint());
    }

    /**
     * 自定义套盒分析参数结构
     */
    class TaoheAnalysisParam {

        /**
         * 自定义套盒分析参数结构的构造函数
         *
         * @param layerTaoheMapper   pg库查询入口
         * @param analysisLayerInfo  关联的 AnalysisLayerInfo 对象
         * @param usedSerial  是否使用分组字段
         * @param usedPage  是否分页
         * @param tbName  空间表名
         * @param wkt   几何图形
         * @param distance  缓冲区半径
         * @param areaColName    面积字段名
         * @param serialColName    分组字段名
         * @param overArea    输入多边形的面积，单位平方米
         * @param offset  查询结果的偏移位置
         * @param limit  查询结果的记录条数
         */
        public TaoheAnalysisParam(LayerTaoheMapper layerTaoheMapper, AnalysisLayerInfo analysisLayerInfo, boolean usedSerial, boolean usedPage,
                                  String tbName, String wkt, double distance, String areaColName,
                                  String serialColName, double overArea, int offset, int limit) {
            this.layerTaoheMapper = layerTaoheMapper;
            this.analysisLayerInfo = analysisLayerInfo;
            this.usedSerial = usedSerial;
            this.usedPage = usedPage;
            this.tbName = tbName;
            this.wkt = wkt;
            this.distance = distance;
            this.areaColName = areaColName;
            this.serialColName = serialColName;
            this.overArea = overArea;
            this.offset = offset;
            this.limit = limit;
        }

        /**
         * pg库查询入口
         */
        public LayerTaoheMapper layerTaoheMapper;

        /**
         * 关联的 AnalysisLayerInfo 对象
         */
        public AnalysisLayerInfo analysisLayerInfo;

        /**
         * 是否使用分组字段
         */
        public boolean usedSerial;

        /**
         * 是否分页
         */
        public boolean usedPage;

        /**
         * 空间表名
         */
        public String tbName;

        /**
         * 几何图形
         */
        public String wkt;

        /**
         * 缓冲区半径
         */
        public double distance;

        /**
         * 面积字段名
         */
        public  String areaColName;

        /**
         * 分组字段名
         */
        public String serialColName;

        /**
         * 输入多边形的面积，单位平方米
         */
        public double overArea;

        /**
         * 查询结果的偏移位置
         */
        public int offset;

        /**
         * 查询结果的记录条数
         */
        public int limit;
    }

    /**
     * 统计 allcount 数值，即每个表查询相交图斑的数量
     */
    public TableRowCountList calTableRowCounts() {
        Map<String, Integer> map = new HashMap();
        for(AnalysisLayerInfo analysisLayerInfo: this) {
            countXztbByName(map, analysisLayerInfo.getSpots());
        }

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

    /**
     * 根据表名，统计该表名包含的相交图斑数量
     * @param map   kv数组，用了统计表和记录数
     * @param yjjbntCals   相交图斑的二维数组
     */
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

    /**
     * 套盒明细分析
     * @param serialName  分类表名的对象
     * @param wkt   几何图形
     * @param distance   缓冲区半径
     * @param overArea   输入多边形的面积，单位平方米
     * @param layerTaoheMapper   pg库查询入口
     */
    public void TaoheAnalysisLayersDetail(SerialName serialName, String wkt, double distance, double overArea, LayerTaoheMapper layerTaoheMapper) {
        StopWatch st = new StopWatch("TaoheAnalysisLayersDetail 函数");

        List<String> lstLayerNames = null;
        if(serialName.newTableName != null) {
            lstLayerNames = layerTaoheMapper.getLayerNamesByGuid(serialName.newTableName);
        }

        st.start("整理套盒明细计算前的参数");
        List<TaoheAnalysisParam> taoheAnalysisParams = new ArrayList<>();
        int size = this.size();
        for (int i=0; i<size; i++) {
            AnalysisLayerInfo analysisLayerInfo = this.get(i);
            String tbName = analysisLayerInfo.getTableName();
            if(lstLayerNames !=null && lstLayerNames.contains(tbName))  // 如果已经分析过这个表里，这次分析就跳过
                continue;

            int intersectsCount =  analysisLayerInfo.getIntersectsCount();
            String areaColName = analysisLayerInfo.getAreaColName();
            String spotColName = analysisLayerInfo.getSpotColName();
            threadCalXZTBDetail(taoheAnalysisParams, analysisLayerInfo, intersectsCount, wkt, distance, overArea, areaColName, spotColName, layerTaoheMapper);
        }
        st.stop();

        st.start("线程池进行套盒分析");
        Function<TaoheAnalysisParam, Integer> fun1 = this::threadCalDetailSelectPolygon;
        // 多线程计算套盒明细分析
        List<Integer> list = ThreadParallelUtil.run(taoheAnalysisParams, taoheAnalysisParam -> {
            return fun1.apply(taoheAnalysisParam);
        }, ThreadParallelUtil.ConfigFactory.TAOHE_ANALYSIS);
        st.stop();

        System.out.println(st.prettyPrint());
    }

    /**
     * 计算套盒明细
     * @param taoheAnalysisParam   自定义套盒分析参数对象
     * @return  返回影响的记录数
     */
    private Integer threadCalDetailSelectPolygon(TaoheAnalysisParam taoheAnalysisParam) {
        if(taoheAnalysisParam.usedPage) {
            ArrayList<XztbCal> xztbCals = taoheAnalysisParam.layerTaoheMapper.selectXZTBTmpValue(taoheAnalysisParam.tbName, taoheAnalysisParam.wkt, taoheAnalysisParam.distance, taoheAnalysisParam.areaColName, taoheAnalysisParam.serialColName, taoheAnalysisParam.overArea, taoheAnalysisParam.offset, taoheAnalysisParam.limit);
            for(XztbCal xztbCal: xztbCals) {
                if(xztbCal.getCalArea() > taoheAnalysisParam.overArea) {
                    xztbCal.setCalArea(taoheAnalysisParam.overArea);
                }
                BigDecimal ratio2 = new BigDecimal(xztbCal.getCalArea()).divide(new BigDecimal(taoheAnalysisParam.overArea), 10, RoundingMode.HALF_UP);
                double ratio = ratio2.doubleValue();
                if(ratio2.compareTo(CONST_MIN_PLOTRATIO) >= 0) { // 如果是相交关系，还需要进一步判断输入多边形是不是被包含
                    ratio = 1.0;
                    xztbCal.setCalArea(taoheAnalysisParam.overArea);
                }
                xztbCal.setCoverRatio(ratio);
            }
            taoheAnalysisParam.analysisLayerInfo.getSpots().add(xztbCals);
        } else {
            ArrayList<XztbCal> xztbCals = taoheAnalysisParam.layerTaoheMapper.selectXZTBTmpValue3(taoheAnalysisParam.tbName, taoheAnalysisParam.wkt, taoheAnalysisParam.distance, taoheAnalysisParam.areaColName, taoheAnalysisParam.serialColName, taoheAnalysisParam.overArea);
            for(XztbCal xztbCal: xztbCals) {
                if(xztbCal.getCalArea() > taoheAnalysisParam.overArea) {
                    xztbCal.setCalArea(taoheAnalysisParam.overArea);
                }
                BigDecimal ratio2 = new BigDecimal(xztbCal.getCalArea()).divide(new BigDecimal(taoheAnalysisParam.overArea), 10, RoundingMode.HALF_UP);
                double ratio = ratio2.doubleValue();
                if(ratio2.compareTo(CONST_MIN_PLOTRATIO) >= 0 && xztbCal.getRelationType() == 2) { // 如果是相交关系，还需要进一步判断输入多边形是不是被包含
                    ArrayList<XztbCal> xztbCals2 = taoheAnalysisParam.layerTaoheMapper.selectXZTBTmpValue4(taoheAnalysisParam.tbName, taoheAnalysisParam.wkt, taoheAnalysisParam.distance, taoheAnalysisParam.areaColName, taoheAnalysisParam.serialColName, taoheAnalysisParam.overArea);
                    if(xztbCals2.size() > 0) {
                        ratio = 1.0;
                        xztbCal.setCalArea(taoheAnalysisParam.overArea);
                    }
                }
                xztbCal.setCoverRatio(ratio);
            }
            taoheAnalysisParam.analysisLayerInfo.getSpots().add(xztbCals);
        }

        return 1;
    }

    /**
     * 计算套盒概要分析
     * @param taoheAnalysisParam   自定义套盒分析参数对象
     * @return  返回影响的记录数
     */
    private Integer threadCalselectPolygon(TaoheAnalysisParam taoheAnalysisParam) {
        if(taoheAnalysisParam.usedSerial) {
            ArrayList<XztbCal> xztbCals;
            if(taoheAnalysisParam.usedPage) {
                xztbCals = taoheAnalysisParam.layerTaoheMapper.selectXZTBTmpValue(taoheAnalysisParam.tbName, taoheAnalysisParam.wkt, taoheAnalysisParam.distance, taoheAnalysisParam.areaColName, taoheAnalysisParam.serialColName, taoheAnalysisParam.overArea, taoheAnalysisParam.offset, taoheAnalysisParam.limit);
            } else {
                xztbCals = taoheAnalysisParam.layerTaoheMapper.selectXZTBTmpValue3(taoheAnalysisParam.tbName, taoheAnalysisParam.wkt, taoheAnalysisParam.distance, taoheAnalysisParam.areaColName, taoheAnalysisParam.serialColName, taoheAnalysisParam.overArea);
            }

            for(XztbCal xztbCal: xztbCals) {
                if(xztbCal.getCalArea() > taoheAnalysisParam.overArea) {
                    xztbCal.setCalArea(taoheAnalysisParam.overArea);
                }
                BigDecimal ratio2 = new BigDecimal(xztbCal.getCalArea()).divide(new BigDecimal(taoheAnalysisParam.overArea), 10, RoundingMode.HALF_UP);
                double ratio = ratio2.doubleValue();
                if(ratio2.compareTo(CONST_MIN_PLOTRATIO) >= 0 && xztbCal.getRelationType() == 2) { // 如果是相交关系，还需要进一步判断输入多边形是不是被包含
                    ArrayList<XztbCal> xztbCals2 = taoheAnalysisParam.layerTaoheMapper.selectXZTBTmpValue4(taoheAnalysisParam.tbName, taoheAnalysisParam.wkt, taoheAnalysisParam.distance, taoheAnalysisParam.areaColName, taoheAnalysisParam.serialColName, taoheAnalysisParam.overArea);
                    if(xztbCals2.size() > 0) {
                        ratio = 1.0;
                        xztbCal.setCalArea(taoheAnalysisParam.overArea);
                    }
                }
                xztbCal.setCoverRatio(ratio);
            }
            taoheAnalysisParam.analysisLayerInfo.getUsedSerialCals().add(xztbCals);
        } else {
            ArrayList<YjjbntCal> yjjbntCals;
            if(taoheAnalysisParam.usedPage) {
                yjjbntCals = taoheAnalysisParam.layerTaoheMapper.selectYJJBNTTmpValue(taoheAnalysisParam.tbName, taoheAnalysisParam.wkt, taoheAnalysisParam.distance, taoheAnalysisParam.areaColName, taoheAnalysisParam.overArea, taoheAnalysisParam.offset, taoheAnalysisParam.limit);
            } else {
                yjjbntCals = taoheAnalysisParam.layerTaoheMapper.selectYJJBNTTmpValue3(taoheAnalysisParam.tbName, taoheAnalysisParam.wkt, taoheAnalysisParam.distance, taoheAnalysisParam.areaColName, taoheAnalysisParam.overArea);
            }
            for(YjjbntCal yjjbntCal: yjjbntCals) {
                if(yjjbntCal.getCalArea() > taoheAnalysisParam.overArea) {
                    yjjbntCal.setCalArea(taoheAnalysisParam.overArea);
                }
                BigDecimal ratio2 = new BigDecimal(yjjbntCal.getCalArea()).divide(new BigDecimal(taoheAnalysisParam.overArea), 10, RoundingMode.HALF_UP);
                double ratio = ratio2.doubleValue();
                if(ratio2.compareTo(CONST_MIN_PLOTRATIO) >= 0 && yjjbntCal.getRelationType() == 2) { // 如果是相交关系，还需要进一步判断输入多边形是不是被包含
                    ArrayList<YjjbntCal> yjjbntCals2 = taoheAnalysisParam.layerTaoheMapper.selectYJJBNTTmpValue4(taoheAnalysisParam.tbName, taoheAnalysisParam.wkt, taoheAnalysisParam.distance, taoheAnalysisParam.areaColName, taoheAnalysisParam.overArea);
                    if(yjjbntCals2.size() > 0) {
                        ratio = 1.0;
                        yjjbntCal.setCalArea(taoheAnalysisParam.overArea);
                    }
                }
                yjjbntCal.setCoverRatio(ratio);
            }
            taoheAnalysisParam.analysisLayerInfo.getNoUsedSerialCals().add(yjjbntCals);
        }

        return 1;
    }

    /**
     * 计算套盒概要分析xztb的参数数组
     * @param taoheAnalysisParams   自定义套盒分析参数对象数组
     * @param analysisLayerInfo   图层的配置信息
     * @param intersectsCount   相交的图斑数量
     * @param wkt   几何图形
     * @param distance   缓冲区半径
     * @param overArea   输入多边形的面积，单位平方米
     * @param areaColName   业务面积字段
     * @param serialColName   地块所属分类的字段
     * @param layerTaoheMapper   pg库查询入口
     * @return  返回影响的记录数
     */
    private int threadCalXZTBSummary(List<TaoheAnalysisParam> taoheAnalysisParams, AnalysisLayerInfo analysisLayerInfo, int intersectsCount, String wkt,
                                     double distance, double overArea, String areaColName, String serialColName, LayerTaoheMapper layerTaoheMapper) {
        if(intersectsCount == 0)
            return 0;

        String tbName = analysisLayerInfo.getTableName();
        String type = analysisLayerInfo.getLayerTypeSimple();
        int maxIntersectsCount = globalMaxIntersectsCount;

        int count = 1;

        // 如果相交的图斑总数小于 maxIntersectsCount，则不适用多线程；否则，拆表使用多线程进行查询
        if(intersectsCount >= maxIntersectsCount) {
            int perThreadCount = globalPerThreadCount;

            int polygonCount = layerTaoheMapper.selectTaoheAnalysisCountByLayerName(tbName);
            count = (int) Math.ceil(polygonCount / (double) perThreadCount);
            log.info(CONST_MULTITHREADING_LOGINFO_DIVIDED_GROUPS, tbName, type, count);
            for (int i = 0; i < count; i++) {
                int offset = i * perThreadCount;
                int limit = perThreadCount;

                taoheAnalysisParams.add(new TaoheAnalysisParam(layerTaoheMapper, analysisLayerInfo, true, true, tbName, wkt, distance, areaColName, serialColName, overArea, offset, limit));
            }
        } else {
            taoheAnalysisParams.add(new TaoheAnalysisParam(layerTaoheMapper, analysisLayerInfo, true, false, tbName, wkt, distance, areaColName, serialColName, overArea, -1, -1));
        }

        return count;
    }

    /**
     * 计算套盒概要分析yjjbnt的参数数组
     * @param taoheAnalysisParams   自定义套盒分析参数对象数组
     * @param analysisLayerInfo   图层的配置信息
     * @param intersectsCount   相交的图斑数量
     * @param wkt   几何图形
     * @param distance   缓冲区半径
     * @param overArea   输入多边形的面积，单位平方米
     * @param areaColName   业务面积字段
     * @param layerTaoheMapper   pg库查询入口
     * @return  返回影响的记录数
     */
    private int threadCalYJJBNTSummary(List<TaoheAnalysisParam> taoheAnalysisParams, AnalysisLayerInfo analysisLayerInfo, int intersectsCount, String wkt,
                                       double distance, double overArea, String areaColName, LayerTaoheMapper layerTaoheMapper) {
        if(intersectsCount == 0)
            return 0;

        String tbName = analysisLayerInfo.getTableName();
        String type = analysisLayerInfo.getLayerTypeSimple();
        int maxIntersectsCount = globalMaxIntersectsCount;
        int count = 1;

        if(intersectsCount >= maxIntersectsCount) {
            int perThreadCount = globalPerThreadCount;

            int polygonCount = layerTaoheMapper.selectTaoheAnalysisCountByLayerName(tbName);
            count = (int) Math.ceil(polygonCount / (double) perThreadCount);
            log.info(CONST_MULTITHREADING_LOGINFO_DIVIDED_GROUPS, tbName, type, count);
            for (int i = 0; i < count; i++) {
                int offset = i * perThreadCount;
                int limit = perThreadCount;

                taoheAnalysisParams.add(new TaoheAnalysisParam(layerTaoheMapper, analysisLayerInfo, false, true, tbName, wkt, distance, areaColName, "", overArea, offset, limit));
            }
        } else {
            taoheAnalysisParams.add(new TaoheAnalysisParam(layerTaoheMapper, analysisLayerInfo, false, false, tbName, wkt, distance, areaColName, "", overArea, -1, -1));
        }

        return count;
    }

    /**
     * 计算套盒明细分析的参数数组
     * @param taoheAnalysisParams   自定义套盒分析参数对象数组
     * @param analysisLayerInfo   图层的配置信息
     * @param intersectsCount   相交的图斑数量
     * @param wkt   几何图形
     * @param distance   缓冲区半径
     * @param overArea   输入多边形的面积，单位平方米
     * @param areaColName   业务面积字段
     * @param serialColName   地块所属分类的字段/图斑名称的字段
     * @param layerTaoheMapper   pg库查询入口
     * @return  返回影响的记录数
     */
    private int threadCalXZTBDetail(List<TaoheAnalysisParam> taoheAnalysisParams, AnalysisLayerInfo analysisLayerInfo, int intersectsCount, String wkt,
                                    double distance, double overArea, String areaColName, String serialColName, LayerTaoheMapper layerTaoheMapper) {

        if(intersectsCount == 0)
            return 0;

        String tbName = analysisLayerInfo.getTableName();
        String type = analysisLayerInfo.getLayerTypeSimple();

        int maxIntersectsCount = globalMaxIntersectsCount;

        int count = 1;

        // 如果相交的图斑总数小于 maxIntersectsCount，则不适用多线程；否则，拆表使用多线程进行查询
        if(intersectsCount >= maxIntersectsCount) {
            int perThreadCount = globalPerThreadCount;

            int polygonCount = layerTaoheMapper.selectTaoheAnalysisCountByLayerName(tbName);
            count = (int) Math.ceil(polygonCount / (double) perThreadCount);
            log.info(CONST_MULTITHREADING_LOGINFO_DIVIDED_GROUPS, tbName, type, count);
            for (int i = 0; i < count; i++) {
                int offset = i * perThreadCount;
                int limit = perThreadCount;

                taoheAnalysisParams.add(new TaoheAnalysisParam(layerTaoheMapper, analysisLayerInfo, true, true, tbName, wkt, distance, areaColName, serialColName, overArea, offset, limit));
            }
        } else {
            taoheAnalysisParams.add(new TaoheAnalysisParam(layerTaoheMapper, analysisLayerInfo, true, false, tbName, wkt, distance, areaColName, serialColName, overArea, -1, -1));
        }

        return count;
    }

    /**
    * 计算图斑相交面积和比例
    * */
    public ArrayList<TaoheAnalysisSerial> AnalysisSerials(double overArea) throws ErrorLayerSpotException {
        ArrayList<TaoheAnalysisSerial> taoheAnalysisSerials = new ArrayList<>();
        for(AnalysisLayerInfo analysisLayerInfo: this) {
            // 处理每一类的相交图斑面积、比例的汇总
            taoheAnalysisSerials.add(analysisLayerInfo.createSerial(overArea));
        }
        return taoheAnalysisSerials;
    }
}
