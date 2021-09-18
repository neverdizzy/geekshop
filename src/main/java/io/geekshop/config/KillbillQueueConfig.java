// package io.geekshop.config;
//
// import lombok.extern.slf4j.Slf4j;
// import org.killbill.bus.DefaultPersistentBus;
// import org.killbill.commons.embeddeddb.mysql.MySQLStandaloneDB;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
//
// import javax.sql.DataSource;
// import java.util.Properties;
//
// /**
//  * @author bo.chen
//  * @date 2021/1/26
//  **/
// @Slf4j
// @Configuration
// public class KillbillQueueConfig {
//
//     @Value("${databaseName}")
//     private String databaseName;
//
//     @Value("${username}")
//     private String username;
//
//     @Value("${password}")
//     private String password;
//
//     @Value("${jdbcConnectionString}")
//     private String jdbcConnectionString;
//
//     @Value("${useMariaDB}")
//     private Boolean useMariaDB;
//
//     @Bean
//     public MySQLStandaloneDB mySQLStandaloneDB() {
//         MySQLStandaloneDB mySQLStandaloneDB = new MySQLStandaloneDB(databaseName, username, password, jdbcConnectionString, useMariaDB);
//         log.info("databaseName:{}, username:{}, password:{}, jdbcConnectionString:{}, useMariaDB:{}", databaseName, username, password, jdbcConnectionString, useMariaDB);
//         return mySQLStandaloneDB;
//     }
//
//     @Bean
//     public DefaultPersistentBus defaultPersistentBus() {
//         DataSource dataSource = null;
//         final Properties properties = new Properties();
//         properties.setProperty("org.killbill.persistent.bus.main.inMemory", "false");
//         properties.setProperty("org.killbill.persistent.bus.main.queue.mode", "STICKY_POLLING");
//         properties.setProperty("org.killbill.persistent.bus.main.max.failure.retry", "3");
//         properties.setProperty("org.killbill.persistent.bus.main.claimed", "100");
//         properties.setProperty("org.killbill.persistent.bus.main.claim.time", "5m");
//         properties.setProperty("org.killbill.persistent.bus.main.sleep", "100");
//         properties.setProperty("org.killbill.persistent.bus.main.off", "false");
//         properties.setProperty("org.killbill.persistent.bus.main.nbThreads", "1");
//         properties.setProperty("org.killbill.persistent.bus.main.queue.capacity", "3000");
//         properties.setProperty("org.killbill.persistent.bus.main.tableName", "bus_events");
//         properties.setProperty("org.killbill.persistent.bus.main.historyTableName", "bus_events_history");
//
//         DefaultPersistentBus bus = new DefaultPersistentBus(dataSource, properties);
//         return bus;
//     }
// }
