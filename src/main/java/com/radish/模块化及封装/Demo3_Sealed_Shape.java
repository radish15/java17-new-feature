package com.radish.模块化及封装;

/**
 * @author luoyuheng
 * @date 2025/11/3 11:40
 */
public sealed abstract class Demo3_Sealed_Shape permits Demo3_Sealed_Circle, Demo3_Sealed_Rectangle, Demo3_Sealed_Square {
	public abstract int lines();
}
