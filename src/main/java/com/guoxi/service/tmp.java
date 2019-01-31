package com.guoxi.service;

import com.aliyun.hitsdb.client.HiTSDB;
import com.aliyun.hitsdb.client.HiTSDBClientFactory;
import com.aliyun.hitsdb.client.HiTSDBConfig;
import com.aliyun.hitsdb.client.value.request.Point;
import com.aliyun.hitsdb.client.value.response.batch.DetailsResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class tmp {
    /**
     * 同步写入 opentsdb
     * @param count
     * @param batchNum
     * @return
     */
   public  long putData(int count,int batchNum){
       // 创建 HiTSDB 对象
       HiTSDBConfig config = HiTSDBConfig.address("192.168.155.163", 4242).config();
       HiTSDB tsdb = HiTSDBClientFactory.connect(config);

       List<Point> points = new ArrayList<>();

       //构建 Point
       for (int i = 0; i < count; i++) {
           long timestamp = System.currentTimeMillis();
           Point point = Point.metric("guoxi2")
                   .tag("guoxi2", "guoxi2")
                   .value(timestamp + i, Math.random() + i)
                   .build();

           // 手动打包数据
           points.add(point);
       }

       long startTime = System.currentTimeMillis();   //获取开始时间
       System.out.println(startTime);


       List<Point> pointList = new ArrayList<>();
       for (Point point:points) {
           pointList.add(point);
           //batchNum 这个数字很关键 同步写数据的时候会默认每500提交一次 batchNum*500就是每次提交的数量 不能太大 具体原因还没找到
           if (pointList.size() == batchNum){
               List<Point> pointListB = pointList;
               DetailsResult detailsResult = tsdb.putSync(pointListB, DetailsResult.class);
               System.out.println(detailsResult);
               pointList.clear();
           }
       }

       long endTime = System.currentTimeMillis(); //获取结束时间
       System.out.println(endTime);
       System.out.println("程序运行时间::" + (endTime - startTime) + "ms");

       return endTime - startTime;
   }
}
