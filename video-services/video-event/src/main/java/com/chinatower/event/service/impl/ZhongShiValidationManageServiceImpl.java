package com.chinatower.event.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.entity.Result;
import com.chinatower.event.dto.zhongshi.FunctionValidationDTO;
import com.chinatower.event.dto.zhongshi.LargeModelValidationDTO;
import com.chinatower.event.dto.zhongshi.ModelValidationDTO;
import com.chinatower.event.mapper.FunctionValidationMapper;
import com.chinatower.event.mapper.LargeModelValidationMapper;
import com.chinatower.event.mapper.ModelValidationMapper;
import com.chinatower.event.service.ZhongShiValidationManageService;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 中试验证数据维护服务实现
 */
@Service
@Slf4j
public class ZhongShiValidationManageServiceImpl implements ZhongShiValidationManageService {

    private static final Integer NOT_DELETE = 0;
    private static final Integer DELETED = 1;
    private static final Integer YES = 1;
    private static final int DEFAULT_PAGE_NUM = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;

    @Autowired
    private FunctionValidationMapper functionValidationMapper;

    @Autowired
    private ModelValidationMapper modelValidationMapper;

    @Autowired
    private LargeModelValidationMapper largeModelValidationMapper;

    /**
     * 功能验证新增
     */
    @Override
    public Result<Boolean> addFunctionValidation(FunctionValidationDTO req) {
        if (req == null) {
            return Result.fail("请求参数不能为空");
        }
        if (StringUtils.isBlank(req.getFunctionModule()) || req.getTestDate() == null || req.getTestTimes() == null) {
            return Result.fail("功能模块、测试日期、测试次数不能为空");
        }
        SysUser user = getCurrentUser();
        req.setTenantId(user.getTenantId());
        req.setIndustryCode(user.getIndustryCode());
        req.setCreator(user.getUserName());
        req.setCreateTime(new Date());
        req.setDeleteFlag(NOT_DELETE);
        if (req.getPassed() == null) {
            req.setPassed(YES);
        }
        if (req.getFirstPassRate() == null) {
            req.setFirstPassRate(BigDecimal.ZERO);
        }
        if (req.getFinalPassRate() == null) {
            req.setFinalPassRate(BigDecimal.ZERO);
        }
        int rows = functionValidationMapper.insert(req);
        log.info("新增功能验证完成，主键ID:{}，影响行数:{}", req.getId(), rows);
        return Result.data(rows > 0);
    }

    /**
     * 功能验证编辑
     */
    @Override
    public Result<Boolean> editFunctionValidation(FunctionValidationDTO req) {
        if (req == null || req.getId() == null) {
            return Result.fail("主键ID不能为空");
        }
        SysUser user = getCurrentUser();
        FunctionValidationDTO db = getFunctionById(req.getId(), user.getTenantId(), user.getIndustryCode());
        if (db == null) {
            return Result.fail("记录不存在");
        }
        req.setTenantId(user.getTenantId());
        req.setIndustryCode(user.getIndustryCode());
        req.setModifier(user.getUserName());
        req.setUpdateTime(new Date());
        req.setDeleteFlag(NOT_DELETE);
        int rows = functionValidationMapper.updateById(req);
        log.info("编辑功能验证完成，主键ID:{}，影响行数:{}", req.getId(), rows);
        return Result.data(rows > 0);
    }

    /**
     * 功能验证删除
     */
    @Override
    public Result<Boolean> deleteFunctionValidation(Long id) {
        if (id == null) {
            return Result.fail("主键ID不能为空");
        }
        SysUser user = getCurrentUser();
        UpdateWrapper<FunctionValidationDTO> wrapper = new UpdateWrapper<>();
        wrapper.lambda()
                .set(FunctionValidationDTO::getDeleteFlag, DELETED)
                .set(FunctionValidationDTO::getModifier, user.getUserName())
                .set(FunctionValidationDTO::getUpdateTime, new Date())
                .eq(FunctionValidationDTO::getId, id)
                .eq(FunctionValidationDTO::getTenantId, user.getTenantId())
                .eq(FunctionValidationDTO::getIndustryCode, user.getIndustryCode())
                .eq(FunctionValidationDTO::getDeleteFlag, NOT_DELETE);
        int rows = functionValidationMapper.update(null, wrapper);
        log.info("删除功能验证完成，主键ID:{}，影响行数:{}", id, rows);
        return Result.data(rows > 0);
    }

