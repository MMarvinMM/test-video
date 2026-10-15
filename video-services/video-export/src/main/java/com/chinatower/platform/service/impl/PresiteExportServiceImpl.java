package com.chinatower.platform.service.impl;

import com.chinatower.event.constants.Constants;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.request.Page;
import com.chinatower.mongodb.request.QueryEntity;
import com.chinatower.mongodb.request.SortEntity;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.PresiteVO;
import com.chinatower.platform.entity.VmVideoPresetSnapshotMongo;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.platform.mapper.IDeviceMapper;
import com.chinatower.platform.mongodb.PresetSnapshotMongoDAO;
import com.chinatower.platform.service.PresiteExportService;
import com.chinatower.platform.utils.DownloadUtil;
import com.chinatower.video.api.entity.PresetDTO;
import com.chinatower.video.api.entity.VideoConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @program: video-platform
 * @ClassName: PresiteExportServiceImpl
 * @description: 预置位相关导出业务服务层
 * @author: fengty
 * @create: 2024-07-10 13:41
 */
@Service
@Slf4j
public class PresiteExportServiceImpl implements PresiteExportService {

    @Autowired
    PresetSnapshotMongoDAO presetSnapshotMongoDAO;

    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Resource
    private IDeviceMapper deviceMapper;

    @Override
    public void downloadPresiteImg(HttpServletResponse response, HttpServletRequest request, PresiteVO presiteVO) {
        List<VmVideoPresetSnapshotMongo> list = new ArrayList<>();
        try {
            if(presiteVO.getSingleFlag() != 1) {
                Integer pageNum = presiteVO.getPageNum();
                Integer pageSize = presiteVO.getPageSize();
                if(pageNum == null){
                    pageNum = 1;
                }
                if(pageSize == null){
                    pageSize = 10;
                }

                QueryEntity query = new QueryEntity(VmVideoPresetSnapshotMongo.class);

                //下载来源  0:单屏模式预置位抓图，1:视频抓图
                String downloadType = presiteVO.getDownloadType();
                if(Constants.ONE.equals(downloadType)){
                    query.in("id", presiteVO.getIdList().toArray(new String[0]));
                    //由于mysql的时候pageNo是1开始的，mongodb需要减一
                    presiteVO.setPageNum(0);
                    presiteVO.setPageSize(pageSize);
                }else{
                    query.eq(VideoConstants.PRESET_ID_FIELD, presiteVO.getPresetId());
                    //由于mysql的时候pageNo是1开始的，mongodb需要减一
                    presiteVO.setPageNum(pageNum-1);
                    presiteVO.setPageSize(pageSize);
                }
                query.eq(VideoConstants.DELETE_FLAG_FIELD, DeviceConstants.DELETE_FLAG_NORMAL_INT);
                query.between(VideoConstants.SNAPSHOT_TIME_FIELD, presiteVO.getStartTime(), presiteVO.getEndTime());

                //获取分页参数
                Page page = new Page(presiteVO.getPageNum(), presiteVO.getPageSize());
                // 排序
                SortEntity sortEntity = new SortEntity();
                sortEntity.desc(VideoConstants.SNAPSHOT_TIME_FIELD);
                page.setSortEntity(sortEntity);
                Page<VmVideoPresetSnapshotMongo> pageInfo = presetSnapshotMongoDAO.findPage(query, page, null, MongoDBType.PRIMARY);
                list = pageInfo.getList();
            } else {
                QueryEntity query = new QueryEntity(VmVideoPresetSnapshotMongo.class);
                query.eq("id", presiteVO.getId());
                VmVideoPresetSnapshotMongo preset = presetSnapshotMongoDAO.get(query, MongoDBType.PRIMARY);
                list.add(preset);
            }
        } catch (Exception e) {
            log.error("预置位抓图查询mongoDB异常：{}", e);
        }
        if( ! CollectionUtils.isEmpty(list) ) {
            try {
                //重构list
                this.buildResult(list);
                DownloadUtil.downLoadPresite(list,presiteVO,response);
            } catch (IOException e) {
                log.error("预置位抓图，下载压缩包报错：{}", e);
                throw new RuntimeException(e);
            }
        }
    }

    private void buildResult(List<VmVideoPresetSnapshotMongo> list){
        if(!CollectionUtils.isEmpty(list)){
            // 批量obs加密
            Set<String> fileUrlList = list.stream().map(VmVideoPresetSnapshotMongo::getImgUrl).collect(Collectors.toSet());
            Map<String,String> fileUrlMap = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList<>(fileUrlList));
            for( VmVideoPresetSnapshotMongo temp : list )
            {
                temp.setImgUrl(fileUrlMap.get(temp.getImgUrl()));
            }

            // 批量查询通道名称
            List<DeviceInfoVO> deviceInfoVOS = deviceMapper.queryChannelInfoBatch(list);
            for( VmVideoPresetSnapshotMongo temp : list )
            {
                for (DeviceInfoVO deviceInfoVO : deviceInfoVOS) {
                    if(temp.getChannelCode().equals(deviceInfoVO.getChannelCode())){
                        temp.setChannelName(deviceInfoVO.getChannelName());
                    }
                }
            }

            // 批量查询预置位名称
            List<PresetDTO> presetDTOS = deviceMapper.queryPresetInfoBatch(list);
            for( VmVideoPresetSnapshotMongo temp : list )
            {
                for (PresetDTO presetDTO : presetDTOS) {
                    if(temp.getDeviceCode().equals(presetDTO.getDeviceCode()) && temp.getPresetId().equals(presetDTO.getPresetId())){
                        temp.setPresetName(presetDTO.getPresetName());
                    }
                }
            }
        }
    }
}
