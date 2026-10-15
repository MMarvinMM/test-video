package com.chinatower.bdm.modules.layer.service.impl;

import com.chinatower.bdm.modules.layer.domain.LayerMode;
import com.chinatower.bdm.modules.layer.mapper.LayerModeMapper;
import com.chinatower.bdm.modules.layer.service.ILayerModeService;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 图层类型模式管理 服务实现类
 * </p>
 *
 * @author ch
 * @since 2023-04-17
 */
@Service
@RequiredArgsConstructor
public class LayerModeServiceImpl  implements ILayerModeService {

    private final LayerModeMapper layerModeMapper;
    //记忆模式
    public static final String LAYER_MODE = "_layer_mode";

    public String getLayerModeId(){
        return SecurityUtils.getLoginUser().getUser().getUserId() + LAYER_MODE;
    }

    @Override
    public Result<LayerMode> info() {
        String mode = layerModeMapper.getLayerMode(getLayerModeId());
        LayerMode layerMode = new LayerMode();
        if (StringUtils.isNotEmpty(mode)){
            layerMode.setModeValue(Integer.valueOf(mode));
        }else {
            layerMode.setModeValue(0);
            layerModeMapper.insertLayerMode(getLayerModeId(),"0");
        }
        return Result.data(layerMode);
    }

    @Override
    public Result<String> addOrModify(Integer modeValue) {
        if (modeValue > 2 || modeValue < 0){
            return Result.fail("数据错误");
        }

        int count = layerModeMapper.selectLayerMode(getLayerModeId());
        if (count > 0){
            layerModeMapper.updateLayerMode(getLayerModeId(),String.valueOf(modeValue));
        }else {
            layerModeMapper.insertLayerMode(getLayerModeId(),String.valueOf(modeValue));
        }
        return Result.success("修改成功");
    }
}
