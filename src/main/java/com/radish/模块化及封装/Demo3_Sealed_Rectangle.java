package com.radish.模块化及封装;

/**
 * @author luoyuheng
 * @date 2025/11/3 11:46
 */
public sealed class Demo3_Sealed_Rectangle extends Demo3_Sealed_Shape permits Demo3_Sealed_FilledRectangle {
	@Override
	public int lines() {
		return 0;
	}
}
