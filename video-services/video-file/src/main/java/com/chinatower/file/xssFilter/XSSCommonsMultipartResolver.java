package com.chinatower.file.xssFilter;

import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;

public class XSSCommonsMultipartResolver extends CommonsMultipartResolver {
    private boolean resolveLazily = false;
    public XSSCommonsMultipartResolver() {
        super();
    }

    @Override
    public void setResolveLazily(boolean resolveLazily) {
        this.resolveLazily = resolveLazily;
    }

    @Override
    public MultipartHttpServletRequest resolveMultipart(final HttpServletRequest request) throws MultipartException {
        Assert.notNull(request, "Request must not be null");
        if (this.resolveLazily) {
            return new XSSMultipartHttpServletRequest(request) {
                @Override
                protected void initializeMultipart() {
                    MultipartParsingResult parsingResult = parseRequest(request);
                    setMultipartFiles(parsingResult.getMultipartFiles());
                    setMultipartParameters(parsingResult.getMultipartParameters());
                    setMultipartParameterContentTypes(parsingResult.getMultipartParameterContentTypes());
                }
            };
        } else {
            MultipartParsingResult parsingResult = parseRequest(request);
            return new XSSMultipartHttpServletRequest(request, parsingResult.getMultipartFiles(),
                    parsingResult.getMultipartParameters(), parsingResult.getMultipartParameterContentTypes());
        }
    }
}
