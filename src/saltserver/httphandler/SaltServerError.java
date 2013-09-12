/*
 */
package saltserver.httphandler;

import vellum.exception.DisplayMessage;

/**
 *
 * @author evan.summers
 */
public enum SaltServerError implements DisplayMessage {
    INVALID_ARGS;
    
    @Override
    public String getDisplayMessage() {
        return name();
    }
}
