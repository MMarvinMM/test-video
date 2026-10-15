package com.chinatower.bdm.api;

import com.chinatower.bdm.api.entity.ResourceConf;
import com.chinatower.bdm.api.factory.ResourceFallbackFactory;
import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.List;

@FeignClient(contextId = "ResourceAPI",
        value = ServiceNameConstants.BDM_SERVICE,
        fallbackFactory = ResourceFallbackFactory.class)
public interface ResourceAPI {

    /**
     * 资源数据查询/资源图层查询
     * @apiNote 资源数据查询/资源图层查询
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		resourceTypeId：资源类型编码
     * 		resourceTypeName：资源类型名称
     * 		icon：图片地址
     * 		approve：默认状态0 是1 否
     * @apiVersion 13.3
     * @tag 十三、网格能力
     * @requestDemo
     * {
     * 	"resourceTypeId": "1111"
     * }
     * @responseDemo
     * code: 状态码，表示请求的处理结果（整型，200表示成功）
     * msg: 返回消息，描述处理结果的信息（字符串，例如“请求成功”）
     * time: 时间戳，表示响应生成的时间（长整型，例如1648517381866）
     * data: [
     *       {
     *       wordsId: 关键字主键（字符串，例如“202203281530252388”）
     *       userId: 用户主键（字符串或null）
     *       typeId: 类型主键（字符串，例如“202203281106361980”）
     *       content: 内容（字符串，例如“物资仓库”）
     *       createDate: 创建日期（字符串，例如“2022-03-28T07:30:25.000+00:00”）
     *       tenantId: 租户主键（字符串，例如“122035788”）
     *       appVerCode: 版本编码（字符串或null）
     *       initWordsFlag: 初始化常用语标识（字符串或null）
     *       updateTime: 修改时间（字符串，例如“2022-03-28T07:30:25.000+00:00”）
     *       industryCode: 行业编码（字符串或null）
     *       }
     *   ]
     *
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1648517381866,
     * 	"data": [
     *                {
     * 			"createBy": "wangguan1",
     * 			"createTime": "2022-03-28T07:30:25.000+00:00",
     * 			"updateBy": "wangguan1",
     * 			"updateTime": "2022-03-28T07:30:25.000+00:00",
     * 			"dataScope": null,
     * 			"appVerCode": null,
     * 			"tenantId": "122035788",
     * 			"userId": null,
     * 			"id": 17492,
     * 			"resourceId": "202203281530252388",
     * 			"resourceName": "物资仓库",
     * 			"resourceTypeId": "202203281106361980",
     * 			"orderNum": null,
     * 			"status": "0",
     * 			"delFlag": 0,
     * 			"tenantName": "wangguan1",
     * 			"remark": null,
     * 			"longitude": "116.136449",
     * 			"latitude": "39.549046",
     * 			"elevation": null,
     * 			"addr": "7",
     * 			"provinceName": "广西壮族自治区",
     * 			"cityName": "北海市",
     * 			"resourceTypeName": "物资仓库",
     * 			"radius": null,
     * 			"icon": null,
     * 			"longitudeParam": null,
     * 			"latitudeParam": null,
     * 			"resourceTypeList": null,
     * 			"distance": null,
     * 			"typeLevelUrl": null,
     * 			"pageNum": 1,
     * 			"pageSize": 10
     *        }
     * 	]
     * }
     * @param resourceConf
     * @return
     */
    @PostMapping("/resource/selectResource")
    Result<List<ResourceConf>> selectResource(@RequestBody ResourceConf resourceConf);

