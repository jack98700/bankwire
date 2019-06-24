package com.revolut.account.test.backwire.integration;

import com.revolut.test.bankwire.Application;
import io.restassured.RestAssured;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;


public class ApiIntegrationTest {

    @BeforeClass
    public static void setUp() {
        configureHost();
        configurePort();
        configureBasePath();
        startServer();
    }

    private static void configureHost() {
        String baseHost = System.getProperty("server.host");
        if (baseHost == null) {
            baseHost = "http://localhost";
        }
        RestAssured.baseURI = baseHost;
    }

    private static void configurePort() {
        String port = System.getProperty("server.port");
        if (port == null) {
            RestAssured.port = Integer.parseInt("7000");
        } else {
            RestAssured.port = Integer.parseInt(port);
        }
    }

    private static void configureBasePath() {
        String basePath = System.getProperty("server.base");
        if (basePath == null) {
            basePath = "/";
        }
        RestAssured.basePath = basePath;
    }

    private static void startServer() {
        Application.main(new String[]{});
    }


    @Test
    public void findByAccountNumber_Exists_Success() {
        String number = given()
                .param("firstName", "testName")
                .and().param("lastName", "testSurname")
                .and().param("currencyCode", "EUR")
                .and().param("money", "10.00")
                .when().post("/account")
                .then().extract().path("accountNumber");

        get("/account/".concat(number)).then().body("accountNumber", equalTo(number));
    }

    @Test
    public void findByAccountNumber_Exists_Failure() {


        get("/account/".concat("abc")).then().statusCode(404);
    }

    @Test
    public void findAllAccounts_Success() {
        given()
                .param("firstName", "testName")
                .and().param("lastName", "testSurname")
                .and().param("currencyCode", "EUR")
                .and().param("money", "10.00")
                .when().post("/account")
                .then().extract().path("accountNumber");

        get("/accounts").then().statusCode(200);
    }

    @Test
    public void createAccount_Success() {
        given()
                .param("firstName", "testName")
                .and().param("lastName", "testSurname")
                .and().param("currencyCode", "EUR")
                .and().param("money", "10.00")
                .when().post("/account")
                .then().statusCode(200);
    }

    @Test
    public void createAccount_InvalidMoneyFormat_Failure() {
        given()
                .param("firstName", "testName")
                .and().param("lastName", "testSurname")
                .and().param("currencyCode", "EUR")
                .and().param("money", "InValid")
                .when().post("/account")
                .then().statusCode(400);
    }

    @Test
    public void deleteAccount_Exists_Success() {
        String accountNumber = given()
                .param("firstName", "testName")
                .and().param("lastName", "testSurname")
                .and().param("currencyCode", "EUR")
                .and().param("money", "10.00")
                .when().post("/account")
                .then().extract().path("accountNumber");

        delete("/account/".concat(accountNumber)).then().assertThat()
                .statusCode(200)
                .extract()
                .asString();

    }

    @Test
    public void deleteAccount_Exists_Failure() {
        String number = "9870";

        delete("/account/".concat(number)).then().assertThat().statusCode(400);
    }

    @Test
    public void findTransferById_Exists_Success() {
        String senderAccountNumber = given()
                .param("firstName", "testName")
                .and().param("lastName", "testSurname")
                .and().param("currencyCode", "EUR")
                .and().param("money", "10.00")
                .when().post("/account")
                .then().extract().path("accountNumber");

        String receiverAccountNumber = given()
                .param("firstName", "rec first name")
                .and().param("lastName", "rec last name")
                .and().param("currencyCode", "EUR")
                .and().param("money", "10.00")
                .when().post("/account")
                .then().extract().path("accountNumber");

        String transferId = given()
                .param("senderAccountNumber", senderAccountNumber)
                .and().param("receiverAccountNumber", receiverAccountNumber)
                .and().param("money", "10.00")
                .when().post("/transfer")
                .then().extract().path("transferId");

        get("/transfer/".concat(transferId)).then().body("transferId", equalTo(transferId));


    }

    @Test
    public void findTransferById_Exists_Failure() {
        String transferId = "9870";

        get("/transfer/".concat(transferId)).then().statusCode(404);


    }

    @Test
    public void getAllTransfers_Success() {
        String senderAccountNumber = given()
                .param("firstName", "testName")
                .and().param("lastName", "testSurname")
                .and().param("currencyCode", "EUR")
                .and().param("money", "10.00")
                .when().post("/account")
                .then().extract().path("accountNumber");

        String receiverAccountNumber = given()
                .param("firstName", "rec first name")
                .and().param("lastName", "rec last name")
                .and().param("currencyCode", "EUR")
                .and().param("money", "10.00")
                .when().post("/account")
                .then().extract().path("accountNumber");

        String transferId = given()
                .param("senderAccountNumber", senderAccountNumber)
                .and().param("receiverAccountNumber", receiverAccountNumber)
                .and().param("money", "10.00")
                .when().post("/transfer")
                .then().extract().path("transferId");

        get("/transfers").then().statusCode(200);

    }


