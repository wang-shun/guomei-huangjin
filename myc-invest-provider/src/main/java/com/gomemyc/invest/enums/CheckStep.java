package com.gomemyc.invest.enums;

import com.gomemyc.common.constant.BaseEnum;

/** 
 * 审核步骤
 * @author zhangWei 
 */
public enum CheckStep  implements BaseEnum{  
	
	FIRST("初审",0),
	SECOND("终审",1),
	APPLY("申请转让",2),
	CANCLE("取消转让",3);
	 
    private  String key;
    private  Integer index;
    private CheckStep(String key,Integer index) {
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
  