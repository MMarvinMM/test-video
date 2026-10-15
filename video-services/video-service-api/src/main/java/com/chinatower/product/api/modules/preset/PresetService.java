package com.chinatower.product.api.modules.preset;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.framework.common_service.response.ProcessResult;
import com.chinatower.product.api.modules.common.Constants;
import com.chinatower.product.api.modules.common.RespResult;
import com.chinatower.product.api.modules.devices.DevicesService;
import com.chinatower.product.api.modules.preset.api.PresetAPI;
import com.chinatower.product.api.modules.preset.dao.PresetDao;
import com.chinatower.product.api.modules.preset.entity.ChannelVideoVO;
import com.chinatower.product.api.modules.videosquare.entity.VideoRequestVO;
import com.chinatower.provider.call.video.client.PresetNewClient;
import com.chinatower.provider.call.video.client.VideoClient;
import com.chinatower.receive.api.IReceiveAlarmNewService;
import com.chinatower.receive.model.TranslnglatReq;
import com.chinatower.video.api.entity.*;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * video-service-api
 * @description: 预置点业务类
 * @author: wanglei
 * @create: 2021/11/19 09:24
 */
@RestController
@Slf4j
@CrossOrigin
@Api(tags = "预置点服务接口", description = "预置点服务接口")
public class PresetService implements PresetAPI {

	@Resource
	private VideoClient videoClient;

	@Resource
	private PresetNewClient presetNewClient;

	@Resource
	private DevicesService devicesService;

	@Autowired
	private IReceiveAlarmNewService receiveAlarmNewService;



	@Resource
	private PresetDao presetDao;

	public static final String ERROR_CODE_1001 = "1001";
	public static final String ERROR_CODE_1004 = "1004";
	public static final String RETURN_CODE_ERR_MSG = "请求失败";
	public static final String RETURN_CODE_SUCCESS_MSG = "请求成功";

	@Resource
	private FileUrlEncryptUtil fileUrlEncryptUtil;

	/**
	 * 把ProcessResult对象转为RespResult对象
	 * @param processResult
	 */
	private RespResult parseRespResult(ProcessResult processResult) {
		String resultStat = processResult.getResultStat();
		if( "SUCCESS".equalsIgnoreCase(resultStat) ) {
			// 成功
			return RespResult.SUCCESS(processResult.getData(), processResult.getMess());
		} else {
			// 失败
			return RespResult.FAIL("请求失败", "1002", processResult.getMess());
		}
	}

	/**
	 * 5.10 3D定位
	 */
	@Override
	public RespResult thirdLocation(@PathVariable String deviceCode, @PathVariable String channelCode, @RequestBody Map map, HttpServletRequest request) {
		VideoRequestVO videoVO = new VideoRequestVO();
	    videoVO.setDeviceCode(deviceCode);
	    videoVO.setChannelCode(channelCode);
	    videoVO.setCustomerCode(request.getHeader(Constants.CUSTOMER_CODE));
	    JSONObject res = this.thirdLocation(videoVO, map);
	    return JSON.toJavaObject(res, RespResult.class);
	}

	private JSONObject thirdLocation(VideoRequestVO videoRequestVO, Map map) {
	    JSONObject resultJson = videoClient.thirdLocation(videoRequestVO.getDeviceCode(),
	            videoRequestVO.getChannelCode(), videoRequestVO.getCustomerCode(), videoRequestVO.getUserCode(),
	            videoRequestVO.getOrderCode(),
	            (JSONObject) JSONObject.toJSON(map));
	    log.info("========thirdLocation：" + map);
	    log.info("========thirdLocation：" + resultJson);
	    return resultJson;
	}

	/**
	 * 5.11  抓图
	 */
	@Override
	public RespResult snapshot(@PathVariable String deviceCode, @PathVariable String channelCode, @RequestBody Map map, HttpServletRequest request) {
		VideoRequestVO videoVO = new VideoRequestVO();
	    videoVO.setDeviceCode(deviceCode);
	    videoVO.setChannelCode(channelCode);
	    videoVO.setCustomerCode(request.getHeader(Constants.CUSTOMER_CODE));
	    JSONObject res = this.snapshot(videoVO, map);
	    return JSON.toJavaObject(res, RespResult.class);
	}

