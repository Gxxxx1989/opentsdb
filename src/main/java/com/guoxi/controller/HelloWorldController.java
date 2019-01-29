package com.guoxi.controller;

import com.guoxi.service.Opentsdb;
import com.guoxi.service.Opentsdb1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
    @Autowired
    Opentsdb opentsdb;
    @Autowired
    Opentsdb1 opentsdb1;

    private long result;

    @RequestMapping(value = "/")
    public long putDataToOpentsdb(int count,int batchNum) {
//        try {
//             result=opentsdb.putData(param);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        result= opentsdb1.putData(count, batchNum);
        return result;
    }
}
