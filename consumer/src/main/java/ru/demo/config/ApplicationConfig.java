package ru.demo.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.demo.service.SoapRequestSender;

import java.util.HashMap;
import java.util.Map;


@EnableKafka
@Configuration
public class ApplicationConfig {
    private static final Logger log = LoggerFactory.getLogger(ApplicationConfig.class);
    public final String topicName;

    public ApplicationConfig(@Value("${spring.kafka.topic}") String topicName) {
        this.topicName = topicName;
    }

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServersConfig;
    @Value("${spring.kafka.consumer.group-id}")
    private String groupID;

    @Bean
    public ConsumerFactory<String, String> productConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersConfig);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupID);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new StringDeserializer());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(productConsumerFactory());

        return factory;
    }

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(topicName).partitions(1).replicas(1).build();
    }


    @Bean
    public KafkaClient stringValueConsumer(SoapRequestSender soapRequest) {
        return new KafkaClient(soapRequest);
    }


    public static class KafkaClient {
        private final SoapRequestSender soapRequestSender;


        public KafkaClient(SoapRequestSender soapRequestSender) {
            this.soapRequestSender = soapRequestSender;
        }

        @KafkaListener(topics = "${spring.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
        public void consume(ConsumerRecord<String, String> record) throws Exception {
            String key = record.key();
            String value = record.value();
            log.info(String.format("key -> " + key + ", value -> " + value));
            soapRequestSender.choiceOfMethodAction(key, value);
        }

    }
}
