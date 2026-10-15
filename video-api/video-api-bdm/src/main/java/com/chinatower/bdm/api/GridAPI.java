package com.chinatower.bdm.api;

import com.chinatower.bdm.api.entity.*;
import com.chinatower.bdm.api.factory.GridFallbackFactory;
import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "GridAPI",
        value = ServiceNameConstants.BDM_SERVICE,
        fallbackFactory = GridFallbackFactory.class)
public interface GridAPI {
    /**
     * 根据经纬度获取网格
     * @apiNote 根据经纬度获取网格
     * 返回参数解析：
     * 		gridName：网格名称
     * 		userList：用户列表
     * 		tenantId：租户id
     * 		gridId：网格id
     * 		gridLevel：网格层级
     * 		industryCode：行业编码
     * @apiVersion 13.44
     * @tag 十三、网格能力
     * @requestDemo
     * {
     * 	"tenantIdList": [
     * 		"123028344"
     * 	],
     * 	"latitude": "26.042072591",
     * 	"userId": "0de92a689b9f4640bf7162209756163c",
     * 	"appVerCode": "1.0",
     * 	"industryCode": "500000",
     * 	"tenantId": "123028344",
     * 	"longitude": "106.612722103"
     * }
     * @responseDemo
     * gridName: 网格名称（字符串，例如“黄祖宽-耕地智保版本-1.0”）
     * userList: 用户 ID 列表（数组，包含多个用户 ID）
     * tenantId: 租户 ID（字符串，例如“700015365”）
     * gridId: 网格 ID（字符串，例如“202211302039297995”）
     * gridLevel: 网格级别（字符串，例如“1”）
     * industryCode: 行业代码（字符串，例如“500000”）
     *
     * [
     *        {
     * 		"gridName": "二级网格",
     * 		"userList": [
     * 			"50dbed8ad48541efa7dfb3aedd0b0e26",
     * 			"5159d97aad654c2fa369ecdb13505f9a",
     * 			"172ae66577494e6aafb2c09cdd3a2ea2"
     * 		],
     * 		"tenantId": "700015365",
     * 		"gridId": "202302271427363790",
     * 		"gridLevel": "2",
     * 		"industryCode": "500000"
     *    },
     *    {
     * 		"gridName": "黄祖宽-耕地智保版本-1.0",
     * 		"userList": [
     * 			"ef689e400d494618ba23f99a84c7711e",
     * 			"0e37451dff194dfe9d6a43a64fa192d1",
     * 			"dfb489ff75964e36bd40e2c32fb7b14c",
     * 			"1783ebfb1c174270ba29655a8fc7d88e",
     * 			"5159d97aad654c2fa369ecdb13505f9a",
     * 			"5a21b9ef1acf49f49ad0a0af58757740"
     * 		],
     * 		"tenantId": "700015365",
     * 		"gridId": "202211302039297995",
     * 		"gridLevel": "1",
     * 		"industryCode": "500000"
     *    }
     * ]
     * @param param
     * @return
     */
    @PostMapping("/grid/getMessageByLngLat")
    Result<List<GridByLngLat>> getMessageByLngLat(@RequestBody GridByLngLatParam param);

    /**
     * 根据经纬度获取网格 for 微创告警调用
     * @apiNote 根据经纬度获取网格 for 微创告警调用
     * 返回参数解析：
     * 		gridName：网格名称
     * 		userList：用户列表
     * 		tenantId：租户id
     * 		gridId：网格id
     * 		gridLevel：网格层级
     * 		industryCode：行业编码
     * @apiVersion 13.45
     * @tag 十三、网格能力
     * @requestDemo
     * {
     * 	"tenantIdList": [
     * 		"123028344"
     * 	],
     * 	"latitude": "26.042072591",
     * 	"userId": "0de92a689b9f4640bf7162209756163c",
     * 	"appVerCode": "1.0",
     * 	"industryCode": "500000",
     * 	"tenantId": "123028344",
     * 	"longitude": "106.612722103"
     * }
     * @responseDemo
     * gridName: 网格名称（字符串，例如“二级网格”）
     * userList: 用户 ID 列表（数组，包含多个用户 ID）
     * tenantId: 租户 ID（字符串，例如“700015365”）
     * gridId: 网格 ID（字符串，例如“202302271427363790”）
     * gridLevel: 网格级别（字符串，例如“2”）
     * industryCode: 行业代码（字符串，例如“500000”）
     *
     * [
     *        {
     * 		"gridName": "二级网格",
     * 		"userList": [
     * 			"50dbed8ad48541efa7dfb3aedd0b0e26",
     * 			"5159d97aad654c2fa369ecdb13505f9a",
     * 			"172ae66577494e6aafb2c09cdd3a2ea2"
     * 		],
     * 		"tenantId": "700015365",
     * 		"gridId": "202302271427363790",
     * 		"gridLevel": "2",
     * 		"industryCode": "500000"
     *    },
     *    {
     * 		"gridName": "黄祖宽-耕地智保版本-1.0",
     * 		"userList": [
     * 			"ef689e400d494618ba23f99a84c7711e",
     * 			"0e37451dff194dfe9d6a43a64fa192d1",
     * 			"dfb489ff75964e36bd40e2c32fb7b14c",
     * 			"1783ebfb1c174270ba29655a8fc7d88e",
     * 			"5159d97aad654c2fa369ecdb13505f9a",
     * 			"5a21b9ef1acf49f49ad0a0af58757740"
     * 		],
     * 		"tenantId": "700015365",
     * 		"gridId": "202211302039297995",
     * 		"gridLevel": "1",
     * 		"industryCode": "500000"
     *    }
     * ]
     * @param param
     * @return
     */
    @PostMapping("/grid/getMessageByLngLatNew")
    Result<List<GridByLngLat>> getMessageByLngLatNew(@RequestBody GridByLngLatParam param);

    /**
     * 网格树形结构数据
     * @apiNote 网格树形结构数据
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		id：id
     * 		pid：父级id
     * 		label：名称
     * 	    children：子级
     * @apiVersion 13.46
     * @tag 十三、网格能力
     * @requestDemo
     * {}
     * @responseDemo
     * msg: 返回消息（字符串，例如“操作成功”）
     * code: 返回的状态码（整数，例如 200）
     * data: 数据列表（数组，包含多个网格信息对象）
     *      id: 网格 ID（字符串，例如“202203041640461146”）
     *      label: 网格名称（字符串，例如“一级网格”）
     *      pid: 父级 ID（字符串，例如“0”）
     *      dept: 部门 ID（字符串，例如“0”）
     *      children: 子网格列表（数组，包含多个子网格对象，可能为空）
     *          id: 子网格 ID（字符串，例如“202203041807385082”）
     *          label: 子网格名称（字符串，例如“二级网格1”）
     *          pid: 父级网格 ID（字符串，例如“202203041640461146”）
     *          dept: 部门 ID（字符串，例如“1”）
     *          tenantId: 租户 ID（字符串，可能为空）
     *          children: 子网格的子网格列表（数组，可能为空）
     *
     * {
     * 	"msg": "操作成功",
     * 	"code": 200,
     * 	"data": [
     *                {
     * 			"id": "202203041640461146",
     * 			"label": "一级网格",
     * 			"pid": "0",
     * 			"dept": "0",
     * 			"children": [
     *                {
     * 					"id": "202203041807385082",
     * 					"label": "二级网格1",
     * 					"pid": "202203041640461146",
     * 					"dept": "1",
     * 					"tenantId": null
     *                },
     *                {
     * 					"id": "202203061452463159",
     * 					"label": "二级网格2",
     * 					"pid": "202203041640461146",
     * 					"dept": "0",
     * 					"children": [
     *                        {
     * 							"id": "202203151541514102",
     * 							"label": "二级网格2-1",
     * 							"pid": "202203061452463159",
     * 							"dept": "0",
     * 							"children": [
     *                                {
     * 									"id": "202203151543007268",
     * 									"label": "二级网格3-2",
     * 									"pid": "202203151541514102",
     * 									"dept": "0",
     * 									"tenantId": null
     *                                },
     *                                {
     * 									"id": "202203151542131345",
     * 									"label": "二级网格3-1",
     * 									"pid": "202203151541514102",
     * 									"dept": "0",
     * 									"tenantId": null
     *                                }
     * 							],
     * 							"tenantId": null
     *                        }
     * 					],
     * 					"tenantId": null
     *                }
     * 			],
     * 			"tenantId": null
     *        }
     * 	]
     * }
     * @param vo
     * @return
     */
    @PostMapping(value = "/grid/getGridTree")
    AjaxResult getGridTree(@RequestBody CommonFeignEntity vo);

    /**
     * 网格单条数据查询
     * @apiNote 网格单条数据查询
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		gridId：网格id
     * 		gridCode：网格编码
     * 		gridName：网格名称
     * 	    gridName：网格名称
     *      parentId：父级网格id
     *      parentCode：父级网格编码
     *      gridLevel ：网格层级
     * 		pointStatus：标绘状态 1 已标绘
     * 	    gridRange：网格坐标范围
     *      color：网格颜色
     *      gridCenter：网格中心坐标
     *      gridEmCenter：所属组织编码
     * 		gridEmCenterName：所属组织
     * 	    todayAlarmNum：今日告警数
     *      keeperCount：网格员数量
     *      gridKeeperlist 网格员列表
     *      keeperPhoneNumber 网格员电话(父级节点 gridKeeperlist)
     *      keeperName 网格员名称(父级节点 gridKeeperlist)
     * @apiVersion 13.48
     * @tag 十三、网格能力
     * @requestDemo
     * {
     * 	"gridId": 202209011726583118
     * }
     * @responseDemo
     * code: 状态码（如 200 表示成功）
     * msg: 返回消息（例如“请求成功”）
     * time: 时间戳（毫秒）
     * data: 包含网格详情的对象
     *      creator: 创建者用户名（如 "junnan01"）
     *      createTime: 创建时间（如 "2022-09-01 17:26:58"）
     *      modifier: 修改者的唯一标识（如 "b1921ab47c25491da9e45d6a7ecaffda"）
     *      updateTime: 修改时间（如 "2023-08-15 15:35:42"）
     *      deleteFlag: 删除标志（0 表示未删除，1 表示已删除）
     *      tenantId: 租户 ID（如 "122035807"）
     *      userId: 用户 ID（可能为空）
     *      industryCode: 行业代码（如 "000000"）
     *      gridId: 网格 ID（如 "202209011726583118"）
     *      gridName: 网格名称（如 "测试新增客0323-通用平台版本-1.0"）
     *      gridCode: 网格代码（如 "202209011726583118"）
     *      parentId: 父网格 ID（如 "0"）
     *      gridLevel: 网格级别（如 "1"）
     *      gridLevelCode: 网格级别代码（如 "101,202209011726583118"）
     *      gridAddress: 网格地址（如 "辽宁省大连市高新区"）
     *      gridIntroduce: 网格简介（如 "辽宁省大连市高新区"）
     *      gridPicture: 网格图片 URL（如 "https://video-platform.obs.cn-north-4.myhuaweicloud.com/..."）
     *      reginScopeDesc: 区域描述（如 "辽宁省大连市高新区"）
     *      gridSort: 网格排序（如 1）
     *      pointStatus: 网格点状态（如 "1"）
     *      gridBeginTime: 网格开始时间（如 "2023-06-01"）
     *      gridEndTime: 网格结束时间（如 "2023-06-30"）
     *      gridArea: 网格面积（如 "500"）
     *      color: 网格颜色（如 "#00b0ff,#00b0ff,#00b0ff,#00b0ff"）
     *      gridCenter: 网格中心坐标（例如：[[116.349189,39.762163],[121.512234,38.846254],...]）
     *      gridRange: 网格范围（包含多个坐标点）
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1692254701025,
     * 	"data": {
     * 		"creator": "junnan01",
     * 		"createTime": "2022-09-01 17:26:58",
     * 		"createBy": null,
     * 		"updateBy": null,
     * 		"modifier": "b1921ab47c25491da9e45d6a7ecaffda",
     * 		"updateTime": "2023-08-15 15:35:42",
     * 		"deleteFlag": 0,
     * 		"tenantId": "122035807",
     * 		"userId": null,
     * 		"appVerCode": null,
     * 		"industryCode": "000000",
     * 		"dataScope": null,
     * 		"criteriaList": null,
     * 		"gridId": "202209011726583118",
     * 		"tenantName": null,
     * 		"industryName": null,
     * 		"gridCode": "202209011726583118",
     * 		"gridName": "测试新增客0323-通用平台版本-1.0",
     * 		"gbCode": null,
     * 		"parentId": "0",
     * 		"parentName": null,
     * 		"parentCode": "0",
     * 		"gridClassification": null,
     * 		"gridLevel": "1",
     * 		"gridLevelCode": "101,202209011726583118",
     * 		"gridProperty": null,
     * 		"isEnd": null,
     * 		"gridAddress": "辽宁省大连市高新区",
     * 		"gridIntroduce": "辽宁省大连市高新区",
     * 		"gridPicture": "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/fb9b874a9ab7a611f9dae400dacabc6e/5223dd9c9287b0c8d728301df09b2e27/9d0ea5f7fdc54a8e97b3347c665143f1.jpeg?AccessKeyId=FIFYCLGVA3XZR4XRFLII&Expires=1692255299&Signature=6rnigKQpg3HB6PCF39dWOV3fdqo%3D",
     * 		"reginScopeDesc": "辽宁省大连市高新区",
     * 		"gridSort": 1,
     * 		"pointStatus": "1",
     * 		"gridEmCenter": null,
     * 		"gridEmCenterName": null,
     * 		"gridBeginTime": "2023-06-01",
     * 		"gridEndTime": "2023-06-30",
     * 		"gridArea": "500",
     * 		"isValid": null,
     * 		"pageNum": 1,
     * 		"pageSize": 10,
     * 		"color": "#00b0ff,#00b0ff,#00b0ff,#00b0ff",
     * 		"gridCenter": "[[116.349189,39.762163],[121.512234,38.846254],[121.492773,40.260205],[110.741397,32.95521]]",
     * 		"gridRange": "[[[115.50936,40.416857],[117.314643,40.438108],[117.556588,39.629441],[116.500404,39.086217],[115.14179,39.244944]],[[121.510587,38.848115],[121.511669,38.848102],[121.512509,38.848102],[121.51309,38.848039],[121.513688,38.848014],[121.514156,38.847876],[121.514722,38.847637],[121.515012,38.847247],[121.515141,38.846756],[121.515141,38.846329],[121.514867,38.845851],[121.514867,38.845549],[121.514867,38.845121],[121.514867,38.84492],[121.514269,38.844694],[121.513688,38.844631],[121.512864,38.844492],[121.512057,38.844417],[121.511314,38.844392],[121.51083,38.844392],[121.510184,38.844794],[121.509861,38.845197],[121.50957,38.84575],[121.509344,38.846454],[121.509328,38.846945],[121.509619,38.847385],[121.509893,38.847737],[121.509861,38.847699],[121.509909,38.847712],[121.510232,38.847926],[121.510216,38.847938],[121.510216,38.847964]],[[112.668352,39.488348],[120.222838,43.972921],[124.586205,36.808641],[130.317194,36.547489],[129.817904,38.35683]],[[108.653995,35.369163],[106.54462,30.541256],[114.938174,31.595193]]]",
     * 		"tenantList": null,
     * 		"gridIdList": null,
     * 		"excludeGridId": 0,
     * 		"keeperName": "崔艳",
     * 		"keeperId": "202308030849536142",
     * 		"keeperPhoneNumber": "186****5081",
     * 		"provinceCode": "110000",
     * 		"cityCode": "110100",
     * 		"provinceName": null,
     * 		"cityName": null,
     * 		"topTreeName": null,
     * 		"keeperPhotoUrl": "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/2.0/ec7c3b0bc5f9b10ff3b4839d88332a78/b8fa99eb4862053891457aae4c1915d2/20ecba45f3d540f5b3f5068a6e0a47f9.jpg?AccessKeyId=FIFYCLGVA3XZR4XRFLII&Expires=1692255300&Signature=nTMsITEUcJITpDNxUSR2aANSJTg%3D",
     * 		"isLine": "0",
     * 		"phonePlainText": "18604085081",
     * 		"todayAlarmNum": "0",
     * 		"keeperCount": 5
     *        }
     * }
     * @param param
     * @return
     */
    @PostMapping("/grid/findOneSdk")
    Result<GridVO> findGridInfo(@RequestBody GridInfoParam param);

