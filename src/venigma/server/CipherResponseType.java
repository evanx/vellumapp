/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package venigma.server;

/**
 *
 * @author evan
 */
public enum CipherResponseType {
    PING,
    OK,
    ERROR,
    ERROR_CERT,
    ERROR_SUBJECT,
    ERROR_AUTH,
    ERROR_NOT_PROVIDER,
    ERROR_NOT_USER,
    ERROR_NOT_ADMIN,
    ERROR_NOT_STARTED,
    ERROR_USER_NOT_FOUND,
    ERROR_USER_ALREADY_REVOKED,
    ERROR_USER_ALREADY_GRANTED,
    ERROR_USER_ALREADY_EXISTS,
    ERROR_KEY_ALREADY_EXISTS,
    ERROR_NO_KEY_SIZE,
    ERROR_INVALID_KEY_SIZE,
    ERROR_KEY_NOT_FOUND,
}
