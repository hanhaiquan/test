package com.zjft.monitor.service.imp;

import com.zjft.monitor.domain.CarInfo;
import com.zjft.monitor.mapper.CarInfoMapper;
import com.zjft.monitor.service.CarInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by hqhan on 2018/12/12.
 */
@Service
@Transactional
public class CarInfoImpl implements CarInfoService {

    @Autowired
    private CarInfoMapper carInfoMapper;

    @Override
    public List<CarInfo> getCarInfoList(String status) {
        return carInfoMapper.getCarInfoList(status);
    }

    @Override
    public void upateCarStatus(List<CarInfo> carInfoList,String status) {
        carInfoMapper.upateCarStatus(carInfoList,status);
    }

}
