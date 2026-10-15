package com.chinatower.platform.common.balance;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * 随机算法
 */
@Component("random")
public class RandomLoadBalance extends AbstractLoadBalance {

	@Override
	protected String doSelect(Map<String,Integer> map){
		ArrayList<String> lists = new ArrayList<String>();
		Set<String> sets=map.keySet();
	    lists.addAll(sets);
	    
		int len=lists.size();
		SecureRandom random = new SecureRandom();
        int index=random.nextInt(len);
        
        return lists.get(index);
	}

}
