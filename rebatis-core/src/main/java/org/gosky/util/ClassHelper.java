package org.gosky.util;


import org.gosky.annotations.Mapper;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by zzqno on 2017-3-21.
 * class助手 用于获取所有被注解的类
 */
public final class ClassHelper {

    /**
     * 定义集合类（用于存放加载的类）
     */
    private static Set<Class<?>> CLASS_SET;

    static {
//        String basePackage = ConfigHelper.getAppBasePackage();
        CLASS_SET = ClassUtil.getClassSet("org.gosky.mapping");
    }

    /**
     * 获取包下的所有类
     *
     * @return
     */
    public static Set<Class<?>> getClassSet() {
        return CLASS_SET;
    }

    /**
     * 获取应用包下所有带有mapper注解的接口
     *
     * @return
     */
    public static Set<Class<?>> getMapperInterfaceSet() {
        return CLASS_SET.stream().filter(cls -> cls.isAnnotationPresent(Mapper.class) && cls.isInterface()).collect(Collectors.toSet());
    }


    /**
     * 获取应用包下带有某注解的类
     *
     * @param annotationClass
     * @return
     */
    public static Set<Class<?>> getClassSetByAnnotation(Class<? extends Annotation> annotationClass) {
        return CLASS_SET.stream().filter(cls -> cls.isAnnotationPresent(annotationClass)).collect(Collectors.toSet());
    }

    /**
     * 获取应用包下名下某父类（或接口）的所有子类（或实现类）
     *
     * @param superClass
     * @return
     */
    public static Set<Class<?>> getClassSetBySuper(Class<?> superClass) {
        return CLASS_SET.stream().filter(cls -> superClass.isAssignableFrom(cls) && !superClass.equals(cls)).collect(Collectors.toSet());
    }

}
