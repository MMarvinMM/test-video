package com.chinatower.platform.common.balance;

import java.util.Map;

/**
 * 负载算法接口
 */
public interface ILoadBalance {
	public String select(Map<String, Integer> map);
}
