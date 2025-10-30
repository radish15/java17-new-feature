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
