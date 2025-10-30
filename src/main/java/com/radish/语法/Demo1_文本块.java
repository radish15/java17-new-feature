package com.radish.语法;

/**
 * @author luoyuheng
 * @date 2025/10/30 17:11
 */
public class Demo1_文本块 {
	public static void main(String[] args) {
		String text =
				"""
				1234567890\s222
				0987654321\
				54321 %s;
				""";
		System.out.println(String.format(text, "AAA"));
	}
}
