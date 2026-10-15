package com.chinatower.product.api.modules.preset.api;

import com.chinatower.product.api.modules.common.RespResult;
import com.chinatower.product.api.modules.preset.entity.ChannelVideoVO;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @description: 预置点API
 * @author: wanglei
 * @create: 2021/11/19 09:24
 */
public interface PresetAPI {

	/**
	 * 增加预置点
	 * @param deviceCode
	 * @param channelCode
	 * @param map
	 * @param request
	 * @return
	 */
	@PostMapping("/preset/devices/{deviceCode}/channels/{channelCode}/preset/addCameraPreset")
    RespResult addCameraPreset(String deviceCode, String channelCode, Map map, HttpServletRequest request);

	/**
	 * 删除预置点
	 * @param deviceCode
	 * @param channelCode
	 * @param presetId
	 * @param request
	 * @return
	 */
	@PostMapping("/preset/devices/{deviceCode}/channels/{channelCode}/preset/{presetId}/delCameraPreset")
    RespResult delCameraPreset(String deviceCode, String channelCode, Integer presetId, HttpServletRequest request);

	/**
	 * 修改预置点
	 * @param deviceCode
	 * @param channelCode
	 * @param map
	 * @param request
	 * @return
	 */
	@PostMapping("/preset/devices/{deviceCode}/channels/{channelCode}/updatePresetInfo")
    RespResult updatePresetInfo(String deviceCode, String channelCode, Map map, HttpServletRequest request);

	/**
	 * 查询预置点列表
	 * @param deviceCode
	 * @param channelCode
	 * @param request
	 * @return
	 */
	@PostMapping("/preset/devices/{deviceCode}/channels/{channelCode}/queryPresetList")
    RespResult queryPresetList(String deviceCode, String channelCode, HttpServletRequest request);

	/**
	 * 转到预置点
	 * @param deviceCode
	 * @param channelCode
	 * @param map
	 * @param request
	 * @return
	 */
	@PostMapping("/preset/devices/{deviceCode}/channels/{channelCode}/doControlPreset")
    RespResult doControlPreset(String deviceCode, String channelCode, Map map, HttpServletRequest request);

	/**
	 * 3D定位
	 * @param deviceCode
	 * @param channelCode
	 * @param map
	 * @param request
	 * @return
	 */
	@PostMapping("/plat/devices/{deviceCode}/channels/{channelCode}/ptz/thirdLocation")
    RespResult thirdLocation(String deviceCode, String channelCode, Map map, HttpServletRequest request);

	/**
	 * 抓图
	 * @param deviceCode
	 * @param channelCode
	 * @param map
	 * @param request
	 * @return
	 */
	@PostMapping("/plat/devices/{deviceCode}/channels/{channelCode}/snapshot")
    RespResult snapshot(String deviceCode, String channelCode, Map map, HttpServletRequest request);

	/**
	 * 查询通道的录像列表
	 * @param deviceCode
	 * @param channelCode
	 * @param map
	 * @param request
	 * @return
	 */
	@PostMapping("/devices/{deviceCode}/channels/{channelCode}/queryVideoList")
    RespResult queryVideoList(String deviceCode, String channelCode, Map map, HttpServletRequest request);

	/**
	 * 查询PTZ位置
	 * @param deviceCode
	 * @param channelCode
	 * @param request
	 * @return
	 */
	@PostMapping("/plat/devices/{deviceCode}/channels/{channelCode}/ptz/queryPtzPosition")
    RespResult queryPtzPosition(String deviceCode, String channelCode, HttpServletRequest request);

	/**
	 * 转到指定PTZ位置
	 * @param deviceCode
	 * @param channelCode
	 * @param map
	 * @param request
	 * @return
	 */
	@PostMapping("/plat/devices/{deviceCode}/channels/{channelCode}/ptz/doPtzLocationCmd")
    RespResult doPtzLocationCmd(String deviceCode, String channelCode, Map map, HttpServletRequest request);

	/**
	 * 获取加密后的OBS文件地址
	 * @param map
	 * @return
	 */
	@PostMapping("/file/obsFiles/getObsEncryptUrls")
    RespResult getObsEncryptUrls(Map<String,Object> map);


	/**
	 * 查询设备视场角
	 * @param videoVO
	 * @param request
	 * @return
	 */
	@PostMapping("/openApi/getAngByDeviceParam")
	RespResult getAngByDeviceParam(ChannelVideoVO videoVO, HttpServletRequest request);
}














