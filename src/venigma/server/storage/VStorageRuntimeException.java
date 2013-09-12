/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package venigma.server.storage;

/**
 *
 * @author evan
 */
public class VStorageRuntimeException extends RuntimeException {
    VStorageExceptionType storageExceptionType;

    public VStorageRuntimeException(VStorageExceptionType storageExceptionType, Throwable exception) {
        super(VStorageExceptions.formatMessage(exception, storageExceptionType), exception);
        this.storageExceptionType = storageExceptionType;
    }
    
    public VStorageRuntimeException(VStorageExceptionType storageExceptionType) {
        super(VStorageExceptions.formatMessage(storageExceptionType));
        this.storageExceptionType = storageExceptionType;
    }

    public VStorageRuntimeException(VStorageExceptionType storageExceptionType, Object ... args) {
        super(VStorageExceptions.formatMessage(storageExceptionType, args));
        this.storageExceptionType = storageExceptionType;
    }
    
    public VStorageExceptionType getStorageExceptionType() {
        return storageExceptionType;
    }
        
}
