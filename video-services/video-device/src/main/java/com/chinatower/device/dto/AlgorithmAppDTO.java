package com.chinatower.device.dto;

import com.chinatower.platform.entity.device.AlgorithmAreaVO;
import com.chinatower.platform.entity.order.OrderAlgorithmsVO;
import lombok.Data;

import java.util.List;

@Data
public class AlgorithmAppDTO {
    List<AlgorithmAreaVO> algorithmAreaVOS;
    List<OrderAlgorithmsVO> orderAlgorithmsVOS;
}
