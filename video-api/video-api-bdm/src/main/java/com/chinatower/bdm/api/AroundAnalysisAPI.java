package com.chinatower.bdm.api;

import com.chinatower.bdm.api.entity.AroundAnalysisParamVO;
import com.chinatower.bdm.api.entity.AroundAnalysisResultVO;
import com.chinatower.bdm.api.factory.AroundAnalysisFallbackFactory;
import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "AroundAnalysisAPI",
        value = ServiceNameConstants.BDM_SERVICE,
        fallbackFactory = AroundAnalysisFallbackFactory.class)
public interface AroundAnalysisAPI {

    /**
     * 周边分析
     * @apiNote 根据传参经纬度为圆心，传参距离为半径，查询权限范围内、距离范围内指定资源类型的资源基本信息集合
     * @apiVersion 13.1
     * @tag 十三、网格能力
     * @requestDemo
     * {
     * 	"type": "1,2,3,4,5",
     * 	"longitude": "121.5144113498264",
     * 	"latitude": "38.84739339192708",
     * 	"radius": "10000",
     * 	"alarmTimeStart": "2023-08-18 00:00:00",
     * 	"alarmTimeEnd": "2023-08-24 23:59:59"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *     name: 名称
     *     location: 位置
     *     distance: 距离（km）
     *     longitude: 经度
     *     latitude: 纬度
     *     type: 类型(1.摄像机 2.无人机 3.大喇叭 4.网格员 5.告警)
     *     icon: 资源图标
     *     id: 唯一标识
     *     status: 状态
     *     imageUrl: 摄像机自定义图标
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1690944818426,
     * 	"data": [
     *         {
     * 			"name": "测试新增客0323lxt111",
     * 			"location": "辽宁省大连市甘井子区七贤岭街道汇贤街大连腾飞园区(汇贤街)",
     * 			"distance": "0.1",
     * 		    "longitude": "116.147549000",
     * 		    "latitude": "39.757563000",
     * 		    "type": "1",
     * 		    "status": "0",
     * 		    "icon": "",
     * 		    "id": "11010800011327000006",
     * 		    "imageUrl": "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/ecd0c97ddb7c95b8db34f721db7d595f/94736018f6e941164e7ba1757a43dab5/c35d5996b5a04bbd9676d21bfd1803c8.svg"
     *        }
     * 	]
     * }
     */
    @PostMapping("/aroundAnalysis/getAroundAnalysisList")
    Result<List<AroundAnalysisResultVO>> getAroundAnalysisList(@RequestBody AroundAnalysisParamVO aroundAnalysisParamVO);


    /**
     * 周边分析
     * @apiNote 根据传参经纬度为圆心，传参距离为半径，查询权限范围内、距离范围内指定资源类型的资源基本信息集合
     * @apiVersion 13.2
     * @tag 十三、网格能力
     * @requestDemo
     * {
     * 	"type": "1,2,3,4,5",
     * 	"longitude": "121.5144113498264",
     * 	"latitude": "38.84739339192708",
     * 	"radius": "10000",
     * 	"alarmTimeStart": "2023-08-18 00:00:00",
     * 	"alarmTimeEnd": "2023-08-24 23:59:59"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *     name: 名称
     *     location: 位置
     *     distance: 距离（km）
     *     longitude: 经度
     *     latitude: 纬度
     *     type: 类型(1.摄像机 2.无人机 3.大喇叭 4.网格员 5.告警,6.资源,7.雷达,8.物联设备,9.执法记录仪)
     *     icon: 资源图标
     *     id: 唯一标识
     *     status: 状态
     *     imageUrl: 摄像机自定义图标
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1690944818426,
     * 	"data": [
     *         {
     * 			"name": "测试新增客0323lxt111",
     * 			"location": "辽宁省大连市甘井子区七贤岭街道汇贤街大连腾飞园区(汇贤街)",
     * 			"distance": "0.1",
     * 		    "longitude": "116.147549000",
     * 		    "latitude": "39.757563000",
     * 		    "type": "1",
     * 		    "status": "0",
     * 		    "icon": "",
     * 		    "id": "11010800011327000006",
     * 		    "imageUrl": "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/ecd0c97ddb7c95b8db34f721db7d595f/94736018f6e941164e7ba1757a43dab5/c35d5996b5a04bbd9676d21bfd1803c8.svg"
     *        }
     * 	]
     * }
     */
    @PostMapping("/aroundAnalysisOwn/getAroundAnalysisOwnList")
    Result<List<AroundAnalysisResultVO>> getAroundAnalysisOwnList(@RequestBody AroundAnalysisParamVO aroundAnalysisParamVO);
}
