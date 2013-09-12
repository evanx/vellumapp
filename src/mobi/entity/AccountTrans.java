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
public class AccountTrans {
    Long accountTransId;
    long debitAccountId;
    long creditAccountId;
    String description;
    String transType;
    String transStatus;
    Date requestTime;
    Date transTime;
    String currency;
    BigDecimal amount;
    
    public AccountTrans() {
    }

    public long getCreditAccountId() {
        return creditAccountId;
    }

    public void setCreditAccountId(long creditAccountId) {
        this.creditAccountId = creditAccountId;
    }

    public long getDebitAccountId() {
        return debitAccountId;
    }

    public void setDebitAccountId(long debitAccountId) {
        this.debitAccountId = debitAccountId;
    }

    public Long getAccountTransId() {
        return accountTransId;
    }

    public void setAccountTransId(Long accountTransId) {
        this.accountTransId = accountTransId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public String getTransStatus() {
        return transStatus;
    }

    public void setTransStatus(String transStatus) {
        this.transStatus = transStatus;
    }

    public Date getTransTime() {
        return transTime;
    }

    public void setTransTime(Date transTime) {
        this.transTime = transTime;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }  
}
