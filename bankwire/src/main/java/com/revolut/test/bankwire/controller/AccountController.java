package com.revolut.test.bankwire.controller;

import io.javalin.Context;

public interface AccountController {

    void findByAccountNumber(Context context);

    void findAllAccounts(Context context);

    void createAccount(Context context);

    void deleteAccount(Context context);

}
