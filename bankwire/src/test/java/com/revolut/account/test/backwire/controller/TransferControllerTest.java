package com.revolut.account.test.backwire.controller;


import com.revolut.test.bankwire.context.ContextWrapper;
import com.revolut.test.bankwire.controller.TransferController;
import com.revolut.test.bankwire.dto.*;
import com.revolut.test.bankwire.exception.InSufficientAccountBalanceException;
import com.revolut.test.bankwire.repo.AccountDao;
import com.revolut.test.bankwire.repo.TransferDao;
import com.revolut.test.bankwire.controller.TransferControllerImpl;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


import static org.mockito.Mockito.*;

import io.javalin.Context;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

@RunWith(MockitoJUnitRunner.class)
public class TransferControllerTest {

    private TransferController transferController;

    @Mock
    private Context context;

    @Mock
    private ContextWrapper contextWrapper;

    @Mock
    private TransferDao transferDao;

    @Mock
    private AccountDao accountDao;

    @Mock
    private Transfer transfer;


    @Before
    public void setUp()  {
        transferController = new TransferControllerImpl(contextWrapper, transferDao, accountDao);
    }


    @Test
    public void findTransferById_Exists_Success() {
        //given
        String transferId = "1234";
        when(contextWrapper.pathParam(context, "id")).thenReturn(transferId);
        when(transferDao.findByTransferId(transferId)).thenReturn(Optional.of(transfer));

        //when
        transferController.findTransferById(context);

        //then
        verify(transferDao).findByTransferId(transferId);
        verify(contextWrapper).json(context, transfer, 200);
    }

    @Test
    public void findTransferById_Exists_Failure() {
        //given
        String transferId = "1234";
        when(contextWrapper.pathParam(context, "id")).thenReturn(transferId);
        when(transferDao.findByTransferId(transferId)).thenReturn(Optional.empty());

        //when
        transferController.findTransferById(context);

        //then
        verify(transferDao).findByTransferId(transferId);
        verify(contextWrapper).json(context, String.format("transfer with id %s not found ", transferId), 404);
    }

    @Test
    public void getAllTransfers_Success() {
        //given
        Queue<Transfer> transfers = new LinkedList<>();
        when(transferDao.findAllTransfers()).thenReturn(transfers);

        //when
        transferController.getAllTransfers(context);

        //then
        verify(transferDao, atLeastOnce()).findAllTransfers();
        verify(contextWrapper).json(context, transfers, 200);
    }

    @Test
    public void transferMoney_Valid_Success() throws  Exception{
        // given
        String transferId = "9870";
        String senderAccountNumber = "abc";
        String receiverAccountNumber = "xyz";
        Transfer transfer = new TransferBuilder().setTransferId(transferId).createTransfer();
        when(transferDao.transferMoney(any(Transfer.class))).thenReturn(transfer);
        when(contextWrapper.formParam(context,"senderAccountNumber")).thenReturn(senderAccountNumber);
        when(contextWrapper.formParam(context,"receiverAccountNumber")).thenReturn(receiverAccountNumber);
        when(contextWrapper.formParam(context,"money")).thenReturn("10.00");
        when(accountDao.findByAccountNumber(senderAccountNumber)).thenReturn(new AccountBuilder().setAccountNumber(senderAccountNumber).setCurrency(CurrencyUnit.EUR).createAccountOptional());
        when(accountDao.findByAccountNumber(receiverAccountNumber)).thenReturn(new AccountBuilder().setAccountNumber(receiverAccountNumber).setCurrency(CurrencyUnit.EUR).createAccountOptional());

        //when
        transferController.transferMoney(context);

        //then
        verify(transferDao).transferMoney(any(Transfer.class));
        verify(contextWrapper).json(context, transfer, 200);

    }

