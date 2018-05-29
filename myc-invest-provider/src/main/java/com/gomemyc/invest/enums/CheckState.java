package com.gomemyc.invest.enums;

import com.gomemyc.common.constant.BaseEnum;

/**
 * 审核状态
 * @author zhangWei
 *
 */
public enum CheckState implements BaseEnum {
	
	
	PASS("通过",0),
	REFUSE("拒绝",1),
	SUCCESS("成功",2),
	FAIL("失败",3);
	
	 
    private  String key;
    private  Integer index;
    private CheckState(String key,Integer index) {
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
  