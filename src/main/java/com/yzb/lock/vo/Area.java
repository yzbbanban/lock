package com.yzb.lock.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by brander on 2018/2/28
 */
public class Area implements Serializable {
    /**
     * 主键 ID
     */
    private Integer areaId;
    /**
     * 名称
     */
    private String areaName;
    /**
     * 权重，越大越在前显示
     */
    private Integer priority;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date lastEditTime;
    /**
     * 版本
     */
    private Long version;

    /**
     * 并发测试select
     */
    private Long testSelect;

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastEditTime() {
        return lastEditTime;
    }

    public void setLastEditTime(Date lastEditTime) {
        this.lastEditTime = lastEditTime;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getTestSelect() {
        return testSelect;
    }

    public void setTestSelect(Long testSelect) {
        this.testSelect = testSelect;
    }

    @Override
    public String toString() {
        return "Area{" +
                "areaId=" + areaId +
                ", areaName='" + areaName + '\'' +
                ", priority=" + priority +
                ", createTime=" + createTime +
                ", lastEditTime=" + lastEditTime +
                ", version=" + version +
                ", testSelect=" + testSelect +
                '}';
    }
}
