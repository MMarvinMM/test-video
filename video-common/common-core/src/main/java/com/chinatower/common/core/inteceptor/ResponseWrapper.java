package com.chinatower.common.core.inteceptor;

import org.springframework.http.HttpHeaders;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class ResponseWrapper extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    private PrintWriter printWriter;
    private ServletOutputStream servletOutputStream;
    private final Map<String, List<String>> headers = new HashMap<>();
    private String contentType;

    public ResponseWrapper(ServletResponse response) throws IOException {
        super((HttpServletResponse) response); // 在此假设传入的response是HttpServletResponse
        this.servletOutputStream = new CustomServletOutputStream(byteArrayOutputStream);
        this.printWriter = new PrintWriter(byteArrayOutputStream);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return servletOutputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return printWriter;
    }

    @Override
    public void flushBuffer() throws IOException {
        if (printWriter != null) {
            printWriter.flush();
        }
        if (servletOutputStream != null) {
            servletOutputStream.flush();
        }
    }

    public String getResponseBody() throws IOException {
        flushBuffer(); // 确保所有数据被写入
        return byteArrayOutputStream.toString(); // 使用默认字符集转换为字符串
    }

    public void copyBodyToResponse() throws IOException {
        ServletResponse originalResponse = getResponse();
        if (originalResponse instanceof HttpServletResponse) {
            HttpServletResponse httpResponse = (HttpServletResponse) originalResponse;
            httpResponse.getOutputStream().write(byteArrayOutputStream.toByteArray());
        }
    }

    @Override
    public void setHeader(String name, String value) {
        headers.put(name, Collections.singletonList(value));
        super.setHeader(name, value);
    }

    @Override
    public void addHeader(String name, String value) {
        headers.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
        super.addHeader(name, value);
    }

    @Override
    public String getHeader(String name) {
        List<String> values = headers.get(name);
        return values != null && !values.isEmpty() ? values.get(0) : null;
    }

    @Override
    public Collection<String> getHeaderNames() {
        return headers.keySet();
    }

    @Override
    public Collection<String> getHeaders(String name) {
        List<String> values = headers.get(name);
        return values != null ? values : Collections.emptyList();
    }

    @Override
    public void setContentType(String type) {
        this.contentType = type;
        super.setContentType(type);
    }

    @Override
    public String getContentType() {
        return this.contentType != null ? this.contentType : super.getContentType();
    }

    private static class CustomServletOutputStream extends ServletOutputStream {
        private final ByteArrayOutputStream stream;

        public CustomServletOutputStream(ByteArrayOutputStream stream) {
            this.stream = stream;
        }

        @Override
        public void write(int b) throws IOException {
            stream.write(b);
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            // No-op
        }
    }
}
