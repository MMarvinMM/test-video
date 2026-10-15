package com.chinatower.device.repository.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.device.constants.DeviceSyncConstants;
import com.chinatower.device.dataobject.AlgorithmConfigDO;
import com.chinatower.device.mapper.AlgorithmConfigMapper;
import com.chinatower.device.repository.IAlgorithmConfigRepository;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.AlgorithmConfigDTO;
import com.chinatower.platform.entity.device.AlgorithmConfigVO;
import com.chinatower.platform.entity.device.AlgorithmListVO;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.page.PageMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 算法配置 服务实现类
 * @auther: zhaozj
 * @date: 2022/12/07
 * @version: release_v1.5.50
 * @requestID: 配置管理_算法配置
 */
@Slf4j
@Service
public class AlgorithmConfigRepositoryImpl extends ServiceImpl<AlgorithmConfigMapper, AlgorithmConfigDO>
        implements IAlgorithmConfigRepository {

    /**
     * 功能描述: 算法配置--列表分页查询算法配置
     * @auther: zhaozj
     * @date: 2022/12/07
     * @version: release_v1.5.50
     * @requestID: 配置管理_算法配置
     */
    @DataScope(tenantColumn = "dataTable.tenant_id", acceptColumn="dataTable.accept_node", deviceColumn = "dataTable.device_code",alarmSourceVal = "4")
    @Override
    public List<AlgorithmListVO> getAlgorithmConfigList(AlgorithmConfigDTO algorithmConfigDTO) {
        log.info(DeviceSyncConstants.ALGO_CONFIG_LIST_PARAM + JSON.toJSONString(algorithmConfigDTO));
        SysUser user = SecurityUtils.getLoginUser().getUser();
        algorithmConfigDTO.setIndustryCode(user.getIndustryCode());
        algorithmConfigDTO.setTenantId(user.getTenantId());
        if (null != algorithmConfigDTO.getPageNum() && null != algorithmConfigDTO.getPageSize()) {
            PageMethod.startPage(algorithmConfigDTO.getPageNum(), algorithmConfigDTO.getPageSize());
        }
        List<AlgorithmListVO> list = baseMapper.getAlgorithmConfigList(algorithmConfigDTO);

        List<String> channelCodeList = new ArrayList<>();
        for (AlgorithmListVO vo : list) {
            channelCodeList.add(vo.getChannelCode());
        }
        algorithmConfigDTO.setChannelCodeList(channelCodeList);

        //增加返回已订购算法数量、已配置算法数量、待配置算法数量
        List<AlgorithmListVO> orderAlgoCount = baseMapper.getOrderAlgorithmTotal(algorithmConfigDTO);
        List<AlgorithmListVO> algoConfigCount = baseMapper.getAlgorithmConfigCount(algorithmConfigDTO);

        //将相同设备相同通道的已订购算法数量与已配置算法数量进行合并
        orderAlgoCount.stream().map(total -> {
            algoConfigCount.stream().filter(count-> Objects.equals(total.getDeviceCode(),count.getDeviceCode()) &&
                    Objects.equals(total.getChannelCode(),count.getChannelCode())).forEach(count-> {
                total.setAlgoConfigCount(count.getAlgoConfigCount());
            });
            return total;
        }).collect(Collectors.toList());

        //算法列表list中在相同设备相同通道信息中增加已订购算法数量与已配置算法数量字段
        list.stream().map(total -> {
            orderAlgoCount.stream().filter(count-> Objects.equals(total.getDeviceCode(),count.getDeviceCode()) &&
                    Objects.equals(total.getChannelCode(),count.getChannelCode())).forEach(count-> {
                total.setOrderAlgoCount(count.getOrderAlgoCount());
                total.setAlgoConfigCount(count.getAlgoConfigCount());
                Integer cou = total.getOrderAlgoCount() - total.getAlgoConfigCount();
                if (cou < 0) {
                    cou = 0;
                }
                total.setToBeConfigCount(cou);
            });
            return total;
        }).collect(Collectors.toList());

        list.forEach(algorithmListVO -> algorithmListVO.setTenantId(algorithmConfigDTO.getTenantId()));
        return list;
    }

    @Override
    public List<AlgorithmListVO> getAlgorithmConfigList2(AlgorithmConfigDTO algorithmConfigDTO) {
        log.info(DeviceSyncConstants.ALGO_CONFIG_LIST_PARAM + JSON.toJSONString(algorithmConfigDTO));
        algorithmConfigDTO.setIndustryCode(SecurityUtils.getLoginUser().getUser().getIndustryCode());
        if (null != algorithmConfigDTO.getPageNum() && null != algorithmConfigDTO.getPageSize()) {
            PageMethod.startPage(algorithmConfigDTO.getPageNum(), algorithmConfigDTO.getPageSize());
        }
        List<AlgorithmListVO> list = baseMapper.getAlgorithmConfigList2(algorithmConfigDTO);
        list.forEach(algorithmListVO -> algorithmListVO.setTenantId(algorithmConfigDTO.getTenantId()));
        return list;
    }

    /**
     * 功能描述 查询算法配置
     *
     * @param algorithmConfigDTO
     * @return
     * @author: chenshaowu
     * @date: 2022/2/11
     */
    @Override
    public List<AlgorithmConfigVO> queryalgorithmConfig(AlgorithmConfigDTO algorithmConfigDTO) {
        return baseMapper.queryalgorithmConfig(algorithmConfigDTO);
    }

}
