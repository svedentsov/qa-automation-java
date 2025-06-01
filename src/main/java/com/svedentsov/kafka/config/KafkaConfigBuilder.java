package com.svedentsov.kafka.config;

import lombok.extern.slf4j.Slf4j;
import org.aeonbits.owner.ConfigFactory;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;

import java.util.Properties;

/**
 * Класс KafkaConfigBuilder предоставляет методы для создания конфигураций продюсеров и консюмеров Kafka.
 */
@Slf4j
public class KafkaConfigBuilder {

    private static final KafkaConfig config = ConfigFactory.create(KafkaConfig.class);

    /**
     * Возвращает конфигурацию продюсера Kafka для указанного топика.
     *
     * @param topic имя топика Kafka
     * @return конфигурация продюсера Kafka
     */
    public static Properties getProducerConfig(String topic) {
        log.info("Создание конфигурации продюсера для топика: {}", topic);
        Properties props = createDefaultProducerConfig();
        customizeProducerConfig(props, topic);
        return props;
    }

    /**
     * Возвращает конфигурацию консюмера Kafka для указанного топика.
     *
     * @param topic имя топика Kafka
     * @return конфигурация консюмера Kafka
     */
    public static Properties getConsumerConfig(String topic) {
        log.info("Создание конфигурации консюмера для топика: {}", topic);
        Properties props = createDefaultConsumerConfig();
        customizeConsumerConfig(props, topic);
        return props;
    }

    /**
     * Создает конфигурацию продюсера по умолчанию.
     *
     * @return конфигурация продюсера по умолчанию
     */
    private static Properties createDefaultProducerConfig() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.bootstrapServers());
        configureSSL(props);
        return props;
    }

    /**
     * Создает конфигурацию консюмера по умолчанию.
     *
     * @return конфигурация консюмера по умолчанию
     */
    private static Properties createDefaultConsumerConfig() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.bootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, config.groupId());
        configureSSL(props);
        return props;
    }

    /**
     * Настраивает SSL параметры для конфигурации Kafka.
     *
     * @param props объект Properties, в который добавляются SSL параметры
     */
    private static void configureSSL(Properties props) {
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, config.sslTruststoreLocation());
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, config.sslTruststorePassword());
        props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, config.sslKeystoreLocation());
        props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, config.sslKeystorePassword());
        props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, config.sslKeyPassword());
        props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, config.sslEndpointIdentificationAlgorithm());
    }

    /**
     * Настраивает конфигурацию продюсера для определённого топика.
     *
     * @param props объект Properties, содержащий конфигурацию продюсера
     * @param topic имя топика Kafka
     */
    private static void customizeProducerConfig(Properties props, String topic) {
        if ("special-topic".equals(topic)) {
            log.info("Настройка конфигурации продюсера для специального топика");
            props.put(ProducerConfig.ACKS_CONFIG, "all");
            props.put(ProducerConfig.RETRIES_CONFIG, "3");
        } else {
            log.warn("Используется конфигурация продюсера по умолчанию для топика: {}", topic);
        }
    }

    /**
     * Настраивает конфигурацию консюмера для определённого топика.
     *
     * @param props объект Properties, содержащий конфигурацию консюмера
     * @param topic имя топика Kafka
     */
    private static void customizeConsumerConfig(Properties props, String topic) {
        if ("special-topic".equals(topic)) {
            log.info("Настройка конфигурации консюмера для специального топика");
            props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        } else {
            log.warn("Используется конфигурация консюмера по умолчанию для топика: {}", topic);
        }
    }
}
