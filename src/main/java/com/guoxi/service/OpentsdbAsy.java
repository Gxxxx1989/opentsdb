package com.guoxi.service;


import ch.qos.logback.core.net.SyslogOutputStream;
import com.aliyun.hitsdb.client.HiTSDB;
import com.aliyun.hitsdb.client.HiTSDBClientFactory;
import com.aliyun.hitsdb.client.HiTSDBConfig;
import com.aliyun.hitsdb.client.callback.BatchPutCallback;
import com.aliyun.hitsdb.client.callback.BatchPutDetailsCallback;
import com.aliyun.hitsdb.client.value.Result;
import com.aliyun.hitsdb.client.value.request.Point;
import com.aliyun.hitsdb.client.value.response.batch.DetailsResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OpentsdbAsy {

    //异步写数据
    public long putDataAsy(int count, int batchNum) {
        final AtomicLong num = new AtomicLong();
        // 回调对象
        BatchPutDetailsCallback bc = new BatchPutDetailsCallback() {

            @Override
            public void response(String address, List<Point> points, DetailsResult result) {
                long afterNum = num.addAndGet(points.size());
                System.out.println("成功处理" + points.size() + ",已处理" + afterNum);
            }
        };
        BatchPutCallback cb = new BatchPutCallback() {

            @Override
            public void response(String address, List<Point> input, Result output) {
                long afterNum = num.addAndGet(input.size());
                System.out.println("成功处理" + input.size() + ",已处理" + afterNum);
            }

            @Override
            public void failed(String address, List<Point> input, Exception ex) {
                ex.printStackTrace();
                long afterNum = num.addAndGet(input.size());
                System.out.println("失败处理" + input.size() + ",已处理" + afterNum);
            }

        };

        HiTSDBConfig config = HiTSDBConfig
                // 配置地址，第一个参数可以是 HiTSDB 的域名或 IP。第二个参数表示 HiTSDB 端口。
                .address("192.168.155.163", 4242)

                // 只读开关，默认为 false。当 readonly 设置为 true 时，异步写开关会被关闭。
                .readonly(false)

                // 网络连接池大小，默认为64。
                //.httpConnectionPool(64)

                // HTTP 等待时间，单位为秒，默认为90秒。
                .httpConnectTimeout(90)

                // IO 线程数，默认为1。
                .ioThreadCount(400)

                // 异步写开关。默认为 true。推荐异步写。
                .asyncPut(true)

                // 异步写相关，客户端缓冲队列长度，默认为10000。
                //.batchPutBufferSize(20000)

                // 异步写相关，缓冲队列消费线程数，默认为 1。
                //.batchPutConsumerThreadCount(2)

                // 异步写相关，每次批次提交给客户端点的个数，默认为 500。
                .batchPutSize(50000)

                // 异步写相关，每次等待最大时间限制，单位为 ms，默认为 300。
                .batchPutTimeLimit(300)

                // 异步写相关，写请求队列数，默认等于连接池数。可根据读写次数的比例进行配置。
                // .putRequestLimit(100)

                // 异步写相关，不限制写请求队列数，若关闭可能导致 OOM，不建议关闭。
                //.closePutRequestLimit()

                // 异步写相关，异步批量 Put 回调接口。
                .listenBatchPut(cb)
                // .listenBatchPut(new BatchPutCallback() {
                // @Override
                // public void response(String address, List<Point> input, Result output)
                // long afterNum = num.addAndGet(input.size());
                // System.out.println("成功处理" + input.size() + ",已处理" + afterNum);
                // }
                // })
                // 流量限制，设置每秒最大提交 Point 的个数。
                .maxTPS(1000000)
                .config(); // 构造 HiTSDBConfig 对象

        HiTSDB tsdb = HiTSDBClientFactory.connect(config);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            Point point = Point.metric("gx")
                    .tag("x", "x")
                    .value(new Date(), Math.random() * 100)
                    .build();

            // 1秒提交1次
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                System.out.println("处理失败");
//            }
            tsdb.put(point);
            System.out.println("现在插入第" + (i + 1) + "条数据");

        }
        long endTime = System.currentTimeMillis();
        System.out.println("耗时：" + (endTime - startTime) + "毫秒");
        return endTime - startTime;

    }


}
