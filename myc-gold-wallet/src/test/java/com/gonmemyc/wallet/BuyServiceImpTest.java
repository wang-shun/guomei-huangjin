package com.gonmemyc.wallet;

import java.math.BigDecimal;
import org.junit.Test;
import com.gomemyc.wallet.resp.BuyTimeGoldResultDto;
import com.gomemyc.wallet.resp.ConfirmBuyTimeGoldResultDto;
import com.gomemyc.wallet.walletinter.GoldBuyWallet;

/**
 * 
 * @ClassName BuyServiceImpTest
 * @author zhuyunpeng
 * @description: 
 * @date 2017年3月9日
 */
public class  BuyServiceImpTest{
	
	
	//2.4 购买定期金
	@Test
	public void testBuyTimeGold(){
		BuyTimeGoldResultDto dto=
				GoldBuyWallet.buyTimeGold("1.1.0","MC170220090909000001","2017-3-16 14:16:10",new BigDecimal("8000.00"), "111111", "1", "300703", "1.1.0","soq9zs2ztxqzwsdeeu4hgxwkg3w2gdfxyvm6wcqqksps156bvmmwi9ekuxb47g2r","http://182.92.10.201:8093");
		System.out.println(dto.toString());
	}
	
	//2.5购买定期金确认
	@Test
	public void testConfirmBuyTimeGold(){
		ConfirmBuyTimeGoldResultDto dto=
				GoldBuyWallet.confirmBuyTime("1.1.0","MC170220090909000001","2017-02-22 12:24:24",new BigDecimal("8000.00"), new BigDecimal("8000.00"), new BigDecimal("8000.00"),  "111", "1111111111", "soq9zs2ztxqzwsdeeu4hgxwkg3w2gdfxyvm6wcqqksps156bvmmwi9ekuxb47g2r","http://182.92.10.201:8093");
		System.out.println(dto.toString());
	}
	
}