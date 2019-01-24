package com.yzb.lock.dao;


import com.yzb.lock.vo.Sign;

/**
 * Created by brander on 2018/3/1
 */
public interface SignDao {


    /**
     * 更新区域信息
     *
     * @param sign
     * @return
     */
    int updateSign(Sign sign);


    int insertSign(Sign sign);

}
