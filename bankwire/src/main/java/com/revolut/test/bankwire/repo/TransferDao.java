package com.revolut.test.bankwire.repo;

import com.revolut.test.bankwire.dto.Transfer;

import java.util.Optional;
import java.util.Queue;

public interface TransferDao {

    Optional<Transfer> findByTransferId(String transferId);

    Queue<Transfer> findAllTransfers();

    Transfer transferMoney(Transfer transfer) throws Exception;

    void clearTransfers();
}
