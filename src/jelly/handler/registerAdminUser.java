package jelly.handler;

import jelly.app.JellyApp;
import jelly.app.JellyHandler;
import jx.*;

/**
 *
 * @author evan.summers
 */
public class registerAdminUser implements JellyHandler {
    JellyApp app;
    JxAction action;
    String email;
    String name;
    char[] password;
    
    private void init() throws JxMapException {
        email = action.getRequestMap().getString("email");
        name = action.getRequestMap().getString("name");
        password = action.getRequestMap().getChars("password");
    }
    
    @Override
    public void handle(JellyApp app, JxAction action) throws Exception {
        this.app = app;
        this.action = action;
        init();
        app.getStorage().put("jelly", "AdminUser", email, action.getRequestMap());
    }    
    
    
}
