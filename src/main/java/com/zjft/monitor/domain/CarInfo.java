package com.zjft.monitor.domain;

/**
 * Created by hqhan on 2018/12/12.
 */
public class CarInfo {

    private String plateNo;
    private String lineNo;
    private String status;

    public CarInfo(String plateNo, String lineNo, String status) {
        this.plateNo = plateNo;
        this.lineNo = lineNo;
        this.status = status;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    public String getLineNo() {
        return lineNo;
    }

    public void setLineNo(String lineNo) {
        this.lineNo = lineNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
