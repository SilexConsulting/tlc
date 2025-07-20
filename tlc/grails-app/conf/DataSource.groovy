// Common dataSource settings
dataSource {
    pooled = true
    driverClassName = System.getProperty('hibernate.connection.driver_class') ?: 'com.mysql.cj.jdbc.Driver'
    username = System.getProperty('hibernate.connection.username')
    password = System.getProperty('hibernate.connection.password')
    url = System.getProperty('hibernate.connection.url')
    dialect = System.getProperty('hibernate.dialect') ?: 'org.hibernate.dialect.MySQLDialect'
}

// Hibernate caching settings
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.region.factory_class = 'org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory'
    singleSession = true
    flush.mode = 'auto'
    show_sql = false
    format_sql = true
}

environments {
    development {
        dataSource {
            dbCreate = "update"
        }
    }
    test {
        dataSource {
            dbCreate = "update"
        }
    }
    production {
        dataSource {
            dbCreate = "update"
            properties {
                maxActive = 25                                  // Allow 16 active 'user' connections + the task.queue.limit (default is 8) + 1 for the task scanner
                maxIdle= 12                                     // Allow about half the connections to be idle at the same time before closing the excess
                minEvictableIdleTimeMillis = 1000 * 60 * 5      // Allow a connection to be idle for 5 minutes before being eligible for eviction
                timeBetweenEvictionRunsMillis = 1000 * 60 * 5   // Run the eviction routine every 5 minutes
                numTestsPerEvictionRun = 4                      // Evict up to 4 idle connections in any eviction run
                testOnBorrow = true                             // Ensure the connection is alive before giving it to us (consider db server, firewall and O/S TCP/IP timeouts)
                testWhileIdle = false                           // Don't check connections when they're idle
                testOnReturn = false                            // Don't check a connection is alive when we give it back
                validationQuery = 'SELECT 1'                    // The SQL statement (must return at least one row) to use for testing - (Oracle would be "SELECT 1 FROM DUAL")
            }
        }
    }
}