    /**
     * 资源数据查询(分页)
     * @apiNote 资源数据查询(分页)
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		resourceTypeId：资源类型编码
     * 		resourceTypeName：资源类型名称
     * 		resourceId：资源编码
     * 		resourceName：资源名称
     * 		longitude：经度
     * 		latitude：纬度
     * 		elevation：高程(米)
     * 		addr：地址
     * @apiVersion 13.4
     * @tag 十三、网格能力
     * @requestDemo
     * {
     * 	"resourceTypeId": "1111"
     * }
     * @responseDemo
     * code: 状态码，表示请求的处理结果（整型，200表示成功）
     * total: 数据总量，表示符合条件的数据条数（整型，例如5）
     * rows:
     *      createBy: 创建人（字符串，例如“admin”）
     *      createTime: 创建时间（字符串，例如“2022-03-25T17:07:45.000+00:00”）
     *      updateBy: 更新人（字符串，例如“admin”）
     *      updateTime: 更新时间（字符串，例如“2022-03-25T17:07:45.000+00:00”）
     *      dataScope: 数据范围（字符串或null）
     *      appVerCode: 版本编码（字符串或null）
     *      tenantId: 租户主键（字符串，例如“TEN100000”）
     *      userId: 用户主键（字符串或null）
     *      id: 数据主键（整型，例如17483）
     *      resourceId: 资源ID（字符串，例如“6656853376329845682”）
     *      resourceName: 资源名称（字符串，例如“测试资源2-1”）
     *      resourceTypeId: 资源类型ID（字符串，例如“202203251548144223”）
     *      orderNum: 排序号（整型或null）
     *      status: 状态（字符串，例如“0”）
     *      delFlag: 删除标识（整型，例如0）
     *      tenantName: 租户名称（字符串，例如“北京林草局”）
     *      remark: 备注信息（字符串，例如“1”）
     *      longitude: 经度（字符串，例如“119.565656”）
     *      latitude: 纬度（字符串，例如“39.565656”）
     *      elevation: 海拔高度（字符串或null）
     *      addr: 地址（字符串，例如“1111111”）
     *      provinceName: 省份名称（字符串，例如“北京市”）
     *      cityName: 城市名称（字符串，例如“东城区”）
     *      resourceTypeName: 资源类型名称（字符串，例如“测试资源类型2-1”）
     *      radius: 半径（字符串或null）
     *      icon: 图标信息（字符串或null）
     *      longitudeParam: 经度参数（字符串或null）
     *      latitudeParam: 纬度参数（字符串或null）
     *      resourceTypeList: 资源类型列表（字符串或null）
     *      distance: 距离（字符串或null）
     *      typeLevelUrl: 类型层级路径（字符串或null）
     *      pageNum: 当前页码（整型，例如1）
     *      pageSize: 每页大小（整型，例如10）
     *
     * {
     * 	"total": 5,
     * 	"rows": [
     *                {
     * 			"createBy": "admin",
     * 			"createTime": "2022-03-25T17:07:45.000+00:00",
     * 			"updateBy": "admin",
     * 			"updateTime": "2022-03-25T17:07:45.000+00:00",
     * 			"dataScope": null,
     * 			"appVerCode": null,
     * 			"tenantId": "TEN100000",
     * 			"userId": null,
     * 			"id": 17483,
     * 			"resourceId": "6656853376329845682",
     * 			"resourceName": "测试资源2-1",
     * 			"resourceTypeId": "202203251548144223",
     * 			"orderNum": null,
     * 			"status": "0",
     * 			"delFlag": 0,
     * 			"tenantName": "北京林草局",
     * 			"remark": "1",
     * 			"longitude": "119.565656",
     * 			"latitude": "39.565656",
     * 			"elevation": "1",
     * 			"addr": "1111111",
     * 			"provinceName": "北京市",
     * 			"cityName": "东城区",
     * 			"resourceTypeName": "测试资源类型2-1",
     * 			"radius": null,
     * 			"icon": null,
     * 			"longitudeParam": null,
     * 			"latitudeParam": null,
     * 			"resourceTypeList": null,
     * 			"distance": null,
     * 			"typeLevelUrl": null,
     * 			"pageNum": 1,
     * 			"pageSize": 10
     *        }
     * 	],
     * 	"code": 200
     * }
     * @param resourceConf
     * @return
     */
    @PostMapping("/resource/selectResourceConfList")
     TableDataInfo selectResourceConfList(@RequestBody ResourceConf resourceConf);

