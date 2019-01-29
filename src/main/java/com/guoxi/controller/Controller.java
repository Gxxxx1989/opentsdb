package com.guoxi.controller;

import com.guoxi.service.OpentsdbAsy;
import com.guoxi.service.OpentsdbSyc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    @Autowired
    OpentsdbAsy opentsdbAsy;//异步调用
    @Autowired
    OpentsdbSyc opentsdbSyc;//同步调用

    private long result;

    /**
     * 同步调用
     *
     * @param count
     * @param batchNum
     * @return 写数据所用时间
     */
    @RequestMapping(value = "/syc")
    public long putDataToOpentsdbSyc(int count, int batchNum) {
        result = opentsdbSyc.putData(count, batchNum);
        return result;
    }

    /**
     * 异步调用
     *
     * @return 写数据所用时间
     */
    @RequestMapping(value = "/asy")
    public long putDataToOpentsdbAsy(int count, int batchNum) {

        long result = opentsdbAsy.putDataAsy(count, batchNum);
        return result;
    }
}
