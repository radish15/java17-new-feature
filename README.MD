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

### 3、密封类 Sealed Classes
密封类在 JDK15 引入，到 JDK17 中正式转正。

在 JDK8 中，每一个类都可以被任意多个子类继承，并修改其中的内置功能。比如 JDK8 中最重要的类加载双亲委派机制，在应用当中，程序员可以随意挑选一个内置的类加载器，继承出新的类加载器实现，随意打破双亲委派机制。这其实是不太安全的，意味着很多内置的行为得不到保护。而密封类就是用来限制每一个父类可以被哪些子类继承或者实现。

首先在声明类或方法时，如果增加sealed修饰，那么还需要同时增加permits指定这个类可以被哪些类来继承或实现。例如：

```java
public sealed abstract class Shape permits Circle, Rectangle, Square {
    public abstract int lines();
}
```

接下来，Shape 的子类也会要收到限制。在声明类时，需要声明自己的密封属性。可以有三个选项：

*   final，表示这个子类不能再被继承了。
*   non-sealed 表示这个子类没有密封特性，可以随意继承。
*   sealed 表示这个子类有密封特性。再按照之前的方式声明他的子类。

例如针对 Shape，就可以声明这样的一些子类：

```java
// 非密封子类，可以随意继承
public non-sealed class Square extends Shape{
    @Override
    public int lines() {
        return 4;
    }
}
//final 子类，不可再被继承
public final class Circle extends Shape{
    @Override
    public int lines() {
        return 0;
    }
}
// 密封子类，继续声明他所允许的子类。
public sealed class Rectangle extends Shape permits FilledRectangle {
    @Override
    public int lines() {
        return 3;
    }
}

public final class FilledRectangle extends Rectangle{
    @Override
    public int lines() {
        return 0;
    }
}
```

比如对 JDK8 的类加载体系，就可以通过密封类机制，让子类只能从 SecureClassLoader或者 URLClassLoader 往下继承，这样就可以保护SecureClassLoader和 URLClassLoader 中的安全行为。

> 当然，这只是假设，JDK 的类加载机制并没有这么做。

密封类能够保护父类的安全行为，但是也有一些限制。父类和指定的子类必须在同一个显式命名的module下，并且子类必须直接继承父类。

### 4、模块化 Module System
Java 模块系统（Module System），也称为 Project Jigsaw，是在 JDK 9 中正式引入的，而非 JDK 17 特有的特性。它是 Java 平台的一个重要架构改进。

以下是模块化系统的主要特性：

1. **模块声明**
    - 使用 `module` 关键字定义模块，并在 `module-info.java` 文件中声明模块及其依赖关系。
    - 示例：
      ```java
      module my.module {
          requires java.base;
          exports com.example.mypackage;
      }
      ```


2. **强封装性**
    - 默认情况下，模块中的包是私有的，除非显式地通过 `exports` 指令对外暴露。
    - 这增强了封装性和安全性，只有明确导出的包才能被其他模块访问。

3. **明确的依赖关系**
    - 通过 `requires` 指令明确声明对其他模块的依赖。
    - 这有助于构建更清晰、可维护的应用程序结构，并防止意外的内部API使用。

4. **解决类路径问题**
    - 解决了传统的 "jar hell" 问题，提供了更好的依赖管理和冲突检测机制。
    - 引入了模块路径（module path）的概念，与传统的类路径（classpath）相区分。

5. **服务发现机制**
    - 支持通过 `provides...with` 和 `uses` 指令实现服务提供者和服务使用者之间的解耦。
    - 提供了一种标准化的方式来实现插件化架构。

6. **可扩展性**
    - 允许创建自定义运行时镜像（custom runtime images），只包含应用程序实际需要的模块，减小部署包大小。

模块系统是 Java 发展历程中的一个重要里程碑，对于现代 Java 应用开发具有深远影响。它并不是 JDK 17 独有的特性，而是一个基础性的平台改进。

### 5、GC调整
另外，从 JDK8 到 JDK17，还涉及到了非常多的调整，这些调整让 Java 具备了更多现代语言的特性，并且执行效率也在不断提高。

