package com.svedentsov.kafka.factory;

import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.Closeable;

/**
 * Фабрика для создания экземпляров {@link KafkaConsumer}.
 * В отличие от {@link ProducerFactory}, данная фабрика <b>не кэширует</b> создаваемые
 * экземпляры, так как {@link KafkaConsumer} не является потокобезопасным. Каждый вызов
 * create-метода должен возвращать новый экземпляр, жизненным циклом которого управляет
 * вызывающий код.
 */
public interface ConsumerFactory extends Closeable {

    /**
     * Создаёт <b>новый</b> экземпляр {@link KafkaConsumer} для строкового формата.
     *
     * @param topicName имя топика (может использоваться для получения специфичной конфигурации).
     * @return новый экземпляр {@link KafkaConsumer<String, String>}.
     */
    KafkaConsumer<String, String> createStringConsumer(String topicName);

    /**
     * Создаёт <b>новый</b> экземпляр {@link KafkaConsumer} для Avro-формата.
     *
     * @param topicName имя топика (может использоваться для получения специфичной конфигурации).
     * @return новый экземпляр {@link KafkaConsumer<String, Object>}.
     */
    KafkaConsumer<String, Object> createAvroConsumer(String topicName);

    /**
     * Закрывает ресурсы, принадлежащие самой фабрике.
     * В реализации по умолчанию этот метод пуст, так как фабрика не владеет созданными консьюмерами.
     */
    @Override
    void close();
}