    /**
     * 功能验证列表查询
     */
    @Override
    public List<FunctionValidationDTO> listFunctionValidation(FunctionValidationDTO req) {
        SysUser user = getCurrentUser();
        QueryWrapper<FunctionValidationDTO> wrapper = new QueryWrapper<>();
        boolean hasDateRange = req != null && req.getStartDate() != null && req.getEndDate() != null;
        wrapper.lambda()
                .eq(FunctionValidationDTO::getTenantId, user.getTenantId())
                .eq(FunctionValidationDTO::getIndustryCode, user.getIndustryCode())
                .eq(FunctionValidationDTO::getDeleteFlag, NOT_DELETE)
                .like(StringUtils.isNotBlank(req == null ? null : req.getFunctionModule()),
                        FunctionValidationDTO::getFunctionModule, req.getFunctionModule())
                .orderByDesc(FunctionValidationDTO::getTestDate)
                .orderByDesc(FunctionValidationDTO::getCreateTime);
        if (hasDateRange) {
            wrapper.lambda().between(FunctionValidationDTO::getTestDate,
                    startOfDay(req.getStartDate()), endOfDay(req.getEndDate()));
        }
        return functionValidationMapper.selectList(wrapper);
    }

    /**
     * 功能验证分页查询
     */
    @Override
    public IPage<FunctionValidationDTO> pageFunctionValidation(FunctionValidationDTO req) {
        SysUser user = getCurrentUser();
        Page<FunctionValidationDTO> page = new Page<>(getPageNum(req == null ? null : req.getPageNum()),
                getPageSize(req == null ? null : req.getPageSize()));
        QueryWrapper<FunctionValidationDTO> wrapper = new QueryWrapper<>();
        boolean hasDateRange = req != null && req.getStartDate() != null && req.getEndDate() != null;
        wrapper.lambda()
                .eq(FunctionValidationDTO::getTenantId, user.getTenantId())
                .eq(FunctionValidationDTO::getIndustryCode, user.getIndustryCode())
                .eq(FunctionValidationDTO::getDeleteFlag, NOT_DELETE)
                .like(StringUtils.isNotBlank(req == null ? null : req.getFunctionModule()),
                        FunctionValidationDTO::getFunctionModule, req.getFunctionModule())
                .orderByDesc(FunctionValidationDTO::getTestDate)
                .orderByDesc(FunctionValidationDTO::getCreateTime);
        if (hasDateRange) {
            wrapper.lambda().between(FunctionValidationDTO::getTestDate,
                    startOfDay(req.getStartDate()), endOfDay(req.getEndDate()));
        }
        return functionValidationMapper.selectPage(page, wrapper);
    }

    /**
     * 小模型验证新增
     */
    @Override
    public Result<Boolean> addModelValidation(ModelValidationDTO req) {
        if (req == null) {
            return Result.fail("请求参数不能为空");
        }
        if (StringUtils.isBlank(req.getAlgorithmName()) || req.getTestDate() == null || req.getTestTimes() == null) {
            return Result.fail("算法名称、测试日期、测试次数不能为空");
        }
        SysUser user = getCurrentUser();
        req.setTenantId(user.getTenantId());
        req.setIndustryCode(user.getIndustryCode());
        req.setCreator(user.getUserName());
        req.setCreateTime(new Date());
        req.setDeleteFlag(NOT_DELETE);
        if (req.getPassed() == null) {
            req.setPassed(YES);
        }
        if (StringUtils.isBlank(req.getTestStatus())) {
            req.setTestStatus("已完成");
        }
        int rows = modelValidationMapper.insert(req);
        log.info("新增小模型验证完成，主键ID:{}，影响行数:{}", req.getId(), rows);
        return Result.data(rows > 0);
    }

    /**
     * 小模型验证编辑
     */
    @Override
    public Result<Boolean> editModelValidation(ModelValidationDTO req) {
        if (req == null || req.getId() == null) {
            return Result.fail("主键ID不能为空");
        }
        SysUser user = getCurrentUser();
        ModelValidationDTO db = getModelById(req.getId(), user.getTenantId(), user.getIndustryCode());
        if (db == null) {
            return Result.fail("记录不存在");
        }
        req.setTenantId(user.getTenantId());
        req.setIndustryCode(user.getIndustryCode());
        req.setModifier(user.getUserName());
        req.setUpdateTime(new Date());
        req.setDeleteFlag(NOT_DELETE);
        int rows = modelValidationMapper.updateById(req);
        log.info("编辑小模型验证完成，主键ID:{}，影响行数:{}", req.getId(), rows);
        return Result.data(rows > 0);
    }

