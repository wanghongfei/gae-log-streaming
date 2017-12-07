package org.fh.gae.streaming.task;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

public class ChargingTask {
    public void run(String appname) {
        SparkConf conf = new SparkConf().setAppName(appname);
        JavaSparkContext sc = new JavaSparkContext(conf);

        // 读日志
        JavaRDD<String> allLogRdd = sc.textFile("/Users/whf/projects/open/gae-log-streaming/data/all.log");
        // 日志分类
        JavaRDD<String> exposeLogRdd = allLogRdd.filter((line) -> line.startsWith("1"));
        JavaRDD<String> searchLogRdd = allLogRdd.filter((line) -> line.startsWith("0"));

        // 曝光日志pair
        JavaPairRDD<String, ExposeLog> exposePairRdd = exposeLogRdd.mapToPair((line) -> {
            String[] terms = line.split("\t");
            if (terms.length != 4) {
                return null;
            }

            String sid = terms[1];
            ExposeLog exposeLog = new ExposeLog(Long.parseLong(terms[2]));
            return new Tuple2<>(sid, exposeLog);
        });

        // 检索日志pair
        JavaPairRDD<String, SearchLog> searchPairRdd = searchLogRdd.mapToPair((line) -> {
            String[] terms = line.split("\t");
            if (terms.length != 20) {
                return null;
            }

            String sid = terms[1];
            SearchLog searchLog = new SearchLog(sid, Long.parseLong(terms[19]), Long.parseLong(terms[2]));
            return new Tuple2<>(sid, searchLog);
        });

        // join
        JavaPairRDD<String, Tuple2<SearchLog, ExposeLog>> joinedPairRdd = searchPairRdd.join(exposePairRdd);

        // map
        JavaPairRDD<String, JoinedLog> joinRdd = joinedPairRdd.mapValues((tuple) -> {
            SearchLog searchLog = tuple._1;
            // ExposeLog exposeLog = tuple._2;

            return new JoinedLog(searchLog.getSid(), searchLog.getBid(), searchLog.getTs());
        });

        joinRdd.saveAsTextFile("/Users/whf/projects/open/gae-log-streaming/target/joined");
    }

    private long toLong(String str) {
        try {
            return Long.parseLong(str);

        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private int toInt(String str) {
        try {
            return Integer.parseInt(str);

        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
