package com.chinatower.bdm.modules.layer.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.bdm.api.constants.WpzfConstants;
import com.chinatower.bdm.modules.layer.domain.LayerTypeAttrFilter;
import com.chinatower.bdm.modules.layer.domain.LayerTypeFields;
import com.chinatower.bdm.modules.layer.mapper.LayerTypeAttrFilterMapper;
import com.chinatower.bdm.modules.layer.mapper.LayerTypeFieldsMapper;
import com.chinatower.bdm.modules.layer.mapper.VmLayerTemplateConfigMapper;
import com.chinatower.bdm.modules.layer.service.VmLayerTemplateConfigService;
import com.chinatower.common.core.dto.bdm.*;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.IdUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.constants.Constants;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.enums.SysLayerTypeFieldsTypeEnums;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.BeanCopyUtils;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.platform.utils.StreamUtils;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.google.common.collect.Lists;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 图层模板配置业务接口实现
 * @author lyy
 * @version v1.6.60
 * @date 2024/04/29 14:36
 */
@Slf4j
@Service
@DS("db1")
public class VmLayerTemplateConfigServiceImpl implements VmLayerTemplateConfigService {

    @Autowired
    private VmLayerTemplateConfigMapper layerTemplateConfigMapper;

    @Autowired
    private LayerTypeFieldsMapper layerTypeFieldsMapper;

    @Autowired
    private LayerTypeAttrFilterMapper layerTypeAttrFilterMapper;

    //字段code正则
    private static final String CODE_PATTERN = "^[A-Za-z_$][0-9A-Za-z_$]{0,63}";
    private static final String TABLE_PATTERN = "^[0-9A-Za-z_]+";

    /**
     * 获取图层模板配置列表信息
     * @author lyy
     * @param layerTempConfigDTO
     * @return Result<Object>
     */
    @Override
    @DataScope(dbType ="db1", tenantColumn="t.tenant_id", industryCode="t.industry_code", acceptColumn = "t.platform_code")
    public Result<Object> getLayerTempConfigList(VmLayerTempConfigDTO layerTempConfigDTO) {
        PageInfo<LayerTempConfigVO> pageInfo;
        try {
            //设置分页参数
            Integer pageNum = layerTempConfigDTO.getPageNum();
            Integer pageSize = layerTempConfigDTO.getPageSize();
            if (null == pageNum || pageNum <= 0) {
                pageNum = 1;
            }
            if (null == pageSize || pageSize <= 0) {
                pageSize = 10;
            }
            PageMethod.startPage(pageNum, pageSize);
            List<LayerTempConfigVO> resultList = layerTemplateConfigMapper.getLayerTempConfigList(layerTempConfigDTO);
            pageInfo = new PageInfo(resultList);
        } catch (Exception e) {
            return Result.fail(ResultCode.ERROR.getCode(), "查询图层模板配置异常");
        }
        return Result.data(pageInfo, Constants.QUERY_WAS_SUCCESSFUL);
    }

