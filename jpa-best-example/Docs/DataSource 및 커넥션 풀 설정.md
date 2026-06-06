# DataSource 및 Connection Pool Settings

## HikariCP 설정 커스터마이징
springboot 는 HikariCP 를 기본 커넥션 풀로 사용한다 <br>
spring-boot-starter-data-jpa 및 spring-boot-starter-jdbc 를 추가하면 기본 설정으로 HikariCP 종속성이 추가 된다 <br>

프로덕션 환경을 위한 설정은 블라드미하체아의 FlexyPool 을 사용하는 것이다 <br>
link: https://vladmihalcea.com/tutorials/flexypool/

HikariCP 설정은 application.yml 을 통해 조정이 가능하다 <br>
각 파라미터 값은 spring.datasource.hikari.* 로 시작되는 속성을 변경하면 된다 <br>
```yaml
spring:
  datasource:
    hikari:
      connection-timeout: 50000
      idle-timeout: 300000
      max-lifetime: 900000
      maximum-pool-size: 8
      minimum-idle: 8
      pool-name: MyPool
      connection-test-query: select 1 from dual
```

### DataSourceBuilder 를 통한 최적화
위 yml 설정을 기반으로 DataSourceBuilder 를 생성하여 파이프라인을 구성할 수 있다.
```java
@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public HikariDataSource dataSource(DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }
}

```

2번째 방법으로는  application.yml 을 사용하지 않고 직접 지정을 할 수도 있다 <br>
```java
@Bean
public HikariDataSource dataSource() {
    HikariDataSource hikariDataSource = new HikariDataSource();
    hikariDataSource.setConnectionTimeout(50_000);
    hikariDataSource.setIdleTimeout(300_000);
    // 위처럼 set 으로 설정이 가능하다.
    
    return HikariDataSource;
}
```

## 2개의 커넥션 풀을 갖는 2개의 데이터 소스 구성 방법
```java
@Entity
public class Author {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //
}

@Entity
public class Book {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}

```
같은 엔티티를 구성하고 레포지토리 인터페이스를 만들 떄는 각각 다른 패키지에 구성을 해야한다 <br>
dataSource 생성이 패키지 경로에 따라서 다르게 생성이 되기 때문이다. <br>

보통 위 같은 경우는 레플리카 db 가 있을 떄 read 작업은 slave DB, cud 작업은 master DB 를 이용하게 끔한다 <br>
DB는 아마 설정에 따라 다르긴 하겠지만, 5분 마다 동기화 10분마다 동기화 등, 작업이 있을 것이다 <br>

이에 대해 설정하기 위해서는 application.yml 에 총 2개의 설정을 해야한다. <br>
```yaml
app:
  datasource:
    master:
      hikari:
        username: hkjin01
        password: 1234
        url: "master DB -> CUD"
        connection-timeout: 50000

    slave:
      hikari:
        username: hkjin02
        password: 1234
        url: "Slave DB -> R"
        connection-timeout: 50000
```

총 2개의 데이터 소스에 대한 설정이 있다. 위 설정을 config 클래스에서 직접 설정을 해줄 필요가 있다 <br>


기본적으로 작업이 많을 slave DB 를 우선적으로 연결하기 위해서 @Primary 를 붙여준다.
```java
@Configuration
public class DataSourceConfig {

    @Bean(name = "masterDataSource")
    @ConfigurationProperties(prefix = "app.datasource.master.hikari")
    public HikariDataSource masterDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setPoolName("MasterHikariPool");
        return dataSource;
    }

    @Bean(name = "slaveDataSource")
    @ConfigurationProperties(prefix = "app.datasource.slave.hikari")
    public HikariDataSource slaveDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setReadOnly(true);
        dataSource.setPoolName("SlaveHikariPool");
        return dataSource;
    }
}

```

다음 단계로 JPA 가 작업을 보고 어떤 데이터 소스를 이용할지를 구성해서 스프링부트에 알려주기 위한 설정을 잡아줘야 한다 <br>
```java
public class ReadWriteRoutingDataSource extends AbstractRoutingDataSource {
    
    @Override
    protected Object determineCurrentLookupKey() {
        return TransactionSynchronizationManager.isCurrentTransactionReadOnly() ? "slave" : "master";
    }
}

@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

    @Bean(name = "masterDataSource")
    @ConfigurationProperties(prefix = "app.datasource.master.hikari")
    public HikariDataSource masterDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setPoolName("MasterHikariPool");
        return dataSource;
    }

    @Bean(name = "slaveDataSource")
    @ConfigurationProperties(prefix = "app.datasource.slave.hikari")
    public HikariDataSource slaveDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setReadOnly(true);
        dataSource.setPoolName("SlaveHikariPool");
        return dataSource;
    }

    @Bean(name = "routingDataSource")
    public DataSource routingDataSource(
            @Qualifier("masterDataSource") DataSource masterDataSource,
            @Qualifier("slaveDataSource") DataSource slaveDataSource) {

        ReadWriteRoutingDataSource routingDataSource = new ReadWriteRoutingDataSource();

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("master", masterDataSource);
        dataSourceMap.put("slave", slaveDataSource);

        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(slaveDataSource); // 기본값은 Slave

        return routingDataSource;
    }

    @Bean
    @Primary
    public DataSource dataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }

    @Bean
    public PlatformTransactionManager transactionManager(
            @Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
```

마지막으로 JPA 를 사용한다고 하면 아래 설정을 잡아줘야 한다.
```java
@Configuration
@EnableJpaRepositories(
        basePackages = "org.hyeonqz.jpabestexample.*",
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager"
)
public class JpaConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("dataSource") DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.company.payment.entity");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "validate");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        properties.setProperty("hibernate.show_sql", "false");
        em.setJpaProperties(properties);

        return em;
    }
}

```

위 처럼 설정을하고 @Transactional(readOnly = true) 옵션을 주게 되면 자동으로 slave db 를 조회하게 될 것이다 <br>
일반 @Transactional 이 걸려있으면 master db 를 사용하게 될 것이다 <br>
물론 위 어노테이션 안에 read 작업이 있을 수도 있지만, 분기 처리는 잘 될 것이다 <br>

