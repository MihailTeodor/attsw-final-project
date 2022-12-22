package com.gurzumihail.library;

import static org.junit.Assert.*;

import org.junit.Test;

public class AppTest {
	App myApp;
	@Test
	public void test() {
		myApp = new App();
		assertEquals("Hello!", myApp.main("Hello!"));
	}

}
