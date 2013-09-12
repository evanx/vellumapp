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

/**
 *
 * @author evan.summers
 */
public interface HttpExchange {
    
    public Headers getRequestHeaders();

    public Headers getResponseHeaders();

    public URI getRequestURI() throws URISyntaxException;

    public String getRequestMethod();

    public HttpContext getHttpContext();

    public void close();

    public InputStream getRequestBody() throws IOException;

    public OutputStream getResponseBody() throws IOException;

    public PrintWriter getWriter() throws IOException;
    
    public void sendResponseHeaders(int code, long length) throws IOException;

    public InetSocketAddress getRemoteAddress();

    public int getResponseCode();
        
    public InetSocketAddress getLocalAddress();
        
    public String getProtocol();
        
    public Object getAttribute(String string);
    
    public void setAttribute(String string, Object o);

    public void setStreams(InputStream in, OutputStream out);
    
    public HttpPrincipal getPrincipal();
    
}
