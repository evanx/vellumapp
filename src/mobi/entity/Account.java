/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package mobi.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author evan.summers
 */
public class Account {
    Long accountId;
    String description;
    BigDecimal balance;
    String balanceCurrency;
    Long balanceAccountTransId;
    Date timeCreated;
            
    public Account() {
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Long getBalanceAccountTransId() {
        return balanceAccountTransId;
    }

    public void setBalanceAccountTransId(Long balanceAccountTransId) {
        this.balanceAccountTransId = balanceAccountTransId;
    }

    public String getBalanceCurrency() {
        return balanceCurrency;
    }

    public void setBalanceCurrency(String balanceCurrency) {
        this.balanceCurrency = balanceCurrency;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    
}
