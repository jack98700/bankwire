package com.revolut.test.bankwire.controller;

import com.revolut.test.bankwire.context.ContextWrapper;
import com.revolut.test.bankwire.dto.Account;
import com.revolut.test.bankwire.dto.AccountBuilder;
import com.revolut.test.bankwire.dto.User;
import com.revolut.test.bankwire.repo.AccountDao;
import io.javalin.Context;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AccountControllerImpl implements AccountController {

    private AccountDao accountDao;

    private ContextWrapper contextWrapper;

    @Inject
    public AccountControllerImpl(final ContextWrapper contextWrapper, final AccountDao accountDao) {
        this.contextWrapper = contextWrapper;
        this.accountDao = accountDao;
    }

    @Override
    public void findByAccountNumber(final Context context) {

        String accountNumber = contextWrapper.pathParam(context, "id");

        Optional<Account> account = accountDao.findByAccountNumber(contextWrapper.pathParam(context, "id"));

        if (account.isPresent()) {
            contextWrapper.json(context, account.get(), 200);
        } else {
            contextWrapper.json(context, String.format("Account %s does not exist", accountNumber), 404);
        }
    }


    @Override
    public void findAllAccounts(Context context) {
        // TODO Auto-generated method stub
        List<Account> accounts = accountDao.findAllAccounts();
        contextWrapper.json(context, accounts, 200);
    }

    @Override
    public void createAccount(Context context) {
        String firstName = contextWrapper.formParam(context, "firstName");
        String lastName = contextWrapper.formParam(context, "lastName");
        if (firstName == null || lastName == null) {
            contextWrapper.json(context, "First Name and Last Name are Mandatory", 400);
            return;
        }

        Optional<Account> accountOptional = createAccountObject(context);

        if (!accountOptional.isPresent()) {
            contextWrapper.json(context, "Invalid money or currency format", 400);
            return;
        }

        try {
            Account account = accountDao.createAccount(accountOptional.get());
            contextWrapper.json(context, account, 200);
        } catch (Exception exception) {
            contextWrapper.json(context, exception.getMessage(), 500);
        }


    }

    @Override
    public void deleteAccount(Context context) {

        String accountNumber = contextWrapper.pathParam(context, "id");

        try {
            accountDao.deleteAccount(accountNumber);
            contextWrapper.json(context, String.format("Account %s deleted Successfully", accountNumber), 200);
        } catch (Exception exception) {
            contextWrapper.json(context, exception.getMessage(), 400);
        }


    }

    private Optional<Money> parseMoney(Context context) {
        try {
            String money = contextWrapper.formParam(context, "money");
            String currency = contextWrapper.formParam(context, "currencyCode");
            return Optional.of(Money.of(CurrencyUnit.of(currency), new BigDecimal(money)));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    private Optional<Account> createAccountObject(Context context) {
        String firstName = contextWrapper.formParam(context, "firstName");
        String lastName = contextWrapper.formParam(context, "lastName");
        String currency = contextWrapper.formParam(context, "currencyCode");

        return parseMoney(context).map(money -> new AccountBuilder().setAccountNumber(UUID.randomUUID().toString())
                .setMoney(money).setCurrency(CurrencyUnit.of(currency)).setUser(new User(firstName, lastName)).setCreatedDate(new Date()).createAccount());

    }

}
