package com.chinatower.device.service;

import com.chinatower.common.entity.Result;
import com.chinatower.device.dto.AudioDetailResponseDTO;
import com.chinatower.device.dto.TextDetailResponseDTO;
import com.chinatower.device.entity.dto.VmHornAuditionRequestDTO;
import com.chinatower.device.entity.dto.VmHornContentConfDTO;
import com.chinatower.device.entity.dto.VmHornContentConfRequestListDTO;
import com.chinatower.device.entity.dto.VmHornContentRequestDTO;
import com.chinatower.device.entity.vo.HornAuditionResultVO;
import com.chinatower.device.entity.vo.VmHornContentVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author willem
 * @date 2024/10/9
 * @description 广播内容
 */
public interface IVmHornContentService {
    Result<String> add(VmHornContentVO param);

    Result<String> update(VmHornContentVO param);

    Result<String> audit(VmHornContentVO param);

    Result<String> delete(List<String> ids);

    Result<PageInfo<VmHornContentConfDTO>> list(VmHornContentConfRequestListDTO param);

    Result<PageInfo<VmHornContentConfDTO>> getListByContentType(VmHornContentRequestDTO param);

    Result<HornAuditionResultVO> audition(VmHornAuditionRequestDTO param);

    Result<AudioDetailResponseDTO> audioDetail(String audioId);

    Result<TextDetailResponseDTO> textDetail(String txtId);

    Result<VmHornContentConfDTO> detail(VmHornContentConfRequestListDTO hornContentConfDTO);
}