​	比如，重写了Socket 底层的 API 代码，让这写代码使用更简单，实现也更易于维护和替换。

​	默认禁用偏向锁。从 JDK1.6 开始，对synchronized关键字的实现就形成了 无锁 ->偏向锁->轻量级锁->重量级锁 的锁升级过程。而在 JDK15 中，默认就废弃了偏向锁。当然，目前还是可以通过添加参数 -XX:+UseBiasedLocking 手动开启偏向锁。

​	不过 JDK 在升级过程中，一直保持着良好的向下兼容性，因此，有很多优化调整对开发工作影响都还不是太大。但是在这些调整当中，有一部分调整是大家需要额外关心一下的，那就是对于 GC 的调整。

1 、ZGC 转正

​	ZGC 在之前已经做过介绍，是现在最为强大的一个垃圾回收器，自 JDK11 开始引入，从 JDK15 开始正式投入了使用。现在使用-XX:+UseZGC参数就可以快速使用 ZGC 。

​	另外，ZGC 的具体实现其实也在版本升级过程中不断优化。在 JDK17 中使用指令 java -XX:+PrintFlagsFinal -version 可以简单看到，与 ZGC 相关的系统不稳定参数已经基本没有了。`G1 的还有一大堆` 这也说明 ZGC 的算法优化已经相当成熟了。

​	随 ZGC 登场的，还有 RedHat 推出的Shenandoah 垃圾回收器。尽管 Oracle 一直比较抵触这个非官方推出的垃圾回收器，但是最终也还是将Shennandoah 垃圾回收器以可选的方案集成了进来。现在可以使用 -XX:+UseShenandoahGC 参数手动选择shennandoah。

2 、废除 CMS

​	虽然 CMS作为 G1 之前唯一的一款并发的垃圾回收器，在相当长的时间里，都扮演者非常重要的角色。在最为经典的 JDK8 时代，尽管 CMS 一直没有作为默认垃圾回收器登场过，但是关于 G1 和 CMS 的比较以及取舍，一直都是业界津津乐道的话题。但是，随着 G1 垃圾回收器发展得更为完善，以及后续ZGC，shennandoah等现代垃圾回收器开始登场，过于复杂的 CMS 垃圾回收器还是退出了历史舞台。

​	在 JDK14 中，就彻底删除了 CMS 垃圾回收器。与 CMS 一起退场的，还有Parallel Scavenge +SerialOld的经典 GC 组合。`SerialOld 这个最早的垃圾回收器其实早就应该退出历史舞台了，只不过由于他一直作为 CMS 的补充方案而一直保留。这次也终于随着 CMS 一起退出了。`

## GraalVM 虚拟机
Graal 编译器以及由此诞生的GraalVM，虽然目前还处在实验阶段，但是也是 Java 程序员们必须要了解的，因为他未来极有可能替代 HotSpot，成为 Java生态的下一代技术基础。

### 1、关于Graal

Graal编译器最早是作为 HotSpot 的 C1 编译器的下一代编译器设计的，使用 Java 语言进行编写。2012 年，Graal编译器才发展成为一个独立的 Java 编译项目。而早期的 Graal其实也和 C1，C2 一样，需要与 HotSpot 虚拟机配合工作。但是随着 JDK9 开始推出 JVMCI(Java虚拟机编译器接口)，才让 Graal 可以从 HotSpot 中独立出来，并逐渐形成了现在的 GraalVM 。

虽然你可能对 Graal 了解不多，但是，Graal 其实一直深受业界关注。Oracle 公司希望他能够发展成为一个更完美的编译器，款高编译效率、高输出质量、支持提前编译和即时编译，同时支持应用于包括HotSpot在内的不同虚拟机。而使用 C \C++编写的 C1 和 C2 编译器，也逐渐变得越来越臃肿，维护和更新都更加困难。这时使用 Java 语言编写的 Graal 自然就成了首选 。

