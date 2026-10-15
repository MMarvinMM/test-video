package com.chinatower.device.dto;

import com.chinatower.platform.entity.device.ChannelVideoPlanVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName RecordPlanCheckDTO
 * @Deacription
 * @Author chenmf
 * @Date 2022/3/13 19:45
 * @Version 1.0
 **/
@Data
public class RecordPlanCheckDTO implements Serializable {
    
    private List<ChannelVideoPlanVO> conflictedlist;
    
    private List<ChannelStorageTimeVO> storageDaylist;
}
