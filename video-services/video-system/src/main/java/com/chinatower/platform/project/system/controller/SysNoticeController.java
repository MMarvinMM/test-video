package com.chinatower.platform.project.system.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.common.utils.RequestUtils;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.common.constant.Constants;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.common.utils.security.AESUtil;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.system.api.request.notice.SysNotice;
import com.chinatower.platform.project.system.domain.dto.NoticeQueryDTO;
import com.chinatower.platform.project.system.domain.vo.GetNoticeReqVo;
import com.chinatower.platform.project.system.domain.vo.SysJpushRecord;
import com.chinatower.platform.project.system.service.ISysJpushRecord;
import com.chinatower.platform.project.system.service.ISysNoticeService;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * video-system
 * 公告 信息操作处理
 *
 * @author iwhalecloud
 */
@Api(tags = "公告 信息操作处理")
@Slf4j
@RestController
public class SysNoticeController extends TowerBaseController {
    @Autowired
    private ISysNoticeService noticeService;
    @Autowired
    private ISysJpushRecord ISysJpushRecord;

    private static final String TENANT_ADMIN = "1";

    private static final String NO_INPUT = "】，请勿输入脚本代码";

    private static final String NOTICE_CONTAIN = "公告内容包含非法字符【";
    /**
     * 获取通知公告列表
     */
    @ApiOperation(value = "获取通知公告列表")
    @PostMapping("/system/notice/list")
    public TableDataInfo noticeList(@RequestBody SysNotice notice) {
        List<SysNotice> list = noticeService.selectNoticeList(notice);
        return getDataTable(list);
    }

    /**
     * 获取通知公告列表-app使用
     */
    @ApiOperation(value = "获取通知公告列表")
    @PostMapping("/system/notice/appList")
    public TableDataInfo appList(@RequestBody SysNotice notice) {
        startPage();
        List<SysNotice> list = noticeService.selectNoticeList(notice);
        return getDataTable(list);
    }

