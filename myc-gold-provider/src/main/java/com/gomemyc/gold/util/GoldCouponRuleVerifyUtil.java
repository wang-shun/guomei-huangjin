package com.gomemyc.gold.util;

import java.math.BigDecimal;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gomemyc.coupon.model.CouponPackage;
import com.gomemyc.coupon.model.CouponPlacement;
import com.gomemyc.coupon.model.enums.CouponStatus;
import com.gomemyc.coupon.model.enums.CouponType;
import com.gomemyc.coupon.model.enums.InvestTimeUnit;
import com.gomemyc.gold.enums.GoldCouponStatusType;

/** 
 * ClassName:GoldCouponRuleVerifyUtil
 * Date:     2017年3月29日 
 * @author   LiuQiangBin 
 * @since    JDK 1.8 
 * @description 黄金钱包红包规则校验工具类
 */
public class GoldCouponRuleVerifyUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(GoldCouponRuleVerifyUtil.class);

	/**
	 * 
	 * 红包规则校验
	 * 
	 * @param couponId  红包id
	 * @param useCoupon  该产品是否可用红包
	 * @param couponPlacement  红包
	 * @param amount  用户投资金额
	 * @param loanId  产品类型id
	 * @param productId  产品id
	 * @param days 产品投资天数
	 * @return  boolean (校验成功或失败)
	 * @since JDK 1.8
	 * @author LiuQiangBin 
	 * @date 2017年3月29日
	 */
	public static boolean goldCouponRuleVerify(String couponId,
											   Integer useCoupon,
											   CouponPlacement couponPlacement,
											   BigDecimal amount,
											   String loanId,
											   String productId,
											   Integer days)
	{
		logger.info("goldCouponRuleVerify  in  GoldCouponRuleVerifyUtil ,the param [couponId={}],[useCoupon={}],[couponPlacement={}],[amount={}],[loanId={}],[productId={}],[days={}]",couponId,useCoupon,couponPlacement,amount,loanId,productId,days);

		//判断红包id是否为空,如果为空，即用户没有输入红包，直接放行,返回true
		if(StringUtils.isBlank(couponId)){
		    logger.info("goldCouponRuleVerify  in  GoldCouponRuleVerifyUtil ,the param [couponId={}]",couponId);
			return true;
		}
		
		//判断产品是否可用红包,返回false
		if(useCoupon == GoldCouponStatusType.DISABLED.getStatus()){
		    logger.info("goldCouponRuleVerify  in  GoldCouponRuleVerifyUtil ,the param [useCoupon={}]",useCoupon);
			return false;
		}
		
		//判断是否有这个红包,返回false
		if(couponPlacement == null){
		    logger.info("goldCouponRuleVerify  in  GoldCouponRuleVerifyUtil ,the param [couponPlacement={}]",couponPlacement);
			return false;
		}
	
		//判断红包是否可用,返回false
		if(!CouponStatus.PLACED.equals(couponPlacement.getStatus())){
		    logger.info("goldCouponRuleVerify  in  GoldCouponRuleVerifyUtil ,the param [CouponStatus={}]",couponPlacement.getStatus());
			return false;
		}
		
		//取出红包所属的包
		CouponPackage couponPackage = couponPlacement.getCouponPackage();
		
		//判断红包所属的包是否为空,返回false
		if(couponPackage == null){
		    logger.info("goldCouponRuleVerify  in  GoldCouponRuleVerifyUtil ,the param [couponPackage={}]",couponPackage);
			return false;
		}
		
		//判断红包是否是抵现券,返回false
		if(!CouponType.CASH.equals(couponPackage.getType())){
		    logger.info("goldCouponRuleVerify  in  GoldCouponRuleVerifyUtil ,the param [CouponType={}]",couponPackage.getType());
			return false;
		}
	
		//判断红包是否过期,返回false (TimeExpire=null为永不过期)
		if(couponPackage.getTimeExpire() != null && DateUtil.strToDateLong(DateUtil.getDateTime()).after(couponPackage.getTimeExpire())){
		    logger.info("goldCouponRuleVerify  in  GoldCouponRuleVerifyUtil ,the param [currentTime={}],[timeExpire={}]",DateUtil.strToDateLong(DateUtil.getDateTime()),couponPackage.getTimeExpire());
			return false;
		}
		
		//判断用户金额是否大于红包使用的最小投资金额,返回false
		if(amount.compareTo(new BigDecimal(couponPackage.getMinimumInvest())) < 0 ){
		    logger.info("goldCouponRuleVerify  in  GoldCouponRuleVerifyUtil ,the param [amount={}],[minimumInvest={}]",amount,couponPackage.getMinimumInvest());
			return false;
		}
		
		//判断此产品类型是否可用这个红包,返回false
		if(StringUtils.isNotBlank(couponPackage.getProductId())){
			if(!couponPackage.getProductId().equals(productId)){
				logger.info("goldCouponRuleVerify  in  GoldCouponRuleVerifyUtil ,the param [productId={}],[couponPackage_productId={}]",productId,couponPackage.getProductId());
				return false;
			}
		}
		
		//判断此产品是否可用这个红包,返回false
		if(StringUtils.isNotBlank(couponPackage.getLoanId())){
			if(!couponPackage.getLoanId().equals(loanId)){
				logger.info("goldCouponRuleVerify  in  GoldCouponRuleVerifyUtil ,the param [loanId={}],[couponPackage_LoanId={}]",loanId,couponPackage.getLoanId());
				return false;
			}
		}
		
		//最短投资时间
        int minimumDuration=InvestTimeUnit.MONTH.equals(couponPackage.getInvestTimeUnit())?couponPackage.getMinimumDuration() * 30:couponPackage.getMinimumDuration();
		//判断此产品的投资天数是否可用该红包,返回false (minimumDuration=0 没有限制)
		if(minimumDuration != 0 && days.compareTo(minimumDuration) < 0){
		    logger.info("goldCouponRuleVerify  in  GoldCouponRuleVerifyUtil ,the param [minimumDuration={}],[days={}]",minimumDuration,days);
			return false;
		}
        
        //最长投资时间
        int maximumDuration=InvestTimeUnit.MONTH.equals(couponPackage.getInvestTimeUnit())?couponPackage.getMaximumDuration() * 30:couponPackage.getMaximumDuration();
		//判断此产品的投资天数是否可用该红包,返回false (maximumDuration=0 没有限制)
		if(maximumDuration != 0 && days.compareTo(maximumDuration) > 0){
		    logger.info("goldCouponRuleVerify  in  GoldCouponRuleVerifyUtil ,the param [maximumDuration={}],[days={}]",maximumDuration,days);
			return false;
		}
        
		return true;
	}
}
