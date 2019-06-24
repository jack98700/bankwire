package com.revolut.test.bankwire.dto;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

public class Transfer {

    private String transferId;

    private String fromAccountNumber;

    private String toAccountNumber;

    private Money money;

    private CurrencyUnit currency;

    private Date createdDate;

    private transient AtomicBoolean running;

    public Transfer(String transferId, String fromAccountNumber, String toAccountNumber, Money money, CurrencyUnit currency,
                    Date createdDate) {
        super();
        this.transferId = transferId;
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.money = money;
        this.currency = currency;
        this.createdDate = createdDate;
        this.running = new AtomicBoolean(true);
    }

    public boolean isRunning() {
        return running.get();
    }

    public void setRunning(Boolean running) {
        this.running.set(running);
    }

    public String getTransferId() {
        return transferId;
    }

    public String getFromAccountNumber() {
        return fromAccountNumber;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }

    public Money getMoney() {
        return money;
    }

    public CurrencyUnit getCurrency() {
        return currency;
    }

    public Date getCreatedDate() {
        return createdDate;
    }


}
