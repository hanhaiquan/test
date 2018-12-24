package com.zjft.monitor.mapper;

import com.zjft.monitor.domain.CarInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by hqhan on 2018/12/12.
 */
@Repository
public interface CarInfoMapper {

    List<CarInfo> getCarInfoList(@Param("status") String status);

    void upateCarStatus(@Param("carInfoList") List<CarInfo> carList, @Param("status") String status);

}
