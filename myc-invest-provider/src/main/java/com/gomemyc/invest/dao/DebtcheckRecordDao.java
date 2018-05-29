package com.gomemyc.invest.dao;

import com.gomemyc.invest.entity.DebtcheckRecord;

/**
 * 债转审批记录
 * @author Administrator
 *
 */
public interface DebtcheckRecordDao {

	/**
	 * 新建
	 * @param debtcheckRecord
	 * @return
	 */
	int add(DebtcheckRecord debtcheckRecord);
}
