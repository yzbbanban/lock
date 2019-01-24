package com.yzb.lock.dao;


import com.yzb.lock.vo.Area;

import java.util.List;

/**
 * Created by brander on 2018/3/1
 */
public interface AreaDao {

    /**
     * 列出区域列表
     *
     * @return
     */
    List<Area> queryArea();

    /**
     * 根据Id列出具体区域
     *
     * @param areaId
     * @return
     */
    Area queryAreaById(int areaId);

    /**
     * 插入区域信息
     *
     * @param area
     * @return
     */
    int insertArea(Area area);

    /**
     * 更新区域信息
     *
     * @param area
     * @return
     */
    int updateArea(Area area);

    /**
     * 删除区域信息
     *
     * @param areaId
     * @return
     */
    int deleteArea(int areaId);

    /**
     * @return
     */
    int getCount();

    /**
     * @param area
     * @return
     */
    int updateSelect(Area area);
}
