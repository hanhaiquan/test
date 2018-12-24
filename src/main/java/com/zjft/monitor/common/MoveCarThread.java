package com.zjft.monitor.common;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.rowset.CachedRowSet;
import java.net.URI;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hqhan on 2018/12/10.
 */
public class MoveCarThread implements Runnable {


    private Log log = LogFactory.getLog(MoveCarThread.class);

    private String plateNo;
    private Channel channel;
    private Connection conn;
    private int[] cameraNoArray = {1, 2, 3, 4, 5};

    public MoveCarThread(String plateNo, Channel channel, Connection conn) {
        this.plateNo = plateNo;
        this.channel = channel;
        this.conn = conn;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        try {
            for (int i = 0; i < cameraNoArray.length; i++) {
                DefaultFullHttpRequest request = buildRequestMsg(plateNo, cameraNoArray[i]);
                channel.write(request);
                channel.flush();
                log.info("摄像头[" + cameraNoArray[i] + "] 监测到车辆[" + plateNo + "] 进入，将车辆状态置为[" + getCarStatus(cameraNoArray[i]) + "] ");
                updateCarInfo(conn, plateNo, (cameraNoArray[i]+1)+"", getParkArea(cameraNoArray[i]));
                int ramdomNum = (int) (8 + Math.random() * (15 - 8 + 1));
                Thread.sleep(ramdomNum * 1000);//
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

    private DefaultFullHttpRequest buildRequestMsg(String plateNo, int cameraNo) throws Exception {
        String msg = "{\"plateNo\": \"" + plateNo + "\",\"cameraNo\":" + cameraNo + "}";
        log.debug("SendingMsg..." + msg);
        URI uri = new URI("/iTMS/pilotedParking/updateCarStatus");
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
                uri.toASCIIString(), Unpooled.wrappedBuffer(msg.getBytes("UTF-8")));

        request.headers().set(HttpHeaders.Names.HOST, "127.0.0.1");
        request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        request.headers().set(HttpHeaders.Names.CONTENT_LENGTH, request.content().readableBytes());

        return request;
    }

    private String getCarStatus(int cameraNo) {
        String status = "";
        switch (cameraNo) {
            case 1:
                status = "已签到";
                break;
            case 2:
                status = "已入场";
                break;
            case 3:
                status = "已待命";
                break;
            case 4:
                status = "已入泊";
                break;
            case 5:
                status = "已离场";
                break;
            default:
                status = "未签到";
                break;
        }
        return status;
    }

    private String getParkArea(int cameraNo) {
        String areaNo = "";
        switch (cameraNo) {
            case 1:
                areaNo = "05";//签到进入等待区
                break;
            case 2:
                areaNo = "06";//入场进入入场区
                break;
            case 3:
                areaNo = "07";//进入待命区
                break;
            case 4:
                areaNo = "04";//交接区
                break;
            case 5:
                areaNo = "";
                break;
            default:
                areaNo = "";
                break;
        }
        return areaNo;
    }


    private void updateCarInfo(Connection conn, String plateNo, String status, String areaNo) throws Exception {
        //更新车辆状态
        String update = "UPDATE LOGISTICS_CAR_INFO SET STATUS='" + status + "' WHERE PLATE_NO='" + plateNo + "'";
        DBUtil.executeCUID(update, conn, log);
        //记录车辆停泊信息
        String qry = "SELECT PLATE_NO,AREA_NO,PARK_DATE,PARK_TIME FROM AREA_PARKING_INFO WHERE PLATE_NO='" + plateNo + "'";
        CachedRowSet rowset = DBUtil.executeQuery(qry, conn, log);
        Date d = new Date();
        String date = DateFormat.getDateInstance(DateFormat.DEFAULT).format(d);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        String time = df.format(d);

        String updatePark = "";
        if (rowset.size() > 0) {
            if (areaNo.length() == 0) {//已离场删除记录
                updatePark = "DELETE FROM AREA_PARKING_INFO WHERE PLATE_NO='" + plateNo + "'";
            } else {
                updatePark = "UPDATE AREA_PARKING_INFO SET AREA_NO='" + areaNo + "',PARK_DATE='" + date + "', PARK_TIME='" + time + "' WHERE PLATE_NO='" + plateNo + "'";
            }
        } else {
            if (areaNo.length() > 0) {
                updatePark = "INSERT INTO AREA_PARKING_INFO(PLATE_NO,AREA_NO,PARK_DATE,PARK_TIME) VALUES('" + plateNo + "','" + areaNo + "','" + date + "','" + time + "')";
            }
        }

        if (updatePark.length() > 0) {
            DBUtil.executeCUID(updatePark, conn, log);
        }
    }

}
