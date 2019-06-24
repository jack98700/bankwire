package com.revolut.test.bankwire.repo;

import com.revolut.test.bankwire.dto.Account;
import com.revolut.test.bankwire.dto.Transfer;
import com.revolut.test.bankwire.exception.InSufficientAccountBalanceException;
import com.revolut.test.bankwire.validation.Validation;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TransferDaoImpl implements TransferDao {

    private static final long TIME_OUT = TimeUnit.SECONDS.toNanos(3);
    private static final Random random = new Random();
    private static final long FIXED_DELAY = 1;
    private static final long RANDOM_DELAY = 2;
    private Queue<Transfer> transfers = new LinkedBlockingQueue<>();
    private AccountDao accountDao;


    @Inject
    public TransferDaoImpl(AccountDao accountDao) {
        this.accountDao = accountDao;

    }

    @Override
    public void clearTransfers() {
        transfers.clear();
    }

    @Override
    public Optional<Transfer> findByTransferId(String transferId) {
        return transfers.stream().filter(transfer -> transfer.getTransferId().equals(transferId)).findFirst();

    }

    @Override
    public Queue<Transfer> findAllTransfers() {
        return transfers;
    }


    @Override
    public Transfer transferMoney(Transfer transfer) throws Exception {

        long stopTime = System.nanoTime() + TIME_OUT;
        while (transfer.isRunning()) {

            Optional<Account> senderAccountOptional = accountDao.findByAccountNumber(transfer.getFromAccountNumber());
            Optional<Account> receiverAccountOptional = accountDao.findByAccountNumber(transfer.getToAccountNumber());
            validateAccountTransfer(senderAccountOptional, receiverAccountOptional, transfer);
            Account senderAccount = senderAccountOptional.get();
            Account receiverAccount = receiverAccountOptional.get();

            if (senderAccount.getLock().tryLock()) {
                try {
                    if (receiverAccount.getLock().tryLock()) {
                        try {
                            validateAccountTransfer(accountDao.findByAccountNumber(transfer.getFromAccountNumber()), accountDao.findByAccountNumber(transfer.getToAccountNumber()), transfer);
                            accountDao.deductAccount(senderAccount, transfer.getMoney());
                            accountDao.creditAccount(receiverAccount, transfer.getMoney());
                            transfers.add(transfer);
                            transfer.setRunning(false);
                        } finally {
                            receiverAccount.getLock().unlock();
                        }
                    }
                } finally {
                    senderAccount.getLock().unlock();
                }
            }


            if (System.nanoTime() > stopTime) {
                transfer.setRunning(false);
            }

            try {
                TimeUnit.NANOSECONDS.sleep(FIXED_DELAY + random.nextLong() % RANDOM_DELAY);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(exception);
            }
        }
        return transfer;
    }

    private void validateAccountTransfer(Optional<Account> senderAccount, Optional<Account> receiverAccount, Transfer transfer) throws Exception {
        Validation.validateMoneyTransfer(senderAccount, receiverAccount);
        if (senderAccount.get().getMoney().isLessThan(transfer.getMoney())) {
            throw new InSufficientAccountBalanceException(String.format("InSufficient Account Balance %s", senderAccount.get().getMoney()));
        }
    }
}
