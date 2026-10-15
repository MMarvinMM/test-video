package com.chinatower.platform.project.system.service.impl;
import java.util.Date;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.request.JgPushRequest;
import com.chinatower.platform.entity.request.MsgSwitchRequest;
import com.chinatower.platform.entity.request.SysMsgSwitch;
import com.chinatower.platform.fegin.client.MsgFeignClient;
import com.chinatower.platform.project.custom.common.Constants;
import com.chinatower.system.api.request.notice.SysNotice;
import com.chinatower.platform.project.system.domain.dto.JPushUserDataDTO;
import com.chinatower.platform.project.system.domain.dto.NoticeQueryDTO;
import com.chinatower.platform.project.system.domain.vo.GetNoticeReqVo;
import com.chinatower.platform.project.system.enums.AppNoticeType;
import com.chinatower.platform.project.system.mapper.SysDeptMapper;
import com.chinatower.platform.project.system.mapper.SysNoticeMapper;
import com.chinatower.platform.project.system.service.ISysNoticeService;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageHelper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.yaml.snakeyaml.util.UriEncoder;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 公告 服务层实现
 *
 * @author iwhalecloud
 */
@Service
@Slf4j
public class SysNoticeServiceImpl implements ISysNoticeService {
    @Resource
    private SysNoticeMapper noticeMapper;
    @Resource
    private SysDeptMapper sysDeptMapper;
    @Resource
    private MsgFeignClient msgFeignClient;

    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    private static Pattern IMG_PATTERN = Pattern.compile("<(img)(.*?)(/>|></img>|>)", Pattern.CASE_INSENSITIVE);
    private static Pattern SRC_PATTERN = Pattern.compile("(src|SRC)=(\"|\')(.*?)(\"|\')");

    private static final String FILE_URL = "fileUrl";