    @Test
    public void transferMoney_Valid_Success() {
        String senderAccountNumber = given()
                .param("firstName", "testName")
                .and().param("lastName", "testSurname")
                .and().param("currencyCode", "EUR")
                .and().param("money", "10.00")
                .when().post("/account")
                .then().extract().path("accountNumber");

        String receiverAccountNumber = given()
                .param("firstName", "rec first name")
                .and().param("lastName", "rec last name")
                .and().param("currencyCode", "EUR")
                .and().param("money", "10.00")
                .when().post("/account")
                .then().extract().path("accountNumber");

        String transferId = given()
                .param("senderAccountNumber", senderAccountNumber)
                .and().param("receiverAccountNumber", receiverAccountNumber)
                .and().param("money", "10.00")
                .when().post("/transfer")
                .then().extract().path("transferId");

        Assert.assertNotNull(transferId);
    }

    @Test
    public void transferMoney_TransferSenderAndReceiverAreSame_Failure(){
        String accountNumber = given()
                .param("firstName", "testName")
                .and().param("lastName", "testSurname")
                .and().param("currencyCode", "EUR")
                .and().param("money", "10.00")
                .when().post("/account")
                .then().extract().path("accountNumber");



        given()
                .param("senderAccountNumber", accountNumber)
                .and().param("receiverAccountNumber", accountNumber)
                .and().param("money", "10.00")
                .when().post("/transfer")
                .then().statusCode(400);
    }

    @Test
    public void transferMoney_SenderDoesNotExist_Failure(){
        String accountNumber = "jio";

        given()
                .param("senderAccountNumber", accountNumber)
                .and().param("receiverAccountNumber", accountNumber)
                .and().param("money", "10.00")
                .when().post("/transfer")
                .then().statusCode(400);
    }


    @Test
    public void transferMoney_SenderReceiverCurrencyDoesNotMatch_Failure(){
        String senderAccountNumber = given()
                .param("firstName", "testName")
                .and().param("lastName", "testSurname")
                .and().param("currencyCode", "USD")
                .and().param("money", "10.00")
                .when().post("/account")
                .then().extract().path("accountNumber");

        String receiverAccountNumber = given()
                .param("firstName", "rec first name")
                .and().param("lastName", "rec last name")
                .and().param("currencyCode", "EUR")
                .and().param("money", "10.00")
                .when().post("/account")
                .then().extract().path("accountNumber");

         given()
                .param("senderAccountNumber", senderAccountNumber)
                .and().param("receiverAccountNumber", receiverAccountNumber)
                .and().param("money", "10.00")
                .when().post("/transfer")
                .then().statusCode(400);
    }

    @Test
    public void transferMoney_InValidMoneyFormat_Failure(){
        String senderAccountNumber = given()
                .param("firstName", "testName")
                .and().param("lastName", "testSurname")
                .and().param("currencyCode", "EUR")
                .and().param("money", "10.00")
                .when().post("/account")
                .then().extract().path("accountNumber");

        String receiverAccountNumber = given()
                .param("firstName", "rec first name")
                .and().param("lastName", "rec last name")
                .and().param("currencyCode", "EUR")
                .and().param("money", "10.00")
                .when().post("/account")
                .then().extract().path("accountNumber");

        given()
                .param("senderAccountNumber", senderAccountNumber)
                .and().param("receiverAccountNumber", receiverAccountNumber)
                .and().param("money", "InValid")
                .when().post("/transfer")
                .then().statusCode(400);
    }

    @Test
    public void transferMoney_InSufficientAccountBalance_Failure(){
        String senderAccountNumber = given()
                .param("firstName", "testName")
                .and().param("lastName", "testSurname")
                .and().param("currencyCode", "EUR")
                .and().param("money", "10.00")
                .when().post("/account")
                .then().extract().path("accountNumber");

        String receiverAccountNumber = given()
                .param("firstName", "rec first name")
                .and().param("lastName", "rec last name")
                .and().param("currencyCode", "EUR")
                .and().param("money", "10.00")
                .when().post("/account")
                .then().extract().path("accountNumber");

        given()
                .param("senderAccountNumber", senderAccountNumber)
                .and().param("receiverAccountNumber", receiverAccountNumber)
                .and().param("money", "20.00")
                .when().post("/transfer")
                .then().statusCode(400);
    }
}
