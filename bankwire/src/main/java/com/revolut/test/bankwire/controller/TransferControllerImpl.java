package com.revolut.test.bankwire.controller;


import com.revolut.test.bankwire.context.ContextWrapper;
import com.revolut.test.bankwire.dto.Account;
import com.revolut.test.bankwire.dto.Transfer;
import com.revolut.test.bankwire.dto.TransferBuilder;
import com.revolut.test.bankwire.repo.AccountDao;
import com.revolut.test.bankwire.repo.TransferDao;
import com.revolut.test.bankwire.validation.Validation;
import io.javalin.Context;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;

public class TransferControllerImpl implements TransferController {

    private TransferDao transferDao;

    private ContextWrapper contextWrapper;

    private AccountDao accountDao;

    @Inject
    public TransferControllerImpl(ContextWrapper contextWrapper, TransferDao transferDao, AccountDao accountDao) {
        this.transferDao = transferDao;
        this.contextWrapper = contextWrapper;
        this.accountDao = accountDao;
    }


    @Override
    public void findTransferById(Context context) {

        String transferId = contextWrapper.pathParam(context, "id");

        Optional<Transfer> transfer = transferDao.findByTransferId(transferId);
        if (transfer.isPresent()) {
            contextWrapper.json(context, transfer.get(), 200);
        } else {
            contextWrapper.json(context, String.format("transfer with id %s not found ", transferId), 404);
        }
    }

    @Override
    public void getAllTransfers(Context context) {
        Queue<Transfer> transfers = transferDao.findAllTransfers();
        contextWrapper.json(context, transfers, 200);

    }

    @Override
    public void transferMoney(Context context) {

        String senderAccountNumber = contextWrapper.formParam(context, "senderAccountNumber");
        String receiverAccountNumber = contextWrapper.formParam(context, "receiverAccountNumber");
        String moneyStr = contextWrapper.formParam(context, "money");

        try {
            Validation.validateMoneyTransferRequestParam(senderAccountNumber, receiverAccountNumber);

            Optional<Account> senderAccountOptional = accountDao.findByAccountNumber(senderAccountNumber);
            Optional<Account> receiverAccountOptional = accountDao.findByAccountNumber(receiverAccountNumber);
            Validation.validateMoneyTransfer(senderAccountOptional, receiverAccountOptional);

            Optional<Transfer> transferOptional = createTransferObject(context, senderAccountOptional.get().getCurrency());
            if (!transferOptional.isPresent()) {
                contextWrapper.json(context, String.format("Money format invalid", moneyStr), 400);
                return;
            }

            Transfer transfer = transferDao.transferMoney(transferOptional.get());
            contextWrapper.json(context, transfer, 200);

        } catch (
                Exception exception) {
            contextWrapper.json(context, exception.getMessage(), 400);
        }
    }

    private Optional<Money> parseMoney(Context context, CurrencyUnit currencyUnit) {
        try {
            String money = contextWrapper.formParam(context, "money");
            return Optional.of(Money.of(currencyUnit, new BigDecimal(money)));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    private Optional<Transfer> createTransferObject(Context context, CurrencyUnit currencyUnit) {
        String senderAccountNumber = contextWrapper.formParam(context, "senderAccountNumber");
        String receiverAccountNumber = contextWrapper.formParam(context, "receiverAccountNumber");
        return parseMoney(context, currencyUnit).map(money -> new TransferBuilder().setTransferId(UUID.randomUUID().toString()).setCurrency(currencyUnit).setMoney(money).setFromAccountNumber(senderAccountNumber).setToAccountNumber(receiverAccountNumber).setCreatedDate(new Date()).createTransfer());
    }
}