另外，在业务层面，Java 也急需一种更高效的编译器来迎合现在越来越火爆的云原生架构。现在作为 Java 主流的服务端版本总体上是面向大规模，长时间运行的系统设计的。像即时编译器(JIT)、性能优化、垃圾回收等有代表性的特征都是面向程序长时间运行设计的，需要一段时间预热才能达到最佳性能，才能享受硬件规模提升带来的红利。但是现在的微服务背景下，对服务的规模以及稳定性要求在逐渐降低，反而对容器化、启动速度、预热时间等方面提出了新的要求。而这方面都是 Java的弱项。因此 Java 语言也需要这样一款新的虚拟机，来提升与很多新出来的现代语言，比如golang的竞争优势。

### 2、使用GraalVM
接下来使用 GraalVM 就比较简单了。使用 GraalVM 和使用其他的 JDK，没有什么大的区别。使用 GraalVM 的方式也是 下载-》配置环境变量-》编译-》执行 几个步骤。

GraalVM 的官网地址是：<https://www.graalvm.org> 。官网上目前就可以下载对应版本的产品。当前有 Java17 和 Java21两个版本。

下载下来后是一个tar包压缩文件。接下来跟安装jdk一样，解压，配置JAVA\_HOME 环境变量，就可以用java -version进行测试了。这部分就略过了。比如我安装后的结果是这样的

```java
[oper@localhost ~]$ java -version
java version "17.0.9" 2023-10-17 LTS
Java(TM) SE Runtime Environment Oracle GraalVM 17.0.9+11.1 (build 17.0.9+11-LTS-jvmci-23.0-b21)
Java HotSpot(TM) 64-Bit Server VM Oracle GraalVM 17.0.9+11.1 (build 17.0.9+11-LTS-jvmci-23.0-b21, mixed mode, sharing)
```

​	另外，在 GraalVM 中还提供了一个管理指令 gu

```shell
[oper@localhost ~]$ gu list
ComponentId              Version             Component name                Stability                     Origin 
---------------------------------------------------------------------------------------------------------------------------------
graalvm                  23.0.2              GraalVM Core                  Supported                     
native-image             23.0.2              Native Image                  Early adopter    
```

​	接下来，写一个简单的 Java 代码进行测试。Hello.java

```java
public class Hello {
    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}
```

​	使用 javac 编译成 Hello.class文件。然后使用java Hello进行执行。使用time指令看到执行的时间如下：

```shell
[oper@localhost ~]$ time java Hello
Hello World!

real    0m0.062s
user    0m0.067s
sys     0m0.011s
```

​	接下来，切换成 Oracle 的 JDK17，也同样执行这个程序，做个简单的对比。结果如下：

​	然后，GraalVM 还提供了一个功能，可以将 Class 文件直接编译成本地镜像，这些本地镜像不需要 JVM 虚拟机也能直接运行。这就是 Graal 的 AOT 提前编译。

​	但是我在编译这个简单的 Hello 时，却遇到了意想不到的错误：

