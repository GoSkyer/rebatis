package org.gosky.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class TypeConstants {

    public static List<Class<?>> typeList = new ArrayList<Class<?>>() {{
        add(String.class);

        add(Byte.class);
        add(Long.class);
        add(Short.class);
        add(Integer.class);
        add(Integer.class);
        add(Double.class);
        add(Float.class);
        add(Boolean.class);

        add(Byte[].class);
        add(Long[].class);
        add(Short[].class);
        add(Integer[].class);
        add(Integer[].class);
        add(Double[].class);
        add(Float[].class);
        add(Boolean[].class);

        add(byte.class);
        add(long.class);
        add(short.class);
        add(int.class);
        add(int.class);
        add(double.class);
        add(float.class);
        add(boolean.class);

        add(byte[].class);
        add(long[].class);
        add(short[].class);
        add(int[].class);
        add(int[].class);
        add(double[].class);
        add(float[].class);
        add(boolean[].class);

        add(Date.class);
        add(BigDecimal.class);
        add(BigDecimal.class);
        add(BigInteger.class);
        add(Object.class);

        add(Date[].class);
        add(BigDecimal[].class);
        add(BigDecimal[].class);
        add(BigInteger[].class);
        add(Object[].class);

//        add(Map.class);
//        add(HashMap.class);
        add(List.class);
        add(ArrayList.class);
        add(Collection.class);
        add(Iterator.class);
    }};
}