    /**
     * 小模型验证删除
     */
    @Override
    public Result<Boolean> deleteModelValidation(Long id) {
        if (id == null) {
            return Result.fail("主键ID不能为空");
        }
        SysUser user = getCurrentUser();
        UpdateWrapper<ModelValidationDTO> wrapper = new UpdateWrapper<>();
        wrapper.lambda()
                .set(ModelValidationDTO::getDeleteFlag, DELETED)
                .set(ModelValidationDTO::getModifier, user.getUserName())
                .set(ModelValidationDTO::getUpdateTime, new Date())
                .eq(ModelValidationDTO::getId, id)
                .eq(ModelValidationDTO::getTenantId, user.getTenantId())
                .eq(ModelValidationDTO::getIndustryCode, user.getIndustryCode())
                .eq(ModelValidationDTO::getDeleteFlag, NOT_DELETE);
        int rows = modelValidationMapper.update(null, wrapper);
        log.info("删除小模型验证完成，主键ID:{}，影响行数:{}", id, rows);
        return Result.data(rows > 0);
    }

    /**
     * 小模型验证列表查询
     */
    @Override
    public List<ModelValidationDTO> listModelValidation(ModelValidationDTO req) {
        SysUser user = getCurrentUser();
        QueryWrapper<ModelValidationDTO> wrapper = new QueryWrapper<>();
        boolean hasDateRange = req != null && req.getStartDate() != null && req.getEndDate() != null;
        wrapper.lambda()
                .eq(ModelValidationDTO::getTenantId, user.getTenantId())
                .eq(ModelValidationDTO::getIndustryCode, user.getIndustryCode())
                .eq(ModelValidationDTO::getDeleteFlag, NOT_DELETE)
                .like(StringUtils.isNotBlank(req == null ? null : req.getAlgorithmName()),
                        ModelValidationDTO::getAlgorithmName, req.getAlgorithmName())
                .eq(StringUtils.isNotBlank(req == null ? null : req.getTestStatus()),
                        ModelValidationDTO::getTestStatus, req.getTestStatus())
                .orderByDesc(ModelValidationDTO::getTestDate)
                .orderByDesc(ModelValidationDTO::getCreateTime);
        if (hasDateRange) {
            wrapper.lambda().between(ModelValidationDTO::getTestDate,
                    startOfDay(req.getStartDate()), endOfDay(req.getEndDate()));
        }
        return modelValidationMapper.selectList(wrapper);
    }

    /**
     * 小模型验证分页查询
     */
    @Override
    public IPage<ModelValidationDTO> pageModelValidation(ModelValidationDTO req) {
        SysUser user = getCurrentUser();
        Page<ModelValidationDTO> page = new Page<>(getPageNum(req == null ? null : req.getPageNum()),
                getPageSize(req == null ? null : req.getPageSize()));
        QueryWrapper<ModelValidationDTO> wrapper = new QueryWrapper<>();
        boolean hasDateRange = req != null && req.getStartDate() != null && req.getEndDate() != null;
        wrapper.lambda()
                .eq(ModelValidationDTO::getTenantId, user.getTenantId())
                .eq(ModelValidationDTO::getIndustryCode, user.getIndustryCode())
                .eq(ModelValidationDTO::getDeleteFlag, NOT_DELETE)
                .like(StringUtils.isNotBlank(req == null ? null : req.getAlgorithmName()),
                        ModelValidationDTO::getAlgorithmName, req.getAlgorithmName())
                .eq(StringUtils.isNotBlank(req == null ? null : req.getTestStatus()),
                        ModelValidationDTO::getTestStatus, req.getTestStatus())
                .orderByDesc(ModelValidationDTO::getTestDate)
                .orderByDesc(ModelValidationDTO::getCreateTime);
        if (hasDateRange) {
            wrapper.lambda().between(ModelValidationDTO::getTestDate,
                    startOfDay(req.getStartDate()), endOfDay(req.getEndDate()));
        }
        return modelValidationMapper.selectPage(page, wrapper);
    }

