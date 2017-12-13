package org.fh.gae.streaming.task;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.fh.gae.streaming.task.kafka.KafkaSender;
import org.fh.gae.streaming.task.log.ChargeLog;
import org.fh.gae.streaming.task.log.ExposeLog;
import org.fh.gae.streaming.task.log.SearchLog;
import scala.Tuple2;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ChargingTask {
    /**
     * 从kafka创建两条数据流，分别接收检索日志和曝光日志，并对他们实时join，生成扣费信息再次投到kafka中
     * @param appname
     * @throws Exception
     */
    public void run(String appname) throws Exception {

        SparkConf conf = new SparkConf().setAppName(appname);
        JavaStreamingContext ctx = new JavaStreamingContext(conf, Durations.seconds(10));


        // 检索日志流
        JavaDStream<String> searchStream = createSearchStream(ctx);
        // JavaDStream<String> searchStream = createKafkaSearchStream(ctx);
        // 曝光日志流
        JavaDStream<String> exposeStream = createExposeStream(ctx);
        // JavaDStream<String> exposeStream = createKafkaExposeStream(ctx);

        ctx.remember(Durations.seconds(20));
        final JavaPairRDD<String, ExposeLog>[] lastRdd = new JavaPairRDD[1];


        // 曝光日志pair
        JavaPairDStream<String, ExposeLog> exposePairStream = exposeStream
                // 单词切分
                .map( line -> line.split("\t") )
                // 按长度过虑
                .filter( terms -> terms.length == 4 )
                // 转换成pair
                .mapToPair(terms -> {
                    String sid = terms[1];
                    ExposeLog exposeLog = new ExposeLog(Long.parseLong(terms[2]));
                    return new Tuple2<>(sid, exposeLog);
                })
                // 去重
                .reduceByKey((log1, log2) -> log1)
                // 过虑掉上个批次出现过的曝光记录
                .transformToPair((Function<JavaPairRDD<String, ExposeLog>, JavaPairRDD<String, ExposeLog>>) pairRdd -> {
                    if (null == lastRdd[0]) {
                        lastRdd[0] = pairRdd;
                        return pairRdd;
                    }

                    pairRdd = pairRdd.subtractByKey(lastRdd[0]);
                    lastRdd[0] = pairRdd;

                    return pairRdd;
                });

        // 检索日志pair
        JavaPairDStream<String, SearchLog> searchPairStream = searchStream
                // 单词切分
                .map( line -> line.split("\t") )
                // 按长度过虑
                .filter( terms -> terms.length == 20 )
                // 转换成pair
                .mapToPair((terms) -> {
                    String sid = terms[1];
                    SearchLog searchLog = SearchLog.ofString(terms);

                    return new Tuple2<>(sid, searchLog);
                });

        // 两日志JOIN拼接
        JavaPairDStream<String, Tuple2<SearchLog, ExposeLog>> joinedPairStream = searchPairStream.join(exposePairStream);
        // joinedPairStream.print();

        // 遍历拼接结果
        // 发kafka消息
        joinedPairStream.foreachRDD(rdd -> {
            rdd.foreachPartition(it -> {
                while (it.hasNext()) {
                    Tuple2<String, Tuple2<SearchLog, ExposeLog>> tuple = it.next();

                    SearchLog searchLog = tuple._2._1;
                    ExposeLog exposeLog = tuple._2._2;
                    ChargeLog chargeLog = new ChargeLog();
                    chargeLog.setExposeTs(exposeLog.getTs());
                    chargeLog.setSearchLog(searchLog);

                    try {
                        KafkaSender.getInstance().send(chargeLog.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        });


        ctx.start();
        ctx.awaitTermination();
    }

    private JavaDStream<String> createExposeStream(JavaStreamingContext ctx) {
        return ctx.socketTextStream("whf-mbp.local", 8888);
    }

    private JavaDStream<String> createSearchStream(JavaStreamingContext ctx) {
        return ctx.socketTextStream("whf-mbp.local", 7777).window(Durations.seconds(20), Durations.seconds(10));
    }

    private JavaDStream<String> createKafkaSearchStream(JavaStreamingContext ctx) throws IOException {
        Properties props = new Properties();
        props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("kafka-search.properties"));

        Map<String, Integer> topicMap = new HashMap<>();
        topicMap.put(props.getProperty("topic"), 1);


        return KafkaUtils.createStream(ctx, props.getProperty("zk-list"), props.getProperty("group"), topicMap)
                .map( tuple -> tuple._2 );
    }

    private JavaDStream<String> createKafkaExposeStream(JavaStreamingContext ctx) throws IOException {
        Properties props = new Properties();
        props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("kafka-expose.properties"));

        Map<String, Integer> topicMap = new HashMap<>();
        topicMap.put(props.getProperty("topic"), 1);

        return KafkaUtils.createStream(ctx, props.getProperty("zk-list"), props.getProperty("group"), topicMap)
                .map( tuple -> tuple._2 );
    }
}
