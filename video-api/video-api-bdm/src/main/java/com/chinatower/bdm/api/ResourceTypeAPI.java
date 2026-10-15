package com.chinatower.bdm.api;


import com.chinatower.bdm.api.entity.ResourceTreeSelect;
import com.chinatower.bdm.api.entity.ResourceType;
import com.chinatower.bdm.api.entity.ResourceTypeInt;
import com.chinatower.bdm.api.factory.ResourceTypeFallbackFactory;
import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.List;

@FeignClient(contextId = "ResourceTypeAPI",
        value = ServiceNameConstants.BDM_SERVICE,
        fallbackFactory = ResourceTypeFallbackFactory.class)
public interface ResourceTypeAPI {
    /**
     * 资源类型数据获取
     * @apiNote 资源类型数据获取
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		resourceTypeId：资源类型编码
     * 		resourceTypeName：资源类型名称
     * 		icon：图片地址
     * 		approve：默认状态0 是1 否
     * @apiVersion 13.7
     * @tag 十三、网格能力
     * @requestDemo
     * {
     * 	"longitude": "116.2713031684028",
     * 	"latitude": "39.95382921006944",
     * 	"tenantIdList": [
     * 		"122035807"
     * 	]
     * }
     * @responseDemo
     * code: 状态码，表示请求结果（整型，例如 200）
     * msg: 返回消息，说明请求状态（字符串，例如“请求成功”）
     * time: 时间戳，表示请求的时间（整型，例如 1648458254956）
     * data:
     *      createBy: 创建者（字符串，例如“wangguan1”）
     *      createTime: 创建时间（字符串，例如“2022-03-28T03:07:53.000+00:00”）
     *      updateBy: 更新者（字符串，例如“wangguan1”）
     *      updateTime: 更新时间（字符串，例如“2022-03-28T03:07:53.000+00:00”）
     *      dataScope: 数据范围（字符串或 null）
     *      appVerCode: 应用版本编码（字符串或 null）
     *      tenantId: 租户主键（字符串，例如“122035788”）
     *      userId: 用户主键（字符串或 null）
     *      id: 资源类型主键（整型，例如 17511）
     *      resourceTypeId: 资源类型编码（字符串，例如“202203281107526729”）
     *      resourceTypeName: 资源类型名称（字符串，例如“危险源”）
     *      parentId: 父级主键（字符串，例如“0”）
     *      parentName: 父级名称（字符串或 null）
     *      orderNum: 排序号（整型或 null）
     *      status: 状态（字符串，例如“0”）
     *      oppositeStatus: 反向状态（字符串或 null）
     *      delFlag: 删除标记（整型，例如 0）
     *      tenantName: 租户名称（字符串，例如“wangguan1”）
     *      remark: 备注（字符串或 null）
     *      icon: 图标（字符串或 null）
     *      approve: 审批状态（字符串，例如“1”）
     *      provinceName: 省份名称（字符串，例如“广西壮族自治区”）
     *      cityName: 城市名称（字符串，例如“北海市”）
     *      children: 子级列表（数组，可能为空）
     *      typeLevelUrl: 类型层级链接（字符串或 null）
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1648458254956,
     *     "data": [
     *         {
     *             "createBy": "wangguan1",
     *             "createTime": "2022-03-28T03:07:53.000+00:00",
     *             "updateBy": "wangguan1",
     *             "updateTime": "2022-03-28T03:07:53.000+00:00",
     *             "dataScope": null,
     *             "appVerCode": null,
     *             "tenantId": "122035788",
     *             "userId": null,
     *             "id": 17511,
     *             "resourceTypeId": "202203281107526729",
     *             "resourceTypeName": "危险源",
     *             "parentId": "0",
     *             "parentName": null,
     *             "orderNum": null,
     *             "status": "0",
     *             "oppositeStatus": null,
     *             "delFlag": 0,
     *             "tenantName": "wangguan1",
     *             "remark": null,
     *             "icon": null,
     *             "approve": "1",
     *             "provinceName": "广西壮族自治区",
     *             "cityName": "北海市",
     *             "children": [],
     *             "typeLevelUrl": null
     *         }
     *     ]
     * }
     * @param resourceType
     * @return
     */
    @PostMapping("/resourceType/selectResourceType")
    Result<List<ResourceType>> selectResourceType(@RequestBody ResourceType resourceType);


    /**
     * 资源类型树
     * @apiNote 资源类型树
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		id：id
     * 		pid：父级id
     * 		label：名称
     * 		children：子级
     * @apiVersion 13.8
     * @tag 十三、网格能力
     * @requestDemo
     * {
     * }
     * @responseDemo
     * code: 状态码，表示请求结果（整型，例如 200）
     * msg: 返回消息，说明请求状态（字符串，例如“请求成功”）
     * time: 时间戳，表示请求的时间（整型，例如 1648458745168）
     * data:
     *       label: 资源类型标签（字符串，例如“测试资源类型2”）
     *       id: 主键（字符串，例如“202203251548026601”）
     *       resourceTypeId: 资源类型 ID（字符串，例如“202203251548026601”）
     *       resourceTypeName: 资源类型名称（字符串，例如“测试资源类型2”）
     *       pid: 父级主键（字符串，例如“0”）
     *       dept: 部门（字符串或 null）
     *       children: 子资源类型列表（数组，可能为空或包含以下字段）
     *       label: 子资源类型标签（字符串，例如“测试资源类型2-1”）
     *       id: 子资源类型主键（字符串，例如“202203251548144223”）
     *       resourceTypeId: 子资源类型 ID（字符串，例如“202203251548144223”）
     *       resourceTypeName: 子资源类型名称（字符串，例如“测试资源类型2-1”）
     *       pid: 父级主键（字符串，例如“202203251548026601”）
     *       dept: 部门（字符串或 null）
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1648458745168,
     *     "data": [
     *
     *         {
     *             "label": "测试资源类型2",
     *             "id": "202203251548026601",
     *             "resourceTypeId": "202203251548026601",
     *             "resourceTypeName": "测试资源类型2",
     *             "pid": "0",
     *             "dept": null,
     *             "children": [
     *                 {
     *                     "label": "测试资源类型2-1",
     *                     "id": "202203251548144223",
     *                     "resourceTypeId": "202203251548144223",
     *                     "resourceTypeName": "测试资源类型2-1",
     *                     "pid": "202203251548026601",
     *                     "dept": null
     *                 }
     *             ]
     *         }
     *     ]
     * }
     * @param resourceType
     * @return
     */
    @ApiOperation(value = "资源类型树")
    @GetMapping("/resourceType/selectResourceTypeTree")
    Result<List<ResourceTreeSelect>> selectResourceTypeTree(@RequestBody ResourceType resourceType);

    @ApiOperation(value = "资类型数据初始化")
    @PostMapping("/resourceType/initResourceType")
    Result initResourceType(@RequestBody ResourceTypeInt resourceTypeInt);
}
