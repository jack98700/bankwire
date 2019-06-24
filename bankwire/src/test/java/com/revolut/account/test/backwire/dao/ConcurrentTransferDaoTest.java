package com.revolut.account.test.backwire.dao;

import com.revolut.test.bankwire.dto.*;
import com.revolut.test.bankwire.repo.AccountDao;
import com.revolut.test.bankwire.repo.AccountDaoImpl;
import com.revolut.test.bankwire.repo.TransferDao;
import com.revolut.test.bankwire.repo.TransferDaoImpl;
import net.jodah.concurrentunit.Waiter;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ConcurrentTransferDaoTest {


    private Logger logger = LoggerFactory.getLogger(ConcurrentTransferDaoTest.class);

    private TransferDao transferDao;


    private AccountDao accountDao;

    private ExecutorService executorService;

    private Waiter waiter;

    private static final int POOL_SIZE = 3;

    @Before
    public void setUp() {
        accountDao = new AccountDaoImpl();
        transferDao = new TransferDaoImpl(accountDao);
        executorService = Executors.newFixedThreadPool(POOL_SIZE);
        waiter = new Waiter();
    }

    @After
    public void tearDown() {
        transferDao.clearTransfers();
    }

    @Test
    public void transferMoney_ConcurrentTransfers_AllSuccessful() throws Throwable {
        //given
        String senderAccountNumber = "abc";
        String receiverAccountNumber = "xyz";
        String transferId1 = "1";
        String transferId2 = "2";
        String transferId3 = "3";
        Account senderAccount = createAccountMock(senderAccountNumber, "100");
        Account receiverAccount = createAccountMock(receiverAccountNumber, "100");
        accountDao.createAccount(senderAccount);
        accountDao.createAccount(receiverAccount);
        Transfer transfer1 = createTransferMock(transferId1, "10", senderAccountNumber, receiverAccountNumber);
        Transfer transfer2 = createTransferMock(transferId2, "10", receiverAccountNumber, senderAccountNumber);
        Transfer transfer3 = createTransferMock(transferId3, "10", senderAccountNumber, receiverAccountNumber);

        // when
        executorService.submit(() -> commitTransfer(transfer1));
        executorService.submit(() -> commitTransfer(transfer2));
        executorService.submit(() -> commitTransfer(transfer3));
        waiter.await(5, TimeUnit.SECONDS, 3);

        //then
        assertThat(transferDao.findAllTransfers().size(), is(3));
        assertEquals(accountDao.findByAccountNumber(senderAccountNumber).get().getMoney(), Money.of(CurrencyUnit.EUR, new BigDecimal("90")));
        assertEquals(accountDao.findByAccountNumber(receiverAccountNumber).get().getMoney(), Money.of(CurrencyUnit.EUR, new BigDecimal("110")));

    }

    @Test
    public void transferMoney_ConcurrentTransfers_OnlyOneSuccessful() throws Throwable {
        //given
        String senderAccountNumber = "abc";
        String receiverAccountNumber = "xyz";
        String transferId1 = "1";
        String transferId2 = "2";
        Account senderAccount = createAccountMock(senderAccountNumber, "10");
        Account receiverAccount = createAccountMock(receiverAccountNumber, "10");
        accountDao.createAccount(senderAccount);
        accountDao.createAccount(receiverAccount);
        Transfer transfer1 = createTransferMock(transferId1, "10");
        Transfer transfer2 = createTransferMock(transferId2, "10");


        // when
        executorService.submit(() -> commitTransfer(transfer1));
        executorService.submit(() -> commitTransfer(transfer2));

        waiter.await(5, TimeUnit.SECONDS, 1);

        //then
        assertThat(transferDao.findAllTransfers().size(), is(1));
        assertEquals(accountDao.findByAccountNumber(senderAccountNumber).get().getMoney(), Money.of(CurrencyUnit.EUR, new BigDecimal("0")));
        assertEquals(accountDao.findByAccountNumber(receiverAccountNumber).get().getMoney(), Money.of(CurrencyUnit.EUR, new BigDecimal("20")));

    }

    public void commitTransfer(Transfer transfer) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(3000));
            transferDao.transferMoney(transfer);
            waiter.assertNotNull(transfer);
            logger.info("Thread Name %s", Thread.currentThread().getName());
            waiter.resume();
        } catch (Exception exception) {
            logger.error(exception.getMessage());
        }

    }


    public Account createAccountMock(String accountNumber, String money) {
        return new AccountBuilder().setUser(new User("test","test1"))
                .setAccountNumber(accountNumber)
                .setCurrency(CurrencyUnit.EUR)
                .setMoney(Money.of(CurrencyUnit.EUR, new BigDecimal(money)))
                .setCreatedDate(new Date())
                .createAccount();
    }


    public Transfer createTransferMock(String transferId, String money, String senderAccountNumber,String receiverAccountNumber ) {
        return new TransferBuilder().setTransferId(transferId).setFromAccountNumber(senderAccountNumber)
                .setToAccountNumber(receiverAccountNumber)
                .setCurrency(CurrencyUnit.EUR)
                .setMoney(Money.of(CurrencyUnit.EUR, new BigDecimal(money)))
                .setCreatedDate(new Date())
                .createTransfer();
    }

    public Transfer createTransferMock(String transferId, String money) {
        return new TransferBuilder().setTransferId(transferId).setFromAccountNumber("abc")
                .setToAccountNumber("xyz")
                .setCurrency(CurrencyUnit.EUR)
                .setMoney(Money.of(CurrencyUnit.EUR, new BigDecimal(money)))
                .setCreatedDate(new Date())
                .createTransfer();
    }

}
