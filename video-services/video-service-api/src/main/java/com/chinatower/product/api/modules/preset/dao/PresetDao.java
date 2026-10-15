package com.chinatower.product.api.modules.preset.dao;

import com.chinatower.video.api.entity.VideoPresetDO;

import java.util.List;
import java.util.Map;

public interface PresetDao {

	public List<String> queryErrorCode(Map map);

	VideoPresetDO selectById(String presetId,String deviceCode);
	
}
