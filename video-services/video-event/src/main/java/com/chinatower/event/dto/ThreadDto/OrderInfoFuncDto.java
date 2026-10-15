package com.chinatower.event.dto.ThreadDto;

import com.chinatower.event.entity.vo.OrderInfoVO;
import lombok.Data;

@Data
public class OrderInfoFuncDto {

    private OrderInfoVO orderInfoVO;
    private String fileImgUrlSrc;
    private String fileImgUrlIcon;
    private String videoUrl;
}