    /**
     * 网格员单条数据查询
     * @apiNote 网格员单条数据查询
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		keeperId：网格员id
     * 		keeperCode：网格员编码
     * 		loginName：登录账号
     * 	    userId：用户id
     *      keeperIdCardNumber：身份证号
     *      keeperPhoneNumber：电话号
     *      keeperEmail ：邮箱
     * 		longitude：经度
     * 	    latitude：纬度
     *      isLine：是否在线
     *      address：地址
     *      provinceId：省份编码
     * 		cityId：地市编码
     * 	    countyId：区县编码
     *      gridIds：所属网格id
     *      gridNames：所属网格名称
     * @apiVersion 13.49
     * @tag 十三、网格能力
     * @requestDemo
     * {
     * 	"keeperId": "202208231656167331"
     * }
     * @responseDemo
     * code: 状态码（如 200 表示成功）
     * msg: 返回消息（例如“请求成功”）
     * time: 时间戳（毫秒）
     * data: 包含网格员详情的对象
     *      creator: 创建者的唯一标识（如 "019aca627cb74223a95dbec3d964e397"）
     *      createTime: 创建时间（如 "2022-08-23 16:56:16"）
     *      updateTime: 最后更新时间（如 "2022-08-24 21:48:21"）
     *      deleteFlag: 删除标志（0 表示未删除，1 表示已删除）
     *      tenantId: 租户 ID（如 "122035797"）
     *      userId: 用户 ID（如 "bc4e69134200443caf76592263aa0c12"）
     *      appVerCode: 应用版本号（如 "1.0"）
     *      industryCode: 行业代码（如 "100000"）
     *      keeperId: 网格员 ID（如 "202208231656167331"）
     *      aesPhoneNumber: 加密后的电话号码（如 "ec51915a23eb2118b73d3f2315fd25a0"）
     *      keeperCode: 网格员编码（如 "202208231656167331"）
     *      staffId: 员工 ID（如 "bc4e69134200443caf76592263aa0c12"）
     *      staffOrgId: 员工所属组织 ID（如 "122035797-01"）
     *      loginName: 登录用户名（如 "cswgy082301"）
     *      entryTime: 入职时间（如 "2022-08-23 00:00:00"）
     *      wechatNumber: 微信号（如果有的话，当前为 null）
     *      party: 政党（如果有的话，当前为 null）
     *      education: 教育背景（如果有的话，当前为 null）
     *      isSpecial: 是否特殊人员（当前为 null）
     *      specialType: 特殊类型（当前为 null）
     *      majorType: 专业类型（当前为 null）
     *      keeperAidentity: 网格员身份证明（当前为 null）
     *      keeperMotto: 网格员格言（当前为 null）
     *      personalProfile: 个人简介（当前为 null）
     *      isGridManager: 是否为网格管理员（"0" 表示否）
     *      versions: 版本号（如 1）
     *      keeperIdCardNumber: 网格员身份证号码（如 "110111********0001"）
     *      keeperBirthDate: 网格员出生日期（当前为 null）
     *      keeperAddress: 网格员住址（如 "北京市海淀区四季青镇四季青路永泰郦城果岭"）
     *      keeperPhoneNumber: 网格员电话（如 "133****3233"）
     *      keeperEmail: 网格员电子邮件（当前为 null）
     *      keeperGender: 性别（"0" 表示女性）
     *      keeperName: 网格员姓名（如 "测试网格员0823-01"）
     *      longitude: 网格员位置的经度（如 "116.2713292100694"）
     *      latitude: 网格员位置的纬度（如 "39.95393744574653"）
     *      takePointTime: 点位时间（当前为 null）
     *      fjUrl: 附件 URL（当前为 null）
     *      isLine: 线路标志（"0" 表示否）
     *      provinceId: 省份 ID（如 "110000"）
     *      cityId: 城市 ID（如 "110100"）
     *      countyId: 区县 ID（如 "110108"）
     *      locationFlag: 位置标志（"0" 表示未标记）
     *      keeperType: 网格员类型（如 "1"）
     *      operatingDuty: 操作职责（当前为 null）
     *      staffOrgName: 员工所在组织名称（如 "测试"）
     *      gridIds: 网格 ID 列表（如 ["202208231610068821"]）
     *      gridNames: 网格名称列表（如 ["网格测试0823-001"]）
     *      gridNameStr: 网格名称字符串（如 "网格测试0823-001"）
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1692255879752,
     * 	"data": {
     * 		"creator": "019aca627cb74223a95dbec3d964e397",
     * 		"createTime": "2022-08-23 16:56:16",
     * 		"createBy": null,
     * 		"updateBy": null,
     * 		"modifier": null,
     * 		"updateTime": "2022-08-24 21:48:21",
     * 		"deleteFlag": 0,
     * 		"tenantId": "122035797",
     * 		"userId": "bc4e69134200443caf76592263aa0c12",
     * 		"appVerCode": "1.0",
     * 		"industryCode": "100000",
     * 		"dataScope": null,
     * 		"criteriaList": null,
     * 		"keeperId": "202208231656167331",
     * 		"gridType": null,
     * 		"aesPhoneNumber": "ec51915a23eb2118b73d3f2315fd25a0",
     * 		"keeperCode": "202208231656167331",
     * 		"staffId": "bc4e69134200443caf76592263aa0c12",
     * 		"staffOrgId": "122035797-01",
     * 		"loginName": "cswgy082301",
     * 		"entryTime": "2022-08-23 00:00:00",
     * 		"wechatNumber": null,
     * 		"party": null,
     * 		"education": null,
     * 		"isSpecial": null,
     * 		"specialType": null,
     * 		"majorType": null,
     * 		"keeperAidentity": null,
     * 		"keeperMotto": null,
     * 		"personalProfile": null,
     * 		"isGridManager": "0",
     * 		"keeperSort": null,
     * 		"starClass": null,
     * 		"versions": 1,
     * 		"keeperIdCardNumber": "110111********0001",
     * 		"keeperBirthDate": null,
     * 		"keeperAddress": "北京市海淀区四季青镇四季青路永泰郦城果岭",
     * 		"keeperPhoneNumber": "133****3233",
     * 		"keeperEmail": null,
     * 		"keeperGender": "0",
     * 		"keeperName": "测试网格员0823-01",
     * 		"longitude": "116.2713292100694",
     * 		"latitude": "39.95393744574653",
     * 		"takePointTime": null,
     * 		"fjUrl": null,
     * 		"isLine": "0",
     * 		"provinceId": "110000",
     * 		"cityId": "110100",
     * 		"countyId": "110108",
     * 		"locationFlag": "0",
     * 		"keeperType": "1",
     * 		"operatingDuty": null,
     * 		"staffOrgName": "测试",
     * 		"gridIds": [
     * 			"202208231610068821"
     * 		],
     * 		"gridNames": [
     * 			"网格测试0823-001"
     * 		],
     * 		"status": null,
     * 		"distance": null,
     * 		"gridNameStr": "网格测试0823-001",
     * 		"tenantName": null,
     * 		"gridIdList": null
     *        }
     * }
     * @param param
     * @return
     */
    @PostMapping("/gridKeeper/findOneSdk")
    Result<GridKeeperVO> findGridKeeperInfo(@RequestBody GridKeeperInfoParam param);


