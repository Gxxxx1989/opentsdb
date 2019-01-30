package com.guoxi.service;

import com.aliyun.hitsdb.client.HiTSDB;
import com.aliyun.hitsdb.client.HiTSDBClientFactory;
import com.aliyun.hitsdb.client.HiTSDBConfig;
import com.aliyun.hitsdb.client.value.request.Query;
import com.aliyun.hitsdb.client.value.request.SubQuery;
import com.aliyun.hitsdb.client.value.response.QueryResult;
import com.aliyun.hitsdb.client.value.type.Aggregator;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class QueryOpentsdb {

    public List<QueryResult> QueryTsdb(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String startDate="2019-01-23 00:00:00";

        String endDate="2019-02-02 00:00:00";

        Date startTime = null;

        Date endTime=null;
        try {
            startTime=sdf.parse(startDate);
            endTime=sdf.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Query query = Query
                .timeRange(startTime, endTime)    // 设置查询时间条件
                .sub(SubQuery.metric("guoxi1").aggregator(Aggregator.AVG).tag("guoxi1", "guoxi1").build())    // 设置子查询
                .build();


        HiTSDBConfig config = HiTSDBConfig.address("192.168.155.163", 4242).config();
        HiTSDB tsdb = HiTSDBClientFactory.connect(config);

        List<QueryResult> result = tsdb.query(query);
        System.out.println("返回结果：" + result);

        return result;
    }
}
