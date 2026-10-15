package com.chinatower.platform.handler;

import com.chinatower.common.core.exception.NodeNotFoundException;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public class NodeNotFoundExceptionHandler extends DefaultErrorWebExceptionHandler {

    public NodeNotFoundExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties, ErrorProperties errorProperties, ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, errorProperties, applicationContext);
    }

    @Override
    protected Mono<ServerResponse> renderErrorResponse(ServerRequest request) {

        Map<String, Object> responseBodyMap = new HashMap<>();

        Throwable error = getError(request);
        if (error instanceof NodeNotFoundException) {
            NodeNotFoundException exception = (NodeNotFoundException) error;
            responseBodyMap.put("code", exception.getCode());
            responseBodyMap.put("msg", exception.getMessage());
            responseBodyMap.put("data", null);
            return ServerResponse
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(responseBodyMap));
        }
        responseBodyMap = this.getErrorAttributes(request, this.getErrorAttributeOptions(request, MediaType.ALL));
        return ServerResponse.status(this.getHttpStatus(responseBodyMap)).contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(error));
    }
}
