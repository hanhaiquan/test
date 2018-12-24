package com.zjft.monitor.domain;

/**
 * Created by hqhan on 2018/12/12.
 */
public class CarParkInfo {

    private String plateNo;
    private String areaNo;
    private String status;
    private String lineNo;
    private String lineName;
    private String parkDate;
    private String parkTime;

    public CarParkInfo() {
    }

    public CarParkInfo(String plateNo, String areaNo, String parkDate, String parkTime) {
        this.plateNo = plateNo;
        this.areaNo = areaNo;
        this.parkDate = parkDate;
        this.parkTime = parkTime;
    }

    public CarParkInfo(String plateNo, String areaNo, String status, String lineNo, String lineName, String parkDate, String parkTime) {
        this.plateNo = plateNo;
        this.areaNo = areaNo;
        this.status = status;
        this.lineNo = lineNo;
        this.lineName = lineName;
        this.parkDate = parkDate;
        this.parkTime = parkTime;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    public String getAreaNo() {
        return areaNo;
    }

    public void setAreaNo(String areaNo) {
        this.areaNo = areaNo;
    }

    public String getParkDate() {
        return parkDate;
    }

    public void setParkDate(String parkDate) {
        this.parkDate = parkDate;
    }

    public String getParkTime() {
        return parkTime;
    }

    public void setParkTime(String parkTime) {
        this.parkTime = parkTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLineNo() {
        return lineNo;
    }

    public void setLineNo(String lineNo) {
        this.lineNo = lineNo;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }
}
