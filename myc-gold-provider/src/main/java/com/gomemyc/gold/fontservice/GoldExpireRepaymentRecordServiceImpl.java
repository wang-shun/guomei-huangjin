package com.gomemyc.gold.fontservice;

import com.alibaba.dubbo.config.annotation.Service;
import com.gomemyc.gold.dao.GoldExpireRepaymentRecordDao;
import com.gomemyc.gold.dto.GoldExpireRepaymentRecordDTO;
import com.gomemyc.gold.entity.GoldExpireRepaymentRecord;
import com.gomemyc.gold.service.GoldExpireRepaymentRecordService;
import com.gomemyc.util.BeanMapper;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * ClassName:GoldExpireRepaymentRecordServiceImpl <br/>
 * Date:     2017年3月21日 上午11:26:09 <br/>
 * @author   liujunhan
 * @version
 * @since    JDK 1.8
 * @see
 * @description
 */
@Service(timeout=6000)
public class GoldExpireRepaymentRecordServiceImpl implements GoldExpireRepaymentRecordService {

    @Autowired
    private GoldExpireRepaymentRecordDao goldExpireRepaymentRecordDao;
    /**
     * 接收ExpireRepaymentRecord对象并插入数据库
     * @return GoldExpireRepaymentRecordDTO 对象
     * @return 返回插入数据库时影响的行数  1：成功
     */
    @Override
    public int addExpireRepaymentRecord(GoldExpireRepaymentRecordDTO goldExpireRepaymentRecordDTO) {
        return goldExpireRepaymentRecordDTO !=null ? 0 : goldExpireRepaymentRecordDao.insertExpireRepaymentRecord(BeanMapper.map(goldExpireRepaymentRecordDTO, GoldExpireRepaymentRecord.class));
    }
  
}