    /**
     * 资源单条数据查询
     * @apiNote 资源单条数据查询
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		resourceTypeId：资源类型编码
     * 		resourceTypeName：资源类型名称
     * 		resourceId：资源编码
     * 		resourceName：资源名称
     * 		longitude：经度
     * 		latitude：纬度
     * 		elevation：高程(米)
     * 		addr：地址
     * 		provinceId：省份编码
     * 		cityId：地市编码
     * 		countyId：区县编码
     * 		gridIds：所属网格id
     * 		gridNames：所属网格名称
     * @apiVersion 13.5
     * @tag 十三、网格能力
     * @requestDemo
     * {
     * 	"resourceId": "1111"
     * }
     * @responseDemo
     * code: 状态码，表示请求的处理结果（整型，例如200表示成功）
     * msg: 返回消息（字符串，例如“请求成功”）
     * time: 时间戳，表示响应生成的时间（长整型，例如1648459469221）
     * data:
     *   createBy: 创建人（字符串，例如“admin”）
     *   createTime: 创建时间（字符串，例如“2022-03-03T16:39:46.000+00:00”）
     *   updateBy: 更新人（字符串，例如“admin”）
     *   updateTime: 更新时间（字符串，例如“2022-03-03T16:39:46.000+00:00”）
     *   dataScope: 数据范围（字符串或null）
     *   appVerCode: 版本编码（字符串或null）
     *   tenantId: 租户主键（字符串或null）
     *   userId: 用户主键（字符串或null）
     *   id: 数据主键（整型，例如17428）
     *   resourceId: 资源ID（字符串，例如“d919c7930dd44728bc9ac197bf913082”）
     *   resourceName: 资源名称（字符串，例如“资源名称二号2”）
     *   resourceTypeId: 资源类型ID（字符串，例如“b5041092a99a4a0e880e3c1c298bdb63”）
     *   orderNum: 排序号（整型或null）
     *   status: 状态（字符串，例如“0”）
     *   delFlag: 删除标识（整型，例如0）
     *   tenantName: 租户名称（字符串或null）
     *   remark: 备注信息（字符串，例如“1”）
     *   longitude: 经度（字符串，例如“116.234240”）
     *   latitude: 纬度（字符串，例如“39.539591”）
     *   elevation: 海拔高度（字符串或null）
     *   addr: 地址（字符串，例如“1”）
     *   provinceName: 省份名称（字符串或null）
     *   cityName: 城市名称（字符串或null）
     *   resourceTypeName: 资源类型名称（字符串，例如“资源类型名称二号”）
     *   radius: 半径（字符串或null）
     *   icon: 图标信息（字符串或null）
     *   longitudeParam: 经度参数（字符串或null）
     *   latitudeParam: 纬度参数（字符串或null）
     *   resourceTypeList: 资源类型列表（字符串或null）
     *   distance: 距离（字符串或null）
     *   typeLevelUrl: 类型层级路径（字符串或null）
     *   pageNum: 当前页码（整型，例如1）
     *   pageSize: 每页大小（整型，例如10）
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1648459469221,
     *     "data": {
     *         "createBy": "admin",
     *         "createTime": "2022-03-03T16:39:46.000+00:00",
     *         "updateBy": "admin",
     *         "updateTime": "2022-03-03T16:39:46.000+00:00",
     *         "dataScope": null,
     *         "appVerCode": null,
     *         "tenantId": null,
     *         "userId": null,
     *         "id": 17428,
     *         "resourceId": "d919c7930dd44728bc9ac197bf913082",
     *         "resourceName": "资源名称二号2",
     *         "resourceTypeId": "b5041092a99a4a0e880e3c1c298bdb63",
     *         "orderNum": null,
     *         "status": "0",
     *         "delFlag": 0,
     *         "tenantName": null,
     *         "remark": "1",
     *         "longitude": "116.234240",
     *         "latitude": "39.539591",
     *         "elevation": null,
     *         "addr": "1",
     *         "provinceName": null,
     *         "cityName": null,
     *         "resourceTypeName": "资源类型名称二号",
     *         "radius": null,
     *         "icon": null,
     *         "longitudeParam": null,
     *         "latitudeParam": null,
     *         "resourceTypeList": null,
     *         "distance": null,
     *         "typeLevelUrl": null,
     *         "pageNum": 1,
     *         "pageSize": 10
     *     }
     * }
     * @param resourceConf
     * @return
     */
    @PostMapping(value = "/resource/getResourceInfoDetail")
     Result<ResourceConf> getResourceInfoDetail(@RequestBody ResourceConf resourceConf);

