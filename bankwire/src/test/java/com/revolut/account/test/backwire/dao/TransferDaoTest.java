package com.revolut.account.test.backwire.dao;


import com.revolut.test.bankwire.dto.*;
import com.revolut.test.bankwire.exception.AccountNotFoundException;
import com.revolut.test.bankwire.exception.InSufficientAccountBalanceException;
import com.revolut.test.bankwire.exception.SenderReceiverCurrencyMisMatchException;
import com.revolut.test.bankwire.repo.AccountDao;
import com.revolut.test.bankwire.repo.TransferDao;
import com.revolut.test.bankwire.repo.TransferDaoImpl;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;
import java.util.Queue;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TransferDaoTest {

    private TransferDao transferDao;

    @Mock
    private AccountDao accountDao;

    @Before
    public void setUp() {
        transferDao = new TransferDaoImpl(accountDao);
    }

    @After
    public void tearDown() {
        transferDao.clearTransfers();
    }

    @Test
    public void findByTransferId_Exists_Success() throws Exception {
        //given
        String senderAccountNumber = "abc";
        String receiverAccountNumber = "xyz";
        String transferId = "abs";
        Account senderAccount = createAccountMock(senderAccountNumber);
        Account receiverAccount = createAccountMock(receiverAccountNumber);
        when(accountDao.findByAccountNumber(senderAccountNumber)).thenReturn(Optional.of(senderAccount));
        when(accountDao.findByAccountNumber(receiverAccountNumber)).thenReturn(Optional.of(receiverAccount));
        transferDao.transferMoney(createTransferMock(transferId));

        // when
        Optional<Transfer> transferOptional = transferDao.findByTransferId(transferId);

        //then
        assertTrue(transferOptional.isPresent());
    }

    @Test
    public void findByTransferId_Exists_Failure() {
        //given
        String transferId = "abs";

        // when
        Optional<Transfer> transferOptional = transferDao.findByTransferId(transferId);

        //then
        assertFalse(transferOptional.isPresent());
    }

    @Test(expected = InSufficientAccountBalanceException.class)
    public void transferMoney_SenderBalanceBelowTransferAmount_Exception() throws Exception {

        //given
        String senderAccountNumber = "abc";
        String receiverAccountNumber = "xyz";
        String transferId = "abs";
        Account senderAccount = createAccountMock(senderAccountNumber, new BigDecimal("10"));
        Account receiverAccount = createAccountMock(receiverAccountNumber);
        when(accountDao.findByAccountNumber(senderAccountNumber)).thenReturn(Optional.of(senderAccount));
        when(accountDao.findByAccountNumber(receiverAccountNumber)).thenReturn(Optional.of(receiverAccount));

        // when
        transferDao.transferMoney(createTransferMock(transferId));

        //then

    }

    @Test(expected = AccountNotFoundException.class)
    public void transferMoney_SenderAccountNotFound_Exception() throws Exception {

        //given
        String senderAccountNumber = "abc";
        String receiverAccountNumber = "xyz";
        String transferId = "abs";
        Account receiverAccount = createAccountMock(receiverAccountNumber);
        when(accountDao.findByAccountNumber(senderAccountNumber)).thenReturn(Optional.empty());
        when(accountDao.findByAccountNumber(receiverAccountNumber)).thenReturn(Optional.of(receiverAccount));

        // when
        transferDao.transferMoney(createTransferMock(transferId));

        //then

    }

    @Test(expected = AccountNotFoundException.class)
    public void transferMoney_ReceiverBalanceBelowTransferAmount_Exception() throws Exception {

        //given
        String senderAccountNumber = "abc";
        String receiverAccountNumber = "xyz";
        String transferId = "abs";
        Account senderAccount = createAccountMock(senderAccountNumber, new BigDecimal("10"));
        when(accountDao.findByAccountNumber(senderAccountNumber)).thenReturn(Optional.of(senderAccount));
        when(accountDao.findByAccountNumber(receiverAccountNumber)).thenReturn(Optional.empty());

        // when
        transferDao.transferMoney(createTransferMock(transferId));

        //then

    }

    @Test(expected = SenderReceiverCurrencyMisMatchException.class)
    public void transferMoney_SenderReceiverCurrencyMisMatch_Exception() throws Exception {

        //given
        String senderAccountNumber = "abc";
        String receiverAccountNumber = "xyz";
        String transferId = "abs";
        Account senderAccount = createAccountMock(senderAccountNumber, new BigDecimal("10"), CurrencyUnit.USD);
        Account receiverAccount = createAccountMock(receiverAccountNumber);
        when(accountDao.findByAccountNumber(senderAccountNumber)).thenReturn(Optional.of(senderAccount));
        when(accountDao.findByAccountNumber(receiverAccountNumber)).thenReturn(Optional.of(receiverAccount));

        // when
        transferDao.transferMoney(createTransferMock(transferId));

        //then

    }


    @Test
    public void findAllTransfers_Success() throws Exception {
        //given
        String senderAccountNumber = "abc";
        String receiverAccountNumber = "xyz";
        String transferId = "123";
        String transferAnotherId = "456";
        Account senderAccount = createAccountMock(senderAccountNumber);
        Account receiverAccount = createAccountMock(receiverAccountNumber);
        when(accountDao.findByAccountNumber(senderAccountNumber)).thenReturn(Optional.of(senderAccount));
        when(accountDao.findByAccountNumber(receiverAccountNumber)).thenReturn(Optional.of(receiverAccount));
        transferDao.transferMoney(createTransferMock(transferId));
        transferDao.transferMoney(createTransferMock(transferAnotherId));

        // when
        Queue<Transfer> transfers = transferDao.findAllTransfers();

        //then
        assertThat(transfers.size(), is(2));
    }

    @Test
    public void clearTransfers_DeletedAll_Success() throws Exception {
        //given
        String senderAccountNumber = "abc";
        String receiverAccountNumber = "xyz";
        String transferId = "123";
        String transferAnotherId = "456";
        Account senderAccount = createAccountMock(senderAccountNumber);
        Account receiverAccount = createAccountMock(receiverAccountNumber);
        when(accountDao.findByAccountNumber(senderAccountNumber)).thenReturn(Optional.of(senderAccount));
        when(accountDao.findByAccountNumber(receiverAccountNumber)).thenReturn(Optional.of(receiverAccount));
        transferDao.transferMoney(createTransferMock(transferId));
        transferDao.transferMoney(createTransferMock(transferAnotherId));

        // when
        transferDao.clearTransfers();

        //then
        assertThat(transferDao.findAllTransfers().size(), is(0));
    }

    @Test
    public void transferMoney_Success() throws Exception {
        //given
        String senderAccountNumber = "abc";
        String receiverAccountNumber = "xyz";
        String transferId = "abs";
        Account senderAccount = createAccountMock(senderAccountNumber);
        Account receiverAccount = createAccountMock(receiverAccountNumber);
        when(accountDao.findByAccountNumber(senderAccountNumber)).thenReturn(Optional.of(senderAccount));
        when(accountDao.findByAccountNumber(receiverAccountNumber)).thenReturn(Optional.of(receiverAccount));

        // when
        transferDao.transferMoney(createTransferMock(transferId));

        //then
        assertTrue(transferDao.findByTransferId(transferId).isPresent());
    }

    @Test
    public void transferMoney_SenderAccountDeducted_Success() throws Exception {
        //given
        String senderAccountNumber = "abc";
        String receiverAccountNumber = "xyz";
        String transferId = "123";
        Account senderAccount = createAccountMock(senderAccountNumber);
        Account receiverAccount = createAccountMock(receiverAccountNumber);
        when(accountDao.findByAccountNumber(senderAccountNumber)).thenReturn(Optional.of(senderAccount));
        when(accountDao.findByAccountNumber(receiverAccountNumber)).thenReturn(Optional.of(receiverAccount));
        Transfer transfer = createTransferMock(transferId);

        // when
        transferDao.transferMoney(transfer);


        //then
        assertTrue(transferDao.findByTransferId(transferId).isPresent());
        verify(accountDao).deductAccount(senderAccount, transfer.getMoney());
    }

    @Test
    public void transferMoney_ReceiverAccountCredited_Success() throws Exception {
        //given
        String senderAccountNumber = "abc";
        String receiverAccountNumber = "xyz";
        String transferId = "123";
        Account senderAccount = createAccountMock(senderAccountNumber);
        Account receiverAccount = createAccountMock(receiverAccountNumber);
        when(accountDao.findByAccountNumber(senderAccountNumber)).thenReturn(Optional.of(senderAccount));
        when(accountDao.findByAccountNumber(receiverAccountNumber)).thenReturn(Optional.of(receiverAccount));
        Transfer transfer = createTransferMock(transferId);

        // when
        transferDao.transferMoney(transfer);

        //then
        assertTrue(transferDao.findByTransferId(transferId).isPresent());
        verify(accountDao).creditAccount(receiverAccount, transfer.getMoney());
    }


    private Account createAccountMock(String accountNumber) {
        return new AccountBuilder().setUser(new User("test","test1"))
                .setAccountNumber(accountNumber)
                .setCurrency(CurrencyUnit.EUR)
                .setMoney(Money.of(CurrencyUnit.EUR, 100))
                .setCreatedDate(new Date())
                .createAccount();
    }


    private Account createAccountMock(String accountNumber, BigDecimal money) {
        return new AccountBuilder().setUser(new User("test","test1"))
                .setAccountNumber(accountNumber)
                .setCurrency(CurrencyUnit.EUR)
                .setMoney(Money.of(CurrencyUnit.EUR, money))
                .setCreatedDate(new Date())
                .createAccount();
    }

    private Account createAccountMock(String accountNumber, BigDecimal money, CurrencyUnit currencyUnit) {
        return new AccountBuilder().setUser(new User("test","test1"))
                .setAccountNumber(accountNumber)
                .setCurrency(currencyUnit)
                .setMoney(Money.of(currencyUnit, money))
                .setCreatedDate(new Date())
                .createAccount();
    }

    public Transfer createTransferMock(String transferId) {
        return new TransferBuilder().setTransferId(transferId).setFromAccountNumber("abc")
                .setToAccountNumber("xyz")
                .setCurrency(CurrencyUnit.EUR)
                .setMoney(Money.of(CurrencyUnit.EUR, 100))
                .setCreatedDate(new Date())
                .createTransfer();
    }

}
