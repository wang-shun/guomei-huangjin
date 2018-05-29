package com.gomemyc.gold.util;

import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.model.user.User;
import com.gomemyc.trade.enums.ExceptionCode;

/** 
 * ClassName:GoldProductRuleVerifyUtil
 * Date:     2017年3月29日 
 * @author   LiuQiangBin 
 * @since    JDK 1.8 
 * @description 黄金钱包产品投资金额规则校验工具类
 */
public class GoldProductRuleVerifyUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(GoldProductRuleVerifyUtil.class);

	/**
	 * 
	 * 产品投资金额规则校验
	 * 
	 * @param amount  投资金额
	 * @param minInvestAmount  产品最小投资金额
	 * @param maxInvestAmount  产品最大投资金额
	 * @param investTimes  用户投资次数
	 * @param MaxTimes  产品最大可投资次数
	 * @param balance  产品剩余可投资金额
	 * @param maxTotalAmount  产品可投资的总金额
	 * @param productSumAmount  产品已被购买的金额
	 * @param stepAmount  产品投资同步增长最小金额
	 * @param User 用户实体信息
	 * @param productUserId 产品发布者id
	 * @return  boolean (校验成功或失败)
	 * @since JDK 1.8
	 * @author LiuQiangBin 
	 * @date 2017年3月29日
	 */
	public static boolean goldProductRuleVerify(BigDecimal amount, 
											    BigDecimal minInvestAmount, 
											    BigDecimal maxInvestAmount,
											    Integer investTimes,
											    Integer MaxTimes,
											    BigDecimal balance,
											    BigDecimal maxTotalAmount,
											    BigDecimal productSumAmount,
											    Long stepAmount,
											    User user,
											    String productUserId)
	{
		logger.info("goldProductRuleVerify  in  GoldProductRuleVerifyUtil ,the param [amount={}],[minInvestAmount={}],[maxInvestAmount={}],[investTimes={}],[MaxTimes={}],[balance={}],[maxTotalAmount={}],[productSumAmount={}],[stepAmount={}]",amount,minInvestAmount,maxInvestAmount,investTimes,MaxTimes,balance,maxTotalAmount,productSumAmount,stepAmount);

        // 用户购买规则(状态为启用,不能为企业用户,不能为产品发布者)
        if (null == user || !user.isEnabled() || user.isEnterprise() || 
                user.getId().equals(productUserId)) {
		    logger.info("goldProductRuleVerify  in  GoldProductRuleVerifyUtil ,the param [userError={}]",user);
        	return false;
        }
		
		//如果用户投资金额小于最小投资金额,返回false
		if(amount.compareTo(minInvestAmount) < 0){
		    logger.info("goldProductRuleVerify  in  GoldProductRuleVerifyUtil ,the param [amount={}],[minInvestAmount={}]",amount,minInvestAmount);
			return false;
		}
		
		//如果用户投资金额大于最大投资金额,返回false
		if(amount.compareTo(maxInvestAmount) > 0){
		    logger.info("goldProductRuleVerify  in  GoldProductRuleVerifyUtil ,the param [amount={}],[maxInvestAmount={}]",amount,maxInvestAmount);
			return false;
		}
		
		//查看用户购买的金额是否符合标准
		BigDecimal[] result = amount.divideAndRemainder(new BigDecimal(stepAmount));
		logger.info("goldProductRuleVerify  in  GoldProductRuleVerifyUtil ,the result[0] is [{}]",result[0]);
		logger.info("goldProductRuleVerify  in  GoldProductRuleVerifyUtil ,the result[1] is [{}]",result[1]);
		
		//如果用户投资金额不满足最小增长数量,返回false
		if(result[1].compareTo(BigDecimal.ZERO) != 0){
		    logger.info("goldProductRuleVerify  in  GoldProductRuleVerifyUtil ,the param [result[1]={}]",result[1]);
			return false;
		}
		//如果用户投资次数大于等于产品可投资次数,返回false
		if(MaxTimes > 0 &&
		   investTimes.compareTo(MaxTimes) >= 0 ){
		    logger.info("goldProductRuleVerify  in  GoldProductRuleVerifyUtil ,the param [investTimes={}],[MaxTimes={}]",investTimes,MaxTimes);
			return false;
		}
		//如果用户投资金额大于剩余可投资金额,返回false
		if(amount.compareTo(balance) > 0){
		    logger.info("goldProductRuleVerify  in  GoldProductRuleVerifyUtil ,the param [amount={}],[balance={}]",amount,balance);
			return false;
		}
		//用户已购买金额加上用户要购买的金额 = 总金额
		if(productSumAmount == null)
			productSumAmount = amount;
		else
			productSumAmount = productSumAmount.add(amount);
		logger.info("goldProductRuleVerify  in  GoldProductRuleVerifyUtil ,the productSumAmount is [{}]",productSumAmount);
		//如果用户投资金额加上所有用户已购买金额大于产品最大总投资金额,返回false
		if(productSumAmount.compareTo(maxTotalAmount) > 0){
		    logger.info("goldProductRuleVerify  in  GoldProductRuleVerifyUtil ,the param [productSumAmount={}],[maxTotalAmount={}]",productSumAmount,maxTotalAmount);
			return false;
		}
		return true;
	}
}