    /**
     * 资源树查询
     * @apiNote 资源树查询
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		id：id
     * 		pid：父级id
     * 		label：名称
     * 		type：0 资源类型 1 资源
     * 		icon：图片地址
     * 		children：子级
     * @apiVersion 13.6
     * @tag 十三、网格能力
     * @requestDemo
     * {
     * }
     * @responseDemo
     * msg: 操作成功，表示请求的处理结果（字符串，例如“操作成功”）
     * code: 状态码，表示请求的处理结果（整型，例如200）
     * data:
     *      id: 主键ID（字符串，例如“a63f6eff4dc3495b8ef00df34a737edc”）
     *      label: 标签名称（字符串，例如“测试资源类型1”）
     *      pid: 父级ID（字符串，例如“0”）
     *      dept: 部门标识（字符串，例如“0”）
     *      children: 子级列表（数组，包含以下字段）
     *      id: 主键ID（字符串，例如“f20c441164a14105991a716bddbc4041”）
     *      label: 标签名称（字符串，例如“测试类型1-2”）
     *      pid: 父级ID（字符串，例如“a63f6eff4dc3495b8ef00df34a737edc”）
     *      dept: 部门标识（字符串，例如“0”）
     *      children: 子级列表（数组，包含以下字段）
     *              id: 主键ID（字符串，例如“7d74252751be4ac489891c4eafac6ad7”）
     *              label: 标签名称（字符串，例如“测试资源”）
     *              pid: 父级ID（字符串，例如“f20c441164a14105991a716bddbc4041”）
     *              dept: 部门标识（字符串，例如“1”）
     *              tenantId: 租户主键（字符串或null）
     *              tenantId: 租户主键（字符串或null）
     *      tenantId: 租户主键（字符串或null）
     *
     * {
     *     "msg": "操作成功",
     *     "code": 200,
     *     "data": [
     *         {
     *             "id": "a63f6eff4dc3495b8ef00df34a737edc",
     *             "label": "测试资源类型1",
     *             "pid": "0",
     *             "dept": "0",
     *             "children": [
     *                 {
     *                     "id": "f20c441164a14105991a716bddbc4041",
     *                     "label": "测试类型1-2",
     *                     "pid": "a63f6eff4dc3495b8ef00df34a737edc",
     *                     "dept": "0",
     *                     "children": [
     *                         {
     *                             "id": "7d74252751be4ac489891c4eafac6ad7",
     *                             "label": "测试资源",
     *                             "pid": "f20c441164a14105991a716bddbc4041",
     *                             "dept": "1",
     *                             "tenantId": null
     *                         }
     *                     ],
     *                     "tenantId": null
     *                 }
     *             ],
     *             "tenantId": null
     *         }
     *     ]
     * }
     * @param vo
     * @return
     */
    @PostMapping(value = "/resource/getResourceTree")
    AjaxResult getResourceTree(@RequestBody CommonFeignEntity vo);
}