	private JSONObject snapshot(VideoRequestVO videoRequestVO, Map map) {
	    JSONObject resultJson = videoClient.snapshot(videoRequestVO.getDeviceCode(),
	            videoRequestVO.getChannelCode(), videoRequestVO.getCustomerCode(), videoRequestVO.getUserCode(),
	            videoRequestVO.getOrderCode(),
	            (JSONObject) JSONObject.toJSON(map));
	    log.info("========snapshot：" + map);
	    log.info("========snapshot：" + resultJson);
	    return resultJson;
	}

	/**
	 * 5.14  转到指定PTZ位置 
	 */
	@Override
	public RespResult doPtzLocationCmd(@PathVariable String deviceCode, @PathVariable String channelCode, @RequestBody Map map, HttpServletRequest request) {
//		VideoRequestVO videoVO = new VideoRequestVO();
//		videoVO.setDeviceCode(deviceCode);
//		videoVO.setChannelCode(channelCode);
//		videoVO.setCustomerCode(request.getHeader(Constants.CUSTOMER_CODE));
//		ProcessResult tokenResult = devicesService.getToken(videoVO);
//		if(ProcessResult.ERROR.equals(tokenResult.getResultStat())){
//			return RespResult.FAIL(tokenResult.getMess(), Constants.ERROR_CODE_3006, tokenResult.getMess());
//		}
//		videoVO.setLockToken((String) tokenResult.getData());
//		JSONObject res = this.doPtzLocationCmd(videoVO, map);
//		if (!NullProcessUtil.isNull(res.getString(Constants.ERROR_CODE)) &&
//				Constants.ERROR_CODE_1003.equals(res.getString(Constants.ERROR_CODE))) {
//			ProcessResult doLockRes = devicesService.doLock(videoVO);
//			if (Constants.SUCCESS.equals(doLockRes.getResultStat())) {
//				String json = JSON.toJSONString(doLockRes.getData());//map转String
//				JSONObject platObj = JSON.parseObject(json);//String转json
//				PlatformControlVO platVO = JSON.toJavaObject(platObj , PlatformControlVO.class);
//				if (1 == platVO.getLock()) {  //0-排队中；1-加锁成功
//					videoVO.setLockToken(platVO.getToken());
//					res = this.doPtzLocationCmd(videoVO,map);
//				}
//			}
//		}
//		return JSON.toJavaObject(res, RespResult.class);

		DoPtzLocationCmdReq doControlPresetReq = new DoPtzLocationCmdReq();
		String customerCode = request.getHeader(Constants.CUSTOMER_CODE);
		String userCode = request.getHeader(Constants.USER_CODE);
		String appCode = request.getHeader(Constants.APP_CODE);
		String token = request.getHeader(Constants.X_TOKEN);
		doControlPresetReq.setLockToken(token);
		doControlPresetReq.setPan(Float.parseFloat(map.get("pan").toString()));
		doControlPresetReq.setTilt(Float.parseFloat(map.get("tilt").toString()));
		doControlPresetReq.setZoom(Float.parseFloat(map.get("zoom").toString()));
		log.info("========doPtzLocationCmd入参:deviceCode={},channelCode={},X-CustomerCode={},X-UserCode={},X-AppCode={}, doControlPresetReq={}",
				deviceCode, channelCode, customerCode, userCode, appCode, doControlPresetReq);
		Result result = presetNewClient.doPtzLocationCmd(deviceCode, channelCode, appCode, customerCode, userCode, doControlPresetReq);
		log.info("========doPtzLocationCmd结果：" + result);
	    return parseRespResultNew(result);
	}

	private JSONObject doPtzLocationCmd(VideoRequestVO videoRequestVO, Map map) {
	    JSONObject resultJson = videoClient.doPtzLocationCmd(videoRequestVO.getDeviceCode(),
	            videoRequestVO.getChannelCode(), videoRequestVO.getCustomerCode(), videoRequestVO.getUserCode(),
	            videoRequestVO.getOrderCode(), videoRequestVO.getLockToken(),
	            (JSONObject) JSONObject.toJSON(map));
	    log.info("========doPtzLocationCmd：" + map);
	    log.info("========doPtzLocationCmd：" + resultJson);
	    return resultJson;
	}

