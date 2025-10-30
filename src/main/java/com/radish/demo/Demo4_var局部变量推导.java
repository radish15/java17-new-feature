package com.radish.demo;

import java.util.Arrays;

/**
 * @author luoyuheng
 * @Description 对于某些可以直接推导出类型的局部变量，可以使用var进行声明。
 * @date 2025/10/31 0:53
 */
public class Demo4_var局部变量推导 {
	public static void main(String[] args) {
		var nums = new int[] {1, 2, 3, 4, 5};
		var sum = Arrays.stream(nums).sum();
		System.out.println("数组之和为：" + sum);
	}
}
