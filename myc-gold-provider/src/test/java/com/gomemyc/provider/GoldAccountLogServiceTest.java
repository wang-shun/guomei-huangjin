package com.gomemyc.provider;/**
 * Created by jk on 2017/3/22.
 */

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import com.alibaba.dubbo.config.annotation.Reference;
import com.gomemyc.base.BaseFunctionalTestCase;
import com.gomemyc.gold.dto.GoldAccountLogDTO;
import com.gomemyc.gold.service.GoldAccountLogService;

/**
 * ClassName:
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:   TODO ADD REASON. <br/>
 * Date:     2017/3/22 <br/>
 *
 * @author zhuyunpeng
 * @description 对账日志文件测试用例
 * @see
 * @since JDK 1.8
 */
public class GoldAccountLogServiceTest extends BaseFunctionalTestCase {

	
    @Reference
    private GoldAccountLogService goldAccountLogService;

    //保存日志用例
    @Test
    public void testInsert(){
        GoldAccountLogDTO goldAccountLogDTO = new GoldAccountLogDTO("No128", "test12", "100001", "feel", new Date(), 1, 1, "日收益", 10, new BigDecimal(100.00), new BigDecimal(10.0), "挺划算");
        int an = goldAccountLogService.addGoldAccountLog(goldAccountLogDTO);
        System.out.println("an======================="+an);

    }

    //查询指定用户对账记录数用例
    @Test
    public void testCountRecord(){
        int an = goldAccountLogService.countLogRecord("test11");
        System.out.println("an======================="+an);
    }
    
    //查询所有用户对账日志记录
    @Test
    public void testSelectAllRecord(){
    	List<GoldAccountLogDTO> records = goldAccountLogService.selectAllRecord();
    	int count=0;
    	for (GoldAccountLogDTO dto : records) System.out.println("dto"+count+++"===="+dto.toString());
		
    }

}
