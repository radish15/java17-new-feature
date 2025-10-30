# JDK17新特性梳理
## 语法层面新特性
### 1、文本块
文本块功能，文本块指多行的字符串，使用连续的三个双引号来包围一段带换行的文字，它避免了换行转义的需要，并支持String.format。

同时添加了两个新的转义字符:

*   \\: 置于行尾，用来将两行连接为一行
*   \s: 单个空白字符

示例代码：
```java
public static void main(String[] args) {
    String text =
            """
            1234567890\s222
            0987654321\
            54321 %s;
            """;
    System.out.println(String.format(text, "AAA"));
}
```
> 打印结果
> 1234567890 222\
> 098765432154321 AAA;

### 2、Switch表达式增强
扩展switch语句，使其既可以作为语句使用，也可以作为表达式使用，并且两种形式都可以用“传统”或“简化”的作用域和控制流行为。
同时添加了yield关键字，提供break与switch返回值的功能。

从 JDK8 到 JDK17，Switch 表达式做了很大的增强，再也不是简单的if-else的替代品了。

扩展switch语句，使其既可以作为语句使用，也可以作为表达式使用，并且两种形式都可以用“传统”或“简化”的作用域和控制流行为。同时添加了yield关键字，提供break 与switch返回值的功能。

示例 1：可以将多个匹配写到一起。
```java
static void demo1 (String type) {
    switch (type) {
        case "a", "A" -> System.out.println("A");
        case "b", "B" -> System.out.println("B");
        default -> System.out.println("404");
    };
}
```

示例 2：每个分支直接返回一个值。
```java
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
```

### 3、instanceof的模式匹配
在 JDK 17 中，instanceof 的模式匹配功能得到了进一步增强。以下是一些关于 instanceof 新用法的演示示例：

1.基本用法
```java
public class InstanceOfPatternMatchingDemo {
    public static void main(String[] args) {
        Object obj = "Hello World";
        
        // 传统写法
        if (obj instanceof String) {
            String str = (String) obj;
            System.out.println(str.toUpperCase());
        }
        
        // JDK 17 新写法 - 模式匹配
        if (obj instanceof String str) {
            System.out.println(str.toUpperCase()); // 直接使用 str，无需强制转换
        }
    }
}
```
2.结合条件判断使用
```java
public class InstanceOfWithConditions {
    public static void main(String[] args) {
        Object obj = "test";
        
        // 在 if 条件中同时进行类型检查和业务逻辑判断
        if (obj instanceof String str && str.length() > 3) {
            System.out.println("字符串长度大于3: " + str);
        }
        
        // 多个条件组合
        Object number = 10;
        if (number instanceof Integer i && i > 0 && i < 100) {
            System.out.println("有效数字: " + i);
        }
    }
}
```

### 4、var局部变量推导
```java
var nums = new int[] {1, 2, 3, 4, 5};
var sum = Arrays.stream(nums).sum();
System.out.println("数组之和为：" + sum);
```

## 模块化及封装
从 JDK8 开始，JDK 中陆续更新了很多应用相关的新特性。这些新特性里明显能够看出借鉴了很多新兴的动态语言的特征，让 Java 变得更年轻有活力了。

### 1、记录类 record

记录类（Record）是 JDK 14 引入并在后续版本中完善的特性，用于创建不可变的数据载体类。

#### 主要特点：
- **简洁性**：自动生成构造函数、getter方法、`equals()`、`hashCode()` 和 `toString()` 方法
- **不可变性**：所有字段都是 `final` 的，初始化后，不允许修改属性值，用反射也不行
- **透明性**：字段可以通过标准的 getter 方法访问

#### 示例代码：

```java
// 基本记录类定义
public record Person(String name, int age) {
    // 可以添加自定义方法
    public boolean isAdult() {
        return age >= 18;
    }
}

// 使用示例
public class RecordDemo {
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
```
```java
// 带有紧凑构造函数的记录类
public record Point(int x, int y) {
    // 紧凑构造函数（不声明参数）
    public Point {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("坐标不能为负数");
        }
    }
    
    // 标准构造函数
    public Point() {
        this(0, 0);
    }
    
    // 自定义方法
    public double distanceFromOrigin() {
        return Math.sqrt(x * x + y * y);
    }
}
```

### 2、隐藏类 Hidden Classes
从 JDK15 开始，JDK 引入了一个很有意思的特性，隐藏类。隐藏类是一种不能被其他类直接使用的类。隐藏类不再依赖于类加载器，而是通过读取目标类字节码的方式，创建一个对其他类字节码隐藏的class对象，然后通过反射的方式创建对象，调用方法。

我们先来一个示例理解一下什么是隐藏类，再来思考隐藏类有什么用处。

比如先编写一个普通的测试类

```java
public class HiddenClass {
	public String sayHello(String name) {
		return "Hello, " + name;
	}

	public static void printHello(String name) {
		System.out.printf("""
				Hello, %s !
				Hello, HiddenClass !
				%n""", name);
	}
}
```
传统方式下，要使用这个类，就需要经过编译，然后类加载的整个过程。但是隐藏类机制允许直接从编译后的class字节码入手，并且绕过整个类加载的复杂过程，直接使用这个类。

比如，我们可以使用下面的方法获取class字节数组：

```java
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
```
这样就可以拿到一串编码后的class文件的字节码。接下来，就可以用这个字节码直接生成这个类。例如：
```java
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
```

示例很简单，看似花里胡哨，但是其实，这将是以后开发框架非常重要的一个特性。因为这些隐藏类直接操作字节码，因此，不再需要 JVM 中繁琐的类加载过程，不再需要进行那些链接Linking操作，类的卸载也不再受类加载器的限制，可以极大提高 Java 的动态语言能力。

实际上这也是 Java吸收其他语言优点的一种表现。近年来有很多基于 JVM 的语言都在强调动态语言。比如 Scala，Kotlin 中大量运用匿名函数，Java 自己的 Lambda 表达式，本质上也是一种匿名函数。这些匿名函数在语法层面并不需要提前声明，只要在运行时拿来用就可以了。但是在 JVM 中，Java 一切皆对象，这些匿名函数也必须经过类加载的繁琐过程，并且类的卸载也非常受限制。所以在 Spring 框架中，大量的运用了 ASM 这样的直接操作字节码的技术，就是为了加快这些动态对象的生命周期。但是这些技术方案的实现即麻烦又低效，而 JDK 中引入了隐藏类机制，就可以作为生成动态类的新标准。