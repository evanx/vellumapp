/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package venigma.server.storage;

/**
 *
 * @author evan
 */
public enum VStorageExceptionType {
    CONNECTION_ERROR,
    ID_NULL,
    ENTITY_NULL,
    ENTITY_NOT_FOUND,
    UPDATE_COUNT,
    KEY_NULL,
    KEY_NOT_FOUND,
    KEY_NOT_INSERTED,
    KEY_NOT_UPDATED,
    KEY_NOT_DELETED,
    KEY_ALREADY_EXISTS,
    KEY_NOT_DECRYPTED,
    ID_NOT_FOUND,
    ID_ALREADY_EXISTS,
    PAIR_NOT_FOUND,
    PAIR_ALREADY_EXISTS,
    NO_DATABASE_STORE_PASSWORD,
    NO_DATABASE_NAME;
    
    public VStorageRuntimeException newRuntimeException() {
        return new VStorageRuntimeException(this);
    }
    
    public VStorageException newException() {
        return new VStorageException(this);
    }
    
}
