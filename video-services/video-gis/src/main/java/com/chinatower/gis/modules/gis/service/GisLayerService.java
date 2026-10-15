package com.chinatower.gis.modules.gis.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.sql.SqlUtil;
import com.chinatower.gis.api.entity.layer.*;
import com.chinatower.gis.modules.gis.entity.LayerInfo;
import com.chinatower.gis.modules.gis.mapper.GisLayerMapper;
import com.chinatower.gis.modules.gis.mapper.LayerInfoMapper;
import com.chinatower.gis.modules.gis.mapper.PgLayerCommonMapper;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.entity.gis.SysGisFieldsDTO;
import com.chinatower.platform.entity.gis.SysGisFieldsDictDTO;
import com.chinatower.platform.enums.LayerParcelQuerySearchTypeEnums;
import com.chinatower.platform.enums.LayerParcelQueryValueTypeEnums;
import com.chinatower.platform.enums.SpaceQueryTypeEnums;
import com.chinatower.platform.enums.SysLayerTypeFieldsTypeEnums;
import com.chinatower.platform.utils.StreamUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @ClassName GisLayerService
 * @Description GisLayerService服务类
 * @Author D
 * @Date 2022/3/14 15:53
 */
@Service
@Slf4j
@SuppressWarnings("all")
public class GisLayerService {

    @Autowired
    private GisLayerMapper gisLayerMapper;

    @Autowired
    private LayerInfoMapper layerInfoMapper;

    @Resource
    private PgLayerCommonMapper pgLayerCommonMapper;

    /**
     * 根据过滤条件查询 Features
     *
     * @param getFeaturesByFilterAPIReq
     * @return
     */
    public GeojsonStruckRes getFeaturesByFilterAPI(GetFeaturesByFilterAPIReq getFeaturesByFilterAPIReq) {
        LayerInfo layerInfo = layerInfoMapper.selectById(getFeaturesByFilterAPIReq.getLayerID());
        getFeaturesByFilterAPIReq.setLayerName(layerInfo.getLayerName());
        String tableField = gisLayerMapper.tableField(getFeaturesByFilterAPIReq);
        getFeaturesByFilterAPIReq.setCols(tableField);
        int returnFeaturesCount = 0;
        GeojsonStruckRes geojsonStruckRes = new GeojsonStruckRes();
        if (StringUtils.isNotEmpty(getFeaturesByFilterAPIReq.getWktFilter())){
            SqlUtil.filterKeyword(getFeaturesByFilterAPIReq.getWktFilter());
        }
        if (getFeaturesByFilterAPIReq.getCount() == 0) {
            returnFeaturesCount = gisLayerMapper.getFeaturesByFilterAPICount(getFeaturesByFilterAPIReq);
        } else {
            String returnFeatures = "{\"type\":\"FeatureCollection\",\"features\":null}";
            try {
                returnFeatures = gisLayerMapper.getFeaturesByFilterAPI(getFeaturesByFilterAPIReq);
                JSONObject object = JSON.parseObject(returnFeatures);
                JSONArray features = object.getJSONArray("features");
                JSONObject o = features.getJSONObject(0);
                if (o.getString("id") == null){
                    geojsonStruckRes.setGeoData(null);
                    returnFeaturesCount = 0;
                }else {
                    returnFeaturesCount = gisLayerMapper.getFeaturesByFilterAPICount(getFeaturesByFilterAPIReq);
                    geojsonStruckRes.setGeoData(JSON.parse(returnFeatures));
                }

            } catch (Exception e) {
                log.error("地块高级查询错误", e);
            }
        }
        geojsonStruckRes.setGeoType(layerInfo.getGeoType());
        geojsonStruckRes.setAllCount(returnFeaturesCount);
        return geojsonStruckRes;
    }

