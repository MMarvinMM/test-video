package com.chinatower.industry.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.industry.api.factory.IndustryAlgorithmFallbackFactory;
import com.chinatower.industry.dto.AlgorithmInfoDO;
import com.chinatower.industry.dto.IndustryAlgorithmDTO;
import com.chinatower.industry.vo.IndustryAlgorithmQueryVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@FeignClient(contextId = "industryAlgorithmService", value = ServiceNameConstants.DEVICE_SERVICE, fallbackFactory = IndustryAlgorithmFallbackFactory.class)
public interface IndustryAlgorithmService {

    /**
     * 算法查询接口
     * @apiNote 算法查询接口
     * @apiVersion 3.28
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "tenantId":"121063278",
     *     "industryCode":"200001",
     *     "deviceCode":"11010800831327000003"
     * }
     * @responseDemo
     * total 总数
     *  algorithmCode 算法编码
     *  algorithmName 算法名称
     *  alarmType 告警类型
     *  typeName 告警类型名称
     * {
     *     "total":6,
     *     "rows":[
     *         {
     *             "algorithmCode":"FJYJ-1212102001",
     *             "algorithmName":"",
     *             "alarmType":"1212102",
     *             "typeName":""
     *         },
     *         {
     *             "algorithmCode":"ZGTT-1414107001",
     *             "algorithmName":"",
     *             "alarmType":"1414107",
     *             "typeName":""
     *         },
     *         {
     *             "algorithmCode":"XWKJ-1414105001",
     *             "algorithmName":"",
     *             "alarmType":"1414105",
     *             "typeName":""
     *         }
     *     ],
     *     "code":0
     * }
     */
    @PostMapping("/industry/algorithm/queryByTenantId")
    TableDataInfo queryByTenantId(@RequestBody IndustryAlgorithmQueryVo industryAlgorithmQueryVo);


    /**
     * 查询绑定的后端算法 + 前端算法
     * @apiNote 查询绑定的后端算法 + 前端算法
     * @apiVersion 3.29
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "pageNum":1,
     *     "pageSize":3,
     *     "deviceCodeList":[
     *         "11010000831327000051",
     *         "11010000831187000010"
     *     ]
     * }
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     * data map结构 设备编码对应的所有算法
     *      algorithmCode 算法编码
     *      algorithmName 算法名称
     *      alarmType 告警类型
     *      typeName 告警类型名称
     *      deviceCode 设备编码
     *      algorithmType 算法类型 0 后端算法 1 前端算法
     * {
     *     "code":200,
     *     "msg":"",
     *     "time":1687676088327,
     *     "data":{
     *         "11010800011327000012":[
     *             {
     *                 "algorithmCode":"ZGTT-1010102002",
     *                 "algorithmName":"",
     *                 "alarmType":"1010100",
     *                 "typeName":"",
     *                 "deviceCode":"11010800011327000012"
     *                 "algorithmType": 0
     *             },
     *             {
     *                 "algorithmCode":"QDYL-1212116001",
     *                 "algorithmName":"",
     *                 "alarmType":"1212116",
     *                 "typeName":"",
     *                 "deviceCode":"11010800011327000012"
     *                 "algorithmType": 0
     *             }
     *         ]
     *     }
     * }
     */
    @PostMapping("/industry/algorithm/queryByTenantIdAndDevices")
    Result<Map<String, List<IndustryAlgorithmDTO>>> queryByTenantIdAndDevices(@RequestBody @Valid IndustryAlgorithmQueryVo industryAlgorithmQueryVo);


    /**
     * 摄像机自身绑定的后端算法
     * @apiNote 摄像机自身绑定的后端算法
     * @apiVersion 3.30
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "userId": "5d707910cb73409da426f500d4cdde54",
     *     "tenantId": "121063379",
     *     "industryCode": "700002",
     *     "deviceCodeList":["11010500831327000149","11010800011327000012",
     *     "11010800831327000003","11010800831327000008",
     *     "35090200991327000001","42282500831327000027"]
     * }
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     * data 算法列表
     *      algorithmCode 算法编码
     *      algorithmName 算法名称
     *      alarmType 告警类型
     *      typeName 告警类型名称
     *      deviceCode 设备编码
     *      status 算法任务状态：0-启用  1-停用
     *      algorithmType 算法类型 0-后端 1-前端
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1694673080340,
     *     "data": [
     *         {
     *             "algorithmCode": "FJYJ-1212128001",
     *             "algorithmName": "人员密度监控",
     *             "alarmType": "1212128",
     *             "typeName": "人员密度监控告警",
     *             "deviceCode": "11010800831327000008",
     *             "status": "0",
     *             "algorithmType": "0"
     *         },
     *         {
     *             "algorithmCode": "FJYJ-1212102001",
     *             "algorithmName": "人员闯入监控",
     *             "alarmType": "1212102",
     *             "typeName": "人员闯入监控告警",
     *             "deviceCode": "11010800831327000003",
     *             "status": "0",
     *             "algorithmType": "0"
     *         }
     *     ]
     * }
     */
    @PostMapping("/industry/algorithm/queryConfigByTenantId")
    Result<List<IndustryAlgorithmDTO>> queryConfigByTenantId(@RequestBody IndustryAlgorithmQueryVo industryAlgorithmQueryVo);

    /**
     * 根据算法CODE列表批量获取算法详情信息
     * @param list
     * @return
     */
    @PostMapping("/video/algorithm/getAlgorithmInfoBatch")
    Result<List<AlgorithmInfoDO>> getAlgorithmInfoBatch(@RequestBody List<String> list);
}
