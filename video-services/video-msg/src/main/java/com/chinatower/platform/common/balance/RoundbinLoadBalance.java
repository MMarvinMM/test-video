package com.chinatower.platform.common.balance;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * 轮询算法
 */
@Component("roundbin")
public class RoundbinLoadBalance extends AbstractLoadBalance {
	private static int pos=0;//当前位置
	
    @Override
    protected String doSelect(Map<String,Integer> map){
    	ArrayList<String> lists = new ArrayList<String>();
		Set<String> sets=map.keySet();
	    lists.addAll(sets);
	    
    	String result="";
    	synchronized (RoundbinLoadBalance.class) {
			if(pos>=lists.size()){
				pos=0;
			}
			result=lists.get(pos);
			pos++;
		}
        
        return result;
    }
}
