package com.chinatower.platform.filter;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.constant.CompTechConstant;
import com.chinatower.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Description 对请求的API调用过滤，记录接口的请求时间，方便日志审计、告警、分析等运维操作
 * @author zheng.li
 * @date 2021/8/4
 */
@Slf4j
@Component
public class ApiLoggingFilter implements GlobalFilter, Ordered {

	private static final String START_TIME = "startTime";

	private static final String X_REAL_IP = "X-Real-IP";

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String requestUrl = exchange.getRequest().getURI().getRawPath();
		String traceId = exchange.getRequest().getHeaders().getFirst(CompTechConstant.TOWER_TRACE_ID_HEADER);
		StringBuilder beforeReqLog = new StringBuilder(300);
		// 日志参数
		List<Object> beforeReqArgs = new ArrayList<>();
		beforeReqLog.append("\n\n================ Gateway Request Start  ================\n");
		// 打印路由
		beforeReqLog.append("===> 请求方式:{}, 请求接口地址:{}\n");
		// 参数
		String requestMethod = exchange.getRequest().getMethodValue();
		beforeReqArgs.add(requestMethod);
		beforeReqArgs.add(requestUrl);

		// 打印请求头
		HttpHeaders headers = exchange.getRequest().getHeaders();
		headers.forEach((headerName, headerValue) -> {
			beforeReqLog.append("===Headers===  {}: {}\n");
			beforeReqArgs.add(headerName);
			beforeReqArgs.add(StringUtils.join(headerValue));
		});

		HandlerMethod handlerMethod = (HandlerMethod) exchange.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);

		if (Objects.nonNull(handlerMethod)) {
			RequestMapping requestMapping = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), RequestMapping.class);
			requestMapping.name();
			log.info("requestMapping.name():{}", requestMapping.name());
			log.info("requestMapping.path():{}", requestMapping.path());
			log.info("requestMapping.value():{}", requestMapping.value());
		}
		beforeReqLog.append("================ Gateway Request End =================\n");
		// 打印执行时间
		log.info(beforeReqLog.toString(), beforeReqArgs.toArray());
		exchange.getAttributes().put(START_TIME, System.currentTimeMillis());
		return chain.filter(exchange).then(Mono.fromRunnable(() -> {
			ServerHttpResponse response = exchange.getResponse();
			Long startTime = exchange.getAttribute(START_TIME);
			if (startTime != null) {
				Long executeTime = (System.currentTimeMillis() - startTime);
				// 构建成一条长 日志，避免并发下日志错乱
				StringBuilder responseLog = new StringBuilder(300);
				// 日志参数
				List<Object> responseArgs = new ArrayList<>();
				responseLog.append("\n\n================ Gateway Response Start  ================\n");
				// 打印路由
				responseLog.append("<=== 请求方式:{},请求接口地址:{},响应状态码:{},请求耗时:{}\n");
				// 参数
				responseArgs.add(requestMethod);
				responseArgs.add(requestUrl);
				int code = response.getStatusCode() != null
						? response.getStatusCode().value() : 500;
				responseArgs.add(code);
				responseArgs.add(executeTime + "ms");

				// 打印请求头
				HttpHeaders httpHeaders = response.getHeaders();
				httpHeaders.forEach((headerName, headerValue) -> {
					responseLog.append("===Headers===  {}: {}\n");
					responseArgs.add(headerName);
					responseArgs.add(StringUtils.join(headerValue));
				});

				responseLog.append("================ Gateway Response End  =================\n");
				// 打印执行时间
				log.info(responseLog.toString(), responseArgs.toArray());
			}
		}));
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

}
