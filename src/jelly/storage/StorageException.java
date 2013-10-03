/*
 */
package jelly.storage;

/**
 *
 * @author evan.summers
 */
public class StorageException extends Exception {
    StorageExceptionType exceptionType;
    Object[] args;
    
    public StorageException(StorageExceptionType exceptionType, Object ... args) {
        super(exceptionType.name());
        this.exceptionType = exceptionType;
        this.args = args;
    }
    
}
