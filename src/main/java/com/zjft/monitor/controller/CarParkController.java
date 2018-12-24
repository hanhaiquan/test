package com.zjft.monitor.controller;

import com.zjft.monitor.domain.CarParkInfo;
import com.zjft.monitor.service.CarParkService;
import io.goeasy.GoEasy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by hqhan on 2018/12/12.
 */
@RestController
@RequestMapping("/carPark")
public class CarParkController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private CarParkService carParkService;

    @RequestMapping("/carParks")
    public ModelAndView qryCarParkList() {
        ModelAndView modelAndView = new ModelAndView("/display");
        modelAndView.addObject("retList", carParkService.getAllCarParkInfoList(""));
        GoEasy goEasy = new GoEasy("rest-hangzhou.goeasy.io", "BC-5e5483e493404122b1f182d6b89f1708");
        goEasy.publish("zjft", "哇哦");
        return modelAndView;
    }

    @DeleteMapping("delete/{plateNo}")
    public String deleteParkInfo(@PathVariable String plateNo) {
        carParkService.delete(plateNo);
        return "delete OK";
    }

    //指定车辆签到
    @GetMapping("/carPark/{plateNo}")
    public List<CarParkInfo> qryCarParkInfoByNo(@PathVariable String plateNo) {
        return carParkService.qryCarParkByNo(plateNo);
    }


}
