package com.radish.demo;

/**
 * @author luoyuheng
 * @date 2025/10/30 17:38
 */
public class Demo3_instanceof的模式匹配 {
	public static void main(String[] args) {
		demo1();
		demo2();
	}

	// 基本用法
	static void demo1() {
		Object obj = "Hello World";

		// JDK 17 新写法 - 模式匹配
		if (obj instanceof String str) {
			System.out.println(str.toUpperCase()); // 直接使用 str，无需强制转换
		}
	}

	// 结合条件判断使用
	static void demo2() {
		Object obj = "test";

		// 在 if 条件中同时进行类型检查和业务逻辑判断
		if (obj instanceof String str && str.length() > 3) {
			System.out.println("字符串长度大于3: " + str);
		}

		// 多个条件组合
		Object number = 10;
		if (number instanceof Integer i && i > 0 && i < 100) {
			System.out.println("有效数字: " + i);
		}
	}
}
