package com.gomemyc.provider;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gomemyc.base.BaseFunctionalTestCase;
import com.gomemyc.gold.service.GoldCommonService;
import org.junit.Test;

/**
 * Created by TianBin on 2017/3/15.
 */
public class GoldCommonServiceTest  extends BaseFunctionalTestCase {


    @Reference
    GoldCommonService goldCommonService;
    

    @Test
    public void selectGoldRealPrice(){
//        System.out.println(goldCommonService.selectGoldRealPrice());
    }

}