    /**
     * 大模型验证新增
     */
    @Override
    public Result<Boolean> addLargeModelValidation(LargeModelValidationDTO req) {
        if (req == null) {
            return Result.fail("请求参数不能为空");
        }
        if (StringUtils.isBlank(req.getModelName()) || StringUtils.isBlank(req.getDownstreamAlgorithm())
                || req.getTestDate() == null || req.getTestTimes() == null) {
            return Result.fail("模型名称、下游算法、测试日期、测试次数不能为空");
        }
        SysUser user = getCurrentUser();
        req.setTenantId(user.getTenantId());
        req.setIndustryCode(user.getIndustryCode());
        req.setCreator(user.getUserName());
        req.setCreateTime(new Date());
        req.setDeleteFlag(NOT_DELETE);
        if (req.getPassed() == null) {
            req.setPassed(YES);
        }
        if (req.getFirstPassRate() == null) {
            req.setFirstPassRate(BigDecimal.ZERO);
        }
        if (req.getFinalPassRate() == null) {
            req.setFinalPassRate(BigDecimal.ZERO);
        }
        int rows = largeModelValidationMapper.insert(req);
        log.info("新增大模型验证完成，主键ID:{}，影响行数:{}", req.getId(), rows);
        return Result.data(rows > 0);
    }

    /**
     * 大模型验证编辑
     */
    @Override
    public Result<Boolean> editLargeModelValidation(LargeModelValidationDTO req) {
        if (req == null || req.getId() == null) {
            return Result.fail("主键ID不能为空");
        }
        SysUser user = getCurrentUser();
        LargeModelValidationDTO db = getLargeModelById(req.getId(), user.getTenantId(), user.getIndustryCode());
        if (db == null) {
            return Result.fail("记录不存在");
        }
        req.setTenantId(user.getTenantId());
        req.setIndustryCode(user.getIndustryCode());
        req.setModifier(user.getUserName());
        req.setUpdateTime(new Date());
        req.setDeleteFlag(NOT_DELETE);
        int rows = largeModelValidationMapper.updateById(req);
        log.info("编辑大模型验证完成，主键ID:{}，影响行数:{}", req.getId(), rows);
        return Result.data(rows > 0);
    }

    /**
     * 大模型验证删除
     */
    @Override
    public Result<Boolean> deleteLargeModelValidation(Long id) {
        if (id == null) {
            return Result.fail("主键ID不能为空");
        }
        SysUser user = getCurrentUser();
        UpdateWrapper<LargeModelValidationDTO> wrapper = new UpdateWrapper<>();
        wrapper.lambda()
                .set(LargeModelValidationDTO::getDeleteFlag, DELETED)
                .set(LargeModelValidationDTO::getModifier, user.getUserName())
                .set(LargeModelValidationDTO::getUpdateTime, new Date())
                .eq(LargeModelValidationDTO::getId, id)
                .eq(LargeModelValidationDTO::getTenantId, user.getTenantId())
                .eq(LargeModelValidationDTO::getIndustryCode, user.getIndustryCode())
                .eq(LargeModelValidationDTO::getDeleteFlag, NOT_DELETE);
        int rows = largeModelValidationMapper.update(null, wrapper);
        log.info("删除大模型验证完成，主键ID:{}，影响行数:{}", id, rows);
        return Result.data(rows > 0);
    }

    /**
     * 大模型验证列表查询
     */
    @Override
    public List<LargeModelValidationDTO> listLargeModelValidation(LargeModelValidationDTO req) {
        SysUser user = getCurrentUser();
        QueryWrapper<LargeModelValidationDTO> wrapper = new QueryWrapper<>();
        boolean hasDateRange = req != null && req.getStartDate() != null && req.getEndDate() != null;
        wrapper.lambda()
                .eq(LargeModelValidationDTO::getTenantId, user.getTenantId())
                .eq(LargeModelValidationDTO::getIndustryCode, user.getIndustryCode())
                .eq(LargeModelValidationDTO::getDeleteFlag, NOT_DELETE)
                .like(StringUtils.isNotBlank(req == null ? null : req.getModelName()),
                        LargeModelValidationDTO::getModelName, req.getModelName())
                .like(StringUtils.isNotBlank(req == null ? null : req.getDownstreamAlgorithm()),
                        LargeModelValidationDTO::getDownstreamAlgorithm, req.getDownstreamAlgorithm())
                .orderByDesc(LargeModelValidationDTO::getTestDate)
                .orderByDesc(LargeModelValidationDTO::getCreateTime);
        if (hasDateRange) {
            wrapper.lambda().between(LargeModelValidationDTO::getTestDate,
                    startOfDay(req.getStartDate()), endOfDay(req.getEndDate()));
        }
        return largeModelValidationMapper.selectList(wrapper);
    }

