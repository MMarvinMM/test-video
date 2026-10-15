package com.chinatower.gis.modules.gis.entity.layer;

import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.gis.api.entity.layer.TaoheAnalysisSerial;
import com.chinatower.gis.api.entity.layer.TaoheAnalysisSerialItem;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.*;

import static com.chinatower.gis.utils.LayerTaoheUtil.*;
import static com.chinatower.gis.utils.LayerTaoheUtil.saveDouble4Pot;

/**
 * 套合分析的图层的配置信息
 *
 * @desription: 套合分析的图层的配置信息
 *
 */
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnalysisLayerInfo {

        //new AnalysisLayerInfo(2, 1, "tkq", "xmmc")
    public AnalysisLayerInfo(int analysisType, int index, String layerTypeName, String serialColName, boolean usedSerial){
        this.analysisType = analysisType;
        this.index = index;
        this.layerTypeSimple = layerTypeSimple;
        this.layerTypeName = layerTypeName;
        this.usedSerialName = usedSerialName;
        this.otherSerialName = otherSerialName;
        this.areaColName = areaColName;
        this.serialColName = serialColName;
        this.spotColName = spotColName;

        this.usedSerial = StringUtils.isEmpty(usedSerialName);
        if(this.usedSerial) {
            this.usedSerialCals = new ArrayList<>();
        } else {
            this.noUsedSerialCals = new ArrayList<>();
        }

        this.spots = new ArrayList<>();
    }

    public AnalysisLayerInfo(int analysisType, int index, String layerTypeSimple, String layerTypeName, String usedSerialName, String otherSerialName, String areaColName, String serialColName, String spotColName){
        this.analysisType = analysisType;
        this.index = index;
        this.layerTypeSimple = layerTypeSimple;
        this.layerTypeName = layerTypeName;
        this.usedSerialName = usedSerialName;
        this.otherSerialName = otherSerialName;
        this.areaColName = areaColName;
        this.serialColName = serialColName;
        this.spotColName = spotColName;

        this.usedSerial = StringUtils.isEmpty(usedSerialName);
        if(this.usedSerial) {
            this.usedSerialCals = new ArrayList<>();
        } else {
            this.noUsedSerialCals = new ArrayList<>();
        }

        this.spots = new ArrayList<>();
    }

    /**
     * 是否使用分组字段
     */
    @Getter
    private boolean usedSerial;

    /**
     * 不使用分组字段时的图斑项集合
     */
    @Getter
    private List<List<XztbCal>> usedSerialCals;

    /**
     * 使用分组字段时的图斑项集合
     */
    @Getter
    private List<List<YjjbntCal>> noUsedSerialCals;  //noUsedSerialCals

    /**
     * 套合明细的各个图斑项集合
     */
    @Getter
    private List<List<XztbCal>> spots;

    /**
     * 序号，用于排序
     * */
    private int index;


    /**
     * 分析类型，默认值为 1，代表“国土”，2代表矿业
     * */
    private int analysisType = 1;

    /**
     * 表名
     * */
    private String tableName;

    /**
     * 相交的图斑数量
     * */
    private int intersectsCount = 0;

    /**
     * 类型简拼
     * */
    private String layerTypeSimple;
    /**
     * 类型中文名称
     * */
    private String layerTypeName;

    /**
     * 有相交部分的 label 名称，用于饼图图例显示
     * */
    private String usedSerialName;

    /**
     * 没有相交部分的 label 名称，用于饼图图例显示
     * */
    private String otherSerialName;

    /**
     * 业务面积字段
     * */
    private String areaColName;

    /**
     * 地块所属分类的字段
     * */
    private String serialColName;

    /**
     * 图斑名称的字段
     * */
    private String spotColName;


    /**
     * 处理每一类的相交图斑面积、比例的汇总
     * */
    public TaoheAnalysisSerial createSerial(double overArea) throws ErrorLayerSpotException {
        if(this.usedSerial) {
            return createXZTBSerial(overArea);
        } else {
            return createYJJBNTSerial(overArea);
        }
    }

    /**
     * 处理带有分组字段类型的相交图斑面积、比例的汇总
     * */
    public TaoheAnalysisSerial createXZTBSerial(double overArea) throws ErrorLayerSpotException {
        TaoheAnalysisSerial taoheAnalysisSerial = new TaoheAnalysisSerial();

        List<TaoheAnalysisSerialItem> serialItems = new ArrayList<>();
        taoheAnalysisSerial.setSerialDatas(serialItems);
        taoheAnalysisSerial.setName(this.layerTypeSimple);
        taoheAnalysisSerial.setType(this.layerTypeName);
        Map<String, Double> mapObj = new HashMap();
        // 分组统计每一类的面积数据
        for (List<XztbCal> arr : this.usedSerialCals) {
            for (XztbCal xztbCal : arr) {
                String dlmc = xztbCal.getSerial();
                if (!mapObj.containsKey(dlmc))
                    mapObj.put(dlmc, 0.0);

                double area = xztbCal.getCalArea();
                double val = mapObj.get(dlmc);
                mapObj.put(dlmc, val + area);
            }
        }

        /**
         * 对 map 对象的值进行倒排序，如果 map 对象值的和 大于 overArea，
         * 则对数据进行手动矫正一次，即用最大的值减去 总值溢出的差值
         * 如果一次矫正以后得到的和仍然大于 overArea，则不再矫正，而报异常错误
         * */
        List<Map.Entry<String, Double>> kvs = calKvsBySort(mapObj, overArea, this.usedSerialCals);

        // 计算各类的总和，以及占比
        double overArea0 = roundDouble2Pot(overArea);
        double sumValue = 0.0;
        for (Map.Entry<String, Double> entry : kvs) {
            String dlmc = entry.getKey();
            double area = roundDouble2Pot(entry.getValue());
            double radia = roundDouble4Pot(area / overArea0);

            sumValue += area;

            TaoheAnalysisSerialItem item = new TaoheAnalysisSerialItem();
            serialItems.add(item);
            item.setName(dlmc);
            item.setValue(saveDouble2Pot(area));
            item.setRadio(saveDouble4Pot(radia));
        }

        createXZTBSerialForOtherCase(serialItems, sumValue, overArea0);

        return taoheAnalysisSerial;
    }

    /**
     * 对 map 对象的值进行倒排序，如果 map 对象值的和 大于 overArea，
     * 则对数据进行手动矫正一次，即用最大的值减去 总值溢出的差值
     * 如果一次矫正以后得到的和仍然大于 overArea，则不再矫正，而报异常错误
     * */
    private List<Map.Entry<String, Double>> calKvsBySort(Map<String, Double> mapObj, double overArea, List<List<XztbCal>> arrs) throws ErrorLayerSpotException {

        List<Map.Entry<String, Double>> entries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : mapObj.entrySet()) {
            entries.add(entry);
        }

        // 使用匿名比较器排序
        Collections.sort(entries, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> p1, Map.Entry<String, Double> p2) {
                return (int) ((p1.getValue() - p2.getValue()) * 100000.0);
            }
        });
        double sumArea = sumArea(entries, entries.size());

        // 如果计算总面积大于输入面积，相关图斑存在互相压盖的情况，需要处理一下
        // 处理方法，最好的图斑面积由减法得到
        if(sumArea > overArea) {
            Map.Entry<String, Double> entry = entries.get(entries.size()-1);
            double area = entry.getValue() - (sumArea - overArea);
            if(area>0) {
                entry.setValue(area);
            } else {
                String spots = getSpotNames(arrs);
                StringBuilder sb = new StringBuilder();
                sb.append("当前图层图斑间叠加过多。");
                sb.append("    图层名：" + tableName);
                sb.append("    bms ：" + spots);
                String text = sb.toString();
                throw new ErrorLayerSpotException(text);
            }
        }

        return entries;
    }

    private String getSpotNames(List<List<XztbCal>> arrs) {
        List<String> strs = new ArrayList<String>();
        for (List<XztbCal> arr : arrs) {
            for (XztbCal xztbCal : arr) {
                String bsm = xztbCal.getBsm();
                strs.add(bsm);
            }
        }

        String bsms = String.join(", ",strs);
        return bsms;
    }

    private double sumArea(List<Map.Entry<String, Double>> entries, int count) {
        double sum = 0.0;
        for(int i=0; i<entries.size(); i++) {
            if(i >= count)
                break;
            Map.Entry<String, Double> entry = entries.get(i);
            sum += entry.getValue();
        }
        return sum;
    }


    public TaoheAnalysisSerial createYJJBNTSerial(double overArea) {
        double overArea0 = roundDouble2Pot(overArea);
        double sumValue = 0.0;
        for (List<YjjbntCal> arr : this.noUsedSerialCals) {
            for (YjjbntCal xztbCal : arr) {
                double area = roundDouble2Pot(xztbCal.getCalArea());
                sumValue += area;
            }
        }

        TaoheAnalysisSerial taoheAnalysisSerial = new TaoheAnalysisSerial();

        List<TaoheAnalysisSerialItem> serialItems = new ArrayList<>();
        taoheAnalysisSerial.setSerialDatas(serialItems);
        taoheAnalysisSerial.setName(this.layerTypeSimple);
        taoheAnalysisSerial.setType(this.layerTypeName);

        double sumRadio = 0.0;
        if(sumValue > overArea) {
            sumValue = overArea;
        }
        TaoheAnalysisSerialItem item;
        item = new TaoheAnalysisSerialItem();
        serialItems.add(item);
        item.setName(this.usedSerialName);
        item.setValue(saveDouble2Pot(sumValue));
        sumRadio = roundDouble4Pot(sumValue / overArea0);
        item.setRadio(saveDouble4Pot(sumRadio));

        createXZTBSerialForOtherCase(serialItems, sumValue, overArea0);

        return taoheAnalysisSerial;
    }

    /**
     * 处理类型的相交图斑面积、比例的汇总以后的“未占用、未覆盖”的面积和比例
     * 如果得到的面积、比例大于 overArea，需要手动修正
     * */
    private void createXZTBSerialForOtherCase(List<TaoheAnalysisSerialItem> serialItems, double sumValue, double overArea) {
        TaoheAnalysisSerialItem item = new TaoheAnalysisSerialItem();
        serialItems.add(item);
        double deltaValue = overArea - sumValue;
        if(deltaValue > 0.0 ) {
            item.setName(this.otherSerialName);
            item.setValue(saveDouble2Pot(deltaValue));
            item.setRadio(saveDouble4Pot(1.0 - sumValue/overArea));
        } else if(deltaValue < 0.0 ){
            item.setName(this.otherSerialName);
            item.setValue(getZerpString());
            item.setRadio(getZerpString());
        } else {
            item.setName(this.otherSerialName);
            item.setValue(getZerpString());
            item.setRadio(getZerpString());
        }
    }
}
