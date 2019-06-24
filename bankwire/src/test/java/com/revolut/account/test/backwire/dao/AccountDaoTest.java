package com.revolut.account.test.backwire.dao;

import com.revolut.test.bankwire.dto.Account;
import com.revolut.test.bankwire.dto.AccountBuilder;
import com.revolut.test.bankwire.dto.User;
import com.revolut.test.bankwire.exception.AccountAlreadyExistsException;
import com.revolut.test.bankwire.exception.AccountNotFoundException;
import com.revolut.test.bankwire.repo.AccountDao;
import com.revolut.test.bankwire.repo.AccountDaoImpl;
import org.jetbrains.annotations.TestOnly;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountDaoTest {

    private AccountDao accountDao;

    @Before
    public void setUp() {
        accountDao = new AccountDaoImpl();
    }

    @After
    public void tearDown() {
        accountDao.clearAccounts();
    }


    @Test
    public void findByAccountNumber_Exist_Failure() {
        // given
        String accountNumber = "123";

        //when
        Optional<Account> account = accountDao.findByAccountNumber(accountNumber);

        //then
        assertFalse("account not Exist", account.isPresent());
    }

    @Test
    public void findByAccountNumber_Exist_Success() throws Exception {
        // given
        String accountNumber = "123";
        accountDao.createAccount(createAccountMock(accountNumber));

        //when
        Optional<Account> accountOptional = accountDao.findByAccountNumber(accountNumber);

        //then
        assertTrue("Account Exist", accountOptional.isPresent());
    }

    @Test
    public void findAllAccounts_Success() throws Exception {
        //given
        accountDao.createAccount(createAccountMock());
        accountDao.createAccount(createAccountMock());

        //when
        List<Account> accounts = accountDao.findAllAccounts();

        //then
        assertThat(accounts.size(), is(2));
    }

    @Test
    public void clearAccounts_DeleteAll_Success() throws Exception {
        //given
        accountDao.createAccount(createAccountMock());
        accountDao.createAccount(createAccountMock());

        //when
        accountDao.clearAccounts();

        //then
        assertThat(accountDao.findAllAccounts().size(), is(0));
    }
    @Test
    public void createAccount_Success() throws Exception {

        //given
        Account account = createAccountMock();

        //when
        accountDao.createAccount(account);
        Optional<Account> accountOptional = accountDao.findByAccountNumber(account.getAccountNumber());

        //verify
        assertEquals(account, accountOptional.get());

    }

    @Test(expected = AccountAlreadyExistsException.class)
    public void createAccount_AccountAlreadyExists_Exception() throws Exception {

        //given
        Account account = createAccountMock();
        accountDao.createAccount(account);

        //when
        accountDao.createAccount(account);

    }

    @Test
    public void deleteAccount_Success() throws Exception {

        //given
        String accountNumber = "9870";
        accountDao.createAccount(createAccountMock(accountNumber));

        //when
        accountDao.deleteAccount(accountNumber);

        //verify
        assertFalse(accountDao.findByAccountNumber(accountNumber).isPresent());

    }

    @Test(expected = AccountNotFoundException.class)
    public void deleteAccount_AccountNotFound_Exception() throws Exception {

        //given
        String accountNumber = "9870";

        //when
        accountDao.deleteAccount(accountNumber);

        //verify


    }

    @Test
    public void creditAccount_AccountCredited_Success() throws Exception {

        //given
        String accountNumber = "9870";
        Account account = createAccountMock(accountNumber);
        accountDao.createAccount(account);
        Money money = Money.of(CurrencyUnit.EUR, new BigDecimal("10"));
        Money balanceAfterDeduct = Money.of(CurrencyUnit.EUR, new BigDecimal("110"));


        //when
        accountDao.creditAccount(account, money);

        //verify
        assertEquals(accountDao.findByAccountNumber("9870").get().getMoney(), balanceAfterDeduct);
    }

    @Test(expected = AccountNotFoundException.class)
    public void creditAccount_AccountNotFound_Exception() throws Exception {

        //given
        String accountNumber = "9870";
        Account account = createAccountMock(accountNumber);
        Money money = Money.of(CurrencyUnit.EUR, new BigDecimal("10"));


        //when
        accountDao.creditAccount(account, money);

        //verify
    }


    @Test
    public void deductAccount_AccountDeducted_Success() throws Exception {

        //given
        String accountNumber = "9870";
        Account account = createAccountMock(accountNumber);
        accountDao.createAccount(account);
        Money money = Money.of(CurrencyUnit.EUR, new BigDecimal("10.00"));
        Money balanceAfterDeduct = Money.of(CurrencyUnit.EUR, new BigDecimal("90"));


        //when
        accountDao.deductAccount(account, money);

        //verify
        assertEquals(accountDao.findByAccountNumber(accountNumber).get().getMoney(), balanceAfterDeduct);
    }

    @Test(expected = AccountNotFoundException.class)
    public void deductAccount_AccountNotFound_Exception() throws Exception {

        //given
        String accountNumber = "9870";
        Account account = createAccountMock(accountNumber);
        Money money = Money.of(CurrencyUnit.EUR, new BigDecimal("10.00"));


        //when
        accountDao.deductAccount(account, money);

        //verify
    }


    private Account createAccountMock() {
        return new AccountBuilder().setUser(new User("test","test1"))
                .setAccountNumber(UUID.randomUUID().toString())
                .setCurrency(CurrencyUnit.EUR)
                .setMoney(Money.of(CurrencyUnit.EUR, 100))
                .setCreatedDate(new Date())
                .createAccount();
    }

    private Account createAccountMock(String accountNumber) {
        return new AccountBuilder().setUser(new User("test","test1"))
                .setAccountNumber(accountNumber)
                .setCurrency(CurrencyUnit.EUR)
                .setMoney(Money.of(CurrencyUnit.EUR, 100))
                .setCreatedDate(new Date())
                .createAccount();
    }
}
