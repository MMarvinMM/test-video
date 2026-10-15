package com.chinatower.platform.consumer.service;

import com.alibaba.fastjson.JSON;
import com.chinatower.platform.consumer.dao.SysDeptDAO;
import com.chinatower.platform.consumer.item.CanalBean;
import com.chinatower.platform.consumer.item.Constants;
import com.chinatower.platform.consumer.item.SysDeptDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 部门表数据监听
 */
@Slf4j
@Service
public class SysDeptService {

    @Autowired
    SysDeptDAO sysDeptDAO;

    public void dealMsg(String message){
        CanalBean canalBeans = JSON.parseObject(message, CanalBean.class);
        List<SysDeptDTO> insertList = new ArrayList<>();
        List<SysDeptDTO> updateList = new ArrayList<>();

        for(Object obj: canalBeans.getData()){
            if(obj == null){
                continue;
            }
            SysDeptDTO sysDeptDTO = JSON.parseObject(JSON.toJSONString(obj), SysDeptDTO.class);
            if (Constants.DELETE.equals(canalBeans.getType())) {
                //逻辑删除
                sysDeptDTO.setDelFlag("2");
                updateList.add(sysDeptDTO);
            }else if(Constants.INSERT.equals(canalBeans.getType()) || Constants.UPDATE.equals(canalBeans.getType())){
                //插入操作
                if(sysDeptDTO.getId() == null || sysDeptDAO.countById(sysDeptDTO.getId()) <= 0 ){
                    insertList.add(sysDeptDTO);
                }else{
                    updateList.add(sysDeptDTO);
                }
            }else{
                log.warn("仅支持表的dml语句同步");
            }
        }
        // 批量操作
        if(!CollectionUtils.isEmpty(updateList)){
            sysDeptDAO.batchUpdate(updateList);
        }
        if (!CollectionUtils.isEmpty(insertList)) {
            sysDeptDAO.batchInsert(insertList);
        }
    }
}
