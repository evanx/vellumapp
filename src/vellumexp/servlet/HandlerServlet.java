/*
 * Copyright 2011, Evan Summers
 * Apache Software License 2.0
 */
package vellumexp.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author evan.summers
 */
public class HandlerServlet extends HttpServlet {

   HandlerFactory handlerFactory;

    public HandlerServlet(HandlerFactory handlerFactory) {
        this.handlerFactory = handlerFactory;
    }
            
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {    
        try {
            HttpHandler handler = handlerFactory.newHandler();
            HttpExchange httpExchange = new ServletHttpExchange(request, response);
            handler.handle(httpExchange);
        } catch (Exception e) {
            throw new ServletException(e);
        }  
    }
}
