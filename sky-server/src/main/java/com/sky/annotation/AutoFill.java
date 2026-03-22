package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)//该注解指定只能加在方法上
@Retention(RetentionPolicy.RUNTIME)
/*
* 它的核心作用是：告诉编译器，这个自定义注解不仅要保留在编译后的
* .class 文件中，还要保留到程序运行时（JVM 启动后）
* ，并且可以通过反射（Reflection）机制动态读取。*/

public @interface AutoFill {
    //指定数据库操作类型
    OperationType value();
}
