package com.chinatower.platform.common.balance;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * 负载随机算法
 */
@Component("randomweight")
public class RandomWeightLoadBalance extends AbstractLoadBalance{

	@Override
	protected String doSelect(Map<String, Integer> map) {
		ArrayList<String> lists = new ArrayList<String>();
		
		//根据权重来生成比重的数据放入List集合
		Iterator<String> iterator = map.keySet().iterator();
		while(iterator.hasNext()){
			String key=iterator.next();
			Integer weight=map.get(key);
			
			for(int i=0;i<weight;i++){
				lists.add(key);
			}
		}
		//开始随机
		int len=lists.size();
		SecureRandom random = new SecureRandom();
        int index=random.nextInt(len);
        
        return lists.get(index);
	}

}
