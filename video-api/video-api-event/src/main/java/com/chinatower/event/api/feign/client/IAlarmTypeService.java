package com.chinatower.event.api.feign.client;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.core.domain.AlarmSoundDO;
import com.chinatower.common.core.dto.AlarmTypeClassDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.event.api.feign.factory.AlarmTypeFallbackFactory;
import com.chinatower.event.entity.dto.AlarmReasonQueryDTO;
import com.chinatower.event.entity.dto.AlarmSourceSubTypeDTO;
import com.chinatower.event.entity.dto.AlarmTypeTagInfoDTO;
import com.chinatower.event.entity.vo.AlarmReasonVO;
import com.chinatower.event.entity.vo.AlarmSourceSubTypeVO;
import com.chinatower.event.entity.vo.AlarmTypeTagInfoVO;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 告警类型相关对外接口
 */
@FeignClient(contextId = "alarmTypeService", value = ServiceNameConstants.EVENT_SERVICE, fallbackFactory = AlarmTypeFallbackFactory.class)
public interface IAlarmTypeService {

    /**
     * 根据客户和行业编码查询客户权限范围内的告警来源及告警类型信息
     * @apiNote 根据客户和行业编码查询客户权限范围内的告警来源及告警类型信息
     * @apiVersion 1.4
     * @tag 一、告警事件能力
     * @requestDemo
     * {
     *      "tenantId": "121062646",
     *      "industryCode": "000000"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *      alarmSource: 告警来源编码 1-算法AI告警；2-人工上报；3-一键告警；4、摄像机算法告警 ；8-物联设备告警；9-卫星告警；10-雷达告警
     *      alarmSourceName: 告警来源名称
     *      alarmTypeId: 告警类型ID
     *      typeName: 告警类型名称
     *      typeValue: 告警类型值编码
     *      icon: 告警类型图标地址
     * 返回参数示例：
     * {
     *   "code": 200,
     *   "msg": "请求成功",
     *   "time": 1673254838741,
     *   "data": [
     *     {
     *       "alarmSource":"4",
     *       "alarmSourceName":"摄像机类型",
     *       "alarmTypeVOList":[
     *          {
     *            "alarmTypeId":"11",
     *            "typeName":"22",
     *            "typeValue":"33",
     *            "alarmSource":"4",
     *            "alarmSourceName":"摄像机告警",
     *            "icon":"icon-gncheliangchuangrujiankong"
     *            }
     *        ]
     *     },
     *     {
     *        "alarmSource":"2",
     *        "alarmSourceName":"人工上报",
     *        "alarmTypeVOList":[
     *           {
     *             "alarmTypeId":"11",
     *             "typeName":"22",
     *             "typeValue":"33",
     *             "alarmSource":"2",
     *             "alarmSourceName":"人工上报",
     *             "icon":""
     *             }
     *          ]
     *       }
     *    ]
     * }
     */
    @PostMapping("/alarm/type/findAlarmSourceSubType")
    Result<List<AlarmSourceSubTypeVO>> findAlarmSourceSubType(@RequestBody AlarmSourceSubTypeDTO req);

