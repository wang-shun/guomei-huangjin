package com.gomemyc.invest.enums;

import com.gomemyc.common.constant.BaseEnum;

/**
 * 审核类型
 * @author zhangWei
 *
 */
public enum CheckType  implements BaseEnum{
	 
	
	PERSON("人工审核",0),
	SYSTEM("系统审核",1),

	//=========撤销操作源类型============
	MANAGER_CANCEL("后台取消",2),
	SELLER_CANCLE("出让人取消",3),
	BUYER_CANCLE("购买者取消",4);
	//=========撤销操作源类型============
	
	 
    private  String key;
    private  Integer index;
    private CheckType(String key,Integer index) {
        this.key = key;
        this.index=index;
    }

    public String getKey() {
        return key;
    }

	@Override
	public Integer getIndex() {
		return index;
	}

	
	
	
	

}
  