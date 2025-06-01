package com.svedentsov.kafka.config;

import org.aeonbits.owner.Config;

/**
 * Интерфейс для загрузки конфигурации Kafka с использованием библиотеки Owner.
 * Конфигурационные параметры загружаются из файла kafka.properties.
 */
@Config.Sources("file:resources/kafka.properties")
public interface KafkaConfig extends Config {
    /**
     * Адреса Kafka брокеров для подключения, разделенные запятыми.
     */
    @Key("kafka.bootstrap.servers")
    @DefaultValue("localhost:9092")
    String bootstrapServers();

    /**
     * Идентификатор группы для консюмера Kafka.
     */
    @Key("kafka.group.id")
    @DefaultValue("test-group")
    String groupId();

    /**
     * Путь к файлу truststore, содержащему доверенные сертификаты для SSL соединений.
     */
    @Key("kafka.ssl.truststore.location")
    String sslTruststoreLocation();

    /**
     * Пароль для доступа к файлу truststore.
     */
    @Key("kafka.ssl.truststore.password")
    String sslTruststorePassword();

    /**
     * Путь к файлу keystore, содержащему клиентские сертификаты и ключи для SSL соединений.
     */
    @Key("kafka.ssl.keystore.location")
    String sslKeystoreLocation();

    /**
     * Пароль для доступа к файлу keystore.
     */
    @Key("kafka.ssl.keystore.password")
    String sslKeystorePassword();

    /**
     * Пароль для ключа в файле keystore.
     */
    @Key("kafka.ssl.key.password")
    String sslKeyPassword();

    /**
     * Алгоритм проверки идентификации конечной точки SSL.
     */
    @Key("kafka.ssl.endpoint.identification.algorithm")
    @DefaultValue("")
    String sslEndpointIdentificationAlgorithm();
}
