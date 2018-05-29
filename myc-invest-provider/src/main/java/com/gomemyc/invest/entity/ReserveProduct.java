package com.gomemyc.invest.entity;

import com.gomemyc.common.StringIdEntity;

/**
 * 可预约产品类型。
 * 
 * @author 何健
 *
 */
public class ReserveProduct extends StringIdEntity{

    /** 序列化id */
	private static final long serialVersionUID = -8906369980319411080L;

	/**
     * 预约产品中文名称
     */
    private String productName;

    /**
     * 是否启用该预约产品， 0:不启用 1:启用
     */
    private boolean enable;

    /**
     * 是否删除该预约产品， 0:不删除 1:删除
     */
    private boolean deleted;
    
    /**
     * 是否显示推荐， 0:不突显 1:突显
     */
    private boolean recommend;
    
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isRecommend() {
		return recommend;
	}

	public void setRecommend(boolean recommend) {
		this.recommend = recommend;
	}
}
