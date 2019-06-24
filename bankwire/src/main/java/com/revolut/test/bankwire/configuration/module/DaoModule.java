package com.revolut.test.bankwire.configuration.module;

import com.revolut.test.bankwire.repo.AccountDao;
import com.revolut.test.bankwire.repo.AccountDaoImpl;
import com.revolut.test.bankwire.repo.TransferDao;
import com.revolut.test.bankwire.repo.TransferDaoImpl;
import dagger.Module;
import dagger.Provides;

import javax.inject.Inject;
import javax.inject.Singleton;

@Module
public class DaoModule {

    @Inject
    @Provides
    @Singleton
    AccountDao accountDao() {
        return new AccountDaoImpl();
    }


    @Inject
    @Provides
    @Singleton
    TransferDao transferDao(AccountDao accountDao) {
        return new TransferDaoImpl(accountDao);
    }


}
