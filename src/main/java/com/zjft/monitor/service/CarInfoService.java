package com.zjft.monitor.service;

import com.zjft.monitor.domain.CarInfo;

import java.util.List;

/**
 * Created by hqhan on 2018/12/12.
 */
public interface CarInfoService {

    List<CarInfo> getCarInfoList(String status);

    void upateCarStatus(List<CarInfo> carInfoList, String status);

}
