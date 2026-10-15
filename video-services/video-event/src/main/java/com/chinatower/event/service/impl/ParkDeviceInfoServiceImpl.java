package com.chinatower.event.service.impl;

import com.chinatower.common.entity.Result;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.constant.MessageConstants;
import com.chinatower.event.constant.TableConstant;
import com.chinatower.event.mapper.AlarmOrderAppMapper;
import com.chinatower.event.mapper.ParkDeviceInfoMapper;
import com.chinatower.event.mapper.SysMapper;
import com.chinatower.event.service.ParkDeviceInfoService;
import com.chinatower.event.util.DateUtils;
import com.chinatower.event.utils.TableUtil;
import com.chinatower.event.vo.ParkDeviceInfoReq;
import com.chinatower.event.vo.ParkDeviceInfoVo;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service("parkDeviceInfoService")
public class ParkDeviceInfoServiceImpl implements ParkDeviceInfoService {

    @Resource
    private ParkDeviceInfoMapper parkDeviceInfoDAO;

    @Resource
    private AlarmOrderAppMapper alarmOrderAppMapper;

    @Autowired
    private SysMapper sysMapper;

    @ApiOperation(tags = "查询公园摄像机机列表", value = "")
    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE,tenantColumn="p.tenant_id",industryCode="p.industry_code")
    public List<ParkDeviceInfoVo> getParkDeviceList(ParkDeviceInfoReq req) {
         if (req.getPageNum() != null || req.getPageSize() != null) {
            //分页开始的数
            int offset = (req.getPageNum() - Constants.ONE_NUM) * req.getPageSize();

            req.setLimit(req.getPageSize());
            req.setOffset(offset);
        }

        // 获取当前登录人
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if(currentUser != null && StringUtils.isEmpty(currentUser.getUser().getTenantId())) {
            List<ParkDeviceInfoVo> list = new ArrayList<>();
            return list;
        }
        if(currentUser != null && !StringUtils.isEmpty(currentUser.getUser().getUserId())) {
            req.setTenantId(currentUser.getUser().getTenantId());
        }
        Integer allCount = parkDeviceInfoDAO.selectVmParkDeviceInfoListCount(req);
        // 默认按照park_code排序
        /*if ((req.getCreateDateOrder() != null &&  !req.getCreateDateOrder().isEmpty()) || (req.getUpdateDateOrder() != null &&  !req.getUpdateDateOrder().isEmpty())) {
            String orderSql = genDateOrderSql(req.getCreateDateOrder(), req.getUpdateDateOrder());
            req.setOrderSql(orderSql);
        }*/
        List<ParkDeviceInfoVo> resultList = parkDeviceInfoDAO.selectVmParkDeviceInfoList(req);
        if (!StringUtils.isEmpty(req.getParkRemark()) && "parkPeople".equals(req.getParkRemark())){
            try {
                ParkDeviceInfoReq reqs = new ParkDeviceInfoReq();
                List<ReceiveAlarmVO> receiveAlarmVOS =  this.getOrderAlarmByTenantCode(reqs);
                //将所有的摄像机进行分组
                Map<String, List<ReceiveAlarmVO>> groupBySex = receiveAlarmVOS.stream().filter(cabinetInfo->cabinetInfo.getDeviceCode()!=null).collect(Collectors.groupingBy(ReceiveAlarmVO::getDeviceCode));
                resultList.forEach(item ->{
                    String deviceCode = item.getParkDeviceCodes();
                    String[] split = deviceCode.split(",");
                    int count = 0;
                    for (int i = 0; i < split.length; i++) {
                        String deviceCodes = split[i];
                        if (groupBySex.containsKey(deviceCodes) && groupBySex.get(deviceCodes) != null && groupBySex.get(deviceCodes).size() > 0) {
                            for (ReceiveAlarmVO item3:groupBySex.get(deviceCodes)) {
                                String regEx="[^0-9]";
                                Pattern p = Pattern.compile(regEx);
                                String recount = StringUtils.isEmpty(p.matcher(item3.getAlarmBody()).replaceAll("").trim()) ? "0": p.matcher(item3.getAlarmBody()).replaceAll("").trim();
                                count = Integer.parseInt(recount) + count ;
                            }
                        }
                    }
                    item.setParkTrafficCount(String.valueOf(count));
                    String percent = new BigDecimal(count).divide(new BigDecimal(item.getParkCarryingCapacity()),Constants.NINE_NUM,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(Constants.NINE_NUM,BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString();
                    item.setParkPercent(percent);
                });
                resultList = resultList.stream().sorted(Comparator.comparing(ParkDeviceInfoVo::getParkPercent,Comparator.comparingDouble(Double::parseDouble)).reversed()).collect(Collectors.toList());
            }catch (Exception e){
                log.error(MessageConstants.PARK_PEOPLE_ERROR,e);
            }

        }

        return resultList;
    }

    @ApiOperation(tags = "新增公园摄像机机", value = "")
    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE,tenantColumn="p.tenant_id",industryCode="p.industry_code")
    public Result addParkDeviceInfo(ParkDeviceInfoReq req) {
        ParkDeviceInfoVo parkDeviceInfoVo = new ParkDeviceInfoVo();
        BeanUtils.copyProperties(req,parkDeviceInfoVo);
        // 获取当前登录人
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if(currentUser != null && !StringUtils.isEmpty(currentUser.getUser().getTenantId())){
            SysUser user =sysMapper.selectUserOnlyByUserId(currentUser.getUser().getUserId());
            parkDeviceInfoVo.setCreator(user.getUserName());
            parkDeviceInfoVo.setTenantId(currentUser.getUser().getTenantId());
        }else{
            return Result.fail (Constants.CHECK_TENANT_ID);
        }
        List<ParkDeviceInfoVo> resultList = parkDeviceInfoDAO.selectVmParkDeviceInfoCount(req);
        if (!CollectionUtils.isEmpty(resultList)) {
            return Result.fail (MessageConstants.PARK_NAME_ERROR);
        }
        parkDeviceInfoDAO.insertVmParkDeviceInfo(parkDeviceInfoVo);
        return  Result.success("操作成功");
    }

    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE,tenantColumn="p.tenant_id",industryCode="p.industry_code")
    public Result updateParkDeviceInfo(ParkDeviceInfoReq req) {
        ParkDeviceInfoVo vo = new ParkDeviceInfoVo();
        BeanUtils.copyProperties(req,vo);
        // 获取当前登录人
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if(currentUser != null && !StringUtils.isEmpty(currentUser.getUser().getTenantId())){
            SysUser user =sysMapper.selectUserOnlyByUserId(currentUser.getUser().getUserId());
            vo.setModifier(user.getUserName());
        }else{
            return Result.fail(Constants.CHECK_TENANT_ID);
        }
        List<ParkDeviceInfoVo> resultList = parkDeviceInfoDAO.selectVmParkDeviceInfoCount(req);
        if (!CollectionUtils.isEmpty(resultList) && !Constants.YES_FLAG.equals(req.getDeleteFlag())) {
            return Result.fail(MessageConstants.PARK_NAME_ERROR);
        }
        parkDeviceInfoDAO.updateVmParkDeviceInfo(vo);
            return Result.success("操作成功");
        }

    private String genDateOrderSql(String createDateOrder, String updateDateOrder) {
        StringBuffer orderSql = new StringBuffer();
        if (createDateOrder != null) {
            orderSql.append("creator_date ").append(createDateOrder);
        } else if (updateDateOrder != null) {
            orderSql.append("modifier_date ").append(updateDateOrder);
        }
        return orderSql.toString();
    }



    private List<ReceiveAlarmVO> getOrderAlarmByTenantCode(ParkDeviceInfoReq req) {
        // 获取当前登录人
        LoginUser currentUser = SecurityUtils.getLoginUser();
        String tenantId= currentUser.getUser().getTenantId();
        String timeStr1= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String tableName = TableUtil.getTableName(DateUtils.string2Date(timeStr1), TableConstant.WARN_ORDER_TEBLE);
        req.setTenantId(tenantId);
        req.setTableName(tableName);
        return alarmOrderAppMapper.getOrderAlarmByTenantCode(req);
    }
}
