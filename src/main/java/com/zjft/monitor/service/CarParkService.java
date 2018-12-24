package com.zjft.monitor.service;

import com.zjft.monitor.domain.CarParkInfo;

import java.util.List;

/**
 * Created by hqhan on 2018/12/12.
 */
public interface CarParkService {

    List<CarParkInfo> getAllCarParkInfoList(String areaNo);

    void delete(String plateNo);

    void update(CarParkInfo carParkInfo);

    List<CarParkInfo> qryCarParkByNo(String plateNo);

    void insert(CarParkInfo newCarParkInfo);
}
