package jelly.handler;

import vellum.jx.JAction;
import vellum.jx.JMapException;
import jelly.app.JellyApp;
import jelly.app.JellyHandler;

/**
 *
 * @author evan.summers
 */
public class registerAdminUser implements JellyHandler {
    JellyApp app;
    JAction action;
    String email;
    String name;
    char[] password;
    
    private void init() throws JMapException {
        email = action.getRequestMap().getString("email");
        name = action.getRequestMap().getString("name");
        password = action.getRequestMap().getChars("password");
    }
    
    @Override
    public void handle(JellyApp app, JAction action) throws Exception {
        this.app = app;
        this.action = action;
        init();
        app.getStorage().put("jelly", "AdminUser", email, action.getRequestMap());
    }
    
    
}
