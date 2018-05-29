package com.gomemyc.wallet.resp;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @ClassName QueryTimeProductListRDErrorListDto
 * @author zhuyunpeng
 * @description 定期金产品查询响应data中Errorlist参数实体
 * @date 2017年3月8日
 */
public class QueryTimeProductListRDErrorListDto implements Serializable {

	
	private static final long serialVersionUID = -5352639935254384708L;

	//错误码
	private String errCode;

	//错误原因
	private String errMsg;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public QueryTimeProductListRDErrorListDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

}
