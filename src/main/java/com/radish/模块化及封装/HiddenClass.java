package com.radish.模块化及封装;

/**
 * @author luoyuheng
 * @Description
 * @date 2025/10/31 1:15
 */
public class HiddenClass {
	public String sayHello(String name) {
		return "Hello, " + name;
	}

	public static void printHello(String name) {
		System.out.printf("""
                Hello, %s !
                Hello, HiddenClass !
                %n""", name);
	}
}