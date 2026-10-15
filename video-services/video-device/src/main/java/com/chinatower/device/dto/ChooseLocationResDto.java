package com.chinatower.device.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChooseLocationResDto implements Serializable {

    private List<ChooseLocationThreeDto> successList;

    private List<Integer> errList;
}
