package com.revolut.test.bankwire.configuration.module;

import com.revolut.test.bankwire.context.ContextWrapper;
import com.revolut.test.bankwire.context.ContextWrapperImpl;
import com.revolut.test.bankwire.controller.AccountController;
import com.revolut.test.bankwire.controller.AccountControllerImpl;
import com.revolut.test.bankwire.controller.TransferController;
import com.revolut.test.bankwire.controller.TransferControllerImpl;
import com.revolut.test.bankwire.repo.AccountDao;
import com.revolut.test.bankwire.repo.TransferDao;
import dagger.Module;
import dagger.Provides;

import javax.inject.Inject;
import javax.inject.Singleton;

@Module
public class ControllerModule {

    @Provides
    @Singleton
    ContextWrapper contextWrapper() {
        return new ContextWrapperImpl();
    }

    @Inject
    @Provides
    @Singleton
    AccountController accountController(final ContextWrapper contextWrapper, final AccountDao accountDao) {
        return new AccountControllerImpl(contextWrapper, accountDao);
    }

    @Inject
    @Provides
    @Singleton
    TransferController transferController(final ContextWrapper contextWrapper, final TransferDao transferDao, final AccountDao accountDao) {
        return new TransferControllerImpl(contextWrapper, transferDao, accountDao);
    }
}