    /**
     * 根据图层模板ID查询,校验当前图层模板下是否已上传图层数据
     * @param layerTempConfigDTO
     * @return Result<Object>
     */
    @Override
    public Result<Object> queryLayerTemplateIsExists(VmLayerTempConfigDTO layerTempConfigDTO) {
        // 获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        if (loginUser == null || currentUser == null) {
            // 无法获取当前登录人，返回错误信息
            return Result.fail(Constants.NO_USER_INFO);
        }
        if(StringUtils.isEmpty(layerTempConfigDTO.getId())){
            return Result.fail("图层模板ID不能为空");
        }
        Integer templateIsExists = layerTemplateConfigMapper.selectSatelliteLayerCount(currentUser.getTenantId(),currentUser.getIndustryCode(),currentUser.getPlatformCode(),layerTempConfigDTO.getId());
        //判断当前图层模板下是否已上传图层数据
        if (templateIsExists > 0) {
            LayerTempConfigVO configVO = layerTemplateConfigMapper.queryLayerTemplateIsExists(layerTempConfigDTO);
            //如果已有图层，提示无法删除
            return Result.fail(ResultCode.FAILURE.getCode(),   "【" + configVO.getLayerTempName() + "】下已上传图层数据，无法删除。");
        }
        //如果没有上传图层数据,进行二次确认
        return Result.success("是否确认删除该图层模板？");
    }

    /**
     * 查看字段数据详情、编辑回显
     * @param layerTempConfigDTO
     * @return Result<Object>
     */
    @Override
    public List<VmLayerTempFieldsVO> queryLayerTempFieldsDetail(VmLayerTempConfigDTO layerTempConfigDTO) {
        // 获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        if (loginUser == null || currentUser == null) {
            // 无法获取当前登录人，返回错误信息
            throw new CustomException(Constants.NO_USER_INFO);
        }
        //查询当前图层模板是否已上传图层数据
        Integer count = layerTemplateConfigMapper.selectSatelliteLayerCount(currentUser.getTenantId(),currentUser.getIndustryCode(),
                currentUser.getPlatformCode(),layerTempConfigDTO.getId());
        LayerTempConfigVO layerTempConfigVO = layerTemplateConfigMapper.queryLayerTemplateIsExists(layerTempConfigDTO);
         //查询图层模版字段数据
        List<LayerTypeFields> fieldsList = layerTypeFieldsMapper.infos(layerTempConfigVO.getAbbreviation());
        List<VmLayerTempFieldsVO> fieldsVOS = BeanCopyUtils.copyList(fieldsList, VmLayerTempFieldsVO.class);
        List<String> codes = StreamUtils.toList(fieldsList, LayerTypeFields::getCode);
        //查询图层字段对应的字典数据
        List<LayerTypeAttrFilterVO> fieldFilters = layerTypeAttrFilterMapper.selectByAbbreviationAndCodes(layerTempConfigVO.getAbbreviation(), codes);
        Map<String, List<LayerTypeAttrFilterVO>> fieldFiltersMap = StreamUtils.groupByKey(fieldFilters, LayerTypeAttrFilterVO::getCode);
        fieldsVOS.forEach(vo -> {
            vo.setLayerTempName(layerTempConfigVO.getLayerTempName());
            vo.setSourceFlag(count > 0 ? Constants.ONE : ""); //当用户上传图层数据后，图层模板则不允许新增或删除字段，不允许修改字段code和字段类型
            vo.setFilters(fieldFiltersMap.get(vo.getCode()));
            vo.setHasArea(vo.getCode().equals(layerTempConfigVO.getAreaRef()) ? Constants.ONE_NUM : Constants.ZERO_NUM);
            vo.setHasTbbh(vo.getCode().equals(layerTempConfigVO.getTbbhRef()) ? Constants.ONE_NUM : Constants.ZERO_NUM);
            vo.setCategory(getCategoryByAbbreviation(vo.getAbbreviation(), vo.getCode()));
        });
         return fieldsVOS;
    }

    private Integer getCategoryByAbbreviation(String abbreviation, String code) {
        Map<String, String[]> cshtcmbMap = new HashMap<>();
        cshtcmbMap.put(Constants.GDWPZF_TCMB, WpzfConstants.GDWPZF_CSHTCMB);
        cshtcmbMap.put(Constants.KCWPZF_TCMB, WpzfConstants.KCWPZF_CSHTCMB);
        cshtcmbMap.put(Constants.FNHWPZF_TCMB, WpzfConstants.FNHWPZF_CSHTCMB);
        cshtcmbMap.put(Constants.FLHWPZF_TCMB, WpzfConstants.FLHWPZF_CSHTCMB);

        Integer category = Constants.ONE_NUM;
        if(cshtcmbMap.containsKey(abbreviation)){
            //默认图层模板
            if (Arrays.asList(cshtcmbMap.get(abbreviation)).contains(code)) {
                category = Constants.ZERO_NUM;
            }
        } else {
            //自定义图层模板
            if (Arrays.asList(WpzfConstants.GDWPZF_CSHTCMB).contains(code) || Arrays.asList(WpzfConstants.KCWPZF_CSHTCMB).contains(code)
                    || Arrays.asList(WpzfConstants.FNHWPZF_CSHTCMB).contains(code) || Arrays.asList(WpzfConstants.FLHWPZF_CSHTCMB).contains(code)) {
                category = Constants.ZERO_NUM;
            }
        }
        return category;
    }

    /**
     * 校验图层模板名称是否唯一
     * @param layerTempConfigDTO
     * @return Result<Object>
     */
    @Override
    @DataScope(dbType ="db1", tenantColumn="t.tenant_id", industryCode="t.industry_code", acceptColumn = "t.platform_code")
    public Result<Object> getLayerTempNameCount(VmLayerTempConfigDTO layerTempConfigDTO) {
        int rows = layerTemplateConfigMapper.getLayerTempNameCount(layerTempConfigDTO);
        if (rows > 0) {
            return Result.data(rows, Constants.LAYER_TEMPLATE_NAME_Y);
        }
        return Result.data(rows);
    }

    /**
     * 新增图层模板配置信息
     * @param tempConfigDTO
     * @return Result<String>
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Result<String> addOrModifyLayerTempConfig(VmLayerTempConfigDTO tempConfigDTO) {
        log.info("新增图层模板类型数据 \r\n {}", JSON.toJSONString(tempConfigDTO));
        // 获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        if (loginUser == null || currentUser == null) {
            // 无法获取当前登录人，返回错误信息
            return Result.fail(Constants.NO_USER_INFO);
        }
        if (StringUtils.isEmpty(tempConfigDTO.getLayerTempName())) {
            throw new CustomException("图层模板名称不能为空");
        }
        if (tempConfigDTO.getLayerTempName().trim().length() > 20) {
            throw new CustomException("图层模板名称限制最多支持输入20个文字");
        }
        //校验图层模板名称是否唯一
        if(!StringUtils.isEmpty(tempConfigDTO.getLayerTempName())){
            tempConfigDTO.setTenantId(currentUser.getTenantId());
            tempConfigDTO.setIndustryCode(currentUser.getIndustryCode());
            tempConfigDTO.setPlatformCode(currentUser.getPlatformCode());
            Integer tempNameCount = layerTemplateConfigMapper.getLayerTempNameCount(tempConfigDTO);
            if(tempNameCount > 0){
                return Result.fail(Constants.LAYER_TEMPLATE_NAME_Y);
            }
        }
        LayerTempConfigVO configVO = new LayerTempConfigVO(); //新增/修改的图层模板
        List<String> filedIds = new ArrayList<>();  //需要删除的字段ID
        List<String> dictIds = new ArrayList<>();  //需要删除的字典ID
        //没传ID为新增
        if (StringUtils.isEmpty(tempConfigDTO.getId())) {
            //新增前校验：判断新增的字段数据中是否包含全部的默认图层模板字段数据
            validateFields(tempConfigDTO.getLayerTempFields());

            List<AddOrModifyLayerTypeFieldsVO> addFields = new ArrayList<>(); //新增的图层模板字段
            List<LayerTypeAttrFilter> addFilters = new ArrayList<>(); //新增的图层模板字典

            //填充图层模板、字段以及字典数据
            fillLayerTemplates(tempConfigDTO, configVO, addFields, filedIds, dictIds, addFilters, currentUser);

            if(configVO != null){
                log.info("新增图层模版数据入库 {}", JSON.toJSONString(configVO));
                layerTemplateConfigMapper.addVmLayerTemplateConfig(configVO);
                //新增字段数据
                if(!CollectionUtils.isEmpty(addFields)){
                    List<LayerTypeFields> fields = BeanCopyUtils.copyList(addFields, LayerTypeFields.class);
                    layerTypeFieldsMapper.saveBatch(fields);
                }
                //新增字段字典
                if(!CollectionUtils.isEmpty(addFilters)){
                    layerTypeAttrFilterMapper.saveBatch(addFilters);
                }
            }
        } else {  //编辑逻辑
            List<AddOrModifyLayerTypeFieldsVO> modifyFields = new ArrayList<>(); //修改的图层模板字段
            List<LayerTypeAttrFilter> modifyFilters = new ArrayList<>(); //修改的图层模板字段字典

            //填充图层模板、字段以及字典数据
            fillLayerTemplates(tempConfigDTO, configVO, modifyFields, filedIds, dictIds, modifyFilters, currentUser);
            if(configVO != null){
                layerTemplateConfigMapper.updateLayerTempConfigById(configVO);
                //修改字段数据
                if (!modifyFields.isEmpty()) {
                    List<LayerTypeFields> fields = BeanCopyUtils.copyList(modifyFields, LayerTypeFields.class);
                    if (!filedIds.isEmpty()) {
                        layerTypeFieldsMapper.deleteByIds(filedIds);
                    }
                    layerTypeFieldsMapper.saveBatch(fields);
                }
                //修改字典数据
                if (!modifyFilters.isEmpty()) {
                    if (!dictIds.isEmpty()) {
                        layerTypeAttrFilterMapper.deleteByIds(dictIds);
                    }
                    layerTypeAttrFilterMapper.saveBatch(modifyFilters);
                }
            }
        }
            return Result.success("图层模板配置成功");
    }

    /**
     * 根据选择的告警类型获取对应初始化图层模板信息
     * @param configDTO
     * @return
     */
    @Override
    public Result<Object> selectLayerFieldsByType(VmLayerTempConfigDTO configDTO) {
        PageInfo<VmLayerTempFieldsVO> pageInfo;
        try {
            //设置分页参数
            Integer pageNum = configDTO.getPageNum();
            Integer pageSize = configDTO.getPageSize();
            if (null == pageNum || pageNum <= 0) {
                pageNum = 1;
            }
            if (null == pageSize || pageSize <= 0) {
                pageSize = 10;
            }
            PageMethod.startPage(pageNum, pageSize);
            LayerTempConfigVO initLayerTemp = layerTemplateConfigMapper.selectInitLayerTempByType(configDTO);
            List<LayerTypeFields> fieldsList = layerTypeFieldsMapper.selectByAbbreviation(initLayerTemp.getAbbreviation());
            List<VmLayerTempFieldsVO> newFieldList = fieldsList.stream()
                    .map(field -> {
                        VmLayerTempFieldsVO fieldsVO = new VmLayerTempFieldsVO();
                        BeanUtils.copyProperties(field, fieldsVO);
                        fieldsVO.setHasArea(field.getCode().equals(initLayerTemp.getAreaRef()) ? Constants.ONE_NUM : Constants.ZERO_NUM);
                        fieldsVO.setHasTbbh(field.getCode().equals(initLayerTemp.getTbbhRef()) ? Constants.ONE_NUM : Constants.ZERO_NUM);
                        fieldsVO.setCategory(Constants.ZERO_NUM);
                        return fieldsVO;
                    })
                    .collect(Collectors.toList());
            pageInfo = new PageInfo(newFieldList);
        } catch (Exception e) {
            return Result.fail(ResultCode.ERROR.getCode(), "查询初始化图层模板配置异常");
        }
        return Result.data(pageInfo, Constants.QUERY_WAS_SUCCESSFUL);
    }

    /**
     * 获取图层模板下拉列表数据
     * @param configDTO
     * @return
     */
    @Override
    public Result<Object> getLayerTempByTypeList(VmLayerTempConfigDTO configDTO) {
        // 获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        if (loginUser == null || currentUser == null) {
            // 无法获取当前登录人，返回错误信息
            return Result.fail(Constants.NO_USER_INFO);
        }
        if (StringUtils.isEmpty(configDTO.getAlarmType())) {
            throw new CustomException("告警类型ID不能为空");
        }
        if (StringUtils.isEmpty(configDTO.getTenantId())) {
            configDTO.setTenantId(currentUser.getTenantId());
            configDTO.setIndustryCode(currentUser.getIndustryCode());
            configDTO.setPlatformCode(currentUser.getPlatformCode());
        }
        List<Map<String, Object>> mapList = layerTemplateConfigMapper.getLayerTempByTypeList(configDTO);
        return Result.data(mapList);
    }

    private void validateFields(List<AddOrModifyLayerTypeFieldsVO> layerTempFields) {
        String[] codes = layerTempFields.stream()
                .map(AddOrModifyLayerTypeFieldsVO::getCode)
                .toArray(String[]::new);

        Map<String[], String> cshtcmbMap = new HashMap<>();
        String abbreviation = layerTempFields.get(0).getAbbreviation();
        if(Constants.GDWPZF_TCMB.equals(abbreviation)){
            cshtcmbMap.put(WpzfConstants.GDWPZF_CSHTCMB, "耕地卫片执法初始化图层模版字段填写不完整！");
        }
        if(Constants.KCWPZF_TCMB.equals(abbreviation)){
            cshtcmbMap.put(WpzfConstants.KCWPZF_CSHTCMB, "矿产卫片执法初始化图层模版字段填写不完整！");
        }
        if(Constants.FNHWPZF_TCMB.equals(abbreviation)){
            cshtcmbMap.put(WpzfConstants.FNHWPZF_CSHTCMB, "非农化卫片执法初始化图层模版字段填写不完整！");
        }
        if(Constants.FLHWPZF_TCMB.equals(abbreviation)){
            cshtcmbMap.put(WpzfConstants.FLHWPZF_CSHTCMB, "非粮化卫片执法初始化图层模版字段填写不完整！");
        }
        for (Map.Entry<String[], String> entry : cshtcmbMap.entrySet()) {
            String[] cshtcmb = entry.getKey();
            String errorMessage = entry.getValue();
            long matchCount = Arrays.stream(codes)
                    .filter(code -> Arrays.asList(cshtcmb).contains(code))
                    .count();
            if (matchCount != cshtcmb.length) {
                throw new CustomException(errorMessage);
            }
        }
    }

    private void fillLayerTemplates(VmLayerTempConfigDTO tempConfigDTO, LayerTempConfigVO configVO,
                                    List<AddOrModifyLayerTypeFieldsVO> addFields, List<String> filedIds,
                                    List<String> dictIds, List<LayerTypeAttrFilter> addFilters, SysUser currentUser) {
        BeanUtils.copyProperties(tempConfigDTO, configVO);
        if(StringUtils.isEmpty(tempConfigDTO.getId())){
            configVO.setId(IdUtils.fastSimpleUUID());
            configVO.setCategory(tempConfigDTO.getCategory() == null ? Constants.ONE_NUM : tempConfigDTO.getCategory());
            //新增时设置简称字段值，生成简称规则：wpzf_名称拼音首字母，如果重复序号+1
            setlayerabbreviation(configVO);
            configVO.setAbbreviation(Constants.ABBREVIATION_PREFIX + configVO.getAbbreviation());
            configVO.setCreateTime(new Date());
            configVO.setCreator(currentUser.getUserName());
            configVO.setTenantId(currentUser.getTenantId());
            configVO.setAppVerCode(currentUser.getAppVerCode());
            configVO.setIndustryCode(currentUser.getIndustryCode());
            configVO.setPlatformCode(currentUser.getPlatformCode());
        } else {
            //编辑时不能修改简称
            configVO.setAbbreviation(tempConfigDTO.getAbbreviation());
            configVO.setCategory(tempConfigDTO.getCategory());
            configVO.setModifier(currentUser.getUserName());
            configVO.setUpdateTime(new Date());
        }

        /*VmLayerTempConfigDTO dto = new VmLayerTempConfigDTO();
        dto.setAbbreviation(tempConfigDTO.getSuperTypeName()); //父级类型字段（存的是简称字段的值）
        LayerTempConfigVO layerTemp = layerTemplateConfigMapper.queryLayerTemplateIsExists(dto);
        if(layerTemp != null){
            configVO.setSuperTypeName(layerTemp.getAbbreviation());
            configVO.setInitialTypeName(layerTemp.getInitialTypeName());
            //如果是第一次复制则第一级为当前类型
            if (StringUtils.isEmpty(layerTemp.getInitialTypeName())){
                configVO.setInitialTypeName(layerTemp.getAbbreviation());
            }
        }*/

        //图层模版字段数据
        List<AddOrModifyLayerTypeFieldsVO> fields = tempConfigDTO.getLayerTempFields();
        //图层模板字段校验
        layerTempFieldsIsCheck(tempConfigDTO, fields, currentUser);
        if (!CollectionUtils.isEmpty(fields)) {
            //获取需要修改的图层模板字段ID集合
            List<String> deleteFieldsIds = fields.stream().filter(field -> !StringUtils.isEmpty(field.getId())).map(AddOrModifyLayerTypeFieldsVO::getId).collect(Collectors.toList());
            //需要删除的图层模版字段数据
            filedIds.addAll(deleteFieldsIds);
            filedIds.addAll(tempConfigDTO.getFieldIds());
            if(!StringUtils.isEmpty(tempConfigDTO.getId())){
                List<String> codes = fields.stream().map(AddOrModifyLayerTypeFieldsVO::getCode).collect(Collectors.toList());
                if (!codes.isEmpty()) {
                    List<LayerTypeFields> ids = layerTypeFieldsMapper.selectByFieldCodes(tempConfigDTO.getAbbreviation(), codes);
                    filedIds.addAll(StreamUtils.toList(ids, LayerTypeFields::getId));
                }
            }
            //设置面积字段
            Optional<String> firstArea = fields.stream().filter(f -> f.getHasArea() == 1).map(AddOrModifyLayerTypeFieldsVO::getCode).findFirst();
            firstArea.ifPresent(configVO::setAreaRef);
            if (StringUtils.isEmpty(configVO.getAreaRef())){
                configVO.setAreaRef("calarea");
            }
            //设置图斑编号字段
            Optional<String> firstTbbh = fields.stream().filter(f -> f.getHasTbbh() != null && f.getHasTbbh() == 1).map(AddOrModifyLayerTypeFieldsVO::getCode).findFirst();
            firstTbbh.ifPresent(configVO::setTbbhRef);
            if (StringUtils.isEmpty(configVO.getTbbhRef())){
                configVO.setTbbhRef("bsm");
            }

            //查询数据库表中已有的字段字典数据
            List<LayerTypeAttrFilter> sourceFilter = layerTypeAttrFilterMapper.selectByAbbreviation(tempConfigDTO.getAbbreviation());
            Map<String, Map<String, List<LayerTypeAttrFilter>>> group2Map = StreamUtils.groupBy2Key(sourceFilter, LayerTypeAttrFilter::getFilterType, LayerTypeAttrFilter::getFilterAttrKey);
            fields.forEach(field -> {
                //需要删除的字典
                dictIds.addAll(field.getIds());
                //图层模板字段校验
                layerTemplateFieldsIsCheck(field);

                field.setAbbreviation(configVO.getAbbreviation());
                field.setId(IdUtils.fastSimpleUUID());
                field.setCreateTime(new Date());
                field.setCreator(currentUser.getUserName());
                field.setUpdateTime(new Date());
                field.setModifier(currentUser.getUserName());

                //获取字典数据
                List<LayerTypeAttrFilterVO> filters = field.getFilters();
                if (!CollectionUtils.isEmpty(filters)) {
                    //需要删除的字段字典数据
                    List<LayerTypeAttrFilter> attrFilters = BeanCopyUtils.copyList(filters, LayerTypeAttrFilter.class);
                    attrFilters = StreamUtils.filter(attrFilters,f -> StringUtils.isNotEmpty(f.getFilterAttrKey()));

                    Map<String, List<LayerTypeAttrFilter>> filterMap = group2Map.get(field.getCode());
                    for (LayerTypeAttrFilter f : attrFilters) {
                        //判断字段是否有重复的key
                        if (filterMap != null && !filterMap.isEmpty()) {
                            List<LayerTypeAttrFilter> layerTypeAttrFilters = filterMap.get(f.getFilterAttrKey());
                            if (layerTypeAttrFilters != null && !layerTypeAttrFilters.isEmpty()) {
                                dictIds.addAll(StreamUtils.toList(layerTypeAttrFilters, LayerTypeAttrFilter::getId));
                            }
                        }
                        f.setFilterType(field.getCode());
                        f.setAbbreviation(configVO.getAbbreviation());
                        f.setCreateTime(new Date());
                        f.setCreator(currentUser.getUserName());
                        f.setDeleteFlag(0);
                        f.setShowType(f.getShowType());
                    }
                    //需要修改的字段筛选值
                    List<String> deleteFilterIds = attrFilters.stream().filter(filter -> StringUtils.isNotEmpty(filter.getId())).map(LayerTypeAttrFilter::getId).collect(Collectors.toList());
                    dictIds.addAll(deleteFilterIds);
                    //字段字典值
                    addFilters.addAll(attrFilters);
                } else {
                    if (field.getMatchDictFlag() == 1) {
                        throw new CustomException("[ " + field.getName() + " ]请添加字典项");
                    }
                }
            });
            addFields.addAll(fields); //图层模板字段值
        }
    }

    private void setlayerabbreviation(LayerTempConfigVO configVO) {
        List<LayerTempConfigVO> list = Lists.newArrayList(configVO);
        //生成图层类型简称,规则为：名称拼音首字母，如果重复序号+1
        List<LayerTempConfigVO> abbreviation = this.getAbbreviation(list);
        list.stream().map(item -> {
            abbreviation.stream().filter(area -> Objects.equals(item.getLayerTempName(), area.getLayerTempName())
            ).forEach(area -> {
                item.setAbbreviation(area.getAbbreviation());
            });
            return item;
        }).collect(Collectors.toList());
        Map<String, String> map = StreamUtils.toMap(list, LayerTempConfigVO::getLayerTempName, LayerTempConfigVO::getAbbreviation);
        configVO.setAbbreviation(map.get(configVO.getLayerTempName()));
    }

    private List<LayerTempConfigVO> getAbbreviation(List<LayerTempConfigVO> list) {
        for (LayerTempConfigVO vo : list) {
            vo.setAbbreviation(this.getInitials(vo.getLayerTempName()).replaceAll("\\s*", ""));
        }
        List<LayerTempConfigVO> listByAbbreviation = layerTemplateConfigMapper.selectLayerByAbbreviation(list);
        for (LayerTempConfigVO vo : list) {
            int a = listByAbbreviation.size()+1;
            vo.setAbbreviation(vo.getAbbreviation() + a);
            //将重新构造的对象放到listByAbbreviation里，防止list里生成的简称重复
            listByAbbreviation.add(vo);
        }
        return list;
    }

    /**
     * 获取图层模板名称首字母
     * @param layerTempName
     * @return
     */
    private String getInitials(String layerTempName) {
        try {
            char[] charArray = layerTempName.toCharArray();
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
                if (Character.toString(charArray[i]).matches(com.chinatower.bdm.common.Constants.U4E00)) {
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
            String err = com.chinatower.bdm.common.Constants.PINYIN_ERROR;
            log.error(err, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return layerTempName;
        }
    }

    private void layerTemplateFieldsIsCheck(AddOrModifyLayerTypeFieldsVO field) {
        if (StringUtils.isEmpty(field.getCode()) || !Pattern.matches(CODE_PATTERN, field.getCode())) {
            throw new CustomException("请输入数字、字母、下划线和$符号，但不能以数字开头，也不能使用数据库的保留字，长度不能超过64个字符");
        }
        if (!StringUtils.isEmpty(field.getCode()) && Constants.PARAM_GEOMETRY.equals(field.getCode())) {
            throw new CustomException("不能定义为'geometry'");
        }
        if (StringUtils.isEmpty(field.getName()) || field.getName().length() > 64) {
            throw new CustomException("不能为空且长度不能超过64个字符");
        }
        if (StringUtils.isNotEmpty(field.getRemark()) && field.getRemark().length() > 200) {
            throw new CustomException("长度不能超过200个字符");
        }
        SysLayerTypeFieldsTypeEnums typeEnums = SysLayerTypeFieldsTypeEnums.get(field.getType());
        if (typeEnums == null) {
            throw new CustomException("请选择字段类型");
        }
        if (field.getNotNullFlag() == null) {
            //throw new CustomException("请选择是否为空");
            throw new CustomException("请选择是否必填");
        }
        if (field.getMatchDictFlag() == null) {
            throw new CustomException("请选择是否匹配字典值");
        }
        if (field.getHasTbbh() == null) {
            throw new CustomException("请选择是否为图斑编号");
        }

    }

    private void layerTempFieldsIsCheck(VmLayerTempConfigDTO tempConfigDTO, List<AddOrModifyLayerTypeFieldsVO> tempfields, SysUser currentUser) {
        if(!CollectionUtils.isEmpty(tempfields)){
            Map<String, List<AddOrModifyLayerTypeFieldsVO>> map = StreamUtils.groupByKey(tempfields, AddOrModifyLayerTypeFieldsVO::getCode);
            for (String code : map.keySet()) {
                if (map.get(code).size() > 1) {
                    throw new CustomException("【 " + code + " 】 字段重复，请检查后操作!");
                }
            }
        }
        //判断ID不为空是修改
        if(!StringUtils.isEmpty(String.valueOf(tempConfigDTO.getId()))){
            //查询该图层模板是否已有卫片执法配置
            Integer count = layerTemplateConfigMapper.selectSatelliteLayerCount(currentUser.getTenantId(), currentUser.getIndustryCode(), currentUser.getPlatformCode(), tempConfigDTO.getId());
            //查询已存在的图层模板字段数据
            List<LayerTypeFields> layerTypeFields = layerTypeFieldsMapper.selectByAbbreviation(tempConfigDTO.getAbbreviation());
            if (layerTypeFields != null && !layerTypeFields.isEmpty()) {
                if (count > 0) {
                    throw new CustomException("该图层模板已存在卫片执法数据配置");
                }
                Map<String, LayerTypeFields> fieldsMap = StreamUtils.toMap(layerTypeFields, LayerTypeFields::getCode, s -> s);
                tempfields.forEach(field -> {
                    LayerTypeFields layerTypeField = fieldsMap.get(field.getCode());
                    if (count > 0 && (layerTypeField == null || !Objects.equals(layerTypeField.getType(), field.getType()))) {
                        throw new CustomException("该字段已存在图层模板不能新增修改字段");
                    }
                });
            }
        }
    }

    /**
     * 删除图层模板配置
     * @param layerTempId
     * @return Result<String>
     */
    @Override
    @Transactional
    public Result<String> deleteLayerTempConfig(String layerTempId) {
        if(StringUtils.isEmpty(layerTempId)){
            return Result.fail("参数不能为空");
        }
        LayerTempConfigVO configVO = layerTemplateConfigMapper.selectLayerTempById(layerTempId);
        int i = layerTemplateConfigMapper.delLayerTempById(layerTempId);
        if (!StringUtils.isEmpty(configVO.getAbbreviation())) {
            List<String> abbreviations = new ArrayList<>();
            abbreviations.add(configVO.getAbbreviation());
            layerTypeFieldsMapper.deleteByAbbreviation(abbreviations);
            layerTypeAttrFilterMapper.deleteByAbbreviation(abbreviations);
        }
        return i > 0 ? Result.success("图层模板配置删除成功") : Result.fail("图层模板配置删除失败");
    }
}
