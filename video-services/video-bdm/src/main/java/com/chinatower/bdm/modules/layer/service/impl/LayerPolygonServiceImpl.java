package com.chinatower.bdm.modules.layer.service.impl;

import com.chinatower.bdm.modules.layer.domain.Layer;
import com.chinatower.bdm.modules.layer.domain.LayerPolygon;
import com.chinatower.bdm.modules.layer.mapper.LayerMapper;
import com.chinatower.bdm.modules.layer.mapper.LayerPolygonMapper;
import com.chinatower.bdm.modules.layer.service.ILayerPolygonService;
import com.chinatower.common.core.dto.bdm.LayerPolygonAddDTO;
import com.chinatower.common.core.dto.bdm.LayerPolygonDTO;
import com.chinatower.common.core.dto.bdm.LayerPolygonQueryDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.BeanCopyUtils;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 图层图斑管理 服务实现类
 * </p>
 *
 * @author ch
 * @since 2023-04-10
 */
@Service
@RequiredArgsConstructor
public class LayerPolygonServiceImpl implements ILayerPolygonService {

    private final LayerPolygonMapper layerPolygonMapper;
    private final LayerMapper layerMapper;

    @Override
    public List<LayerPolygon> list(LayerPolygonQueryDTO dto) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (VideoSystemEnums.UserType.TENANT_USER.getIndex().equals(loginUser.getUser().getUserType())) {
            dto.setTenantId(loginUser.getUser().getTenantId());
            dto.setIndustryCode(loginUser.getUser().getIndustryCode());
        }
        LayerPolygon polygon = new LayerPolygon();
        polygon.setTenantId(dto.getTenantId());
        polygon.setIndustryCode(dto.getIndustryCode());
        polygon.setId(dto.getLayerPolygonId());
        polygon.setLayerId(dto.getLayerId());
        polygon.setFeatureId(dto.getFeatureId());
        return layerPolygonMapper.selectList(polygon);

    }

    @Override
    public LayerPolygonDTO info(String layerId, String featureId) {
        LoginUser loginUser = SecurityUtils.getLoginUser();

        LayerPolygon layerPolygon = new LayerPolygon();
        layerPolygon.setTenantId(loginUser.getUser().getTenantId());
        layerPolygon.setIndustryCode(loginUser.getUser().getIndustryCode());
        layerPolygon.setLayerId(layerId);
        layerPolygon.setFeatureId(featureId);
        List<LayerPolygon> list = layerPolygonMapper.selectList(layerPolygon);
        if (!list.isEmpty()) {
            LayerPolygon polygon = list.get(0);
            return BeanCopyUtils.copy(polygon, LayerPolygonDTO.class);
        }
        return null;
    }

    @Override
    public Integer getLayerPolygonCount(String layerId) {
        LoginUser loginUser = SecurityUtils.getLoginUser();

        return layerPolygonMapper.selectCount(loginUser.getUser().getTenantId(), loginUser.getUser().getIndustryCode(), layerId);
    }

    @Override
    public Result<String> addLayerPolygon(LayerPolygonAddDTO dto) {
        Layer layer = layerMapper.selectById(dto.getLayerId());
        if (layer == null) {
            throw new CustomException("参数错误");
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (!layer.getTenantId().equals(loginUser.getUser().getTenantId())
                || !layer.getIndustryCode().equals(loginUser.getUser().getIndustryCode())) {
            throw new CustomException("数据错误");
        }
        LayerPolygon copy = BeanCopyUtils.copy(dto, LayerPolygon.class);
        copy.setCreator(loginUser.getUsername());
        copy.setCreateTime(new Date());
        int i = layerPolygonMapper.insert(copy);
        return i > 0 ? Result.data(copy.getId()) : Result.fail("新增时报");
    }
}
