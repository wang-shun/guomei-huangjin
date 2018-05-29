/** 
 * Project Name:myc-rest 
 * File Name:GoldProductController.java 
 * Package Name:com.gomemyc.controller 
 * Date:2017年3月5日下午4:48:58 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  
  
package com.gomemyc.controller;


import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.alibaba.dubbo.config.annotation.Reference;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.common.page.Page;
import com.gomemyc.exception.AppRuntimeException;
import com.gomemyc.gold.dto.GoldProductDetailsDTO;
import com.gomemyc.gold.dto.GoldProductDTO;
import com.gomemyc.gold.service.GoldProductService;
import com.gomemyc.http.MediaTypes;
import com.gomemyc.util.InfoCode;
import com.gomemyc.util.RestResp;

/** 
 * ClassName:GoldProductController
 * Function: TODO ADD FUNCTION
 * Reason:   TODO ADD REASON
 * Date:     2017年3月5日 下午4:48:58
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description
 */
@Component
@Path("/api/v1/gold/product")
@Produces(MediaTypes.JSON_UTF_8)
public class GoldProductController {
	
	@Reference
	private GoldProductService goldProductService;

	private static final Logger logger = LoggerFactory.getLogger(GoldProductController.class);

	/**
	 *
	 * 首页列表
	 * TODO(需要从redis获取数据进行数据包装)
	 *
	 * @author TianBin
	 * @param page 页码
	 * @param pageSize 每页线束数量
	 * @return RestResp
	 * @since JDK 1.8
	 */
	@GET
	@Path("/index/list")
	public Object indexList(@DefaultValue("1") @QueryParam("page")int page,@DefaultValue("10") @QueryParam("pageSize")int pageSize){
		logger.info("indexList in GoldProductController, the params page is [{}],pageSize is [{}]",page,pageSize);
		try{
			return goldProductService.listPageIndexList(page, pageSize);
		} catch(ServiceException se){
			switch (se.getErrorCode()) {
				case 50000:
					throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
				default:
					break;
			}
		} catch(Exception e){
			logger.error("indexList in GoldProductController, the params error is [{}]","indexListError");
			logger.error("indexList in GoldProductController, the params Exception is [{}]",e);
			throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
		}
		return new Page<GoldProductDTO>(null,0,0,0L);
	}




	/**
	 *
	 * 根据id查询产品详情
	 * TODO(需要从redis获取数据进行数据包装)
	 * 
	 * @author TianBin
	 * @param productId  产品ID
	 * @return RestResp
	 * @since JDK 1.8
	 */
	@GET
	@Path("/info")
	public Object InfoByProduct(@QueryParam("productId")String productId){
		logger.info("list in GoldProductController, the params productId is [{}]",productId);		
		if (StringUtils.isBlank(productId))
			throw new AppRuntimeException(InfoCode.WRONG_PARAMETERS);
		try{
			return goldProductService.findInfoByProductId(productId);
		} catch(ServiceException se){
			switch (se.getErrorCode()) {
				case 50000:
					throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
				default:
					break;
			}
		} catch(Exception e){
			logger.error("InfoByProduct in GoldProductController, the params error is [{}]","InfoByProductError");
			logger.error("InfoByProduct in GoldProductController, the params Exception is [{}]",e);
			throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
		}
		return new GoldProductDetailsDTO();
	}

}
  