package com.revolut.test.bankwire;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.revolut.test.bankwire.configuration.ApplicationComponent;
import com.revolut.test.bankwire.configuration.DaggerApplicationComponent;
import com.revolut.test.bankwire.controller.AccountController;
import com.revolut.test.bankwire.controller.TransferController;
import io.javalin.Javalin;
import io.javalin.json.JavalinJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    private static final int PORT = 7000;

    public static void main(String args[]) {
        ApplicationComponent applicationComponent = DaggerApplicationComponent.create();
        AccountController accountController = applicationComponent.accountController();
        TransferController transferController = applicationComponent.transferController();

        final Gson gson = new GsonBuilder().create();
        JavalinJson.setFromJsonMapper(gson::fromJson);
        JavalinJson.setToJsonMapper(gson::toJson);


        Javalin app = Javalin.create()
                .port(PORT)
                .start();


        app.get("/accounts", accountController::findAllAccounts);
        app.get("/account/:id", accountController::findByAccountNumber);

        app.delete("/account/:id", accountController::deleteAccount);
        app.post("/account", accountController::createAccount);

        app.get("/transfers", transferController::getAllTransfers);
        app.get("/transfer/:id", transferController::findTransferById);

        app.post("/transfer", transferController::transferMoney);

        app.exception(Exception.class, (exception, context) -> {
            context.status(500);
            LOG.error("error occurred", exception);
        });

    }

}
