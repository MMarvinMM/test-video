package com.chinatower.cascade.modules.config.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.cascade.api.entity.PlatformDataConfigVO;
import com.chinatower.cascade.api.entity.PlatformDataVO;
import com.chinatower.cascade.modules.config.mapper.CascadeConfigMapper;
import com.chinatower.cascade.modules.config.service.ICascadeConfigService;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.platform.common.utils.TreeUtils;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.dto.ColonyApp;
import com.chinatower.platform.dto.ColonyNode;
import com.chinatower.platform.dto.ColonyRelationDto;
import com.chinatower.platform.model.CascadeResult;
import com.chinatower.platform.model.dto.CascadeConfigSyncVO;
import com.chinatower.platform.common.utils.security.AESUtil;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 功能说明：平台配置信息service实现类
 *
 * @author hued
 * @date 2023/10/24
 * @requestID
 */
@Service
@Slf4j
public class CascadeConfigServiceImpl implements ICascadeConfigService {
    @Autowired
    CascadeConfigMapper cascadeConfigMapper;

    public final String MIDDLE_LINE = "-";

    public final String GENERAL_URL = "generalInternetUrl";

    public final String VERSION = "version";
    public final String MINI_PROGRAM_VERSION = "miniProgramVersion";
    public final String WGT_VERSION = "wgtVersion";
    public final String COLONY_VERSION = "colonyVersion";
    public final String NODE_ALIAS = "nodeAlias";



    @Value("${platform.config.sk:2178eh23iudh287932d123cd}")
    private String platformDataConfig;
    @Override
    @Transactional
    public CascadeResult sync(ColonyRelationDto colonyRelationDto) {
        List<ColonyNode> colonyRelation = colonyRelationDto.getColonyRelation();

        Map<String, String> codeAlias = new HashMap<>();
        if(!CollectionUtils.isEmpty(colonyRelation)){
            List<CascadeConfigSyncVO> list = new ArrayList<>();

            //属地版本list
            List<String> colonyCodeList = new ArrayList<>();
            for (ColonyNode colonyNode : colonyRelation) {
                CascadeConfigSyncVO cascadeConfigSyncVO = new CascadeConfigSyncVO();
                BeanUtils.copyProperties(colonyNode,cascadeConfigSyncVO);
                cascadeConfigSyncVO.setLevel(String.valueOf(colonyNode.getLevel()));
                List<ColonyApp> apps = colonyNode.getApps();
                if (!CollectionUtils.isEmpty(apps)) {
                    for (ColonyApp app : apps) {
                        if("general".equals(app.getAppId())){
                            cascadeConfigSyncVO.setGeneralUrl(app.getAppAddr());
                            String extra = app.getExtra();
                            if (StringUtil.isNotEmpty(extra)) {
                                JSONObject parse = JSON.parseObject(extra);
                                if (parse.containsKey(GENERAL_URL)) {
                                    String generalInternetUrl = (String) parse.get(GENERAL_URL);
                                    if (StringUtil.isNotEmpty(generalInternetUrl)) {
                                        cascadeConfigSyncVO.setProgramUrl(generalInternetUrl);
                                    }
                                }
                                if (parse.containsKey(MINI_PROGRAM_VERSION)) {
                                    String miniProgramVersion = (String) parse.get(MINI_PROGRAM_VERSION);
                                    if (StringUtil.isNotEmpty(miniProgramVersion)) {
                                        cascadeConfigSyncVO.setMiniProgramVersion(miniProgramVersion);
                                    }
                                }
                                if (parse.containsKey(WGT_VERSION)) {
                                    String wgtVersion = (String) parse.get(WGT_VERSION);
                                    if (StringUtil.isNotEmpty(wgtVersion)) {
                                        cascadeConfigSyncVO.setWgtVersion(wgtVersion);
                                    }
                                }
                                if (parse.containsKey(COLONY_VERSION)) {
                                    String colonyVersion = (String) parse.get(COLONY_VERSION);
                                    if (StringUtil.isNotEmpty(colonyVersion)) {
                                        cascadeConfigSyncVO.setColonyVersion(colonyVersion);
                                        colonyCodeList.add(colonyVersion);
                                    }
                                }
                                if (parse.containsKey(NODE_ALIAS)) {
                                    String alias = (String) parse.get(NODE_ALIAS);
                                    if (StringUtil.isNotEmpty(alias)) {
                                        cascadeConfigSyncVO.setNodeAlias(alias);
                                        codeAlias.put(colonyNode.getColonyCode(), alias);
                                    }
                                }
                            }

                        }
                    }
                }
                list.add(cascadeConfigSyncVO);
            }

            if(!CollectionUtils.isEmpty(list)){
                //先删后增
                cascadeConfigMapper.deleteAll();
                cascadeConfigMapper.insertBatch(list);
            }

            if (!CollectionUtils.isEmpty(colonyCodeList)) {

                //查询ios、android都未配置的版本号
                List<String> delVersion = cascadeConfigMapper.findNoneWgtVersion();
                if (!delVersion.isEmpty()) {
                    //删除未配置的wgt版本数据
                    cascadeConfigMapper.deleteByUpgradeIsNull(delVersion);
                }

                //查询已存在的版本
                List<String> existColonyList = cascadeConfigMapper.findWgtByColonyCodeList(colonyCodeList);
                List<String> addList= colonyCodeList.stream().filter(item -> !existColonyList.contains(item)).collect(Collectors.toList());

                //不存在的新增
                if (!CollectionUtils.isEmpty(addList)) {
                    HashSet<String> strings = new HashSet<>(addList);
                    cascadeConfigMapper.addBatchWgtVersion(strings);
                }

                //查询未配置的小程序属地版本
                List<String> delVersionMiniProgram = cascadeConfigMapper.findNoneMiniProgramVersion();
                if (!delVersionMiniProgram.isEmpty()) {
                    //删除未配置的小程序属地版本数据
                    cascadeConfigMapper.deleteByMiniProgramUpgradeIsNull(delVersionMiniProgram);
                }
                //查询已存在的小程序属地版本
                List<String> existColonyListMiniProgram = cascadeConfigMapper.findMiniProgramByColonyCodeList(colonyCodeList);
                List<String> addListMiniProgram = colonyCodeList.stream().filter(item -> !existColonyListMiniProgram.contains(item)).collect(Collectors.toList());
                //不存在的新增
                if (!CollectionUtils.isEmpty(addListMiniProgram)) {
                    HashSet<String> strings = new HashSet<>(addListMiniProgram);
                    cascadeConfigMapper.addBatchMiniProgramVersion(strings);
                }
            }

            //redis缓存
            // redis存hash类型数据，格式如下：
            //	key：固定值（COLONY_RELATION_ALL:平台编码）
            //	value：平台配置数据JSON串
            Map<String, Object> mulitCodeMap = new HashMap<>();

            Map<String, Object> mulitAliasMap = new HashMap<>();

            for (ColonyNode cascadeConfigSyncVO : colonyRelation) {
                //编码
//                RedisUtil.set(Constants.COLONY_RELATION_ALL + cascadeConfigSyncVO.getColonyCode(),
//                        JSONObject.toJSON(cascadeConfigSyncVO),
//                        RedisDBType.PRIMARY);
                mulitCodeMap.put(Constants.COLONY_RELATION_ALL + cascadeConfigSyncVO.getColonyCode(), JSONObject.toJSON(cascadeConfigSyncVO));
                //别名
                if (codeAlias.containsKey(cascadeConfigSyncVO.getColonyCode())) {
                    String key = codeAlias.get(cascadeConfigSyncVO.getColonyCode());
                    log.info("增加redis key:{}", JSON.toJSONString(key));
                    mulitAliasMap.put(Constants.COLONY_RELATION_ALIAS_ALL + key, JSONObject.toJSON(cascadeConfigSyncVO));
//                    RedisUtil.set(Constants.COLONY_RELATION_ALIAS_ALL + key,
//                            JSONObject.toJSON(cascadeConfigSyncVO),
//                            RedisDBType.PRIMARY);
                }

            }
            //新增编码
            RedisUtil.multiSet(mulitCodeMap, RedisDBType.PRIMARY);
            //新增别名
            RedisUtil.multiSet(mulitAliasMap, RedisDBType.PRIMARY);
        }

        return CascadeResult.success("平台配置信息同步成功！");
    }