    /**
     * 根据租户查询网格员列表sdk
     * @apiNote 根据租户查询网格员列表sdk
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		keeperId：网格员id
     * 		keeperCode：网格员编码
     * 		loginName：登录账号
     * 	    userId：用户id
     *      keeperIdCardNumber：身份证号
     *      keeperPhoneNumber：电话号
     *      keeperEmail ：邮箱
     * 		longitude：经度
     * 	    latitude：纬度
     *      isLine：是否在线
     *      address：地址
     *      provinceId：省份编码
     * 		cityId：地市编码
     * 	    countyId：区县编码
     *      gridId：所属网格id
     *      gridName：所属网格名称
     * @apiVersion 13.50
     * @tag 十三、网格能力
     * @requestDemo
     * {}
     * @responseDemo
     * code: 状态码（如 200 表示成功）
     * msg: 返回消息（如“请求成功”）
     * time: 时间戳（毫秒）
     * data: 包含网格员信息的列表
     *      creator: 创建者的唯一标识（如 "47610796b99b4523981a351f0e401044"）
     *      createTime: 创建时间（如 "2023-01-12 14:58:55"）
     *      updateTime: 最后更新时间（如 "2023-07-28 15:21:02"）
     *      modifier: 修改者的唯一标识（如 "47610796b99b4523981a351f0e401044"）
     *      deleteFlag: 删除标志（0 表示未删除，1 表示已删除）
     *      tenantId: 租户 ID（如 "122035807"）
     *      userId: 用户 ID（如 "d9eee890ce2a4daa9ff94bd46a60fc2b"）
     *      appVerCode: 应用版本号（如 "1.0"）
     *      industryCode: 行业代码（如 "000000"）
     *      keeperId: 网格员 ID（如 "202301121458557799"）
     *      gridType: 网格类型（当前为 null）
     *      aesPhoneNumber: 加密后的电话号码（当前为 null）
     *      keeperCode: 网格员编码（如 "202301121458557799"）
     *      staffId: 员工 ID（如 "d9eee890ce2a4daa9ff94bd46a60fc2b"）
     *      staffOrgId: 员工所属组织 ID（如 "122035807-01"）
     *      loginName: 登录用户名（如 "liujl01"）
     *      entryTime: 入职时间（如 "2023-01-12 00:00:00"）
     *      wechatNumber: 微信号（当前为 null）
     *      party: 政党（如 "1" 表示党内）
     *      education: 教育背景（当前为 null）
     *      isSpecial: 是否特殊人员（当前为 null）
     *      specialType: 特殊类型（当前为 null）
     *      majorType: 专业类型（当前为 null）
     *      keeperAidentity: 网格员身份证明（当前为 null）
     *      keeperMotto: 网格员格言（当前为 null）
     *      personalProfile: 个人简介（当前为 null）
     *      isGridManager: 是否为网格管理员（当前为 null）
     *      versions: 版本号（如 1）
     *      keeperIdCardNumber: 网格员身份证号码（如 "220625********2226"）
     *      keeperBirthDate: 网格员出生日期（当前为 null）
     *      keeperAddress: 网格员住址（当前为 null）
     *      keeperPhoneNumber: 网格员电话（如 "189****7757"）
     *      keeperEmail: 网格员电子邮件（当前为 null）
     *      keeperGender: 性别（"1" 表示男性）
     *      keeperName: 网格员姓名（如 "刘娟玲"）
     *      longitude: 网格员位置的经度（当前为 null）
     *      latitude: 网格员位置的纬度（当前为 null）
     *      takePointTime: 点位时间（当前为 null）
     *      fjUrl: 附件 URL（当前为 null）
     *      isLine: 线路标志（"0" 表示否）
     *      provinceId: 省份 ID（当前为 null）
     *      cityId: 城市 ID（当前为 null）
     *      countyId: 区县 ID（当前为 null）
     *      locationFlag: 位置标志（"0" 表示未标记）
     *      keeperType: 网格员类型（如 "2"）
     *      operatingDuty: 操作职责（当前为 null）
     *      staffOrgName: 员工所在组织名称（如 "测试新增客0323-通用平台版本-1.0"）
     *      gridIds: 网格 ID 列表（当前为 null）
     *      gridNames: 网格名称列表（当前为 null）
     *      gridNameStr: 网格名称字符串（当前为 null）
     *      tenantName: 租户名称（当前为 null）
     *      gridId: 网格 ID（如 202304101005541730）
     *      gridName: 网格名称（如 "蜗牛测试1"）
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1692932481210,
     * 	"data": [
     *                {
     * 			"creator": "47610796b99b4523981a351f0e401044",
     * 			"createTime": "2023-01-12 14:58:55",
     * 			"createBy": null,
     * 			"updateBy": null,
     * 			"modifier": "47610796b99b4523981a351f0e401044",
     * 			"updateTime": "2023-07-28 15:21:02",
     * 			"deleteFlag": 0,
     * 			"tenantId": "122035807",
     * 			"userId": "d9eee890ce2a4daa9ff94bd46a60fc2b",
     * 			"appVerCode": "1.0",
     * 			"industryCode": "000000",
     * 			"dataScope": null,
     * 			"criteriaList": null,
     * 			"keeperId": "202301121458557799",
     * 			"gridType": null,
     * 			"aesPhoneNumber": null,
     * 			"keeperCode": "202301121458557799",
     * 			"staffId": "d9eee890ce2a4daa9ff94bd46a60fc2b",
     * 			"staffOrgId": "122035807-01",
     * 			"loginName": "liujl01",
     * 			"entryTime": "2023-01-12 00:00:00",
     * 			"wechatNumber": null,
     * 			"party": "1",
     * 			"education": null,
     * 			"isSpecial": null,
     * 			"specialType": null,
     * 			"majorType": null,
     * 			"keeperAidentity": null,
     * 			"keeperMotto": null,
     * 			"personalProfile": null,
     * 			"isGridManager": null,
     * 			"keeperSort": null,
     * 			"starClass": null,
     * 			"versions": 1,
     * 			"keeperIdCardNumber": "220625********2226",
     * 			"keeperBirthDate": null,
     * 			"keeperAddress": null,
     * 			"keeperPhoneNumber": "189****7757",
     * 			"keeperEmail": null,
     * 			"keeperGender": "1",
     * 			"keeperName": "刘娟玲",
     * 			"longitude": null,
     * 			"latitude": null,
     * 			"takePointTime": null,
     * 			"fjUrl": null,
     * 			"isLine": "0",
     * 			"provinceId": null,
     * 			"cityId": null,
     * 			"countyId": null,
     * 			"locationFlag": "0",
     * 			"keeperType": "2",
     * 			"operatingDuty": null,
     * 			"staffOrgName": "测试新增客0323-通用平台版本-1.0",
     * 			"gridIds": null,
     * 			"gridNames": null,
     * 			"status": null,
     * 			"distance": null,
     * 			"gridNameStr": null,
     * 			"tenantName": null,
     * 			"gridIdList": null,
     * 			"gridId": 202304101005541730,
     * 			"gridName": "蜗牛测试1"
     *                }
     * 	]
     * }
     * @param param
     * @return
     */
    @PostMapping("/gridKeeper/findAllSdk")
    Result<List<GridKeeperVO>> findGridKeeperAll(@RequestBody CommonFeignEntity param);

    /**
     * 网格员检索 -sdk
     * @apiNote 网格员检索 -sdk
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		total：总数
     * 		rows：列表
     * 		keeperId：网格员id
     * 	    keeperCode：网格员编号
     *      keeperName：网格员姓名
     *      loginName：登录账户
     *      keeperIdCardNumber ：身份证号(脱敏)
     * 		keeperPhoneNumber：电话（脱敏）
     * 	    aesPhoneNumber：电话（加密）
     *      keeperEmail：邮箱
     *      keeperGender：性别: 0-男；1-女
     *      latitude：纬度
     * 		longitude：经度
     * 	    isLine：1 在线  0 离线
     *      keeperAddress：地址
     *      provinceId：省份编码
     * 		cityId：地市编码
     * 	    countyId：区县编码
     *      keeperType：1-网格长；2-网格员
     *      gridIds：所属网格id
     *      gridNames：所属网格名称
     * @apiVersion 13.51
     * @tag 十三、网格能力
     * @requestDemo
     * {
     * 	"pageNum": 1,
     * 	"pageSize": 10,
     * 	"isLine": "0"
     * }
     * @responseDemo
     * code: 状态码，200 表示请求成功。
     * msg: 返回消息，请求成功。
     * time: 时间戳，表示请求的时间。
     * data: 包含分页数据的对象，其中包含
     *      total: 数据总数（如 27）。
     *      rows: 当前页的数据列表，包含网格员的详细信息。
     *          creator: 创建者的唯一标识（如 "47610796b99b4523981a351f0e401044"）。
     *          createTime: 创建时间（如 "2023-01-12 14:58:55"）。
     *          updateTime: 最后更新时间（如 "2023-07-28 15:21:02"）。
     *          modifier: 修改者的唯一标识（如 "47610796b99b4523981a351f0e401044"）。
     *          deleteFlag: 删除标志（0 表示未删除，1 表示已删除）。
     *          tenantId: 租户 ID（如 "122035807"）。
     *          userId: 用户 ID（如 "d9eee890ce2a4daa9ff94bd46a60fc2b"）。
     *          appVerCode: 应用版本号（如 "1.0"）。
     *          industryCode: 行业代码（如 "000000"）。
     *          keeperId: 网格员 ID（如 "202301121458557799"）。
     *          gridType: 网格类型（当前为 null）。
     *          aesPhoneNumber: 加密后的电话号码（如 "116b2908295e8aa18a58512004d11a76"）。
     *          keeperCode: 网格员编码（如 "202301121458557799"）。
     *          staffId: 员工 ID（如 "d9eee890ce2a4daa9ff94bd46a60fc2b"）。
     *          staffOrgId: 员工所属组织 ID（如 "122035807-01"）。
     *          loginName: 登录用户名（如 "liujl01"）。
     *          entryTime: 入职时间（如 "2023-01-12 00:00:00"）。
     *          wechatNumber: 微信号（当前为 null）。
     *          party: 政党（如 "1" 表示党内）。
     *          education: 教育背景（当前为 null）。
     *          isSpecial: 是否特殊人员（当前为 null）。
     *          specialType: 特殊类型（当前为 null）。
     *          majorType: 专业类型（当前为 null）。
     *          keeperAidentity: 网格员身份证明（当前为 null）。
     *          keeperMotto: 网格员格言（当前为 null）。
     *          personalProfile: 个人简介（当前为 null）。
     *          isGridManager: 是否为网格管理员（当前为 null）。
     *          versions: 版本号（如 1）。
     *          keeperIdCardNumber: 网格员身份证号码（如 "220625********2226"）。
     *          keeperBirthDate: 网格员出生日期（当前为 null）。
     *          keeperAddress: 网格员住址（当前为 null）。
     *          keeperPhoneNumber: 网格员电话（如 "189****7757"）。
     *          keeperEmail: 网格员电子邮件（当前为 null）。
     *          keeperGender: 性别（"1" 表示男性）。
     *          keeperName: 网格员姓名（如 "刘娟玲"）。
     *          longitude: 网格员位置的经度（当前为 null）。
     *          latitude: 网格员位置的纬度（当前为 null）。
     *          takePointTime: 点位时间（当前为 null）。
     *          fjUrl: 附件 URL（当前为 null）。
     *          isLine: 线路标志（"0" 表示否）。
     *          provinceId: 省份 ID（当前为 null）。
     *          cityId: 城市 ID（当前为 null）。
     *          countyId: 区县 ID（当前为 null）。
     *          locationFlag: 位置标志（"0" 表示未标记）。
     *          keeperType: 网格员类型（如 "2"）。
     *          operatingDuty: 操作职责（当前为 null）。
     *          staffOrgName: 员工所在组织名称（如 "测试新增客0323-通用平台版本-1.0"）。
     *          gridIds: 网格 ID 列表（如 ["202304101005541730"]）。
     *          gridNames: 网格名称列表（如 ["蜗牛测试1"]）。
     *          gridNameStr: 网格名称字符串（如 "蜗牛测试1"）。
     *          tenantName: 租户名称（当前为 null）。
     *          gridIdList: 网格 ID 列表（当前为 null）。
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1692256249869,
     * 	"data": {
     * 		"total": 27,
     * 		"rows": [
     *                        {
     * 				"creator": "47610796b99b4523981a351f0e401044",
     * 				"createTime": "2023-01-12 14:58:55",
     * 				"createBy": null,
     * 				"updateBy": null,
     * 				"modifier": "47610796b99b4523981a351f0e401044",
     * 				"updateTime": "2023-07-28 15:21:02",
     * 				"deleteFlag": 0,
     * 				"tenantId": "122035807",
     * 				"userId": "d9eee890ce2a4daa9ff94bd46a60fc2b",
     * 				"appVerCode": "1.0",
     * 				"industryCode": "000000",
     * 				"dataScope": null,
     * 				"criteriaList": null,
     * 				"keeperId": "202301121458557799",
     * 				"gridType": null,
     * 				"aesPhoneNumber": "116b2908295e8aa18a58512004d11a76",
     * 				"keeperCode": "202301121458557799",
     * 				"staffId": "d9eee890ce2a4daa9ff94bd46a60fc2b",
     * 				"staffOrgId": "122035807-01",
     * 				"loginName": "liujl01",
     * 				"entryTime": "2023-01-12 00:00:00",
     * 				"wechatNumber": null,
     * 				"party": "1",
     * 				"education": null,
     * 				"isSpecial": null,
     * 				"specialType": null,
     * 				"majorType": null,
     * 				"keeperAidentity": null,
     * 				"keeperMotto": null,
     * 				"personalProfile": null,
     * 				"isGridManager": null,
     * 				"keeperSort": null,
     * 				"starClass": null,
     * 				"versions": 1,
     * 				"keeperIdCardNumber": "220625********2226",
     * 				"keeperBirthDate": null,
     * 				"keeperAddress": null,
     * 				"keeperPhoneNumber": "189****7757",
     * 				"keeperEmail": null,
     * 				"keeperGender": "1",
     * 				"keeperName": "刘娟玲",
     * 				"longitude": null,
     * 				"latitude": null,
     * 				"takePointTime": null,
     * 				"fjUrl": null,
     * 				"isLine": "0",
     * 				"provinceId": null,
     * 				"cityId": null,
     * 				"countyId": null,
     * 				"locationFlag": "0",
     * 				"keeperType": "2",
     * 				"operatingDuty": null,
     * 				"staffOrgName": "测试新增客0323-通用平台版本-1.0",
     * 				"gridIds": [
     * 					"202304101005541730"
     * 				],
     * 				"gridNames": [
     * 					"蜗牛测试1"
     * 				],
     * 				"status": null,
     * 				"distance": null,
     * 				"gridNameStr": "蜗牛测试1",
     * 				"tenantName": null,
     * 				"gridIdList": null
     *            }
     * 		],
     * 		"code": 200
     * 	}
     * }
     * @param param
     * @return
     */
    @PostMapping("/gridKeeper/search")
    Result<TableDataInfo> searchGridKeeper(@RequestBody GridKeeperSearchParam param);

