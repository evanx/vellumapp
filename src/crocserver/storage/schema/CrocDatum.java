/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package crocserver.storage.schema;

/**
 *
 * @author evan.summers
 */
public enum CrocDatum {

    ServiceRecord_service_record_id,
    ServiceRecord_host,
    ServiceRecord_service,
    ServiceRecord_time,
    ServiceRecord_notified_time,
    ServiceRecord_dispatched_time,
    ServiceRecord_status,
    ServiceRecord_exit_code,
    ServiceRecord_outText,
    ServiceRecord_errText,
    ;
    
    String column;

    CrocDatum(String column) {
        this.column = column;
    }

    CrocDatum() {
        this.column = parseColumn();
    }

    public String parseColumn() {
        int index = name().indexOf("_");
        return name().substring(index + 1);
    }
}