    @Test
    public void transferMoney_TransferSenderAndReceiverAreSame_Failure()throws  Exception{
        // given
        String senderAccountNumber = "abc";
        String receiverAccountNumber = "abc";
        Optional<Account> senderAccount = new AccountBuilder().setAccountNumber(senderAccountNumber).createAccountOptional();
        Optional<Account> receiverAccount = new AccountBuilder().setAccountNumber(receiverAccountNumber).createAccountOptional();
        when(contextWrapper.formParam(context,"senderAccountNumber")).thenReturn(senderAccountNumber);
        when(contextWrapper.formParam(context,"receiverAccountNumber")).thenReturn(receiverAccountNumber);
        when(accountDao.findByAccountNumber(senderAccountNumber)).thenReturn(senderAccount);
        when(accountDao.findByAccountNumber(receiverAccountNumber)).thenReturn(receiverAccount);

        //when
        transferController.transferMoney(context);

        //then
        verify(transferDao, never()).transferMoney(any(Transfer.class));
        verify(contextWrapper, atLeastOnce()).json(context, "Transfer UnSuccessfully Sender are Receiver are identical", 400);
    }

    @Test
    public void transferMoney_SenderDoesNotExist_Failure()throws  Exception{
        // given
        String senderAccountNumber = "abc";
        String receiverAccountNumber = "xyz";
        when(contextWrapper.formParam(context,"senderAccountNumber")).thenReturn(senderAccountNumber);
        when(contextWrapper.formParam(context,"receiverAccountNumber")).thenReturn(receiverAccountNumber);
        when(accountDao.findByAccountNumber(senderAccountNumber)).thenReturn(Optional.empty());
        when(accountDao.findByAccountNumber(receiverAccountNumber)).thenReturn(new AccountBuilder().setAccountNumber(receiverAccountNumber).createAccountOptional());

        //when
        transferController.transferMoney(context);

        //then
        verify(transferDao, never()).transferMoney(any(Transfer.class));
        verify(contextWrapper, atLeastOnce()).json(context, "Transfer UnSuccessfully Sender Account does not exist", 400);
    }

    @Test
    public void transferMoney_ReceiverDoesNotExist_Failure()throws  Exception{
        // given
        String senderAccountNumber = "abc";
        String receiverAccountNumber = "xyz";
        when(contextWrapper.formParam(context,"senderAccountNumber")).thenReturn(senderAccountNumber);
        when(contextWrapper.formParam(context,"receiverAccountNumber")).thenReturn(receiverAccountNumber);
        when(accountDao.findByAccountNumber(senderAccountNumber)).thenReturn(new AccountBuilder().setAccountNumber(senderAccountNumber).createAccountOptional());
        when(accountDao.findByAccountNumber(receiverAccountNumber)).thenReturn( Optional.empty());

        //when
        transferController.transferMoney(context);

        //then
        verify(transferDao, never()).transferMoney(any(Transfer.class));
        verify(contextWrapper, atLeastOnce()).json(context, "Transfer UnSuccessfully Receiver Account does not exist", 400);
    }

    @Test
    public void transferMoney_SenderReceiverCurrencyDoesNotMatch_Failure()throws  Exception{
        // given
        String senderAccountNumber = "abc";
        String receiverAccountNumber = "xyz";
        when(contextWrapper.formParam(context,"senderAccountNumber")).thenReturn(senderAccountNumber);
        when(contextWrapper.formParam(context,"receiverAccountNumber")).thenReturn(receiverAccountNumber);
        when(accountDao.findByAccountNumber(senderAccountNumber)).thenReturn(new AccountBuilder().setAccountNumber(senderAccountNumber).setCurrency(CurrencyUnit.USD).createAccountOptional());
        when(accountDao.findByAccountNumber(receiverAccountNumber)).thenReturn( new AccountBuilder().setAccountNumber(receiverAccountNumber).setCurrency(CurrencyUnit.JPY).createAccountOptional());

        //when
        transferController.transferMoney(context);

        //then
        verify(transferDao, never()).transferMoney(any(Transfer.class));
        verify(contextWrapper, atLeastOnce()).json(context, "Transfer UnSuccessfully Sender Receiver Currencies don't match", 400);
    }

    @Test
    public void transferMoney_SenderAccountNumberIsNull_Failure()throws  Exception{
        // given
        String senderAccountNumber = null;
        String receiverAccountNumber = "xyz";
        when(contextWrapper.formParam(context,"senderAccountNumber")).thenReturn(senderAccountNumber);
        when(contextWrapper.formParam(context,"receiverAccountNumber")).thenReturn(receiverAccountNumber);

        //when
        transferController.transferMoney(context);

        //then
        verify(transferDao, never()).transferMoney(any(Transfer.class));
        verify(contextWrapper, atLeastOnce()).json(context, "Transfer UnSuccessfully Sender Account and Receiver Account Number are Mandatory", 400);
    }

