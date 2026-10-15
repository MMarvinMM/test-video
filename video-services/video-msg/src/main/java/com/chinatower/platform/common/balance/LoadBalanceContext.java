package com.chinatower.platform.common.balance;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LoadBalanceContext implements ApplicationContextAware {
	
	@Value("${loadbalance}")
	private String loadbalance;
	private ApplicationContext applicationContext;

	public String select(Map<String,Integer> map){
		Object obj=applicationContext.getBean(loadbalance);
		if(obj!=null){
			ILoadBalance lb=(ILoadBalance) obj;
			return lb.select(map);
		}
		return null;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext=applicationContext;
	}
}