    /**
     * 周边网格员搜索
     * @apiNote 周边网格员搜索
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		keeperId：网格员id
     * 	    keeperCode：网格员编号
     *      keeperName：网格员姓名
     *      loginName：登录账户
     *      keeperIdCardNumber ：身份证号(脱敏)
     * 		keeperPhoneNumber：电话（脱敏）
     * 	    aesPhoneNumber：电话（加密）
     *      keeperEmail：邮箱
     *      keeperGender：性别: 0-男；1-女
     *      latitude：纬度
     * 		longitude：经度
     * 	    isLine：1 在线  0 离线
     *      keeperAddress：地址
     *      provinceId：省份编码
     * 		cityId：地市编码
     * 	    countyId：区县编码
     *      keeperType：1-网格长；2-网格员
     *      locationFlag：是否开启定位 1 是
     * @apiVersion 13.52
     * @tag 十三、网格能力
     * @requestDemo
     * {
     * 	"longitude": "121.5144113498264",
     * 	"latitude": "38.84739339192708",
     * 	"radius": "10000"
     * }
     * @responseDemo
     * code: 状态码，200 表示请求成功。
     * msg: 返回消息，请求成功。
     * time: 时间戳，表示请求的时间。
     * data: 一个包含网格员信息的数组，数组中每个对象表示一个网格员的信息。
     *      creator: 创建者的唯一标识（如 "d6fa064b618b4c1c906a36a52453b035"）。
     *      createTime: 创建时间（如 "2023-05-09 17:12:15"）。
     *      updateTime: 最后更新时间（如 "2023-08-03 17:09:15"）。
     *      modifier: 修改者的唯一标识（如 "8f39282ee6bb4dedb32ab6027ff167fc"）。
     *      deleteFlag: 删除标志（0 表示未删除，1 表示已删除）。
     *      tenantId: 租户 ID（如 "122035807"）。
     *      userId: 用户 ID（如 "d6fa064b618b4c1c906a36a52453b035"）。
     *      appVerCode: 应用版本号（当前为 null）。
     *      industryCode: 行业代码（如 "000000"）。
     *      keeperId: 网格员 ID（如 "202305091712156057"）。
     *      gridType: 网格类型（当前为 null）。
     *      aesPhoneNumber: 加密后的电话号码（当前为 null）。
     *      keeperCode: 网格员编码（如 "202305091712156057"）。
     *      staffId: 员工 ID（如 "d6fa064b618b4c1c906a36a52453b035"）。
     *      staffOrgId: 员工所属组织 ID（如 "122035807-01"）。
     *      loginName: 登录用户名（如 "fxy-ty"）。
     *      entryTime: 入职时间（如 "2023-05-10 00:00:00"）。
     *      wechatNumber: 微信号（当前为 null）。
     *      party: 政党（当前为 null）。
     *      education: 教育背景（当前为 null）。
     *      isSpecial: 是否特殊人员（当前为 null）。
     *      specialType: 特殊类型（当前为 null）。
     *      majorType: 专业类型（当前为 null）。
     *      keeperAidentity: 网格员身份证明（当前为 null）。
     *      keeperMotto: 网格员格言（当前为 null）。
     *      personalProfile: 个人简介（当前为 null）。
     *      isGridManager: 是否为网格管理员（当前为 null）。
     *      keeperSort: 排序字段（当前为 null）。
     *      starClass: 星级字段（当前为 null）。
     *      versions: 版本号（如 1）。
     *      keeperIdCardNumber: 网格员身份证号码（如 "321345199312039876"）。
     *      keeperBirthDate: 网格员出生日期（当前为 null）。
     *      keeperAddress: 网格员住址（如 "辽宁省大连市甘井子区七贤岭街道汇贤街大连腾飞园区(汇贤街)"）。
     *      keeperPhoneNumber: 网格员电话（如 "13398745823"）。
     *      keeperEmail: 网格员电子邮件（当前为 null）。
     *      keeperGender: 性别（当前为 null）。
     *      keeperName: 网格员姓名（如 "fxy"）。
     *      longitude: 网格员位置的经度（如 "121.5144748263889"）。
     *      latitude: 网格员位置的纬度（如 "38.84746771918402"）。
     *      takePointTime: 点位时间（如 "2023-08-03 17:09:15"）。
     *      fjUrl: 附件 URL（当前为 null）。
     *      isLine: 线路标志（"0" 表示否）。
     *      provinceId: 省份 ID（如 "210000"）。
     *      cityId: 城市 ID（如 "210200"）。
     *      countyId: 区县 ID（如 "210211"）。
     *      locationFlag: 位置标志（"0" 表示未标记）。
     *      keeperType: 网格员类型（如 "2"）。
     *      operatingDuty: 操作职责（当前为 null）。
     *      staffOrgName: 员工所在组织名称（如 "测试新增客0323-通用平台版本-1.0"）。
     *      gridIds: 网格 ID 列表（当前为 null）。
     *      gridNames: 网格名称列表（当前为 null）。
     *      status: 状态字段（如 "0"）。
     *      distance: 距离（如 "9.925976013577227"）。
     *      gridNameStr: 网格名称字符串（当前为 null）。
     *      tenantName: 租户名称（当前为 null）。
     *      gridRefGridKeeperVOList
     *          gridId: 网格 ID（如 "202305121516254841"）。
     *          gridCode: 网格编码（如 "202305121516254841"）。
     *          gridName: 网格名称（如 "昕测试用"）。
     *          keeperId: 网格员 ID（如 "202305091712156057"）。
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1692256902200,
     * 	"data": [
     *                {
     * 			"creator": "d6fa064b618b4c1c906a36a52453b035",
     * 			"createTime": "2023-05-09 17:12:15",
     * 			"createBy": null,
     * 			"updateBy": null,
     * 			"modifier": "8f39282ee6bb4dedb32ab6027ff167fc",
     * 			"updateTime": "2023-08-03 17:09:15",
     * 			"deleteFlag": 0,
     * 			"tenantId": "122035807",
     * 			"userId": "d6fa064b618b4c1c906a36a52453b035",
     * 			"appVerCode": null,
     * 			"industryCode": "000000",
     * 			"dataScope": null,
     * 			"criteriaList": null,
     * 			"keeperId": "202305091712156057",
     * 			"gridType": null,
     * 			"aesPhoneNumber": null,
     * 			"keeperCode": "202305091712156057",
     * 			"staffId": "d6fa064b618b4c1c906a36a52453b035",
     * 			"staffOrgId": "122035807-01",
     * 			"loginName": "fxy-ty",
     * 			"entryTime": "2023-05-10 00:00:00",
     * 			"wechatNumber": null,
     * 			"party": null,
     * 			"education": null,
     * 			"isSpecial": null,
     * 			"specialType": null,
     * 			"majorType": null,
     * 			"keeperAidentity": null,
     * 			"keeperMotto": null,
     * 			"personalProfile": null,
     * 			"isGridManager": null,
     * 			"keeperSort": null,
     * 			"starClass": null,
     * 			"versions": 1,
     * 			"keeperIdCardNumber": "321345199312039876",
     * 			"keeperBirthDate": null,
     * 			"keeperAddress": "辽宁省大连市甘井子区七贤岭街道汇贤街大连腾飞园区(汇贤街)",
     * 			"keeperPhoneNumber": "13398745823",
     * 			"keeperEmail": null,
     * 			"keeperGender": null,
     * 			"keeperName": "fxy",
     * 			"longitude": "121.5144748263889",
     * 			"latitude": "38.84746771918402",
     * 			"takePointTime": "2023-08-03 17:09:15",
     * 			"fjUrl": null,
     * 			"isLine": "0",
     * 			"provinceId": "210000",
     * 			"cityId": "210200",
     * 			"countyId": "210211",
     * 			"locationFlag": "0",
     * 			"keeperType": "2",
     * 			"operatingDuty": null,
     * 			"staffOrgName": "测试新增客0323-通用平台版本-1.0",
     * 			"gridIds": null,
     * 			"gridNames": null,
     * 			"status": "0",
     * 			"distance": "9.925976013577227",
     * 			"gridNameStr": null,
     * 			"tenantName": null,
     * 			"gridRefGridKeeperVOList": [
     *                {
     * 					"creator": null,
     * 					"createTime": null,
     * 					"createBy": null,
     * 					"updateBy": null,
     * 					"modifier": null,
     * 					"updateTime": null,
     * 					"deleteFlag": null,
     * 					"tenantId": null,
     * 					"userId": null,
     * 					"appVerCode": null,
     * 					"industryCode": null,
     * 					"dataScope": null,
     * 					"criteriaList": null,
     * 					"gridId": "202305121516254841",
     * 					"gridCode": "202305121516254841",
     * 					"gridName": "昕测试用",
     * 					"keeperId": 202305091712156057
     *                }
     * 			]
     *        }
     * 	]
     * }
     * @param vo
     * @return
     */
    @PostMapping("/gridKeeper/getKeeperByPeriphery")
    Result<List<GridKeeperVO>> getKeeperByPeriphery(@RequestBody @Validated KeeperPeripheryParam vo);


    /**
     * 网格员树
     * @apiNote 网格员树
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		id：id
     * 	    pid：父级id
     *      label：名称
     *      type：0 网格 1 网格长 2 网格
     *      tel ：电话号码
     * 		userId：用户id
     * 	    center：中心点坐标
     *      isLine：是否在线
     *      allTotal：网格员总数
     *      lineTotal：网格员在线数
     * 		children：子级
     * @apiVersion 13.53
     * @tag 十三、网格能力
     * @requestDemo
     * {"type":"1"}
     * @responseDemo
     * code: 状态码，200 表示请求成功。
     * msg: 返回消息，操作成功。
     * data: 包含组织和人员信息的数组，数据中的每一项代表一个组织或人员。
     *      id: 组织 ID（如 "202209011726583118"）。
     *      label: 组织名称（如 "测试新增客0323-通用平台版本-1.0"）。
     *      pid: 父组织 ID（根组织的父 ID 为 "0"）。
     *      children
     *          id: 子组织或人员的 ID（如 "202308030849536142"）。
     *          label: 子组织或人员的名称（如 "崔艳"）。
     *          pid: 父组织的 ID。
     *          children
     *              children
     *              id: 子组织或人员的 ID（如 "202308030849536142"）。
     *              label: 子组织或人员的名称（如 "崔艳"）。
     *              pid: 父组
     *
     * {
     * 	"msg": "操作成功",
     * 	"code": 200,
     * 	"data": [
     *                {
     * 			"id": "202209011726583118",
     * 			"label": "测试新增客0323-通用平台版本-1.0",
     * 			"pid": "0",
     * 			"children": [
     *                {
     * 					"id": "202308030849536142",
     * 					"label": "崔艳",
     * 					"pid": "202209011726583118",
     * 					"children": [
     *                        {
     * 							"id": "202308111427322248",
     * 							"label": "王大",
     * 							"pid": "202308030849536142",
     * 							"tel": "183****1850",
     * 							"isLine": "0",
     * 							"allTotal": "0",
     * 							"lineTotal": "0",
     * 							"icon": null,
     * 							"type": "2",
     * 							"center": null,
     * 							"gridLevelCode": null,
     * 							"color": "",
     * 							"userId": "b1921ab47c25491da9e45d6a7ecaffda",
     * 							"userName": "wangsn01",
     * 							"locationFlag": "0"
     *                        },
     *                        {
     * 							"id": "202305091716413758",
     * 							"label": "裴鸽",
     * 							"pid": "202308030849536142",
     * 							"tel": "189****7757",
     * 							"isLine": "0",
     * 							"allTotal": "0",
     * 							"lineTotal": "0",
     * 							"icon": null,
     * 							"type": "2",
     * 							"center": null,
     * 							"gridLevelCode": null,
     * 							"color": "",
     * 							"userId": "3e438170e0f24f24b1d60aeae5f88922",
     * 							"userName": "peige01",
     * 							"locationFlag": "0"
     *                        }
     * 					]
     *                }
     * 			]
     *        }
     * 	]
     * }
     * @param vo
     * @return
     */
    @PostMapping(value = "/gridKeeper/getGridKeeperTree")
    AjaxResult getGridKeeperTree(@RequestBody @Validated KeeperTreeSelectParam vo);

