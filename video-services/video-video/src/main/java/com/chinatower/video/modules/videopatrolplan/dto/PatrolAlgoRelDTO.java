package com.chinatower.video.modules.videopatrolplan.dto;

import com.chinatower.video.api.entity.QueryPatrolData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatrolAlgoRelDTO implements Serializable {

    private List<QueryPatrolData> list;

    private String algorithmCode;

    private String tenantId;

    private String industryCode;
}
