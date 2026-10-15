package com.chinatower.platform.service;

import com.chinatower.event.api.model.LocalImgReq;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.entity.vo.WarningOrderListVO;
import com.chinatower.event.entity.vo.AlarmInterceptExport;
import com.chinatower.event.entity.vo.LeaderExport;
import com.chinatower.event.entity.vo.WarningOrderExport;
import com.chinatower.platform.dto.AlarmOrderDTO;
import com.chinatower.platform.dto.ReceiveSatelliteAlarmDto;
import com.chinatower.system.api.resp.SysDictDataResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 功能说明: event服务导出接口类
 *
 * @author lyy
 * @date 2021-12-09 15:39
 * @requestID
 */
public interface EventExportService {

    /**
     * 获取告警事件导出数据
     * @param dto
     * @return
     */
    List<WarningOrderExport> exportWarningOrderList(AlarmOrderDTO dto);

    /**
     * 我的收藏-导出列表
     * @param dto
     * @return
     */
    List<WarningOrderExport> exportWarningOrderCollectionList(AlarmOrderDTO dto);

    /**
     * 导出领导批示列表
     * @param dto
     * @return
     */
    List<LeaderExport> exportLeaderAuditList(LeaderAuidtListDTO dto);

    /**
     * 导出回收站列表
     * @param dto
     * @return
     */
    List<AlarmInterceptExport> exportInterceptList(WarningInterceptDTO dto);

    /**
     * 导出告警详情环节信息及附件
     * @param dto
     * @return
     */
    void exportEventInfo(ExportOrderInfoReq dto,MultipartFile file,HttpServletResponse response) throws IOException;

    /**
     * 获取告警类型名称列表
     * @return
     */
    List<Map> getAlarmTypeList();

    /**
     * 根据枚举类型获取枚举列表
     * @param enumTypeId
     * @return
     */
    List<SysDictDataResponse> getEnumList(int enumTypeId);

    /**
     * 判断是否有编辑权限 1:可编辑   0：不可编辑
     * @param isAdmin
     * @param userId
     * @param vo
     * @param flowLinkIds
     * @return
     */
    int ifEditHandle(boolean isAdmin, String userId, WarningOrderListVO vo,String flowLinkIds);

    /**
     * 查询结果转化
     * @param resultList
     * @param alarmOrderListReq
     * @param userId
     * @param flowLinkIds
     */
    void resultHandle(List<WarningOrderListVO> resultList, AlarmOrderDTO alarmOrderListReq, String userId, String flowLinkIds);

    /**
     * 告警图片下载
     * @param response
     * @param request
     * @param req
     */
    void downloadImgs(HttpServletResponse response, HttpServletRequest request, AlarmDownloadReq req) throws Exception;

    void downloadImgsTower(HttpServletResponse response, HttpServletRequest request, AlarmDownloadReq req) throws Exception;


    ResponseEntity<byte[]> downloadImgsStream( HttpServletRequest request, AlarmDownloadReq req) throws Exception;

    HttpServletResponse downloadImgsResponse(HttpServletResponse response, HttpServletRequest request, AlarmDownloadReq req) throws Exception;

    /**
     * 告警视频下载
     * @param response
     * @param request
     * @param req
     */
    void downloadVideosByFfmpeg(HttpServletResponse response, HttpServletRequest request, DownloadVideoReq req) throws Exception;

    /**
     * 属地化告警图片下载
     * @param response
     * @param request
     * @param list
     * @return
     * @throws Exception
     */
    void downloadImgsLocal(HttpServletResponse response, HttpServletRequest request, List<LocalImgReq> list) throws Exception;


    void downloadLocalVideosByFfmpeg(HttpServletResponse response, HttpServletRequest request, LocalDownloadVideoReq req) throws Exception;

    void downloadImgsAndVideos(HttpServletResponse response, HttpServletRequest request, DownloadImgsAndVideoReq req);

    /**
     *   下载告警工单的单个图片或者视频
     *
     * @param response 响应参数
     * @param request 请求参数
     * @param req 传入参数
     */
    void downloadSingleImgOrVideo(HttpServletResponse response, HttpServletRequest request, DownloadSingleImgOrVideoReq req);


    void downloadVideoByFlush(HttpServletResponse response, HttpServletRequest request, InputStream videoStream,String fileName,String isH265);

    void downloadPhotoByFlush(HttpServletResponse response, HttpServletRequest request, InputStream videoStream, String fileName);

    /**
     * 视频URL下载 -> 加水印 -> 上传文件中心 -> 返回上传后的URL
     */
    String downloadVideoByUrlAndUpload(HttpServletResponse response, HttpServletRequest request, String url, String isH265);
    /**
     * 卫星告警图片下载
     * @param response
     * @param request
     * @param list
     */
    void downloadImgsSatellite(HttpServletResponse response, HttpServletRequest request, List<ReceiveSatelliteAlarmDto> list) throws Exception;


}
