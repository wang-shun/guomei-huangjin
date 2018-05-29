package com.gonmemyc.wallet;

import org.junit.Test;
import com.gomemyc.wallet.resp.CheckDailyInterestResultDto;
import com.gomemyc.wallet.resp.CheckExpireOrderResultDto;
import com.gomemyc.wallet.resp.CheckSumInterestResultDto;
import com.gomemyc.wallet.resp.CheckTimeOrderResultDto;
import com.gomemyc.wallet.walletinter.GoldCheckAccountWallet;

/**
 * 
 * @ClassName CheckAccountServiceImpTest
 * @author zhuyunpeng
 * @description: 
 * @date 2017年3月9日
 */
public class  CheckAccountServiceImpTest{
	
	
	//2.7.1  买定期金对账文件
	@Test
	public void checkAccountTimeGold(){
		CheckTimeOrderResultDto dto=
				GoldCheckAccountWallet.checkTimeOrder("1.1.0","MC170220090909000001","300101", "2017-03-03","soq9zs2ztxqzwsdeeu4hgxwkg3w2gdfxyvm6wcqqksps156bvmmwi9ekuxb47g2r","http://182.92.10.201:8093");
		System.out.println(dto.toString());
	}
	
	//2.7.2定期金到期对账文件
	@Test
	public void checkExpireOrderGold(){
		CheckExpireOrderResultDto dto=
				GoldCheckAccountWallet.checkExpireOrder("1.1.0","MC170220090909000001","300101", "2017-03-03","soq9zs2ztxqzwsdeeu4hgxwkg3w2gdfxyvm6wcqqksps156bvmmwi9ekuxb47g2r","http://182.92.10.201:8093");
		System.out.println(dto.toString());
	}
	
	//2.7.3每天利息对账文件
	@Test
	public void checkDailyInterest(){
		CheckDailyInterestResultDto dto=
				GoldCheckAccountWallet.checkDailyInterest("1.1.0","MC170220090909000001","300101", "2017-03-02","soq9zs2ztxqzwsdeeu4hgxwkg3w2gdfxyvm6wcqqksps156bvmmwi9ekuxb47g2r","http://182.92.10.201:8093");
		System.out.println(dto.toString());
	}
	
	//2.7.4定期到期利息汇总对账文件
	@Test
	public void checkSumInterest(){
		CheckSumInterestResultDto dto=
				GoldCheckAccountWallet.checkSumInterest("1.1.0","MC170220090909000001","300101","soq9zs2ztxqzwsdeeu4hgxwkg3w2gdfxyvm6wcqqksps156bvmmwi9ekuxb47g2r","http://182.92.10.201:8093");
		System.out.println(dto.toString());
	}
	
}