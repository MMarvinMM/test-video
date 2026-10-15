package com.chinatower.gis.modules.gis.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.chinatower.bdm.api.LayerApi;
import com.chinatower.bdm.api.entity.MineralsLayerReqVO;
import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.core.dto.bdm.UpdateAreaDTO;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.IdUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.gis.api.entity.DeleteLayerReq;
import com.chinatower.gis.api.entity.LayerInfoAddDTO;
import com.chinatower.gis.api.entity.LayerPGDTO;
import com.chinatower.gis.api.entity.LayerPGRes;
import com.chinatower.gis.api.entity.layer.*;
import com.chinatower.gis.modules.gis.entity.*;
import com.chinatower.gis.modules.gis.mapper.*;
import com.chinatower.gis.utils.StreamUtils;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.entity.admin.SysArea;
import com.chinatower.platform.entity.gis.SysGisFieldsDTO;
import com.chinatower.platform.enums.*;
import com.chinatower.platform.utils.BeanCopyUtils;
import com.chinatower.platform.utils.BigDecimalUtils;
import com.chinatower.platform.utils.SmsUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.input.ReaderInputStream;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.WKBWriter;
import org.locationtech.jts.io.WKTReader;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Wrapper;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author chuan hao
 * @version 1.0
 * @desription: pg图层操作
 * @data 创建时间：2023/8/11 22:29
 */
@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("all")
public class PgService {

    private final LayerInfoMapper layerInfoMapper;

    private final SlzyCeshitableMapper slzyCeshitableMapper;
    private final PgLayerCommonMapper pgLayerCommonMapper;
    private final GisMapper gisMapper;
    private final LayerApi layerApi;

    public final Map<String, PgBaseMapper> map;

    public static final String LAYER_TEMP_ERROR = "图层模板不存在";

    private static final String YU_BI_DU_1 = "yu_bi_du_1";
    private static final String BSM = "bsm";
    private static final String PROPERTIES = "properties";
    private static final String GEOM = "geom";
    private static final String DLMC = "dlmc";
    private static final String COVERAREA = "coverarea";
    private static final String COVERRATIO = "coverratio";
    private static final String REFTABLENAME = "reftablename";
    private static final String ZLDWMC = "zldwmc";
    //图层固定必有字段
    public static final String FIXED_FIELDS = "geom,layertype,bsm,properties";
    public static final String XZTB_TMP_FIELDS = "bsm, coverarea, coverratio, dlmc, reftablename";
    private static final String N = "\n";
    private static final String T = "\t";
    // 表名称——字段名称-表名称——字段名称
    private static final String INDEX_SQL_GEOM = "CREATE INDEX %s_%s ON %s USING gist (%s);";
    private static final String INDEX_SQL = "CREATE INDEX %s_%s ON %s (%s);";
    private static final String INDEX_SEQUENCE_SQL = "ALTER SEQUENCE %s_gid_seq OWNED BY %s.gid;";
    private static final String SQL_SEQUENCE = "CREATE SEQUENCE %s_gid_seq\n" +
            "    INCREMENT 1\n" +
            "    START 1\n" +
            "    MINVALUE 1\n" +
            "    MAXVALUE 2147483647\n" +
            "    CACHE 1;";

    public PgBaseMapper getPgBaseMapper(String name) {
        return map.get(name);
    }

    public void updateExpire() {
        List<String> tableNames = pgLayerCommonMapper.updateExpireTableName();
        for (String tableName : tableNames) {
           try {
               pgLayerCommonMapper.updateExpire(tableName);
           }catch (Exception e){
               log.error("更新过期状态错误",e);
           }
        }
    }

    /**
     * @param pgdto
     * @return 创建模板表
     */
    public Result createLayerInfo(LayerPGDTO pgdto) throws Exception {
        PGLayerTempEnums tempEnums = PGLayerTempEnums.get(pgdto.getLayerTypeSimple());
        if (tempEnums == null) {
            return Result.fail(LAYER_TEMP_ERROR);
        }
        PgBaseMapper pgBaseMapper = getPgBaseMapper(tempEnums.getMapper());

        LayerInfo info = BeanCopyUtils.copy(pgdto, LayerInfo.class);
        info.setLayerName(pgdto.getPgTableName());
        //保存图层信息
        info.setDeleteFlag(false);
        info.setLayerName(pgdto.getPgTableName());
        layerInfoMapper.insert(info);
        //创建新表
        pgBaseMapper.createTable(info.getLayerName());
        //创建视图
        pgBaseMapper.createTableView(info.getLayerName());
        return Result.success("创建成功");
    }

    /**
     * @param pgdto
     * @return 创建模板表
     */
    public Result createModelLayerTable(LayerPGDTO pgdto) {
        //创建新表
        pgLayerCommonMapper.createTable(getCreateLayerTableSql(pgdto));
        return Result.success("创建成功");
    }

    public Result editLayer(String layerName,String bakLayerName) {
        //备份成功
        pgLayerCommonMapper.editLayer(layerName,bakLayerName);
        return Result.success("备份成功");
    }

    public Result editBackLayer(String layerName,String bakLayerName) {
        //备份成功
        pgLayerCommonMapper.editBackLayer(layerName,bakLayerName);
        return Result.success("备份还原成功");
    }



