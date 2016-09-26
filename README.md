# n26_transactions
N26 Java Code Challenge

We would like to have a RESTful web service that stores some transactions (in memory is fine)
and returns information about those transactions.
The transactions to be stored have a type and an amount. The service should support returning all
transactions of a type. Also, transactions can be linked to each other (using a "parent_id") and we
need to know the total amount involved for all transactions linked to a particular transaction.

1) Please complete it using Java and in 3 consecutive days.

2) Code does not need to be deployable

3) We prefer that you post code on Github or Bitbucket, so we can review the code.

4) Do not use SQL.

In general we are looking for a good implementation, code quality and how the implementation is
tested. Some discussion about asymptotic behaviour would also be appreciated.

to run type:

mvn package && java -jar target/n26_transactions-0.0.1-SNAPSHOT.jar
