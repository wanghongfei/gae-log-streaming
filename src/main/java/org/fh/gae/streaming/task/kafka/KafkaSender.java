package org.fh.gae.streaming.task.kafka;

import kafka.producer.KeyedMessage;
import kafka.producer.Producer;
import kafka.producer.ProducerConfig;
import scala.collection.JavaConverters;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class KafkaSender {
    private Producer<String, String> producer;

    private String topic;

    public KafkaSender(Properties props) {
        ProducerConfig config = new ProducerConfig(props);
        this.topic = props.getProperty("topic");

        this.producer = new Producer<String, String>(config);
    }

    public void send(String message) {
        KeyedMessage<String, String> msg = new KeyedMessage<>(topic, message);

        producer.send(JavaConverters.asScalaIterableConverter(Arrays.asList(msg)).asScala().toSeq());
    }

    public void send(List<String> msgList) {
        List<KeyedMessage<String, String>> keyedMessages = msgList.stream()
                .map( msg -> new KeyedMessage<String, String>(topic, msg) )
                .collect(Collectors.toList());

        producer.send(JavaConverters.asScalaIterableConverter(keyedMessages).asScala().toSeq());
    }
}