    /**
     * 根据空间范围过滤条件查询 Features
     *
     * @param getFeaturesByWktReq
     * @return
     */
    public GeojsonStruckRes getFeaturesByWkt(GetFeaturesByWktReq getFeaturesByWktReq) {
        String geowhere;
        if (getFeaturesByWktReq.getDistance() > 0) {
            String geoTemplate = "right join (select ST_Buffer(ST_GeomFromText('%s',4326)::geography, %f)::geometry) cb2 on ST_intersects(cb.geom, cb2.ST_Buffer)";
            geowhere = String.format(geoTemplate, getFeaturesByWktReq.getWktFilter(), getFeaturesByWktReq.getDistance());
        } else {
//            String geoTemplate = "right join (select ST_Buffer(ST_GeomFromText('%s',4326)::geography, 0)::geometry) cb2 on ST_intersects(cb.geom, cb2.ST_Buffer)";
            String geoTemplate = "right join (select ST_GeomFromText('%s',4326) as ST_Buffer) cb2 on ST_intersects(cb.geom, cb2.ST_Buffer)";
            geowhere = String.format(geoTemplate, getFeaturesByWktReq.getWktFilter());
        }
        GetFeaturesByFilterAPIReq getFeaturesByFilterAPIReq = new GetFeaturesByFilterAPIReq();
        getFeaturesByFilterAPIReq.setLayerID(getFeaturesByWktReq.getLayerID());
        getFeaturesByFilterAPIReq.setFilter(geowhere);
        getFeaturesByFilterAPIReq.setCount(getFeaturesByWktReq.getCount());
        if (getFeaturesByWktReq.getFirstIndex() != 0) {
            getFeaturesByFilterAPIReq.setFirstIndex((getFeaturesByWktReq.getFirstIndex() - 1) * getFeaturesByWktReq.getCount());
        }
        GeojsonStruckRes returnFeatures = getFeaturesByFilterAPI(getFeaturesByFilterAPIReq);
        return returnFeatures;
    }


    /**
     * 根据过滤条件查询 Features
     *
     * @param getFeaturesByFilterAPIReq
     * @return
     */
    public GeojsonStruckRes getFeaturesByFilterAPI2(GetFeaturesByFilterAPIReq getFeaturesByFilterAPIReq) {
        LayerInfo layerInfo = layerInfoMapper.selectById(getFeaturesByFilterAPIReq.getLayerID());
        getFeaturesByFilterAPIReq.setLayerName(layerInfo.getLayerName());
        String cols = getFeaturesByFilterAPIReq.getCols();
        if(cols == null) {
            String tableField = gisLayerMapper.tableField(getFeaturesByFilterAPIReq);
            getFeaturesByFilterAPIReq.setCols(tableField);
        }
        int returnFeaturesCount = 0;
        GeojsonStruckRes geojsonStruckRes = new GeojsonStruckRes();
        if (StringUtils.isNotEmpty(getFeaturesByFilterAPIReq.getWktFilter())){
            SqlUtil.filterKeyword(getFeaturesByFilterAPIReq.getWktFilter());
        }
        if (getFeaturesByFilterAPIReq.getCount() == 0) {
            returnFeaturesCount = gisLayerMapper.getFeaturesByFilterAPICount(getFeaturesByFilterAPIReq);
        } else {
            String returnFeatures = "{\"type\":\"FeatureCollection\",\"features\":null}";
            try {
                returnFeatures = gisLayerMapper.getFeaturesByFilterAPI2(getFeaturesByFilterAPIReq);
                JSONObject object = JSON.parseObject(returnFeatures);
                JSONArray features = object.getJSONArray("features");
                JSONObject o = features.getJSONObject(0);
                if (o.getString("id") == null){
                    geojsonStruckRes.setGeoData(null);
                    returnFeaturesCount = 0;
                }else {
                    returnFeaturesCount = gisLayerMapper.getFeaturesByFilterAPICount(getFeaturesByFilterAPIReq);
                    geojsonStruckRes.setGeoData(JSON.parse(returnFeatures));
                }

            } catch (Exception e) {
                log.error("地块高级查询错误", e);
            }
        }
        geojsonStruckRes.setGeoType(layerInfo.getGeoType());
        geojsonStruckRes.setAllCount(returnFeaturesCount);
        return geojsonStruckRes;
    }