    /**
     * 创建表sql
     */
    private String getCreateLayerTableSql(LayerPGDTO pgdto) {
        //类型字段
        List<SysGisFieldsDTO> fields = pgdto.getFields();
        StringBuilder sqlSb = new StringBuilder();
        //创建主键序列
        sqlSb.append(String.format(SQL_SEQUENCE, pgdto.getPgTableName()));
        //创建表语句
        sqlSb.append(N);
        sqlSb.append("CREATE TABLE ").append(pgdto.getPgTableName());
        sqlSb.append(N).append("(");
        sqlSb.append(N).append("gid").append(T).append("integer NOT NULL DEFAULT nextval('" + pgdto.getPgTableName() + "_gid_seq'::regclass),");
        sqlSb.append(N).append(GEOM).append(T).append("geometry,");
        sqlSb.append(N).append("minx").append(T).append(SysLayerTypeFieldsTypeEnums.TWO.getValue());
        sqlSb.append(N).append("miny").append(T).append(SysLayerTypeFieldsTypeEnums.TWO.getValue());
        sqlSb.append(N).append("maxx").append(T).append(SysLayerTypeFieldsTypeEnums.TWO.getValue());
        sqlSb.append(N).append("maxy").append(T).append(SysLayerTypeFieldsTypeEnums.TWO.getValue());
        sqlSb.append(N).append("layertype").append(T).append(String.format(SysLayerTypeFieldsTypeEnums.THREE.getValue(), 50));
        sqlSb.append(N).append("calarea").append(T).append(SysLayerTypeFieldsTypeEnums.TWO.getValue());
        sqlSb.append(N).append(PROPERTIES).append(T).append(SysLayerTypeFieldsTypeEnums.ZERO.getValue());
        sqlSb.append(N).append(BSM).append(T).append(String.format(SysLayerTypeFieldsTypeEnums.THREE.getValue(), 50));
        //其他动态字段
        String[] split = FIXED_FIELDS.split(",");
        List<String> list = Arrays.asList(split);
        fields.forEach(field -> {
            boolean b = list.stream().anyMatch(s -> s.trim().equalsIgnoreCase(field.getCode()));
            if (b) {
                return;
            }
            SysLayerTypeFieldsTypeEnums typeEnums = SysLayerTypeFieldsTypeEnums.get(field.getType());
            String limitation = typeEnums.getValue();
            if (typeEnums == SysLayerTypeFieldsTypeEnums.THREE) {
                limitation = String.format(limitation, field.getChartLength() == null ? 50 : field.getChartLength());
            }
            sqlSb.append(N).append(field.getCode()).append(T).append(limitation);
        });
        sqlSb.append(N).append("CONSTRAINT").append(T).append(pgdto.getPgTableName() + "_pkey PRIMARY KEY (gid)");
        sqlSb.append(N).append(");");
        sqlSb.append(N).append(String.format(INDEX_SEQUENCE_SQL, pgdto.getPgTableName(), pgdto.getPgTableName()));
        sqlSb.append(N).append(String.format(INDEX_SQL_GEOM, pgdto.getPgTableName(), GEOM, pgdto.getPgTableName(), GEOM));
        sqlSb.append(N).append(String.format(INDEX_SQL, pgdto.getPgTableName(), BSM, pgdto.getPgTableName(), BSM));
        //索引字段
        List<SysGisFieldsDTO> indexList = fields.stream().filter(field -> field.getFilterIndex() != null && field.getFilterIndex() == 1).collect(Collectors.toList());
        if (!indexList.isEmpty()) {
            indexList.forEach(field -> {
                boolean b = list.stream().anyMatch(s -> s.equals(field.getCode()));
                if (!b) {
                    sqlSb.append(N).append(String.format(INDEX_SQL, pgdto.getPgTableName(), field.getCode(), pgdto.getPgTableName(), field.getCode()));
                }
            });
        }
        log.info("创建表sql \r\n {}", sqlSb);

        return sqlSb.toString();
    }

    /**
     * @param pgdto
     * @return 插入图层信息表
     */
    public Result insertLayerInfo(LayerPGDTO pgdto) {
        LayerInfo info = BeanCopyUtils.copy(pgdto, LayerInfo.class);
        info.setLayerName(pgdto.getPgTableName());
        //保存图层信息
        info.setDeleteFlag(false);
        info.setLayerName(pgdto.getPgTableName());
        layerInfoMapper.insert(info);
        return Result.success("创建成功");
    }

    /**
     * @param pgdto
     * @return 计算图斑总面积
     * @throws Exception
     */
    public UpdateAreaDTO layerArea(LayerPGDTO pgdto) throws Exception {
        //删除错误数据
        List<String> mustFields = pgdto.getMustFields().stream().map(SysGisFieldsDTO::getCode).collect(Collectors.toList());
        if (!mustFields.isEmpty()) {
            String join = StreamUtils.join(mustFields, s -> s, " is null or ") + " is null";
            log.info("必填字段 {} {}", pgdto.getPgTableName(), join);
            pgLayerCommonMapper.deleteBadData(pgdto.getPgTableName(), join);
        }
        PGLayerTempEnums tempEnums = PGLayerTempEnums.get(pgdto.getLayerTypeSimple());
        if (PGLayerTempEnums.TKQ == tempEnums || PGLayerTempEnums.CKQ == tempEnums) {
            pgLayerCommonMapper.updateExpire(pgdto.getPgTableName());
        }
//        pgLayerCommonMapper.updateSRID(pgdto.getPgTableName());
        //计算面积
        pgLayerCommonMapper.updateCalarea(pgdto.getPgTableName());
        Double layerArea = 0D;
        if (StringUtils.isNotEmpty(pgdto.getAreaField())) {
            //图层总面积
            layerArea = pgLayerCommonMapper.getLayerArea(pgdto.getPgTableName(), pgdto.getAreaField());
        }
        UpdateAreaDTO res = new UpdateAreaDTO();
        res.setAbbreviation(pgdto.getLayerTypeSimple());
        res.setType(pgdto.getType());
        res.setLayerArea(layerArea);
        res.setPolygonCount(layerInfoMapper.getCount(pgdto.getPgTableName()));
        res.setLayerId(pgdto.getLayerId());
        return res;
    }


