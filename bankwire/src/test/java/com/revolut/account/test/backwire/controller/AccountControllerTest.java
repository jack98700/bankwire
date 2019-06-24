package com.revolut.account.test.backwire.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.revolut.test.bankwire.context.ContextWrapper;
import com.revolut.test.bankwire.controller.AccountController;
import com.revolut.test.bankwire.dto.Account;
import com.revolut.test.bankwire.dto.AccountBuilder;
import com.revolut.test.bankwire.exception.AccountAlreadyExistsException;
import com.revolut.test.bankwire.exception.AccountNotFoundException;
import com.revolut.test.bankwire.repo.AccountDao;
import com.revolut.test.bankwire.controller.AccountControllerImpl;
import io.javalin.Context;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AccountControllerTest {

    private AccountController accountServiceImpl;

    @Mock
    private AccountDao accountDao;

    @Mock
    private Account account;

    @Mock
    private Context context;

    @Mock
    private ContextWrapper contextWrapper;

    @Before
    public void setUp() {
        accountServiceImpl = new AccountControllerImpl(contextWrapper, accountDao);
    }

    @Test
    public void findByAccountNumber_Exists_Success() {

        // given
        String accountNumber = "1";
        when(contextWrapper.pathParam(context, "id")).thenReturn(accountNumber);
        when(accountDao.findByAccountNumber(accountNumber))
                .thenReturn(Optional.of(account));

        // when
        accountServiceImpl.findByAccountNumber(context);

        // then
        verify(accountDao).findByAccountNumber(accountNumber);
        verify(contextWrapper).json(context, account, 200);
    }

    @Test
    public void findByAccountNumber_Exists_Failure() {

        // given
        String accountNumber = "2";
        when(contextWrapper.pathParam(context, "id")).thenReturn(accountNumber);
        when(accountDao.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        // when
        accountServiceImpl.findByAccountNumber(context);

        // then
        verify(accountDao).findByAccountNumber(accountNumber);
        verify(contextWrapper, atLeastOnce()).json(context, String.format("Account %s does not exist", accountNumber), 404);
    }


    @Test
    public void findAllAccounts_NotEmpty_Success() {

        // given
        List<Account> accounts = new ArrayList<Account>();
        when(accountDao.findAllAccounts()).thenReturn(accounts);

        // when
        accountServiceImpl.findAllAccounts(context);

        // then
        verify(accountDao, only()).findAllAccounts();
        verify(contextWrapper, atLeastOnce()).json(context, accounts, 200);
    }

    @Test
    public void findAllAccounts_Empty_Success() {

        // given
        when(accountDao.findAllAccounts()).thenReturn(null);

        // when
        accountServiceImpl.findAllAccounts(context);

        // then
        verify(accountDao, only()).findAllAccounts();
        verify(contextWrapper, atLeastOnce()).json(context, null, 200);

    }

    @Test
    public void createAccount_Success() throws Exception {

        //given
        when(contextWrapper.formParam(context, "firstName")).thenReturn("Test");
        when(contextWrapper.formParam(context, "lastName")).thenReturn("Test");
        when(contextWrapper.formParam(context, "money")).thenReturn("10");
        when(contextWrapper.formParam(context, "currencyCode")).thenReturn("EUR");

        // when
        accountServiceImpl.createAccount(context);

        //then
        verify(accountDao).createAccount(any(Account.class));


    }

    @Test
    public void createAccount_AccountAlreadyExists_Failure() throws Exception {

        //given
        String accountNumber = "abc";
        when(contextWrapper.formParam(context, "firstName")).thenReturn("Test");
        when(contextWrapper.formParam(context, "lastName")).thenReturn("Test");
        when(contextWrapper.formParam(context, "money")).thenReturn("10");
        when(contextWrapper.formParam(context, "currencyCode")).thenReturn("EUR");
        AccountAlreadyExistsException accountAlreadyExistsException = new AccountAlreadyExistsException(accountNumber);
        when(accountDao.createAccount(any(Account.class))).thenThrow(accountAlreadyExistsException);

        // when
        accountServiceImpl.createAccount(context);

        //then
        contextWrapper.json(context, String.format("Account %s already exists", accountNumber, 500));


    }


    @Test
    public void createAccount_InValidMoneyFormat_Failure() throws Exception {

        //given
        String money = "XXX";
        String currency = "EUR";
        when(contextWrapper.formParam(context, "firstName")).thenReturn("Test");
        when(contextWrapper.formParam(context, "lastName")).thenReturn("Test");
        when(contextWrapper.formParam(context, "money")).thenReturn(money);
        when(contextWrapper.formParam(context, "currencyCode")).thenReturn(currency);

        // when
        accountServiceImpl.createAccount(context);

        //then
        verify(accountDao, never()).createAccount(any(Account.class));
        contextWrapper.json(context, "Invalid money or currency format", 400);


    }

    @Test
    public void createAccount_InvalidFirstOrLastName_Failure() throws Exception {

        //given
        String money = "XXX";
        String currency = "EUR";
        when(contextWrapper.formParam(context, "money")).thenReturn(money);
        when(contextWrapper.formParam(context, "currencyCode")).thenReturn(currency);

        // when
        accountServiceImpl.createAccount(context);

        //then
        verify(accountDao, never()).createAccount(any(Account.class));
        contextWrapper.json(context, "First Name and Last Name are Mandatory", 400);


    }


    @Test
    public void createAccount_InvalidCurrencyFormat_Failure() throws Exception {

        //given
        String currency = "1234";
        String money = "10";
        when(contextWrapper.formParam(context, "firstName")).thenReturn("Test");
        when(contextWrapper.formParam(context, "lastName")).thenReturn("Test");
        when(contextWrapper.formParam(context, "money")).thenReturn(money);
        when(contextWrapper.formParam(context, "currencyCode")).thenReturn(currency);

        // when
        accountServiceImpl.createAccount(context);

        //then
        verify(accountDao, never()).createAccount(any(Account.class));
        contextWrapper.json(context, String.format("Invalid money %s or currency %s format ", money, currency), 500);


    }

    @Test
    public void deleteAccount_Exists_Success() throws Exception {

        //given
        String accountNumber = "abc";
        when(contextWrapper.pathParam(context, "id")).thenReturn(accountNumber);

        // when
        accountServiceImpl.deleteAccount(context);

        //then
        verify(accountDao).deleteAccount(accountNumber);
        verify(contextWrapper).json(context, String.format("Account %s deleted Successfully", accountNumber), 200);
    }

    @Test
    public void deleteAccount_Exists_Failure() throws  Exception{

        //given
        String accountNumber = "abc";
        AccountNotFoundException accountNotFoundException = new AccountNotFoundException(String.format("Account %s not Found", accountNumber));
        when(contextWrapper.pathParam(context, "id")).thenReturn(accountNumber);
        when(accountDao.deleteAccount(accountNumber)).thenThrow(accountNotFoundException);

        // when
        accountServiceImpl.deleteAccount(context);

        //then
        verify(contextWrapper).json(context, String.format("Account %s not Found", accountNumber), 400);
    }


}
