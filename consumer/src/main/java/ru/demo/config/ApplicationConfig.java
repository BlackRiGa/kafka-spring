package ru.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.JacksonUtils;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import ru.demo.model.StringValue;
import ru.demo.service.SoapRequestSender;
import ru.demo.service.soapMessageFactory.SoapMessageFactoryFindObject;
import ru.demo.service.soapMessageFactory.SoapMessageFactoryFindObjects;
import ru.demo.service.soapMessageFactory.SoapMessageFactoryUpdateObject;
import ru.demo.service.stringValue.StringValueConsumer;
import ru.demo.service.stringValue.StringValueConsumerLogger;

import javax.xml.soap.SOAPMessage;
import java.util.List;

import static org.springframework.kafka.support.serializer.JsonDeserializer.TYPE_MAPPINGS;

@Configuration
public class ApplicationConfig {
    private static final Logger log = LoggerFactory.getLogger(ApplicationConfig.class);
    public final String topicName;

    public ApplicationConfig(@Value("${application.kafka.topic}") String topicName) {
        this.topicName = topicName;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return JacksonUtils.enhancedObjectMapper();
    }

    @Bean
    public ConsumerFactory<String, StringValue> consumerFactory(
            KafkaProperties kafkaProperties, ObjectMapper mapper) {
        var props = kafkaProperties.buildConsumerProperties();
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(TYPE_MAPPINGS, "ru.demo.model.StringValue:ru.demo.model.StringValue");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 3);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 3_000);

        var kafkaConsumerFactory = new DefaultKafkaConsumerFactory<String, StringValue>(props);
        kafkaConsumerFactory.setValueDeserializer(new JsonDeserializer<>(mapper));
        return kafkaConsumerFactory;
    }

    @Bean("listenerContainerFactory")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, StringValue>>
    listenerContainerFactory(ConsumerFactory<String, StringValue> consumerFactory) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, StringValue>();
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        factory.setConcurrency(1);
        factory.getContainerProperties().setIdleBetweenPolls(1_000);
        factory.getContainerProperties().setPollTimeout(1_000);

        var executor = new SimpleAsyncTaskExecutor("k-consumer-");
        executor.setConcurrencyLimit(10);
        var listenerTaskExecutor = new ConcurrentTaskExecutor(executor);
        factory.getContainerProperties().setListenerTaskExecutor(listenerTaskExecutor);
        return factory;
    }

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(topicName).partitions(1).replicas(1).build();
    }

    @Bean
    public StringValueConsumer stringValueConsumerLogger() {
        return new StringValueConsumerLogger();
    }

    @Bean
    public KafkaClient stringValueConsumer(StringValueConsumer stringValueConsumer, SoapRequestSender soapRequestSender, SoapMessageFactoryFindObjects soapMessageFactoryFindObjects, SoapMessageFactoryFindObject soapMessageFactoryFindObject, SoapMessageFactoryUpdateObject soapMessageFactoryUpdateObject) {
        return new KafkaClient(stringValueConsumer, soapRequestSender, soapMessageFactoryFindObject, soapMessageFactoryFindObjects, soapMessageFactoryUpdateObject);
    }

    public static class KafkaClient {
        private final StringValueConsumer stringValueConsumer;
        private final SoapRequestSender soapRequestSender;
        private final SoapMessageFactoryFindObject soapMessageFactoryFindObject;
        private final SoapMessageFactoryFindObjects soapMessageFactoryFindObjects;
        private final SoapMessageFactoryUpdateObject soapMessageFactoryUpdateObject;

        public KafkaClient(StringValueConsumer stringValueConsumer, SoapRequestSender soapRequestSender, SoapMessageFactoryFindObject soapMessageFactoryFindObject, SoapMessageFactoryFindObjects soapMessageFactoryFindObjects, SoapMessageFactoryUpdateObject soapMessageFactoryUpdateObject) {
            this.stringValueConsumer = stringValueConsumer;
            this.soapRequestSender = soapRequestSender;
            this.soapMessageFactoryFindObject = soapMessageFactoryFindObject;
            this.soapMessageFactoryFindObjects = soapMessageFactoryFindObjects;
            this.soapMessageFactoryUpdateObject = soapMessageFactoryUpdateObject;
        }

        @KafkaListener(
                topics = "${application.kafka.topic}",
                containerFactory = "listenerContainerFactory")
        public void listen(@Payload List<StringValue> values) throws Exception {
            int i = 2;

            if (i == 0) {
                SOAPMessage soapMessage = soapMessageFactoryFindObject.createSoapRequestFindObject();
                System.out.println(soapRequestSender.sendSoapRequest(soapMessage));
            }
            if (i == 1) {
                SOAPMessage soapMessage = soapMessageFactoryFindObjects.createSoapRequestFindObjects();
                System.out.println(soapRequestSender.sendSoapRequest(soapMessage));
            }
            if (i == 2) {
                SOAPMessage soapMessage = soapMessageFactoryUpdateObject.createUpdateObjectSoapRequest();
                System.out.println(soapRequestSender.sendSoapRequest(soapMessage));
            }
//            if (i = 3) {
//            }

            log.info("values, values.size:{}", values.size());
            stringValueConsumer.accept(values);
        }
    }
}
