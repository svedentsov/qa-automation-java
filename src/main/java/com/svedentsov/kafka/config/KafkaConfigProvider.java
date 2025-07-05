package com.svedentsov.kafka.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;

import java.util.Properties;

import static java.util.Objects.requireNonNull;

/**
 * Провайдер конфигураций для клиентов Kafka, построенный на принципах внедрения зависимостей.
 * Этот класс отвечает за создание объектов {@link Properties} для продюсеров и консьюмеров.
 * Он инстанцируется с конкретной конфигурацией {@link KafkaConfig}, что упрощает
 * тестирование (позволяя легко подменять конфигурацию на mock-объекты) и соответствует принципам SOLID.
 */
@Slf4j
public class KafkaConfigProvider {

    private final KafkaConfig config;

    /**
     * Создает экземпляр провайдера конфигураций.
     *
     * @param config объект конфигурации, содержащий базовые настройки Kafka. Не может быть {@code null}.
     */
    public KafkaConfigProvider(KafkaConfig config) {
        this.config = requireNonNull(config, "KafkaConfig не может быть null.");
    }

    /**
     * Создает и возвращает конфигурацию для Kafka Producer.
     *
     * @param topic топик, для которого создается конфигурация. Может использоваться для кастомизации.
     *              Если null, возвращаются базовые настройки без топик-специфичных.
     * @return {@link Properties} с настройками для продюсера.
     */
    public Properties getProducerConfig(String topic) {
        log.info("Создание конфигурации продюсера для топика: {}", topic != null ? topic : "общий");
        Properties props = createBaseProducerConfig();
        applySslConfig(props);
        if (topic != null) { // Применяем топик-специфичные настройки только если топик указан
            applyTopicSpecificProducerConfig(props, topic);
        } else {
            log.debug("Топик не указан, применяются только базовые настройки продюсера.");
        }
        log.debug("Итоговая конфигурация продюсера для топика '{}': {}", topic != null ? topic : "общий", props);
        return props;
    }

    /**
     * Создает и возвращает конфигурацию для Kafka Consumer.
     * Конфигурация включает базовые настройки, SSL (если задан) и может быть
     * дополнена специфичными настройками для конкретного топика.
     *
     * @param topic топик, для которого создается конфигурация. Не может быть {@code null}.
     * @return {@link Properties} с полным набором настроек для консюмера.
     */
    public Properties getConsumerConfig(String topic) {
        requireNonNull(topic, "Имя топика не может быть null для конфигурации консьюмера.");
        log.info("Создание конфигурации консюмера для топика: {}", topic);
        Properties props = createBaseConsumerConfig();
        applySslConfig(props);
        applyTopicSpecificConsumerConfig(props, topic);
        log.debug("Итоговая конфигурация консюмера для топика '{}': {}", topic, props);
        return props;
    }

    /**
     * Создает базовый набор свойств для продюсера.
     */
    private Properties createBaseProducerConfig() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.bootstrapServers());
        // Здесь можно добавить другие общие для всех продюсеров настройки
        return props;
    }

    /**
     * Создает базовый набор свойств для консьюмера.
     */
    private Properties createBaseConsumerConfig() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.bootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, config.groupId());
        // Важная настройка: по умолчанию авто-коммит лучше выключать и управлять смещениями вручную для большей надежности.
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        // Определяет, с какого места начать чтение, если нет сохраненного смещения.
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        return props;
    }

    /**
     * Применяет SSL-конфигурацию к переданному объекту Properties, если она задана.
     */
    private void applySslConfig(Properties props) {
        if (config.sslTruststoreLocation() == null || config.sslTruststoreLocation().isBlank()) {
            log.debug("SSL конфигурация не будет применена, так как 'ssl.truststore.location' не задан.");
            return;
        }
        log.info("Применение SSL конфигурации.");
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, config.sslTruststoreLocation());
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, config.sslTruststorePassword());
        if (config.sslKeystoreLocation() != null && !config.sslKeystoreLocation().isBlank()) {
            props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, config.sslKeystoreLocation());
            props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, config.sslKeystorePassword());
            props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, config.sslKeyPassword());
        }
        props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, config.sslEndpointIdentificationAlgorithm());
    }

    /**
     * Применяет специфичные для топика настройки продюсера.
     */
    private void applyTopicSpecificProducerConfig(Properties props, String topic) {
        if ("special-topic".equals(topic)) {
            log.info("Применение специальных настроек продюсера для топика: {}", topic);
            props.put(ProducerConfig.ACKS_CONFIG, "all");
            props.put(ProducerConfig.RETRIES_CONFIG, 3);
        } else {
            log.info("Использование стандартной конфигурации продюсера для топика: {}", topic);
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
            log.info("Использование стандартной конфигурации консюмера для топика: {}", topic);
        }
    }
}
