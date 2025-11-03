package com.radish.模块化及封装;

/**
 * final 子类，不可再被继承
 * @author luoyuheng
 * @date 2025/11/3 11:42
 */
public final class Demo3_Sealed_Circle extends Demo3_Sealed_Shape {
	@Override
	public int lines() {
		return 0;
	}
}
