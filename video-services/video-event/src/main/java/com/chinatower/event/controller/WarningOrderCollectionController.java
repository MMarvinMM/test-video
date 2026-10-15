package com.chinatower.event.controller;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.chinatower.event.annotation.EventObsEncrypt;
import com.chinatower.event.dataobject.WarningOrderCollectionDO;
import com.chinatower.event.dto.AlarmOrderDTO;
import com.chinatower.event.repository.ISysService;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.BigScreenOrderListDTO;
import com.chinatower.event.entity.vo.WarningOrderListVO;
import com.chinatower.event.repository.IWarningOrderCollectionRepository;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * video-event
 * <p>
 * 告警信息收藏表 前端控制器
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-27
 */
@RestController
@Slf4j
@RequestMapping(value = "/alarm/order/collection")
public class WarningOrderCollectionController extends TowerBaseController {

    @Autowired
    private IWarningOrderCollectionRepository warningOrderCollectionRepository;
    @Autowired
    private ISysService sysService;
    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    /**
     * 功能描述：我的收藏-查询列表
     *
     * @param dto
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 13:45
     * @version:
     */
    @EventObsEncrypt
    @PostMapping("/qryWarningOrderCollection")
    public Result qryWarningOrderCollection(@RequestBody AlarmOrderDTO dto) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null || StringUtils.isEmpty(loginUser.getUser().getUserId())) {
            // 无法获取当前登录人，返回错误信息
            return Result.fail(Constants.NO_USER_INFO);
        }

        if(!CollectionUtils.isEmpty(dto.getOrderStatusList()) &&dto.getOrderStatusList().contains(Constants.NINE)){
            dto.setEnableAcceptanceMode(Constants.ONE);
            //dto.getOrderStatusList().clear();
        }
        String loginUserId = sysService.getLoginUserId();
        dto.setCollectionCreator(loginUserId);
        dto.setRoleIds(loginUser.getUser().getRoleIds());
        dto.setAdmin(Constants.ONE.equals(loginUser.getUser().getIsTenantAdmin()));
        startPage();
        List<WarningOrderListVO> list = warningOrderCollectionRepository.qryWarningOrderCollection(dto);

        if (CollectionUtils.isEmpty(list)) {
            return Result.success("未查询到收藏数据");
        }
        return result(list);
    }

    /**
     * 功能描述：我的收藏-新增收藏
     *
     * @param req
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 13:45
     * @version:
     */
    @PostMapping("/addWarningOrderCollection")
    public Result addWarningOrderCollection(@RequestBody WarningOrderCollectionDO req) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null || StringUtils.isEmpty(loginUser.getUser().getUserId())) {
            // 无法获取当前登录人，返回错误信息
            return Result.fail(Constants.NO_USER_INFO);
        }
        if (StringUtils.isEmpty(req.getWarningOrderId())) {
            return Result.fail(Constants.WARNING_ORDER_ID_MSG);
        }
        if (Objects.isNull(req.getAlarmTime())) {
            return Result.fail(Constants.ALARM_TIME_MSG);
        }
        try {
            String loginUserId = sysService.getLoginUserId();
            req.setCollectionCreator(loginUserId);
            warningOrderCollectionRepository.addWarningOrderCollection(req);
        }
        catch (Exception e) {
            return Result.fail(e.getMessage());
        }
        return Result.success("");
    }

    /**
     * 功能描述：我的收藏-取消收藏
     *
     * @param req
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 13:46
     * @version:
     */
    @PostMapping("/delWarningOrderCollection")
    public Result delWarningOrderCollection(@RequestBody WarningOrderCollectionDO req) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null || StringUtils.isEmpty(loginUser.getUser().getUserId())) {
            // 无法获取当前登录人，返回错误信息
            return Result.fail(Constants.NO_USER_INFO);
        }
        if (StringUtils.isEmpty(req.getWarningOrderId())) {
            return Result.fail(Constants.WARNING_ORDER_ID_MSG);
        }
        try {
            String loginUserId = sysService.getLoginUserId();
            req.setCollectionCreator(loginUserId);
            warningOrderCollectionRepository.delWarningOrderCollection(req);
        }
        catch (Exception e) {
            return Result.fail(e.getMessage());
        }
        return Result.success("");
    }

    /**
     * 功能描述：我的收藏-大屏展示
     *
     * @param dto
     * @return TableDataInfo
     * @author zhen.yan
     * @date 2022/6/15 13:46
     * @version:
     */
    @PostMapping("/qryWarningOrderCollectionForBigScreen")
    @DataScope(dbType = EbikeDataBaseConstant.BASE, tenantColumn="dataTable.tenant_id", industryCode="dataTable.industry_code",
            typeValueColumn = "dataTable.warning_type_id")
    public TableDataInfo qryWarningOrderCollectionForBigScreen(@RequestBody BigScreenOrderListDTO dto) {
        startPage();
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null || StringUtils.isEmpty(loginUser.getUser().getUserId())) {
            // 无法获取当前登录人，返回错误信息
            return getDataTable(new ArrayList<>());
        }
        String loginUserId = sysService.getLoginUserId();
        dto.setCollectionCreator(loginUserId);
        if(Constants.NINE.equals(dto.getOrderStatus())){
            dto.setEnableAcceptanceMode(Constants.ONE);
            dto.setOrderStatus("");
        }
        if(!CollectionUtils.isEmpty(dto.getOrderStatusList()) &&dto.getOrderStatusList().contains(Constants.NINE)){
            dto.setEnableAcceptanceMode(Constants.ONE);
        }
        List<Map> list = warningOrderCollectionRepository.qryWarningOrderCollectionForBigScreen(dto);
        if(!CollectionUtils.isEmpty(list)){
            List<String> imgUrlList = list.stream().map(item -> String.valueOf(item.get(Constants.PARAM_IMG_URL))).filter(Objects::nonNull).collect(Collectors.toList());
            Map<String, String> stringStringMap = fileUrlEncryptUtil.dealObsFileUrlBatch(imgUrlList);
            // 处理Obs文件URL加密返回
            list.forEach(item -> item.put(Constants.PARAM_IMG_URL, stringStringMap.get(MapUtils.getString(item, Constants.PARAM_IMG_URL))));
            List<String> iconList = list.stream().map(item -> String.valueOf(item.get("icon")))
                    .filter(icon -> !StringUtils.isEmpty(icon) && (icon.contains("http") || icon.startsWith("/"))).collect(Collectors.toList());
            Map<String, String> iconStringMap = fileUrlEncryptUtil.dealObsFileUrlBatch(iconList);
            // 处理Obs文件URL加密返回
            list.forEach(item -> {
                String icon = String.valueOf(item.get("icon"));
                if (!StringUtils.isEmpty(icon) && (icon.contains("http") || icon.startsWith("/"))){
                    item.put("icon", iconStringMap.get(icon));
                }

            });
        }
        return getDataTable(list);
    }

}
