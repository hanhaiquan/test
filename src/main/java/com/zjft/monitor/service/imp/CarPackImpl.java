package com.zjft.monitor.service.imp;

import com.zjft.monitor.domain.CarParkInfo;
import com.zjft.monitor.mapper.CarParkInfoMapper;
import com.zjft.monitor.service.CarParkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by hqhan on 2018/12/12.
 */
@Service
@Transactional
public class CarPackImpl implements CarParkService {

    @Autowired
    private CarParkInfoMapper carParkInfoMapper;

    @Override
    public List<CarParkInfo> getAllCarParkInfoList(String areaNo) {
        return carParkInfoMapper.getAllCarParkInfoList(areaNo);
    }

    @Override
    public void delete(String plateNo) {
        carParkInfoMapper.delete(plateNo);
    }

    @Override
    public void update(CarParkInfo carParkInfo) {
        carParkInfoMapper.update(carParkInfo);
    }

    @Override
    public List<CarParkInfo> qryCarParkByNo(String plateNo) {
        return carParkInfoMapper.qryCarParkByNo(plateNo);
    }

    @Override
    public void insert(CarParkInfo carParkInfo) {
        carParkInfoMapper.insert(carParkInfo);
    }
}