    /**
     * 根据告警声音id查询告警声音信息
     * @apiNote 根据告警声音id查询告警声音信息 详细信息
     * @apiVersion 1.5
     * @tag 一、告警事件能力
     * @requestDemo
     * {
     *   "id":"111"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *      alarmSoundId: 告警声音ID
     *      tenantId: 客户ID
     *      alarmSoundName: 告警声音名称
     *      docName: 文件名称
     *      docUrl: 文件url
     *      playTime: 播放时长
     *      createPerson: 创建人
     *      updatePerson: 修改人
     *      statusCd: 状态 0启用 1停用
     *      createTime: 创建时间
     *      updateTime: 更新时间
     *      industryCode: 行业应用
     *      appVerCode: 应用版本
     *      soundType: 声音类型，0 默认声音，1 其他声音
     * 返回参数示例：
     * {
     *   "code": 200,
     *   "msg": "请求成功",
     *   "time": 1673254838741,
     *   "data": {
     *              "alarmSoundId":"11",
     *              "tenantId":"22",
     *              "alarmSoundName":"22",
     *              "docName":"22",
     *              "docUrl":"33",
     *              "playTime":"44",
     *              "createPerson":"33",
     *              "updatePerson":"22",
     *              "statusCd":"1",
     *              "createTime":"2023-08-09 22:49:03",
     *              "updateTime":"2023-08-09 22:49:03",
     *              "industryCode":"22",
     *              "appVerCode":"1.0",
     *              "soundType":"1"
     *        }
     * }
     */
    @GetMapping("/AlarmSound/selectById/{id}")
    Result<AlarmSoundDO> selectById(@PathVariable(value = "id") Long id);

    /**
     * 根据告警类型标签编码查询告警原因列表
     * @apiNote 根据告警类型标签编码查询告警原因列表
     * @apiVersion 1.6
     * @tag 一、告警事件能力
     * @requestDemo
     * {
     *   "typeValue":"1010100"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *    id: 告警类型ID
     *    reason: 类型名称
     * 返回参数示例：
     * {
     *    "code":200,
     *    "msg":"请求成功",
     *    "time":1697442625640,
     *    "data":[
     *        {
     *           "id":1,
     *           "reason":"告警原因1"
     *        },
     *        {
     *           "id":2,
     *           "reason":"告警原因2"
     *        }
     *    ]
     * }
     */
    @PostMapping("/alarm/reason/list")
    Result<List<AlarmReasonVO>> selectAlarmReasonList(@RequestBody AlarmReasonQueryDTO param);

    /**
     * 根据告警类型编码列表查询客户告警标签关联信息
     * @apiNote 根据告警类型编码列表查询客户告警标签关联信息
     * @apiVersion 1.7
     * @tag 一、告警事件能力
     * @requestDemo
     * {
     *   "typeValueList":["1010100"]
     * }
     * @responseDemo
     * 返回参数data下字段解析:
     *    tagId: 告警类型标签编码
     *    tagName: 告警类型标签名称
     *    typeValue: 告警类型编码
     *    typeName: 告警类型名称
     * 返回参数示例：
     * {
     *   "code": 200,
     *   "msg": "请求成功",
     *   "time": 1673254838741,
     *   "data": [
     *       {
     *           "tagId": 189,
     *           "tagName": "测试标签",
     *           "typeValue": "1010100",
     *           "typeName": "烟火侦测（可见光）告警"
     *       }
     *   ]
     * }
     */
    @PostMapping("/alarm/type/selectRelateTagInfoByAlarmType")
    Result<List<AlarmTypeTagInfoVO>> selectRelateTagInfoByAlarmType(@RequestBody AlarmTypeTagInfoDTO req);

    /**
     * 根据客户信息查询告警类型标签信息
     * @apiNote 根据客户信息查询告警类型标签信息
     * @apiVersion 1.8
     * @tag 一、告警事件能力
     * @responseDemo
     * 返回参数data下字段解析：
     *    tagId: 告警类型标签编码
     *    tagName: 告警类型标签名称
     * 返回参数示例：
     * {
     *    "code": 200,
     *    "msg": "请求成功",
     *    "time": 1673254838741,
     *    "data": [
     *        {
     *           "tagId": 189,
     *           "tagName": "测试标签"
     *        }
     *    ]
     * }
     */
    @GetMapping("/alarm/type/selectTagInfoByTenant")
    Result<List<AlarmTypeTagInfoVO>> selectTagInfoByTenant();

    @ApiOperation(value = "告警类型列表查询", notes = "告警类型列表查询")
    @PostMapping("/alarm/type/findList")
    TableDataInfo findList(@RequestBody AlarmTypeClassDTO dto);
}