    /**
     * 租户网格员图层
     * @apiNote 租户网格员图层
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		keeperId：网格员id
     * 	    keeperCode：网格员编号
     *      keeperName：网格员姓名
     *      loginName：登录账户
     *      keeperIdCardNumber ：身份证号(脱敏)
     * 		keeperPhoneNumber：电话（脱敏）
     * 	    aesPhoneNumber：电话（加密）
     *      keeperEmail：邮箱
     *      keeperGender：性别: 0-男；1-女
     *      latitude：纬度
     * 		longitude：经度
     * 	    isLine：1 在线  0 离线
     *      keeperAddress：地址
     *      provinceId：省份编码
     * 		cityId：地市编码
     * 	    countyId：区县编码
     *      keeperType：1-网格长；2-网格员
     *      locationFlag：是否开启定位 1 是
     * @apiVersion 13.54
     * @tag 十三、网格能力
     * @requestDemo
     * {
     * 	"gridId": "202306091628379049"
     * }
     * @responseDemo
     * code: 状态码，200 表示请求成功。
     * msg: 返回消息，请求成功。
     * data: 包含个人信息的数组，每一项代表一个具体的负责人或管理员
     *      keeperId: 负责人 ID（如 "202305121403032566"）。
     *      keeperCode: 负责人代码（如 "202305121403032566"）。
     *      keeperName: 负责人姓名（如 "张三三"）。
     *      loginName: 登录名（如 "zhangsan002"）。
     *      keeperIdCardNumber: 身份证号（如 "211432********1234"）。
     *      keeperPhoneNumber: 电话号码（如 "133****3233"）。
     *      keeperEmail: 电子邮箱（如 null，即未提供）。
     *      status: 状态（如 "0"，表示该负责人处于有效状态）。
     *      longitude: 经度（未提供）。
     *      latitude: 纬度（未提供）。
     *      address: 地址（空字符串，表示未提供地址信息）。
     *      provinceId: 省份 ID（未提供）。
     *      cityId: 城市 ID（未提供）。
     *      countyId: 区县 ID（未提供）。
     *      keeperGender: 性别（如 "0"，表示女性）。
     *      userId: 用户 ID（如 "5806cf9cb4904f22b815264855f5927f"）。
     *      aesPhoneNumber: 加密后的电话号码（如 "ec51915a23eb2118b73d3f2315fd25a0"）。
     *      isLine: 是否在线（如 "0"，表示不在线）。
     *      locationFlag: 位置标志（如 "0"，表示未标记位置）。
     *      keeperType: 负责人类型（如 "2"）。
     *      provinceName: 省份名称（未提供）。
     *      cityName: 城市名称（未提供）。
     *      tenantId: 租户 ID（未提供）。
     *      tenantName: 租户名称（未提供）。
     *      industryCode: 行业代码（未提供）。
     *      industryName: 行业名称（未提供）。
     *      appVerCode: 应用版本代码（未提供）。
     *      color: 颜色代码（如 "#00b0ff"）。
     *      gridId: 网格 ID（如 "202306091628379049"）。
     *      keeperPhotoUrl: 负责人照片 URL（未提供）。
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1692259722047,
     * 	"data": [
     *                {
     * 			"keeperId": "202305121403032566",
     * 			"keeperCode": "202305121403032566",
     * 			"keeperName": "张三三",
     * 			"loginName": "zhangsan002",
     * 			"keeperIdCardNumber": "211432********1234",
     * 			"keeperPhoneNumber": "133****3233",
     * 			"keeperEmail": null,
     * 			"status": "0",
     * 			"longitude": null,
     * 			"latitude": null,
     * 			"address": "",
     * 			"provinceId": null,
     * 			"cityId": null,
     * 			"countyId": null,
     * 			"keeperGender": "0",
     * 			"userId": "5806cf9cb4904f22b815264855f5927f",
     * 			"aesPhoneNumber": "ec51915a23eb2118b73d3f2315fd25a0",
     * 			"isLine": "0",
     * 			"locationFlag": "0",
     * 			"keeperType": "2",
     * 			"provinceName": null,
     * 			"cityName": null,
     * 			"tenantId": null,
     * 			"tenantName": null,
     * 			"industryCode": null,
     * 			"industryName": null,
     * 			"appVerCode": null,
     * 			"color": "#00b0ff",
     * 			"gridId": "202306091628379049",
     * 			"keeperPhotoUrl": null
     *        }
     * 	]
     * }
     * @param gridKeeperLayerParam
     * @return
     */
    @PostMapping("/gridKeeper/getGridKeeperByLayer")
    Result<List<GridKeeperListVO>> getGridKeeperByLayer(@RequestBody GridKeeperLayerParam gridKeeperLayerParam);


    /**
     * 新租户网格员图层（一个网格员返回一条）
     * @apiNote 新租户网格员图层（一个网格员返回一条）
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		keeperId：网格员id
     * 	    keeperCode：网格员编号
     *      keeperName：网格员姓名
     *      loginName：登录账户
     *      keeperIdCardNumber ：身份证号(脱敏)
     * 		keeperPhoneNumber：电话（脱敏）
     * 	    aesPhoneNumber：电话（加密）
     *      keeperEmail：邮箱
     *      keeperGender：性别: 0-男；1-女
     *      latitude：纬度
     * 		longitude：经度
     * 	    isLine：1 在线  0 离线
     *      keeperAddress：地址
     *      provinceId：省份编码
     * 		cityId：地市编码
     * 	    countyId：区县编码
     *      keeperType：1-网格长；2-网格员
     *      locationFlag：是否开启定位 1 是
     * @apiVersion 13.55
     * @tag 十三、网格能力
     * @requestDemo
     * {
     * 	"gridId": "202306091628379049"
     * }
     * @responseDemo
     * code: 状态码，200 表示请求成功。
     * msg: 返回消息，请求成功。
     * data: 个人信息数据，包含了负责人的详细资料。
     *      keeperId: 负责人 ID（202305121403032566）
     *      keeperCode: 负责人代码（202305121403032566）
     *      keeperName: 负责人姓名（张三三）
     *      loginName: 登录名（zhangsan002）
     *      keeperIdCardNumber: 身份证号（211432********1234，部分信息被隐藏）
     *      keeperPhoneNumber: 电话号码（133****3233，部分信息被隐藏）
     *      keeperEmail: 电子邮箱（null，即未提供）
     *      status: 负责人状态（0，表示有效）
     *      longitude: 经度（null，未提供）
     *      latitude: 纬度（null，未提供）
     *      address: 地址（""，未提供）
     *      provinceId: 省份 ID（null，未提供）
     *      cityId: 城市 ID（null，未提供）
     *      countyId: 区县 ID（null，未提供）
     *      keeperGender: 性别（0，表示女性）
     *      userId: 用户 ID（5806cf9cb4904f22b815264855f5927f）
     *      aesPhoneNumber: 加密后的电话号码（ec51915a23eb2118b73d3f2315fd25a0）
     *      isLine: 是否在线（0，表示不在线）
     *      locationFlag: 位置标志（0，表示未标记位置）
     *      keeperType: 负责人类型（2）
     *      provinceName: 省份名称（null，未提供）
     *      cityName: 城市名称（null，未提供）
     *      tenantId: 租户 ID（null，未提供）
     *      tenantName: 租户名称（null，未提供）
     *      industryCode: 行业代码（null，未提供）
     *      industryName: 行业名称（null，未提供）
     *      appVerCode: 应用版本代码（null，未提供）
     *      color: 颜色（#00b0ff，可能代表该负责人在某个系统中的标识色）
     *      gridId: 网格 ID（202306091628379049）
     *      keeperPhotoUrl: 负责人照片 URL（null，未提供）
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1692259722047,
     * 	"data": [
     *                {
     * 			"keeperId": "202305121403032566",
     * 			"keeperCode": "202305121403032566",
     * 			"keeperName": "张三三",
     * 			"loginName": "zhangsan002",
     * 			"keeperIdCardNumber": "211432********1234",
     * 			"keeperPhoneNumber": "133****3233",
     * 			"keeperEmail": null,
     * 			"status": "0",
     * 			"longitude": null,
     * 			"latitude": null,
     * 			"address": "",
     * 			"provinceId": null,
     * 			"cityId": null,
     * 			"countyId": null,
     * 			"keeperGender": "0",
     * 			"userId": "5806cf9cb4904f22b815264855f5927f",
     * 			"aesPhoneNumber": "ec51915a23eb2118b73d3f2315fd25a0",
     * 			"isLine": "0",
     * 			"locationFlag": "0",
     * 			"keeperType": "2",
     * 			"provinceName": null,
     * 			"cityName": null,
     * 			"tenantId": null,
     * 			"tenantName": null,
     * 			"industryCode": null,
     * 			"industryName": null,
     * 			"appVerCode": null,
     * 			"color": "#00b0ff",
     * 			"gridId": "202306091628379049",
     * 			"keeperPhotoUrl": null
     *        }
     * 	]
     * }
     * @param gridKeeperLayerParam
     * @return
     */
    @PostMapping("/gridKeeper/getGridKeeperByLayerNew")
    Result<List<GridKeeperListVO>> getGridKeeperByLayerNew(@RequestBody GridKeeperLayerParam gridKeeperLayerParam);

    /**
     * 租户网格图层
     * @apiNote 租户网格图层
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		gridId：网格id
     * 		gridCode：网格编码
     * 		gridName：网格名称
     * 	    gridName：网格名称
     *      parentId：父级网格id
     *      parentCode：父级网格编码
     *      gridLevel ：网格层级
     * 		pointStatus：标绘状态 1 已标绘
     * 	    gridRange：网格坐标范围
     *      color：网格颜色
     *      gridCenter：网格中心坐标
     *      gridEmCenter：所属组织编码
     * 		gridEmCenterName：所属组织
     * 	    keeperId：网格长id
     *      keeperName：网格长姓名
     * @apiVersion 13.56
     * @tag 十三、网格能力
     * @requestDemo
     * {
     * 	"gridId": "202209011726583118"
     * }
     * @responseDemo
     * code: 状态码，200 表示请求成功。
     * msg: 返回消息，请求成功。
     * time: 请求的时间戳（1692276808429）。
     * data: 网格信息数据，包含详细的网格及其负责人资料。
     *      creator: 创建者（null，未提供）
     *      createTime: 创建时间（2022-09-01 17:26:58）
     *      createBy: 创建者（null，未提供）
     *      updateBy: 更新者（null，未提供）
     *      modifier: 修改者（null，未提供）
     *      updateTime: 更新时间（null，未提供）
     *      deleteFlag: 删除标记（null，未提供）
     *      tenantId: 租户 ID（null，未提供）
     *      userId: 用户 ID（null，未提供）
     *      appVerCode: 应用版本代码（null，未提供）
     *      industryCode: 行业代码（null，未提供）
     *      dataScope: 数据范围（null，未提供）
     *      criteriaList: 筛选条件列表（null，未提供）
     *      parentName: 上级名称（null，未提供）
     *      gridId: 网格 ID（202209011726583118）
     *      isEnd: 是否是结束网格（null，未提供）
     *      gridCode: 网格代码（202209011726583118）
     *      gridName: 网格名称（测试新增客0323-通用平台版本-1.0）
     *      parentId: 上级网格 ID（0，表示根网格）
     *      parentCode: 上级网格代码（0）
     *      gridLevel: 网格等级（1，表示一级网格）
     *      pointStatus: 点状态（1，可能表示网格处于活跃状态）
     *      color: 网格颜色（#00b0ff,#00b0ff,#00b0ff,#00b0ff，可能是表示网格的视觉标识颜色）
     *      gridCenter: 网格中心的坐标点（[[116.349189,39.762163], [121.512234,38.846254], ...]，一个包含多个坐标的多边形）
     *      gridRange: 网格范围（多个坐标集，表示该网格的地理边界）
     *      keeperName: 负责人姓名（崔艳）
     *      keeperId: 负责人 ID（202308030849536142）
     *      keeperPhoneNumber: 负责人电话（186****5081，部分信息被隐藏）
     *      tenantName: 租户名称（null，未提供）
     *      provinceCode: 省份代码（null，未提供）
     *      cityCode: 城市代码（null，未提供）
     *      provinceName: 省份名称（null，未提供）
     *      cityName: 城市名称（null，未提供）
     *      keeperPhotoUrl: 负责人照片 URL（null，未提供）
     *      isLine: 是否在线（null，未提供）
     *      phonePlainText: 负责人电话明文（null，未提供）
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1692276808429,
     * 	"data": [
     *                {
     * 			"creator": null,
     * 			"createTime": "2022-09-01 17:26:58",
     * 			"createBy": null,
     * 			"updateBy": null,
     * 			"modifier": null,
     * 			"updateTime": null,
     * 			"deleteFlag": null,
     * 			"tenantId": null,
     * 			"userId": null,
     * 			"appVerCode": null,
     * 			"industryCode": null,
     * 			"dataScope": null,
     * 			"criteriaList": null,
     * 			"parentName": null,
     * 			"gridId": "202209011726583118",
     * 			"isEnd": null,
     * 			"gridCode": "202209011726583118",
     * 			"gridName": "测试新增客0323-通用平台版本-1.0",
     * 			"parentId": "0",
     * 			"parentCode": "0",
     * 			"gridLevel": "1",
     * 			"pointStatus": "1",
     * 			"color": "#00b0ff,#00b0ff,#00b0ff,#00b0ff",
     * 			"gridCenter": "[[116.349189,39.762163],[121.512234,38.846254],[121.492773,40.260205],[110.741397,32.95521]]",
     * 			"gridRange": "[[[115.50936,40.416857],[117.314643,40.438108],[117.556588,39.629441],[116.500404,39.086217],[115.14179,39.244944]],[[121.510587,38.848115],[121.511669,38.848102],[121.512509,38.848102],[121.51309,38.848039],[121.513688,38.848014],[121.514156,38.847876],[121.514722,38.847637],[121.515012,38.847247],[121.515141,38.846756],[121.515141,38.846329],[121.514867,38.845851],[121.514867,38.845549],[121.514867,38.845121],[121.514867,38.84492],[121.514269,38.844694],[121.513688,38.844631],[121.512864,38.844492],[121.512057,38.844417],[121.511314,38.844392],[121.51083,38.844392],[121.510184,38.844794],[121.509861,38.845197],[121.50957,38.84575],[121.509344,38.846454],[121.509328,38.846945],[121.509619,38.847385],[121.509893,38.847737],[121.509861,38.847699],[121.509909,38.847712],[121.510232,38.847926],[121.510216,38.847938],[121.510216,38.847964]],[[112.668352,39.488348],[120.222838,43.972921],[124.586205,36.808641],[130.317194,36.547489],[129.817904,38.35683]],[[108.653995,35.369163],[106.54462,30.541256],[114.938174,31.595193]]]",
     * 			"keeperName": "崔艳",
     * 			"keeperId": "202308030849536142",
     * 			"keeperPhoneNumber": "186****5081",
     * 			"tenantName": null,
     * 			"provinceCode": null,
     * 			"cityCode": null,
     * 			"provinceName": null,
     * 			"cityName": null,
     * 			"keeperPhotoUrl": null,
     * 			"isLine": null,
     * 			"phonePlainText": null
     *        }
     * 	]
     * }
     * @param gridLayerParam
     * @return
     */
    @PostMapping("/grid/getGridListByLayer")
    Result<List<GridListVO>> getGridListByLayer(@RequestBody GridLayerParam gridLayerParam);

