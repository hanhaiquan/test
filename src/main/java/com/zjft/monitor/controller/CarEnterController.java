package com.zjft.monitor.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zjft.monitor.domain.CarInfo;
import com.zjft.monitor.domain.CarParkInfo;
import com.zjft.monitor.service.CarInfoService;
import com.zjft.monitor.service.CarParkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by hqhan on 2018/12/12.
 * 车辆入场
 */
@RestController
@Transactional
@RequestMapping(value = "/carEnter")
public class CarEnterController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CarInfoService carInfoService;

    @Autowired
    private CarParkService carParkService;

    @PostMapping("/update")
    public String doCarInfoUpdate(@RequestBody JSONObject jsonParam) {
        logger.info("Update car info...");
        //获取前端上送的carInfoList
        JSONArray carArray = (JSONArray) jsonParam.getJSONArray("carInfoList");
        List<CarInfo> carInfoList = JSONObject.parseArray(carArray.toJSONString(), CarInfo.class);
        //获取摄像头编号,并转换为对应的状态
        String cameraNo = jsonParam.get("cameraNo").toString();
        String status = getCarStatus(cameraNo);
        //更新对应车辆的状态信息
        carInfoService.upateCarStatus(carInfoList, status);
        //记录车辆停靠区域信息
        //-判断是否有停靠记录 有更新没有插入,离场则删除
        Date d = new Date();
        String date = DateFormat.getDateInstance(DateFormat.DEFAULT).format(d);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        String time = df.format(d);
        CarParkInfo carParkInfo = null;
        for (CarInfo carInfo : carInfoList) {
            List<CarParkInfo> carParkInfoList = carParkService.qryCarParkByNo(carInfo.getPlateNo());
            if (carParkInfoList.size() > 0) {
                carParkInfo = carParkInfoList.get(0);
            } else {
                carParkInfo = null;
            }
            if (carParkInfo != null) {
                if ("6".equals(status)) {//车辆是离场状态时，删除记录
                    carParkService.delete(carInfo.getPlateNo());
                } else {
                    carParkInfo.setAreaNo(getParkArea(cameraNo));
                    carParkInfo.setParkDate(date);
                    carParkInfo.setParkTime(time);
                    carParkService.update(carParkInfo);
                }
            } else {
                if (!"6".equals(status)) {
                    carParkInfo = new CarParkInfo();
                    carParkInfo.setPlateNo(carInfo.getPlateNo());
                    carParkInfo.setAreaNo(getParkArea(cameraNo));
                    carParkInfo.setParkDate(date);
                    carParkInfo.setParkTime(time);
                    carParkService.insert(carParkInfo);
                }
            }
            String areaDes = getParkArea(cameraNo).length() == 0 ? "离开金库" : "进入[" + getParkArea(cameraNo) + "]区域";
            logger.info("摄像头[" + cameraNo + "] 监测到车辆[" + carInfo.getPlateNo() + "] " + areaDes + "，将车辆状态置为[" + getCarStatus(cameraNo) + "] ");
        }

        logger.info("Update car info ok");
        return "update success";
    }

    private String getCarStatus(String cameraNo) {
        String status = "";
        switch (cameraNo) {
            case "1":
                status = "2";
                break;
            case "2":
                status = "3";
                break;
            case "3":
                status = "4";
                break;
            case "4":
                status = "5";
                break;
            case "5":
                status = "6";
                break;
            default:
                status = "1";
                break;
        }
        return status;
    }

    private String getCarStatusDesc(String cameraNo) {
        String status = "";
        switch (cameraNo) {
            case "1":
                status = "已签到";
                break;
            case "2":
                status = "已入场";
                break;
            case "3":
                status = "已待命";
                break;
            case "4":
                status = "已入泊";
                break;
            case "5":
                status = "已离场";
                break;
            default:
                status = "未签到";
                break;
        }
        return status;
    }

    private String getParkArea(String cameraNo) {
        String areaNo = "";
        switch (cameraNo) {
            case "1":
                areaNo = "05";//签到进入等待区
                break;
            case "2":
                areaNo = "06";//入场进入入场区
                break;
            case "3":
                areaNo = "07";//进入待命区
                break;
            case "4":
                areaNo = "04";//交接区
                break;
            case "5":
                areaNo = "";
                break;
            default:
                areaNo = "";
                break;
        }
        return areaNo;
    }

}