	/**
	 * 5.16	 获取加密后的OBS文件地址
	 */
	@Override
	public RespResult getObsEncryptUrls(@RequestBody Map<String,Object> map) {
		try {
			List<String> fileUrls = (List<String>) map.get("fileUrls");
			if(fileUrls == null || fileUrls.isEmpty()){
				return RespResult.FAIL(RETURN_CODE_ERR_MSG,ERROR_CODE_1001,"原始路径fileUrls不可为空");
			}
			List<JSONObject> list = new ArrayList<>();
			for (String url : fileUrls) {
				JSONObject demo = new JSONObject();
				demo.put("fileUrl",url);
				String encryptUrl = fileUrlEncryptUtil.dealObsFileUrl(url);
				demo.put("encryptUrl",encryptUrl);
				list.add(demo);
			}
			return RespResult.SUCCESS(list,RETURN_CODE_SUCCESS_MSG);
		}catch (Exception e){
		    log.error("111",e);
			return RespResult.FAIL(RETURN_CODE_ERR_MSG,ERROR_CODE_1004,"内部服务异常");
		}
	}

	@Override
	public RespResult addCameraPreset(@PathVariable String deviceCode, @PathVariable String channelCode, @RequestBody Map map, HttpServletRequest request) {
		AddPresetReq addPresetReq = new AddPresetReq();
		addPresetReq.setPresetName((String) map.get(Constants.PRESETNAME));
		String customerCode = request.getHeader(Constants.CUSTOMER_CODE);
		String userCode = request.getHeader(Constants.USER_CODE);
		String appCode = request.getHeader(Constants.APP_CODE);
		log.info("========addCameraPreset入参:deviceCode={},channelCode={},X-CustomerCode={},X-UserCode={},X-AppCode={},addPresetReq={}",
				deviceCode, channelCode, customerCode, userCode, appCode, addPresetReq);
		ResultEntity resultTemp = presetNewClient.addCameraPreset(deviceCode, channelCode,  appCode, customerCode, userCode, addPresetReq);
		log.info("========addCameraPreset结果：" + resultTemp);
		if(resultTemp.getReturnCode().equals("0000")){
			String presetId = (String) resultTemp.getData();
			VideoPresetDO videoPresetDO = presetDao.selectById(presetId,deviceCode);
			Map<String, Object> resultMap = new HashMap<>();
			resultMap.put("presetId", presetId);
			resultMap.put("presetName", videoPresetDO.getPresetName());
			resultMap.put("deviceCode", videoPresetDO.getDeviceCode());
			resultMap.put("channelCode", videoPresetDO.getChannelCode());
			Map<String, Object> ptz = new HashMap<>();
			ptz.put("pan", videoPresetDO.getPan());
			ptz.put("tilt", videoPresetDO.getTilt());
			ptz.put("zoom", videoPresetDO.getZoom());
			resultMap.put("preset", ptz);
			ResultEntity result = ResultEntity.success(resultMap);
			return parseRespResultNew(result);
		}else{
			return parseRespResultNew(resultTemp);
		}
	}

	@Override
	public RespResult delCameraPreset(@PathVariable String deviceCode, @PathVariable String channelCode, @PathVariable Integer presetId, HttpServletRequest request) {
		DelPresetReq delPresetReq = new DelPresetReq();
		delPresetReq.setPresetId(Long.parseLong(presetId.toString()));
		String customerCode = request.getHeader(Constants.CUSTOMER_CODE);
		String userCode = request.getHeader(Constants.USER_CODE);
		String appCode = request.getHeader(Constants.APP_CODE);
		log.info("========delCameraPreset入参:deviceCode={},channelCode={},X-CustomerCode={},X-UserCode={},X-AppCode={},delPresetReq={}",
				deviceCode, channelCode, customerCode, userCode, appCode, delPresetReq);
		Result<String> result = presetNewClient.delCameraPreset(deviceCode, channelCode, presetId.longValue(), appCode, customerCode, userCode, delPresetReq);
		log.info("========delCameraPreset结果：" + result);
		return parseRespResultNew(result);
	}

