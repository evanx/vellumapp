/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package crocserver.httphandler.secure;

import bizstat.enumtype.NotifyType;
import crocserver.storage.servicerecord.ServiceRecord;
import bizstat.enumtype.ServiceStatus;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import crocserver.app.CrocApp;
import crocserver.exception.CrocError;
import crocserver.exception.CrocException;
import crocserver.exception.CrocExceptionType;
import vellum.httpserver.Httpx;
import crocserver.notify.ServiceRecordProcessor;
import crocserver.storage.clientcert.Cert;
import java.io.IOException;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import vellum.util.Streams;
import crocserver.storage.common.CrocStorage;
import vellum.parameter.StringMap;

/**
 *
 * @author evan.summers
 */
public class PostHandler implements HttpHandler {

    Logr logger = LogrFactory.getLogger(getClass());
    CrocApp app;
    CrocStorage storage;
    HttpExchange httpExchange;
    Httpx httpExchangeInfo;
    String certName;
    String serviceName;
    String serviceText;
    ServiceRecord newRecord;
    ServiceStatus serviceStatus = ServiceStatus.UNKNOWN;
    NotifyType notifyType;
    StringMap pathParameterMap = new StringMap();

    public PostHandler(CrocApp app) {
        super();
        this.app = app;
        this.storage = app.getStorage();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        this.httpExchange = httpExchange;
        httpExchangeInfo = new Httpx(httpExchange);
        httpExchange.getResponseHeaders().set("Content-type", "text/plain");
        serviceText = Streams.readString(httpExchange.getRequestBody());
        if (httpExchangeInfo.getPathLength() < 3) {
            httpExchangeInfo.handleError(new CrocError(CrocExceptionType.INVALID_ARGS, httpExchangeInfo.getPath()));
        } else {
            certName = httpExchangeInfo.getPathString(1);
            serviceName = httpExchangeInfo.getPathString(2);
            httpExchangeInfo.parsePathParameters(pathParameterMap, 3);
            try {
                handle();
            } catch (Exception e) {
                httpExchangeInfo.handleError(e);
            }
        }
        httpExchange.close();
    }

    private void handle() throws Exception {
        newRecord = new ServiceRecord(certName, serviceName);
        newRecord.parseOutText(serviceText);
        ServiceRecordProcessor processor = new ServiceRecordProcessor(app);
        String notifyName = pathParameterMap.get(NotifyType.class.getSimpleName());
        if (notifyName != null) {
            notifyType = NotifyType.valueOf(notifyName);
            ServiceRecord previousRecord = storage.getServiceRecordStorage().findLatest(certName, serviceName);
            processor.process(notifyType, previousRecord, newRecord);
            logger.info("notify", processor.isNotify());
        }
        Cert cert = storage.getCertStorage().findName(certName);
        if (cert == null) {
            throw new CrocException(CrocExceptionType.NOT_FOUND, certName);
        }
        storage.getServiceRecordStorage().insert(cert.getOrgId(), newRecord);
        httpExchangeInfo.write(newRecord.getStringMap());
    }
}
