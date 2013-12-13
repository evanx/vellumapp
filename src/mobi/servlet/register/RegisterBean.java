/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package mobi.servlet.register;

import java.security.GeneralSecurityException;
import vellum.util.Base64;
import vellum.crypto.PasswordSalts;
import vellum.crypto.Passwords;

/**
 *
 * @author evan.summers
 */
public class RegisterBean {
    String email;
    String name;
    String password;
    String confirmPassword;
    String passwordHash;
    byte[] salt = PasswordSalts.nextSalt();
    
    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String encodeSalt() {
        return Base64.encode(salt);
    }

    public String hashPassword() throws GeneralSecurityException {
        return Base64.encode(Passwords.hashPassword(password.toCharArray(), salt));
    }

    
}