	@Override
	public RespResult updatePresetInfo(@PathVariable String deviceCode, @PathVariable String channelCode, @RequestBody Map map, HttpServletRequest request) {
		ModPresetReq modPresetReq = new ModPresetReq();
		Integer presetId = (Integer) map.get(Constants.PRESETID);
		modPresetReq.setPresetId(presetId.longValue());
		modPresetReq.setPresetName((String) map.get(Constants.PRESETNAME));
		String customerCode = request.getHeader(Constants.CUSTOMER_CODE);
		String userCode = request.getHeader(Constants.USER_CODE);
		String appCode = request.getHeader(Constants.APP_CODE);
		log.info("========updatePresetInfo入参:deviceCode={},channelCode={},X-CustomerCode={},X-UserCode={},X-AppCode={},modPresetReq={}",
				deviceCode, channelCode, customerCode, userCode, appCode, modPresetReq);
		Result<String> result = presetNewClient.updatePresetInfo(deviceCode, channelCode, appCode, customerCode, userCode, modPresetReq);
		log.info("========updatePresetInfo结果：" + result);
		return parseRespResultNew(result);
	}

	@Override
	public RespResult queryPresetList(@PathVariable String deviceCode, @PathVariable String channelCode, HttpServletRequest request) {
		VideoBaseReq videoBaseReq = new VideoBaseReq();
		String customerCode = request.getHeader(Constants.CUSTOMER_CODE);
		String userCode = request.getHeader(Constants.USER_CODE);
		String appCode = request.getHeader(Constants.APP_CODE);
		log.info("========queryPresetList入参:deviceCode={},channelCode={},X-CustomerCode={},X-UserCode={},X-AppCode={}, videoBaseReq={}",
				deviceCode, channelCode, customerCode, userCode, appCode, videoBaseReq);
		Result<List<QueryPresetListData>> result = presetNewClient.queryPresetList(deviceCode, channelCode, appCode, customerCode, userCode, videoBaseReq);
		log.info("========queryPresetList结果：" + result);
		return parseRespResultNew(result);
	}

	public RespResult parseRespResultNew(Result result){
		int code = result.getCode();
		if( code == 200 || code == 0) {
			// 成功
			return RespResult.SUCCESS(result.getData(), result.getMsg());
		} else {
			// 失败
			return RespResult.FAIL("请求失败", "1002", result.getMsg());
		}
	}

	public RespResult parseRespResultNew(ResultEntity result){
		String code = result.getReturnCode();
		if( ("0000").equals(code)) {
			// 成功
			return RespResult.SUCCESS(result.getData(), result.getReturnMsg());
		} else {
			// 失败
			return RespResult.FAIL("请求失败", "1002", result.getErrorMsg());
		}
	}

	@Override
	public RespResult queryVideoList(@PathVariable String deviceCode, @PathVariable String channelCode, @RequestBody Map map, HttpServletRequest request) {
		VideoPlayerQueryVideoListReq videoPlayerQueryVideoListReq = new VideoPlayerQueryVideoListReq();
		videoPlayerQueryVideoListReq.setCurrentPage((int) map.get(Constants.CURRENT_PAGE));
		videoPlayerQueryVideoListReq.setPageSize((int) map.get(Constants.PAGE_SIZE));
		videoPlayerQueryVideoListReq.setRecordType((int) map.get(Constants.RECORD_TYPE));
		List<Map<String, String>> times = new ArrayList<>();
		Map<String, String> mapTemp = new HashMap<>();
		mapTemp.put(Constants.START_TIME, (String) map.get(Constants.START_TIME));
		mapTemp.put(Constants.END_TIME, (String) map.get(Constants.END_TIME));
		times.add(mapTemp);
		videoPlayerQueryVideoListReq.setTimes(times);
		String customerCode = request.getHeader(Constants.CUSTOMER_CODE);
		String userCode = request.getHeader(Constants.USER_CODE);
		String appCode = request.getHeader(Constants.APP_CODE);
		log.info("========queryVideoList入参:deviceCode={},channelCode={},X-CustomerCode={},X-UserCode={},X-AppCode={}, videoPlayerQueryVideoListReq={}",
				deviceCode, channelCode, customerCode, userCode, appCode, videoPlayerQueryVideoListReq);
		Result<QueryVideoData> result = presetNewClient.queryVideoList(deviceCode, channelCode, appCode, customerCode, userCode, videoPlayerQueryVideoListReq);
		log.info("========queryVideoList结果：" + result);
		return parseRespResultNew(result);
	}