    /**
     * 根据空间范围过滤条件查询 Features
     *
     * @param getFeaturesByWktReq
     * @return
     */
    public GeojsonStruckRes getFeaturesByWkt2(GetFeaturesByWktReq getFeaturesByWktReq) {
        String geowhere;
        if (getFeaturesByWktReq.getDistance() > 0) {
            String geoTemplate = "right join (select ST_Buffer(ST_GeomFromText('%s',4326)::geography, %f)::geometry) cb2 on ST_intersects(cb.geom, cb2.ST_Buffer)";
            geowhere = String.format(geoTemplate, getFeaturesByWktReq.getWktFilter(), getFeaturesByWktReq.getDistance());
        } else {
//            String geoTemplate = "right join (select ST_Buffer(ST_GeomFromText('%s',4326)::geography, 0)::geometry) cb2 on ST_intersects(cb.geom, cb2.ST_Buffer)";
            String geoTemplate = "right join (select ST_GeomFromText('%s',4326) as ST_Buffer) cb2 on ST_intersects(cb.geom, cb2.ST_Buffer)";
            geowhere = String.format(geoTemplate, getFeaturesByWktReq.getWktFilter());
        }
        GetFeaturesByFilterAPIReq getFeaturesByFilterAPIReq = new GetFeaturesByFilterAPIReq();
        getFeaturesByFilterAPIReq.setCols(getFeaturesByWktReq.getCols());
        getFeaturesByFilterAPIReq.setLayerID(getFeaturesByWktReq.getLayerID());
        getFeaturesByFilterAPIReq.setFilter(geowhere);
        getFeaturesByFilterAPIReq.setCount(getFeaturesByWktReq.getCount());
        if (getFeaturesByWktReq.getFirstIndex() != 0) {
            getFeaturesByFilterAPIReq.setFirstIndex((getFeaturesByWktReq.getFirstIndex() - 1) * getFeaturesByWktReq.getCount());
        }
        GeojsonStruckRes returnFeatures = getFeaturesByFilterAPI2(getFeaturesByFilterAPIReq);
        return returnFeatures;
    }

