package com.chinatower.gis.modules.gis.service;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.dto.bdm.LayerQueryDTO;
import com.chinatower.common.core.utils.IdUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.gis.api.entity.layer.*;
import com.chinatower.gis.modules.gis.entity.GeoExtent;
import com.chinatower.gis.modules.gis.entity.layer.*;
import com.chinatower.gis.modules.gis.mapper.LayerTaoheMapper;
import com.chinatower.gis.modules.gis.mapper.PgLayerCommonMapper;
import com.chinatower.platform.annotation.DataPermissions;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.*;

import static com.chinatower.gis.api.entity.layer.LayerConstants.*;
import static com.chinatower.gis.utils.LayerTaoheUtil.saveDouble2Pot;
import static com.chinatower.gis.utils.LayerTaoheUtil.saveDouble4Pot;

/**
 * @ClassName GisLayerTaoheService
 * @Description GisLayerTaoheService服务类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GisLayerTaoheService {

    /**
     * 套合分析相关数据库查询接口
     */
    @Autowired
    private LayerTaoheMapper layerTaoheMapper;

    @Autowired
    private GisLayerTaoheInsertTmpTable gisLayerTaoheInsertTmpTable;

    @Autowired
    private final PgLayerCommonMapper pgLayerCommonMapper;

    @Autowired
    private GisInOutBalance gisInOutBalance;

    /**
     * 最大相交数量，大于等于该值，查询的时候使用 union all sql；否则使用简单slq
     */
    public static int globalMaxIntersectsCount = 8000;

    /**
     * 分组查询时，每组的数量
     */
    public static int globalPerThreadCount = 4000;

    /**
     * 临时表的最大保存时间
     */
    public static int globalMaxSaveDay = 3;

    public static void taoheAnalysisSetOptions(TaoheAnalysisSetOptionsReq taoheAnalysisSetOptionsReq) {
        if(taoheAnalysisSetOptionsReq.getMaxIntersectsCount() > 0)
            globalMaxIntersectsCount = taoheAnalysisSetOptionsReq.getMaxIntersectsCount();
        if(taoheAnalysisSetOptionsReq.getPerThreadCount() > 0)
            globalPerThreadCount = taoheAnalysisSetOptionsReq.getPerThreadCount();
        if(taoheAnalysisSetOptionsReq.getMaxSaveDay() > 0)
            globalMaxSaveDay = taoheAnalysisSetOptionsReq.getMaxSaveDay();
    }

    /**
     * 获取默认套盒图层
     *
     * @param taoheAnalysisAllReq 入参
     * @return
     */
    public TaoheAnalysisDefaultLayersRes taoheAnalysisDefaultLayers(TaoheAnalysisAllReq taoheAnalysisAllReq) throws Exception{
        StopWatch st = new StopWatch("taoheAnalysisAll");
        st.start("获取命中图层，统计图层的相交数量");

        AnalysisLayerInfos analysisLayerInfos = new AnalysisLayerInfos();
        int analysisType = taoheAnalysisAllReq.getAnalysisType();
        analysisLayerInfos.calUsedLayers(analysisType);

        String tenantId = null;
        String industryCode = null;

        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser != null) {
            SysUser user = loginUser.getUser();
            tenantId = user.getTenantId();
            industryCode = user.getIndustryCode();
        } else {
            throw new NoAreaColNameException(CONST_ERROR_INFO_NOT_SUPPORTED_TENANTID);
//            tenantId = taoheAnalysisAllReq.getTenantId();
//            industryCode = taoheAnalysisAllReq.getIndustryCode();
        }

        LayerQueryDTO dto = new LayerQueryDTO();
        //如果是客户则只能查询自己的
        if (VideoSystemEnums.UserType.TENANT_USER.getIndex().equals(loginUser.getUser().getUserType())) {
            dto.setTenantId(tenantId);
            dto.setIndustryCode(industryCode);
        }

        String typeSimplesString = analysisLayerInfos.getTypeSimplesString();

        ArrayList<TaoheLayerInfo> taoheAnalysisLayers = layerTaoheMapper.selectTaoheAnalysisLayers(dto, typeSimplesString);

        TaoheAnalysisDefaultLayersRes taoheAnalysisRes = new TaoheAnalysisDefaultLayersRes();
        ArrayList<TaoheAnalysisLayerItem> layerItems = new ArrayList<>();
        taoheAnalysisRes.setLayerItems(layerItems);

        for(TaoheLayerInfo taoheLayerInfo: taoheAnalysisLayers) {
            TaoheAnalysisLayerItem item = new TaoheAnalysisLayerItem();
            layerItems.add(item);
            item.setLayerID(taoheLayerInfo.getLayerId());
            item.setAbbreviation(taoheLayerInfo.getLayerTypeSimple());
        }

        st.stop();
        System.out.println(st.prettyPrint());

        return taoheAnalysisRes;    // 参数化实现,替换线程池工具,使用线程池工具，默认方法
    }

    /**
     * 套合结果，分析项使用配置获取
     *
     * @param taoheAnalysisAllReq 入参
     * @return
     */
    public TaoheAnalysisRes taoheAnalysisAll(TaoheAnalysisAllReq taoheAnalysisAllReq) throws Exception{
        return this.taoheAnalysisAll_0(taoheAnalysisAllReq);    // 参数化实现,替换线程池工具,使用线程池工具，默认方法
    }

    /**
     * 相交分析，输入图形和半径，图层ID，计算并返回相交面积，和该图层面积
     *
     * @param intersectAnalysisReq 相交分析入参对象
     * @return 套合分析结果对象
     */
    public IntersectAnalysisRes intersectAnalysis(IntersectAnalysisReq intersectAnalysisReq) throws NoAreaColNameException {

        if(intersectAnalysisReq.getInputGeoItems() == null)
            throw new NoAreaColNameException("入参缺少 inputGeoItems 参数！");
        if(intersectAnalysisReq.getInputGeoItems().size() == 0)
            throw new NoAreaColNameException("入参 inputGeoItems 参数中没有信息！");
        if(intersectAnalysisReq.getLayerItems() == null)
            throw new NoAreaColNameException("入参缺少 layerItems 参数！");
        if(intersectAnalysisReq.getLayerItems().size() == 0)
            throw new NoAreaColNameException("入参 layerItems 参数中没有信息！");

        StopWatch st = new StopWatch("intersectAnalysis");

        st.start("获取图层信息");
        ArrayList<TaoheLayerInfo> taoheAnalysisLayers = getIntersectAnalysisLayers(intersectAnalysisReq);
        st.stop();

        st.start("逐个分析图层");

        IntersectAnalysisRes intersectAnalysisRes = new IntersectAnalysisRes();
        ArrayList<IntersectAnalysisItem> analysisItems = new ArrayList<>();
        intersectAnalysisRes.setItems(analysisItems);
        for(TaoheLayerInfo layerInfo: taoheAnalysisLayers) {
            intersectAnalysisForLayer(intersectAnalysisReq, analysisItems, layerInfo);
        }
        st.stop();

        System.out.println(st.prettyPrint());

        return intersectAnalysisRes;
    }

    private void intersectAnalysisForLayer(IntersectAnalysisReq intersectAnalysisReq, ArrayList<IntersectAnalysisItem> analysisItems, TaoheLayerInfo layerInfo) {
        StopWatch st = new StopWatch("intersectAnalysisForLayer");

        st.start("组装SQL的geoSql部分");
        String geoSql = getGeoSql(intersectAnalysisReq.getInputGeoItems());
        st.stop();

        st.start("执行SQL查询相交情况");
        String layerName = layerInfo.getLayerName();
        String areaField = layerInfo.getAreaField();
        IntersectAnalysisItem analysisItem = layerTaoheMapper.selectIntersectAnalysis(layerName, areaField, geoSql);
        st.stop();


        st.start("计算图层的各个结果值");
        double dIntersectArea = Double.parseDouble(analysisItem.getIntersectArea());
        double dInputArea = Double.parseDouble(analysisItem.getInputArea());
        String strLayerArea = layerInfo.getLayerArea();
        double dLayerArea = Double.parseDouble(strLayerArea);

        analysisItem.setLayerId(layerInfo.getLayerId());
        analysisItem.setLayerArea(saveDouble2Pot(dLayerArea));
        analysisItem.setIntersectArea(saveDouble2Pot(dIntersectArea));
        analysisItem.setInputArea(saveDouble2Pot(dInputArea));
        analysisItem.setRadio(saveDouble4Pot(dIntersectArea/dLayerArea));
        analysisItems.add(analysisItem);
        st.stop();

        System.out.println(st.prettyPrint());
    }

    private ArrayList<TaoheLayerInfo> getIntersectAnalysisLayers(IntersectAnalysisReq intersectAnalysisReq) throws NoAreaColNameException {

        /*
	AND (
		lyrtp.abbreviation = 'jsydgzq' AND lyr.id = 'ce6804b1d9284eb88741586ed644fdaa'
		OR lyrtp.abbreviation = 'stbhhx' AND lyr.id = '31918a79bcf2489695c3dbf16c417034'
		OR lyrtp.abbreviation = 'tdbp' AND lyr.id = 'bc36bcb7b3724048973aa5b0b9aa129a'
		OR lyrtp.abbreviation = 'xztb' AND lyr.id = '8df3e84eb0db4c779e4408448014bdbf'
		OR lyrtp.abbreviation = 'yjjbnt' AND lyr.id = '69e513a2deba4cb0a91e3f755b09056f'
	)
        * */

        List<TaoheAnalysisLayerItem> taoheLayerInfos = intersectAnalysisReq.getLayerItems();
        if (taoheLayerInfos == null || taoheLayerInfos.size() == 0) {
            return null;
        }
        String slqExt = getSqlExt(taoheLayerInfos);

        String tenantId = null;
        String industryCode = null;

        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser != null) {
            SysUser user = loginUser.getUser();
            tenantId = user.getTenantId();
            industryCode = user.getIndustryCode();
        } else {
            throw new NoAreaColNameException(CONST_ERROR_INFO_NOT_SUPPORTED_TENANTID);
//            tenantId = taoheAnalysisAllReq.getTenantId();
//            industryCode = taoheAnalysisAllReq.getIndustryCode();
        }

        LayerQueryDTO dto = new LayerQueryDTO();
        //如果是客户则只能查询自己的
        if (VideoSystemEnums.UserType.TENANT_USER.getIndex().equals(loginUser.getUser().getUserType())) {
            dto.setTenantId(tenantId);
            dto.setIndustryCode(industryCode);
        }

        return layerTaoheMapper.selectIntersectAnalysisLayersExt(dto, slqExt);
    }

    private String getGeoSql(List<InputGeoItem> inputGeoItems) {
        if(inputGeoItems == null || inputGeoItems.size() == 0)
            return null;

        int size = inputGeoItems.size();

        String geoSql = "";
        if (StringUtils.isNotEmpty(inputGeoItems.get(0).getLayerID()) && StringUtils.isNotEmpty(inputGeoItems.get(0).getBsm())) {

        }

        for (int i = 0; i < size; i++) {
            InputGeoItem geoItem = inputGeoItems.get(i);
            if(i == 0) {
                geoSql = getSringBuilderPartByGeoItem(geoItem);
            } else {
                geoSql = getSringBuilderPartByGeoItemEx(geoSql, geoItem);
            }
        }

        return geoSql;
    }

    private static String getSringBuilderPartByGeoItemEx(String geoSql, InputGeoItem geoItem) {
        StringBuilder sb = new StringBuilder();
        sb.append("ST_Union ( ");
        sb.append(geoSql);
        sb.append(",");
        sb.append(getSringBuilderPartByGeoItem(geoItem));
        sb.append(" )");
        geoSql = sb.toString();
        return geoSql;
    }

    private static String getSringBuilderPartByGeoItem(InputGeoItem item) {
        StringBuilder sb = new StringBuilder();
        boolean isZero = isZero(item.getDistance());
        if(!isZero) {
            sb.append("ST_Buffer ( ");
        }
        sb.append("ST_GeomFromText('");
        sb.append(item.getWkt());
        if(!isZero) {
            sb.append("', 4326 ) :: geography, ");
            sb.append(item.getDistance());
            sb.append(" ) :: geometry");
        } else {
            sb.append("', 4326 )");
        }
        return sb.toString();
    }

    /**
     * 套合结果，分析项使用配置获取，不写死
     *
     * @param taoheAnalysisAllReq 套盒分析入参对象
     * @return 套合分析结果对象
     */
    public TaoheAnalysisRes taoheAnalysisAll_0(TaoheAnalysisAllReq taoheAnalysisAllReq) throws Exception {
        StopWatch st = new StopWatch("taoheAnalysisAll");

        /**
         * 查询结果GUID
         * 1、默认为null，
         * 2、当为null时，进行一个新的套合分析。
         * 3、如果不为null，择进行已有guid的继续分析，如果guid不为空且不存在，择报错
         * */
        String guid = taoheAnalysisAllReq.getGuid();
        SerialName serialName = new SerialName();
        if(guid != null) {
            boolean isDelete = layerTaoheMapper.selectIsDeleteByGuid(guid);
            if(isDelete) {
                throw new NoAreaColNameException("入参guid[" + guid + "]系统中已经被删除，请重新请求，并且入参中不带guid！");
            }
            serialName.newTableName = layerTaoheMapper.selectTableNameByGuid(guid);
            if(serialName.newTableName == null) {
                throw new NoAreaColNameException("入参guid[" + guid + "]系统中不存在！");
            }
        }

        st.start("计算套合分析需要的图层");
        AnalysisLayerInfos analysisLayerInfos = new AnalysisLayerInfos();
        int analysisType = taoheAnalysisAllReq.getAnalysisType();
        analysisLayerInfos.calUsedLayers(analysisType);
        st.stop();

        ArrayList<TaoheLayerInfo> taoheAnalysisLayers = null;
        if(guid == null) {
            st.start("获取命中图层, GUID为空");
            taoheAnalysisLayers = getTaoheAnalysisLayers(taoheAnalysisAllReq, analysisLayerInfos);
            st.stop();
        } else {
            st.start("获取命中图层，GUID为 " + guid);
            taoheAnalysisLayers = getTaoheAnalysisCustomLayers(taoheAnalysisAllReq, analysisLayerInfos);
            st.stop();
        }

//        if(taoheAnalysisLayers == null || taoheAnalysisLayers.size() == 0)
//            throw new NoAreaColNameException("查询默认套盒图层返回空！");

        st.start("计算绘制的多边形面积");
        double overArea = calArea(taoheAnalysisAllReq);
        st.stop();

        st.start("统计图层的相交数量");
        checkTableExist(layerTaoheMapper, taoheAnalysisLayers);
        analysisLayerInfos.setTaoheLayerInfos(taoheAnalysisLayers);

        String wkt = taoheAnalysisAllReq.getWkt();
        double distance = taoheAnalysisAllReq.getDistance();
        analysisLayerInfos.calIntersects2(layerTaoheMapper, wkt, distance, globalMaxIntersectsCount);
        if(guid != null) {  // 当 GUID 不为空时，删除掉没有相交图层的类型
            analysisLayerInfos.ClearNoIntersectsNode(taoheAnalysisAllReq);
        }
        st.stop();

        st.start("计算图斑相交面积和比例");
        // 线程池计算每一类的每个图斑相交面积和比例
        analysisLayerInfos.TaoheAnalysisLayersSummary(wkt, distance, overArea, layerTaoheMapper);
        // 汇总每一类
        ArrayList<TaoheAnalysisSerial> taoheAnalysisSerials = analysisLayerInfos.AnalysisSerials(overArea);
        st.stop();

        st.start("整理返回的json结构");
        TaoheAnalysisRes taoheAnalysisRes = new TaoheAnalysisRes();
        if(guid == null) {
            guid = IdUtils.fastSimpleUUID();
        }
        taoheAnalysisRes.setGuid(guid);
        taoheAnalysisRes.setSerials(taoheAnalysisSerials);
        st.stop();

        st.start("gisLayerTaoheInsertTmpTable.asyncTaoheAnalysisLayersInsert");
        String userID = taoheAnalysisAllReq.getUserId();
        gisLayerTaoheInsertTmpTable.asyncTaoheAnalysisLayersInsert(analysisLayerInfos, guid, userID, serialName, wkt, distance, overArea);
        st.stop();

        System.out.println(st.prettyPrint());
        return taoheAnalysisRes;
    }

    /**
    * 兼容用户自定义初始化的套盒图层，且未传所有类型，用默认套合图层替代
    * */
    @DataPermissions(layerColumn = "lyr.id")
    private ArrayList<TaoheLayerInfo> getTaoheAnalysisLayers(TaoheAnalysisAllReq taoheAnalysisAllReq, AnalysisLayerInfos analysisLayerInfos) throws NoAreaColNameException {
//        String tenantId = taoheAnalysisAllReq.getTenantId();
//        String industryCode = taoheAnalysisAllReq.getIndustryCode();

        String tenantId = null;
        String industryCode = null;

        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser != null) {
            SysUser user = loginUser.getUser();
            tenantId = user.getTenantId();
            industryCode = user.getIndustryCode();
        } else {
            throw new NoAreaColNameException(CONST_ERROR_INFO_NOT_SUPPORTED_TENANTID);
//            tenantId = taoheAnalysisAllReq.getTenantId();
//            industryCode = taoheAnalysisAllReq.getIndustryCode();
        }

        LayerQueryDTO dto = new LayerQueryDTO();
        //如果是客户则只能查询自己的
        if (VideoSystemEnums.UserType.TENANT_USER.getIndex().equals(loginUser.getUser().getUserType())) {
            dto.setTenantId(tenantId);
            dto.setIndustryCode(industryCode);
        }

        String typeSimplesString = analysisLayerInfos.getTypeSimplesString();

        ArrayList<TaoheLayerInfo> taoheAnalysisLayers = layerTaoheMapper.selectTaoheAnalysisLayers(dto, typeSimplesString);

        /*
	AND (
		lyrtp.abbreviation = 'jsydgzq' AND lyr.id = 'ce6804b1d9284eb88741586ed644fdaa'
		OR lyrtp.abbreviation = 'stbhhx' AND lyr.id = '31918a79bcf2489695c3dbf16c417034'
		OR lyrtp.abbreviation = 'tdbp' AND lyr.id = 'bc36bcb7b3724048973aa5b0b9aa129a'
		OR lyrtp.abbreviation = 'xztb' AND lyr.id = '8df3e84eb0db4c779e4408448014bdbf'
		OR lyrtp.abbreviation = 'yjjbnt' AND lyr.id = '69e513a2deba4cb0a91e3f755b09056f'
	)
        * */

        List<TaoheAnalysisLayerItem> taoheLayerInfos = taoheAnalysisAllReq.getLayerItems();
        if(taoheLayerInfos != null && taoheLayerInfos.size() > 0) {
            String slqExt = getSqlExt(taoheLayerInfos);
            ArrayList<TaoheLayerInfo> taoheAnalysisLayersCustom = layerTaoheMapper.selectTaoheAnalysisLayersExt(dto, typeSimplesString, slqExt);

            Map<String, ArrayList<TaoheLayerInfo>> mapTaoheLayerInfosCustom = convert2MapTaoheLayerInfos(taoheAnalysisLayersCustom);
            Map<String, ArrayList<TaoheLayerInfo>> mapTaoheLayerInfos = convert2MapTaoheLayerInfos(taoheAnalysisLayers);
            Map<String, ArrayList<TaoheLayerInfo>> mapTaoheLayerInfoOverride = mapTaoheLayerInfosOverride(mapTaoheLayerInfos, mapTaoheLayerInfosCustom);
            taoheAnalysisLayers = convert2ListTaoheLayerInfos(mapTaoheLayerInfoOverride);
        }
        return taoheAnalysisLayers;
    }


    private ArrayList<TaoheLayerInfo> getTaoheAnalysisCustomLayers(TaoheAnalysisAllReq taoheAnalysisAllReq, AnalysisLayerInfos analysisLayerInfos) throws NoAreaColNameException {

        ArrayList<TaoheLayerInfo> taoheAnalysisLayers = null;

        List<TaoheAnalysisLayerItem> taoheLayerInfos = taoheAnalysisAllReq.getLayerItems();
        if(taoheLayerInfos != null && taoheLayerInfos.size() > 0) {
            String typeSimplesString = analysisLayerInfos.getTypeSimplesString();
            String slqExt = getSqlExt(taoheLayerInfos);

            String tenantId = null;
            String industryCode = null;

            LoginUser loginUser = SecurityUtils.getLoginUser();
            if(loginUser != null) {
                SysUser user = loginUser.getUser();
                tenantId = user.getTenantId();
                industryCode = user.getIndustryCode();
            } else {
                throw new NoAreaColNameException(CONST_ERROR_INFO_NOT_SUPPORTED_TENANTID);
//            tenantId = taoheAnalysisAllReq.getTenantId();
//            industryCode = taoheAnalysisAllReq.getIndustryCode();
            }

            LayerQueryDTO dto = new LayerQueryDTO();
            //如果是客户则只能查询自己的
            if (VideoSystemEnums.UserType.TENANT_USER.getIndex().equals(loginUser.getUser().getUserType())) {
                dto.setTenantId(tenantId);
                dto.setIndustryCode(industryCode);
            }

            taoheAnalysisLayers = layerTaoheMapper.selectTaoheAnalysisLayersExt(dto, typeSimplesString, slqExt);
            if(taoheAnalysisLayers.size() != taoheLayerInfos.size())
                throw new NoAreaColNameException("入参图层列表在数据库中查不到！");
        } else {
            taoheAnalysisLayers = getTaoheAnalysisLayers(taoheAnalysisAllReq, analysisLayerInfos);
        }
        return taoheAnalysisLayers;
    }

    private String getSqlExt(List<TaoheAnalysisLayerItem> taoheLayerInfos) {
        StringBuilder sb = new StringBuilder();
        sb.append(" AND ( ");
        for (int i=0; i<taoheLayerInfos.size(); i++){
            if(i > 0) {
                sb.append(" OR ");
            }
            TaoheAnalysisLayerItem info = taoheLayerInfos.get(i);
            sb.append("lyrtp.abbreviation = '" + info.getAbbreviation() + "' AND lyr.id = '" + info.getLayerID() + "'");
        }
        sb.append(" ) ");
        String slqExt = sb.toString();
        return slqExt;
    }

    private Map<String, ArrayList<TaoheLayerInfo>> convert2MapTaoheLayerInfos(ArrayList<TaoheLayerInfo> taoheAnalysisLayers) {
        Map<String, ArrayList<TaoheLayerInfo>> map = new HashMap<>();
        for(TaoheLayerInfo taoheLayerInfo: taoheAnalysisLayers) {
            String type = taoheLayerInfo.getLayerTypeSimple();
            ArrayList<TaoheLayerInfo> infos = map.get(type);
            if(infos == null) {
                infos = new ArrayList<>();
                map.put(type, infos);
            }
            infos.add(taoheLayerInfo);
        }
        return map;
    }

    /*
    * 使用 targets 覆盖 sources相同的值
    * */
    private Map<String, ArrayList<TaoheLayerInfo>> mapTaoheLayerInfosOverride(Map<String, ArrayList<TaoheLayerInfo>> sources, Map<String, ArrayList<TaoheLayerInfo>> targets) {
        targets.forEach((type, list) -> {
            if(sources.containsKey(type)) {
                sources.replace(type, list);
            }
        });
        return sources;
    }

    private ArrayList<TaoheLayerInfo> convert2ListTaoheLayerInfos(Map<String, ArrayList<TaoheLayerInfo>> mapTaoheLayerInfoOverride) {
        ArrayList<TaoheLayerInfo> infos = new ArrayList<>();
        mapTaoheLayerInfoOverride.forEach((type, list) -> infos.addAll(list));
        return infos;
    }


    /**
     * 检查表是否存在
     *
     * @param layerTaoheMapper 套合分析相关数据库查询接口
     * @param taoheAnalysisLayers 套盒图层信息数组
     */
    private void checkTableExist(LayerTaoheMapper layerTaoheMapper, ArrayList<TaoheLayerInfo> taoheAnalysisLayers) throws NoAreaColNameException {
        for(TaoheLayerInfo taoheLayerInfo: taoheAnalysisLayers) {
            String tableName = taoheLayerInfo.getLayerName();
            boolean exist = layerTaoheMapper.selectTestExistTable(tableName);
            if(!exist)
                throw new NoAreaColNameException("图层 " +taoheLayerInfo.getLayerCustomName() + "("+tableName+") 不存在，请检查图层列表！");
        }
    }

    /**
     * 计算输入多边形的面积，如果输入图层id和bsm，则直接取面积数据
     *
     * @param taoheAnalysisAllReq 套盒分析入参对象
     */
    private double calArea(TaoheAnalysisAllReq taoheAnalysisAllReq) throws NoAreaColNameException {
        double overArea = 0.0;
        String lyrid = taoheAnalysisAllReq.getLayerID();
        String bsm = taoheAnalysisAllReq.getBsm();
        // 点击图斑详情的套盒，优先用图斑的真实面积，不做计算
        if (StringUtils.isNotEmpty(lyrid) && StringUtils.isNotEmpty(bsm)) {
            // pg 改 mysql
            LayerTypeAndName layerTypeAndName = layerTaoheMapper.selectLayerTypeAndNameBylayerID2(lyrid);
            if(layerTypeAndName == null) {
                throw new NoAreaColNameException("入参 layerID " + lyrid + " 找不到对应的记录！");
            }
            String areaColName = layerTypeAndName.getAreaColName();
            if(areaColName == null) {
                throw new NoAreaColNameException("类型 " + layerTypeAndName.getLayerTypeName() + " 缺少面积字段配置！");
            }

            String tableName = layerTypeAndName.getLayerName();
            // 是否黑成计算面积，而不是直接使用用户提供的面积，因为后面的面积比例计算都是计算面积
            YjjbntCal rs = layerTaoheMapper.selectAreaByBsm(tableName, areaColName, bsm);
            overArea = rs.getCalArea();
            String wkt = rs.getTbName();
            taoheAnalysisAllReq.setWkt(wkt);
        }

        if(isZero(overArea)) {
            String wkt = taoheAnalysisAllReq.getWkt();
            double distance = taoheAnalysisAllReq.getDistance();
            // 计算输入多边形的面积，可以带缓冲区
            overArea = getAreaByWkt(wkt, distance);
        }

        if(overArea == 0) {
            overArea = 1;
        }
        return overArea;
    }

    private static boolean isZero(double overArea) {
        return Math.abs(overArea - 0.0) < 1e-6;
    }

    /**
     * 套合明细的饼图
     *
     * @param taoheAnalysisLayerStatisticsReq
     * @return
     */
    public ArrayList<TaoheAnalysisSerialItem> taoheAnalysisLayerStatistics(TaoheAnalysisLayerStatisticsReq taoheAnalysisLayerStatisticsReq) throws NoLayerException, NoSpotException {
        String guid = taoheAnalysisLayerStatisticsReq.getGuid();
        String layerId = taoheAnalysisLayerStatisticsReq.getLayerID();
        StopWatch st = new StopWatch("taoheAnalysisLayerStatistics 分析");
        st.start("在 taoheAnalysisLayerStatistics 中执行 getTaoheAnalysisStruct 函数");
        TaoheAnalysisStruct taoheAnalysisStruct = layerTaoheMapper.getTaoheAnalysisStruct(guid, layerId);
        st.stop();
        if(taoheAnalysisStruct == null) {
            String lyrName = layerTaoheMapper.getLayerName(layerId);
            throw new NoLayerException("图层 " + lyrName + " " + CONST_ERROR_INFO_NOTBOXANALYSIS + "。");
        }

        String tmpName = taoheAnalysisStruct.getTmpName();
        double overArea = taoheAnalysisStruct.getOverArea();
        String sourcTableName = taoheAnalysisStruct.getSourceTableName();

        st.start("查询套合详情指定图层的分页结果");
        ArrayList<TaoheAnalysisSerialItem> serialItems = layerTaoheMapper.selectTaoheAnalysisSerialItems(tmpName, sourcTableName);
        st.stop();
        if(serialItems.size() == 0) {
            String lyrName = layerTaoheMapper.getLayerName(layerId);
            throw new NoSpotException("图层 " + lyrName + " 在这次套合统计分页查询中，没有可用的图斑。");
        }

        st.start("统计套合详情指定图层的各项");
        double sum = 0.0;
        double sumRadio = 0.0;
        for (TaoheAnalysisSerialItem item : serialItems) {
            sum += Double.parseDouble(item.getValue());
            sumRadio += Double.parseDouble(item.getRadio());
        }

        TaoheAnalysisSerialItem item = new TaoheAnalysisSerialItem();
        serialItems.add(item);
        item.setValue(saveDouble2Pot(overArea - sum));
        item.setRadio(saveDouble4Pot(1.0 - sumRadio));
        item.setName(CONST_WEIDIEJIA);
        st.stop();

        System.out.println(st.prettyPrint());
        return serialItems;
    }

    /**
     * 套合明细的结果列表
     *
     * @param taoheAnalysisLayerTableReq
     * @return
     */
    public GeojsonStruckRes taoheAnalysisLayerTable(TaoheAnalysisLayerTableReq taoheAnalysisLayerTableReq) throws NoLayerException, NoSpotException {
        GeojsonStruckRes geojsonStruckRes = null;
        if (taoheAnalysisLayerTableReq.getCount() == 0) {
            geojsonStruckRes = taoheAnalysisLayerTableAllcount(taoheAnalysisLayerTableReq);
        } else {
            geojsonStruckRes = taoheAnalysisLayerTableByPage(taoheAnalysisLayerTableReq);
        }
        return geojsonStruckRes;
    }

    /**
     * 套合明细的分页结果列表
     *
     * @param taoheAnalysisLayerTableReq
     * @return
     */
    private GeojsonStruckRes taoheAnalysisLayerTableByPage(TaoheAnalysisLayerTableReq taoheAnalysisLayerTableReq) throws NoLayerException, NoSpotException {
        StopWatch st = new StopWatch("taoheAnalysisLayerTableByPage");

        String guid = taoheAnalysisLayerTableReq.getGuid();
        String layerId = taoheAnalysisLayerTableReq.getLayerID();

        st.start("在 taoheAnalysisLayerTableByPage 中执行 getTaoheAnalysisStruct 函数");
        TaoheAnalysisStruct taoheAnalysisStruct = layerTaoheMapper.getTaoheAnalysisStruct(guid, layerId);
        st.stop();
        if(taoheAnalysisStruct == null) {
            String lyrName = layerTaoheMapper.getLayerName(layerId);
            throw new NoLayerException("图层 " + lyrName + " " + CONST_ERROR_INFO_NOTBOXANALYSIS + "。");
        }

        String sourcTableName = taoheAnalysisStruct.getSourceTableName();

        int firstIndex = taoheAnalysisLayerTableReq.getFirstIndex();
        int count = taoheAnalysisLayerTableReq.getCount();

        int offset = (firstIndex - 1) * count;
        String tmpName = taoheAnalysisStruct.getTmpName();

        st.start("selectTaoheAnalysisLayerTableBsm");
        ArrayList<String> bsms = layerTaoheMapper.selectTaoheAnalysisLayerTableBsm(count, offset, tmpName, sourcTableName);
        st.stop();
        if(bsms.size() == 0) {
            String lyrName = layerTaoheMapper.getLayerName(layerId);
            throw new NoSpotException("图层 " + lyrName + " 在这次套合分析详情列表分页请求中，没有可返回的图斑，请检查分页参数。");
        }

        st.start("sql查询分页的geojson");
        String cols = layerTaoheMapper.selectTableColsByTableName(sourcTableName);
        String sqlRest = "";

        for (int i = 0; i < bsms.size(); i++) {
            String bsm = bsms.get(i);
            if (i == 0) {
                sqlRest = "select " + cols + " from " + sourcTableName + " where bsm = '" + bsm + "'";
            } else {
                sqlRest = sqlRest + "\n" +
                        "UNION ALL\n" +
                        "select " + cols + " from " + sourcTableName + " where bsm = '" + bsm + "'";
            }
        }

        st.stop();
        String jsonStr = layerTaoheMapper.selectTaoheAnalysisLayerTableObj(cols, sqlRest);
        st.start("序列化json字符串");
        Object jsonObj = JSON.parseObject(jsonStr);
        st.stop();

        System.out.println(st.prettyPrint());
        GeojsonStruckRes geojsonStruckRes = new GeojsonStruckRes();
        geojsonStruckRes.setAllCount(bsms.size());
        geojsonStruckRes.setGeoData(jsonObj);
        return geojsonStruckRes;
    }

    /**
     * 套合明细的分页结果总数
     *
     * @param taoheAnalysisLayerTableReq
     * @return
     */
    private GeojsonStruckRes taoheAnalysisLayerTableAllcount(TaoheAnalysisLayerTableReq taoheAnalysisLayerTableReq) throws NoLayerException {
        StopWatch st = new StopWatch("taoheAnalysisLayerTableAllcount");

        String guid = taoheAnalysisLayerTableReq.getGuid();
        String layerId = taoheAnalysisLayerTableReq.getLayerID();

        st.start("在 taoheAnalysisLayerTableAllcount 中执行 getTaoheAnalysisStruct 函数");
        TaoheAnalysisStruct taoheAnalysisStruct = layerTaoheMapper.getTaoheAnalysisStruct(guid, layerId);
        st.stop();
        if(taoheAnalysisStruct == null) {
            String lyrName = layerTaoheMapper.getLayerName(layerId);
            throw new NoLayerException("图层 " + lyrName + " " + CONST_ERROR_INFO_NOTBOXANALYSIS + "。");
        }

        String sourcTableName = taoheAnalysisStruct.getSourceTableName();

        st.start("查询分析的时间汇总");
        String allCountStr = layerTaoheMapper.selectTaoheAnalysisLayerTableAllCountStr(guid);
        st.stop();

        st.start("反序列化json对象");
        TableRowCountList lstObj = JSON.parseObject(allCountStr, TableRowCountList.class);
        st.stop();

        st.start("查找该类型的总数");
        int allCount = lstObj.CountByTableName(sourcTableName);
        st.stop();

        System.out.println(st.prettyPrint());
        GeojsonStruckRes geojsonStruckRes = new GeojsonStruckRes();
        geojsonStruckRes.setAllCount(allCount);
        return geojsonStruckRes;
    }

    public Result gisCalculateAea(GisCalculateAeaReq gisCalculateAeaReq) {
        String wkt = gisCalculateAeaReq.getWkt();
        double distance = gisCalculateAeaReq.getDistance();
        int inProject = gisCalculateAeaReq.getInProject();

        double dArea = getAreaByWkt(wkt, distance, inProject);
        String strArea = saveDouble2Pot(dArea);
        return Result.data(strArea);
    }

    public  double getAreaByWkt(String wkt, double distance, int inProject) {
        double dArea = layerTaoheMapper.selectCalculateAea(wkt, distance, inProject);
        return dArea;
    }

    public  double getAreaByWkt(String wkt, double distance) {
        double dArea = layerTaoheMapper.selectCalculateAea(wkt, distance, 4326);
        return dArea;
    }

    public Map<String, String> searchTdbpState(SearchTdbpStateReq searchTdbpStateReq) throws NoSpotException, NoAreaColNameException {
        StopWatch st = new StopWatch("查询土地报批状态");

        st.start("获取命中图层，统计图层的相交数量_searchTdbpState");
//        String tenantId = searchTdbpStateReq.getTenantId();
//        String industryCode = searchTdbpStateReq.getIndustryCode();

        String tenantId = null;
        String industryCode = null;

        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser != null) {
            SysUser user = loginUser.getUser();
            tenantId = user.getTenantId();
            industryCode = user.getIndustryCode();
        } else {
            throw new NoAreaColNameException(CONST_ERROR_INFO_NOT_SUPPORTED_TENANTID);
//            tenantId = taoheAnalysisAllReq.getTenantId();
//            industryCode = taoheAnalysisAllReq.getIndustryCode();
        }

        LayerQueryDTO dto = new LayerQueryDTO();
        //如果是客户则只能查询自己的
        if (VideoSystemEnums.UserType.TENANT_USER.getIndex().equals(loginUser.getUser().getUserType())) {
            dto.setTenantId(tenantId);
            dto.setIndustryCode(industryCode);
        }

        String typeSimplesString = "'tdbp'";
        // 搜索设置“默认套合分析”的土地报批图层
        ArrayList<TaoheLayerInfo> taoheAnalysisLayers = layerTaoheMapper.selectTaoheAnalysisLayers(dto, typeSimplesString);
        st.stop();

        if(taoheAnalysisLayers == null) {
            throw new NoSpotException("没有配置土地利用现状的默认套盒图层!");
        }
        if(taoheAnalysisLayers.size() != 1) {
            throw new NoSpotException("配置了 " + taoheAnalysisLayers.size() + " 个土地利用现状的默认套盒图层!");
        }

        String lyrName = taoheAnalysisLayers.get(0).getLayerName();
        List<SearchTdbpStateReqItem> items = searchTdbpStateReq.getItems();
        StringBuilder sb = new StringBuilder();

        int size = items.size();
        // 组装查询语句，搜索“已报批”的入参图斑
        Map<String, String> map = new HashMap<>();
        for (int i=0; i<size; i++) {
            SearchTdbpStateReqItem item = items.get(i);
            String id = item.getId();
            String wkt = item.getWkt();
            map.put(id, CONST_WBP);
            if(i == 0) {
                sb.append(String.format("SELECT '%s' id FROM %s t1 WHERE st_intersects(t1.geom, ST_GeomFromText('%s', 4326))", id, lyrName, wkt));
            } else {
                sb.append(String.format(" union all SELECT '%s' id FROM %s t1 WHERE st_intersects(t1.geom, ST_GeomFromText('%s', 4326))", id, lyrName, wkt));
            }
        }

        String sql = sb.toString();
        ArrayList<String> ids = layerTaoheMapper.selectListString(sql);
        for (String state: ids) {
            map.replace(state, CONST_YBP);
        }

        System.out.println(st.prettyPrint());
        return map;
    }

    public CalInOutBalanceRes calInOutBalance(CalInOutBalanceReq calInOutBalanceReq) throws NoSpotException, NoAreaColNameException {
        // 在进出平衡历史表管理表中搜索guid
        String guid = layerTaoheMapper.selectGuidByTableNames(calInOutBalanceReq.getPreLayerName(), calInOutBalanceReq.getCurLayerName());

        if(guid == null) {
            // 如果没有搜索到，说明还没开始计算，然后进行计算
            guid = reCalInOutBalance(calInOutBalanceReq);
        } else {
            // 1.通过表名查询出结果
            // 2.从结果中判断结果有没有生成
            // 3.如果没有生成，通过当前时间和更新时间判断是否大于1天。如果超过一天则重新计算并更新updatetime

            GetInOutBalanceExtendRes getInOutBalanceExtendRes = layerTaoheMapper.selectUpdateTimeByGuid(guid);
            if(getInOutBalanceExtendRes != null
                    && getInOutBalanceExtendRes.getLc0101().equals("0")
                    && getInOutBalanceExtendRes.getLc0102().equals("0")
                    && getInOutBalanceExtendRes.getLc0103().equals("0")
                    && getInOutBalanceExtendRes.getLr0101().equals("0")
                    && getInOutBalanceExtendRes.getLr0102().equals("0")
                    && getInOutBalanceExtendRes.getLr0103().equals("0")) {
                Date now = new Date();

                Calendar calUpdateTime = Calendar.getInstance();
                calUpdateTime.setTime(getInOutBalanceExtendRes.getUpdateTime());
                Calendar calNow = Calendar.getInstance();
                calNow.setTime(now);

                long difference = calNow.getTimeInMillis() - calUpdateTime.getTimeInMillis();
                if(difference > 24 * 60 * 60 * 1000) {
                    layerTaoheMapper.deleteJcphitemByGuid(guid);
                    guid = reCalInOutBalance(calInOutBalanceReq);
                }
            }
        }

        CalInOutBalanceRes calInOutBalanceRes = new CalInOutBalanceRes();
        calInOutBalanceRes.setGuid(guid);

        return calInOutBalanceRes;
    }

    /**
     * 计算进出平衡
     * @param calInOutBalanceReq 入参
     * @return Result
     * */
    private String reCalInOutBalance(CalInOutBalanceReq calInOutBalanceReq) throws NoSpotException, NoAreaColNameException {
//        String tenantId = calInOutBalanceReq.getTenantId();
//        String industryCode = calInOutBalanceReq.getIndustryCode();

        String tenantId = null;
        String industryCode = null;

        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser != null) {
            SysUser user = loginUser.getUser();
            tenantId = user.getTenantId();
            industryCode = user.getIndustryCode();
        } else {
            throw new NoAreaColNameException(CONST_ERROR_INFO_NOT_SUPPORTED_TENANTID);
//            tenantId = taoheAnalysisAllReq.getTenantId();
//            industryCode = taoheAnalysisAllReq.getIndustryCode();
        }

        LayerQueryDTO dto = new LayerQueryDTO();
        //如果是客户则只能查询自己的
        if (VideoSystemEnums.UserType.TENANT_USER.getIndex().equals(loginUser.getUser().getUserType())) {
            dto.setTenantId(tenantId);
            dto.setIndustryCode(industryCode);
        }

        checkTableNameError(dto, calInOutBalanceReq.getPreLayerName());
        checkTableNameError(dto, calInOutBalanceReq.getCurLayerName());

        String guid = IdUtils.fastSimpleUUID();
        gisInOutBalance.asyncCalInOutBalance(calInOutBalanceReq, guid);
        return guid;
    }

    /**
     * 检查表是否存在问题，即是否存在该表，是否是“土地利用现状”类型
     * @param dto  租户ID和行业ID
     * @param tableName 表名
     * @return Result
     * */
    private void checkTableNameError(LayerQueryDTO dto, String tableName) throws NoSpotException {
        String abbreviation = layerTaoheMapper.selectAbbreviationByTableName(dto, tableName);
        if(abbreviation == null) {
            throw new NoSpotException("表 " + tableName + " 不存在!");
        } else if (!abbreviation.equals(CONST_XZTB)) {
            throw new NoSpotException("表 " + tableName + " 的类型是 " + abbreviation + ",不是 " + CONST_TDBP + " !");
        }
    }

    public GetInOutBalanceRes getInOutBalance(GetInOutBalanceReq getInOutBalanceReq) throws NoSpotException {
        String guid = getInOutBalanceReq.getGuid();

        int count = layerTaoheMapper.selectInOutBalanceExistByGuid(guid);
        if(count == 0) {
            throw new NoSpotException("获取耕地的进出平衡的GUID " + guid + " 不存在，请检查入参!");
        }
        GetInOutBalanceRes getInOutBalanceRes = layerTaoheMapper.selectGetInOutBalanceRes(guid);
        if(getInOutBalanceRes == null) {
            throw new NoSpotException("获取耕地的进出平衡的计算还没有结束，请稍候再试!");
        }
        return getInOutBalanceRes;
    }

    /**
     * 自动比较图层图斑的不同，用于图层比对编辑
     * 1、比较任意bsm字段内容长度，是否为32、39位字符长度，如果不是，则无需做任何操作
     * 2、如果是32、39位字符长度，则为系统自动填充bsm，需要进行图斑覆盖比例判断
     * 3、当某新一个图斑覆盖原图斑的95%以上时，更新为原图斑bsm字段
     */
    public int autoCompareLayerFeatures(CalInOutBalanceReq calcAutoCompareLayerFeaturesReq) throws NoSpotException {
        int changeCount = 0;
        // 查询当前图层表的 bsm 字段第一行数据的长度  polygon_bch_1715651598506_igkhsz
        int length = layerTaoheMapper.selectLengthByTableBsm(calcAutoCompareLayerFeaturesReq.getCurLayerName());
        double tolerance = 0.05;   // 容差，面积差在该范围内，更新为原图斑bsm字段

//        if (length == 32 || length == 39)
        {
            // 1、如果是32、39位字符长度，则为系统自动填充bsm，需要进行图斑覆盖比例判断
            // 2、当某新一个图斑覆盖原图斑的95%以上时，更新为原图斑bsm字段
            changeCount = layerTaoheMapper.autoCompareLayerFeatures(calcAutoCompareLayerFeaturesReq.getPreLayerName(), calcAutoCompareLayerFeaturesReq.getCurLayerName(), tolerance);
        }
        // 比较任意bsm字段内容长度，是否为32、39位字符长度，如果不是，则无需做任何操作

        return changeCount;
    }

    /*
    * 验证sql，处理前查询一下有多少条记录，处理后再查询一下有多少条（应该是零条）
SELECT
 *
FROM
	(
	SELECT
		g1.bsm g1bsm,
		g2.bsm g2bsm,
		st_area (
			st_transform (
				st_intersection ( g1.geom, g2.geom ),
				CAST ( ( FLOOR ( ( ST_X ( st_centroid ( g1.geom ) ) + 1.5 ) / 3 ) + 4488 ) AS INTEGER )
			)
			) / st_area (
			st_transform (
				g1.geom,
				CAST ( ( FLOOR ( ( ST_X ( st_centroid ( g1.geom ) ) + 1.5 ) / 3 ) + 4488 ) AS INTEGER )
			)
		) changeScale
	FROM
		polygon_tdbp_1747387651567_d8azuw g1,
		polygon_tdbp_1747625694506_j2obzb g2
	WHERE
		st_intersects ( g1.geom, g2.geom )
	) t1
WHERE
	t1.changeScale BETWEEN 0.95 AND 1.05
	AND t1.g1bsm != t1.g2bsm
	*

-- 创建新表并复制所有数据
CREATE TABLE polygon_tdbp_1747625694506_j2obzb_backup AS
SELECT * FROM polygon_tdbp_1747625694506_j2obzb;

-- 清空原表并插入备份数据   执行该语句，可以二次使用测试表
TRUNCATE polygon_tdbp_1747625694506_j2obzb;
INSERT INTO polygon_tdbp_1747625694506_j2obzb
SELECT * FROM polygon_tdbp_1747625694506_j2obzb_backup;

    * */


    public LayerHasCustomBsmFieldRes isLayerHasCustomBsmField(String layerId) {
        String layerName = layerTaoheMapper.getTableNameByLayerId(layerId);
        LayerHasCustomBsmFieldRes res = new LayerHasCustomBsmFieldRes();
        res.setRowCount(layerTaoheMapper.countTableRows(layerName));
        int length = layerTaoheMapper.selectLengthByTableBsm(layerName);
        res.setFieldLength(length);
        res.setCustom(length == 32 || length == 39);
        return res;
    }

    /**
     * 根据用户行业、租户获取所有有相交图斑的图层列表
     */
    public List<TaoheLayerInfo> getIntersectingLayers(GetIntersectingPlotReq getIntersectingPlotReq) throws NoAreaColNameException {
        String tenantId;
        String industryCode;

        LayerQueryDTO dto = new LayerQueryDTO();

        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser != null) {
            SysUser user = loginUser.getUser();
            tenantId = user.getTenantId();
            industryCode = user.getIndustryCode();
        } else {
            throw new NoAreaColNameException(CONST_ERROR_INFO_NOT_SUPPORTED_TENANTID);
        }

        //如果是客户则只能查询自己的
        if (VideoSystemEnums.UserType.TENANT_USER.getIndex().equals(loginUser.getUser().getUserType())) {
            dto.setTenantId(tenantId);
            dto.setIndustryCode(industryCode);
        }

        String wkt = getIntersectingPlotReq.getWkt();
        double distance = getIntersectingPlotReq.getDistance();

        GeoExtent geoExtent = layerTaoheMapper.selectGeoExtent(wkt, distance);

        List<TaoheLayerInfo> lst = new ArrayList<>();
        ArrayList<TaoheLayerInfo>  lyres = layerTaoheMapper.selectIntersectingLayers(dto, geoExtent);

        for(TaoheLayerInfo layerInfo: lyres) {
            String tbName = layerInfo.getLayerName();
            int intersectsCount = layerTaoheMapper.isIntersectingPlot(tbName, wkt, distance);
            if(intersectsCount > 0) {
                lst.add(layerInfo);
            }
        }

        return lst;
    }


    public List<TaoheLayerInfo> selectLayerTbbhs(String layerIds) throws NoAreaColNameException {
        String tenantId;
        String industryCode;

        LayerQueryDTO dto = new LayerQueryDTO();

        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser != null) {
            SysUser user = loginUser.getUser();
            tenantId = user.getTenantId();
            industryCode = user.getIndustryCode();
        } else {
            throw new NoAreaColNameException(CONST_ERROR_INFO_NOT_SUPPORTED_TENANTID);
        }

        //如果是客户则只能查询自己的
        if (VideoSystemEnums.UserType.TENANT_USER.getIndex().equals(loginUser.getUser().getUserType())) {
            dto.setTenantId(tenantId);
            dto.setIndustryCode(industryCode);
        }

        List<TaoheLayerInfo> lyrIds = layerTaoheMapper.selectLayerTbbhs(dto, layerIds);
        return lyrIds;
    }

    private static boolean isBlankIncludingWhitespace(String str) {
        return str == null || str.trim().isEmpty();
    }
    /**
     * 根据用户行业、租户获取所有相交图斑列表
     */
    public List<GeojsonStruckRes> getIntersectingPlot(GisLayerService gisLayerService, GetIntersectingPlotReq getIntersectingPlotReq) throws NoAreaColNameException {

        if(getIntersectingPlotReq.getLayerItems() != null) {
            List<GeojsonStruckRes> rslst = new ArrayList<>();
            String layerIds = getIntersectingPlotReq.getLayerIds();
            if(layerIds != null) {
                List<TaoheLayerInfo> lyrIds = selectLayerTbbhs(layerIds);
                for(TaoheLayerInfo lyrId: lyrIds) {
                    String lyrid = lyrId.getLayerId();
                    String areaField = lyrId.getAreaField();
                    GetFeaturesByWktReq getFeaturesByWktReq = new GetFeaturesByWktReq();

                    if(isBlankIncludingWhitespace(areaField)) areaField = "bsm as tbbh";
                    else areaField = areaField + " as tbbh";

                    getFeaturesByWktReq.setCols("bsm, " + areaField);
                    getFeaturesByWktReq.setLayerID(lyrid);
                    getFeaturesByWktReq.setDistance(getIntersectingPlotReq.getDistance());
                    getFeaturesByWktReq.setWktFilter(getIntersectingPlotReq.getWkt());
                    getFeaturesByWktReq.setReturnGeometry(true);
                    getFeaturesByWktReq.setCount(1000);
                    GeojsonStruckRes returnFeatures = gisLayerService.getFeaturesByWkt2(getFeaturesByWktReq);
                    returnFeatures.setLayerID(lyrid);

                    rslst.add(returnFeatures);
                }
            }
            return rslst;
        } else {
            List<TaoheLayerInfo> lyrs = getIntersectingLayers(getIntersectingPlotReq);
            List<TaoheAnalysisLayerItem> layerItems = new ArrayList<>();
            for(TaoheLayerInfo lyr : lyrs) {
                TaoheAnalysisLayerItem item = new TaoheAnalysisLayerItem();
                item.setLayerID(lyr.getLayerId());
                layerItems.add(item);
            }
            getIntersectingPlotReq.setLayerItems(layerItems);
            List<GeojsonStruckRes> rslst = getIntersectingPlot(gisLayerService, getIntersectingPlotReq);
            return rslst;
        }
    }
}
