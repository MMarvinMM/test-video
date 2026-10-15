package com.chinatower.platform.controller;

import com.chinatower.bdm.api.entity.GridKeeperForExportViewVO;
import com.chinatower.bdm.api.entity.GridKeeperParam;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.common.utils.poi.ExcelUtil;
import com.chinatower.platform.dto.KeeperIdGridNameVo;
import com.chinatower.platform.service.BdmGridKeeperService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * video-export
 * @Author：zl
 * @Description：网格员管理接口
 * @Date： 2022/8/9
 * @version release_v1.5
 * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
 */
@Api(value = "网格员相关接口-Api",tags = "网格员菜单后端接口")
@Slf4j
@RestController
@RequestMapping("/gridKeeper")
public class BdmGridKeeperController extends TowerBaseController {
    @Resource
    BdmGridKeeperService bdmGridKeeperService;

    /**
     *
     * 网格员导出
     * 功能描述：网格员导出
     * @Author：zl
     * @param vo
     * @param response
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员相关接口
     */
    @ApiOperation(value = "网格员导出")
    @PostMapping("/export")
    public void export(@RequestBody GridKeeperParam vo, HttpServletResponse response) {
        List<GridKeeperForExportViewVO> list = bdmGridKeeperService.getGridKeeperExportList(vo);
        if (list.isEmpty()){
            list = new ArrayList<>();
        }
        List<String> KeeperIds = list.stream().map(gridKeeperForExportViewVO -> gridKeeperForExportViewVO.getKeeperId()).collect(Collectors.toList());
        List<KeeperIdGridNameVo> IdAndNameList = new ArrayList<>();
        if (!KeeperIds.isEmpty()){
            IdAndNameList = bdmGridKeeperService.findKeeperIdByGridName(KeeperIds);
        }
        Map<String,List<String>> map = new HashMap<>();
        if (IdAndNameList.isEmpty()){
            IdAndNameList = new ArrayList<>();
        }

        for (KeeperIdGridNameVo vo1 : IdAndNameList) {
            List<String> gridNames = map.get(vo1.getKeeperId());
            if (ObjectUtils.isEmpty(gridNames)){
                gridNames = new ArrayList<>();
                map.put(vo1.getKeeperId(),gridNames);
            }
            gridNames.add(vo1.getGridName());
        }

        for (GridKeeperForExportViewVO keeper : list) {
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                if (keeper.getKeeperId().equals(entry.getKey())){
                    keeper.setSuperiorGrid(String.join(",",entry.getValue()));
                }
            }
        }

        byte[] data =new ExcelUtil<>(GridKeeperForExportViewVO.class).exportBytes(list);
        FileUtils.download(response, data);
    }




}
