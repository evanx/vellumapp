/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package venigma.server;

/**
 *
 * @author evan
 */
public enum CipherRequestType {
    PING,
    CHECK,
    ADD_USER,
    REGISTER_USER,
    REGISTER_CIPHER,
    CONFIRM_USER,
    LIST_EMPTY_PASSWORDS,
    SET_PASSWORD,
    ADD_KEY,
    GENERATE_KEY,
    REVISE_KEY,
    CONFIRM_KEY,
    START_SAFE_MODE,
    START,
    STOP,
    GRANT,
    REVOKE,
    ENCIPHER,
    DECIPHER,
    RECIPHER,
    
}
