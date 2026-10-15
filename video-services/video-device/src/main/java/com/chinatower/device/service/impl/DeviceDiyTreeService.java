package com.chinatower.device.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.IdUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.device.constants.DeviceSyncConstants;
import com.chinatower.device.dto.DeviceDiyTreeRequestVO;
import com.chinatower.device.entity.vo.DeviceDiyTreeNodeVO;
import com.chinatower.device.entity.vo.DeviceDiyTreeResultVO;
import com.chinatower.device.entity.vo.DeviceDiyTreeVO;
import com.chinatower.device.mapper.AlgorithmGiveConfigMapper;
import com.chinatower.device.mapper.DeviceDiyTreeMapper;
import com.chinatower.device.mapper.DeviceInfoMapper;
import com.chinatower.device.service.IDeviceDiyTreeService;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.DeviceChannelVO;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DeviceDiyTreeService implements IDeviceDiyTreeService {

    @Autowired
    private DeviceDiyTreeMapper baseMapper;

    @Autowired
    private DeviceInfoMapper deviceInfoMapper;

    @Resource
    private AlgorithmGiveConfigMapper algorithmGiveConfigMapper;

    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Override
    @Transactional
    public Result save(DeviceDiyTreeVO deviceInfoVO) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        deviceInfoVO.setTenantId(user.getTenantId());
        deviceInfoVO.setIndustryCode(user.getIndustryCode());
        //校验入参
        this.check(deviceInfoVO);
        deviceInfoVO.setAppVerCode(user.getAppVerCode());
        deviceInfoVO.setCreator(user.getUserName());
        deviceInfoVO.setTreeId(this.getRandomCode(10));
        baseMapper.saveDiy(deviceInfoVO);
        this.saveDiyNode(deviceInfoVO);
        return Result.data("新增成功");
    }

    @Override
    @Transactional
    public Result update(DeviceDiyTreeVO deviceInfoVO) {
        if(StringUtils.isEmpty(deviceInfoVO.getTreeId())){
            return Result.data("设备树编码不能为空");
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        deviceInfoVO.setTenantId(user.getTenantId());
        deviceInfoVO.setIndustryCode(user.getIndustryCode());
        //校验入参
        this.check(deviceInfoVO);

        deviceInfoVO.setAppVerCode(user.getAppVerCode());
        deviceInfoVO.setCreator(user.getUserName());
        deviceInfoVO.setModifier(user.getUserName());
        //更新自定义树主表
        baseMapper.updateDiy(deviceInfoVO);
        //删除自定义树节点数据
        baseMapper.delDiyNode(deviceInfoVO);
        //重新保存自定义树节点数据
        this.saveDiyNode(deviceInfoVO);
        return Result.data("编辑成功");
    }

    public Result updateStatus(DeviceDiyTreeVO deviceInfoVO) {
        if(StringUtils.isEmpty(deviceInfoVO.getTreeId())){
            return Result.data("设备树编码不能为空");
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        deviceInfoVO.setModifier(user.getUserName());
        //更新自定义树主表
        baseMapper.updateDiy(deviceInfoVO);
        return Result.data("操作成功");
    }

    private void saveDiyNode(DeviceDiyTreeVO deviceInfoVO){
        List<String> deviceList = deviceInfoVO.getNodeList().stream().filter(e->!StringUtils.isEmpty(e.getDeviceCode())).map(DeviceDiyTreeNodeVO::getDeviceCode).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(deviceList)){
            List<DeviceChannelVO> devChanInfo = algorithmGiveConfigMapper.getDevChanInfo(deviceList);

            List<DeviceDiyTreeNodeVO> nodeList = deviceInfoVO.getNodeList();
            for (int i = 0; i < nodeList.size(); i++) {
                DeviceDiyTreeNodeVO deviceDiyTreeNodeVO = nodeList.get(i);
                int index = 0;
                for (DeviceChannelVO deviceChannelVO : devChanInfo) {
                    if(Constants.TWO.equals(deviceDiyTreeNodeVO.getNodeType())
                        && deviceDiyTreeNodeVO.getDeviceCode().equals(deviceChannelVO.getDeviceCode())){
                        DeviceDiyTreeNodeVO vo = new DeviceDiyTreeNodeVO();
                        vo.setNodeType(Constants.THREE);
                        vo.setNodeCode(this.getRandomCode(10));
                        vo.setNodeValue(deviceChannelVO.getChannelName());
                        vo.setChannelCode(deviceChannelVO.getChannelCode());
                        vo.setDeviceCode(deviceDiyTreeNodeVO.getDeviceCode());
                        vo.setNodeLevel(deviceDiyTreeNodeVO.getNodeLevel() + Constants.ONE_NUM);
                        vo.setParentNodeCode(deviceDiyTreeNodeVO.getNodeCode());
                        nodeList.add(i + index + 1, vo);
                        index++;
                    }
                }
            }
            deviceInfoVO.setNodeList(nodeList);
        }

        List<DeviceDiyTreeNodeVO> nodeList = deviceInfoVO.getNodeList();
        String treeId = StringUtils.join(deviceInfoVO.getTreeId(), DeviceSyncConstants.HORIZON_LINE);
        //重写后的节点编码map，格式：{oldNodeCode,newNodeCode}
        Map<String,String> map = new HashMap();
        //根据节点级别分组
        Map<Integer, List<DeviceDiyTreeNodeVO>> levelGroupMap = nodeList.stream().collect(Collectors.groupingBy(DeviceDiyTreeNodeVO::getNodeLevel));
        //按照节点层级排列
        Map<Integer, List<DeviceDiyTreeNodeVO>> sortLevelGroupMap = new TreeMap<>(levelGroupMap);

        for (Map.Entry<Integer, List<DeviceDiyTreeNodeVO>> levelEntry : sortLevelGroupMap.entrySet()) {
            //根据节点级别+父节点编码分组
            Map<String, List<DeviceDiyTreeNodeVO>> levelAndPcodeMap = levelEntry.getValue().stream().collect(Collectors.groupingBy(
                    item->item.getNodeLevel() + Constants.UNDER_LINE + item.getParentNodeCode()));

            for (Map.Entry<String, List<DeviceDiyTreeNodeVO>> levelAndPcodeEntry : levelAndPcodeMap.entrySet()) {
                List<DeviceDiyTreeNodeVO> value = levelAndPcodeEntry.getValue();
                if(value.size() == 1 &&  Constants.ZERO.equals(value.get(Constants.ZERO_NUM).getParentNodeCode())){
                    String rootNodeCode = StringUtils.join(treeId, "01");
                    map.put(value.get(Constants.ZERO_NUM).getNodeCode(),rootNodeCode);
                }else {
                    String nodeCode = "";
                    for (int i = 1; i <= value.size(); i++) {
                        DeviceDiyTreeNodeVO diyTreeNodeVO = value.get(i - 1);
                        nodeCode = map.get(diyTreeNodeVO.getParentNodeCode()) + DeviceSyncConstants.HORIZON_LINE + String.format("%0"+ 2 +"d", i);
                        map.put(diyTreeNodeVO.getNodeCode(),nodeCode);
                    }
                }
            }
        }
        String nodeListStr = JSON.toJSONString(nodeList);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            nodeListStr = nodeListStr.replaceAll(
                    DeviceSyncConstants.QUOTATION + entry.getKey() + DeviceSyncConstants.QUOTATION,
                    DeviceSyncConstants.QUOTATION + entry.getValue() + DeviceSyncConstants.QUOTATION);
        }
        List<DeviceDiyTreeNodeVO> newNodeList =  JSON.parseArray(nodeListStr, DeviceDiyTreeNodeVO.class);
        deviceInfoVO.setNodeList(newNodeList);
        //重新保存自定义树节点数据
        baseMapper.saveDiyNode(deviceInfoVO);
    }
    public Result delete(DeviceDiyTreeRequestVO deviceDiyTreeRequestVO){
        DeviceDiyTreeVO deviceInfoVO = new DeviceDiyTreeVO();
        BeanUtils.copyProperties(deviceDiyTreeRequestVO,deviceInfoVO);

        DeviceDiyTreeVO one = baseMapper.getOne(deviceInfoVO);
        if(one.getTreeStatus().equals(Constants.ZERO)){
            return Result.fail("启用状态不允许删除");
        }
        //删除自定义树节点数据
        baseMapper.delDiy(deviceInfoVO);
        //删除自定义树节点数据
        baseMapper.delDiyNode(deviceInfoVO);
        return Result.data("删除成功");
    }

    public void check(DeviceDiyTreeVO deviceInfoVO){
        List<DeviceDiyTreeVO> list = baseMapper.getList(deviceInfoVO);
        if(StringUtils.isEmpty(deviceInfoVO.getTreeId()) && list.size() >= 10){
            throw new CustomException("设备树不能超过10个");
        }

        List<DeviceDiyTreeVO> treeNameList = list.stream().filter(e->deviceInfoVO.getTreeName().equals(e.getTreeName())
                && !e.getTreeId().equals(deviceInfoVO.getTreeId())).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(treeNameList)){
            throw new CustomException("设备树名称不能重复");
        }

        List<DeviceDiyTreeNodeVO> nodeList = deviceInfoVO.getNodeList();
        if(nodeList.stream().anyMatch(e -> e.getNodeLevel() > 7)){
            throw new CustomException("自定义设备树节点不能超过7级");
        }
    }

    @Override
    public Result<List<DeviceDiyTreeVO>> getList(DeviceDiyTreeVO deviceInfoVO){
        SysUser user = SecurityUtils.getLoginUser().getUser();
        deviceInfoVO.setTenantId(user.getTenantId());
        deviceInfoVO.setIndustryCode(user.getIndustryCode());
        return Result.data(baseMapper.getList(deviceInfoVO));
    }

    @Override
    public Result<List<DeviceDiyTreeResultVO>> getOriginalDiyTree(DeviceDiyTreeVO deviceInfoVO){
        SysUser user = SecurityUtils.getLoginUser().getUser();
        deviceInfoVO.setTenantId(user.getTenantId());
        deviceInfoVO.setIndustryCode(user.getIndustryCode());
        List<DeviceDiyTreeResultVO> originalDiyTree = baseMapper.getOriginalDiyTree(deviceInfoVO);
        JSONArray treeAry = this.listToTree(originalDiyTree, Constants.CODE, Constants.PARENT_CODE);
        List<DeviceDiyTreeResultVO> resultTree = JSON.parseArray(JSON.toJSONString(treeAry), DeviceDiyTreeResultVO.class);
        return Result.data(resultTree);
    }

    @Override
    @DataScope(deviceColumn="t.device_code", acceptColumn = "ord.accept_node",tenantColumn="t.tenant_id",alarmSourceVal = "4", industryCode="t.industry_code")
    public Result<List<DeviceDiyTreeResultVO>> getDiyTree(DeviceDiyTreeResultVO deviceDiyTreeResultVO){
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser) {
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser user = deviceInfoMapper.selectUserOnlyByUserName(tokenUser.getUserName());
            deviceDiyTreeResultVO.setUserId(user.getUserId());
            deviceDiyTreeResultVO.setTenantId(tokenUser.getTenantId());
            deviceDiyTreeResultVO.setIndustryCode(tokenUser.getIndustryCode());
        }
        //查询所有自定义树节点
        List<DeviceDiyTreeResultVO> allDiyTree = baseMapper.getAllDiyTree(deviceDiyTreeResultVO);

        List<DeviceDiyTreeResultVO> collect = allDiyTree.stream().filter(e -> Constants.TWO.equals(e.getNodeType())).collect(Collectors.toList());
        //是否有查询条件
        boolean flag = false;
        if(Constants.ZERO.equals(deviceDiyTreeResultVO.getIsMonitorFlag())
                || !CollectionUtils.isEmpty(deviceDiyTreeResultVO.getCategoryCodeList())
                || !CollectionUtils.isEmpty(deviceDiyTreeResultVO.getStatusList())) {
            flag = true;
        }

        if(!CollectionUtils.isEmpty(allDiyTree) && (!CollectionUtils.isEmpty(collect) || flag)){
            Set<String> parentsList = new HashSet<>();
            //无条件
            if(!flag){
                parentsList = allDiyTree.stream().filter(e -> Constants.ONE.equals(e.getNodeType())).map(DeviceDiyTreeResultVO::getCode).collect(Collectors.toSet());
            }

            //根据节点code分组
            Map<String, List<DeviceDiyTreeResultVO>> allDiyTreeMap = allDiyTree.stream().collect(Collectors.groupingBy(DeviceDiyTreeResultVO::getCode));
            //根据查询条件查询自定义树节点
            List<DeviceDiyTreeResultVO> diyTree = baseMapper.getDiyTree(deviceDiyTreeResultVO);
            if(!CollectionUtils.isEmpty(diyTree)){
                //查询已收藏节点code的所有父级节点code
                parentsList.addAll(this.getParentsList(diyTree, allDiyTreeMap));
                for (String s : parentsList) {
                    diyTree.addAll(allDiyTreeMap.get(s));
                }
            }
            allDiyTree = diyTree;

            //无条件
            if(!flag){
                //根据节点code分组
                Map<String, List<DeviceDiyTreeResultVO>> allDiyTreeMap1 = allDiyTree.stream().collect(Collectors.groupingBy(DeviceDiyTreeResultVO::getCode));
                Collections.sort(allDiyTree, Comparator.comparing(DeviceDiyTreeResultVO::getId));
                Collections.reverse(allDiyTree);
                for (int i = allDiyTree.size() - 1; i >= 0; i--) {
                    DeviceDiyTreeResultVO diyTreeResultVO = allDiyTree.get(i);
                    if (!allDiyTreeMap1.containsKey(diyTreeResultVO.getParentCode()) && !Constants.ONE.equals(diyTreeResultVO.getLevel())) {
                        allDiyTree.remove(diyTreeResultVO);
                        allDiyTreeMap1.remove(diyTreeResultVO.getCode());
                    }
                }
            }
        }
        return Result.data(this.formatTreeList(allDiyTree,currentUser));
    }

    public JSONArray listToTree(List list, String id, String pid) {
        JSONArray arr = JSON.parseArray(JSON.toJSONString(list));
        JSONArray res = new JSONArray();
        JSONObject hash = new JSONObject();
        Iterator var6 = arr.iterator();

        Object anArr;
        JSONObject aVal;
        while(var6.hasNext()) {
            anArr = var6.next();
            aVal = (JSONObject)anArr;
            hash.put(aVal.getString(id), aVal);
        }
        var6 = arr.iterator();
        while(var6.hasNext()) {
            anArr = var6.next();
            aVal = (JSONObject)anArr;
            JSONObject hashVP = (JSONObject)hash.get(aVal.get(pid).toString());
            if (hashVP != null) {
                JSONArray ch;
                if (hashVP.get(Constants.PARAM_LIST) != null) {
                    ch = (JSONArray)hashVP.get(Constants.PARAM_LIST);
                    ch.add(aVal);
                    hashVP.put(Constants.PARAM_LIST, ch);
                } else {
                    ch = new JSONArray();
                    ch.add(aVal);
                    hashVP.put(Constants.PARAM_LIST, ch);
                }
            } else {
                res.add(aVal);
            }
        }
        return res;
    }

    public List<DeviceDiyTreeResultVO> formatTreeList(List<DeviceDiyTreeResultVO> diyTree,LoginUser currentUser) {
        if(CollectionUtils.isEmpty(diyTree)){
            return new ArrayList<>();
        }
        List<DeviceDiyTreeResultVO> diyTreeNew = new ArrayList<>();
        //根据节点级别分组
        Map<String, List<DeviceDiyTreeResultVO>> levelGroupMap = diyTree.stream().collect(Collectors.groupingBy(DeviceDiyTreeResultVO::getLevel));
        //按照节点倒序排列
        Map<String, List<DeviceDiyTreeResultVO>> sortLevelGroupMap = new TreeMap<>(Comparator.reverseOrder());
        sortLevelGroupMap.putAll(levelGroupMap);

        for (Map.Entry<String, List<DeviceDiyTreeResultVO>> levelEntry : sortLevelGroupMap.entrySet()) {
            //根据节点级别+父节点编码分组
            Map<String, List<DeviceDiyTreeResultVO>> levelAndPcodeMap = levelEntry.getValue().stream().collect(Collectors.groupingBy(
                    item->item.getLevel() + Constants.UNDER_LINE + item.getParentCode()));

            for (Map.Entry<String, List<DeviceDiyTreeResultVO>> levelAndPcodeEntry : levelAndPcodeMap.entrySet()) {
                String[] split = levelAndPcodeEntry.getKey().split(Constants.UNDER_LINE);
                int keyLevel = Integer.parseInt(split[Constants.ZERO_NUM]);
                String keyCode = split[Constants.ONE_NUM];
                int isMonitorNum = 0;
                for (DeviceDiyTreeResultVO deviceDiyTreeResultVO : levelAndPcodeEntry.getValue()) {
                    isMonitorNum = isMonitorNum + (Objects.equals(deviceDiyTreeResultVO.getIsMonitor(), Constants.ONE) ? 1 : 0);
                }

                if(keyLevel > Constants.ONE_NUM){
                    List<DeviceDiyTreeResultVO> resultVOS = sortLevelGroupMap.get(String.valueOf(keyLevel - 1));
                    for (DeviceDiyTreeResultVO deviceDiyTreeResultVO : resultVOS) {
                        if(keyCode.equals(deviceDiyTreeResultVO.getCode())){
                            deviceDiyTreeResultVO.setIsMonitorNum(isMonitorNum);
                            if(levelAndPcodeEntry.getValue().size() == isMonitorNum){
                                deviceDiyTreeResultVO.setIsMonitor(Constants.ONE);
                            }
                            break;
                        }
                    }
                }
            }
        }

        //构造新集合
        for (Map.Entry<String, List<DeviceDiyTreeResultVO>> levelEntry : sortLevelGroupMap.entrySet()) {
            diyTreeNew.addAll(levelEntry.getValue());
        }

        //计算节点后的数量  在线通道数量/通道数量
        for (DeviceDiyTreeResultVO deviceDiyTreeResultVO : diyTreeNew) {
            String code = deviceDiyTreeResultVO.getCode();
            //通道总数量
            int num = diyTreeNew.stream().filter(e -> e.getCode().contains(code) && Constants.THREE.equals(e.getNodeType()))
                    .map(DeviceDiyTreeResultVO::getChannelCode)
                    .collect(Collectors.toSet())
                    .size();
            //通道总数量
            int onlineNum = diyTreeNew.stream().filter(e -> e.getCode().contains(code)  && Constants.THREE.equals(e.getNodeType()) && Constants.CHANNEL_ONLINE.equals(e.getStatus()))
                    .map(DeviceDiyTreeResultVO::getChannelCode)
                    .collect(Collectors.toSet())
                    .size();
            deviceDiyTreeResultVO.setNum(num);
            deviceDiyTreeResultVO.setOnlineNum(onlineNum);
        }

        Collections.sort(diyTreeNew, Comparator.comparing(DeviceDiyTreeResultVO::getId));

        List<DeviceDiyTreeResultVO> collect = diyTreeNew.stream().filter(e -> Constants.TWO.equals(e.getNodeType())).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(collect)){
            Set<String> fileUrls = new HashSet<>();
            collect.forEach(dev -> {
                fileUrls.add(dev.getImageUrl());
            });
            //加密
            Map<String, String> result = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList(fileUrls));

            List<DeviceDiyTreeResultVO> alias = baseMapper.getAlias(collect,currentUser.getUser().getTenantId(),currentUser.getUser().getIndustryCode());
            if(!CollectionUtils.isEmpty(alias)){
                Map<String,String> map = new HashMap<>();
                for (DeviceDiyTreeResultVO deviceDiyTreeResultVO : alias) {
                    map.put(deviceDiyTreeResultVO.getDeviceCode(),deviceDiyTreeResultVO.getDeviceName());
                    map.put(deviceDiyTreeResultVO.getChannelCode(),deviceDiyTreeResultVO.getChannelName());
                }
                for (DeviceDiyTreeResultVO deviceDiyTreeResultVO : diyTreeNew) {
                    String deviceAlias = map.get(deviceDiyTreeResultVO.getDeviceCode());
                    String channelAlias = map.get(deviceDiyTreeResultVO.getChannelCode());
                    if(Constants.TWO.equals(deviceDiyTreeResultVO.getNodeType()) && !StringUtils.isEmpty(deviceAlias)){
                        deviceDiyTreeResultVO.setName(deviceAlias);
                        deviceDiyTreeResultVO.setDeviceAlias(deviceAlias);
                        if (!StringUtils.isEmpty(deviceDiyTreeResultVO.getImageUrl())) {
                            deviceDiyTreeResultVO.setImageUrl(result.get(deviceDiyTreeResultVO.getImageUrl()));
                        }
                    }
                    if(Constants.THREE.equals(deviceDiyTreeResultVO.getNodeType()) && !StringUtils.isEmpty(channelAlias)){
                        deviceDiyTreeResultVO.setName(channelAlias);
                        deviceDiyTreeResultVO.setChannelAlias(channelAlias);
                        deviceDiyTreeResultVO.setImageUrl(null);
                    }
                }
            }
        }

        //构造树形结构
        JSONArray treeAry = this.listToTree(diyTreeNew, Constants.CODE, Constants.PARENT_CODE);
        return JSON.parseArray(JSON.toJSONString(treeAry), DeviceDiyTreeResultVO.class);
    }

    private Set<String> getParentsList(List<DeviceDiyTreeResultVO> diyTree,Map<String, List<DeviceDiyTreeResultVO>> allDiyTreeMap) {
        Set<String> nodeCodeList = new HashSet<>();
        Map<String, List<DeviceDiyTreeResultVO>> diyTreeMap = diyTree.stream().collect(Collectors.groupingBy(DeviceDiyTreeResultVO::getCode));
        // 获取组织父节点直至根节点
        for (DeviceDiyTreeResultVO vo : diyTree) {
            boolean stopFlag = true;
            DeviceDiyTreeResultVO parentVo = vo;
            while (stopFlag) {
                String parentCode = parentVo.getParentCode();
                if(Constants.ZERO.equals(parentCode)){
                    stopFlag = false;
                }else{
                    if(!diyTreeMap.containsKey(parentCode)){
                        nodeCodeList.add(parentCode);
                    }
                    parentVo = allDiyTreeMap.get(parentCode).get(Constants.ZERO_NUM);
                }
            }
        }
        return nodeCodeList;
    }

    /**
     * 生成length位的随机数
     * @param length 总位数
     * @return
     */
    private String getRandomCode(int length){
        String num = String.valueOf(IdUtils.buildRandom(length));
        int i1 = length - num.length();
        String str2;
        if(i1 > 0){
            str2 = num + String.format("%0"+ i1 +"d", 0);
        }else{
            str2 = num;
        }
        return str2;
    }
}
