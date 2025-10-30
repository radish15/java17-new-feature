package com.radish.demo;

/**
 * @author luoyuheng
 * @date 2025/10/30 17:38
 */
public class Demo3_instanceof的模式匹配 {
	public static void main(String[] args) {
		int o = 1;
		if (o instanceof Integer i && i > 0) {
			System.out.println(i.intValue());
		} else if (o instanceof String s && s.startsWith("t")) {
			System.out.println(s.charAt(0));
		}
	}
}
