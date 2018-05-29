package com.gomemyc.wallet.resp;

import java.io.Serializable;
import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @ClassName QueryTimeProductListRDataModel
 * @author zhuyunpeng
 * @description 定期金产品查询响应data参数实体
 * @date 2017年3月8日
 */
public class QueryTimeProductListRDataDto implements Serializable {

	 
	private static final long serialVersionUID = -6228330546358210372L;

	//响应产品信息
	private List<QueryTimeProductListRDListDto> list;

	//响应错误信息
	private List<QueryTimeProductListRDErrorListDto> errorList;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public QueryTimeProductListRDataDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public List<QueryTimeProductListRDListDto> getList() {
		return list;
	}

	public void setList(List<QueryTimeProductListRDListDto> list) {
		this.list = list;
	}

	public List<QueryTimeProductListRDErrorListDto> getErrorList() {
		return errorList;
	}

	public void setErrorList(List<QueryTimeProductListRDErrorListDto> errorList) {
		this.errorList = errorList;
	}
	
}
