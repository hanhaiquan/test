package com.zjft.monitor.mapper;

import com.zjft.monitor.domain.CarParkInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by hqhan on 2018/12/12.
 */
@Repository
public interface CarParkInfoMapper {

    List<CarParkInfo> getAllCarParkInfoList(@Param("areaNo") String areaNo);

    void delete(@Param("plateNo") String plateNo);

    void update(CarParkInfo carParkInfo);

    List<CarParkInfo> qryCarParkByNo(@Param("plateNo") String plateNo);

    void insert(CarParkInfo carParkInfo);
}
