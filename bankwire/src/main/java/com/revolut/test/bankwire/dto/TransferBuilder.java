package com.revolut.test.bankwire.dto;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.util.Date;
import java.util.Optional;

public class TransferBuilder {
    private String transferId;
    private String fromAccountNumber;
    private String toAccountNumber;
    private Money money;
    private CurrencyUnit currency;
    private Date createdDate;

    public TransferBuilder setTransferId(String transferId) {
        this.transferId = transferId;
        return this;
    }

    public TransferBuilder setFromAccountNumber(String fromAccountNumber) {
        this.fromAccountNumber = fromAccountNumber;
        return this;
    }

    public TransferBuilder setToAccountNumber(String toAccountNumber) {
        this.toAccountNumber = toAccountNumber;
        return this;
    }

    public TransferBuilder setMoney(Money money) {
        this.money = money;
        return this;
    }

    public TransferBuilder setCurrency(CurrencyUnit currency) {
        this.currency = currency;
        return this;
    }

    public TransferBuilder setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public Optional<Transfer> createTransferOptional() {
        return Optional.of(new Transfer(transferId, fromAccountNumber, toAccountNumber, money, currency, createdDate));
    }

    public Transfer createTransfer() {
        return new Transfer(transferId, fromAccountNumber, toAccountNumber, money, currency, createdDate);
    }
}