	at org.postgresql.core.ConnectionFactory.openConnection(ConnectionFactory.java:57) ~[postgresql-42.7.8.jar!/:42.7.8]

	at org.postgresql.jdbc.PgConnection.<init>(PgConnection.java:279) ~[postgresql-42.7.8.jar!/:42.7.8]

	at org.postgresql.Driver.makeConnection(Driver.java:448) ~[postgresql-42.7.8.jar!/:42.7.8]

	at org.postgresql.Driver.connect(Driver.java:298) ~[postgresql-42.7.8.jar!/:42.7.8]

	at com.zaxxer.hikari.util.DriverDataSource.getConnection(DriverDataSource.java:144) ~[HikariCP-6.3.3.jar!/:na]

	at com.zaxxer.hikari.pool.PoolBase.newConnection(PoolBase.java:370) ~[HikariCP-6.3.3.jar!/:na]

	at com.zaxxer.hikari.pool.PoolBase.newPoolEntry(PoolBase.java:207) ~[HikariCP-6.3.3.jar!/:na]

	at com.zaxxer.hikari.pool.HikariPool.createPoolEntry(HikariPool.java:488) ~[HikariCP-6.3.3.jar!/:na]

	at com.zaxxer.hikari.pool.HikariPool.checkFailFast(HikariPool.java:576) ~[HikariCP-6.3.3.jar!/:na]

	at com.zaxxer.hikari.pool.HikariPool.<init>(HikariPool.java:97) ~[HikariCP-6.3.3.jar!/:na]

	at com.zaxxer.hikari.HikariDataSource.getConnection(HikariDataSource.java:111) ~[HikariCP-6.3.3.jar!/:na]

	at org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl.getConnection(DatasourceConnectionProviderImpl.java:126) ~[hibernate-core-6.6.33.Final.jar!/:6.6.33.Final]

	at org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator$ConnectionProviderJdbcConnectionAccess.obtainConnection(JdbcEnvironmentInitiator.java:485) ~[hibernate-core-6.6.33.Final.jar!/:6.6.33.Final]

	at org.hibernate.resource.transaction.backend.jdbc.internal.DdlTransactionIsolatorNonJtaImpl.getIsolatedConnection(DdlTransactionIsolatorNonJtaImpl.java:46) ~[hibernate-core-6.6.33.Final.jar!/:6.6.33.Final]

	... 129 common frames omitted

Caused by: java.net.ConnectException: Connection refused

	at java.base/sun.nio.ch.Net.pollConnect(Native Method) ~[na:na]

	at java.base/sun.nio.ch.Net.pollConnectNow(Unknown Source) ~[na:na]

	at java.base/sun.nio.ch.NioSocketImpl.timedFinishConnect(Unknown Source) ~[na:na]

	at java.base/sun.nio.ch.NioSocketImpl.connect(Unknown Source) ~[na:na]

	at java.base/java.net.SocksSocketImpl.connect(Unknown Source) ~[na:na]

	at java.base/java.net.Socket.connect(Unknown Source) ~[na:na]

	at org.postgresql.core.PGStream.createSocket(PGStream.java:261) ~[postgresql-42.7.8.jar!/:42.7.8]

	at org.postgresql.core.PGStream.<init>(PGStream.java:122) ~[postgresql-42.7.8.jar!/:42.7.8]

	at org.postgresql.core.v3.ConnectionFactoryImpl.tryConnect(ConnectionFactoryImpl.java:146) ~[postgresql-42.7.8.jar!/:42.7.8]

	at org.postgresql.core.v3.ConnectionFactoryImpl.openConnectionImpl(ConnectionFactoryImpl.java:289) ~[postgresql-42.7.8.jar!/:42.7.8]

	... 143 common frames omitted



