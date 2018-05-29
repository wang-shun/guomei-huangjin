package com.gomemyc.wallet.enums;

/**
  * @ClassName: DictionaryEnum
  * @Description: 黄金钱包接口数据字典枚举
  * @author zhuyunpeng
  * @date 2017年3月8日
  *
 */
public enum DictionaryEnum {
	
	/**
	 * 购买类型(按钱买)
	 */
	BUY_MONEY("1","按钱买"),
	
	/**
	 * 购买类型(按金重买)
	 */
	BUY_GOLD_WEIGHT("2","按金重买");
	
	private String key;
	
    private String keyDesc;
    
    DictionaryEnum(String key,String keyDesc){
    	 this.key=key;
         this.keyDesc=keyDesc;
    }

	public String getKey() {
		 return key;
	}
	
	public String getValue() {
		return keyDesc;
	}
	
	public final static String getKeyByName(String name){
        try {
           return DictionaryEnum.valueOf(name).getKey();
        }catch (Exception e){
           return null;
        }
    }
	 
}