    /**
     * 土地利用现状查询 Features
     *
     * @param searchXZTBReq
     * @return
     */
    public GeojsonStruckRes searcXZTB(SearchXZTBReq searchXZTBReq) {
        StringBuffer sb = new StringBuffer();
        sb.append("where 1=1 ");
        if (StringUtils.isNotEmpty(searchXZTBReq.getDlmc())) {
            sb.append("and cb.dlmc like \'%" + searchXZTBReq.getDlmc() + "%\'");
        }
        if (StringUtils.isNotEmpty(searchXZTBReq.getTbbh())) {
            sb.append("and cb.tbbh like \'%" + searchXZTBReq.getTbbh() + "%\'");
        }
        if (StringUtils.isNotEmpty(searchXZTBReq.getZldwmc())) {
            sb.append("and cb.zldwmc like \'%" + searchXZTBReq.getZldwmc() + "%\'");
        }
        if (StringUtils.isNotEmpty(searchXZTBReq.getGdpdj())) {
            sb.append("and cb.gdpdj in (");
            for (String gdpdjb : searchXZTBReq.getGdpdj()) {
                sb.append("\'" + gdpdjb + "\',");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
        }
        GetFeaturesByFilterAPIReq getFeaturesByFilterAPIReq = new GetFeaturesByFilterAPIReq();
        getFeaturesByFilterAPIReq.setLayerID(searchXZTBReq.getLayerID());
        getFeaturesByFilterAPIReq.setFilter(sb.toString());
        getFeaturesByFilterAPIReq.setCount(searchXZTBReq.getCount());
        if (searchXZTBReq.getFirstIndex() != 0) {
            getFeaturesByFilterAPIReq.setFirstIndex((searchXZTBReq.getFirstIndex() - 1) * searchXZTBReq.getCount());
        }
        GeojsonStruckRes returnFeatures = getFeaturesByFilterAPI(getFeaturesByFilterAPIReq);
        return returnFeatures;
    }

    /**
     * 基本农田查询 Features
     *
     * @param searchJBNTReq
     * @return
     */
    public GeojsonStruckRes searchJBNT(SearchJBNTReq searchJBNTReq) {
        StringBuffer sb = new StringBuffer();
        sb.append("where 1=1");
        if (StringUtils.isNotEmpty(searchJBNTReq.getDlmc())) {
            sb.append("and cb.dlmc in (");
            for (String dlmc : searchJBNTReq.getDlmc()) {
                sb.append("\'" + dlmc + "\',");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
        }
        if (StringUtils.isNotEmpty(searchJBNTReq.getTbbh())) {
            sb.append("and cb.tbbh like \'%" + searchJBNTReq.getTbbh() + "%\'");
        }
        if (StringUtils.isNotEmpty(searchJBNTReq.getGdpdjb())) {
            sb.append("and cb.gdpdjb in (");
            for (String gdpdjb : searchJBNTReq.getGdpdjb()) {
                sb.append("\'" + gdpdjb + "\',");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
        }
        if (StringUtils.isNotEmpty(searchJBNTReq.getGdlx())) {
            sb.append("and cb.gdlx in (");
            for (String gdlx : searchJBNTReq.getGdlx()) {
                sb.append("\'" + gdlx + "\',");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
        }
        if (StringUtils.isNotEmpty(searchJBNTReq.getGdzzsxmc())) {
            sb.append("and cb.gdzzsxmc in (");
            for (String gdzzsxmc : searchJBNTReq.getGdzzsxmc()) {
                sb.append("\'" + gdzzsxmc + "\',");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
        }
        if (StringUtils.isNotEmpty(searchJBNTReq.getGddj())) {
            sb.append("and cb.gddj in (");
            for (String gddj : searchJBNTReq.getGddj()) {
                sb.append("\'" + gddj + "\',");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
        }
        GetFeaturesByFilterAPIReq getFeaturesByFilterAPIReq = new GetFeaturesByFilterAPIReq();
        getFeaturesByFilterAPIReq.setLayerID(searchJBNTReq.getLayerID());
        getFeaturesByFilterAPIReq.setFilter(sb.toString());
        getFeaturesByFilterAPIReq.setCount(searchJBNTReq.getCount());
        if (searchJBNTReq.getFirstIndex() != 0) {
            getFeaturesByFilterAPIReq.setFirstIndex((searchJBNTReq.getFirstIndex() - 1) * searchJBNTReq.getCount());
        }
        GeojsonStruckRes returnFeatures = getFeaturesByFilterAPI(getFeaturesByFilterAPIReq);
        return returnFeatures;
    }

    /**
     * 生态保护红线查询 Features
     *
     * @param searchSTBHHXReq
     * @return
     */
    public GeojsonStruckRes searchSTBHHX(SearchSTBHHXReq searchSTBHHXReq) {
        StringBuffer sb = new StringBuffer();
        sb.append("where 1=1");
        if (StringUtils.isNotEmpty(searchSTBHHXReq.getHxmc())) {
            sb.append("and cb.hxmc like \'%" + searchSTBHHXReq.getHxmc() + "%\'");
        }
        if (StringUtils.isNotEmpty(searchSTBHHXReq.getHxlx())) {
            sb.append("and cb.hxlx in (");
            for (String hxlx : searchSTBHHXReq.getHxlx()) {
                sb.append("\'" + hxlx + "\',");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
        }
        if (StringUtils.isNotEmpty(searchSTBHHXReq.getZrbhdjb())) {
            sb.append("and cb.zrbhdjb in (");
            for (String zrbhdjb : searchSTBHHXReq.getZrbhdjb()) {
                sb.append("\'" + zrbhdjb + "\',");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
        }
        if (StringUtils.isNotEmpty(searchSTBHHXReq.getZrbhdlx())) {
            sb.append("and cb.zrbhdlx in (");
            for (String zrbhdlx : searchSTBHHXReq.getZrbhdlx()) {
                sb.append("\'" + zrbhdlx + "\',");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
        }
        if (StringUtils.isNotEmpty(searchSTBHHXReq.getZrbhdfq())) {
            sb.append("and cb.zrbhdfq in (");
            for (String zrbhdfq : searchSTBHHXReq.getZrbhdfq()) {
                sb.append("\'" + zrbhdfq + "\',");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
        }
        GetFeaturesByFilterAPIReq getFeaturesByFilterAPIReq = new GetFeaturesByFilterAPIReq();
        getFeaturesByFilterAPIReq.setLayerID(searchSTBHHXReq.getLayerID());
        getFeaturesByFilterAPIReq.setFilter(sb.toString());
        getFeaturesByFilterAPIReq.setCount(searchSTBHHXReq.getCount());
        if (searchSTBHHXReq.getFirstIndex() != 0) {
            getFeaturesByFilterAPIReq.setFirstIndex((searchSTBHHXReq.getFirstIndex() - 1) * searchSTBHHXReq.getCount());
        }
        GeojsonStruckRes returnFeatures = getFeaturesByFilterAPI(getFeaturesByFilterAPIReq);
        return returnFeatures;
    }

    /**
     * 建设用地管制区查询,土地利用总体规划 Features
     *
     * @param searcJSYDGZQReq
     * @return
     */
    public GeojsonStruckRes searcJSYDGZQ(SearcJSYDGZQReq searcJSYDGZQReq) {
        StringBuffer sb = new StringBuffer();
        sb.append("where 1=1");
        if (StringUtils.isNotEmpty(searcJSYDGZQReq.getGzqlxdm())) {
            sb.append("and cb.gzqlxdm in (");
            for (String gzqlxdm : searcJSYDGZQReq.getGzqlxdm()) {
                sb.append("\'" + gzqlxdm + "\',");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
        }
        GetFeaturesByFilterAPIReq getFeaturesByFilterAPIReq = new GetFeaturesByFilterAPIReq();
        getFeaturesByFilterAPIReq.setLayerID(searcJSYDGZQReq.getLayerID());
        getFeaturesByFilterAPIReq.setFilter(sb.toString());
        getFeaturesByFilterAPIReq.setCount(searcJSYDGZQReq.getCount());
        if (searcJSYDGZQReq.getFirstIndex() != 0) {
            getFeaturesByFilterAPIReq.setFirstIndex((searcJSYDGZQReq.getFirstIndex() - 1) * searcJSYDGZQReq.getCount());
        }
        GeojsonStruckRes returnFeatures = getFeaturesByFilterAPI(getFeaturesByFilterAPIReq);
        return returnFeatures;
    }

    /**
     * 土地报批查询 Features
     *
     * @param searchTDBPTReq
     * @return
     */
    public GeojsonStruckRes searcTDBP(SearchTDBPTReq searchTDBPTReq) {
        StringBuffer sb = new StringBuffer();
        sb.append("where 1=1");
        if (StringUtils.isNotEmpty(searchTDBPTReq.getXmmc())) {
            sb.append("and cb.xmmc like \'%" + searchTDBPTReq.getXmmc() + "%\'");
        }
        if (StringUtils.isNotEmpty(searchTDBPTReq.getPzwh())) {
            sb.append("and cb.pzwh like \'%" + searchTDBPTReq.getPzwh() + "%\'");
        }
        GetFeaturesByFilterAPIReq getFeaturesByFilterAPIReq = new GetFeaturesByFilterAPIReq();
        getFeaturesByFilterAPIReq.setLayerID(searchTDBPTReq.getLayerID());
        getFeaturesByFilterAPIReq.setFilter(sb.toString());
        getFeaturesByFilterAPIReq.setCount(searchTDBPTReq.getCount());
        if (searchTDBPTReq.getFirstIndex() != 0) {
            getFeaturesByFilterAPIReq.setFirstIndex((searchTDBPTReq.getFirstIndex() - 1) * searchTDBPTReq.getCount());
        }
        GeojsonStruckRes returnFeatures = getFeaturesByFilterAPI(getFeaturesByFilterAPIReq);
        return returnFeatures;
    }


    /**
     * 其他类型查询 Features
     *
     * @param searcOtherReq
     * @return
     */
    public GeojsonStruckRes searcOther(SearcOtherReq searcOtherReq) {
        StringBuffer sb = new StringBuffer();
        sb.append("where 1=1");
        if (StringUtils.isNotEmpty(searcOtherReq.getXzqdm())) {
            sb.append("and cb.xzqdm like \'%" + searcOtherReq.getXzqdm() + "%\'");
        }
        if (StringUtils.isNotEmpty(searcOtherReq.getBsm())) {
            sb.append("and cb.bsm like \'%" + searcOtherReq.getBsm() + "%\'");
        }
        if (StringUtils.isNotEmpty(searcOtherReq.getYsdm())) {
            sb.append("and cb.ysdm like \'%" + searcOtherReq.getYsdm() + "%\'");
        }

        GetFeaturesByFilterAPIReq getFeaturesByFilterAPIReq = new GetFeaturesByFilterAPIReq();
        getFeaturesByFilterAPIReq.setLayerID(searcOtherReq.getLayerID());
        getFeaturesByFilterAPIReq.setFilter(sb.toString());
        getFeaturesByFilterAPIReq.setCount(searcOtherReq.getCount());
        if (searcOtherReq.getFirstIndex() != 0) {
            getFeaturesByFilterAPIReq.setFirstIndex((searcOtherReq.getFirstIndex() - 1) * searcOtherReq.getCount());
        }
        GeojsonStruckRes returnFeatures = getFeaturesByFilterAPI(getFeaturesByFilterAPIReq);
        return returnFeatures;
    }

    public GeojsonStruckRes layerParcelQuery(LayerParcelQueryDTO filter) {
        LayerInfo layerInfo = layerInfoMapper.selectById(filter.getLayerID());
        if (layerInfo == null) {
            throw new CustomException("参数错误");
        }
        StringBuffer sb = new StringBuffer();
        SpaceQueryTypeEnums enums = SpaceQueryTypeEnums.get(filter.getSpaceType());
        if (StringUtils.isNotEmpty(filter.getWktFilter())){
            SqlUtil.filterKeyword(filter.getWktFilter());
        }
        if (StringUtils.isNotEmpty(filter.getWktFilter()) && filter.getDistance() > 0) {
            String geoTemplate = "right join (select ST_Buffer(ST_GeomFromText('%s',4326)::geography, %f)::geometry) cb2 on " + enums.getJoin() + "(cb2.ST_Buffer,cb.geom)";
            sb.append(String.format(geoTemplate, filter.getWktFilter(), filter.getDistance()));
        } else if (StringUtils.isNotEmpty(filter.getWktFilter())) {
            String geoTemplate = "right join (select ST_GeomFromText('%s',4326) as ST_Buffer) cb2 on " + enums.getJoin() + "(cb2.ST_Buffer, cb.geom)";
            sb.append(String.format(geoTemplate, filter.getWktFilter()));
        }
        sb.append("where 1=1 ");
        List<LayerParcelQueryFiltersDTO> filters = filter.getFilters();

        //查询图层字段
        List<SysGisFieldsDTO> fields = pgLayerCommonMapper.getLayerTypeFields(layerInfo.getLayerTypeSimple());
        Map<String, Integer> fieldMap = StreamUtils.toMap(fields, SysGisFieldsDTO::getCode, SysGisFieldsDTO::getType);
        log.info("搜索字段 {} 筛选条件 {} 所有字段 {}", layerInfo.getLayerTypeSimple(), JSON.toJSONString(filter), JSON.toJSONString(fieldMap));
        for (LayerParcelQueryFiltersDTO dto : filters) {
            if(StringUtils.isNotEmpty(dto.getSearchKey())){
                dto.setSearchKey(dto.getSearchKey().toLowerCase());
            }
            LayerParcelQuerySearchTypeEnums searchTypeEnums = LayerParcelQuerySearchTypeEnums.get(dto.getSearchType());
            LayerParcelQueryValueTypeEnums valueTypeEnums = LayerParcelQueryValueTypeEnums.get(dto.getValueType());
            if (searchTypeEnums == null || valueTypeEnums == null) {
                throw new CustomException("搜索类型错误");
            }
            Integer type = fieldMap.get(dto.getSearchKey());
            if (type == null && !"gid".equals(dto.getSearchKey()) && !PgService.FIXED_FIELDS.contains(dto.getSearchKey())) {
                throw new CustomException("字段不存在");
            }
            SysLayerTypeFieldsTypeEnums typeEnums = SysLayerTypeFieldsTypeEnums.get(type);
            StringBuilder filterValues = new StringBuilder();
            if (valueTypeEnums == LayerParcelQueryValueTypeEnums.TWO || valueTypeEnums == LayerParcelQueryValueTypeEnums.THREE) {
                filterValues.append("'%" + dto.getSearchValue().replace("%","\\%") + "%'");
            } else if (valueTypeEnums == LayerParcelQueryValueTypeEnums.NINE || valueTypeEnums == LayerParcelQueryValueTypeEnums.TEN) {
                String[] split = dto.getSearchValue().split(",");
                StringBuilder sbValue = new StringBuilder();
                for (int i = 0; i < split.length; i++) {
                    sbValue.append("'" + split[i] + "'").append(",");
                }
                sbValue.deleteCharAt(sbValue.length() - 1);
                filterValues.append(sbValue);
            } else {
                filterValues.append("'" + dto.getSearchValue() + "'");
            }
            sb.append(searchTypeEnums.getJoin()).append(" cb.").append(dto.getSearchKey() + String.format(valueTypeEnums.getJoin(), filterValues)).append(" ");
        }

        GetFeaturesByFilterAPIReq getFeaturesByFilterAPIReq = new GetFeaturesByFilterAPIReq();
        getFeaturesByFilterAPIReq.setLayerID(filter.getLayerID());
        getFeaturesByFilterAPIReq.setFilter(sb.toString());
        getFeaturesByFilterAPIReq.setCount(filter.getCount());
        if (filter.getFirstIndex() != 0) {
            getFeaturesByFilterAPIReq.setFirstIndex((filter.getFirstIndex() - 1) * filter.getCount());
        }
        GeojsonStruckRes returnFeatures = getFeaturesByFilterAPI(getFeaturesByFilterAPIReq);
        return returnFeatures;
    }

}
