package com.chinatower.platform.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.event.api.ITowerEventWarningService;
import com.chinatower.event.api.ITradeWarningOrderService;
import com.chinatower.event.api.model.LocalImgReq;
import com.chinatower.event.api.model.SatelliteAlarmApiReq;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.entity.vo.*;
import com.chinatower.platform.common.utils.DateUtils;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.common.utils.poi.ExcelUtil;
import com.chinatower.platform.dto.AlarmOrderDTO;
import com.chinatower.platform.dto.DownloadVideoByUrlReq;
import com.chinatower.platform.dto.ReceiveSatelliteAlarmDto;
import com.chinatower.platform.dto.SatelliteAlarmResp;
import com.chinatower.platform.dto.local.InterceptOrderEntity;
import com.chinatower.platform.dto.local.InterceptResp;
import com.chinatower.platform.dto.local.WarningOrderEntity;
import com.chinatower.platform.dto.local.WarningOrderResp;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.service.EventExportService;
import com.chinatower.platform.utils.SecurityUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * video-export
 * 功能说明: event模块导出功能类
 *
 * @author lyy
 * @date 2021-12-09 15:09
 * @requestID
 */
@RestController
@Slf4j
@RequestMapping(value = "/alarm/order")
public class EventExportController extends TowerBaseController {
    @Autowired
    private EventExportService eventExportService;

    @Autowired
    private ITradeWarningOrderService iTradeWarningOrderService;

    @Resource
    private ITowerEventWarningService iTowerEventWarningService;

    /**
     *
     * 导出告警事件列表
     * 功能描述 导出告警事件列表
     *
     * @author lyy
     * @date 2022/12/09 15:39
     * @version:
     */
    @PostMapping("/test")
    public String test(String key) throws InterruptedException {
        String begin = DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", new Date());
        Thread.sleep(100000);
        String end = DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", new Date());
        return begin + "------------" + end;
    }


    /**
     *
     * 导出告警事件列表
     * 功能描述 导出告警事件列表
     *
     * @param dto
     * @param response
     * @author lyy
     * @date 2022/12/09 15:39
     * @version:
     */
    @PostMapping("/exportWarningOrderList")
    public void exportWarningOrderList(@RequestBody AlarmOrderDTO dto, HttpServletResponse response) {
        try {
            List<WarningOrderExport> exportList = eventExportService.exportWarningOrderList(dto);
            byte[] data = new ExcelUtil<>(WarningOrderExport.class).exportBytes(exportList);
            FileUtils.download(response, data, "");
        } catch (Exception e) {
            log.error("导出告警列表异常：", e);
        }
    }

    /**
     *
     * 导出我的收藏列表
     * 我的收藏-导出列表
     *
     * @param dto
     * @return
     */
    @PostMapping("/exportWarningOrderCollection")
    public void exportWarningOrderCollection(@RequestBody AlarmOrderDTO dto, HttpServletResponse response) {
        try {
            List<WarningOrderExport> exportList = eventExportService.exportWarningOrderCollectionList(dto);
            byte[] data = new ExcelUtil<>(WarningOrderExport.class).exportBytes(exportList);
            FileUtils.download(response, data, "");
        } catch (Exception e) {
            log.error("导出收藏列表异常：", e);
        }
    }

    /**
     * 告警视频下载
     *
     * @param req
     * @return
     */
    @PostMapping("/downloadVideos")
    public void downloadVideos(HttpServletResponse response, HttpServletRequest request, @RequestBody DownloadVideoReq req) {

        log.info("EventExportController-downloadVideos下载视频入参:{}", JSON.toJSONString(req));
        try {
            eventExportService.downloadVideosByFfmpeg(response, request, req);
        } catch (Exception e) {
            log.error("downloadVideos-下载视频报错:" + e);
        }
    }

