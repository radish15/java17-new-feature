package com.radish.demo;

/**
 * @author luoyuheng
 * @date 2025/10/30 17:21
 */
public class Demo2_Switch表达式增强 {
	public static void main(String[] args) {
		demo1("a");
		System.out.println(demo2("苹果"));
	}

	static void demo1 (String type) {
		switch (type) {
			case "a", "A" -> System.out.println("A");
			case "b", "B" -> System.out.println("B");
			default -> System.out.println("404");
		};
	}

	static int demo2 (String type) {
		return switch (type) {
			case "苹果", "香蕉", "葡萄" -> 1;
			case "西红柿", "土豆", "茄子" -> 2;
			default -> {
				System.out.println("未知食物");
				yield 3;
			}
		};
	}
}
