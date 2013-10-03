package jelly.app;

import jx.*;

/**
 *
 * @author evan.summers
 */
public interface JellyHandler {
    public void handle(JellyApp app, JxAction action) throws Exception;
}
