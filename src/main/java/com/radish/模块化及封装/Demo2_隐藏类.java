package com.radish.模块化及封装;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * @author luoyuheng
 * @Description
 * @date 2025/10/31 1:13
 */
public class Demo2_隐藏类 {
	public static void main(String[] args) throws Throwable {
		testInvokeHiddenClass(printHiddenClassBytesInBase64());
	}

	public static String printHiddenClassBytesInBase64(){
		//编译后的 class 文件地址
		String classPath = "target/classes/com/radish/模块化及封装/HiddenClass.class";
		try {
			byte[] bytes = Files.readAllBytes(Paths.get(classPath));
			String result = Base64.getEncoder().encodeToString(bytes);
			System.out.println(result);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void testInvokeHiddenClass(String classinfo) throws Throwable {
		//class文件的字节码
		byte[] classInBytes = Base64.getDecoder().decode(classinfo);
		Class<?> proxy = MethodHandles.lookup()
				.defineHiddenClass(classInBytes, true, MethodHandles.Lookup.ClassOption.NESTMATE)
				.lookupClass();

		// 输出类名
		System.out.println(proxy.getName());
		// 输出类有哪些函数
		for (Method method : proxy.getDeclaredMethods()) {
			System.out.println(method.getName());
		}
		// 2. 调用对应的方法
		MethodHandle mhPrintHello = MethodHandles.lookup().findStatic(proxy, "printHello", MethodType.methodType(void.class, String.class));
		mhPrintHello.invokeExact("radish");
		Object proxyObj = proxy.getConstructors()[0].newInstance();
		MethodHandle mhSayHello = MethodHandles.lookup().findVirtual(proxy, "sayHello", MethodType.methodType(String.class, String.class));
		System.out.println(mhSayHello.invoke(proxyObj, "radish"));
	}
}
