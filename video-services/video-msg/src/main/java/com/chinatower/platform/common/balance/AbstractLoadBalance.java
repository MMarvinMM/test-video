package com.chinatower.platform.common.balance;

import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * 负载算法抽象类
 * 目的：把通过功能给封装起来
 */
public abstract class AbstractLoadBalance implements ILoadBalance {

	@Override
    public String select(Map<String,Integer> map) {
		if(CollectionUtils.isEmpty(map)){
            return null;
        }
        if(map.size()==1){
            return map.keySet().iterator().next();
        }
        return doSelect(map);
	}
	
	protected abstract String doSelect(Map<String,Integer> map);
}
