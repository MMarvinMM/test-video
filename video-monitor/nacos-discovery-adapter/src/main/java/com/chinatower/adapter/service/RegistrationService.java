

package com.chinatower.adapter.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import com.chinatower.adapter.model.ChangeItem;

import rx.Single;

/**
 * Returns Services and List of Service with its last changed
 */
@Component
public class RegistrationService {

	private static final String[] NO_SERVICE_TAGS = new String[0];

	private String final String STR_ADDRESS = "Address";

	private String final String STR_SECURE = "secure=";

	@Autowired
	private DiscoveryClient discoveryClient;

	public Single<ChangeItem<Map<String, String[]>>> getServiceNames(long waitMillis, Long index) {
		return returnDeferred(waitMillis, index, () -> {
//			访问/nacos/v1/ns/service/list  获取服务列表getServices()
			List<String> services = discoveryClient.getServices();
			Set<String> set = new HashSet<String>();
			set.addAll(services);
			Map<String, String[]> result = new HashMap<String, String[]>();
			for (String item : set) {
				result.put(item, NO_SERVICE_TAGS);
			}
			return result;
		});
	}

	public Single<ChangeItem<List<Map<String, Object>>>> getService(String appName, long waitMillis, Long index) {
		return returnDeferred(waitMillis, index, () -> {
//			访问/nacos/v1/ns/instance/list  获取实例列表 getInstances(appName)
			List<ServiceInstance> instances = discoveryClient.getInstances(appName);
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

			if (instances == null) {
				return Collections.emptyList();
			} else {
				Set<ServiceInstance> instSet = new HashSet<ServiceInstance>(instances);
				for (ServiceInstance instance : instSet) {
					String id = instance.getServiceId() + "-" +instance.getHost() + ":" + instance.getPort();

					Map<String, Object> ipObj = new HashMap<String, Object>();

					ipObj.put(STR_ADDRESS, instance.getHost());
					ipObj.put("Node", instance.getServiceId());
					ipObj.put("ServiceAddress", instance.getHost());
					ipObj.put("ServiceName", instance.getServiceId());
					ipObj.put("ServiceID", id);
					ipObj.put("ServicePort", instance.getPort());
					ipObj.put("NodeMeta", instance.getMetadata());
					ipObj.put("ServiceMeta", instance.getMetadata());
					ipObj.put("ServiceTags", Collections.singletonList(STR_SECURE+ instance.isSecure()));

					list.add(ipObj);
				}
				return list;
			}
		});
	}

	public Single<ChangeItem<List<Map<String, Object>>>> getHealth(String appName, long waitMillis, Long index) {
		return returnDeferred(waitMillis, index, () -> {
//			访问/nacos/v1/ns/instance/list  获取实例列表 getInstances(appName)
			List<ServiceInstance> instances = discoveryClient.getInstances(appName);
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

			if (instances == null) {
				return Collections.emptyList();
			} else {
				Set<ServiceInstance> instSet = new HashSet<ServiceInstance>(instances);
				for (ServiceInstance instance : instSet) {
					Map<String, Object> obj = new HashMap<String, Object>();
					String id = instance.getServiceId() + "-" +instance.getHost() + ":" + instance.getPort();

					Map<String, Object> nodeObj = new HashMap<String, Object>();
					nodeObj.put(STR_ADDRESS, instance.getHost());
					nodeObj.put("Node", instance.getServiceId());
					nodeObj.put("Meta", instance.getMetadata());
					obj.put("Node", nodeObj);

					Map<String, Object> serviceObj = new HashMap<String, Object>();
					serviceObj.put(STR_ADDRESS, instance.getHost());
					serviceObj.put("Service", instance.getServiceId());
					serviceObj.put("ID", id);
					serviceObj.put("Port", instance.getPort());
					serviceObj.put("Meta", instance.getMetadata());
					serviceObj.put("Tags", Collections.singletonList(STR_SECURE+ instance.isSecure()));
					obj.put("Service", serviceObj);

					Map<String, Object> checksObj = new HashMap<String, Object>();
					checksObj.put("Node", instance.getServiceId());
					checksObj.put("Name", "Service '" + instance.getServiceId() + "' check");
					checksObj.put("CheckID", "service:" + id);
					checksObj.put("Status", "passing");

					checksObj.put("ServiceID", id);
					checksObj.put("ServiceName", instance.getServiceId());
					checksObj.put("ServiceTags", Collections.singletonList(STR_SECURE+ instance.isSecure()));

					obj.put("Checks", Collections.singletonList(checksObj));
					list.add(obj);
				}
				return list;
			}
		});
	}

	private <T> Single<ChangeItem<T>> returnDeferred(long waitMillis, Long index, Supplier<T> fn) {
		return Single.just(new ChangeItem<>(fn.get(), System.currentTimeMillis()));
	}
}
