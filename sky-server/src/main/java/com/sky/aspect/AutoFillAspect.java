package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

//自定义切面，实现公共字段自动填充处理逻辑
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /*
    * 切入点*/
    @Pointcut("execution(* com.sky.mapper.*.*(..))&&@annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}


    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) throws NoSuchMethodException {

        //获取方法签名对象（拥有下面方法的对象，大门的钥匙）
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获得方法上的注解对象(给谁去注解的)
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        //获得数据库操作类型(update/insert)
        OperationType operationType = autoFill.value();

        //获取到当前被拦截的方法的参数--实体对象（就是有updatetime、updateuser那些实体类）
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0){
            //（如果没有的话那就不用整这些了）
            return;
        }

        Object entity = args[0];
        //准备赋值的数据（当然了，这个是针对更新user和更新time的特定的数据，你要想造点其他aop你也可以准备一些其他数据）
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //根据当前不同的操作类型，对对应的属性通过反射来赋值
        if (operationType == OperationType.INSERT){
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setCreateTime.invoke(entity,now);
                setCreateUser.invoke(entity,currentId);
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else  if(operationType == OperationType.INSERT){
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }

}
