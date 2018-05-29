package com.gonmemyc.wallet;

import org.junit.Test;

import com.gomemyc.wallet.resp.QueryBuyTimeOrderResultDto;
import com.gomemyc.wallet.resp.QueryMyAccountResultDto;
import com.gomemyc.wallet.resp.QueryPriceResultDto;
import com.gomemyc.wallet.resp.QueryTimeProductListResultDto;
import com.gomemyc.wallet.walletinter.GoldQueryWallet;


public class  QueryServiceImpTest{
	//2.1 查询实时金价
	@Test
	public void testQueryPrice(){
		QueryPriceResultDto dto=
				GoldQueryWallet.queryPrice("1.1.0","MC170220090909000001","http://182.92.10.201:8093");
		System.out.println(dto.toString());
	}
	//2.2 查询实时金价
	@Test
	public void testQueryMyAccount(){

		QueryMyAccountResultDto dto=
				GoldQueryWallet.queryMyAccount("1.1.0","MC170220090909000001","soq9zs2ztxqzwsdeeu4hgxwkg3w2gdfxyvm6wcqqksps156bvmmwi9ekuxb47g2r","http://182.92.10.201:8093");

		System.out.println(dto.toString());
	}
	//2.3 定期金产品查询
	@Test
	public void testQueryTimeProductList(){

		QueryTimeProductListResultDto dto=
				GoldQueryWallet.queryTimeProduct("1.1.0","MC170220090909000001","soq9zs2ztxqzwsdeeu4hgxwkg3w2gdfxyvm6wcqqksps156bvmmwi9ekuxb47g2r","http://182.92.10.201:8093");

		System.out.println(dto.toString());
	}

	
	//2.6购买定期金交易结果查询
	@Test
	public void testQueryBuyTimeOrder(){

		QueryBuyTimeOrderResultDto dto=
				GoldQueryWallet.queryBuyTimeOrder("1.1.0","MC170220090909000001","1111111111","soq9zs2ztxqzwsdeeu4hgxwkg3w2gdfxyvm6wcqqksps156bvmmwi9ekuxb47g2r","http://182.92.10.201:8093");

		System.out.println(dto.toString());
	}
}