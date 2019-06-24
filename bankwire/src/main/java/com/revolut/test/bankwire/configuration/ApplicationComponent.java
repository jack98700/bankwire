package com.revolut.test.bankwire.configuration;

import com.revolut.test.bankwire.configuration.module.ControllerModule;
import com.revolut.test.bankwire.configuration.module.DaoModule;
import com.revolut.test.bankwire.controller.AccountController;
import com.revolut.test.bankwire.controller.TransferController;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
        ControllerModule.class,
        DaoModule.class
})
public interface ApplicationComponent {

    AccountController accountController();

    TransferController transferController();

}
