package com.svedentsov.kafka.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;

import java.util.Properties;

import static java.util.Objects.requireNonNull;

/**
 * Провайдер конфигураций для клиентов Kafka.
 * Этот класс отвечает за создание объектов {@link Properties} для продюсеров и консюмеров.
 * В отличие от предыдущей реализации со статическими методами, этот класс является
 * stateful-компонентом, который получает конфигурацию через конструктор, что упрощает его тестирование и конфигурирование.
 */
@Slf4j
public class DefaultKafkaConfigProvider {

    private final KafkaConfig config;

    /**
     * Создает экземпляр провайдера конфигураций.
     *
     * @param config объект конфигурации, содержащий базовые настройки Kafka. Не может быть {@code null}.
     */
    public DefaultKafkaConfigProvider(KafkaConfig config) {
        this.config = requireNonNull(config, "KafkaConfig не может быть null.");
    }

    /**
     * Создает и возвращает конфигурацию для Kafka Producer.
     *
     * @param topic топик, для которого создается конфигурация. Может использоваться для кастомизации.
     * @return {@link Properties} с настройками для продюсера.
     */
    public Properties getProducerConfig(String topic) {
        log.info("Создание конфигурации продюсера для топика: {}", topic);
        Properties props = createBaseProducerConfig();
        applySslConfig(props);
        applyTopicSpecificProducerConfig(props, topic);
        log.debug("Итоговая конфигурация продюсера для топика '{}': {}", topic, props);
        return props;
    }

    /**
     * Создает и возвращает конфигурацию для Kafka Consumer.
     *
     * @param topic топик, для которого создается конфигурация.
     * @return {@link Properties} с настройками для консюмера.
     */
    public Properties getConsumerConfig(String topic) {
        log.info("Создание конфигурации консюмера для топика: {}", topic);
        Properties props = createBaseConsumerConfig();
        applySslConfig(props);
        applyTopicSpecificConsumerConfig(props, topic);
        log.debug("Итоговая конфигурация консюмера для топика '{}': {}", topic, props);
        return props;
    }

    private Properties createBaseProducerConfig() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.bootstrapServers());
        return props;
    }

    private Properties createBaseConsumerConfig() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.bootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, config.groupId());
        return props;
    }

    private void applySslConfig(Properties props) {
        if (config.sslTruststoreLocation() == null || config.sslTruststoreLocation().isBlank()) {
            log.warn("SSL конфигурация не будет применена, так как 'ssl.truststore.location' не задан.");
            return;
        }
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, config.sslTruststoreLocation());
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, config.sslTruststorePassword());
        props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, config.sslKeystoreLocation());
        props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, config.sslKeystorePassword());
        props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, config.sslKeyPassword());
        props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, config.sslEndpointIdentificationAlgorithm());
    }

    /**
     * Применяет специфичные для топика настройки продюсера.
     * Этот метод инкапсулирует логику кастомизации.
     */
    private void applyTopicSpecificProducerConfig(Properties props, String topic) {
        if ("special-topic".equals(topic)) {
            log.info("Применение специальных настроек продюсера для топика: {}", topic);
            props.put(ProducerConfig.ACKS_CONFIG, "all");
            props.put(ProducerConfig.RETRIES_CONFIG, 3);
        } else {
            log.info("Используется стандартная конфигурация продюсера для топика: {}", topic);
        }
    }

    /**
     * Применяет специфичные для топика настройки консюмера.
     */
    private void applyTopicSpecificConsumerConfig(Properties props, String topic) {
        if ("special-topic".equals(topic)) {
            log.info("Применение специальных настроек консюмера для топика: {}", topic);
            props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        } else {
            log.info("Используется стандартная конфигурация консюмера для топика: {}", topic);
        }
    }
}
