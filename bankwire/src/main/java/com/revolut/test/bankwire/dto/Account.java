package com.revolut.test.bankwire.dto;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Account {

    private String accountNumber;

    private User user;

    private Money money;

    private CurrencyUnit currency;

    private Date createdDate;


    private Lock lock;

    public Account(String accountNumber, User user, Money money, CurrencyUnit currency, Date createdDate) {
        this.accountNumber = accountNumber;
        this.user = user;
        this.money = money;
        this.currency = currency;
        this.createdDate = createdDate;
        this.lock = new ReentrantLock();
    }

    public Lock getLock() {
        return lock;
    }

    public void setLock(Lock lock) {
        this.lock = lock;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Money getMoney() {
        return money;
    }

    public void setMoney(Money money) {
        this.money = money;
    }

    public CurrencyUnit getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyUnit currency) {
        this.currency = currency;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
