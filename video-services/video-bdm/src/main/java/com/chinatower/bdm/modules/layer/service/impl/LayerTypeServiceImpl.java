package com.chinatower.bdm.modules.layer.service.impl;

import com.alibaba.fastjson.JSON;
import com.chinatower.bdm.api.entity.ListByConditionVO;
import com.chinatower.bdm.common.Constants;
import com.chinatower.bdm.modules.layer.domain.Layer;
import com.chinatower.bdm.modules.layer.domain.LayerType;
import com.chinatower.bdm.modules.layer.domain.LayerTypeAttrFilter;
import com.chinatower.bdm.modules.layer.domain.LayerTypeFields;
import com.chinatower.bdm.modules.layer.mapper.LayerMapper;
import com.chinatower.bdm.modules.layer.mapper.LayerTypeAttrFilterMapper;
import com.chinatower.bdm.modules.layer.mapper.LayerTypeFieldsMapper;
import com.chinatower.bdm.modules.layer.mapper.LayerTypeMapper;
import com.chinatower.bdm.modules.layer.service.ILayerTypeService;
import com.chinatower.bdm.modules.layer.service.LayerTypeFieldsService;
import com.chinatower.common.core.utils.IdUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.entity.admin.SysDictData;
import com.chinatower.common.core.dto.bdm.*;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.enums.SysLayerTypeFieldsTypeEnums;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.BeanCopyUtils;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.platform.utils.StreamUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 图层类别管理 服务实现类
 * </p>
 *
 * @author ch
 * @since 2023-04-06
 */
@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("all")
public class LayerTypeServiceImpl implements ILayerTypeService {

    private final LayerTypeMapper layerTypeMapper;
    private final LayerTypeFieldsService layerTypeFieldsService;
    private final LayerTypeFieldsMapper layerTypeFieldsMapper;
    private final LayerTypeAttrFilterMapper layerTypeAttrFilterMapper;
    private final LayerMapper layerMapper;
    private final SystemFeignClient systemFeignClient;

    //字典初始化数据
    private static final String SYS_DICT_DATA_TYPE_NAME = "sys_layer_type_name";

    private static final String ERROR_LAYER_NAME = "图层类型已存在，请重新输入！";
    private static final String INDEX_ERROR = "有多个字段，请检查后操作!";
    //字段code正则
    private static final String CODE_PATTERN = "^[A-Za-z_$][0-9A-Za-z_$]{0,63}";
    private static final String TABLE_PATTERN = "^[0-9A-Za-z_]+";


