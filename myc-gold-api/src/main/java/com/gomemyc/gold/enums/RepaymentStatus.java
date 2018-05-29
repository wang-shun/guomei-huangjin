package com.gomemyc.gold.enums;

import com.gomemyc.common.constant.BaseEnum;

/**
 *
 * @author rooseek
 */
public enum RepaymentStatus implements BaseEnum {

    //每期还款的状态
	WAITBANK(-3,"待北京银行成标"),
	WAITACCOUNT(-2,"待本地账户成标"),
	WAITRECHARGE(-1,"待生成融资方充值订单"),
    UNDUE(0,"未到期"),
    OVERDUE(1,"逾期"),
    BREACH(2,"违约"),
    REPAYED(3,"已还清"),
    /**
     * TODO 暂时未用上,一律用REPAYED表示已还状态</p>
     * 出现逾期或违约后，回收或垫付的状态
     */
    CANCEL(4,"已取消"),
    COLLECTED(5,"已回收");

    private  String key;
    private  Integer index;
    private RepaymentStatus(Integer index, String key) {
        this.key = key;
        this.index=index;
    }

    public String getKey() {
        return key;
    }

	public Integer getIndex() {
		return index;
	}
}
