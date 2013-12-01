package jelly.app;

import vellum.jx.JAction;

/**
 *
 * @author evan.summers
 */
public interface JellyHandler {
    public void handle(JellyApp app, JAction action) throws Exception;
}