    /**
     * 获取通知公告列表
     * @param notice (status 公告状态)
     * @param notice (noticeType 公告类型)
     * @return SysNotice
     */
    @ApiOperation(value = "获取通知公告列表")
    @GetMapping("/system/notice/getList")
    public TableDataInfo getList(SysNotice notice) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        //是否租户管理员(0否,1是)
        String tenantAdmin = user.getIsTenantAdmin();
        notice.setTenantId(user.getTenantId());
        notice.setAppVerCode(user.getAppVerCode());
        notice.setIndustryCode(user.getIndustryCode());
        notice.setUserId(user.getUserId());
        List<SysNotice> list = noticeService.getNoticeList(notice);
        return getDataTable(list);
    }

    /**
     * 根据通知公告编号获取详细信息
     */
    @ApiOperation(value = "根据通知公告编号获取详细信息")
    @GetMapping(value = "/system/notice/detail/{noticeId}")
    public AjaxResult getInfo(@PathVariable Long noticeId) {
        NoticeQueryDTO dto = new NoticeQueryDTO();
        dto.setNoticeId(noticeId);
        return AjaxResult.success(noticeService.selectNoticeById(dto));
    }

    /**
     * 校验内容是否包含XSS危险字符
     * @param content 公告内容
     * @return 如果有危险字符返回错误信息，否则返回null
     */
    private String validateXss(String content) {
        if (content == null || content.isEmpty()) {
            return null;
        }

        String lowerContent = content.toLowerCase();

        // 危险标签
        String[] dangerousTags = {"<script", "</script", "<iframe", "</iframe", "<object", "<embed"};
        for (String tag : dangerousTags) {
            if (lowerContent.contains(tag)) {
                return NOTICE_CONTAIN + tag + NO_INPUT;
            }
        }

        // 危险事件属性
        String[] dangerousEvents = {"onclick=", "onerror=", "onload=", "onmouseover=", "onfocus=", "onblur="};
        for (String event : dangerousEvents) {
            if (lowerContent.contains(event)) {
                return NOTICE_CONTAIN + event + NO_INPUT;
            }
        }

        // 危险协议
        if (lowerContent.contains("javascript:")) {
            return NOTICE_CONTAIN + "javascript:" + NO_INPUT;
        }

        return null;
    }


    /**
     * 新增通知公告
     */
    @ApiOperation(value = "新增通知公告")
    @TowerLog(title = "通知公告", businessType = BusinessType.INSERT)
    @PostMapping(value = "/system/notice/add")
    public Result add(@Validated @RequestBody SysNotice notice) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        if(Constants.ONE.equals(notice.getIfAllReceive())){
            notice.setReceiveDepts(null);
            notice.setReceiveRoles(null);
            notice.setReceiveUsers(null);
        }
        notice.setTenantId(user.getTenantId());
        // 解密 + XSS过滤
        String decryptedContent = AESUtil.decryptBase64(notice.getNoticeContent());
        // ========== 新增：XSS校验 ==========
        String validationResult = validateXss(decryptedContent);
        if (validationResult != null) {
            return Result.fail(validationResult);
        }
        // ================================
        notice.setNoticeContent(decryptedContent);
        notice.setCreateBy(SecurityUtils.getUsername());
        notice.setStatus("0");
        notice.setIndustryCode(user.getIndustryCode());
        notice.setAppVerCode(user.getAppVerCode());
        return toAjax(noticeService.insertNotice(notice));
    }

    /**
     * 修改通知公告
     */
    @ApiOperation(value = "修改通知公告")
    @TowerLog(title = "通知公告", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/system/notice/update")
    public Result edit(@Validated @RequestBody SysNotice notice) {
        if(Constants.ONE.equals(notice.getIfAllReceive())){
            notice.setReceiveDepts(null);
            notice.setReceiveRoles(null);
            notice.setReceiveUsers(null);
        }
        // 解密 + XSS过滤
        String decryptedContent = AESUtil.decryptBase64(notice.getNoticeContent());
        // ========== 新增：XSS校验 ==========
        String validationResult = validateXss(decryptedContent);
        if (validationResult != null) {
            return Result.fail(validationResult);
        }
        // ================================
        notice.setNoticeContent(decryptedContent);
        notice.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(noticeService.updateNotice(notice));
    }

    /**
     * 通知公告发布
     */
    @ApiOperation(value = "通知公告发布")
    @TowerLog(title = "通知公告", businessType = BusinessType.UPDATE)
    @GetMapping("/system/notice/publish/{noticeId}")
    public AjaxResult updateStatus(@PathVariable("noticeId")Long noticeId) throws Exception {
        NoticeQueryDTO dto = new NoticeQueryDTO();
        dto.setNoticeId(noticeId);
        SysNotice notice = noticeService.selectNoticeById(dto);
        if(notice==null){
            return AjaxResult.error("发布失败：公告信息查询异常，公告不存在或已被删除");
        }
        notice.setStatus(Constants.ONE);
        notice.setPublishTime(new Date());
        notice.setUpdateBy(SecurityUtils.getUsername());
        return AjaxResult.judge(noticeService.updateNotice(notice));
    }

    /**
     * 删除通知公告
     */
    @ApiOperation(value = "删除通知公告")
    @TowerLog(title = "通知公告", businessType = BusinessType.DELETE)
    @GetMapping("/system/notice/del/{noticeIds}")
    public Result remove(@PathVariable Long[] noticeIds) {
        NoticeQueryDTO dto = new NoticeQueryDTO();
        dto.setNoticeIds(noticeIds);
        return toAjax(noticeService.deleteNoticeByIds(dto));
    }

    /**
     * 获取待阅通知公告
     */
    @ApiOperation(value = "获取待阅通知公告")
    @GetMapping(value = "/base/system/notice/toRead")
    public AjaxResult getToReadNotice() {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        PageHelper.startPage(1,100);
        return AjaxResult.success(noticeService.selectToReadNoticeByUser(user));
    }

    /**
     * 获取App端用户的待阅通知公告
     */
    @ApiOperation(value = "获取App端用户的待阅通知公告")
    @PostMapping(value = "/base/system/appNotice/toRead")
    public AjaxResult getAppToReadNotice(@RequestBody GetNoticeReqVo vo) {
        PageHelper.startPage(1,100);
        return AjaxResult.success(noticeService.selectAppToReadNoticeByUser(vo));
    }

    /**
     * 已阅
     */
    @ApiOperation(value = "readed")
    @GetMapping("/base/system/notice/readed/{noticeId}")
    public Result updateReadNotice(@PathVariable Long noticeId) {
        String userId = SecurityUtils.getLoginUser().getUser().getUserId();
        return Result.success(noticeService.insertNoticeRead(noticeId,userId)>0?"操作成功":"已阅");
    }

    /**
     * 查询公告记录数
     */
    @ApiOperation(value = "readed")
    @PostMapping("/base/system/notice/countNoticeReadRecordByUserId")
    public Result<Integer> countNoticeReadRecordByUserId(@RequestBody GetNoticeReqVo vo) {
        Result<Integer> result;
        String userId = SecurityUtils.getLoginUser().getUser().getUserId();

        vo.setUserId(userId.toString());
        //获取通知消息数量
        int num =  noticeService.countNoticeReadRecordByUserId(vo);
        //获取极光推送消息数量

        SysJpushRecord sysJpushRecord= new SysJpushRecord();
        HttpServletRequest request = RequestUtils.getRequest();
        String clientVersion = request.getHeader("client_version");
        log.info("获取请求头client_version:{}", clientVersion);
        if (StringUtils.isNotEmpty(clientVersion)) {
            //极光消息增加行业区分  设置alias=userId_industryCode
            String industryCode = SecurityUtils.getLoginUser().getUser().getIndustryCode();
            if (StringUtils.isEmpty(industryCode)) {
                industryCode = com.chinatower.platform.project.system.constants.Constants.TOWER_INDUSTRY_CODE;
            }
            sysJpushRecord.setAlias(userId + "_" + industryCode);
        } else {
            sysJpushRecord.setAlias(userId);
        }
        log.info("查询极光记录入参:{}", sysJpushRecord);
        int jgNum = ISysJpushRecord.countJpushRecord(sysJpushRecord);
        result = Result.success("查询成功");
        result.setData(num+jgNum);
        return result;

    }

    /**
     * 获取App端用户的通知公告
     */
    @ApiOperation(value = "获取App端用户的通知公告")
    @PostMapping(value = "/base/system/appNotice/getAppNotice")
    public AjaxResult getAppNotice(@RequestBody GetNoticeReqVo vo) {
        int pageNum = vo.getPageNum();
        int pageSize = vo.getPageSize();
        if (0 != pageNum && 0 != pageSize) {
            PageHelper.startPage(pageNum, pageSize);
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        vo.setUserId(user.getUserId());
        vo.setDeptId(user.getDeptId());
        return AjaxResult.success(noticeService.selectAppNoticeByUser(vo));
    }

    /**
     * 修改通知公告状态
     */
    @ApiOperation(value = "修改通知公告状态")
    @TowerLog(title = "通知公告", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/base/system/appNotice/updateStatus")
    public AjaxResult updateStatus(@RequestBody SysNotice sysNotice)throws Exception {
        if (Constants.ONE.equals(sysNotice.getStatus())){//如果修改时间为发布则直接走发布接口
            return updateStatus(sysNotice.getNoticeId());
        }
        NoticeQueryDTO dto = new NoticeQueryDTO();
        dto.setNoticeId(sysNotice.getNoticeId());
        dto.setStatus(sysNotice.getStatus());
        noticeService.updateStatus(dto);
        return AjaxResult.success("修改成功");
    }

}