    /**
     * 网格员轨迹获取
     * @apiNote 网格员轨迹获取
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		keeperId：网格员编号
     * 		longitude：经度
     * 		latitude：纬度
     * 	    createTime：创建时间
     * @apiVersion 13.57
     * @tag 十三、网格能力
     * @requestDemo
     * {
     * 	"startTime": "2023-07-15 00:00:00",
     * 	"endTime": "2023-07-20 00:00:00",
     * 	"keeperId": "202302201700361770"
     * }
     * @responseDemo
     * code: 状态码，200 表示请求成功。
     * msg: 返回消息，请求成功。
     * time: 请求的时间戳（1692349197839）。
     * data: 网格的具体位置数据。
     *      collectionName: 收集名称（null，未提供）
     *      keeperId: 负责人 ID（202302201700361770）
     *      longitude: 经度（121.5143934461805）
     *      latitude: 纬度（38.8474644639757）
     *      createTime: 创建时间（2023-07-19 09:35:13）
     *      tenantId: 租户 ID（122035807）
     *      industryCode: 行业代码（000000，未提供具体行业）
     *      appVerCode: 应用版本代码（2.0）
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1692349197839,
     * 	"data": [
     *                {
     * 			"collectionName": null,
     * 			"keeperId": "202302201700361770",
     * 			"longitude": "121.5143934461805",
     * 			"latitude": "38.8474644639757",
     * 			"createTime": "2023-07-19 09:35:13",
     * 			"tenantId": "122035807",
     * 			"industryCode": "000000",
     * 			"appVerCode": "2.0"
     *        },
     *        {
     * 			"collectionName": null,
     * 			"keeperId": "202302201700361770",
     * 			"longitude": "121.5144704861111",
     * 			"latitude": "38.8474964735243",
     * 			"createTime": "2023-07-19 09:36:47",
     * 			"tenantId": "122035807",
     * 			"industryCode": "000000",
     * 			"appVerCode": "2.0"
     *        }
     * 	]
     * }
     * @param queryTrailsParam
     * @return
     */
    @PostMapping("/gridKeeper/getKeeperTrail")
    Result getKeeperTrail(@RequestBody @Validated QueryTrailsParam queryTrailsParam);


    /**
     * 网格员数据获取(无脱敏)
     * @apiNote 网格员数据获取(无脱敏)
     * @apiVersion 13.58
     * @tag 十三、网格能力
     * @requestDemo
     * {
     *     "keeperId": 202208201449461817
     * }
     * @responseDemo
     * code: 状态码，200 表示请求成功。
     * msg: 返回消息，请求成功。
     * time: 请求的时间戳（1694673667382）。
     * data: 详细的个人信息数据。
     *      creator: 创建者 ID（2c3effd3f53a41698ef8a3bcaedf66b0）。
     *      createTime: 创建时间（2022-08-20 14:49:46）。
     *      modifier: 修改者 ID（2c3effd3f53a41698ef8a3bcaedf66b0）。
     *      updateTime: 最后更新时间（2022-08-20 14:50:08）。
     *      deleteFlag: 删除标志（1，表示已删除）。
     *      tenantId: 租户 ID（122035797）。
     *      userId: 用户 ID（null，未提供）。
     *      appVerCode: 应用版本代码（1.0）。
     *      industryCode: 行业代码（100000）。
     *      keeperId: 负责人 ID（202208201449461817）。
     *      keeperCode: 负责人编码（202208201449461817）。
     *      staffId: 员工 ID（019aca627cb74223a95dbec3d964e397）。
     *      staffOrgId: 员工组织 ID（122035797-01）。
     *      loginName: 登录名（test01）。
     *      entryTime: 入职时间（2022-08-20 00:00:00）。
     *      wechatNumber: 微信号（null，未提供）。
     *      keeperIdCardNumber: 负责人身份证号（110120198905263645）。
     *      keeperPhoneNumber: 负责人电话号码（13213241324）。
     *      keeperEmail: 负责人电子邮件（null，未提供）。
     *      keeperGender: 负责人性别（0，表示男性）。
     *      keeperName: 负责人姓名（fengajtest）。
     *      longitude: 经度（null，未提供）。
     *      latitude: 纬度（null，未提供）。
     *      locationFlag: 位置标志（1，表示位置有效）。
     *      keeperType: 负责人类型（2，具体类型未说明）。
     *      isLine: 是否在线（0，表示不在线）。
     *      status: 状态（null，未提供）。
     *      gridIds: 网格 ID 列表（null，未提供）。
     *      gridNames: 网格名称列表（null，未提供）。
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1694673667382,
     * 	"data": {
     * 		"creator": "2c3effd3f53a41698ef8a3bcaedf66b0",
     * 		"createTime": "2022-08-20 14:49:46",
     * 		"createBy": null,
     * 		"updateBy": null,
     * 		"modifier": "2c3effd3f53a41698ef8a3bcaedf66b0",
     * 		"updateTime": "2022-08-20 14:50:08",
     * 		"deleteFlag": 1,
     * 		"tenantId": "122035797",
     * 		"userId": null,
     * 		"appVerCode": "1.0",
     * 		"industryCode": "100000",
     * 		"dataScope": null,
     * 		"criteriaList": null,
     * 		"keeperId": "202208201449461817",
     * 		"gridType": null,
     * 		"aesPhoneNumber": null,
     * 		"keeperCode": "202208201449461817",
     * 		"staffId": "019aca627cb74223a95dbec3d964e397",
     * 		"staffOrgId": "122035797-01",
     * 		"loginName": "test01",
     * 		"entryTime": "2022-08-20 00:00:00",
     * 		"wechatNumber": null,
     * 		"party": null,
     * 		"education": null,
     * 		"isSpecial": null,
     * 		"specialType": null,
     * 		"majorType": null,
     * 		"keeperAidentity": null,
     * 		"keeperMotto": null,
     * 		"personalProfile": null,
     * 		"isGridManager": null,
     * 		"keeperSort": null,
     * 		"starClass": null,
     * 		"versions": 1,
     * 		"keeperIdCardNumber": "110120198905263645",
     * 		"keeperBirthDate": null,
     * 		"keeperAddress": null,
     * 		"keeperPhoneNumber": "13213241324",
     * 		"keeperEmail": null,
     * 		"keeperGender": "0",
     * 		"keeperName": "fengajtest",
     * 		"longitude": null,
     * 		"latitude": null,
     * 		"takePointTime": null,
     * 		"fjUrl": null,
     * 		"isLine": "0",
     * 		"provinceId": null,
     * 		"cityId": null,
     * 		"countyId": null,
     * 		"locationFlag": "1",
     * 		"keeperType": "2",
     * 		"operatingDuty": null,
     * 		"staffOrgName": null,
     * 		"gridIds": null,
     * 		"gridNames": null,
     * 		"status": null,
     * 		"distance": null,
     * 		"gridNameStr": null,
     * 		"tenantName": null,
     * 		"gridIdList": null
     *        }
     * }
     * @param param
     * @return
     */
    @PostMapping("/gridKeeper/findOneNoEncryptionSdk")
    Result findOneNoEncryptionSdk(@RequestBody GridKeeperInfoParam param);

    /**
     * 获取网格全部数据
     * @apiNote 获取网格全部数据
     * @apiVersion 13.59
     * @tag 十三、网格能力
     * @requestDemo
     * {
     *     "deptIds":"122035807-01"
     * }
     * @responseDemo
     * code: 状态码，200 表示请求成功。
     * msg: 返回消息，请求成功。
     * time: 请求的时间戳（1694673955487）。
     * data: 详细的网格信息数据。
     *      creator: 创建者 ID（dacbf40b280b4220a627d0294e5403db）。
     *      createTime: 创建时间（2023-05-12 15:16:25）。
     *      deleteFlag: 删除标志（0，表示未删除）。
     *      tenantId: 租户 ID（122035807）。
     *      industryCode: 行业代码（000000）。
     *      gridId: 网格 ID（202305121516254841）。
     *      gridCode: 网格编码（202305121516254841）。
     *      gridName: 网格名称（昕测试用）。
     *      parentId: 上级网格 ID（202209011726583118）。
     *      gridLevel: 网格等级（2，表示第二级网格）。
     *      gridLevelCode: 网格层级代码（101,202209011726583118,202305121516254841）。
     *      pointStatus: 点状态（0，表示当前状态有效）。
     *      gridEmCenter: 网格应急中心（122035807-01）。
     *      keeperName: 负责人姓名（null，未提供）。
     *      keeperId: 负责人 ID（null，未提供）。
     *      keeperPhoneNumber: 负责人电话号码（null，未提供）。
     *      provinceCode: 省份编码（null，未提供）。
     *      cityCode: 城市编码（null，未提供）。
     *      provinceName: 省份名称（null，未提供）。
     *      cityName: 城市名称（null，未提供）。
     *      topTreeName: 顶级树名称（null，未提供）。
     *      keeperCount: 负责人数量（0，表示当前没有负责人）。
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1694673955487,
     *     "data": [
     *         {
     *             "creator": "dacbf40b280b4220a627d0294e5403db",
     *             "createTime": "2023-05-12 15:16:25",
     *             "createBy": null,
     *             "updateBy": null,
     *             "modifier": null,
     *             "updateTime": null,
     *             "deleteFlag": 0,
     *             "tenantId": "122035807",
     *             "userId": null,
     *             "appVerCode": null,
     *             "industryCode": "000000",
     *             "dataScope": null,
     *             "criteriaList": null,
     *             "gridId": "202305121516254841",
     *             "tenantName": null,
     *             "industryName": null,
     *             "gridCode": "202305121516254841",
     *             "gridName": "昕测试用",
     *             "gbCode": null,
     *             "parentId": "202209011726583118",
     *             "parentName": null,
     *             "parentCode": "202209011726583118",
     *             "gridClassification": null,
     *             "gridLevel": "2",
     *             "gridLevelCode": "101,202209011726583118,202305121516254841",
     *             "gridProperty": null,
     *             "isEnd": null,
     *             "gridAddress": null,
     *             "gridIntroduce": null,
     *             "gridPicture": null,
     *             "reginScopeDesc": null,
     *             "gridSort": null,
     *             "pointStatus": "0",
     *             "gridEmCenter": "122035807-01",
     *             "gridEmCenterName": null,
     *             "gridBeginTime": null,
     *             "gridEndTime": null,
     *             "gridArea": null,
     *             "isValid": null,
     *             "pageNum": 1,
     *             "pageSize": 10,
     *             "color": null,
     *             "gridCenter": null,
     *             "gridRange": null,
     *             "tenantList": null,
     *             "gridIdList": null,
     *             "excludeGridId": 0,
     *             "keeperName": null,
     *             "keeperId": null,
     *             "keeperPhoneNumber": null,
     *             "provinceCode": null,
     *             "cityCode": null,
     *             "provinceName": null,
     *             "cityName": null,
     *             "topTreeName": null,
     *             "keeperPhotoUrl": null,
     *             "isLine": null,
     *             "phonePlainText": null,
     *             "todayAlarmNum": null,
     *             "keeperCount": 0
     *         }
     *     ]
     * }
     * @param param
     * @return
     */
    @PostMapping("/grid/getGridListByTenantId")
    Result<List<GridVO>> getGridListByTenantId(@RequestBody GridAllParam param);

    /**
     * 巡查历史列表数据(扩展)
     * @apiNote 巡查历史列表数据(扩展)
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		confId：配置id
     * 		taskId：巡查任务ID
     * 		gridId：网格id
     * 	    taskStatus：任务状态
     * @apiVersion 13.60
     * @tag 十三、网格能力
     * @requestDemo
     * {
     * 	"keeperId": "202302201700361770"
     * }
     * @responseDemo
     * code：状态码
     * msg：返回消息
     * time：时间戳
     * data
     *    total：总数
     *    code：状态码
     *    rows：数据
     *      creator: 创建者 ID（dacbf40b280b4220a627d0294e5403db）
     *      taskId: 任务 ID（202305121516254841）
     *      taskName: 任务名称（昕测试用）
     *      taskDesc: 任务描述（无）
     *      gridId: 网格 ID（202305121726583118）
     *      gridName: 巡查网格（昕测试用）
     *      keeperId: 巡查人 ID（202208201449461817）
     *      keeperName: 巡查人名称（fengajtest）
     *      startTime: 开始时间（2023-07-19 09:35:13）
     *      endTime: 结束时间（2023-07-19 09:36:47）
     *      createTime: 创建时间（2023-05-12 15:16:25）
     *      inspectionTime: 巡查反馈时间（2023-07-19 09:36:47）
     *      taskStatus: 是否完成（1=已完成）
     *      tenantId: 租户 ID（122035807）
     *      retroaction: 反馈（无）
     *      fjId: 附件 ID（无）
     *      voiceUrl: 语音 URL（无）
     *      videoUrl: 视频 URL（无）
     *      sendFlag: 发送标识（无）
     *      industryCode: 行业代码（000000）
     *      appVerCode: 应用版本（2.0）
     *
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1692349697168,
     * 	"data": {
     * 		"total": 0,
     * 		"rows": [],
     * 		"code": 200
     *        }
     * }
     * @param inspectionTaskParam
     * @return
     */
    @PostMapping(value = "/inspectionTask/getTaskListExtend")
    public Result<TableDataInfo> getTaskListExtend(@RequestBody InspectionTaskParamExtend inspectionTaskParam);

