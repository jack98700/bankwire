package com.revolut.test.bankwire.repo;

import com.revolut.test.bankwire.dto.Account;
import com.revolut.test.bankwire.exception.AccountAlreadyExistsException;
import com.revolut.test.bankwire.exception.AccountNotFoundException;
import org.joda.money.Money;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class AccountDaoImpl implements AccountDao {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        Account account = accounts.get(accountNumber);
        if (account == null) {
            return Optional.empty();
        }
        return Optional.of(accounts.get(accountNumber));
    }

    @Override
    public List<Account> findAllAccounts() {
        return accounts.values().stream().collect(Collectors.toList());
    }

    @Override
    public Account createAccount(Account account) throws Exception {
        if (accounts.containsKey(account.getAccountNumber())) {
            throw new AccountAlreadyExistsException(String.format("Account %s already exists", account.getAccountNumber()));
        }

        accounts.put(account.getAccountNumber(), account);
        return account;
    }

    @Override
    public Account deleteAccount(String accountNumber) throws Exception {
        if (!accounts.containsKey(accountNumber)) {
            throw new AccountNotFoundException(String.format("Account %s not Found", accountNumber));
        }
        return accounts.remove(accountNumber);

    }

    @Override
    public void deductAccount(Account account, Money money) throws Exception {
        if (!accounts.containsKey(account.getAccountNumber())) {
            throw new AccountNotFoundException(String.format("Account %s not Found", account.getAccountNumber()));
        }
        account.setMoney(account.getMoney().minus(money));
        accounts.put(account.getAccountNumber(), account);
    }

    @Override
    public void creditAccount(Account account, Money money) throws Exception {
        if (!accounts.containsKey(account.getAccountNumber())) {
            throw new AccountNotFoundException(String.format("Account %s not Found", account.getAccountNumber()));
        }
        account.setMoney(account.getMoney().plus(money));
        accounts.put(account.getAccountNumber(), account);
    }

    @Override
    public void clearAccounts() {
        accounts.clear();
    }
}
