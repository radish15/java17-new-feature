package com.radish.模块化及封装;

/**
 * 非密封子类，可以随意继承
 * @author luoyuheng
 * @date 2025/11/3 11:43
 */
public non-sealed class Demo3_Sealed_Square extends Demo3_Sealed_Shape {

	@Override
	public int lines() {
		return 0;
	}
}
