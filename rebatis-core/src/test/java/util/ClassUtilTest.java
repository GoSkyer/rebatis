package util;

import org.gosky.util.ClassUtil;
import org.junit.Test;

import java.util.Set;

public class ClassUtilTest {


    @Test
    public void getClassLoad() {
        Set<Class<?>> classSet = ClassUtil.getClassSet("org.gosky.util");
        classSet.forEach(clz -> {
            System.out.println(clz.getSimpleName());
        });
    }

    @Test
    public void loadClass() {
    }

    @Test
    public void getClassSet() {
    }

    @Test
    public void isInt() {
    }

    @Test
    public void isLong() {
    }

    @Test
    public void isDouble() {
    }

    @Test
    public void isString() {
    }
}
