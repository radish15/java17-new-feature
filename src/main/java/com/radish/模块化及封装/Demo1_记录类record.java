package com.radish.模块化及封装;

/**
 * @author luoyuheng
 * @Description
 * @date 2025/10/31 1:04
 */
// 基本记录类定义
record Person(String name, int age) {
	// 可以添加自定义方法
	public boolean isAdult() {
		return age >= 18;
	}
}

// 使用示例
public class Demo1_记录类record {
	public static void main(String[] args) {
		// 创建记录实例
		Person person = new Person("张三", 25);

		// 自动生成的方法
		System.out.println(person.name());    // 张三
		System.out.println(person.age());     // 25
		System.out.println(person.toString()); // Person[name=张三, age=25]

		// 自定义方法
		System.out.println(person.isAdult()); // true

		// 不可变性
		// person.age() = 30; // 编译错误，字段是 final 的
	}
}

