/** 
 * Project Name:myc-rest 
 * File Name:GoldProductController.java 
 * Package Name:com.gomemyc.controller 
 * Date:2017年3月5日下午4:48:58 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  
  
package com.gomemyc.controller;


import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.alibaba.dubbo.config.annotation.Reference;
import com.gomemyc.gold.dto.GoldQueryTimeProductDTO;
import com.gomemyc.gold.service.GoldProductService;
import com.gomemyc.http.MediaTypes;

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
public class GoldQueryProductController {
	
	@Reference
	private GoldProductService goldProductService;

	private static final Logger logger = LoggerFactory.getLogger(GoldQueryProductController.class);

	/**
	 *
	 * 查询
	 * TODO(需要从redis获取数据进行数据包装)
	 *
	 * @author TianBin
	 * @param page 页码
	 * @param pageSize 每页线束数量
	 * @return RestResp
	 * @since JDK 1.8
	 */
	@GET
	@Path("/queryproduct")
	public Object queryproduct(){
		List<GoldQueryTimeProductDTO> queryTimeProduct = goldProductService.queryTimeProduct(0, 0);
		logger.info("queryproduct in GoldQueryProductController, the params queryTimeProduct is [{}]",queryTimeProduct);
		return queryTimeProduct;
	}
}
  