    public int containEntity(String colonyCode, List<CascadeConfigSyncVO> list) {
        int res = -1;
        for (int i = 0; i < list.size(); i++) {
            if (colonyCode.equals(list.get(i).getColonyCode())) {
                res = i;
                break;
            }
        }
        return res;
    }
    @Override
    public Result<List<PlatformDataConfigVO>> getInfo(List<String> list){
        return Result.data(cascadeConfigMapper.getInfo(list));
    }

    @Override
    public Result<List<TreeSelect>> getPlatformTree(){
        List<TreeSelect> tree = new ArrayList<>();
        List<PlatformDataConfigVO> info = cascadeConfigMapper.getInfo(null);

        for (PlatformDataConfigVO platformDataConfigVO : info) {
            tree.add(new TreeSelect(platformDataConfigVO.getPlatformCode(),
                    platformDataConfigVO.getPlatformName(),
                    platformDataConfigVO.getPlatformParentCode(),
                    null,
                    null));
        }
        return Result.data(TreeUtils.listToTreeSelect(tree));
    }

    @Override
    public Result<List<PlatformDataVO>> queryListByKey(String key){
        if(StringUtils.isEmpty(key)){
            return Result.data(new ArrayList<>());
        }
        return Result.data(cascadeConfigMapper.queryListByKey(key));
    }

    @Override
    public String queryAll(String token) {
        String decrypt = AESUtil.decryptBase64(token);
        log.info("解密后的信息: {}", decrypt);
        String[] split = decrypt.split(MIDDLE_LINE);
        if (2 >= split.length) {
            throw new CustomException("解析错误", 400);
        }
        String time = split[1];
        DateTime parse = DateUtil.parse(time, "yyyyMMddHHmmss");
        DateTime now = DateUtil.dateSecond();
        long between = DateUtil.between(parse, now, DateUnit.SECOND);
        if (60 < between) {
            throw new CustomException("加密过期", 400);
        }
        String sk = split[2];
        if (Objects.equals(platformDataConfig, sk)) {
            List<CascadeConfigSyncVO> allConfigInfo = cascadeConfigMapper.getAllConfigInfo();
            String jsonString = JSON.toJSONString(allConfigInfo);
            return AESUtil.encryptBase64(jsonString);
        } else {
            throw new CustomException("token错误", 400);
        }
    }

    @Override
    public Boolean queryByPlatformCode(String platformCode) {
        int count = cascadeConfigMapper.existAlias(platformCode);
        return count > 0;
    }

}
