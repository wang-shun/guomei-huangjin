package com.gomemyc.service;


import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/spring/trade-provider.xml"})
@ActiveProfiles("test")
public abstract class BaseFunctionalTestCase {


	
	
	
}