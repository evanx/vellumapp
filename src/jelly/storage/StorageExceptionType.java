/*
 */
package jelly.storage;

/**
 *
 * @author evan.summers
 */
public enum StorageExceptionType {
    NOT_FOUND;
    
    public StorageException exception(Object... args) {
        return new StorageException(this, args);
    }
}
