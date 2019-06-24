package com.revolut.test.bankwire.validation;

import com.revolut.test.bankwire.dto.Account;
import com.revolut.test.bankwire.exception.AccountNotFoundException;
import com.revolut.test.bankwire.exception.InValidAccountNumberException;
import com.revolut.test.bankwire.exception.SenderReceiverCurrencyMisMatchException;
import com.revolut.test.bankwire.exception.TransferToSameAccountException;

import java.util.Optional;

public class Validation {

    public static void validateMoneyTransfer(Optional<Account> senderAccount, Optional<Account> receiverAccount) throws Exception {

        if (!senderAccount.isPresent()) {
            throw new AccountNotFoundException("Transfer UnSuccessfully Sender Account does not exist");
        }

        if (!receiverAccount.isPresent()) {
            throw new AccountNotFoundException("Transfer UnSuccessfully Receiver Account does not exist");
        }

        if (senderAccount.get().getCurrency() != receiverAccount.get().getCurrency()) {
            throw new SenderReceiverCurrencyMisMatchException(
                    "Transfer UnSuccessfully Sender Receiver Currencies don't match");
        }

    }

    public static void validateMoneyTransferRequestParam(String senderAccountNumber, String receiverAccountNumber) throws Exception {

        if (senderAccountNumber == null || receiverAccountNumber == null) {
            throw new InValidAccountNumberException("Transfer UnSuccessfully Sender Account and Receiver Account Number are Mandatory");
        }

        if (senderAccountNumber.equalsIgnoreCase(receiverAccountNumber)) {
            throw new TransferToSameAccountException("Transfer UnSuccessfully Sender are Receiver are identical");
        }


    }
}
