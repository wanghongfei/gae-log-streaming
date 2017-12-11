package org.fh.gae.streaming.task;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import scala.Tuple2;

import java.util.HashMap;
import java.util.Map;

public class ChargingTask {
    public void run(String appname) {
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
                .transformToPair((Function<JavaPairRDD<String, ExposeLog>, JavaPairRDD<String, ExposeLog>>) pairRdd -> {
                    if (null == lastRdd[0]) {
                        lastRdd[0] = pairRdd;
                        return pairRdd;
                    }

                    pairRdd = pairRdd.subtractByKey(lastRdd[0]);
                    lastRdd[0] = pairRdd;

                    return pairRdd;
                });
        exposePairStream.print();



        // 检索日志pair
        JavaPairDStream<String, SearchLog> searchPairStream = searchStream
                // 单词切分
                .map( line -> line.split("\t") )
                // 按长度过虑
                .filter( terms -> terms.length == 20 )
                // 转换成pair
                .mapToPair((terms) -> {
                    String sid = terms[1];
                    SearchLog searchLog = new SearchLog(sid, Long.parseLong(terms[19]), Long.parseLong(terms[2]));
                    return new Tuple2<>(sid, searchLog);
                });
        searchPairStream.print();

        // 两日志JOIN拼接
        JavaPairDStream<String, Tuple2<SearchLog, ExposeLog>> joinedPairStream = searchPairStream.join(exposePairStream);


        // map
        JavaPairDStream<String, JoinedLog> joinStream = joinedPairStream.mapValues((tuple) -> {
            SearchLog searchLog = tuple._1;

            return new JoinedLog(searchLog.getSid(), searchLog.getBid(), searchLog.getTs());
        });

        processResult(joinStream);


        ctx.start();
        ctx.awaitTermination();
    }

    private JavaDStream<String> createExposeStream(JavaStreamingContext ctx) {
        return ctx.socketTextStream("whf-mbp.local", 8888);
    }

    private JavaDStream<String> createSearchStream(JavaStreamingContext ctx) {
        return ctx.socketTextStream("whf-mbp.local", 7777).window(Durations.seconds(20), Durations.seconds(10));
    }

    private JavaDStream<String> createKafkaSearchStream(JavaStreamingContext ctx) {
        Map<String, Integer> topicMap = new HashMap<>();
        topicMap.put("dev-gae-search", 1);

        return KafkaUtils.createStream(ctx, "10.115.238.30:8701", "gae-log-streaming", topicMap)
                .map( tuple -> tuple._2 );
    }

    private JavaDStream<String> createKafkaExposeStream(JavaStreamingContext ctx) {
        Map<String, Integer> topicMap = new HashMap<>();
        topicMap.put("dev-gae-expose", 1);

        return KafkaUtils.createStream(ctx, "10.115.238.30:8701", "gae-log-streaming", topicMap)
                .map( tuple -> tuple._2 );
    }

    private void processResult(JavaPairDStream<String, JoinedLog> joinedStream) {
        joinedStream.print();
    }
}
