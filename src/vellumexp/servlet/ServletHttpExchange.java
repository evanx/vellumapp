/*
 * Copyright 2011, Evan Summers
 * Apache Software License 2.0
 */
package vellumexp.servlet;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpPrincipal;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author evan.summers
 */
public class ServletHttpExchange implements HttpExchange {

    HttpServletRequest request;
    HttpServletResponse response;

    public ServletHttpExchange(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public Headers getRequestHeaders() {
        return new Headers();
    }

    public Headers getResponseHeaders() {
        return new Headers();
    }

    public URI getRequestURI() throws URISyntaxException {
        return new URI(request.getRequestURI());
    }

    public String getRequestMethod() {
        return request.getRequestURL().toString();
    }

    public PrintWriter getWriter() throws IOException {
        return response.getWriter();
    }
    
    public HttpContext getHttpContext() {
        return null;
    }

    public void close() {
    }

    public InputStream getRequestBody() throws IOException {
        return request.getInputStream();
    }

    public OutputStream getResponseBody() throws IOException {
        return response.getOutputStream();
    }

    public void sendResponseHeaders(int code, long length) throws IOException {
        response.setStatus(code);
        response.setContentLength((int) length);
    }

    public int getResponseCode() {
        return response.getStatus();
    }

    public InetSocketAddress getLocalAddress() {
        return new InetSocketAddress(request.getLocalName(), request.getLocalPort());
    }

    public InetSocketAddress getRemoteAddress() {
        return new InetSocketAddress(request.getRemoteHost(), request.getRemotePort());
    }

    
    public String getProtocol() {
        return request.getProtocol();
    }

    public Object getAttribute(String string) {
        return null;
    }

    public HttpPrincipal getPrincipal() {
        return null;
    }

    public void setAttribute(String string, Object o) {
    }

    public void setStreams(InputStream in, OutputStream out) {
    }
}
