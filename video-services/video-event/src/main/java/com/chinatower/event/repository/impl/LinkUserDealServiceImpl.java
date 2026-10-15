package com.chinatower.event.repository.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.constant.TableConstant;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.dataobject.LinkUserDeal;
import com.chinatower.event.dto.LinkUserDealModel;
import com.chinatower.event.mapper.LinkUserDealMapper;
import com.chinatower.event.repository.ILinkUserDealService;
import com.chinatower.event.util.DateUtils;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 流程环节用户处理业务接口实现
 * @author chmod
 * @version 1.0
 * @date 2022/2/14 14:10
 */
@Slf4j
@Service
@DS(EbikeDataBaseConstant.PLATFORM)
public class LinkUserDealServiceImpl implements ILinkUserDealService {
    @Autowired
    private LinkUserDealMapper linkUserDealMapper;

    @Override
    public Result<LinkUserDealModel> addLinkUserDeal(LinkUserDealModel model) {
        String warningOrderId = model.getWarningOrderId();
        String userIdStr = model.getUserId();
        Long linkId = model.getLinkId();
        String tableSuffix = model.getTableSuffix();
        Result<LinkUserDealModel> result = null;
        try {
            //检查表是否存在
            String tableName = "vm_link_user_deal_" + tableSuffix;
            Integer exist = linkUserDealMapper.checkTableExist(Constants.TABLE_SCHEMA, tableName);
            log.info("schema:{} tableName:{} vm_link_user_deal_分表是否存在:{}", Constants.TABLE_SCHEMA, tableName, exist);
            if (null == exist || exist <= 0) {
                //表不存在，创建分表
                log.info("{} - {} vm_link_user_deal_表不存在，创建分表，tableSuffix：{}", Constants.TABLE_SCHEMA, tableName, tableSuffix);
//                linkUserDealMapper.createTable(tableSuffix);
                //createNewTable(tableSuffix);
            }
            String[] userIdArr = userIdStr.split(",");

            List<LinkUserDealModel> linkUserDealModels = new LinkedList<>();
            for (String userId : userIdArr) {
                LinkUserDealModel item = new LinkUserDealModel();
                item.setLinkId(linkId);
                item.setUserId(userId);
                item.setWarningOrderId(warningOrderId);
                linkUserDealModels.add(item);
            }
            linkUserDealMapper.addLinkUserDeal(linkUserDealModels, tableSuffix);
            result = Result.success("操作成功");
            result.setData(model);
        } catch (Exception e) {
            log.error("新增流程环节用户处理异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), "新增流程环节用户处理异常");
        }
        return result;
    }

    /**
     * 创建新表
     * @param tableSuffix 表后缀
     */
    public void createNewTable(String tableSuffix) {
        Date date = DateUtils.stringToDateYYMMdd(tableSuffix);
        String oldYM = DateUtils.dateToStringYM(DateUtils.getDateAfter(date, -1));
        // 如果新年月和旧年月不同，则创建表
        if (!(tableSuffix).equalsIgnoreCase(oldYM)) {
            linkUserDealMapper.createNewTable(TableConstant.VM_LINK_USER_DEAL + tableSuffix, TableConstant.VM_LINK_USER_DEAL + oldYM);
        }
    }

    @Override
    public Result<List<LinkUserDeal>> queryLinkUserDeal(LinkUserDealModel model) {
        Result result = null;
        String tableSuffix = model.getTableSuffix();
        if (StringUtils.isEmpty(tableSuffix)) {
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR.getCode(), "表后缀名不能为空");
        }
        try {
            List<LinkUserDeal> resultList = linkUserDealMapper.queryLinkUserDeal(model);
            result = Result.success("查询成功");
            result.setData(resultList);
        } catch (Exception e) {
            log.error("查询流程环节用户处理异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), "查询流程环节用户处理异常");
        }

        return result;
    }

    @Override
    public Result<String> modifyLinkUserDeal(LinkUserDealModel model) {
        Result<String> result = null;
        try {
            linkUserDealMapper.modifyLinkUserDeal(model);
            result = Result.success("修改成功");
        } catch (Exception e) {
            log.error("修改流程环节用户处理异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), "修改流程环节用户处理异常");
        }
        return result;
    }
}
