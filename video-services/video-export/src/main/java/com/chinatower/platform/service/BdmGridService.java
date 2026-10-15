package com.chinatower.platform.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.bdm.api.entity.*;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.mapper.BdmGridExportMapper;
import com.chinatower.platform.mapper.BdmGridKeeperExportMapper;
import com.chinatower.platform.utils.JNConstants;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class BdmGridService {
    @Resource
    private BdmGridExportMapper bdmGridExportMapper;
    @Resource
    private BdmGridKeeperExportMapper bdmGridKeeperExportMapper;



    /**
     * 功能描述：导出
     * @Author：zl
     * @param vo
     * @return List<GridExportVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格管理
     */
    @DataScope(tenantColumn = "a.tenant_id",industryCode = "a.industry_code")
    public List<GridExportVO> getGridExportList(GridParam vo){
        List<GridExportVO> list = bdmGridExportMapper.getGridExportList(vo);
        if(!CollectionUtils.isEmpty(list)){
            List<GridListVO> keepers = bdmGridKeeperExportMapper.getKeeperLeader(getGridIds(JSONArray.parseArray(JSONArray.toJSONString(list))),null,SecurityUtils.getLoginUser().getUser().getTenantId(),SecurityUtils.getLoginUser().getUser().getIndustryCode());
            if(!CollectionUtils.isEmpty(keepers)){
                for(GridExportVO exportVO : list){
                    keepers.forEach(item -> {
                        if(item.getGridId().equals(exportVO.getGridCode())){
                            exportVO.setKeeperName(item.getKeeperName());
                        }
                    });
                }
            }
        }
        return list;
    }


    /**
     * 功能描述：转换数据类型
     * @Author：zl
     * @param json
     * @return List<String>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格管理
     */
    private List<String> getGridIds(JSONArray json){
        List<String> list = new ArrayList<>();
        for(Object object : json){
            JSONObject obj = (JSONObject) object;
            String gridId = obj.getString(JNConstants.GRID_ID);
            list.add(gridId);
        }
        return list;
    }


}
