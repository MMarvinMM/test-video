package com.chinatower.video.modules.artags.service.impl;

import com.chinatower.platform.annotation.DataPermissions;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.video.modules.query.dao.VideoPtzQueryDAO;
import com.chinatower.video.modules.query.entity.ArTagFilterVO;
import com.chinatower.video.modules.query.entity.ArTagMoveRes;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class ArTagsScopeServiceImpl {
    @Resource
    VideoPtzQueryDAO videoPtzQueryDAO;
    @DataScope(tenantColumn="dev.tenant_id",industryCode = "dev.industry_code")
    public List<ArTagMoveRes> getArTagByVideo(ArTagFilterVO arTagFilterVO){
        return videoPtzQueryDAO.getArTagByVideo(arTagFilterVO);
    }

    @DataScope(tenantColumn="dev.tenant_id",industryCode = "dev.industry_code")
    public List<ArTagMoveRes> getArTagByMap(ArTagFilterVO arTagFilterVO){
        return videoPtzQueryDAO.getArTagByMap(arTagFilterVO);
    }

    @DataScope(tenantColumn="t1.tenant_id",deviceColumn = "t1.device_code", industryCode = "t1.industry_code",alarmSourceVal = "4",acceptColumn="t1.accept_node")
    public List<ArTagMoveRes> getArTagByDevice(ArTagFilterVO arTagFilterVO){
        return videoPtzQueryDAO.getArTagByDevice(arTagFilterVO);
    }

    @DataScope(tenantColumn="osd.tenant_id",deviceColumn = "osd.device_code", industryCode = "osd.industry_code",acceptColumn = "osd.accept_node", alarmSourceVal = "8,10")
    public List<ArTagMoveRes> getArTagByIot(ArTagFilterVO arTagFilterVO){
        return videoPtzQueryDAO.getArTagByIot(arTagFilterVO);
    }

    @DataScope(tenantColumn="t1.tenant_id",deviceColumn = "t1.device_code", industryCode = "t1.industry_code", acceptColumn = "t1.accept_node", alarmSourceVal = "11")
    public List<ArTagMoveRes> getArTagByUav(ArTagFilterVO arTagFilterVO){
        return videoPtzQueryDAO.getArTagByUav(arTagFilterVO);
    }

    @DataScope(tenantColumn="osd.tenant_id",deviceColumn = "osd.device_code", industryCode = "osd.industry_code", acceptColumn = "osd.accept_node", alarmSourceVal = "8,10")
    public List<ArTagMoveRes> getArTagBySensor(ArTagFilterVO arTagFilterVO){
        return videoPtzQueryDAO.getArTagBySensor(arTagFilterVO);
    }

    @DataScope(tenantColumn="osd.tenant_id",deviceColumn = "osd.device_code", industryCode = "osd.industry_code", acceptColumn = "osd.accept_node", alarmSourceVal = "12")
    public List<ArTagMoveRes> getArTagByHorn(ArTagFilterVO arTagFilterVO){
        return videoPtzQueryDAO.getArTagByHorn(arTagFilterVO);
    }

    @DataPermissions(tenantColumn="l.tenant_id", industryCode = "l.industry_code", layerColumn = "l.id")
    public List<ArTagMoveRes> getArTagByLayer(ArTagFilterVO arTagFilterVO){
        return videoPtzQueryDAO.getArTagByLayer(arTagFilterVO);
    }
}
