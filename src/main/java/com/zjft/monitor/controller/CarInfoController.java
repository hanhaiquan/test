package com.zjft.monitor.controller;

import com.zjft.monitor.service.CarInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by hqhan on 2018/12/12.
 */
@RestController
@RequestMapping("/carInfo")
public class CarInfoController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CarInfoService carInfoService;

    @GetMapping("/carInfos")
    public ModelAndView qryCarParkList() {
        ModelAndView modelAndView = new ModelAndView("/displayCarInfos");
        modelAndView.addObject("retList", carInfoService.getCarInfoList(""));
        return modelAndView;
    }





}
