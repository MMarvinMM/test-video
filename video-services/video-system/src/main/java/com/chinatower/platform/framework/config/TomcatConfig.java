package com.chinatower.platform.framework.config;

import org.apache.catalina.Context;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {

	@Bean
	public TomcatServletWebServerFactory servletContainer() {
		return new TomcatServletWebServerFactory () {
			@Override
			protected void postProcessContext(Context context) {
				SecurityCollection collection = new SecurityCollection();
				collection.addPattern("/*");
				//添加需要排除的安全的方法
				collection.addOmittedMethod("GET");
				collection.addOmittedMethod("POST");
				collection.addOmittedMethod("PUT");
				collection.addOmittedMethod("DELETE");

				SecurityConstraint securityConstraint = new SecurityConstraint();
				//设置用户约束为保密 （CONFIDENTIAL、NONE、INTEGRAL） 如果使用了禁用的方法会报ERR_CERT_AUTHORITY_INVALID错误
				securityConstraint.setUserConstraint("CONFIDENTIAL");
				securityConstraint.addCollection(collection);
				context.addConstraint(securityConstraint);
			}
		};
	}

}