```shell
[oper@localhost ~]$ native-image Hello
========================================================================================================================
GraalVM Native Image: Generating 'hello' (executable)...
========================================================================================================================
[1/8] Initializing...                                                                                    (3.4s @ 0.09GB)
 Java version: 17.0.9+11-LTS, vendor version: Oracle GraalVM 17.0.9+11.1
 Graal compiler: optimization level: 2, target machine: armv8-a, PGO: ML-inferred
 C compiler: gcc (redhat, aarch64, 11.4.1)
 Garbage collector: Serial GC (max heap size: 80% of RAM)
[2/8] Performing analysis...  [****]                                                                     (7.6s @ 0.24GB)
   1,831 (59.26%) of  3,090 types reachable
   1,733 (46.69%) of  3,712 fields reachable
   7,726 (35.98%) of 21,471 methods reachable
     623 types,     0 fields, and   285 methods registered for reflection
      49 types,    32 fields, and    48 methods registered for JNI access
       4 native libraries: dl, pthread, rt, z
[3/8] Building universe...                                                                               (1.0s @ 0.25GB)
[4/8] Parsing methods...      [**]                                                                       (2.6s @ 0.22GB)
[5/8] Inlining methods...     [***]                                                                      (0.6s @ 0.21GB)
[6/8] Compiling methods...    [****]                                                                    (16.6s @ 0.25GB)
[7/8] Layouting methods...    [*]                                                                        (0.4s @ 0.39GB)
[8/8] Creating image...       [*
]                                                                        (0.0s @ 0.27GB)
------------------------------------------------------------------------------------------------------------------------
                        2.7s (8.1% of total time) in 146 GCs | Peak RSS: 0.95GB | CPU load: 1.95
------------------------------------------------------------------------------------------------------------------------
Produced artifacts:
 /home/oper/svm_err_b_20231129T171758.715_pid2504.md (build_info)
========================================================================================================================
Failed generating 'hello' after 33.1s.

The build process encountered an unexpected error:

> java.lang.RuntimeException: There was an error linking the native image: Linker command exited with 1

Linker command executed:
/usr/bin/gcc -z noexecstack -Wl,--gc-sections -Wl,--version-script,/tmp/SVM-6698742675696986223/exported_symbols.list -Wl,-x -o /home/oper/hello hello.o /home/oper/graalvm-jdk-17.0.9/lib/svm/clibraries/linux-aarch64/liblibchelper.a /home/oper/graalvm-jdk-17.0.9/lib/static/linux-aarch64/glibc/libnet.a /home/oper/graalvm-jdk-17.0.9/lib/static/linux-aarch64/glibc/libnio.a /home/oper/graalvm-jdk-17.0.9/lib/static/linux-aarch64/glibc/libjava.a /home/oper/graalvm-jdk-17.0.9/lib/static/linux-aarch64/glibc/libfdlibm.a /home/oper/graalvm-jdk-17.0.9/lib/svm/clibraries/linux-aarch64/libjvm.a -Wl,--export-dynamic -v -L/tmp/SVM-6698742675696986223 -L/home/oper/graalvm-jdk-17.0.9/lib/static/linux-aarch64/glibc -L/home/oper/graalvm-jdk-17.0.9/lib/svm/clibraries/linux-aarch64 -lz -lpthread -ldl -lrt

Linker command output:
使用内建 specs。
COLLECT_GCC=/usr/bin/gcc
COLLECT_LTO_WRAPPER=/usr/libexec/gcc/aarch64-redhat-linux/11/lto-wrapper
目标：aarch64-redhat-linux
配置为：../configure --enable-bootstrap --enable-host-pie --enable-host-bind-now --enable-languages=c,c++,fortran,lto --prefix=/usr --mandir=/usr/share/man --infodir=/usr/share/info --with-bugurl=http://bugzilla.redhat.com/bugzilla --enable-shared --enable-threads=posix --enable-checking=release --with-system-zlib --enable-__cxa_atexit --disable-libunwind-exceptions --enable-gnu-unique-object --enable-linker-build-id --with-gcc-major-version-only --enable-plugin --enable-initfini-array --without-isl --enable-multilib --with-linker-hash-style=gnu --enable-gnu-indirect-function --build=aarch64-redhat-linux --with-build-config=bootstrap-lto --enable-link-serialization=1
线程模型：posix
Supported LTO compression algorithms: zlib zstd
gcc 版本 11.4.1 20230605 (Red Hat 11.4.1-2) (GCC) 
COMPILER_PATH=/usr/libexec/gcc/aarch64-redhat-linux/11/:/usr/libexec/gcc/aarch64-redhat-linux/11/:/usr/libexec/gcc/aarch64-redhat-linux/:/usr/lib/gcc/aarch64-redhat-linux/11/:/usr/lib/gcc/aarch64-redhat-linux/
LIBRARY_PATH=/usr/lib/gcc/aarch64-redhat-linux/11/:/usr/lib/gcc/aarch64-redhat-linux/11/../../../../lib64/:/lib/../lib64/:/usr/lib/../lib64/:/usr/lib/gcc/aarch64-redhat-linux/11/../../../:/lib/:/usr/lib/
COLLECT_GCC_OPTIONS='-z' 'noexecstack' '-o' '/home/oper/hello' '-v' '-L/tmp/SVM-6698742675696986223' '-L/home/oper/graalvm-jdk-17.0.9/lib/static/linux-aarch64/glibc' '-L/home/oper/graalvm-jdk-17.0.9/lib/svm/clibraries/linux-aarch64' '-mlittle-endian' '-mabi=lp64' '-dumpdir' '/home/oper/hello.'
 /usr/libexec/gcc/aarch64-redhat-linux/11/collect2 -plugin /usr/libexec/gcc/aarch64-redhat-linux/11/liblto_plugin.so -plugin-opt=/usr/libexec/gcc/aarch64-redhat-linux/11/lto-wrapper -plugin-opt=-fresolution=/tmp/cc7AWuPB.res -plugin-opt=-pass-through=-lgcc -plugin-opt=-pass-through=-lgcc_s -plugin-opt=-pass-through=-lc -plugin-opt=-pass-through=-lgcc -plugin-opt=-pass-through=-lgcc_s --build-id --no-add-needed --eh-frame-hdr --hash-style=gnu -dynamic-linker /lib/ld-linux-aarch64.so.1 -X -EL -maarch64linux -o /home/oper/hello -z noexecstack /usr/lib/gcc/aarch64-redhat-linux/11/../../../../lib64/crt1.o /usr/lib/gcc/aarch64-redhat-linux/11/../../../../lib64/crti.o /usr/lib/gcc/aarch64-redhat-linux/11/crtbegin.o -L/tmp/SVM-6698742675696986223 -L/home/oper/graalvm-jdk-17.0.9/lib/static/linux-aarch64/glibc -L/home/oper/graalvm-jdk-17.0.9/lib/svm/clibraries/linux-aarch64 -L/usr/lib/gcc/aarch64-redhat-linux/11 -L/usr/lib/gcc/aarch64-redhat-linux/11/../../../../lib64 -L/lib/../lib64 -L/usr/lib/../lib64 -L/usr/lib/gcc/aarch64-redhat-linux/11/../../.. --gc-sections --version-script /tmp/SVM-6698742675696986223/exported_symbols.list -x hello.o /home/oper/graalvm-jdk-17.0.9/lib/svm/clibraries/linux-aarch64/liblibchelper.a /home/oper/graalvm-jdk-17.0.9/lib/static/linux-aarch64/glibc/libnet.a /home/oper/graalvm-jdk-17.0.9/lib/static/linux-aarch64/glibc/libnio.a /home/oper/graalvm-jdk-17.0.9/lib/static/linux-aarch64/glibc/libjava.a /home/oper/graalvm-jdk-17.0.9/lib/static/linux-aarch64/glibc/libfdlibm.a /home/oper/graalvm-jdk-17.0.9/lib/svm/clibraries/linux-aarch64/libjvm.a --export-dynamic -lz -lpthread -ldl -lrt -lgcc --push-state --as-needed -lgcc_s --pop-state -lc -lgcc --push-state --as-needed -lgcc_s --pop-state /usr/lib/gcc/aarch64-redhat-linux/11/crtend.o /usr/lib/gcc/aarch64-redhat-linux/11/../../../../lib64/crtn.o
/usr/bin/ld: 找不到 -lz
collect2: 错误：ld 返回 1

Please inspect the generated error report at:
/home/oper/svm_err_b_20231129T171758.715_pid2504.md

If you are unable to resolve this problem, please file an issue with the error report at:
https://graalvm.org/support
```

​	经分析，这是因为我当前服务器上缺少zlib库导致。所以需要先安装zilb库

```shell
# root权限安装zlib库
sudo yum install zlib-devel
```

​	之后再重新编译，就可以编译出一个可以执行的hello应用程序。这个应用程序不需要JDK也能正常运行，并且执行速度也更快。

    [oper@localhost ~]$ java -version
    bash: java: command not found...
    Install package 'java-11-openjdk-headless' to provide command 'java'? [N/y] n


    [oper@localhost ~]$ time ./hello
    Hello World!

    real    0m0.006s
    user    0m0.000s
    sys     0m0.006s

​	稍有曲折，完成了第一次GraalVM的体验。从这个过程中可以简单看出，GraalVM的这种AOT编译模式，能够极大提升Java程序的执行速度，更贴合现在的微服务，云原生的技术环境。所以，或许不久的将来，深入理解GraalVM有可能成为每个java程序员的必修课。

​	而从当前的官网文档中，可以看到 GraalVM 更为强大之处。
