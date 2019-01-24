package com.yzb.lock.vo;

import java.util.Date;

/**
 * Created by brander on 2019/1/24
 */
public class Sign {

    /**
     * 设备 id
     */
    private String deivceid;

    /**
     * 时间
     */
    private Date time;

    /**
     * 设备电量
     */
    private String battery;

    /**
     * 设备状态
     */
    private String lockstatus;

    public String getDeivceid() {
        return deivceid;
    }

    public void setDeivceid(String deivceid) {
        this.deivceid = deivceid;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public String getLockstatus() {
        return lockstatus;
    }

    public void setLockstatus(String lockstatus) {
        this.lockstatus = lockstatus;
    }

    @Override
    public String toString() {
        return "Sign{" +
                "deivceid='" + deivceid + '\'' +
                ", time=" + time +
                ", battery='" + battery + '\'' +
                ", lockstatus='" + lockstatus + '\'' +
                '}';
    }
}
