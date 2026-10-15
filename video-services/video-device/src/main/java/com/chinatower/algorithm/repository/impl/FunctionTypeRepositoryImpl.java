package com.chinatower.algorithm.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.algorithm.dataobject.FunctionTypeDO;
import com.chinatower.algorithm.mapper.FunctionTypeMapper;
import com.chinatower.algorithm.repository.IFunctionTypeRepository;
import com.chinatower.device.entity.vo.FunctionTypeVO;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.platform.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 功能类别表 服务实现类
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-16
 */
@Service
public class FunctionTypeRepositoryImpl extends ServiceImpl<FunctionTypeMapper, FunctionTypeDO> implements IFunctionTypeRepository {

    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Override
    public Map getFunction(String tenantId) {

        String industryCode = SecurityUtils.getLoginUser().getUser().getIndustryCode();
        String appVerCode = SecurityUtils.getLoginUser().getUser().getAppVerCode();

        Map map = new HashMap<>();
        List<FunctionTypeVO> oList = baseMapper.getAlgorithmByTenantId(tenantId, industryCode, appVerCode);
        setLabelList(oList);
        List<FunctionTypeVO> order = getObsSign(oList);
        List<FunctionTypeVO> noList = baseMapper.getAlgorithm(tenantId, industryCode, appVerCode);
        setLabelList(noList);
        List<FunctionTypeVO> noorder = getObsSign(noList);
        map.put("order", order);
        map.put("noorder", noorder);
        return map;
    }

    @Override
    public FunctionTypeVO getFunctionDetail(String id) {
        try {
            if (StringUtils.isEmpty(id)) {
                throw new CustomException("功能信息id不能为空！");
            }
            List<FunctionTypeVO> functionDetail = baseMapper.getFunctionDetail(id);
            FunctionTypeVO detail = null;
            if (!CollectionUtils.isEmpty(functionDetail)) {
                detail = batchGetObsSign(functionDetail.get(0));
            }
            return detail;
        } catch (Exception e) {
            log.error("getFunctionDetail异常",e);
            throw new CustomException(e.getMessage());
        }

    }

    public List<FunctionTypeVO> getObsSign(List<FunctionTypeVO> list) {
        if (!CollectionUtils.isEmpty(list)) {
            List<String> fileUrls = list.stream().filter(functionTypeVO -> StringUtils.isNotEmpty(functionTypeVO.getDesImageUrl())).map(FunctionTypeVO::getDesImageUrl).collect(Collectors.toList());
            List<String> desImageUrls = new ArrayList<>();
            fileUrls.stream().forEach(fileUrl -> desImageUrls.add(fileUrl.split(",")[0]));
            // 处理Obs文件URL加密返回
            //add by liuxt video-file改造
            Map<String, String> resultDesImageUrl = fileUrlEncryptUtil.dealObsFileUrlBatch(desImageUrls);
            String fileUrl;
            for (FunctionTypeVO f : list) {
                fileUrl = f.getDesImageUrl();
                if (StringUtils.isNotEmpty(fileUrl)) {
                    // 截取第一个，因为前面加密是截取了
                    fileUrl = fileUrl.split(",")[0];
                    if (fileUrl.indexOf("?") >= 0) {
                        fileUrl = fileUrl.substring(0, fileUrl.indexOf("?"));
                    }
                    f.setDesImageUrl(resultDesImageUrl.get(fileUrl));
                }
                f.setDesVideoUrl("");
            }
        }
        return list;
    }

    private List<FunctionTypeVO> setLabelList(List<FunctionTypeVO> list) {
        if (!CollectionUtils.isEmpty(list)) {
            for (FunctionTypeVO f : list) {
                if (StringUtils.isNotEmpty(f.getFunctionLabel())) {
                    f.setLabelList(Arrays.asList(f.getFunctionLabel().split(",")));
                }
            }
        }
        return list;
    }

    public FunctionTypeVO batchGetObsSign(FunctionTypeVO functionTypeVO) {
        try {
            if (functionTypeVO != null) {
                if (StringUtils.isNotEmpty(functionTypeVO.getDesImageUrl())) {
                    List<String> imgUrlList = Arrays.asList(functionTypeVO.getDesImageUrl().split(","));
                    if (!CollectionUtils.isEmpty(imgUrlList)) {
                        List<String> imgList = new ArrayList<>();
                        Map<String, String> stringStringMap = fileUrlEncryptUtil.dealObsFileUrlBatch(imgUrlList);
                        for (String imgUrl : imgUrlList) {
                            imgList.add(stringStringMap.get(imgUrl));
                        }
                        functionTypeVO.setDesImageUrlList(imgList);
                    }
                }
                if (StringUtils.isNotEmpty(functionTypeVO.getDesVideoUrl())) {
                    List<String> videoUrlList = Arrays.asList(functionTypeVO.getDesVideoUrl().split(","));
                    if (!CollectionUtils.isEmpty(videoUrlList)) {
                        List<String> videoList = new ArrayList<>();
                        Map<String, String> stringStringMap = fileUrlEncryptUtil.dealObsFileUrlBatch(videoUrlList);
                        for (String videoUrl : videoUrlList) {
                            videoList.add(stringStringMap.get(videoUrl));
                        }
                        functionTypeVO.setDesVideoUrlList(videoList);
                    }
                }
            }

        } catch (Exception e) {
            log.error("批量获取obs签名异常：" , e);
        }
        return functionTypeVO;
    }
}
