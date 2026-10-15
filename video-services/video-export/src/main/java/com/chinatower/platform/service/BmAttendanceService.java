package com.chinatower.platform.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.bdm.api.entity.*;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.mapper.BmAttendanceExportMapper;
import com.chinatower.platform.utils.JNConstants;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import javax.annotation.Resource;
import java.util.*;

@Service
@Slf4j
public class BmAttendanceService {
    @Resource
    private BmAttendanceExportMapper bmAttendanceDAO;


    /**
     * 网格员考勤列表
     * @author zl
     * @param bmAttendanceParam
     * @return
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员考勤
     */
    public List<BmAttendanceListVO> getList(BmAttendanceParam bmAttendanceParam){
        bmAttendanceParam.setIndustryCode(SecurityUtils.getLoginUser().getUser().getIndustryCode());
        bmAttendanceParam.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
        List<BmAttendanceListVO> list = bmAttendanceDAO.getList(bmAttendanceParam);
        if (!CollectionUtils.isEmpty(list)) {
            //根据网格员id查询归属的网格名称
            List<GridKeeperRefVO> refVOList = bmAttendanceDAO.getGridNamesByKeeperBatch(getKeeperIds(JSONArray.parseArray(JSONArray.toJSONString(list))));
            if (!CollectionUtils.isEmpty(refVOList)) {
                for (BmAttendanceListVO vo : list) {
                    refVOList.forEach(item -> {
                        if (item.getKeeperId().toString().equals(vo.getKeeperId()) && item.getGridId().equals(vo.getGridId()) ) {
                            vo.setGridName(!StringUtils.isEmpty(item.getGridName()) ? item.getGridName().replace(JNConstants.COMMA, JNConstants.COMMA_CH) : JNConstants.EMPTY_STR);
                            vo.setLoginName(item.getLoginName());
                        }
                    });
                }
            }
        }
        return list;
    }

    /**
     * 功能说明：查询历史打卡记录
     * @author zl
     * @param bmAttendanceHistoryParam
     * @return  List<BmAttendanceHistoryListVO>
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员考勤
     */
    @DataScope(tenantColumn = "a.tenant_id",industryCode = "a.industry_code")
    public List<BmAttendanceHistoryListVO> getHistoryList(BmAttendanceHistoryParam bmAttendanceHistoryParam){
        //查询历史打卡记录
        List<BmAttendanceHistoryListVO> list = bmAttendanceDAO.getHistoryList(bmAttendanceHistoryParam);
        if(!CollectionUtils.isEmpty(list)){
            //根据网格员id查询归属的网格名称
            List<GridKeeperRefVO> refVOList = bmAttendanceDAO.getGridNamesByKeeper(list.get(JNConstants.ZERO_NUM));

            if(!CollectionUtils.isEmpty(refVOList)){
                //根据当天时间给 当天的打卡记录赋 状态  或者 删除 还未到打卡时间的记录
                Iterator<BmAttendanceHistoryListVO> it = list.iterator();
                while (it.hasNext()) {
                    BmAttendanceHistoryListVO vo = it.next();
                    DateTime date = DateUtil.date();
                    String dateTime = DateUtil.format(date, JNConstants.DATE_FORMATE);
                    String[] s = dateTime.split(JNConstants.EMPTY_SPACE);
                    String dateStr = s[0];
                    if (dateStr.equals(vo.getDate())) {
                        if (JNConstants.LINE_CENTER.equals(vo.getTime())) {
                            if (JNConstants.ONE.equals(vo.getType())) {
                                if (date.getTime() > DateUtil.parse(vo.getDate() + JNConstants.EMPTY_SPACE + vo.getSignInTime()).getTime()) {
                                    vo.setStatus(JNConstants.THREE);
                                } else {
                                    it.remove();
                                }
                            } else {
                                //判断时间
                                if ( date.getTime() > DateUtil.parse(vo.getDate() + JNConstants.EMPTY_SPACE + vo.getSignOutTime()).getTime()) {
                                    vo.setStatus(JNConstants.SIX);
                                } else {
                                    it.remove();
                                }
                            }
                        }
                    }
                    vo.setGridName(!StringUtils.isEmpty(refVOList.get(0).getGridName())?refVOList.get(0).getGridName().replace(JNConstants.COMMA,JNConstants.COMMA_CH):JNConstants.EMPTY_STR);
                    vo.setLoginName(refVOList.get(0).getLoginName());
                }
            }
        }
        return list;
    }

    /**
     * 功能说明：数据处理，jsonArray转list
     * @author zl
     * @param json
     * @return
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__网格员考勤
     */
    private List<String> getKeeperIds(JSONArray json){
        List<String> list = new ArrayList<>();
        for(Object object : json){
            JSONObject obj = (JSONObject) object;
            String keeperId = obj.getString("keeperId");
            list.add(keeperId);
        }
        System.out.println(list);
        return list;
    }


}
