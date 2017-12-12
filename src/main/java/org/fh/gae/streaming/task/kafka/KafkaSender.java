package org.fh.gae.streaming.task.kafka;

import kafka.producer.KeyedMessage;
import kafka.producer.Producer;
import kafka.producer.ProducerConfig;
import scala.collection.JavaConverters;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class KafkaSender implements Serializable {
    public static final String BROKER_LIST = "10.115.238.30:8701";
    public static final String SERIALIZER = "kafka.serializer.StringEncoder";

    private static Producer<String, String> producer;
    private static KafkaSender kafkaSender;

    public static final String TOPIC = "dev-gae-charge";

    private KafkaSender() {
        Properties props = new Properties();
        props.put("serializer.class", SERIALIZER);
        props.put("metadata.broker.list", BROKER_LIST);

        ProducerConfig config = new ProducerConfig(props);

        producer = new Producer<String, String>(config);
    }

    public static KafkaSender getInstance() {
        if (null == kafkaSender) {
            kafkaSender = new KafkaSender();
        }

        return kafkaSender;
    }

    public void send(String message) {
        KeyedMessage<String, String> msg = new KeyedMessage<>(TOPIC, message);

        producer.send(JavaConverters.asScalaIterableConverter(Arrays.asList(msg)).asScala().toSeq());
    }

    public void send(List<String> msgList) {
        List<KeyedMessage<String, String>> keyedMessages = msgList.stream()
                .map( msg -> new KeyedMessage<String, String>(TOPIC, msg) )
                .collect(Collectors.toList());

        producer.send(JavaConverters.asScalaIterableConverter(keyedMessages).asScala().toSeq());
    }
}
