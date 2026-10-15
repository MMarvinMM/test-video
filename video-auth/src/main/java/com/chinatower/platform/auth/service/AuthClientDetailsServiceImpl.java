//package com.chinatower.platform.auth.service;
//
//
//
//import cn.hutool.core.collection.CollUtil;
//import com.chinatower.platform.auth.model.OauthClientInfo;
//import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
//import org.springframework.security.oauth2.provider.ClientDetails;
//import org.springframework.security.oauth2.provider.ClientDetailsService;
//import org.springframework.security.oauth2.provider.ClientRegistrationException;
//
//import javax.annotation.PostConstruct;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * 认证中心Oauth2.0客户端接口实现类
// * <p>
// * 此类中的clientId等同于 OauthClientInfo的 appKey
// *
// * @author bruce
// * @version v1.0
// * @date 2021/11/11
// */
//public class AuthClientDetailsServiceImpl implements ClientDetailsService {
//
//
//    private List<ClientDetails> clientList;
//
//    /**
//     * client初始化
//     * client目前可用于区分web登录和app登录，所以先写入缓存
//     */
//    @PostConstruct
//    public void initData() {
//        clientList = new ArrayList<>();
//        clientList.add(new OauthClientInfo("app", "123456"));
//        clientList.add(new OauthClientInfo("web", "123456"));
//    }
//
//    /**
//     * @param clientId String
//     * @return ClientDetails
//     * @throws ClientRegistrationException ClientRegistrationException
//     */
//    @Override
//    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
//            List<ClientDetails> findList = clientList.stream().filter(item -> item.getClientId().equals(clientId)).collect(Collectors.toList());
//            if (CollUtil.isEmpty(findList)) {
//                throw new InvalidClientException("client ID does not match authenticated client");
//            }
//            ClientDetails clientDetail = findList.get(0);
//            return clientDetail;
//    }
//
//}
