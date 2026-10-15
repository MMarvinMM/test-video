package com.chinatower.video.modules.common.util;

import java.util.Iterator;

import com.alibaba.fastjson.JSONObject;

/**
 * @ClassName JsonPropUtil
 * @Description
 * @Author zhoushuai
 * @Date 2024/4/16
 **/
public class JsonPropUtil {
	
	//删除json中指定key并返回新json数据，并且不改变原json数据
	public static JSONObject removeKey(JSONObject data,String removeKey) {
		if (removeKey == null || data == null) {
			return data;
		}
		JSONObject newData = new JSONObject();
		for (String key : data.keySet()) {
			if (!removeKey.equals(key)) {
				newData.put(key, data.get(key));
			}
		}
		return newData;
	}

}
