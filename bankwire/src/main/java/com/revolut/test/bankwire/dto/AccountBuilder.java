package com.revolut.test.bankwire.dto;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.util.Date;
import java.util.Optional;

public class AccountBuilder {
    private String accountNumber;
    private User user;
    private Money money;
    private CurrencyUnit currency;

    private Date createdDate;

    public AccountBuilder setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
        return this;
    }

    public AccountBuilder setUser(User user) {
        this.user = user;
        return this;
    }

    public AccountBuilder setMoney(Money money) {
        this.money = money;
        return this;
    }

    public AccountBuilder setCurrency(CurrencyUnit currency) {
        this.currency = currency;
        return this;
    }

    public AccountBuilder setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public Optional<Account> createAccountOptional() {
        return Optional.of(new Account(accountNumber, user, money, currency, createdDate));
    }

    public Account createAccount() {
        return new Account(accountNumber, user, money, currency, createdDate);
    }
}