    /**
     * 智慧乡镇根据网格id查网格名称
     * @apiNote 智慧乡镇根据网格id查网格名称
     * @apiVersion 13.61
     * @tag 十三、网格能力
     * @requestDemo
     * [
     * 	202305121516254841
     * ]
     * @responseDemo
     * code：状态码
     * msg：返回消息
     * time：时间戳
     * data
     *      creator: 创建者 ID（dacbf40b280b4220a627d0294e5403db）
     *      createTime: 创建时间（2023-05-12 15:16:25）
     *      createBy: 创建者（无）
     *      updateBy: 更新者（无）
     *      modifier: 修改者（无）
     *      updateTime: 更新时间（无）
     *      deleteFlag: 删除标识（0=未删除）
     *      tenantId: 租户 ID（122035807）
     *      userId: 用户 ID（无）
     *      appVerCode: 应用版本（无）
     *      industryCode: 行业代码（000000）
     *      dataScope: 数据范围（无）
     *      criteriaList: 条件列表（无）
     *      gridId: 网格 ID（202305121516254841）
     *      tenantName: 租户名称（无）
     *      industryName: 行业名称（无）
     *      gridCode: 网格编码（202305121516254841）
     *      gridName: 网格名称（昕测试用）
     *      gbCode: GB 代码（无）
     *      parentId: 父级 ID（202209011726583118）
     *      parentName: 父级名称（无）
     *      parentCode: 父级编码（202209011726583118）
     *      gridClassification: 网格分类（无）
     *      gridLevel: 网格级别（2）
     *      gridLevelCode: 网格级别编码（101,202209011726583118,202305121516254841）
     *      gridProperty: 网格属性（无）
     *      isEnd: 是否结束（无）
     *      gridAddress: 网格地址（无）
     *      gridIntroduce: 网格介绍（无）
     *      gridPicture: 网格图片（无）
     *      reginScopeDesc: 区域描述（无）
     *      gridSort: 网格排序（无）
     *      pointStatus: 点状态（0=无）
     *      gridEmCenter: 网格应急中心（122035807-01）
     *      gridEmCenterName: 应急中心名称（无）
     *      gridBeginTime: 网格开始时间（无）
     *      gridEndTime: 网格结束时间（无）
     *      gridArea: 网格面积（无）
     *      isValid: 是否有效（无）
     *      pageNum: 页码（1）
     *      pageSize: 页大小（10）
     *      color: 颜色（无）
     *      gridCenter: 网格中心（无）
     *      gridRange: 网格范围（无）
     *      tenantList: 租户列表（无）
     *      gridIdList: 网格 ID 列表（无）
     *      excludeGridId: 排除网格 ID（0）
     *      keeperName: 巡查员姓名（无）
     *      keeperId: 巡查员 ID（无）
     *      keeperPhoneNumber: 巡查员电话（无）
     *      provinceCode: 省份编码（无）
     *      cityCode: 城市编码（无）
     *      provinceName: 省份名称（无）
     *      cityName: 城市名称（无）
     *      topTreeName: 顶级树名称（无）
     *      keeperPhotoUrl: 巡查员照片 URL（无）
     *      isLine: 是否在线（无）
     *      phonePlainText: 电话明文（无）
     *      todayAlarmNum: 今日报警数量（无）
     *      keeperCount: 巡查员数量（0）
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1694674188481,
     *     "data": [
     *         {
     *             "creator": "dacbf40b280b4220a627d0294e5403db",
     *             "createTime": "2023-05-12 15:16:25",
     *             "createBy": null,
     *             "updateBy": null,
     *             "modifier": null,
     *             "updateTime": null,
     *             "deleteFlag": 0,
     *             "tenantId": "122035807",
     *             "userId": null,
     *             "appVerCode": null,
     *             "industryCode": "000000",
     *             "dataScope": null,
     *             "criteriaList": null,
     *             "gridId": "202305121516254841",
     *             "tenantName": null,
     *             "industryName": null,
     *             "gridCode": "202305121516254841",
     *             "gridName": "昕测试用",
     *             "gbCode": null,
     *             "parentId": "202209011726583118",
     *             "parentName": null,
     *             "parentCode": "202209011726583118",
     *             "gridClassification": null,
     *             "gridLevel": "2",
     *             "gridLevelCode": "101,202209011726583118,202305121516254841",
     *             "gridProperty": null,
     *             "isEnd": null,
     *             "gridAddress": null,
     *             "gridIntroduce": null,
     *             "gridPicture": null,
     *             "reginScopeDesc": null,
     *             "gridSort": null,
     *             "pointStatus": "0",
     *             "gridEmCenter": "122035807-01",
     *             "gridEmCenterName": null,
     *             "gridBeginTime": null,
     *             "gridEndTime": null,
     *             "gridArea": null,
     *             "isValid": null,
     *             "pageNum": 1,
     *             "pageSize": 10,
     *             "color": null,
     *             "gridCenter": null,
     *             "gridRange": null,
     *             "tenantList": null,
     *             "gridIdList": null,
     *             "excludeGridId": 0,
     *             "keeperName": null,
     *             "keeperId": null,
     *             "keeperPhoneNumber": null,
     *             "provinceCode": null,
     *             "cityCode": null,
     *             "provinceName": null,
     *             "cityName": null,
     *             "topTreeName": null,
     *             "keeperPhotoUrl": null,
     *             "isLine": null,
     *             "phonePlainText": null,
     *             "todayAlarmNum": null,
     *             "keeperCount": 0
     *         }
     *     ]
     * }
     * @param list
     * @return
     */
    @PostMapping("/grid/getGridNameByGridId")
    Result<List<GridVO>> getGridNameByGridId(@RequestBody List<Long> list);

    /**
     * 新增最高级网格
     * @apiNote 新增最高级网格
     * @apiVersion 13.62
     * @tag 十三、网格能力
     * @param gridVOList
     * @requestDemo
     * todayAlarmNum: 今日告警数（无）
     * keeperCount: 网格员数量（0）
     * gridKeeperlist
     *      keeperId: 网格员ID（无）
     *      gridType: 网格员类型（无）
     *      aesPhoneNumber: aes加密手机号（无）
     *      aesKeeperIdCardNumber: aes加密身份证号（无）
     *      keeperCode: 网格员编号（无）
     *      staffId: 人员id（无）
     *      staffOrgId: 人员岗位信息id（无）
     *      loginName: 登录名（无）
     *      entryTime: 选用时间（无）
     *      wechatNumber: 微信号（无）
     *      party: 政治面貌（无）
     *      education: 学历（无）
     *      isSpecial: 是否特殊网格员（无）
     *      specialType: 特殊网格员分类（无）
     *      majorType: 专业类别（无）
     *      keeperAidentity: 人员身份（无）
     *      keeperMotto: 职业格言（无）
     *      personalProfile: 个人简介（无）
     *      isGridManager: 是否网格长（无）
     *      keeperSort: 展示顺序（无）
     *      starClass: 星级标识（无）
     *      versions: 乐观锁（无）
     *      keeperIdCardNumber: 身份证号（无）
     *      keeperBirthDate: 出生日期（无）
     *      keeperAddress: 现居住地（无）
     *      keeperPhoneNumber: 联系方式（无）
     *      keeperEmail: 邮箱（无）
     *      keeperGender: 性别（无）
     *      keeperName: 网格员姓名（无）
     *      longitude: 经度（无）
     *      latitude: 纬度（无）
     *      takePointTime: 打点时间（无）
     *      fjUrl: 图片地址（无）
     *      isLine: 1 在线 0 离线（无）
     *      provinceId: 实时定位省份编码（无）
     *      cityId: 实时定位地市编码（无）
     *      countyId: 实时定位区县编码（无）
     *      locationFlag: 是否开启定位：0-是；1-否（无）
     *      keeperType: 网格员类型（1-网格长；2-网格员）
     *      operatingDuty: 工作职责（无）
     *      staffOrgName: 组织名称（无）
     *      gridIds: 网格编码集合（无）
     *      gridNames: 网格名称集合（无）
     *      status: 0-启用 1-停用（无）
     *      distance: 网格员位置距离中心点距离（无）
     *      gridNameStr: 网格名称字符串（无）
     *      tenantName: 租户名称（无）
     * @responseDemo
     * msg：返回消息
     * time：时间戳
     * code：状态码
     *
     * {
     *     code:200,
     *     time:1717739316751,
     *     msg:请求成功
     * }
     * @return
     */
    @PostMapping("/grid/addFirstGrid")
    Result addFirstGrid(@RequestBody List<GridVO> gridVOList);

    /**
     * 按日期统计用户巡查次数趋势
     * @apiNote 按日期统计用户巡查次数趋势
     * @apiVersion 13.63
     * @tag 十三、网格能力
     * @requestDemo
     * {
     * 	"beginTime": "2023-08-01",
     * 	"endTime":"2023-08-05",
     * 	"userId":"47610796b99b4523981a351f0e401044"
     * }
     * @responseDemo
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		date：日期
     * 		todayAll：用户巡查次数
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1692350137375,
     * 	"data": [
     *                {
     * 			"date": "2023-08-01",
     * 			"todayAll": 1
     *        },
     *        {
     * 			"date": "2023-08-02",
     * 			"todayAll": 0
     *        },
     *        {
     * 			"date": "2023-08-03",
     * 			"todayAll": 3
     *        },
     *        {
     * 			"date": "2023-08-04",
     * 			"todayAll": 1
     *        },
     *        {
     * 			"date": "2023-08-05",
     * 			"todayAll": 1
     *        }
     * 	]
     * }
     * @param queryCountTrend
     * @return
     */
    @PostMapping("/inspectionTask/inspectionCountTrend")
    Result inspectionCountTrend(@RequestBody QueryCountTrend queryCountTrend);

    /**
     * 根据用户ID查询其网格员ID
     * @apiNote 根据用户ID查询其网格员ID
     * @apiVersion 13.64
     * @tag 十三、网格能力
     * @requestDemo
     * {/bdm/gridKeeper/getGridKeeper?industryCode=000000&userId=bfc94ad18c9442d39575063089bb431f&tenantId=122035807}
     * @responseDemo
     * code：状态码
     * msg：返回消息
     * time：时间戳
     * data
     *      creator: 创建者 ID（无）
     *      createTime: 创建时间（无）
     *      createBy: 创建人（无）
     *      updateBy: 更新人（无）
     *      modifier: 修改人（无）
     *      updateTime: 更新时间（无）
     *      deleteFlag: 删除标志（无）
     *      tenantId: 租户 ID（无）
     *      userId: 用户 ID（无）
     *      appVerCode: 应用版本代码（无）
     *      industryCode: 行业代码（无）
     *      dataScope: 数据范围（无）
     *      criteriaList: 条件列表（无）
     *      keeperId: 网格员 ID（202309080854194965）
     *      gridType: 网格员类型（无）
     *      aesPhoneNumber: aes加密手机号（无）
     *      keeperCode: 网格员编号（202309080854194965）
     *      staffId: 人员 ID（无）
     *      staffOrgId: 人员岗位信息 ID（无）
     *      loginName: 登录名（无）
     *      entryTime: 选用时间（无）
     *      wechatNumber: 微信号（无）
     *      party: 政治面貌（无）
     *      education: 学历（无）
     *      isSpecial: 是否特殊网格员（无）
     *      specialType: 特殊网格员分类（无）
     *      majorType: 专业类别（无）
     *      keeperAidentity: 人员身份（无）
     *      keeperMotto: 职业格言（无）
     *      personalProfile: 个人简介（无）
     *      isGridManager: 是否网格长（0）
     *      keeperSort: 展示顺序（无）
     *      starClass: 星级标识（无）
     *      versions: 乐观锁（无）
     *      keeperIdCardNumber: 身份证号（无）
     *      keeperBirthDate: 出生日期（无）
     *      keeperAddress: 现居住地（无）
     *      keeperPhoneNumber: 联系方式（无）
     *      keeperEmail: 邮箱（无）
     *      keeperGender: 性别（无）
     *      keeperName: 网格员姓名（无）
     *      longitude: 经度（无）
     *      latitude: 纬度（无）
     *      takePointTime: 打点时间（无）
     *      fjUrl: 图片地址（无）
     *      isLine: 是否在线（无）
     *      provinceId: 实时定位省份编码（无）
     *      cityId: 实时定位地市编码（无）
     *      countyId: 实时定位区县编码（无）
     *      locationFlag: 是否开启定位（无）
     *      keeperType: 网格员类型（1）
     *      operatingDuty: 工作职责（无）
     *      staffOrgName: 组织名称（无）
     *      gridIds: 网格编码集合（无）
     *      gridNames: 网格名称集合（无）
     *      status: 状态（无）
     *      distance: 距离（无）
     *      gridNameStr: 网格名称字符串（无）
     *      tenantName: 租户名称（无）
     *      gridIdList: 网格 ID 列表（无）
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1694674551826,
     * 	"data": {
     * 		"creator": null,
     * 		"createTime": null,
     * 		"createBy": null,
     * 		"updateBy": null,
     * 		"modifier": null,
     * 		"updateTime": null,
     * 		"deleteFlag": null,
     * 		"tenantId": null,
     * 		"userId": null,
     * 		"appVerCode": null,
     * 		"industryCode": null,
     * 		"dataScope": null,
     * 		"criteriaList": null,
     * 		"keeperId": "202309080854194965",
     * 		"gridType": null,
     * 		"aesPhoneNumber": null,
     * 		"keeperCode": "202309080854194965",
     * 		"staffId": null,
     * 		"staffOrgId": null,
     * 		"loginName": null,
     * 		"entryTime": null,
     * 		"wechatNumber": null,
     * 		"party": null,
     * 		"education": null,
     * 		"isSpecial": null,
     * 		"specialType": null,
     * 		"majorType": null,
     * 		"keeperAidentity": null,
     * 		"keeperMotto": null,
     * 		"personalProfile": null,
     * 		"isGridManager": "0",
     * 		"keeperSort": null,
     * 		"starClass": null,
     * 		"versions": null,
     * 		"keeperIdCardNumber": null,
     * 		"keeperBirthDate": null,
     * 		"keeperAddress": null,
     * 		"keeperPhoneNumber": null,
     * 		"keeperEmail": null,
     * 		"keeperGender": null,
     * 		"keeperName": null,
     * 		"longitude": null,
     * 		"latitude": null,
     * 		"takePointTime": null,
     * 		"fjUrl": null,
     * 		"isLine": null,
     * 		"provinceId": null,
     * 		"cityId": null,
     * 		"countyId": null,
     * 		"locationFlag": null,
     * 		"keeperType": "1",
     * 		"operatingDuty": null,
     * 		"staffOrgName": null,
     * 		"gridIds": null,
     * 		"gridNames": null,
     * 		"status": null,
     * 		"distance": null,
     * 		"gridNameStr": null,
     * 		"tenantName": null,
     * 		"gridIdList": null
     *        }
     * }
     * @param industryCode,userId,tenantId
     * @return
     */
    @GetMapping("/gridKeeper/getGridKeeper")
    Result getGridKeeper(@RequestParam String industryCode,@RequestParam  String userId,@RequestParam String tenantId);


