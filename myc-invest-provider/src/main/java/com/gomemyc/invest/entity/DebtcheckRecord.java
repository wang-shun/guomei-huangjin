/** 
 * Project Name:国美金融
 * File Name:DebtCheck.java 
 * Package Name:com.creditcloud.debt.entites 
 * Date:2016年2月22日
 *  
 * 
*/

package com.gomemyc.invest.entity;

import java.util.Date;


import com.gomemyc.common.StringIdEntity;
import com.gomemyc.invest.enums.CheckState;
import com.gomemyc.invest.enums.CheckStep;
import com.gomemyc.invest.enums.CheckType;


/**
 * 债转审批记录
 * @author zhangWei
 *
 */

public class DebtcheckRecord extends StringIdEntity {
	
	/**
	 *  审核描述
	 */
	private String description;
	/**
	 *  操作人
	 */
	private String userId;
	/**
	 *  操作人信息
	 */
	private String userInfo;
	/**
	 *  操作时间
	 */
	private Date createTime;
	/**
	 * 来源类型
	 * 0:债转
	 */
	private int  sourceType;
	/**
	 * 来源ID
	 */
	private String sourceId;
	/**
	 *  审核类型
	 */
	private CheckType checkType;
	/**
	 *  审核步骤
	 */
	private CheckStep checkStep;
	/**
	 *  审核状态
	 */
	private CheckState checkState;
	/**
	 *  债转产品id
	 */
	private String debtAssignProductId;


	public CheckType getCheckType() {
		return checkType;
	}

	public void setCheckType(CheckType checkType) {
		this.checkType = checkType;
	}

	public CheckStep getCheckStep() {
		return checkStep;
	}

	public void setCheckStep(CheckStep checkStep) {
		this.checkStep = checkStep;
	}

	public CheckState getCheckState() {
		return checkState;
	}

	public void setCheckState(CheckState checkState) {
		this.checkState = checkState;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	

	public int getSourceType() {
		return sourceType;
	}

	public void setSourceType(int sourceType) {
		this.sourceType = sourceType;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getDebtAssignProductId() {
		return debtAssignProductId;
	}

	public void setDebtAssignProductId(String debtAssignProductId) {
		this.debtAssignProductId = debtAssignProductId;
	}


}
