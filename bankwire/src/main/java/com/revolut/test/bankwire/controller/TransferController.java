package com.revolut.test.bankwire.controller;

import io.javalin.Context;

public interface TransferController {

    void findTransferById(Context context);

    void getAllTransfers(Context context);

    void transferMoney(Context context);

}
