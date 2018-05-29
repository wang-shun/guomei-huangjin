package com.gomemyc.gold.entity.extend;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @ClassName:Contract
 * @Description:黄金产品中有关国美黄金消费综合服务协议实体
 * @author zhuyunpeng
 * @date 2017年4月10日
 */
public class Contract implements Serializable {

	
	private static final long serialVersionUID = -205396369153232337L;
	
	// 黄金品种
	private String goldName;

	// 年化存管收益率
	private Integer rate;

	// 存管期间起始时间
	private Date startTime;

	// 存管期间终止时间
	private Date finishTime;
	
	//标的Id
	private String loanId;

	public String getGoldName() {
		return goldName;
	}

	public void setGoldName(String goldName) {
		this.goldName = goldName == null ? null : goldName.trim();
	}

	public Integer getRate() {
		return rate;
	}

	public void setRate(Integer rate) {
		this.rate = rate;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}

	public String getLoanId() {
		return loanId;
	}

	public void setLoanId(String loanId) {
		this.loanId = loanId == null ? null : loanId.trim();
	}

}