    @Override
    public Result<List<LayerTypeDTO>> list(String typeName) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        List<LayerType> list = getTypeNameList(typeName);
        List<LayerTypeDTO> vos = BeanCopyUtils.copyList(list, LayerTypeDTO.class);
        return Result.data(vos);
    }

    @Override
    public List<LayerTypeListVO> listByCondition(ListByConditionVO req) {

        LoginUser loginUser = SecurityUtils.getLoginUser();

        String orderBy = req.getOrderBy();
        if (StringUtils.isNotEmpty(orderBy)) {
            if (!"createTime".equals(req.getOrderByColumn()) && !"updateTime".equals(req.getOrderByColumn())) {
                throw new CustomException("参数错误");
            }
            if (!"asc".equals(req.getIsAsc()) && !"desc".equals(req.getIsAsc())) {
                throw new CustomException("参数错误");
            }
        }

        List<LayerTypeListVO> list = layerTypeMapper.selectListByCondition(loginUser.getUser().getTenantId(), loginUser.getUser().getIndustryCode(), orderBy, req);
        return list;
    }



    @Override
    public Result<String> createQtTypeInfo() {

        /* 复原表的sql
        * delete from sys_layer_type;
        * insert into sys_layer_type select * from sys_layer_type_20240124145239603;
        * delete from sys_layer_type_fields;
        * insert into sys_layer_type_fields select * from sys_layer_type_fields_20240124145239603;
        * */

        StopWatch st = new StopWatch("创建“其他类型”数据类型的表信息");
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String formattedDateTime = now.format(formatter);
        String typetableback = "sys_layer_type_" + formattedDateTime;
        String typefieldstableback = "sys_layer_type_fields_" + formattedDateTime;

//        String sql = "";
//        sql = String.format("create table %s as select * from sys_layer_type;", typetableback); // 备份 sys_layer_type
        st.start("备份 sys_layer_type 为 " + typetableback);
//        layerTypeMapper.executeSql(sql);
        layerTypeMapper.createTypetableback(typetableback);
        st.stop();

//        sql = String.format("create table %s as select * from sys_layer_type_fields;", typefieldstableback); // 备份 sys_layer_type_fields
        st.start("备份 sys_layer_type_fields 为 " + typefieldstableback);
//        layerTypeMapper.executeSql(sql);
        layerTypeMapper.createTypefieldstableback(typefieldstableback);
        st.stop();

//        sql = "SELECT DISTINCT abbreviation\n" +
//                "FROM\n" +
//                "    sys_layer_type\n" +
//                "WHERE\n" +
//                "    abbreviation NOT IN ('xztb' , 'yjjbnt', 'stbhhx', 'jsydgzq', 'tdbp', 'slzy')\n" +
//                "\tOR abbreviation IS NOT NULL\n" +
//                "ORDER BY abbreviation";        // 查询其他类型
//
//        StringBuilder sbSql = new StringBuilder();
//        List<String> abbs = layerTypeMapper.selectSql(sql);
        List<String> abbs = layerTypeMapper.selectAbbreviationSql();
//        st.start("组装更新sql语句");
//        for (int i=0; i<abbs.size(); i++) {
//            String abb = abbs.get(i);
//            // 其他类型地块	qtlxdk
//            sbSql.append(String.format("update sys_layer_type set \n" +
//                    "\tadd_other_field_values_flag = 1,\t\t-- 是否记录未填写的字段\n" +
//                    "\tsuper_type_name = null,\t\t\t\t\t-- 父类类型名称\n" +
//                    "\tinitial_type_name = null,\t\t\t\t-- 祖类类型名称\n" +
//                    "\tnot_covered_label = null,\t-- 未覆盖显示的标签名（套合）\n" +
//                    "\tcovered_label = null,\t\t\t-- 覆盖显示的标签名（套合）\n" +
//                    "\tcovered_field_code = null,\t\t\t\t-- 覆盖分类字段code（套合）\n" +
//                    "\tunique_field_code = 'bsm',\t\t\t\t-- 唯一值的字段名（套合）\n" +
//                    "\tarea_ref = null,\t\t\t\t-- 业务面积字段名\n" +
//                    "\tlabel = 'bsm',\t\t\t\t\t\t\t-- 属性显示名称（套合）\n" +
//                    "\ttbbh_ref = 'bsm'\t\t\t\t\t\t-- 指定图斑编号的替代字段\n" +
//                    "where abbreviation = '%s';", abb));
//            // 1	标识码	BSM	character varying		50		>0	O	支持模糊查询
//            sbSql.append(String.format("INSERT INTO sys_layer_type_fields \n" +
//                    "\t(id, abbreviation, code, name, remark, type, chart_length, not_null_flag, filter_type, filter_index, identify_index, result_index, delete_flag) VALUES (\n" +
//                    "\tUUID(),\t\t\t\t-- id\n" +
//                    "\t'%s',\t\t\t-- abbreviation 图层类型简称\n" +
//                    "\t'bsm',\t\t\t\t-- code 字段代码\n" +
//                    "\t'标识码',\t\t\t-- name 字段显示名称\n" +
//                    "\t'标识码字段信息，值来自于“地块编号”字段',\t-- remark 备注，说明\n" +
//                    "\t3,\t\t\t\t\t-- type 类型 1:int 2:double 3:varchart 4:timestamp\n" +
//                    "\t50,\t\t\t\t\t-- chart_length 字符类型长度\n" +
//                    "\t1,  \t\t\t\t-- not_null_flag 是否是不能为空的字段（0 代表 false，也就可以为空; 1 代表 true，不能为空)'\n" +
//                    "\t0,  \t\t\t\t-- filter_type 筛选类型（0文本，1下拉，默认：2空）\n" +
//                    "\t1,  \t\t\t\t-- filter_index 属性查询功能的顺序号（默认为0，起始为1）\n" +
//                    "\t1,\t\t\t\t\t-- identify_index 识别功能的顺序号\n" +
//                    "\t1,\t\t\t\t\t-- result_index 结果列表的顺序号\n" +
//                    "\t0\t\t\t\t\t-- delete_flag 删除状态（0 代表 false，未删除; 1 代表 true，已删除)\n" +
//                    ");", abb));
//            // 2	要素代码	ysdm	character varying		20			O	支持模糊查询
//            sbSql.append(String.format("INSERT INTO sys_layer_type_fields \n" +
//                    "\t(id, abbreviation, code, name, remark, type, chart_length, not_null_flag, filter_type, filter_index, identify_index, result_index, delete_flag) VALUES (\n" +
//                    "\tUUID(),\t\t\t\t-- id\n" +
//                    "\t'%s',\t\t\t-- abbreviation 图层类型简称\n" +
//                    "\t'ysdm',\t\t\t\t-- code 字段代码\n" +
//                    "\t'要素代码',\t\t\t-- name 字段显示名称\n" +
//                    "\t'要素代码字段信息',\t-- remark 备注，说明\n" +
//                    "\t3,\t\t\t\t\t-- type 类型 1:int 2:double 3:varchart 4:timestamp\n" +
//                    "\t20,\t\t\t\t\t-- chart_length 字符类型长度\n" +
//                    "\t0,  \t\t\t\t-- not_null_flag 是否是不能为空的字段（0 代表 false，也就可以为空; 1 代表 true，不能为空)'\n" +
//                    "\t0,  \t\t\t\t-- filter_type 筛选类型（0文本，1下拉，默认：2空）\n" +
//                    "\t2,  \t\t\t\t-- filter_index 属性查询功能的顺序号（默认为0，起始为1）\n" +
//                    "\t0,\t\t\t\t\t-- identify_index 识别功能的顺序号\n" +
//                    "\t2,\t\t\t\t\t-- result_index 结果列表的顺序号\n" +
//                    "\t0\t\t\t\t\t-- delete_flag 删除状态（0 代表 false，未删除; 1 代表 true，已删除)\n" +
//                    ");", abb));
//            // 3	行政区代码	xzqdm	character varying		50			O	支持模糊查询
//            sbSql.append(String.format("INSERT INTO sys_layer_type_fields \n" +
//                    "\t(id, abbreviation, code, name, remark, type, chart_length, not_null_flag, filter_type, filter_index, identify_index, result_index, delete_flag) VALUES (\n" +
//                    "\tUUID(),\t\t\t\t-- id\n" +
//                    "\t'%s',\t\t\t-- abbreviation 图层类型简称\n" +
//                    "\t'xzqdm',\t\t\t\t-- code 字段代码\n" +
//                    "\t'行政区代码',\t\t\t-- name 字段显示名称\n" +
//                    "\t'行政区代码字段信息',\t-- remark 备注，说明\n" +
//                    "\t3,\t\t\t\t\t-- type 类型 1:int 2:double 3:varchart 4:timestamp\n" +
//                    "\t50,\t\t\t\t\t-- chart_length 字符类型长度\n" +
//                    "\t0,  \t\t\t\t-- not_null_flag 是否是不能为空的字段（0 代表 false，也就可以为空; 1 代表 true，不能为空)'\n" +
//                    "\t0,  \t\t\t\t-- filter_type 筛选类型（0文本，1下拉，默认：2空）\n" +
//                    "\t3,  \t\t\t\t-- filter_index 属性查询功能的顺序号（默认为0，起始为1）\n" +
//                    "\t0,\t\t\t\t\t-- identify_index 识别功能的顺序号\n" +
//                    "\t3,\t\t\t\t\t-- result_index 结果列表的顺序号\n" +
//                    "\t0\t\t\t\t\t-- delete_flag 删除状态（0 代表 false，未删除; 1 代表 true，已删除)\n" +
//                    ");", abb));
//            // 4	属性json字符串	properties	text					M
//            sbSql.append(String.format("INSERT INTO sys_layer_type_fields \n" +
//                    "\t(id, abbreviation, code, name, remark, type, chart_length, not_null_flag, filter_type, filter_index, identify_index, result_index, delete_flag) VALUES (\n" +
//                    "\tUUID(),\t\t\t\t-- id\n" +
//                    "\t'%s',\t\t\t-- abbreviation 图层类型简称\n" +
//                    "\t'properties',\t\t\t\t-- code 字段代码\n" +
//                    "\t'属性json字符串',\t\t\t-- name 字段显示名称\n" +
//                    "\t'属性json字符串字段信息，固有字段，不支持类型编辑，列举出来仅为结果列表排序使用',\t-- remark 备注，说明\n" +
//                    "\t0,\t\t\t\t\t-- type 类型 1:int 2:double 3:varchart 4:timestamp\n" +
//                    "\t0,\t\t\t\t\t-- chart_length 字符类型长度\n" +
//                    "\t0,  \t\t\t\t-- not_null_flag 是否是不能为空的字段（0 代表 false，也就可以为空; 1 代表 true，不能为空)'\n" +
//                    "\t0,  \t\t\t\t-- filter_type 筛选类型（0文本，1下拉，默认：2空）\n" +
//                    "\t0,  \t\t\t\t-- filter_index 属性查询功能的顺序号（默认为0，起始为1）\n" +
//                    "\t0,\t\t\t\t\t-- identify_index 识别功能的顺序号\n" +
//                    "\t4,\t\t\t\t\t-- result_index 结果列表的顺序号\n" +
//                    "\t0\t\t\t\t\t-- delete_flag 删除状态（0 代表 false，未删除; 1 代表 true，已删除)\n" +
//                    ");", abb));
//        }
//
//        String sqls = sbSql.toString();
//        st.stop();

        st.start("批量插入临时表");
//        layerTypeMapper.insertSql(sqls);
        layerTypeMapper.insertAbbreviation(abbs);
        st.stop();

        System.out.println(st.prettyPrint());
        return Result.success("创建'其他类型'数据类型的表信息成功！");
    }

    @Override
    public List<LayerTypeAttrFilterVO> dictByAbbreviation(String abbreviation) {
        List<LayerTypeAttrFilterVO> dtos = layerTypeAttrFilterMapper.selectVOByAbbreviation(abbreviation);
        return dtos;
    }

    /**
     * @param typeName 类型名称
     * @return 图层类型列表
     */
    private List<LayerType> getTypeNameList(String typeName) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        LayerType type = new LayerType();
        if (StringUtils.isNotEmpty(loginUser.getUser().getTenantId())) {
            type.setTenantId(loginUser.getUser().getTenantId());
        }
        if (StringUtils.isNotEmpty(loginUser.getUser().getIndustryCode())) {
            type.setIndustryCode(loginUser.getUser().getIndustryCode());
        }
        if (StringUtils.isNotEmpty(typeName)) {
            type.setTypeName(typeName);
        }
        return layerTypeMapper.selectList(type);
    }

    /**
     * @param vos 参数
     * @return 新增修改
     */
    @Override
    @Transactional
    public Result<String> addLayerTypes(List<LayerTypeDTO> vos) {
        log.info("新增图层类型数据 \r\n {}", JSON.toJSONString(vos));
        LoginUser loginUser = SecurityUtils.getLoginUser();
        long time = System.currentTimeMillis();
        if (vos == null || vos.isEmpty()) {
            return Result.fail("数据不能为空");
        }
        vos.forEach(vo -> {
            if (StringUtils.isEmpty(vo.getTypeName())) {
                throw new CustomException("图层类型名称不能为空");
            }
            if (vo.getTypeName().trim().length() > 20) {
                throw new CustomException("图层类型名称限制最多支持输入20个文字");
            }
        });
        List<LayerType> dtos = BeanCopyUtils.copyList(vos, LayerType.class);

        //需要修改的数据
        assert dtos != null;
        List<String> typeNames = dtos.stream().map(LayerType::getTypeName).collect(Collectors.toList());
        HashSet<String> set = new HashSet<>(typeNames);
        if (typeNames.size() != set.size()) {
            return Result.fail(ERROR_LAYER_NAME);
        }


        log.info("新增图层类型基础校验 {}",(System.currentTimeMillis() - time));
        //填充新增类型数据
        List<LayerTypeDTO> addListDTOS = vos.stream().filter(dto -> StringUtils.isEmpty(dto.getId())).collect(Collectors.toList());
        //新增的图层类型名称
        List<String> addLayerTypeNames = StreamUtils.toList(addListDTOS, LayerTypeDTO::getTypeName);
        if (!addLayerTypeNames.isEmpty()){
            Integer count = layerTypeMapper.selectCount(loginUser.getUser().getTenantId(), loginUser.getUser().getIndustryCode(), addLayerTypeNames);
            if (count > 0) {
                return Result.fail(ERROR_LAYER_NAME);
            }
        }


        List<AddOrModifyLayerTypeFieldsVO> addFields = new ArrayList<>();
        List<LayerTypeAttrFilter> addFilters = new ArrayList<>();
        List<LayerType> addLayerTypes = new ArrayList<>();

        List<String> filedIds = new ArrayList<>();
        List<String> filterIds = new ArrayList<>();

        if (!addListDTOS.isEmpty()) {
            fillLayerTypes(addFields, addLayerTypes, addFilters, filedIds, filterIds, addListDTOS);
        }
        log.info("新增图层类型新增数据填充 {}",(System.currentTimeMillis() - time));

        //需要修图的类型数据
        List<LayerTypeDTO> modifyListDTOS = vos.stream().filter(dto -> StringUtils.isNotEmpty(dto.getId())).collect(Collectors.toList());
        List<AddOrModifyLayerTypeFieldsVO> modifyFields = new ArrayList<>();
        List<LayerTypeAttrFilter> modifyFilters = new ArrayList<>();
        List<LayerType> modifyLayerTypes = new ArrayList<>();
        if (!modifyListDTOS.isEmpty()) {
            List<String> typeIds = modifyListDTOS.stream().map(LayerTypeDTO::getId).collect(Collectors.toList());
            List<LayerType> types = layerTypeMapper.selectByIds(typeIds);
            //类型ID     简称
            Map<String, LayerType> map = StreamUtils.toMap(types, LayerType::getId, s -> s);
            //修改的图层类型切且名称也修改了的
            List<String> modifyTypeNames = new ArrayList<>();
            //设置类型简称
            modifyListDTOS.forEach(layerType -> {
                LayerType type = map.get(layerType.getId());
                if (!modifyTypeNames.contains(layerType.getTypeName()) && !type.getTypeName().equals(layerType.getTypeName())){
                    modifyTypeNames.add(layerType.getTypeName());
                }
                if (type == null || !loginUser.getUser().getTenantId().equals(type.getTenantId()) || !loginUser.getUser().getIndustryCode().equals(type.getIndustryCode())) {
                    throw new CustomException("图层类型错误");
                }
                layerType.setAbbreviation(type.getAbbreviation());
                layerType.setCategory(type.getCategory());
            });
            if (!modifyTypeNames.isEmpty()){
                //判断修改图层类型名称是否有重复
                Integer modifyTypeNamesCount = layerTypeMapper.selectCount(loginUser.getUser().getTenantId(), loginUser.getUser().getIndustryCode(), modifyTypeNames);
                if (modifyTypeNamesCount > 0) {
                    return Result.fail(ERROR_LAYER_NAME);
                }
            }

            //填充数据
            fillLayerTypes(modifyFields, modifyLayerTypes, modifyFilters, filedIds, filterIds, modifyListDTOS);
        }
        log.info("新增图层类型修改数据填充 {}",(System.currentTimeMillis() - time));


        if (!addLayerTypes.isEmpty()) {
            //生成图层类型简称,规则为：名称拼音首字母，如果重复序号+1
            layerTypeMapper.saveBatch(addLayerTypes);
            if (!addFields.isEmpty()) {
                //新增字段
                List<LayerTypeFields> fields = BeanCopyUtils.copyList(addFields, LayerTypeFields.class);
                layerTypeFieldsMapper.saveBatch(fields);
            }
            if (!addFilters.isEmpty()) {
                layerTypeAttrFilterMapper.saveBatch(addFilters);
            }
        }
        log.info("新增图层类型新增数据入库 {}",(System.currentTimeMillis() - time));
        if (!modifyLayerTypes.isEmpty()) {
            //生成图层类型简称,规则为：名称拼音首字母，如果重复序号+1
            layerTypeMapper.updateBatchById(modifyLayerTypes);

            //修改字段值
            if (!modifyFields.isEmpty()) {
                List<LayerTypeFields> fields = BeanCopyUtils.copyList(modifyFields, LayerTypeFields.class);
                if (!filedIds.isEmpty()) {
                    layerTypeFieldsMapper.deleteByIds(filedIds);
                }
                layerTypeFieldsMapper.saveBatch(fields);
            }
            //修改字典值
            if (!modifyFilters.isEmpty()) {
                if (!filterIds.isEmpty()) {
                    layerTypeAttrFilterMapper.deleteByIds(filterIds);
                }
                layerTypeAttrFilterMapper.saveBatch(modifyFilters);
            }
        }
        log.info("新增图层类型修改数据入库 {}",(System.currentTimeMillis() - time));
        return Result.success("图层类型配置成功");
    }

    @Override
    public Result<String> layerTypeNamesRepeat(List<LayerTypeNamesRepeatVO> vos) {
        LoginUser loginUser = SecurityUtils.getLoginUser();

        if (vos == null || vos.isEmpty()) {
            return Result.fail("数据不能为空");
        }
        vos.forEach(vo -> {
            if (StringUtils.isEmpty(vo.getTypeName())) {
                throw new CustomException("图层名称不能为空");
            }
            if (vo.getTypeName().trim().length() > 20) {
                throw new CustomException("图层类型名称限制最多支持输入20个文字");
            }
        });
        //需要修改的数据
        List<String> typeNamesSet = vos.stream().map(LayerTypeNamesRepeatVO::getTypeName).collect(Collectors.toList());
        HashSet<String> set = new HashSet<>(typeNamesSet);
        if (typeNamesSet.size() != set.size()) {
            return Result.fail(ERROR_LAYER_NAME);
        }

        List<LayerTypeNamesRepeatVO> modifyTypes = vos.stream().filter(vo -> StringUtils.isNotEmpty(vo.getId())).collect(Collectors.toList());
        List<LayerTypeNamesRepeatVO> addTypes = vos.stream().filter(vo -> StringUtils.isEmpty(vo.getId())).collect(Collectors.toList());
        //新增的图层类型名称
        List<String> typeNames = StreamUtils.toList(addTypes, LayerTypeNamesRepeatVO::getTypeName);

        if (!modifyTypes.isEmpty()){
            List<String> typeIds = modifyTypes.stream().map(LayerTypeNamesRepeatVO::getId).collect(Collectors.toList());
            List<LayerType> types = layerTypeMapper.selectByIds(typeIds);
            if (types.isEmpty()){
                return Result.fail("参数错误");
            }
            //修改的图层类型切且名称也修改了的
            modifyTypes.forEach(layerType -> {
                //类型ID     简称
                Map<String, LayerType> map = StreamUtils.toMap(types, LayerType::getId, s -> s);
                LayerType type = map.get(layerType.getId());
                if (!typeNames.contains(layerType.getTypeName()) && !type.getTypeName().equals(layerType.getTypeName())){
                    typeNames.add(layerType.getTypeName());
                }
            });

            if (!typeNames.isEmpty()){
                //判断修改图层类型名称是否有重复
                Integer count = layerTypeMapper.selectCount(loginUser.getUser().getTenantId(), loginUser.getUser().getIndustryCode(), typeNames);
                if (count > 0) {
                    return Result.fail(ERROR_LAYER_NAME);
                }
            }
        }
        return Result.success("");
    }


    /**
     * 填充类型数据
     */
    private void fillLayerTypes(List<AddOrModifyLayerTypeFieldsVO> addFields,
                                List<LayerType> addLayerTypes,
                                List<LayerTypeAttrFilter> addFilters,
                                List<String> fieldIds,
                                List<String> filterIds,
                                List<LayerTypeDTO> typeDTOS) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        typeDTOS.forEach(dto -> {
            LayerType vo = new LayerType();
            vo.setId(dto.getId());
            vo.setTypeName(dto.getTypeName());
            LayerType layerType = layerTypeMapper.selectByAbbreviation(dto.getSuperTypeName(), null, null);
            if (layerType != null){
                vo.setSuperTypeName(layerType.getAbbreviation());
                vo.setInitialTypeName(layerType.getInitialTypeName());
                //如果是第一次复制则第一级为当前类型
                if (StringUtils.isEmpty(layerType.getInitialTypeName())){
                    vo.setInitialTypeName(layerType.getAbbreviation());
                }
            }
            //修改不能改变类型简称
            if (StringUtils.isEmpty(vo.getId())) {
                setAbbreviation(vo);
                vo.setCategory(1);
            } else {
                vo.setAbbreviation(dto.getAbbreviation());
                vo.setCategory(dto.getCategory());
            }
            if (dto.getCategory() != null && dto.getCategory() == 0 && VideoSystemEnums.UserType.TENANT_USER.getIndex().equals(loginUser.getUser().getUserType())){
                throw new CustomException("不能操作初始化图层类型");
            }
            //类型字段数据
            List<AddOrModifyLayerTypeFieldsVO> fields = dto.getFields();

            //图层类型字段校验
            layerTypeFiledVerification(vo, fields, dto);

            if (fields != null && !fields.isEmpty()) {
                //需要修改的字段值
                List<String> deleteFieldsIds = fields.stream().filter(field -> StringUtils.isNotEmpty(field.getId())).map(AddOrModifyLayerTypeFieldsVO::getId).collect(Collectors.toList());
                //需要删除的字段
                fieldIds.addAll(deleteFieldsIds);
                fieldIds.addAll(dto.getIds());
                if (StringUtils.isNotEmpty(vo.getId())) {
                    List<String> codes = fields.stream().map(AddOrModifyLayerTypeFieldsVO::getCode).collect(Collectors.toList());
                    if (!codes.isEmpty()) {
                        List<LayerTypeFields> ids = layerTypeFieldsMapper.selectByFieldCodes(vo.getAbbreviation(), codes);
                        fieldIds.addAll(StreamUtils.toList(ids, LayerTypeFields::getId));
                    }

                }

                //设置面积字段
                Optional<String> firstArea = fields.stream().filter(f -> f.getHasArea() == 1).map(AddOrModifyLayerTypeFieldsVO::getCode).findFirst();
                firstArea.ifPresent(vo::setAreaRef);
                if (StringUtils.isEmpty(vo.getAreaRef())){
                    vo.setAreaRef("calarea");
                }
                //设置图斑编号字段
                Optional<String> firstTbbh = fields.stream().filter(f -> f.getHasTbbh() != null && f.getHasTbbh() == 1).map(AddOrModifyLayerTypeFieldsVO::getCode).findFirst();
                firstTbbh.ifPresent(vo::setTbbhRef);
                if (StringUtils.isEmpty(vo.getTbbhRef())){
                    vo.setTbbhRef("bsm");
                }

                //查询字段字典
                List<LayerTypeAttrFilter> sourceFilter = layerTypeAttrFilterMapper.selectByAbbreviation(vo.getAbbreviation());
                Map<String, Map<String, List<LayerTypeAttrFilter>>> group2Map = StreamUtils.groupBy2Key(sourceFilter, LayerTypeAttrFilter::getFilterType, LayerTypeAttrFilter::getFilterAttrKey);

                //设置字段类型简称简称
                fields.forEach(field -> {
                    //需要删除的字段字典ID 数据
                    filterIds.addAll(field.getIds());
                    //字段校验
                    layerTypeFiledVerification(field);

                    field.setAbbreviation(vo.getAbbreviation());
                    field.setId(IdUtils.fastSimpleUUID());
                    field.setCreateTime(new Date());
                    field.setCreator(loginUser.getUsername());
                    field.setUpdateTime(new Date());
                    field.setModifier(loginUser.getUsername());

                    List<LayerTypeAttrFilterVO> filters = field.getFilters();
                    if (filters != null && !filters.isEmpty()) {
                        //需要删除的字段字典数据
                        List<LayerTypeAttrFilter> attrFilters = BeanCopyUtils.copyList(filters, LayerTypeAttrFilter.class);
                        attrFilters = StreamUtils.filter(attrFilters,f -> StringUtils.isNotEmpty(f.getFilterAttrKey()));

                        Map<String, List<LayerTypeAttrFilter>> filterMap = group2Map.get(field.getCode());
                        for (LayerTypeAttrFilter f : attrFilters) {
                            //判断字段是否有重复的key
                            if (filterMap != null && !filterMap.isEmpty()) {
                                List<LayerTypeAttrFilter> layerTypeAttrFilters = filterMap.get(f.getFilterAttrKey());
                                if (layerTypeAttrFilters != null && !layerTypeAttrFilters.isEmpty()) {
                                    filterIds.addAll(StreamUtils.toList(layerTypeAttrFilters, LayerTypeAttrFilter::getId));
                                }
                            }
                            f.setFilterType(field.getCode());
                            f.setAbbreviation(vo.getAbbreviation());
                            f.setCreateTime(new Date());
                            f.setCreator(loginUser.getUser().getUserId());
                            f.setDeleteFlag(0);
                            f.setShowType(f.getShowType());
                            f.setIsShow("1");
                        }
                        //需要修改的字段筛选值
                        List<String> deleteFilterIds = attrFilters.stream().filter(filter -> StringUtils.isNotEmpty(filter.getId())).map(LayerTypeAttrFilter::getId).collect(Collectors.toList());
                        filterIds.addAll(deleteFilterIds);
                        //字段字典值
                        addFilters.addAll(attrFilters);
                    } else {
                        if (field.getMatchDictFlag() == 1) {
                            throw new CustomException("[ " + field.getName() + " ]请添加字典项");
                        }
                    }
                });
                addFields.addAll(fields);
            }


            //图层数据填充
            if (StringUtils.isEmpty(vo.getId())) {
                if (StringUtils.isEmpty(vo.getInitialTypeName()) && StringUtils.isNotEmpty(vo.getSuperTypeName())){
                    vo.setInitialTypeName(vo.getSuperTypeName());
                }
                vo.setId(IdUtils.fastSimpleUUID());
                vo.setCreateTime(new Date());
                vo.setCreator(loginUser.getUser().getUserName());
                vo.setTenantId(loginUser.getUser().getTenantId());
                vo.setAppVerCode(loginUser.getUser().getAppVerCode());
                vo.setDeleteFlag(0);
                vo.setIndustryCode(loginUser.getUser().getIndustryCode());
                vo.setCategory(1);
            } else {
                vo.setUpdateTime(new Date());
                vo.setModifier(loginUser.getUser().getUserName());
            }

            addLayerTypes.add(vo);
        });
    }

    private void layerTypeFiledVerification(LayerType vo, List<AddOrModifyLayerTypeFieldsVO> fields, LayerTypeDTO dto) {

        if (fields != null && !fields.isEmpty()){
            Map<String, List<AddOrModifyLayerTypeFieldsVO>> map = StreamUtils.groupByKey(fields, AddOrModifyLayerTypeFieldsVO::getCode);
            for (String code : map.keySet()) {
                if (map.get(code).size() > 1) {
                    throw new CustomException("【 " + code + " 】 字段重复，请检查后操作!");
                }
            }


            //判断结果列表是否有重复
            Map<Integer, List<AddOrModifyLayerTypeFieldsVO>> fieldsResultIndexList = fields.stream().filter(f -> f.getResultIndex() != null && f.getResultIndex() != 0).collect(Collectors.groupingBy(AddOrModifyLayerTypeFieldsVO::getResultIndex, LinkedHashMap::new, Collectors.toList()));
            Map<Integer, List<AddOrModifyLayerTypeFieldsVO>> fieldsIdentifyIndexList = fields.stream().filter(f -> f.getIdentifyIndex() != null && f.getIdentifyIndex() != 0).collect(Collectors.groupingBy(AddOrModifyLayerTypeFieldsVO::getIdentifyIndex, LinkedHashMap::new, Collectors.toList()));
            Map<Integer, List<AddOrModifyLayerTypeFieldsVO>> fieldsFilterIndexList = fields.stream().filter(f -> f.getFilterIndex() != null && f.getFilterIndex() != 0).collect(Collectors.groupingBy(AddOrModifyLayerTypeFieldsVO::getFilterIndex, LinkedHashMap::new, Collectors.toList()));
            for (Integer index : fieldsResultIndexList.keySet()) {
                if (fieldsResultIndexList.get(index).size() > 1) {
                    throw new CustomException("列表字段排序为 【 " + index + " 】 " + INDEX_ERROR);
                }
            }

            for (Integer index : fieldsIdentifyIndexList.keySet()) {
                if (fieldsIdentifyIndexList.get(index).size() > 1) {
                    throw new CustomException("弹框字段排序为 【 " + index + " 】 " + INDEX_ERROR);
                }
            }

            for (Integer index : fieldsFilterIndexList.keySet()) {
                if (fieldsFilterIndexList.get(index).size() > 1) {
                    throw new CustomException("查询条件排序为 【 " + index + " 】 " + INDEX_ERROR);
                }
            }
        }

        LoginUser loginUser = SecurityUtils.getLoginUser();
        //ID 不为空是修改
        if (StringUtils.isNotEmpty(vo.getId())) {
            //判断该类型是否已有数据
            Integer count = layerMapper.selectLayerTypeIdCount(loginUser.getUser().getTenantId(), loginUser.getUser().getIndustryCode(), vo.getId());
            //已经图层类型字段数据
            List<LayerTypeFields> layerTypeFields = layerTypeFieldsMapper.selectByAbbreviation(vo.getAbbreviation());
            if (layerTypeFields != null && !layerTypeFields.isEmpty()) {
                if (count > 0 && !dto.getIds().isEmpty()) {
                    throw new CustomException("该类型已存在图层数据不能删除字段");
                }
                Map<String, LayerTypeFields> fieldsMap = StreamUtils.toMap(layerTypeFields, LayerTypeFields::getCode, s -> s);
                fields.forEach(field -> {
                    LayerTypeFields layerTypeField = fieldsMap.get(field.getCode());
                    if (count > 0 && (layerTypeField == null || !Objects.equals(layerTypeField.getType(), field.getType()))) {
                        throw new CustomException("该类型已存在图层数据不能新增修改字段");
                    }
                });
            }
        } else {//新增类型必须配置字段
            if (fields == null || fields.isEmpty()) {
                throw new CustomException("[ " + vo.getTypeName() + " ]图层类型字段未配置");
            }
        }
    }

    /**
     * 图层类型字段校验
     */
    private void layerTypeFiledVerification(AddOrModifyLayerTypeFieldsVO field) {

        if (StringUtils.isEmpty(field.getCode()) || !Pattern.matches(CODE_PATTERN, field.getCode())) {
            throw new CustomException("请输入数字、字母、下划线和$符号，但不能以数字开头，也不能使用数据库的保留字，长度不能超过64个字符");
        }
        if (StringUtils.isEmpty(field.getName()) || field.getName().length() > 64) {
            throw new CustomException("不能为空且长度不能超过64个字符");
        }
        if (StringUtils.isNotEmpty(field.getRemark()) && field.getRemark().length() > 200) {
            throw new CustomException("长度不能超过200个字符");
        }
        SysLayerTypeFieldsTypeEnums typeEnums = SysLayerTypeFieldsTypeEnums.get(field.getType());
        if (typeEnums == null) {
            throw new CustomException("字段类型不能为空");
        }
        if (field.getNotNullFlag() == null) {
            throw new CustomException("请选择是否为空");
        }
        if (field.getMatchDictFlag() == null) {
            throw new CustomException("请选择是否匹配字典值");
        }
        if (field.getNeedAlertFlag() == null) {
            throw new CustomException("请选择是否弹窗中显示");
        }
        if (field.getNeedAlertFlag() != null && field.getNeedAlertFlag() <= 0) {
            field.setIdentifyIndex(0);
        }
        if (field.getFilterType() != null && field.getFilterType() > 1) {
            field.setMatchDictFlag(1);
        }
        if (field.getIdentifyIndex() != null && field.getIdentifyIndex() > 0) {
            field.setNeedAlertFlag(1);
        }
        field.setChartLength(field.getChartLength() == null ? 500 : field.getChartLength());
        field.setFilterType(field.getFilterType() == null ? 2 : field.getFilterType());

        if (field.getFilterIndex() != null && field.getFilterIndex() >= Integer.MAX_VALUE){
            throw new CustomException("查询条件顺序数字过大");
        }
        if (field.getIdentifyIndex() != null && field.getIdentifyIndex() >= Integer.MAX_VALUE){
            throw new CustomException("弹窗字段顺序数字过大");
        }
        if (field.getResultIndex() != null && field.getResultIndex() >= Integer.MAX_VALUE){
            throw new CustomException("列表字段顺序数字过大");
        }
    }

    @Override
    @Transactional
    public Result<String> deleted(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.fail("参数不能为空");
        }
        List<Layer> layers = layerMapper.selectByTypeIds(ids);
        if (layers != null && !layers.isEmpty()) {
            List<String> typeIds = layers.stream().map(Layer::getLayerTypeId).collect(Collectors.toList());
            List<LayerType> types = layerTypeMapper.selectByIds(typeIds);
            String typeNames = types.stream().map(LayerType::getTypeName).collect(Collectors.joining(","));
            return Result.fail("【" + typeNames + "】下已有图层，暂时无法删除，请删除【"+typeNames+"】下的图层后再试");
        }
        List<LayerType> types = layerTypeMapper.selectByIds(ids);
        int count = layerTypeMapper.deleteBatchIds(ids);
        List<String> abbreviations = StreamUtils.toList(types, LayerType::getAbbreviation);
        if (!abbreviations.isEmpty()){
            layerTypeFieldsMapper.deleteByAbbreviation(abbreviations);
            layerTypeAttrFilterMapper.deleteByAbbreviation(abbreviations);
        }
        return count > 0 ? Result.success("删除成功") : Result.fail("删除失败");
    }

    @Override
    public Result<String> deleteCheck(List<String> ids) {
        List<Layer> layers = layerMapper.selectByTypeIds(ids);
        if (layers != null && !layers.isEmpty()) {
            List<String> typeIds = layers.stream().map(Layer::getLayerTypeId).collect(Collectors.toList());
            List<LayerType> types = layerTypeMapper.selectByIds(typeIds);
            String typeNames = types.stream().map(LayerType::getTypeName).collect(Collectors.joining(","));
            return Result.fail("【" + typeNames + "】下已有图层，暂时无法删除，请删除【"+typeNames+"】下的图层后再试");
        }
        return Result.success("可以删除");
    }

    @Override
    public Result<String> validateLayerType(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.fail("参数不能为空");
        }
        List<Layer> layers = layerMapper.selectByTypeIds(ids);
        if (layers != null && !layers.isEmpty()) {
            List<String> typeIds = layers.stream().map(Layer::getLayerTypeId).collect(Collectors.toList());
            List<LayerType> types = layerTypeMapper.selectByIds(typeIds);
            String typeNames = types.stream().map(LayerType::getTypeName).collect(Collectors.joining(","));
            return Result.fail("【" + typeNames + "】下已有图层，暂时无法删除，请删除【" + typeNames + "】下的图层后再试");
        }
        return Result.success("可以删除");
    }

    @Override
    public Result<LayerTypeDTO> copyGetData(String layerTypeId) {
        LayerType layerType = selectByLayerTypeId(layerTypeId);

        LayerTypeDTO typeDTO = new LayerTypeDTO();
        typeDTO.setSuperTypeName(layerType.getAbbreviation());
        if (StringUtils.isEmpty(layerType.getInitialTypeName())){
            typeDTO.setInitialTypeName(layerType.getAbbreviation());
        }
        //字段值
        List<LayerTypeFields> fields = layerTypeFieldsMapper.selectByAbbreviation(layerType.getAbbreviation());
        List<AddOrModifyLayerTypeFieldsVO> fieldsVOS = BeanCopyUtils.copyList(fields, AddOrModifyLayerTypeFieldsVO.class);
        //字典值
        List<LayerTypeAttrFilter> filters = layerTypeAttrFilterMapper.selectByAbbreviation(layerType.getAbbreviation());
        Map<String, List<LayerTypeAttrFilter>> fieldMap = StreamUtils.groupByKey(filters, LayerTypeAttrFilter::getFilterType);
        for (AddOrModifyLayerTypeFieldsVO vo : fieldsVOS){
            List<LayerTypeAttrFilter> attrFilters = fieldMap.get(vo.getCode());
            if (attrFilters != null && !attrFilters.isEmpty()){
                vo.setFilters(BeanCopyUtils.copyList(attrFilters,LayerTypeAttrFilterVO.class));
            }
            if (vo.getCode().equals(layerType.getAreaRef())){
                vo.setHasArea(1);
            }
            if (vo.getCode().equals(layerType.getTbbhRef())){
                vo.setHasTbbh(1);
            }
        }
        typeDTO.setFields(fieldsVOS);
        return Result.data(typeDTO);
    }
    @Override
    @Transactional
    public Result<String> copyAddData(String layerTypeId) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        LayerType layerType = selectByLayerTypeId(layerTypeId);
        LayerType type = layerTypeMapper.getListByLayerTypeName(loginUser.getUser().getTenantId(), loginUser.getUser().getIndustryCode(), layerType);
        String typeSuffix = type.getTypeName().replace(layerType.getTypeName(), "");
        //图层名称自增1
        int typeIndex = 1;
        if (StringUtils.isNotEmpty(typeSuffix)) {
            String regex = "^\\d*";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(typeSuffix);
            if (matcher.find() && typeSuffix.length() == matcher.group().length()) {
                typeIndex = Integer.parseInt(matcher.group()) + 1;
            }
        }
        //复制图层类型
        LayerType copy = BeanCopyUtils.copy(layerType, LayerType.class);
        copy.setSuperTypeName(layerType.getAbbreviation());
        copy.setInitialTypeName(layerType.getInitialTypeName());
        copy.setTypeName(layerType.getTypeName() + typeIndex);
        copy.setId(IdUtils.fastSimpleUUID());
        copy.setCreateTime(new Date());
        copy.setUpdateTime(null);
        copy.setModifier(null);
        copy.setCreator(loginUser.getUser().getUserName());
        copy.setTenantId(loginUser.getUser().getTenantId());
        copy.setAppVerCode(loginUser.getUser().getAppVerCode());
        copy.setDeleteFlag(0);
        copy.setIndustryCode(loginUser.getUser().getIndustryCode());
        copy.setCategory(1);
        setAbbreviation(copy);
        layerTypeMapper.insert(copy);
        //复制字段
        CopyLayerTypeFieldsDTO dto = new CopyLayerTypeFieldsDTO();
        dto.setTreatAbbreviation(layerType.getAbbreviation());
        dto.setTargetAbbreviation(copy.getAbbreviation());
        layerTypeFieldsService.copyLayerTypeFields(dto);
        //复制字典值
        List<LayerTypeAttrFilter> filters = layerTypeAttrFilterMapper.selectByAbbreviation(layerType.getAbbreviation());
        if (filters != null && !filters.isEmpty()) {
            filters.forEach(filter -> {
                filter.setAbbreviation(copy.getAbbreviation());
                filter.setUpdateTime(null);
                filter.setModifier(null);
            });
            layerTypeAttrFilterMapper.saveBatch(filters);
        }
        return Result.success("复制完成");
    }

    @Override
    @Transactional
    public Result<String> addLayerType(SysLayerTypeDTO dto) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isEmpty(dto.getTypeName())) {
            throw new CustomException("图层名称不能为空");
        }
        authLayerTypeName(dto.getTypeName());

        LayerType copy = BeanCopyUtils.copy(dto, LayerType.class);
        List<LayerType> dtos = Lists.newArrayList(copy);
        //需要新增的数据
        dtos.forEach(vo -> {
            vo.setId(IdUtils.fastSimpleUUID());
            vo.setCreateTime(new Date());
            vo.setCreator(loginUser.getUser().getUserName());
            vo.setTenantId(loginUser.getUser().getTenantId());
            vo.setAppVerCode(loginUser.getUser().getAppVerCode());
            vo.setDeleteFlag(0);
            vo.setIndustryCode(loginUser.getUser().getIndustryCode());
            vo.setCategory(1);
        });

        if (!dtos.isEmpty()) {
            //生成图层类型简称,规则为：名称拼音首字母，如果重复序号+1
            this.setAbbreviation(dtos);
            layerTypeMapper.saveBatch(dtos);
        }


        return Result.success("新增成功");
    }


    @Override
    @Transactional
    public Result<String> modifyLayerType(SysLayerTypeDTO dto) {
        LayerType layerType = selectByLayerTypeId(dto.getId());
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (layerType.getCategory() == 0 && VideoSystemEnums.UserType.TENANT_USER.getIndex().equals(loginUser.getUser().getUserType())) {
            throw new CustomException("初始化类型不能修改");
        }

        if (StringUtils.isNotEmpty(dto.getTypeName()) && !layerType.getTypeName().equals(dto.getTypeName())) {
            authLayerTypeName(dto.getTypeName());
        }

        LayerType copy = BeanCopyUtils.copy(dto, LayerType.class);
        setAbbreviation(copy);
        copy.setUpdateTime(new Date());
        copy.setModifier(SecurityUtils.getUsername());
        layerTypeMapper.updateById(copy);

        //简称发生修改
        return Result.success("修改成功");
    }

    /**
     * 获得图层类型
     */
    public LayerType selectByLayerTypeId(String layerTypeId) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        LayerType layerType = layerTypeMapper.selectById(layerTypeId);
        if (layerType != null && layerType.getCategory() == 0){
            return layerType;
        }
        if (layerType == null
                || !layerType.getTenantId().equals(loginUser.getUser().getTenantId())
                || !layerType.getIndustryCode().equals(loginUser.getUser().getIndustryCode())
        ) {
            throw new CustomException("图层类型错误");
        }
        return layerType;
    }

    /**
     * 检验图层类型名称
     */
    private void authLayerTypeName(String typeName) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (typeName.trim().length() > 20) {
            throw new CustomException("图层类型名称限制最多支持输入20个文字");
        }
        List<String> typeNames = Lists.newArrayList(typeName);
        Integer count = layerTypeMapper.selectCount(loginUser.getUser().getTenantId(), loginUser.getUser().getIndustryCode(), typeNames);
        if (count > 0) {
            throw new CustomException(ERROR_LAYER_NAME);
        }
    }

    public List<LayerType> getAbbreviation(List<LayerType> list) {
        SysUser user = SecurityUtils.getLoginUser().getUser();

        for (LayerType layerType : list) {
            layerType.setAbbreviation(this.getInitials(layerType.getTypeName()).replaceAll("\\s*", ""));
        }

        List<LayerType> listByAbbreviation = layerTypeMapper.getListByAbbreviation(user.getTenantId(), user.getIndustryCode(), list);
        for (LayerType layerType : list) {
            List<LayerType> collect = listByAbbreviation.stream().filter(e -> e.getAbbreviation().indexOf(layerType.getAbbreviation()) == 0).collect(Collectors.toList());
            layerType.setAbbreviation(this.getMaxAbbreviation(layerType.getAbbreviation(), collect));
            //将重新构造的对象放到listByAbbreviation里，防止list里生成的简称重复
            listByAbbreviation.add(layerType);
        }
        return list;
    }

    /**
     * 功能描述: 获取拼音首字母
     */
    public String getInitials(String name) {
        try {
            char[] charArray = name.toCharArray();
            StringBuilder pinyin = new StringBuilder();
            HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
            // 设置大小写格式
            defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            // 设置声调格式：
            defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            int length = charArray.length;
            if (length > 10) {
                length = 10;
            }
            for (int i = 0; i < length; i++) {
                //匹配中文,非中文转换会转换成null
                if (Character.toString(charArray[i]).matches(Constants.U4E00)) {
                    String[] hanyuPinyinStringArray = PinyinHelper.toHanyuPinyinStringArray(charArray[i], defaultFormat);
                    if (hanyuPinyinStringArray != null) {
                        pinyin.append(hanyuPinyinStringArray[0].charAt(0));
                    }
                } else if (Character.toString(charArray[i]).matches(TABLE_PATTERN)) {
                    pinyin.append(Character.toString(charArray[i]).toLowerCase(Locale.ROOT));
                }
            }
            if (StringUtils.isEmpty(pinyin.toString())) {
                return "tuceng";
            } else {
                return pinyin.toString();
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            String err = Constants.PINYIN_ERROR;
            log.error(err, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return name;
        }
    }




    /**
     * 判断字符串是否是纯数字
     *
     * @param str
     * @return
     */
    public boolean isNumber(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str.replaceAll("\\s*", "")).matches();
    }

    public String getMaxAbbreviation(String name, List<LayerType> collect) {
        StringBuffer abbreviation = new StringBuffer(name);
        if (!CollectionUtils.isEmpty(collect)) {
            List<String> nameList = collect.stream().map(s -> s.getAbbreviation().substring(name.length()).replaceAll("\\s*", ""))
                    .collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(nameList) && nameList.size() == Constants.ONE_NUM && StringUtils.isEmpty(nameList.get(Constants.ZERO_NUM))) {
                abbreviation.append(Constants.ONE);
            } else {
                nameList = nameList.stream().filter(temp -> !StringUtils.isEmpty(temp) && this.isNumber(temp)).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(nameList)) {
                    abbreviation.append(Constants.ONE);
                } else {
                    //List<String>转为List<Integer>
                    List<Integer> codesInteger = nameList.stream().map(Integer::parseInt).collect(Collectors.toList());
                    //倒序排序
                    Collections.sort(codesInteger, Collections.reverseOrder());
                    abbreviation.append(codesInteger.get(Constants.ZERO_NUM) + Constants.ONE_NUM);
                }
            }
        }
        return abbreviation.toString();
    }

    public void setAbbreviation(LayerType layerType) {
        List<LayerType> list = Lists.newArrayList(layerType);
        setAbbreviation(list);
        Map<String, String> map = StreamUtils.toMap(list, LayerType::getTypeName, LayerType::getAbbreviation);
        layerType.setAbbreviation(map.get(layerType.getTypeName()));
    }

    public void setAbbreviation(List<LayerType> list) {
        //生成图层类型简称,规则为：名称拼音首字母，如果重复序号+1
        List<LayerType> abbreviation = this.getAbbreviation(list);
        list.stream().map(item -> {
            abbreviation.stream().filter(area -> Objects.equals(item.getTypeName(), area.getTypeName())
            ).forEach(area -> {
                item.setAbbreviation(area.getAbbreviation());
            });
            return item;
        }).collect(Collectors.toList());
    }
}