    /**
     * 根据网格员获取所属网格(新)
     * @apiNote 根据网格员获取所属网格(新)
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		gridId：网格id
     * @apiVersion 13.65
     * @tag 十三、网格能力
     * @requestDemo
     * {}
     * @responseDemo
     * code：状态码
     * msg：返回消息
     * time：时间戳
     * data
     *      creator: 创建者 ID (junnan01)
     *      createTime: 创建时间 (2023-02-09 17:14:03)
     *      createBy: 创建人 (无)
     *      updateBy: 更新人 (无)
     *      modifier: 修改人 (无)
     *      updateTime: 更新时间 (无)
     *      deleteFlag: 删除标志 (0)
     *      tenantId: 租户 ID (122035807)
     *      userId: 用户 ID (无)
     *      appVerCode: 应用版本代码 (无)
     *      industryCode: 行业代码 (100000)
     *      platformCode: 平台代码 (无)
     *      dataScope: 数据范围 (无)
     *      criteriaList: 条件列表 (无)
     *      gridId: 网格 ID (202302091714031050)
     *      tenantName: 租户名称 (无)
     *      industryName: 行业名称 (无)
     *      gridCode: 网格编码 (202302091714031050)
     *      gridName: 网格名称 (测试新增客0323-林业版本-1.0)
     *      gbCode: GB编码 (无)
     *      parentId: 父级网格 ID (0)
     *      parentName: 父级网格名称 (无)
     *      parentCode: 父级网格编码 (0)
     *      gridClassification: 网格分类 (无)
     *      gridLevel: 网格级别 (1)
     *      gridLevelCode: 网格级别代码 (101,202302091714031050)
     *      gridProperty: 网格属性 (无)
     *      isEnd: 是否结束 (无)
     *      gridAddress: 网格地址 (无)
     *      gridIntroduce: 网格介绍 (无)
     *      gridPicture: 网格图片 (无)
     *      reginScopeDesc: 区域范围描述 (无)
     *      gridSort: 网格排序 (1)
     *      pointStatus: 点状态 (0)
     *      gridEmCenter: 网格应急中心 (无)
     *      gridEmCenterName: 网格应急中心名称 (无)
     *      gridBeginTime: 网格开始时间 (无)
     *      gridEndTime: 网格结束时间 (无)
     *      gridArea: 网格区域 (无)
     *      isValid: 是否有效 (无)
     *      pageNum: 页码 (1)
     *      pageSize: 每页大小 (10)
     *      color: 网格颜色 (无)
     *      gridCenter: 网格中心 (无)
     *      gridRange: 网格范围 (无)
     *      tenantList: 租户列表 (无)
     *      gridIdList: 网格 ID 列表 (无)
     *      excludeGridId: 排除网格 ID (0)
     *      keeperName: 网格员姓名 (无)
     *      keeperId: 网格员 ID (202302161117035649)
     *      keeperPhoneNumber: 网格员手机号 (无)
     *      provinceCode: 省份编码 (无)
     *      cityCode: 城市编码 (无)
     *      provinceName: 省份名称 (无)
     *      cityName: 城市名称 (无)
     *      topTreeName: 顶层树名称 (无)
     *      keeperPhotoUrl: 网格员照片 URL (无)
     *      isLine: 是否在线 (无)
     *      phonePlainText: 手机号码明文 (无)
     *      borderColor: 边框颜色 (无)
     *      borderColorTransparency: 边框颜色透明度 (无)
     *      borderColorLineThickness: 边框线厚度 (无)
     *      colorTransparency: 颜色透明度 (无)
     *      todayAlarmNum: 今日告警数 (无)
     *      keeperCount: 网格员数量 (0)
     *      gridKeeperlist: 网格员列表 (无)
     *
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1718095139240,
     * 	"data": [
     *                {
     * 			"creator": "junnan01",
     * 			"createTime": "2023-02-09 17:14:03",
     * 			"createBy": null,
     * 			"updateBy": null,
     * 			"modifier": null,
     * 			"updateTime": null,
     * 			"deleteFlag": 0,
     * 			"tenantId": "122035807",
     * 			"userId": null,
     * 			"appVerCode": null,
     * 			"industryCode": "100000",
     * 			"platformCode": null,
     * 			"dataScope": null,
     * 			"criteriaList": null,
     * 			"gridId": "202302091714031050",
     * 			"tenantName": null,
     * 			"industryName": null,
     * 			"gridCode": "202302091714031050",
     * 			"gridName": "测试新增客0323-林业版本-1.0",
     * 			"gbCode": null,
     * 			"parentId": "0",
     * 			"parentName": null,
     * 			"parentCode": "0",
     * 			"gridClassification": null,
     * 			"gridLevel": "1",
     * 			"gridLevelCode": "101,202302091714031050",
     * 			"gridProperty": null,
     * 			"isEnd": null,
     * 			"gridAddress": null,
     * 			"gridIntroduce": null,
     * 			"gridPicture": null,
     * 			"reginScopeDesc": null,
     * 			"gridSort": 1,
     * 			"pointStatus": "0",
     * 			"gridEmCenter": null,
     * 			"gridEmCenterName": null,
     * 			"gridBeginTime": null,
     * 			"gridEndTime": null,
     * 			"gridArea": null,
     * 			"isValid": null,
     * 			"pageNum": 1,
     * 			"pageSize": 10,
     * 			"color": "",
     * 			"gridCenter": "",
     * 			"gridRange": "",
     * 			"tenantList": null,
     * 			"gridIdList": null,
     * 			"excludeGridId": 0,
     * 			"keeperName": null,
     * 			"keeperId": "202302161117035649",
     * 			"keeperPhoneNumber": null,
     * 			"provinceCode": null,
     * 			"cityCode": null,
     * 			"provinceName": null,
     * 			"cityName": null,
     * 			"topTreeName": null,
     * 			"keeperPhotoUrl": null,
     * 			"isLine": null,
     * 			"phonePlainText": null,
     * 			"borderColor": null,
     * 			"borderColorTransparency": null,
     * 			"borderColorLineThickness": null,
     * 			"colorTransparency": null,
     * 			"todayAlarmNum": null,
     * 			"keeperCount": 0,
     * 			"gridKeeperlist": null
     *        },
     *        {
     * 			"creator": "junnan01",
     * 			"createTime": "2023-02-09 17:14:03",
     * 			"createBy": null,
     * 			"updateBy": null,
     * 			"modifier": null,
     * 			"updateTime": null,
     * 			"deleteFlag": 0,
     * 			"tenantId": "122035807",
     * 			"userId": null,
     * 			"appVerCode": null,
     * 			"industryCode": "100000",
     * 			"platformCode": null,
     * 			"dataScope": null,
     * 			"criteriaList": null,
     * 			"gridId": "202302091714031050",
     * 			"tenantName": null,
     * 			"industryName": null,
     * 			"gridCode": "202302091714031050",
     * 			"gridName": "测试新增客0323-林业版本-1.0",
     * 			"gbCode": null,
     * 			"parentId": "0",
     * 			"parentName": null,
     * 			"parentCode": "0",
     * 			"gridClassification": null,
     * 			"gridLevel": "1",
     * 			"gridLevelCode": "101,202302091714031050",
     * 			"gridProperty": null,
     * 			"isEnd": null,
     * 			"gridAddress": null,
     * 			"gridIntroduce": null,
     * 			"gridPicture": null,
     * 			"reginScopeDesc": null,
     * 			"gridSort": 1,
     * 			"pointStatus": "0",
     * 			"gridEmCenter": null,
     * 			"gridEmCenterName": null,
     * 			"gridBeginTime": null,
     * 			"gridEndTime": null,
     * 			"gridArea": null,
     * 			"isValid": null,
     * 			"pageNum": 1,
     * 			"pageSize": 10,
     * 			"color": "",
     * 			"gridCenter": "",
     * 			"gridRange": "",
     * 			"tenantList": null,
     * 			"gridIdList": null,
     * 			"excludeGridId": 0,
     * 			"keeperName": null,
     * 			"keeperId": "202302161117035649",
     * 			"keeperPhoneNumber": null,
     * 			"provinceCode": null,
     * 			"cityCode": null,
     * 			"provinceName": null,
     * 			"cityName": null,
     * 			"topTreeName": null,
     * 			"keeperPhotoUrl": null,
     * 			"isLine": null,
     * 			"phonePlainText": null,
     * 			"borderColor": null,
     * 			"borderColorTransparency": null,
     * 			"borderColorLineThickness": null,
     * 			"colorTransparency": null,
     * 			"todayAlarmNum": null,
     * 			"keeperCount": 0,
     * 			"gridKeeperlist": null
     *        }
     * 	]
     * }
     * @param gridKeeperVO
     * @return
     */
    @PostMapping(value = "/gridKeeper/getGridByKeeperNew")
    Result<List<GridVO>> getGridByKeeperNew(@RequestBody GridKeeperVO gridKeeperVO);


    /**
     * 查询当前用户网格树形结构数据
     * @apiNote 查询当前用户网格树形结构数据
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回描述
     * 		data：返回内容
     * 		id：id
     * 		pid：父级id
     * 		label：名称
     * 	    children：子级
     * @apiVersion 13.66
     * @tag 十三、网格能力
     * @requestDemo
     * {}
     * @responseDemo
     * code: 状态码 (200)
     * msg: 消息 (操作成功)
     * time: 时间戳 (1718185246245)
     * data: 数据数组
     * - id: ID (202302091714031050)
     * - label: 标签 (测试新增客0323-林业版本-1.0)
     * - pid: 父级 ID (0)
     * - dept: 部门 (无)
     * - children: 子项列表
     * - id: 子项 ID (202303081708571849)
     * - label: 子项标签 (网格3)
     * - pid: 父级 ID (202302091714031050)
     * - dept: 部门 (无)
     * - tenantId: 租户 ID (无)
     * - tenantId: 租户 ID (无)
     *
     * {
     * 	"code": 200,
     * 	"msg": "操作成功",
     * 	"time": 1718185246245,
     * 	"data": [
     *                {
     * 			"id": "202302091714031050",
     * 			"label": "测试新增客0323-林业版本-1.0",
     * 			"pid": "0",
     * 			"dept": null,
     * 			"children": [
     *                {
     * 					"id": "202303081708571849",
     * 					"label": "网格3",
     * 					"pid": "202302091714031050",
     * 					"dept": null,
     * 					"tenantId": null
     *                }
     * 			],
     * 			"tenantId": null
     *        }
     * 	]
     * }
     * @param vo
     * @return
     */
    @PostMapping(value = "/grid/getGridTreeCurUser")
    Result<List<TreeSelect>> getGridTreeCurUser(@RequestBody GridVO vo);
}
