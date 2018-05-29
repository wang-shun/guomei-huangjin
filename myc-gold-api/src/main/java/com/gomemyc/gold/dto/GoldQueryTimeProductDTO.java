package com.gomemyc.gold.dto;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @ClassName GoldQueryTimeProductDTO
 * @author LiuQiangBin
 * @description 定期金产品查询响应data中list参数实体
 * @date 2017年3月26日
 */
public class GoldQueryTimeProductDTO implements Serializable {

	private static final long serialVersionUID = 6326728173935245432L;

	//产品代码
	private String procductCode;

	//产品名称
	private String procductName;

	//产品描述
	private String desc;

	//产品描述(年化万值，如660表示年化6.6%)
	private String rate;

	//状态(1-正常,2-已下线)
	private int status;
	
	public GoldQueryTimeProductDTO() {
		super();
	}

	public String getProcductCode() {
		return procductCode;
	}

	public void setProcductCode(String procductCode) {
		this.procductCode = procductCode;
	}

	public String getProcductName() {
		return procductName;
	}

	public void setProcductName(String procductName) {
		this.procductName = procductName;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
}
