package com.gomemyc.gold.service;

import com.gomemyc.gold.dto.GoldExpireRepaymentRecordDTO;

/**
 * ClassName:GoldExpireRepaymentRecordService <br/>
 * Date:     2017年3月21日 上午11:26:09 <br/>
 * @author   liujunhan
 * @version
 * @since    JDK 1.8
 * @see
 * @description
 */
public interface GoldExpireRepaymentRecordService {

    /**
     * 接收ExpireRepaymentRecord对象并插入数据库
     * @return
     */
    int addExpireRepaymentRecord(GoldExpireRepaymentRecordDTO goldExpireRepaymentRecordDTO);
}