    @Resource
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    public static Geometry convertStringToGeometry(String wktString) throws Exception {
        GeometryFactory geometryFactory = new GeometryFactory();
        WKTReader reader = new WKTReader();
        Geometry geometry = reader.read(wktString);
        geometry.setSRID(4326);
        return geometry;
    }

    /**
     * copy 入库图层数据
     */
    public void addCopyPg(LayerPGDTO pgdto, List<SysGisFieldsDTO> fields, List<Map<String, Object>> values) {
        WKBWriter wkbWriter = new WKBWriter();
        DataSource postgres = dynamicRoutingDataSource.getDataSource("postgres");
        Connection hikariConnection = null;
        try {
            hikariConnection = postgres.getConnection();
            Connection unwrappedConn = ((Wrapper) hikariConnection).unwrap(BaseConnection.class);
            CopyManager copyManager = new CopyManager((BaseConnection) unwrappedConn);
            StringBuilder copySqlIn = new StringBuilder();
            copySqlIn.append("COPY ").append(pgdto.getPgTableName()).append(" (");
            copySqlIn.append(FIXED_FIELDS);
            for (SysGisFieldsDTO filed : fields) {
                copySqlIn.append(",").append(filed.getCode());
            }
            copySqlIn.append(") FROM STDIN WITH (FORMAT CSV, DELIMITER '|')");
            log.info("新增图层数据copy {}", copySqlIn);

            List<String> fieldsList = StreamUtils.toList(fields, SysGisFieldsDTO::getCode);
            Map<String, SysGisFieldsDTO> fieldsDTOMap = StreamUtils.toMap(fields, SysGisFieldsDTO::getCode, s -> s);
            StringWriter writer = new StringWriter();
            for (Map map : values) {

                writer.append(map.get("the_geom").toString()).append("|");
                writer.append(pgdto.getLayerTypeSimple().toString()).append("|");
                writer.append(map.getOrDefault(BSM, IdUtils.fastSimpleUUID()).toString()).append("|");
                writer.append(map.get(PROPERTIES).toString()).append("|");

                //fileds
                for (int i = 0; i < fieldsList.size(); i++) {
                    String fieldCode = fieldsList.get(i);
                    SysGisFieldsDTO fieldsDTO = fieldsDTOMap.get(fieldCode);
                    if (!BSM.equals(fieldCode)) {

                        SysLayerTypeFieldsTypeEnums typeEnums = SysLayerTypeFieldsTypeEnums.get(fieldsDTO.getType());
                        Object value = null;
                        if (SysLayerTypeFieldsTypeEnums.THREE == typeEnums
                                || SysLayerTypeFieldsTypeEnums.ZERO == typeEnums) {
                            value = map.getOrDefault(fieldCode, "").toString();
                        } else {
                            value = map.getOrDefault(fieldCode, null);
                        }
                        if (value != null) {
                            writer.append(value.toString());
                        }
                        if (i < fieldsList.size() - 1) {
                            writer.append("|");
                        } else {
                            writer.append("\n");
                        }
                    }
                }
            }
            copyManager.copyIn(copySqlIn.toString(), new ReaderInputStream(new StringReader(writer.toString()), StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("copy 入库失败", e);
            throw new CustomException("保存图层数据错误");
        } finally {
            try {
                if (hikariConnection != null) {
                    hikariConnection.close();
                }
            } catch (Exception e) {
                log.error("关闭jdbc 错误", e);
                throw new CustomException("保存图层数据错误");
            }
        }
    }

    /**
     * @param pgdto 新增图层参数
     * @return 新增图层
     */
    public Result<LayerPGRes> addLayer(LayerPGDTO pgdto) throws Exception {
        PGLayerTempEnums tempEnums = PGLayerTempEnums.get(pgdto.getLayerTypeSimple());
        if (PGLayerTempEnums.QTLXDK == tempEnums && StringUtils.isNotEmpty(pgdto.getInitialTypeName())) {
            tempEnums = PGLayerTempEnums.get(pgdto.getInitialTypeName());
        }
        List<Map<String, Object>> list = pgdto.getList();
        if (tempEnums == PGLayerTempEnums.TDBP_CESHITABLE) {
            for (Map<String, Object> map : list) {
                Object pzrq = map.getOrDefault("pzrq", null);
                if (pzrq != null) {
                    map.put("pzrq", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, DateUtils.parseDate(pzrq)));
                }
            }
        } else if (tempEnums == PGLayerTempEnums.SLZY) {
            for (Map<String, Object> map : list) {
                Object yuBiDuStr = map.getOrDefault(SpecialSubjectEnums.YU_BI_DU.getField(), 0);
                double yuBiDu = Double.parseDouble(yuBiDuStr.toString());
                if (yuBiDu < 0.1) {
                    map.put(YU_BI_DU_1, 1);
                } else if (yuBiDu >= 0.1 && yuBiDu < 0.2) {
                    map.put(YU_BI_DU_1, 2);
                } else if (yuBiDu >= 0.2 && yuBiDu < 0.7) {
                    map.put(YU_BI_DU_1, 3);
                } else if (yuBiDu >= 0.7) {
                    map.put(YU_BI_DU_1, 4);
                }
            }
        }
        //图层类型字段
        List<SysGisFieldsDTO> fields = pgdto.getFields();
        if (fields == null || fields.isEmpty()) {
            return Result.fail("图层类型字段不能为空");
        }

        String[] split = FIXED_FIELDS.split(",");
        List<String> filterCodes = Arrays.asList(split);
        //去掉必填字段
        fields = fields.stream().filter(field -> !filterCodes.stream().anyMatch(s -> s.trim().equalsIgnoreCase(field.getCode()))).collect(Collectors.toList());

//        addCopyPg(pgdto, fields, list);
        addInsertPg(pgdto, fields, list);

        //图层总面积
        LayerPGRes res = new LayerPGRes();
        res.setPolygonCount(list.size());
        res.setLayerId(pgdto.getLayerId());
        res.setPgTableName(pgdto.getPgTableName());
        return Result.data(res);
    }

    /**
     * insert 入库 图层数据
     */
    public void addInsertPg(LayerPGDTO pgdto, List<SysGisFieldsDTO> fields, List<Map<String, Object>> values) {
        List<String> fieldsList = StreamUtils.toList(fields, SysGisFieldsDTO::getCode);
        Map<String, SysGisFieldsDTO> fieldsDTOMap = StreamUtils.toMap(fields, SysGisFieldsDTO::getCode, s -> s);
        StringBuilder sb = new StringBuilder();
        sb.append("insert into ").append(pgdto.getPgTableName()).append(" (");
        sb.append(FIXED_FIELDS);
        StringBuilder insertFields = new StringBuilder();
        fieldsList.forEach(fieldCode -> insertFields.append(",").append(fieldCode));
        sb.append(insertFields);
        sb.append(")");
        sb.append("values");
        log.info("插入数据 {} {}", pgdto.getPgTableName(), sb);
        for (Map<String, Object> map : values) {
            sb.append("(");
            sb.append("ST_GeomFromText('" + map.get("the_geom") + "',4326)");
            sb.append(",").append("'").append(pgdto.getLayerTypeSimple()).append("'");
            sb.append(",").append("'").append(map.getOrDefault(BSM, IdUtils.fastSimpleUUID())).append("'");
            sb.append(",").append("'").append(map.get(PROPERTIES)).append("'");

            fieldsList.forEach(fieldCode -> {
                SysGisFieldsDTO fieldsDTO = fieldsDTOMap.get(fieldCode);
                String code = fieldCode.toLowerCase(Locale.ROOT);
                if (!BSM.equalsIgnoreCase(code)) {
                    SysLayerTypeFieldsTypeEnums typeEnums = SysLayerTypeFieldsTypeEnums.get(fieldsDTO.getType());
                    if (SysLayerTypeFieldsTypeEnums.THREE == typeEnums
                            || SysLayerTypeFieldsTypeEnums.ZERO == typeEnums) {
                        sb.append(",").append("'").append(map.getOrDefault(code, "")).append("'");
                    } else if (SysLayerTypeFieldsTypeEnums.FOUR == typeEnums) {
                        Object orDefault = map.getOrDefault(code, null);
                        if (orDefault != null) {
                            sb.append(",").append("'").append(orDefault).append("'");
                        } else {
                            sb.append(",null");
                        }

                    } else {
                        sb.append(",").append(map.getOrDefault(fieldCode, 0));
                    }
                }
            });
            sb.append(")");
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        pgLayerCommonMapper.saveBatch(sb.toString());
    }

    /**
     * @param req 请求参数
     * @return 删除图层
     */
    public Result<String> deleteLayer(DeleteLayerReq req) {
        if ((req.getLayerIds() == null || req.getLayerIds().isEmpty()) || req.getLayerId() != null) {
            List<String> layerIds = new ArrayList<>();
            layerIds.add(req.getLayerId());
            req.setLayerIds(layerIds);
        }
        if (req.getLayerIds().isEmpty()) {
            return Result.fail("图层ID不能为空");
        }
        List<String> layerIds = req.getLayerIds().stream().distinct().collect(Collectors.toList());

        List<LayerInfo> layerInfos = layerInfoMapper.selectByIds(layerIds);
        if (layerInfos == null || layerInfos.isEmpty()) {
            return Result.fail("图层ID不能为空");
        }
        List<String> tableNames = layerInfos.stream().map(LayerInfo::getLayerName).collect(Collectors.toList());
        layerInfoMapper.deleteTables(tableNames);
        layerInfoMapper.deleteBatchIds(layerIds);
        try {
            layerInfoMapper.deleteTablesSeq(tableNames);
        } catch (Exception e) {
            log.error("删除序列错误", e);
        }
        String str = tableNames.stream().collect(Collectors.joining(","));
        return Result.data(str);
    }

    /**
     * 图斑详情 WFS 数据格式
     *
     * @param req 请求参数
     * @return 巡检路线查询
     */
    public GeojsonStruckRes getXjxlFeaturesByIds(GetXjxlFeaturesByIdsReq req) {
        LayerInfo layerInfo = layerInfoMapper.selectById(req.getLayerID());
        if (layerInfo == null) {
            throw new CustomException("参数错误");
        }
        PGLayerTempEnums tempEnums = PGLayerTempEnums.get(layerInfo.getLayerTypeSimple());
        List<SysGisFieldsDTO> fields = pgLayerCommonMapper.getLayerTypeFields(layerInfo.getLayerTypeSimple());
        Map<String, Object> fieldMap = StreamUtils.toMap(fields, SysGisFieldsDTO::getCode, s -> s);
        String tbbhField = pgLayerCommonMapper.getLayerTbbhField(layerInfo.getLayerTypeSimple());
        StringBuilder mustSb = new StringBuilder();
        mustSb.append(BSM);
        if (StringUtils.isEmpty(tbbhField)) {
            mustSb.append(",'' as tbbh");
        } else {
            mustSb.append("," + tbbhField + " as tbbh");
        }

        if (fieldMap.get(DLMC) == null) {
            mustSb.append(", '' as " + DLMC);
        } else {
            mustSb.append(", " + DLMC);
        }
        if (fieldMap.get(ZLDWMC) == null) {
            mustSb.append(", '' as " + ZLDWMC);
        } else {
            mustSb.append(", " + ZLDWMC);
        }
        List<SysGisFieldsDTO> listFields = StreamUtils.filter(fields, field -> field.getResultIndex() != null && field.getResultIndex() != 0);
        if (tempEnums == PGLayerTempEnums.SLZY) {
            slzyFiledSupplement(listFields);
        }
        String allFields = StreamUtils.join(listFields, SysGisFieldsDTO::getCode, ",");
        log.info("巡检线路 {} 所有字段 {}", mustSb, allFields);
        allFields = "gid,bsm,layertype,maxx,maxy,minx,miny,calarea," + allFields;
        Integer offset = null;
        if (req.getPageNum() != null) {
            offset = (req.getPageNum() - 1) * req.getPageSize();
        }
        String result = pgLayerCommonMapper.inspectionRoute(req, layerInfo.getLayerName(), mustSb.toString(), allFields, offset, req.getPageSize());
        GeojsonStruckRes res = new GeojsonStruckRes();
        if (tempEnums == PGLayerTempEnums.SLZY) {
            GeoData geoData = JSON.parseObject(result, GeoData.class);
            List<String> areas = new ArrayList<>();
            List<GeoDataFeatures> features = geoData.getFeatures();
            if (!features.isEmpty()) {
                for (GeoDataFeatures feature : features) {
                    Map<String, Object> layerXmRes = JSON.parseObject(feature.getProperties().toString(), Map.class);
                    perfectAreas(layerXmRes, areas);
                }
                List<SysArea> sysAreas = gisMapper.selectByAreaCodes(areas);
                Map<String, SysArea> map = StreamUtils.toMap(sysAreas, SysArea::getAreaCode, area -> area);
                for (GeoDataFeatures feature : features) {
                    Map<String, Object> layerXmRes = JSON.parseObject(feature.getProperties().toString(), Map.class);
                    perfectForestLayerXmRes(layerXmRes, map);
                    feature.setProperties(layerXmRes);
                }
            }
            res.setGeoData(geoData);
        } else {
            res.setGeoData(JSON.parse(result));
        }
        res.setGeoType(layerInfo.getGeoType());
        res.setAllCount(pgLayerCommonMapper.inspectionRouteCount(req, layerInfo.getLayerName()));
        return res;
    }


    /**
     * @param req 请求参数
     * @return 巡检下拉数据
     */
    public Result<GeojsonStruckRes> getXlsjFeatures(GetXlsjFeaturesReq req) {
        LayerInfo layerInfo = layerInfoMapper.selectById(req.getLayerID());
        PGLayerTempEnums tempEnums = PGLayerTempEnums.get(layerInfo.getLayerTypeSimple());
        if (tempEnums == PGLayerTempEnums.SLZY) {
            throw new CustomException("该类型不存在，此功能");
        }
        String tbbhField = pgLayerCommonMapper.getLayerTbbhField(layerInfo.getLayerTypeSimple());
        String areaField = pgLayerCommonMapper.getLayerAreaField(layerInfo.getLayerTypeSimple());
        StringBuilder baseFieldsSb = new StringBuilder();
        baseFieldsSb.append(BSM);
        if (StringUtils.isEmpty(areaField)) {
            baseFieldsSb.append(",'' as tbbh");
        } else {
            baseFieldsSb.append("," + tbbhField + " as tbbh");
        }

        if (StringUtils.isEmpty(areaField)) {
            baseFieldsSb.append(",'0' as area");
        } else {
            baseFieldsSb.append("," + areaField + " as area");
        }

        String features = pgLayerCommonMapper.getXlsjFeatures(req, (req.getFirstIndex() - 1) * req.getCount(), req.getCount(), layerInfo.getLayerName(), tbbhField, baseFieldsSb.toString());
        Integer count = pgLayerCommonMapper.getXlsjFeaturesCount(req, layerInfo.getLayerName());
        GeojsonStruckRes res = new GeojsonStruckRes();
        res.setGeoData(JSON.parse(features));
        res.setGeoType(layerInfo.getGeoType());
        res.setAllCount(count);
        return Result.data(res);
    }

    public Result<List<GeometrysRes>> getGeometrys(GeometrysReq req) {
        List<LayerInfo> tables = layerInfoMapper.layerTables(req.getLayerIds());
        if (tables.isEmpty()) {
            return Result.fail("图层数据不存在");
        }
        List<String> listStr = layerInfoMapper.getGeometrys(tables);
        List<GeometrysRes> geometrys = listStr.stream().map(s -> JSON.parseObject(s, GeometrysRes.class)).collect(Collectors.toList());
        return Result.data(geometrys);
    }

    /**
     * 深林资源字段补充
     */
    public void slzyFiledSupplement(List<SysGisFieldsDTO> listFields) {
        listFields.forEach(field -> {
            if ("sheng".equals(field.getCode())) {
                field.setCode("concat(sheng,'0000') as sheng");
            }
            if ("xiang".equals(field.getCode())) {
                field.setCode("concat(xian,xiang,'000') as xiang");
            }
            if ("cun".equals(field.getCode())) {
                field.setCode("concat(xian,xiang,cun) as cun");
            }
        });
    }

    /**
     * @param req
     * @return 小班列表
     */
    public TableDataInfo forestLayerXmList(ForestLayerXmReq req) {
        LayerInfo layerInfo = layerInfoMapper.selectById(req.getLayerId());
        if (layerInfo == null) {
            throw new CustomException("参数错误");
        }
        if (req.getPageNum() == null || req.getPageNum() < 1) {
            req.setPageNum(1);
        }
        if (req.getPageSize() == null) {
            req.setPageSize(5);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("where 1=1 ");
        //兼容以前条件
        slzyList(req);
        //查询图层字段
        List<SysGisFieldsDTO> fields = pgLayerCommonMapper.getLayerTypeFields(layerInfo.getLayerTypeSimple());
        //需要显示的列表字段
        List<SysGisFieldsDTO> listFields = StreamUtils.filter(fields, field -> field.getResultIndex() != null && field.getResultIndex() != 0);
        PGLayerTempEnums tempEnums = PGLayerTempEnums.get(layerInfo.getLayerTypeSimple());
        if (tempEnums == PGLayerTempEnums.SLZY) {
            slzyFiledSupplement(listFields);
        }
        String allFields = StreamUtils.join(listFields, SysGisFieldsDTO::getCode, ",");

        //特殊字段
        List<String> specialFileds = Lists.newArrayList("sheng", "xiang","xian", "cun");
        Map<String, String> nameAreas = new HashMap<>();
        if (!CollectionUtils.isEmpty(req.getFilters())) {
            List<LayerParcelQueryFiltersDTO> filter = StreamUtils.filter(req.getFilters(), d -> specialFileds.contains(d.getSearchKey()));
            Map<String, String> areaMap = StreamUtils.toMap(filter, LayerParcelQueryFiltersDTO::getSearchKey, LayerParcelQueryFiltersDTO::getSearchValue);
            if (!CollectionUtils.isEmpty(areaMap.values())){
                List<SysArea> sysAreas = gisMapper.selectByAreaNames(areaMap.values().stream().collect(Collectors.toList()));
                nameAreas = StreamUtils.toMap(sysAreas, SysArea::getAreaName, SysArea::getAreaCode);
            }
        }

        Map<String, Integer> fieldMap = com.chinatower.platform.utils.StreamUtils.toMap(fields, SysGisFieldsDTO::getCode, SysGisFieldsDTO::getType);
        for (LayerParcelQueryFiltersDTO dto : req.getFilters()) {
            LayerParcelQuerySearchTypeEnums searchTypeEnums = LayerParcelQuerySearchTypeEnums.get(dto.getSearchType());
            LayerParcelQueryValueTypeEnums valueTypeEnums = LayerParcelQueryValueTypeEnums.get(dto.getValueType());
            if (searchTypeEnums == null || valueTypeEnums == null) {
                throw new CustomException("搜索类型错误");
            }
            Integer type = fieldMap.get(dto.getSearchKey());
            if (type == null && !specialFileds.contains(dto.getSearchKey()) && !"gid".equals(dto.getSearchKey()) && !FIXED_FIELDS.contains(dto.getSearchKey())) {
                throw new CustomException("字段不存在");
            }

            String code = nameAreas.get(dto.getSearchValue());
            if ("sheng".equals(dto.getSearchKey()) && StringUtils.isNotEmpty(code)){
                dto.setSearchKey("concat(sheng,'0000')");
                dto.setSearchValue(code);
            }
            if ("xiang".equals(dto.getSearchKey()) && StringUtils.isNotEmpty(code)){
                dto.setSearchKey("concat(xian,xiang,'000')");
                dto.setSearchValue(code);
            }
            if ("xian".equals(dto.getSearchKey()) && StringUtils.isNotEmpty(code)){
                dto.setSearchValue(code);
            }


            StringBuilder filterValues = new StringBuilder();
            if (valueTypeEnums == LayerParcelQueryValueTypeEnums.TWO || valueTypeEnums == LayerParcelQueryValueTypeEnums.THREE) {
                filterValues.append("'%" + dto.getSearchValue() + "%'");
            } else if (valueTypeEnums == LayerParcelQueryValueTypeEnums.NINE) {
                String[] split = dto.getSearchValue().split(",");
                StringBuilder sbValue = new StringBuilder();
                for (String s : split) {
                    sbValue.append("'" + s + "'").append(",");
                }
                sbValue.deleteCharAt(sbValue.length() - 1);
                filterValues.append(sbValue);
            } else {
                filterValues.append("'" + dto.getSearchValue() + "'");
            }
            sb.append(searchTypeEnums.getJoin()).append(dto.getSearchKey() + String.format(valueTypeEnums.getJoin(), filterValues)).append(" ");
        }

        Integer offset = (req.getPageNum() - 1) * req.getPageSize();
        Integer limit = req.getPageSize();
        List<Map<String, Object>> list = slzyCeshitableMapper.forestLayerXmList(layerInfo.getLayerName(), allFields, offset, limit, sb.toString());
        List<String> areas = new ArrayList<>();
        list = list.stream().peek(res -> {
            res.put("geometry", JSON.parseObject(res.get("geometry").toString()));
            res.put("layerId", req.getLayerId());
            perfectAreas(res, areas);
        }).collect(Collectors.toList());

        //查询区域数据
        if (!areas.isEmpty()) {
            List<SysArea> sysAreas = gisMapper.selectByAreaCodes(areas);
            Map<String, SysArea> map = StreamUtils.toMap(sysAreas, SysArea::getAreaCode, area -> area);
            list.forEach(res -> perfectForestLayerXmRes(res, map));
        }

        Long total = slzyCeshitableMapper.forestLayerXmListCount(layerInfo.getLayerName(), sb.toString());
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setTotal(total);
        tableDataInfo.setRows(list);
        tableDataInfo.setCode(HttpStatus.SUCCESS);
        return tableDataInfo;
    }

    /**
     * 深林资源列表以前条件
     */
    private void slzyList(ForestLayerXmReq req) {
        if (StringUtils.isNotEmpty(req.getDiLei())) {
            LayerParcelQueryFiltersDTO dto = new LayerParcelQueryFiltersDTO();
            dto.setSearchKey("di_lei");
            dto.setSearchType(LayerParcelQuerySearchTypeEnums.ONE.getType());
            dto.setSearchValue(req.getDiLei());
            dto.setValueType(LayerParcelQueryValueTypeEnums.ONE.getType());
            req.getFilters().add(dto);
        }
        if (StringUtils.isNotEmpty(req.getYouShiSz())) {
            LayerParcelQueryFiltersDTO dto = new LayerParcelQueryFiltersDTO();
            dto.setSearchKey("you_shi_sz");
            dto.setSearchType(LayerParcelQuerySearchTypeEnums.ONE.getType());
            dto.setSearchValue(req.getYouShiSz());
            dto.setValueType(LayerParcelQueryValueTypeEnums.ONE.getType());
            req.getFilters().add(dto);
        }
        if (StringUtils.isNotEmpty(req.getXiaoBan())) {
            LayerParcelQueryFiltersDTO dto = new LayerParcelQueryFiltersDTO();
            dto.setSearchKey("xiao_ban");
            dto.setSearchType(LayerParcelQuerySearchTypeEnums.ONE.getType());
            dto.setSearchValue(req.getDiLei());
            dto.setValueType(LayerParcelQueryValueTypeEnums.TWO.getType());
            req.getFilters().add(dto);
        }
    }

    /**
     * 完善区域
     */
    private void perfectAreas(Map<String, Object> res, List<String> areas) {
        String province = (String) res.getOrDefault("sheng", "");
        String city = (String) res.getOrDefault("xian", "");
        String county = (String) res.getOrDefault("xiang", "");
        String cun = (String) res.getOrDefault("cun", "");

        if (!areas.contains(province)) {
            areas.add(province);
        }
        if (!areas.contains(city)) {
            areas.add(city);
        }
        if (!areas.contains(county)) {
            areas.add(county);
        }
        if (!areas.contains(cun)) {
            areas.add(cun);
        }
    }

    /**
     * 完善数据
     */
    private void perfectForestLayerXmRes(Map<String, Object> res, Map<String, SysArea> map) {
        SysArea province = map.get(res.get("sheng"));
        SysArea city = map.get(res.get("xian"));
        SysArea county = map.get(res.get("xiang"));
        SysArea cun = map.get(res.get("cun"));

        res.put("sheng", null);
        res.put("xian", null);
        res.put("xiang", null);
        res.put("cun", null);
        if (province != null) {
            res.put("sheng", province.getAreaName());
        }
        if (city != null) {
            res.put("xian", city.getAreaName());
        }
        if (county != null) {
            res.put("xiang", county.getAreaName());
        }
        if (cun != null) {
            res.put("cun", cun.getAreaName());
        }
    }


    /**
     * @param req
     * @return 图例统计
     */
    public List<ForestLayerLegendRes> forestLayerLegendRes(ForestLayerLegendReq req) {
        LayerInfo layerInfo = layerInfoMapper.selectById(req.getLayerId());
        if (layerInfo == null) {
            throw new CustomException("请选择图层");
        }
        List<SysGisFieldsDTO> fields = pgLayerCommonMapper.getLayerTypeFields(layerInfo.getLayerTypeSimple());
        List<String> codes = StreamUtils.toList(fields, SysGisFieldsDTO::getCode);
        String fileCode = null;
        if (StringUtils.isNotEmpty(req.getCode()) && codes.contains(req.getCode())){
            fileCode = req.getCode();
        }
        //兼容新版本
        if (StringUtils.isEmpty(fileCode)){
            SpecialSubjectEnums enums = SpecialSubjectEnums.get(req.getType());
            if (enums != null) {
                fileCode = enums.getField();
            }
        }
        if (StringUtils.isEmpty(fileCode)){
            throw new CustomException("图例统计字段不存在");
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        List<ForestLayerLegendRes> res = slzyCeshitableMapper.forestLayerLegendRes(layerInfo.getLayerName(), fileCode);
        //平方米和亩的计算比例
        double m = 0.0015D;
        double totalCount = res.stream().map(ForestLayerLegendRes::getTotalMj).mapToDouble(value -> value).sum();
        double totalMj = BigDecimalUtils.multiply(totalCount, m);
        List<ForestLayerLegendRes> list = res.stream().peek(result -> {
            if (result.getTotalMj() != null) {
                Double mj = BigDecimalUtils.multiply(result.getTotalMj(), m);
                result.setTotalMj(Double.parseDouble(decimalFormat.format(mj)));
                if (req.getNeedRatio() == 1) {
                    result.setRatio(BigDecimalUtils.longToString100(BigDecimalUtils.multiply(BigDecimalUtils.round(mj, totalMj), BigDecimalUtils.DATA_10000), 2));
                }
            }
        }).collect(Collectors.toList());
        //降序排列
        List<ForestLayerLegendRes> sorted = StreamUtils.sorted(list, Comparator.comparing(ForestLayerLegendRes::getTotalMj, Comparator.reverseOrder()));
        return sorted;
    }

    /**
     * @param req
     * @return 小班详情
     */
    public Map<String, Object> forestLayerXmInfo(ForestLayerXmInfoReq req) {
        LayerInfo layerInfo = layerInfoMapper.selectById(req.getLayerId());
        if (layerInfo == null) {
            throw new CustomException("参数错误");
        }

        List<SysGisFieldsDTO> fields = pgLayerCommonMapper.getLayerTypeFields(layerInfo.getLayerTypeSimple());
        slzyFiledSupplement(fields);
        String allFields = StreamUtils.join(fields, SysGisFieldsDTO::getCode, ",");

        Map<String, Object> infoRes = slzyCeshitableMapper.forestLayerXmInfo(layerInfo.getLayerName(), req.getGid(), req.getBsm(), allFields);
        if (infoRes == null) {
            return Maps.newHashMap();
        }
        List<String> areas = new ArrayList<>();
        perfectAreas(infoRes, areas);
        //查询区域数据
        List<SysArea> sysAreas = gisMapper.selectByAreaCodes(areas);
        if (!sysAreas.isEmpty()) {
            Map<String, SysArea> map = StreamUtils.toMap(sysAreas, SysArea::getAreaCode, area -> area);
            perfectForestLayerXmRes(infoRes, map);
        }
        infoRes.put("geometry", JSON.parseObject(infoRes.get("geometry").toString()));
        infoRes.put("layerId", req.getLayerId());
        return infoRes;
    }

    /**
     * 探而未采图层计算
     */
    public LayerInfoAddDTO calculateLayer(MineralsLayerReq req) {
        String layerId = IdUtils.fastSimpleUUID();
        long nowTime = System.currentTimeMillis();
        String pgTableName = "polygon_" + PGLayerTempEnums.TEWC.getLayerType() + "_" + nowTime + "_" + SmsUtils.generateDynamic().toLowerCase();

        //当前计算的探而未采
        String nowTewcLayer = req.getExploreLayerName() + "," + req.getCollectingLayerName();
        //计算redis ID
        String layerTc = "layer_tc_v2";
        String key = layerTc + ":" + req.getTenantId() + ":" + req.getIndustryCode();
        String tewcLatestKey = layerTc + ":" + req.getTenantId() + ":" + req.getIndustryCode() + ":tewc";

        RedisUtil.set(key, nowTewcLayer, RedisDBType.PRIMARY);
        RedisUtil.expire(key, 3, TimeUnit.HOURS, RedisDBType.PRIMARY);

        RedisUtil.incrBy(layerTc,1,RedisDBType.PRIMARY);

        log.info("{} 生成探而未采图层名称 {}", req.getTenantId(), pgTableName);
        //创建新的探而未采 图层
        pgLayerCommonMapper.createTableName(req.getExploreLayerName(), pgTableName);

        //查询字段
        List<SysGisFieldsDTO> fields = pgLayerCommonMapper.getLayerTypeFields(PGLayerTempEnums.TKQ.getLayerType());
        StringBuilder insertFields = new StringBuilder();
        String[] split = FIXED_FIELDS.split(",");
        fields.forEach(field -> {
            boolean b = Arrays.stream(split).distinct().anyMatch(s -> s.equalsIgnoreCase(field.getCode()));
            if (!b) {
                insertFields.append(field.getCode()).append(",");
            }
        });
        if (insertFields.length() >= 1){
            insertFields.deleteCharAt(insertFields.length() - 1);
        }
        pgLayerCommonMapper.calculateLayer(req.getExploreLayerName(), req.getCollectingLayerName(), pgTableName, insertFields.toString());

        //计算面积
        String areaCode = pgLayerCommonMapper.getLayerAreaField(PGLayerTempEnums.TKQ.getLayerType());

        pgLayerCommonMapper.updateCalarea(pgTableName);
        //计算探而未采面积
        pgLayerCommonMapper.updateTewcCalarea(pgTableName, req.getExploreLayerName(), areaCode);

        Double layerArea = 0D;
        if (StringUtils.isNotEmpty(areaCode)) {
            //图层总面积
            layerArea = pgLayerCommonMapper.getLayerArea(pgTableName, areaCode);
        }
        String tkqAndCkq = RedisUtil.get(key, RedisDBType.PRIMARY);

        String tcCount = RedisUtil.get(layerTc, RedisDBType.PRIMARY);
        if (StringUtils.isNotEmpty(tcCount)) {
            if (Integer.parseInt(tcCount) <= 1) {
                RedisUtil.delete(layerTc, RedisDBType.PRIMARY);
            } else {
                RedisUtil.set(layerTc, Integer.parseInt(tcCount) - 1, RedisDBType.PRIMARY);
            }
        }

        if (nowTewcLayer.equals(tkqAndCkq)) {
            LayerInfo info = new LayerInfo();
            info.setLayerId(layerId);
            info.setDeleteFlag(false);
            info.setLayerName(pgTableName);
            info.setCreator(req.getCreateBy());
            info.setTenantId(req.getTenantId());
            info.setIndustryCode(req.getIndustryCode());
            info.setGeoType(PgLayerStyleEnums.MULTIPOLYGON.getName());
            info.setLayerTypeSimple(PGLayerTempEnums.TEWC.getLayerType());
            info.setLayerTypeName(PGLayerTempEnums.TEWC.getRemark());
            info.setCreateTime(new Date());
            layerInfoMapper.insert(info);

            LayerInfoAddDTO copy = BeanCopyUtils.copy(info, LayerInfoAddDTO.class);
            copy.setPgTableName(pgTableName);
            copy.setCreateBy(req.getCreateBy());
            copy.setLayerArea(layerArea);
            copy.setPolygonCount(layerInfoMapper.getCount(pgTableName));
            return copy;
        }
        if (req.getCount() <= 5 || RedisUtil.hasKey(tewcLatestKey, RedisDBType.PRIMARY)){
            //删除pg
            layerInfoMapper.deleteTables(Collections.singletonList(pgTableName));

            String tkq = null;
            String ckq = null;

            String s = RedisUtil.get(tewcLatestKey, RedisDBType.PRIMARY);
            if (StringUtils.isNotEmpty(s)){
                MineralsLayerReqVO vo = JSON.parseObject(s, MineralsLayerReqVO.class);
                tkq = vo.getTkq();
                ckq = vo.getCkq();
                RedisUtil.delete(tewcLatestKey, RedisDBType.PRIMARY);
            }

            //是否存在探矿权和采矿权
            if (StringUtils.isEmpty(tkq) || StringUtils.isEmpty(ckq)) {
                throw new CustomException("图层丢失");
            }
            req.setExploreLayerName(tkq);
            req.setCollectingLayerName(ckq);
            req.setCount(req.getCount() + 1);
            return calculateLayer(req);
        }
        RedisUtil.delete(key, RedisDBType.PRIMARY);
        throw new CustomException("计算失败");
    }
}
