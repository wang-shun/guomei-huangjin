package com.gomemyc.gold.fontservice;

import com.gomemyc.gold.dao.GoldAccountLogDao;
import com.gomemyc.gold.dto.GoldAccountLogDTO;
import com.gomemyc.gold.entity.GoldAccountLog;
import com.gomemyc.util.BeanMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.gomemyc.gold.service.GoldAccountLogService;

@Service(timeout=6000)
public class GoldAccountLogServiceImpl implements GoldAccountLogService {

	
	private static final Logger logger = LoggerFactory.getLogger(GoldAccountLogServiceImpl.class);
	
	@Autowired
	private GoldAccountLogDao goldAccountLogDao;

	/**
	 *查询指定用户的对账记录数
	 * @param record
	 * @return 记录数
	 * @author zhuyunpeng
	 * @serialData  2017-03-22
	 */
	@Override
	public int countLogRecord(String userId) {
		logger.info("countLogRecord  in  GoldAccountLogServiceImpl ,the param [userId={}]",userId);
		return userId == null ? 0 : goldAccountLogDao.countRecord(userId);
	}

	/**
	 * 将传入的对象插入数据库
	 * @param record
	 * @return 影响的行数
	 * @author zhuyunpeng
	 * @serialData  2017-03-22
	 */
	@Override
	public int addGoldAccountLog(GoldAccountLogDTO goldAccountLogDTO) {
		logger.info("addGoldAccountLog  in  GoldAccountLogServiceImpl ,the param [goldAccountLogDTO={}]",goldAccountLogDTO.toString());
		GoldAccountLog goldAccountLog = new GoldAccountLog();
		BeanMapper.copy(goldAccountLogDTO,goldAccountLog);
		return goldAccountLogDTO == null ? 0 : goldAccountLogDao.insert(goldAccountLog);
	}

	/**
	 * 查询所有日志记录
	 * @param record
	 * @return 影响的行数
	 * @author zhuyunpeng
	 * @serialData  2017-03-22
	 */
	@Override
	public List<GoldAccountLogDTO> selectAllRecord() {
		List<GoldAccountLog> goldAccountLogs = goldAccountLogDao.selectAllRecord();
		logger.info("selectAllRecord  in  GoldAccountLogServiceImpl ,the param [goldAccountLogDTO={}]",goldAccountLogs.toString());
		return BeanMapper.mapList(goldAccountLogs, GoldAccountLogDTO.class);
	}

}