    private static final String NOTICE_TYPE = "noticeType";
    /**
     * 查询公告信息
     *
     * @param noticeId 公告ID
     * @return 公告信息
     */
    @Override
    @DataScope(tenantColumn = "tenant_id", industryCode = "industry_code", appVerCode = "app_ver_code", deptColumn = "dept_id")
    public SysNotice selectNoticeById(NoticeQueryDTO noticeId) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        noticeId.setPlatformCode(user.getPlatformCode());
        SysNotice sysNotice = noticeMapper.selectNoticeById(noticeId);
        if (Objects.nonNull(sysNotice)) {
            try {
                String noticeContent = sysNotice.getNoticeContent();
                Set<String> imgStr = getImgStr(noticeContent);
                Map<String, String> srcMap = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList<>(imgStr));
                for (String srcUrl : imgStr) {
                    if(!StringUtils.isEmpty(srcMap.get(srcUrl))){
                        noticeContent = noticeContent.replace(srcUrl, srcMap.get(srcUrl));
                    }
                }
                sysNotice.setNoticeContent(noticeContent);
            } catch (Exception e) {
                log.error("解析notice content图片失败", e);
            }
            try {
                String noticeBanner = sysNotice.getNoticeBanner();
                JSONArray jsonArray = JSON.parseArray(noticeBanner);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String s = fileUrlEncryptUtil.dealObsFileUrl(jsonObject.getString(FILE_URL));
                jsonObject.put(FILE_URL, s);
                sysNotice.setNoticeBanner(JSON.toJSONString(jsonArray));
            } catch (Exception e) {
                log.error("解析notice banner图片失败", e);
            }
        }
        return sysNotice;
    }

    /**
     * 查询公告列表
     *
     * @param notice 公告信息
     * @return 公告集合
     */
    @DataScope(tenantColumn = "a.tenant_id", industryCode = "a.industry_code", acceptColumn = "ic.accept_node",appVerCode = "a.app_ver_code")
    @Override
    public List<SysNotice> selectNoticeList(SysNotice notice) {
        String[] orderByColumnStr = {"noticeTitle", NOTICE_TYPE, "createBy","createTime","publishTime","status"};
        List<String> variableList = Arrays.asList(orderByColumnStr);
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String platformCode = user.getPlatformCode();
        notice.setPlatformCode(platformCode);
        if (!variableList.contains(notice.getOrderByColumn())) {
            notice.setOrderByColumn("");
        }
        String orderByStr ="";
        if("createBy".equals(notice.getOrderByColumn())){
            orderByStr = " a.create_by " + notice.getIsAsc();
        }else if("createTime".equals(notice.getOrderByColumn())){
            orderByStr = " a.create_time " + notice.getIsAsc();
        }else if("status".equals(notice.getOrderByColumn())){
            orderByStr = " a.status " + notice.getIsAsc();
        }else{
            orderByStr = notice.getOrderBy();
        }
        PageHelper.startPage(notice.getPageNum(), notice.getPageSize(), orderByStr);
        return noticeMapper.selectNoticeList(notice);
    }

    /**
     * 查询公告列表
     *
     * @param notice 公告信息
     * @return 公告集合
     */
    @DataScope(tenantColumn = "a.tenant_id", deptColumn = "a.dept_id", industryCode = "a.industry_code", appVerCode = "a.app_ver_code")
    @Override
    public List<SysNotice> getNoticeList(SysNotice notice) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String platformCode = user.getPlatformCode();
        notice.setPlatformCode(platformCode);
        List<SysNotice> noticeList = noticeMapper.getNoticeList(notice);
        //整改for循环 modify by liuxt 20240806
        Set<String> fileUrls = new HashSet<>();
        if (CollUtil.isNotEmpty(noticeList)) {
            noticeList.stream().forEach(n -> {
                if (StringUtils.isNotBlank(n.getNoticeBanner())) {
                    try {
                        JSONArray jsonArray = JSONArray.parseArray(n.getNoticeBanner());
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String fileUrl = jsonObject.getString(FILE_URL);
                            fileUrls.add(fileUrl);
                        }
                    } catch (Exception e) {

                    }
                }
            });
            //整改for循环 modify by liuxt 20240806
            if(!fileUrls.isEmpty()){
                // 处理Obs文件URL加密返回
                Map<String, String> result = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList<String>(fileUrls));
                noticeList.stream().forEach(n -> {
                    if (StringUtils.isNotBlank(n.getNoticeBanner())) {
                        JSONArray jsonArray = JSONArray.parseArray(n.getNoticeBanner());
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String oriFileUrl = jsonObject.getString(FILE_URL);
                            if (!StringUtils.isEmpty(oriFileUrl)) {
                                jsonObject.put(FILE_URL,result.get(oriFileUrl));
                            }
                        }
                    }
                });
            }
        }
        return noticeList;
    }

    /**
     * 新增公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int insertNotice(SysNotice notice) {
        notice.setCreateTime(new Date());
        notice.setCreateBy(SecurityUtils.getUsername());
        notice.setDeptId(SecurityUtils.getLoginUser().getUser().getDeptId());
        return noticeMapper.insertNotice(notice);
    }

    /**
     * 修改公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    @DataScope(tenantColumn = "tenant_id", industryCode = "industry_code", appVerCode = "app_ver_code", deptColumn = "DEPT_ID")
    public int updateNotice(SysNotice notice) {
        notice.setUpdateTime(new Date());
        notice.setUpdateBy(SecurityUtils.getUsername());
        if(!StringUtils.isNotBlank(notice.getNoticeBanner())){
            notice.setNoticeBanner(null);
        }
        int i = noticeMapper.updateNotice(notice);
        log.info("判断是否推送激光消息{}",notice.getReceiveEnd().contains("2"));
        if (notice.getReceiveEnd().contains("2") && com.chinatower.platform.common.constant.Constants.ONE.equals(notice.getStatus())){
            //推送消息
            CompletableFuture.runAsync(() -> jPushNotice(notice));
        }
        return i;
    }

    /**
     * 推送激光
     * @param notice
     */
    public void jPushNotice(SysNotice notice){
        log.info("开始推送激光消息{}",JSON.toJSONString(notice));
        //查询需要推送的成员
        List<JPushUserDataDTO> list;
        Integer count = 0;
        //如果推送全部，分页查询
        if (com.chinatower.platform.common.constant.Constants.ONE.equals(notice.getIfAllReceive())){
            count = sysDeptMapper.selectTenantUserListCount(notice.getTenantId(), notice.getIndustryCode());
        }
        //每页数据
        int pageSize = 1000;
        //总页数
        int totalPageNum = count / pageSize + 1;
        for (int pageNum = 0; pageNum < totalPageNum; pageNum++) {
            if(com.chinatower.platform.common.constant.Constants.ONE.equals(notice.getIfAllReceive())){
                PageHelper.startPage(pageNum + 1, pageSize, false);
                list = sysDeptMapper.selectTenantUserList(notice.getTenantId(),notice.getIndustryCode());
            }else {
                list = Arrays.stream(notice.getReceiveUsers().split(",")).map(userId -> {
                    JPushUserDataDTO dto = new JPushUserDataDTO();
                    dto.setUserId(userId);
                    dto.setIndustryCode(notice.getIndustryCode());
                    return dto;
                }).collect(Collectors.toList());
            }
            if (list == null || list.isEmpty()){
                return;
            }
            log.info("需要推送的用户数据 {}",JSON.toJSONString(list));
            //查询成员推送开关
            List<MsgSwitchRequest> userList = list.stream().map(dto -> {
                MsgSwitchRequest switchRequest = new MsgSwitchRequest();
                switchRequest.setUserId(dto.getUserId());
                switchRequest.setIndustryCode(dto.getIndustryCode());
                return switchRequest;
            }).collect(Collectors.toList());
            Result<Map<String, SysMsgSwitch>> result = msgFeignClient.queryByUserList(userList);
            if (result.getCode() == ResultCode.FAILURE.getCode()){
                log.info("查询用户推送消息开关报错 {}",notice.getNoticeId());
                return;
            }
            log.info("{} 用户推送数据开关 {}",notice.getNoticeId(),JSON.toJSONString(result));
            if (result.getData() != null){
                Map<String, SysMsgSwitch> maps = result.getData();
                list.forEach(dto ->{
                    SysMsgSwitch o = maps.get(dto.getUserId() + "_" + dto.getIndustryCode());
                    if (o != null){
                        dto.setMsgType(o.getNoticeSwitch()+"");
                    }
                });
            }
            log.info("组装系统消息开关");
            //组装推送消息
            List<JgPushRequest> jPushList = new ArrayList<>();
            list.forEach(dto -> {
                AppNoticeType noticeType = AppNoticeType.get(notice.getNoticeType());
                Map<String,Object> item = new HashMap<>();
                item.put("noticeId",notice.getNoticeId());
                item.put(NOTICE_TYPE,noticeType.getType());
                String itemJson = UriEncoder.encode(JSON.toJSONString(item));

                Map<String,Object> extrasparam = new HashMap<>();
                extrasparam.put("noticeId",notice.getNoticeId());
                extrasparam.put(NOTICE_TYPE,noticeType.getType());
                extrasparam.put("appUrl","/pages/message/message-detail?detail=" + itemJson);

                String userId = dto.getUserId();

                JgPushRequest jgPushRequest = new JgPushRequest();
                jgPushRequest.setAlias(userId);
                jgPushRequest.setNotificationTitle(noticeType.getDesc());
                jgPushRequest.setMsgTitle(notice.getNoticeType());
                jgPushRequest.setMsgContent(notice.getNoticeTitle());
                jgPushRequest.setExtrasparam(JSON.toJSONString(extrasparam));
                jgPushRequest.setMsgType(dto.getMsgType());
                jgPushRequest.setType(noticeType.getType());
                jgPushRequest.setMsgId(notice.getNoticeId()+"");
                jgPushRequest.setAppVerCode(notice.getAppVerCode());
                jgPushRequest.setTenantId(notice.getTenantId());
                jgPushRequest.setUserId(userId);
                jgPushRequest.setIndustryCode(notice.getIndustryCode());
                jPushList.add(jgPushRequest);
            });
            log.info("开始推送激光消息{}",JSON.toJSONString(jPushList));
            try {
                Result jPushResult = msgFeignClient.batchPushNews(jPushList);
                log.info("激光推送结果{}",JSON.toJSONString(jPushResult));
            }catch (Exception e){
                log.error("发送激光错误",e);
            }
        }

    }

    /**
     * 删除公告对象
     *
     * @param noticeId 公告ID
     * @return 结果
     */
    @Override
    @DataScope(tenantColumn = "tenant_id", industryCode = "industry_code", appVerCode = "app_ver_code", deptColumn = "DEPT_ID")
    public int deleteNoticeById(NoticeQueryDTO noticeId) {
        return noticeMapper.deleteNoticeById(noticeId);
    }

    /**
     * 批量删除公告信息
     *
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    @Override
    @DataScope(tenantColumn = "tenant_id", industryCode = "industry_code", appVerCode = "app_ver_code", deptColumn = "DEPT_ID")
    public int deleteNoticeByIds(NoticeQueryDTO noticeIds) {
        return noticeMapper.deleteNoticeByIds(noticeIds);
    }

    /**
     * 查询当前用户待阅公告列表
     *
     * @param user 当前用户信息
     * @return 待阅公告列表
     */
    @Override
    @DataScope(tenantColumn = "a.tenant_id", industryCode = "a.industry_code", appVerCode = "a.app_ver_code", deptColumn = "a.DEPT_ID")
    public List<SysNotice> selectToReadNoticeByUser(SysUser user) {
        return noticeMapper.selectToReadNoticeByUser(user);
    }

    @Override
    @DataScope(tenantColumn = "a.tenant_id", industryCode = "a.industry_code", appVerCode = "a.app_ver_code", deptColumn = "a.DEPT_ID")
    public List<SysNotice> selectAppToReadNoticeByUser(GetNoticeReqVo vo) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String platformCode = user.getPlatformCode();
        vo.setPlatformCode(platformCode);
        return noticeMapper.selectAppToReadNoticeByUser(vo);
    }

    @Override
    @DataScope(tenantColumn = "T.tenant_id", industryCode = "T.industry_code", appVerCode = "T.app_ver_code", deptColumn = "T.DEPT_ID")
    public List<SysNotice> selectAppNoticeByUser(GetNoticeReqVo vo) {
        List<SysNotice> sysNotices = noticeMapper.selectAppNoticeByUser(vo);
        for (SysNotice sysNotice : sysNotices) {
            String noticeBanner = sysNotice.getNoticeBanner();
            if (!StringUtils.isEmpty(noticeBanner)) {
                JSONArray parse = (JSONArray) JSONArray.parse(noticeBanner);
                if (parse.size() > 0) {
                    String fileUrl = (String) ((JSONObject) parse.get(0)).get(FILE_URL);
                    String fileName = (String) ((JSONObject) parse.get(0)).get("fileName");
                    if (!StringUtils.isEmpty(fileName)) {
                        String fileExt = FilenameUtils.getExtension(fileName).toUpperCase();
                        if (!("MP4").equals(fileExt)) {
                            //add by liuxt video-file改造
                            sysNotice.setFileUrl(fileUrlEncryptUtil.dealObsFileUrl(fileUrl));
                        }

                    }

                }
            }
        }
        return sysNotices;
    }

    /**
     * 插入已读记录
     *
     * @param noticeId 已读公告ID
     * @param userId   用户ID
     * @return 插入结果
     */
    @Override
    public int insertNoticeRead(Long noticeId, String userId) {
        if (noticeMapper.countNoticeReadRecord(noticeId, userId) == 0) {
            return noticeMapper.insertNoticeRead(noticeId, userId);
        }
        return 0;
    }

    /**
     * 查询未读公告数量
     *
     * @param vo
     * @return 插入结果
     */
    @Override
    @DataScope(tenantColumn = "T.tenant_id", industryCode = "T.industry_code", appVerCode = "T.app_ver_code", deptColumn = "T.DEPT_ID")
    public int countNoticeReadRecordByUserId(GetNoticeReqVo vo) {
        return noticeMapper.countNoticeReadRecordByUserId(vo);

    }

    @Override
    @DataScope(tenantColumn = "tenant_id", industryCode = "industry_code", appVerCode = "app_ver_code", deptColumn = "DEPT_ID")
    public void updateStatus(NoticeQueryDTO dto) {
        noticeMapper.updateStatus(dto);
    }


    /**
     * 批量插入已读记录
     *
     * @param vo
     * @return 插入结果
     */
    @Override
    public int insertNoticeReadBatch(GetNoticeReqVo vo) {
        return noticeMapper.insertNoticeReadBatch(vo);
    }


    @Override
    @DataScope(tenantColumn = "T.tenant_id", industryCode = "T.industry_code", appVerCode = "T.app_ver_code", deptColumn = "T.DEPT_ID")
    public Integer selectAppNoticeByUserCount(GetNoticeReqVo vo) {
        Integer num = Constants.ZERO;
        List<SysNotice> sysNotices = noticeMapper.selectAppNoticeByUser(vo);
        if (!CollectionUtils.isEmpty(sysNotices)) {
            num = sysNotices.size();//公告数量
        }
        return num;
    }

    public static Set<String> getImgStr(String htmlStr) {
        Set<String> pics = new HashSet<>();
        String img = "";
        Matcher imgs = IMG_PATTERN.matcher(htmlStr);
        while (imgs.find()) {
            // 得到<img />数据
            img = imgs.group();
            // 匹配<img>中的src数据
            Matcher m = SRC_PATTERN.matcher(img);
            while (m.find() && !StringUtils.isEmpty(m.group(Constants.THREE))) {
                pics.add(m.group(Constants.THREE));
            }
        }
        return pics;
    }


}