    /**
     * 告警图片/视频下载
     *
     * @param req
     * @return
     */
    @PostMapping("/downloadImgsAndVideos")
    public void downloadImgsAndVideos(HttpServletResponse response, HttpServletRequest request, @RequestBody DownloadImgsAndVideoReq req) {
        log.info("EventExportController-downloadImgsAndVideos下载图片/视频入参:{}", JSON.toJSONString(req));
        try {
            eventExportService.downloadImgsAndVideos(response, request, req);
        } catch (Exception e) {
            log.error("downloadImgsAndVideos-下载图片/视频报错:" + e);
        }
    }

    /**
     * 通过输入流告警视频下载
     *
     * @param
     * @return
     */
    @PostMapping("/downloadVideoByFlush")
    public void downloadVideoByFlush(HttpServletResponse response, HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        log.info("EventExportController-downloadVideoByFlush下载流视频开始");
        // Check if file is null
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }
        String isH265 = request.getParameter("isH265");
        log.info("downloadVideoByFlush-下载合成水印视频是否是H265编码:" + isH265);
        try (InputStream videoStream = file.getInputStream()) {
            String fileName = file.getOriginalFilename();
            // Log file size
            long fileSizeBytes = file.getSize(); // Get file size in bytes
            log.info("视频流文件名: {}, 大小: {} 字节", fileName, fileSizeBytes);
            eventExportService.downloadVideoByFlush(response, request, videoStream, fileName, isH265);
        } catch (Exception e) {
            log.error("downloadVideoByFlush下载流视频开始-报错:" + e);
        }
    }

    /**
     * 通过输入流告警视频下载
     *
     * @param
     * @return
     */
    @PostMapping(value = "/downloadVideoByUrl", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Result downloadVideoByUrl(HttpServletResponse response,
                                     HttpServletRequest request,
                                     @RequestBody DownloadVideoByUrlReq req) {
        String url = req.getUrl();
        log.info("EventExportController-downloadVideoByUrl加水印并上传开始 url={}", StrUtil.blankToDefault(url, ""));
        if (StrUtil.isBlank(url)) {
            throw new IllegalArgumentException("url不能为空");
        }

        String isH265 = StrUtil.blankToDefault(req.getIsH265(), request.getParameter("isH265"));
        log.info("downloadVideoByUrl-下载合成水印视频是否是H265编码:{}", isH265);
        String uploadedUrl = eventExportService.downloadVideoByUrlAndUpload(response, request, url, isH265);
        if (StrUtil.isBlank(uploadedUrl)) {
            return Result.fail("加水印并上传失败");
        }
        return Result.data(uploadedUrl);
    }

    /**
     * 通过输入流告警图片下载
     *
     * @param
     * @return
     */
    @PostMapping("/downloadPhotoByFlush")
    public void downloadPhotoByFlush(HttpServletResponse response, HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        log.info("EventExportController-downloadVideoByFlush下载流视频开始");
        // Check if file is null
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }
        try (InputStream videoStream = file.getInputStream()) {
            String fileName = file.getOriginalFilename();
            // Log file size
            long fileSizeBytes = file.getSize(); // Get file size in bytes
            log.info("图片流文件名: {}, 大小: {} 字节", fileName, fileSizeBytes);
            eventExportService.downloadPhotoByFlush(response, request, videoStream, fileName);
        } catch (Exception e) {
            log.error("downloadVideoByFlush下载流视频开始-报错:" + e);
        }
    }

    /**
     * 下载告警工单的单个图片或者视频
     *
     * @param response 响应参数
     * @param request  请求参数
     * @param req      传入参数
     */
    @PostMapping("/downloadSingleImgOrVideo")
    public void downloadSingleImgOrVideo(HttpServletResponse response, HttpServletRequest request, @RequestBody DownloadSingleImgOrVideoReq req) {
        log.info("EventExportController-downloadSingleImgOrVideo 下载图片或者视频入参:{}", JSON.toJSONString(req));
        try {
            eventExportService.downloadSingleImgOrVideo(response, request, req);
        } catch (CustomException e) {
            log.error("EventExportController-downloadSingleImgOrVideo-下载图片/视频报错:", e);
            throw e;
        } catch (Exception e) {
            log.error("EventExportController-downloadSingleImgOrVideo-下载图片/视频系统错误:", e);
        }
    }


    /**
     * 属地告警视频下载
     *
     * @param response
     * @param request
     * @param req
     */
    @PostMapping("/downloadLocalVideos")
    public void downloadLocalVideos(HttpServletResponse response, HttpServletRequest request, @RequestBody LocalDownloadVideoReq req) {

        log.info("EventExportController-downloadVideos下载视频入参:{}", JSON.toJSONString(req));
        try {
            eventExportService.downloadLocalVideosByFfmpeg(response, request, req);
        } catch (Exception e) {
            log.error("downloadVideos-下载视频报错:" + e);
        }
    }

    /**
     * 下载属地化告警图片
     *
     * @param response
     * @param request
     * @param req
     * @throws Exception
     */
    @ApiOperation(value = "下载属地化告警图片", notes = "下载告警图片")
    @PostMapping("/downLoadImgsLocalTower")
    public void downLoadImgsLocalTower(HttpServletResponse response, HttpServletRequest request, @RequestBody AlarmLocalDownloadReq req) throws Exception {
        if (StringUtils.isEmpty(req.getName())) {
            req.setName("中国铁塔");
        }
        if (CollectionUtils.isEmpty(req.getImgReq())) {
            req.setImgReq(new ArrayList<>());
        }
        if (Constants.ONE.equals(req.getDownLoadType())) {
            //一次性最大下载五十个告警工单得图片
            req.getDownLoadOrderDTO().setPageNum(1);
            req.getDownLoadOrderDTO().setPageSize(50);
            Result result = iTowerEventWarningService.getOrderListApi(req.getDownLoadOrderDTO());
            WarningOrderResp resp = JSON.parseObject(JSON.toJSONString(result.getData()), WarningOrderResp.class);
            for (WarningOrderEntity entity : resp.getList()) {
                LocalImgReq imgReq = new LocalImgReq();
                List<String> picUrl = new ArrayList<>();
                if (!StringUtils.isEmpty(entity.getFileImgUrlSrc())) {
                    picUrl.add(entity.getFileImgUrlSrc());
                }
                if (!StringUtils.isEmpty(entity.getFileImgUrlIcon())) {
                    picUrl.add(entity.getFileImgUrlIcon());
                }
                if (!StringUtils.isEmpty(entity.getFrontAlarmPicUrl())) {
                    picUrl.add(entity.getFrontAlarmPicUrl());
                }
                imgReq.setPicUrl(picUrl);
                imgReq.setPlatformCode(entity.getAccessNode());
                imgReq.setWarningOrderId(entity.getWarningOrderId());
                req.getImgReq().add(imgReq);
            }
        } else if (Constants.TWO.equals(req.getDownLoadType())) {
            //一次性最大下载五十个告警工单得图片
            req.getDownLoadWarningInterceptDTO().setPageNum(1);
            req.getDownLoadWarningInterceptDTO().setPageSize(50);
            Result result = iTowerEventWarningService.getInterceptListApi(req.getDownLoadWarningInterceptDTO());
            InterceptResp resp = JSON.parseObject(JSON.toJSONString(result.getData()), InterceptResp.class);
            for (InterceptOrderEntity entity : resp.getList()) {
                LocalImgReq imgReq = new LocalImgReq();
                List<String> picUrl = new ArrayList<>();
                if (!StringUtils.isEmpty(entity.getFileImgUrlSrc())) {
                    picUrl.add(entity.getFileImgUrlSrc());
                }
                if (!StringUtils.isEmpty(entity.getFileImgUrlIcon())) {
                    picUrl.add(entity.getFileImgUrlIcon());
                }
                if (!StringUtils.isEmpty(entity.getFrontAlarmPicUrl())) {
                    picUrl.add(entity.getFrontAlarmPicUrl());
                }
                imgReq.setPicUrl(picUrl);
                imgReq.setPlatformCode(entity.getAccessNode());
                imgReq.setWarningOrderId(entity.getWarningOrderId());
                req.getImgReq().add(imgReq);
            }
        }
        eventExportService.downloadImgsLocal(response, request, req.getImgReq());
    }

    /**
     * 下载告警图片-铁塔
     *
     * @param response
     * @param request
     * @param req
     * @throws Exception
     */
    @ApiOperation(value = "下载告警图片", notes = "下载告警图片")
    @PostMapping("/downLoadImgsTower")
    public void downLoadImgsTower(HttpServletResponse response, HttpServletRequest request, @RequestBody AlarmDownloadReq req) throws Exception {
        if (StringUtils.isEmpty(req.getName())) {
            req.setName("中国铁塔");
        }
        //按照搜索框条件筛选下载
        if (Constants.ONE.equals(req.getDownLoadType())) {
            TableDataInfo data = iTowerEventWarningService.getOrderListImgs(req);
            if (data.getTotal() > 50) {
                //return Result.fail(com.chinatower.event.constants.Constants.ERROR_MSG_ORDER_NUM);
                throw new Exception(com.chinatower.event.constants.Constants.ERROR_MSG_ORDER_NUM);
            }
            List<WarningOrderListVO> resultList = JSONObject.parseArray(JSON.toJSONString(data.getRows()), WarningOrderListVO.class);
            if (!CollectionUtils.isEmpty(resultList)) {
                for (WarningOrderListVO vo : resultList) {
                    req.getWarningOrderIds().add(vo.getOrderId());
                }
            } else {
                throw new Exception(com.chinatower.event.constants.Constants.ERROR_MSG_ORDER_MSG);
            }
        } else if (Constants.TWO.equals(req.getDownLoadType())) {
            TableDataInfo data = iTowerEventWarningService.getInterceptListImgs(req);
            if (data.getTotal() > 50) {
                //return Result.fail(com.chinatower.event.constants.Constants.ERROR_MSG_ORDER_NUM);
                throw new Exception(com.chinatower.event.constants.Constants.ERROR_MSG_ORDER_NUM);
            }
            List<Map> list = JSONObject.parseArray(JSON.toJSONString(data.getRows()), Map.class);
            for (Map vo : list) {
                req.getWarningOrderIds().add((String) vo.get(com.chinatower.event.constants.Constants.PARAM_WARNING_ORDER_ID));
            }
        } else if (Constants.THREE.equals(req.getDownLoadType())) {
            AlarmOrderDTO dto = new AlarmOrderDTO();
            BeanUtil.copyProperties(req.getDownLoadOrderDTO(), dto);
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser == null || loginUser.getUser() == null || StringUtils.isEmpty(loginUser.getUser().getUserId())) {
                // 无法获取当前登录人，返回错误信息
                //return Result.fail(com.chinatower.event.constants.Constants.NO_USER_INFO);
                throw new Exception(com.chinatower.event.constants.Constants.NO_USER_INFO);
            }
            dto.setCollectionCreator(String.valueOf(loginUser.getUser().getUserId()));
            dto.setRoleIds(loginUser.getUser().getRoleIds());
            dto.setAdmin(com.chinatower.event.constants.Constants.ONE.equals(loginUser.getUser().getIsTenantAdmin()));
            dto.setPageSize(50);
            Result result = iTowerEventWarningService.qryWarningOrderCollectionImgs(req);
            Map<String, Object> m = (Map<String, Object>) result.getData();
            if (m.get("total") != null && Integer.valueOf(String.valueOf(m.get("total"))) > 50) {
                //return Result.fail(com.chinatower.event.constants.Constants.ERROR_MSG_ORDER_NUM);
                throw new Exception(com.chinatower.event.constants.Constants.ERROR_MSG_ORDER_NUM);
            }
            List<WarningOrderListVO> list = JSONObject.parseArray(JSON.toJSONString(m.get("rows")), WarningOrderListVO.class);
            for (WarningOrderListVO vo : list) {
                req.getWarningOrderIds().add(vo.getOrderId());
            }
            req.setOrderIdDownLoadType(com.chinatower.event.constants.Constants.THREE);
        }
        eventExportService.downloadImgsTower(response, request, req);
    }

    /**
     * 告警图片下载
     *
     * @param req
     * @return
     */
    @PostMapping("/downloadImgs")
    public void downloadImgs(HttpServletResponse response, HttpServletRequest request, @RequestBody AlarmDownloadReq req) throws Exception {
        if (StringUtils.isEmpty(req.getName())) {
            // 获取当前的用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            // 铁塔视角
            if (VideoSystemEnums.UserType.TOWER_USER.getIndex().equals(loginUser.getUser().getUserType())) {
                req.setName("中国铁塔");
            } else {
                // 客户视角判断是否为铁塔切租户
                boolean switchFlag = SecurityUtils.isSwitch();
                //客户视角切换：已切换-sql后面拼接租户ID,未切换-
                if (switchFlag) {
                    //已切换，水印设置为铁塔
                    req.setName("中国铁塔");
                } else {
                    // 未切换，水印为租户名称
                    req.setName(loginUser.getUser().getTenantInfo().getTenantName());
                }
            }

        }
        //按照搜索框条件筛选下载
        List<WarningOrderListVO> searchOrderList = new ArrayList<>();
        if (Constants.ONE.equals(req.getDownLoadType())) {
            TableDataInfo data = iTradeWarningOrderService.getOrderListDownImgs(req);
            searchOrderList = JSONObject.parseArray(JSON.toJSONString(data.getRows()), WarningOrderListVO.class);
            if (!CollectionUtils.isEmpty(searchOrderList)) {
                for (WarningOrderListVO vo : searchOrderList) {
                    req.getWarningOrderIds().add(vo.getOrderId());
                }
            } else {
                //return Result.success("");
                throw new Exception(Constants.ERROR_MSG_ORDER_MSG);
            }
            if (data.getTotal() > 50) {
                //return Result.fail(Constants.ERROR_MSG_ORDER_NUM);
                throw new Exception(Constants.ERROR_MSG_ORDER_NUM);
            }
        } else if (Constants.TWO.equals(req.getDownLoadType())) {
            TableDataInfo data = iTradeWarningOrderService.getInterceptList(req);
            if (data.getTotal() > 50) {
                //return Result.fail(Constants.ERROR_MSG_ORDER_NUM);
                throw new Exception(Constants.ERROR_MSG_ORDER_NUM);
            }
            List<InterceptVO> list = JSONObject.parseArray(JSON.toJSONString(data.getRows()), InterceptVO.class);
            for (InterceptVO vo : list) {
                WarningOrderListVO orderListVO = new WarningOrderListVO();
                BeanUtil.copyProperties(vo, orderListVO);
                orderListVO.setOrderId(vo.getWarningOrderId());
                searchOrderList.add(orderListVO);
                req.getWarningOrderIds().add(vo.getOrderId());
            }
        } else if (Constants.THREE.equals(req.getDownLoadType())) {
            AlarmOrderDTO dto = new AlarmOrderDTO();
            BeanUtil.copyProperties(req.getDownLoadOrderDTO(), dto);
            dto.setPageSize(50);
            Result result = iTradeWarningOrderService.qryWarningOrderCollectionImgs(req);
            Map<String, Object> m = (Map<String, Object>) result.getData();
            if (m.get(Constants.PARAM_TOTAL) != null && Integer.valueOf(String.valueOf(m.get(Constants.PARAM_TOTAL))) > 50) {
                //return Result.fail(Constants.ERROR_MSG_ORDER_NUM);
                throw new Exception(Constants.ERROR_MSG_ORDER_NUM);
            }
            List<WarningOrderListVO> list = JSONObject.parseArray(JSON.toJSONString(m.get("rows")), WarningOrderListVO.class);
            for (WarningOrderListVO vo : list) {
                req.getWarningOrderIds().add(vo.getOrderId());
            }
            req.setOrderIdDownLoadType(Constants.THREE);
        }
        req.setSearchOrderList(searchOrderList);
        eventExportService.downloadImgs(response, request, req);
    }


    /**
     * 告警图片下载
     *
     * @param req
     * @return
     */
    @PostMapping("/downloadImgsStream")
    public ResponseEntity<byte[]> downloadImgsStream(HttpServletRequest request, @RequestBody AlarmDownloadReq req) throws Exception {
        if (StringUtils.isEmpty(req.getName())) {
            // 获取当前的用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (StringUtils.isEmpty(loginUser.getUser().getTenantInfo().getTenantName())) {
                req.setName("中国铁塔");
            } else {
                req.setName(loginUser.getUser().getTenantInfo().getTenantName());
            }

        }
        //按照搜索框条件筛选下载
        List<WarningOrderListVO> searchOrderList = new ArrayList<>();
        if (Constants.ONE.equals(req.getDownLoadType())) {
            TableDataInfo data = iTradeWarningOrderService.getOrderListDownImgs(req);
            searchOrderList = JSONObject.parseArray(JSON.toJSONString(data.getRows()), WarningOrderListVO.class);
            if (!CollectionUtils.isEmpty(searchOrderList)) {
                for (WarningOrderListVO vo : searchOrderList) {
                    req.getWarningOrderIds().add(vo.getOrderId());
                }
            } else {
                return null;
            }
            if (data.getTotal() > 50) {
                return null;
            }
        } else if (Constants.TWO.equals(req.getDownLoadType())) {
            TableDataInfo data = iTradeWarningOrderService.getInterceptList(req);
            if (data.getTotal() > 50) {
                return null;
            }
            List<InterceptVO> list = JSONObject.parseArray(JSON.toJSONString(data.getRows()), InterceptVO.class);
            for (InterceptVO vo : list) {
                WarningOrderListVO orderListVO = new WarningOrderListVO();
                BeanUtil.copyProperties(vo, orderListVO);
                orderListVO.setOrderId(vo.getWarningOrderId());
                searchOrderList.add(orderListVO);
                req.getWarningOrderIds().add(vo.getOrderId());
            }
        } else if (Constants.THREE.equals(req.getDownLoadType())) {
            AlarmOrderDTO dto = new AlarmOrderDTO();
            BeanUtil.copyProperties(req.getDownLoadOrderDTO(), dto);
            dto.setPageSize(50);
            Result result = iTradeWarningOrderService.qryWarningOrderCollectionImgs(req);
            Map<String, Object> m = (Map<String, Object>) result.getData();
            if (m.get(Constants.PARAM_TOTAL) != null && Integer.valueOf(String.valueOf(m.get(Constants.PARAM_TOTAL))) > 50) {
                return null;
            }
            List<WarningOrderListVO> list = JSONObject.parseArray(JSON.toJSONString(m.get("rows")), WarningOrderListVO.class);
            for (WarningOrderListVO vo : list) {
                req.getWarningOrderIds().add(vo.getOrderId());
            }
            req.setOrderIdDownLoadType(Constants.THREE);
        }
        req.setSearchOrderList(searchOrderList);
        return eventExportService.downloadImgsStream(request, req);
    }


    /**
     * 告警图片下载
     *
     * @param req
     * @return
     */
    @PostMapping("/downloadImgsResponse")
    public HttpServletResponse downloadImgsResponse(HttpServletResponse response, HttpServletRequest request, @RequestBody AlarmDownloadReq req) throws Exception {
        if (StringUtils.isEmpty(req.getName())) {
            // 获取当前的用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (StringUtils.isEmpty(loginUser.getUser().getTenantInfo().getTenantName())) {
                req.setName("中国铁塔");
            } else {
                req.setName(loginUser.getUser().getTenantInfo().getTenantName());
            }
        }
        //按照搜索框条件筛选下载
        List<WarningOrderListVO> searchOrderList = new ArrayList<>();
        if (Constants.ONE.equals(req.getDownLoadType())) {
            TableDataInfo data = iTradeWarningOrderService.getOrderListDownImgs(req);
            searchOrderList = JSONObject.parseArray(JSON.toJSONString(data.getRows()), WarningOrderListVO.class);
            if (!CollectionUtils.isEmpty(searchOrderList)) {
                for (WarningOrderListVO vo : searchOrderList) {
                    req.getWarningOrderIds().add(vo.getOrderId());
                }
            } else {
                return null;
            }
            if (data.getTotal() > 50) {
                return null;
            }
        } else if (Constants.TWO.equals(req.getDownLoadType())) {
            TableDataInfo data = iTradeWarningOrderService.getInterceptList(req);
            if (data.getTotal() > 50) {
                return null;
            }
            List<InterceptVO> list = JSONObject.parseArray(JSON.toJSONString(data.getRows()), InterceptVO.class);
            for (InterceptVO vo : list) {
                WarningOrderListVO orderListVO = new WarningOrderListVO();
                BeanUtil.copyProperties(vo, orderListVO);
                orderListVO.setOrderId(vo.getWarningOrderId());
                searchOrderList.add(orderListVO);
                req.getWarningOrderIds().add(vo.getOrderId());
            }
        } else if (Constants.THREE.equals(req.getDownLoadType())) {
            AlarmOrderDTO dto = new AlarmOrderDTO();
            BeanUtil.copyProperties(req.getDownLoadOrderDTO(), dto);
            dto.setPageSize(50);
            Result result = iTradeWarningOrderService.qryWarningOrderCollectionImgs(req);
            Map<String, Object> m = (Map<String, Object>) result.getData();
            if (m.get(Constants.PARAM_TOTAL) != null && Integer.valueOf(String.valueOf(m.get(Constants.PARAM_TOTAL))) > 50) {
                return null;
            }
            List<WarningOrderListVO> list = JSONObject.parseArray(JSON.toJSONString(m.get("rows")), WarningOrderListVO.class);
            for (WarningOrderListVO vo : list) {
                req.getWarningOrderIds().add(vo.getOrderId());
            }
            req.setOrderIdDownLoadType(Constants.THREE);
        }
        req.setSearchOrderList(searchOrderList);
        return eventExportService.downloadImgsResponse(response, request, req);
    }


    /**
     *
     * 导出领导批示列表
     * 功能描述 导出领导批示列表
     *
     * @param dto
     * @param response
     * @author zhen.yan
     * @date 2022/6/15 13:57
     * @version:
     */
    @PostMapping("/exportLeaderAuditList")
    public void exportLeaderAuditList(@RequestBody LeaderAuidtListDTO dto, HttpServletResponse response) {
        try {
            List<LeaderExport> exportList = eventExportService.exportLeaderAuditList(dto);
            byte[] data = new ExcelUtil<>(LeaderExport.class).exportBytes(exportList);
            FileUtils.download(response, data, "");
        } catch (Exception e) {
            log.error("导出领导批示列表异常：", e);
        }
    }

    /**
     *
     * 导出回收站列表
     * 功能描述 导出回收站列表
     *
     * @param dto
     * @param response
     * @author zhen.yan
     * @date 2022/6/15 13:43
     * @version:
     */
    @PostMapping("/exportInterceptList")
    public void exportInterceptList(@RequestBody WarningInterceptDTO dto, HttpServletResponse response) {
        try {
            List<AlarmInterceptExport> exportList = eventExportService.exportInterceptList(dto);
            byte[] data = new ExcelUtil<>(AlarmInterceptExport.class).exportBytes(exportList);
            FileUtils.download(response, data, "");
        } catch (Exception e) {
            log.error("导出回收站列表异常：", e);
        }
    }


    /**
     * 导出告警详情环节信息及附件
     * 功能描述 导出告警详情环节信息及附件
     *
     * @param
     * @param
     * @author cuisj
     * @date 2024/05/29 13:43
     * @version:
     */
    @PostMapping("/exportEventInfo")
    public void exportEventInfo(String warningOrderId, Integer exportSource, Integer exportType, Integer exportView,
                                String devicePitch, String deviceHorizontal, String horiAzimuthAngle,
                                MultipartFile file, HttpServletResponse response) {
        try {
            ExportOrderInfoReq dto = new ExportOrderInfoReq();
            dto.setWarningOrderId(warningOrderId);
            dto.setExportSource(exportSource);
            dto.setExportType(exportType);
            dto.setDeviceHorizontal(deviceHorizontal);
            dto.setDevicePitch(devicePitch);
            dto.setHoriAzimuthAngle(horiAzimuthAngle);
            dto.setExportView(exportView);
            eventExportService.exportEventInfo(dto, file, response);
        } catch (Exception e) {
            log.error("导出告警详情环节信息及附件：", e);
        }
    }


    /**
     * 功能描述 下载卫星告警列表图片
     *
     * @param response
     * @author zhen.yan
     * @date 2022/6/15 13:43
     * @version:
     */
    @PostMapping("/downImgSatellite")
    public void downImgSatellite(@RequestBody SatelliteAlarmApiReq req, HttpServletRequest request, HttpServletResponse response) {
        try {
            log.info("downImgSatellite-卫星下载图片入参:{}", JSON.toJSONString(req));
            req.setExcelFlag(Constants.ONE);
            if (!CollectionUtils.isEmpty(req.getAlarmCodeList())) {
                req.setAlarmCode("");
            }
            Result result = iTowerEventWarningService.getSatelliteOrderList(req);
            if (result != null && result.getData() != null) {
                List<ReceiveSatelliteAlarmDto> list = JSON.parseObject(JSON.toJSONString(result.getData()), SatelliteAlarmResp.class).getList();
                eventExportService.downloadImgsSatellite(response, request, list);
            }
        } catch (Exception e) {
            log.error("下载卫星告警列表图片：", e);
        }
    }

    /**
     * 文件上传
     *
     * @param file
     * @param response
     * @throws IOException
     * @throws InvalidFormatException
     * @throws InvalidFormatException
     */
    @PostMapping("/upload")
    public void uploadFile(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws IOException, InvalidFormatException, InvalidFormatException {
        // 创建新的Word文档
        XWPFDocument document = new XWPFDocument();

        // 创建段落并添加到文档中
        XWPFParagraph paragraph = document.createParagraph();

        // 创建运行并将其添加到段落中
        XWPFRun run = paragraph.createRun();
        byte[] bytes = file.getBytes();
        // 将Blob文件转换为InputStream
        InputStream inputStream = new ByteArrayInputStream(bytes);
        // 添加图片到文档中
        run.addPicture(inputStream, Document.PICTURE_TYPE_PNG, "picture.PNG", Units.toEMU(400), Units.toEMU(400));
        // 保存Word文档
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.write(outputStream);

        // 将生成的Word文档写入HTTP响应中
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setHeader("Content-Disposition", "attachment; filename=output.docx");
        response.getOutputStream().write(outputStream.toByteArray());

        response.flushBuffer();
        outputStream.close();
        document.close();
    }
}