    @Test
    public void transferMoney_ReceiverAccountNumberIsNull_Failure()throws  Exception{
        // given
        String senderAccountNumber = "abc";
        String receiverAccountNumber = null;
        when(contextWrapper.formParam(context,"senderAccountNumber")).thenReturn(senderAccountNumber);
        when(contextWrapper.formParam(context,"receiverAccountNumber")).thenReturn(receiverAccountNumber);

        //when
        transferController.transferMoney(context);

        //then
        verify(transferDao, never()).transferMoney(any(Transfer.class));
        verify(contextWrapper, atLeastOnce()).json(context, "Transfer UnSuccessfully Sender Account and Receiver Account Number are Mandatory", 400);
    }

    @Test
    public void transferMoney_SenderReceiverAccountNumberIdentical_Failure()throws  Exception{
        // given
        String senderAccountNumber = "abc";
        String receiverAccountNumber = "abc";
        when(contextWrapper.formParam(context,"senderAccountNumber")).thenReturn(senderAccountNumber);
        when(contextWrapper.formParam(context,"receiverAccountNumber")).thenReturn(receiverAccountNumber);

        //when
        transferController.transferMoney(context);

        //then
        verify(transferDao, never()).transferMoney(any(Transfer.class));
        verify(contextWrapper, atLeastOnce()).json(context, "Transfer UnSuccessfully Sender are Receiver are identical", 400);
    }
    @Test
    public void transferMoney_InValidMoneyFormat_Failure()throws  Exception{
        // given
        String senderAccountNumber = "abc";
        String receiverAccountNumber = "xyz";
        String moneyStr = "InValid";
        when(contextWrapper.formParam(context,"senderAccountNumber")).thenReturn(senderAccountNumber);
        when(contextWrapper.formParam(context,"receiverAccountNumber")).thenReturn(receiverAccountNumber);
        when(contextWrapper.formParam(context,"money")).thenReturn(moneyStr);
        when(accountDao.findByAccountNumber(senderAccountNumber)).thenReturn(new AccountBuilder().setAccountNumber(senderAccountNumber).setCurrency(CurrencyUnit.EUR).createAccountOptional());
        when(accountDao.findByAccountNumber(receiverAccountNumber)).thenReturn( new AccountBuilder().setAccountNumber(receiverAccountNumber).setCurrency(CurrencyUnit.EUR).createAccountOptional());

        //when
        transferController.transferMoney(context);

        //then
        verify(transferDao, never()).transferMoney(any(Transfer.class));
        verify(contextWrapper, atLeastOnce()).json(context, String.format("Money format invalid", moneyStr), 400);
    }

    @Test
    public void transferMoney_InSufficientAccountBalance_Failure()throws  Exception{
        // given
        String senderAccountNumber = "abc";
        String receiverAccountNumber = "xyz";
        String moneyStr = "100";
        Money senderMoney = Money.of(CurrencyUnit.EUR, 50);
        when(contextWrapper.formParam(context,"senderAccountNumber")).thenReturn(senderAccountNumber);
        when(contextWrapper.formParam(context,"receiverAccountNumber")).thenReturn(receiverAccountNumber);
        when(contextWrapper.formParam(context,"money")).thenReturn(moneyStr);
        when(accountDao.findByAccountNumber(senderAccountNumber)).thenReturn(new AccountBuilder().setAccountNumber(senderAccountNumber).setCurrency(CurrencyUnit.EUR).setMoney(senderMoney).createAccountOptional());
        when(accountDao.findByAccountNumber(receiverAccountNumber)).thenReturn( new AccountBuilder().setAccountNumber(receiverAccountNumber).setCurrency(CurrencyUnit.EUR).createAccountOptional());
        when(transferDao.transferMoney(any(Transfer.class))).thenThrow(new InSufficientAccountBalanceException(String.format("InSufficient Account Balance %s", senderMoney)));

        //when
        transferController.transferMoney(context);

        //then
        verify(transferDao).transferMoney(any(Transfer.class));
        verify(contextWrapper, atLeastOnce()).json(context, String.format("InSufficient Account Balance %s", senderMoney), 400);
    }


}
