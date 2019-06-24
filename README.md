BankWire :-


Allow Money Transfer from One Account to Another

-----------------------------------------------------------------------------




Technology Stack :- 


Java 8, Maven, Javalin, Slf4J, Dagger, Joda Money, Gson


JUnit, Mockito, Concurrent Unit, REST Assured

-----------------------------------------------------------------------------


Build 


Navigate to root directory then execute 


mvn clean install

-----------------------------------------------------------------------------

Run

Run Application.java to start the application

-----------------------------------------------------------------------------

CURL

-------------------------------------------------------------------------------

Create New Account

curl -X POST http://localhost:7000/account -F firstName=John -F lastName=Doe -F currencyCode=EUR -F money=100.00

-------------------------------------------------------------------------------

Get by Account Number

curl -X GET http://localhost:7000/account/{id}

curl -X GET http://localhost:7000/account/60ae5699-d326-4848-a48a-39b984688e28

-------------------------------------------------------------------------------

Get All Accounts 

curl -X GET http://localhost:7000/accounts

-------------------------------------------------------------------------------

Delete By Account Number 

curl -X DELETE http://localhost:7000/account/{id}

curl -X DELETE http://localhost:7000/account/60ae5699-d326-4848-a48a-39b984688e28

-------------------------------------------------------------------------------

Transfer Money

curl -X POST http://localhost:7000/transfer -F senderAccountNumber={id} -F receiverAccountNumber={id} -F money=10.00

curl -X POST http://localhost:7000/transfer -F senderAccountNumber=9b0f6621-c608-4699-97a9-a8ec181b5e32 -F receiverAccountNumber=5c05457e-93a4-47c8-9594-028336721e06 -F money=10.00

-------------------------------------------------------------------------------

Get By Transfer Id

curl -X GET http://localhost:7000/transfer/{transferId}

curl -X GET http://localhost:7000/transfer/b53cbd49-0f5f-455c-ba67-01d92fe95ec4

-------------------------------------------------------------------------------

Get All Transfers 

curl -X GET http://localhost:7000/transfers

-------------------------------------------------------------------------------

Delete By Transfer Id

curl -X DELETE http://localhost:7000/transfer/{transferId}

curl -X DELETE http://localhost:7000/transfer/b53cbd49-0f5f-455c-ba67-01d92fe95ec4