    /**
     * 大模型验证分页查询
     */
    @Override
    public IPage<LargeModelValidationDTO> pageLargeModelValidation(LargeModelValidationDTO req) {
        SysUser user = getCurrentUser();
        Page<LargeModelValidationDTO> page = new Page<>(getPageNum(req == null ? null : req.getPageNum()),
                getPageSize(req == null ? null : req.getPageSize()));
        QueryWrapper<LargeModelValidationDTO> wrapper = new QueryWrapper<>();
        boolean hasDateRange = req != null && req.getStartDate() != null && req.getEndDate() != null;
        wrapper.lambda()
                .eq(LargeModelValidationDTO::getTenantId, user.getTenantId())
                .eq(LargeModelValidationDTO::getIndustryCode, user.getIndustryCode())
                .eq(LargeModelValidationDTO::getDeleteFlag, NOT_DELETE)
                .like(StringUtils.isNotBlank(req == null ? null : req.getModelName()),
                        LargeModelValidationDTO::getModelName, req.getModelName())
                .like(StringUtils.isNotBlank(req == null ? null : req.getDownstreamAlgorithm()),
                        LargeModelValidationDTO::getDownstreamAlgorithm, req.getDownstreamAlgorithm())
                .orderByDesc(LargeModelValidationDTO::getTestDate)
                .orderByDesc(LargeModelValidationDTO::getCreateTime);
        if (hasDateRange) {
            wrapper.lambda().between(LargeModelValidationDTO::getTestDate,
                    startOfDay(req.getStartDate()), endOfDay(req.getEndDate()));
        }
        return largeModelValidationMapper.selectPage(page, wrapper);
    }

    private FunctionValidationDTO getFunctionById(Long id, String tenantId, String industryCode) {
        QueryWrapper<FunctionValidationDTO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(FunctionValidationDTO::getId, id)
                .eq(FunctionValidationDTO::getTenantId, tenantId)
                .eq(FunctionValidationDTO::getIndustryCode, industryCode)
                .eq(FunctionValidationDTO::getDeleteFlag, NOT_DELETE);
        return functionValidationMapper.selectOne(wrapper);
    }

    private ModelValidationDTO getModelById(Long id, String tenantId, String industryCode) {
        QueryWrapper<ModelValidationDTO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(ModelValidationDTO::getId, id)
                .eq(ModelValidationDTO::getTenantId, tenantId)
                .eq(ModelValidationDTO::getIndustryCode, industryCode)
                .eq(ModelValidationDTO::getDeleteFlag, NOT_DELETE);
        return modelValidationMapper.selectOne(wrapper);
    }

    private LargeModelValidationDTO getLargeModelById(Long id, String tenantId, String industryCode) {
        QueryWrapper<LargeModelValidationDTO> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(LargeModelValidationDTO::getId, id)
                .eq(LargeModelValidationDTO::getTenantId, tenantId)
                .eq(LargeModelValidationDTO::getIndustryCode, industryCode)
                .eq(LargeModelValidationDTO::getDeleteFlag, NOT_DELETE);
        return largeModelValidationMapper.selectOne(wrapper);
    }

    /**
     * 获取当前登录用户并做空判断
     */
    private SysUser getCurrentUser() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null == loginUser || null == loginUser.getUser()) {
            throw new CustomException("获取用户信息失败", HttpStatus.BAD_REQUEST);
        }
        return loginUser.getUser();
    }

    private long getPageNum(Integer pageNum) {
        return pageNum == null || pageNum <= 0 ? DEFAULT_PAGE_NUM : pageNum;
    }

    private long getPageSize(Integer pageSize) {
        return pageSize == null || pageSize <= 0 ? DEFAULT_PAGE_SIZE : pageSize;
    }

    private Date startOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private Date endOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }
}
