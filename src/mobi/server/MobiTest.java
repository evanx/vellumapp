/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mobi.server;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import mobi.servlets.MobiServlet;

/**
 *
 * @author evan.summers
 */
public class MobiTest {
 
    public static void main(String[] args) throws LifecycleException, InterruptedException, ServletException {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        Context ctx = tomcat.addContext("/", new File(".").getAbsolutePath());
        Tomcat.addServlet(ctx, "mobi", new MobiServlet()); 
        ctx.addServletMapping("/*", "mobi");
        tomcat.start();
        tomcat.getServer().await();
    }
    
}
