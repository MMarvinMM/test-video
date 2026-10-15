package com.chinatower.gis.modules.gis.service.impl;

import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.entity.Result;
import com.chinatower.gis.api.entity.GisSdkReq;
import com.chinatower.gis.api.entity.MeasureDistanceBatchChildVO;
import com.chinatower.gis.api.entity.MeasureDistanceBatchVO;
import com.chinatower.gis.api.entity.gis.ProfileAnalysisRes;
import com.chinatower.gis.api.entity.gis.profileAnalysisOneRes;
import com.chinatower.gis.api.entity.gis.profileAnalysisTwoRes;
import com.chinatower.gis.modules.gis.mapper.GisPgMapper;
import com.chinatower.gis.modules.gis.service.GisSdkService;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GisSdkServiceImpl implements GisSdkService {

    private final GisPgMapper gisPgMapper;

    @Override
    public Result polygonClipRasterStats(GisSdkReq req) {
        return Result.data(gisPgMapper.polygonClipRasterStats(req.getWkt(),req.getSampleNum()));
    }

    @Override
    public Result profileAnalysis(GisSdkReq req) {
        List<profileAnalysisOneRes> oneMap = gisPgMapper.profileAnalysisOne(req.getWkt(), req.getSampleNum());
        List<profileAnalysisTwoRes> twoMap = gisPgMapper.profileAnalysisTwo(req.getWkt(), req.getSampleNum());

        for (profileAnalysisTwoRes res : twoMap) {
            profileAnalysisOneRes oneRes = new profileAnalysisOneRes();
            oneRes.setSamplelength(res.getKeylength());
            oneRes.setNearestValue(res.getNearestValue());
            oneMap.add(oneRes);
        }

        // 排序
        oneMap.sort(Comparator.comparingDouble(profileAnalysisOneRes::getSamplelength));

        Double [][] samplePoint = new Double[oneMap.size()][2];
        for (int i = 0; i < oneMap.size(); i++) {
            profileAnalysisOneRes oneRes = oneMap.get(i);
            samplePoint[i][0] = oneRes.getSamplelength();
            samplePoint[i][1] = oneRes.getNearestValue();
        }
        Double [][] keyPoint = new Double[twoMap.size()][2];
        for (int i = 0; i < twoMap.size(); i++) {
            profileAnalysisTwoRes oneRes = twoMap.get(i);
            keyPoint[i][0] = oneRes.getKeylength();
            keyPoint[i][1] = oneRes.getNearestValue();
        }
        ProfileAnalysisRes res = new ProfileAnalysisRes();
        res.setSamplePoint(samplePoint);
        res.setKeyPoint(keyPoint);
        return Result.data(res);
    }

    @Override
    public Result measureDistanceLow(GisSdkReq req) {
        return Result.data(gisPgMapper.measureDistanceLow(req.getWkt(),req.getSampleNum()));
    }

    @Override
    public Result measureDistanceHigh(GisSdkReq req) {
        return Result.data(gisPgMapper.measureDistanceHigh(req.getWkt(),req.getSampleNum()));
    }

    @Override
    public Result measureDistanceBatch(MeasureDistanceBatchVO req) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < req.getVos().size(); i++) {
            MeasureDistanceBatchChildVO vo = req.getVos().get(i);
            list.add(i + "_" + vo.getType());
        }
        Map<Integer, Double> params = new HashMap<>();
        ThreadParallelUtil.run(list,d->{
            String[] split = d.split("_");
            Integer sub = Integer.parseInt(split[0]);
            MeasureDistanceBatchChildVO vo = req.getVos().get(sub);
            if (d.contains("high")){
                params.put(sub,gisPgMapper.measureDistanceHigh(vo.getWkt(),req.getSampleNum()));
            }else {
                params.put(sub,gisPgMapper.measureDistanceLow(vo.getWkt(),req.getSampleNum()));
            }
            return null;
        }, ThreadParallelUtil.ConfigFactory.DEFAULT_SINGLE);
        List<Double> values = new ArrayList<>();
        for(Map.Entry<Integer, Double> entry : params.entrySet()){
            values.add(entry.getValue());
        }
        return Result.data(values);
    }

    @Override
    public Result measureAreaLow(GisSdkReq req) {
        return Result.data(gisPgMapper.measureAreaLow(req.getWkt(),req.getSampleNum()));
    }

    @Override
    public Result measureAreaHigh(GisSdkReq req) {
        return Result.data(gisPgMapper.measureAreaHigh(req.getWkt(),req.getSampleNum()));
    }
}
