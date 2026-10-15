package com.chinatower.device.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.device.api.factory.VideoCheckpointDeviceFallbackFactory;
import com.chinatower.platform.entity.devicemgmt.vo.CheckpointDeviceInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "videoCheckpointDeviceMgmtService", value = ServiceNameConstants.DEVICE_SERVICE, fallbackFactory = VideoCheckpointDeviceFallbackFactory.class)
public interface IVideoCheckpointDeviceMgmtService {

    /**
     * 根据条件分页查询设备信息
     * @apiNote 根据条件分页查询设备信息
     * @apiVersion 3.31
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "deviceName": "",
     *     "deviceCode": "",
     *     "channelName": "",
     *     "channelCode": "",
     *     "deviceStatus": "",
     *     "code": null,
     *     "pageNum": 1,
     *     "pageSize": 10,
     *     "provinceId": "",
     *     "cityId": "",
     *     "countyId": "",
     *     "ids": [
     *
     *     ],
     *     "sortName": "createTime",
     *     "sort": "desc"
     * }
     * @responseDemo
     * creator：创建者
     * createTime：创建时间
     * createBy：创建者ID
     * updateBy：更新者ID
     * modifier：修改者
     * updateTime：更新时间
     * deleteFlag：删除标记
     * tenantId：租户ID
     * userId：用户ID
     * appVerCode：应用版本代码
     * industryCode：行业代码
     * dataScope：数据范围
     * criteriaList：条件列表
     * cpId：控制点ID
     * deviceCode：设备编码
     * deviceName：设备名称
     * deviceStatus：设备状态
     * channelCode：通道编码
     * channelName：通道名称
     * provinceId：省份ID
     * provinceName：省份名称
     * cityId：城市ID
     * cityName：城市名称
     * countyId：区县ID
     * countyName：区县名称
     * tenantName：租户名称
     * ids：ID集合
     * pageNum：页码
     * pageSize：每页条数
     * sort：排序字段
     * sortName：排序字段名称
     * {
     *     "total": 7,
     *     "rows": [
     *         {
     *             "creator": null,
     *             "createTime": "2023-09-05 15:03:08",
     *             "createBy": null,
     *             "updateBy": null,
     *             "modifier": null,
     *             "updateTime": null,
     *             "deleteFlag": null,
     *             "tenantId": null,
     *             "userId": null,
     *             "appVerCode": null,
     *             "industryCode": null,
     *             "dataScope": null,
     *             "criteriaList": null,
     *             "cpId": "327",
     *             "deviceCode": "36011200031327000001",
     *             "deviceName": "梅岭漂流岭",
     *             "deviceStatus": "0",
     *             "channelCode": "36011200031310000001",
     *             "channelName": "梅岭漂流岭_1",
     *             "provinceId": "110000",
     *             "provinceName": "北京市",
     *             "cityId": "110100",
     *             "cityName": "北京市",
     *             "countyId": "110105",
     *             "countyName": "朝阳区",
     *             "tenantName": null,
     *             "ids": null,
     *             "pageNum": null,
     *             "pageSize": null,
     *             "sort": null,
     *             "sortName": null
     *         }
     *     ],
     *     "code": 200
     * }
     * @param checkpointDeviceInfoVO
     * @return
     */
    @PostMapping("/video/device/queryCheckpointDeviceInfoPage")
    TableDataInfo queryCheckpointDeviceInfoPage(@RequestBody CheckpointDeviceInfoVO checkpointDeviceInfoVO);

    /**
     * 新增卡口设备
     * @apiNote 新增卡口设备
     * @apiVersion 3.32
     * @tag 三、设备能力
     * @requestDemo
     * [
     *     "11010800831327000357+11010800831187000111"
     * ]
     * @responseDemo
     * code：状态码
     * msg：返回消息
     * time：时间戳
     * {
     *     "code": 200,
     *     "msg": "新增卡口设备成功!",
     *     "time": 1694682288235
     * }
     * @param channelCodeList
     * @return
     */
    @PostMapping("/video/device/addCheckpointDeviceInfo")
    Result<String> insertCheckpointDeviceInfo(@RequestBody List<String> channelCodeList);

    /**
     * 删除卡口设备
     * @apiNote 删除卡口设备
     * @apiVersion 3.33
     * @tag 三、设备能力
     * @requestDemo
     * [
     *     "328"
     * ]
     * @responseDemo
     * code：状态码
     * msg：返回消息
     * time：时间戳
     * {
     *     "code": 200,
     *     "msg": "删除卡口设备成功!",
     *     "time": 1694682378947
     * }
     * @param ids
     * @return
     */
    @PostMapping("/video/device/removeCheckpointDeviceInfo")
    Result<String> deleteCheckpointDeviceInfoBatch(@RequestBody List<String> ids);

    /**
     * 查询租户设备通道树
     * @apiNote 查询租户设备通道树
     * @apiVersion 3.34
     * @tag 三、设备能力
     * @responseDemo
     *  * code：响应状态码（200表示请求成功）
     *     * msg：响应消息
     *     * time：响应时间（时间戳格式）
     *     * data：响应数据（包含多个层级结构）
     *     * └ id：唯一标识
     *     * └ label：节点名称
     *     * └ pid：父节点ID
     *     * └ dept：节点深度
     *     * └ children：子节点列表
     *     * └ tenantId：租户ID（可为空）
     * {
     *     "code":200,
     *     "msg":"请求成功",
     *     "time":1694682219506,
     *     "data":[
     *         {
     *             "id":"510000",
     *             "label":"四川省",
     *             "pid":"100000",
     *             "dept":"2",
     *             "children":[
     *                 {
     *                     "id":"511500",
     *                     "label":"宜宾市",
     *                     "pid":"510000",
     *                     "dept":"3",
     *                     "children":[
     *                         {
     *                             "id":"511503",
     *                             "label":"南溪区",
     *                             "pid":"511500",
     *                             "dept":"4",
     *                             "children":[
     *                                 {
     *                                     "id":"51150300991327000001",
     *                                     "label":"四川-宜宾-南溪区金鸡村二站",
     *                                     "pid":"511503",
     *                                     "dept":"10",
     *                                     "tenantId":null
     *                                 }
     *                             ],
     *                             "tenantId":null
     *                         }
     *                     ],
     *                     "tenantId":null
     *                 }
     *             ],
     *             "tenantId":null
     *         }
     *     ]
     * }
     */
    @PostMapping(value = "/video/device/getCheckpointDeviceTree")
    Result<List<TreeSelect>> getCheckpointDeviceTree();
}
