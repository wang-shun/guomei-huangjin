/** 
 * Project Name:myc-gold-provider 
 * File Name:GoldCommonServiceImpl.java 
 * Package Name:com.gomemyc.gold.fontservice 
 * Date:2017年3月12日上午11:37:49 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  
  
package com.gomemyc.gold.fontservice;

import org.springframework.beans.factory.annotation.Autowired;

//import com.gomemyc.gold.dto.GoldCommonDTO;
//import com.gomemyc.gold.service.GoldCommonService;

import com.alibaba.dubbo.config.annotation.Service;
import com.gomemyc.gold.constant.GoldWalletConstant;
import com.gomemyc.gold.dto.GoldCommonDTO;
import com.gomemyc.gold.service.GoldCommonService;
import com.gomemyc.util.BeanMapper;
import com.gomemyc.wallet.resp.QueryPriceResultDto;
import com.gomemyc.wallet.walletinter.GoldQueryWallet;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * ClassName:GoldCommonServiceImpl <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Reason:   TODO ADD REASON. <br/> 
 * Date:     2017年3月12日 上午11:37:49 <br/> 
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description
 */
@Service(timeout=6000)
public class GoldCommonServiceImpl  implements GoldCommonService {


    @Autowired
    private GoldWalletConstant goldWalletConstant;
	
	 

	/**
	 *
	 * 轮询查询黄金金价放入redis
	 *
	 * @author TianBin
	 * @date 2017年3月12日
	 * @return
	 * @since JDK 1.8
	 */
	public GoldCommonDTO selectGoldRealPrice(){
		GoldCommonDTO goldCommonDTO =new GoldCommonDTO();
		QueryPriceResultDto queryPriceResultDto=GoldQueryWallet.queryPrice(goldWalletConstant.getVersion(), goldWalletConstant.getMerchantCode(), goldWalletConstant.getIp());
		if(queryPriceResultDto!=null &&  queryPriceResultDto.getData()!=null){
			BeanMapper.copy(queryPriceResultDto.getData(), goldCommonDTO);
		}

		goldCommonDTO.setRealPrice(goldCommonDTO.getRealPrice().divide(new BigDecimal(100.00), 2, RoundingMode.CEILING));
		return goldCommonDTO;
	}
}
  