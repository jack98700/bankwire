package com.revolut.test.bankwire.repo;

import com.revolut.test.bankwire.dto.Account;
import org.joda.money.Money;

import java.util.List;
import java.util.Optional;


public interface AccountDao {

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findAllAccounts();

    Account createAccount(Account account) throws Exception;

    Account deleteAccount(String accountNumber) throws Exception;

    void deductAccount(Account account, Money money) throws Exception;

    void creditAccount(Account account, Money money) throws Exception;

    void clearAccounts();

}
