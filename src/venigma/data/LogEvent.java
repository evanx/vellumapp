/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package venigma.data;

import java.util.Date;
import venigma.entity.IdEntity;
import venigma.server.CipherRequest;
import venigma.server.CipherRequestType;
import venigma.server.CipherResponseType;

/**
 *
 * @author evan.summers
 */
public class LogEvent implements IdEntity {
    Long id;
    Date timestamp;
    String message;
    CipherRequestType requestType;
    CipherResponseType responseType;
    CipherRequest request;
            
    public LogEvent() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
        
    public void setId(Long id) {
        this.id = id;
    }
    
    @Override
    public Comparable getId() {
        return id;
    }
    
}
