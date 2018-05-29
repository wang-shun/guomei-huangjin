package com.gomemyc.gold.dao;


import com.gomemyc.gold.entity.GoldExpireRepaymentRecord;

public interface GoldExpireRepaymentRecordDao {
    int deleteByPrimaryKey(String id);

    int insert(GoldExpireRepaymentRecord record);

    GoldExpireRepaymentRecord selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(GoldExpireRepaymentRecord record);

    int updateByPrimaryKey(GoldExpireRepaymentRecord record);

    /**
     * 将传入的对象插入数据库
     * @param record
     * @return 影响的行数
     * @author liujunhan
     * @serialData  2017-03-21
     */
    int insertExpireRepaymentRecord(GoldExpireRepaymentRecord record);
}