	@Override
	public RespResult queryPtzPosition(@PathVariable String deviceCode, @PathVariable String channelCode, HttpServletRequest request) {
		VideoBaseReq videoBaseReq = new VideoBaseReq();
		String customerCode = request.getHeader(Constants.CUSTOMER_CODE);
		String userCode = request.getHeader(Constants.USER_CODE);
		String appCode = request.getHeader(Constants.APP_CODE);
		log.info("========queryPtzPosition入参:deviceCode={},channelCode={},X-CustomerCode={},X-UserCode={},X-AppCode={}, videoBaseReq={}",
				deviceCode, channelCode, customerCode, userCode, appCode, videoBaseReq);
		Result<PresetData> result = presetNewClient.queryPtzPosition(deviceCode, channelCode, appCode, customerCode, userCode, videoBaseReq);
		log.info("========queryPtzPosition结果：" + result);
		return parseRespResultNew(result);
	}

	@Override
	public RespResult doControlPreset(@PathVariable String deviceCode, @PathVariable String channelCode, @RequestBody Map map, HttpServletRequest request) {
		DoControlPresetReq doControlPresetReq = new DoControlPresetReq();
		String customerCode = request.getHeader(Constants.CUSTOMER_CODE);
		String userCode = request.getHeader(Constants.USER_CODE);
		String appCode = request.getHeader(Constants.APP_CODE);
		String token = request.getHeader(Constants.X_TOKEN);
		doControlPresetReq.setLockToken(token);
		doControlPresetReq.setPresetId((int) map.get("presetId"));
		log.info("========doControlPreset入参:deviceCode={},channelCode={},X-CustomerCode={},X-UserCode={},X-AppCode={}, doControlPresetReq={}",
				deviceCode, channelCode, customerCode, userCode, appCode, doControlPresetReq);
		ResultEntity result = presetNewClient.doControlPreset(deviceCode, channelCode, appCode, customerCode, userCode, doControlPresetReq);
		log.info("========doControlPreset结果：" + result);
		return parseRespResultNew(result);
	}


	/**
	 * 查询设备视场角
	 * @param videoVO
	 * @param request
	 * @return
	 */
	public RespResult getAngByDeviceParam(@RequestBody ChannelVideoVO videoVO, HttpServletRequest request) {
		if (StringUtils.isEmpty(videoVO.getDeviceCode()) || StringUtils.isEmpty(videoVO.getChannelCode())) {
            return RespResult.FAIL("请求失败", "1002", "设备和通道编码不能为空");
        }
		RespResult results = queryPtzPosition(videoVO.getDeviceCode(), videoVO.getChannelCode(), request);
		if (!Objects.equals(results.getReturnCode(), "0000")) {
			return results;
		}
		if (Objects.isNull(results.getData())) {
			return RespResult.FAIL("请求失败", "1002", "查询设备视场角失败");
		}
		PresetData resultPtz = JSONObject.parseObject(JSONObject.toJSONString(results.getData()), PresetData.class);
		TranslnglatReq req = new TranslnglatReq();
		req.setDeviceCode(videoVO.getDeviceCode());
		req.setPan(String.valueOf(resultPtz.getPan()));
		req.setTilt(String.valueOf(resultPtz.getTilt()));
		req.setZoom(String.valueOf(resultPtz.getZoom()));
		Result result = receiveAlarmNewService.getAngleByDeviceParam(req);
		if (result.getCode() == ResultCode.SUCCESS.getCode()) {
			return RespResult.SUCCESS(result.getData());
		}
		return RespResult.FAIL("请求失败", "1002", result.getMsg());
	}
}














