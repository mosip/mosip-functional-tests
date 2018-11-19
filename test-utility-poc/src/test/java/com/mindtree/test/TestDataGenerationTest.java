package com.mindtree.test;

import org.junit.Assert;
import org.junit.Test;

import mosip.main.Main;

public class TestDataGenerationTest {

@Test
public void createTestData() {
	String result=Main.createTestData("11110013");
	Assert.assertEquals("Test datas has created sucessfull please check D drive TestDataGenerator folder", result);
}
}
