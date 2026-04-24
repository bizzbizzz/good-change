package org.best.backspringboot.config;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class XssRequestWrapper extends HttpServletRequestWrapper {

    public XssRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getParameter(String name) {
        return sanitize(super.getParameter(name));
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) return null;
        String[] sanitized = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            sanitized[i] = sanitize(values[i]);
        }
        return sanitized;
    }

    private String sanitize(String value) {
        if (value == null) return null;
        return value
                .replace("&",  "&amp;")
                .replace("<",  "&lt;")
                .replace(">",  "&gt;")
                .replace("\"", "&quot;")
                .replace("'",  "&#x27;")
                .replace("/",  "&#x2F;");
    }


    @Override
    public ServletInputStream getInputStream() throws IOException {
        InputStream in = super.getInputStream();
        String body = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        String sanitized = sanitize(body);
        byte[] bytes = sanitized.getBytes(StandardCharsets.UTF_8);
        return new ServletInputStream() {
            private final ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            public int read() { return bis.read(); }
            public boolean isFinished() { return bis.available() == 0; }
            public boolean isReady() { return true; }
            public void setReadListener(ReadListener l) {}
        };
    }
}