//package org.gosky.parsing;
//
//import org.gosky.mapping.MappedStatement;
//import org.gosky.mapping.UserMapper;
//
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.Map;
//
//public class ParamsParser {
//
//
//    public Object pramsParser(String mapperName, String methodName, Object[] objects) throws InvocationTargetException, IllegalAccessException {
//        MappedStatement mappedStatement = new MappedStatement();
//        mappedStatement.setMapperName("userMapping");
//        mappedStatement.setClazz(UserMapper.class);
//
//        Map<String, String> methodSQL = mappedStatement.getMethodSQL();
//        Class<?> clazz = mappedStatement.getClazz();
//        Class<UserMapper> userMapperClass = UserMapper.class;
//        Method[] methods = userMapperClass.getMethods();
//        if (methods.length > 0) {
//            for (Method method : methods) {
//                if (method.getName().equals(methodName)) {
//                    return method.invoke(objects);
//                }
//            }
//        }
//        return null;
//    }
//
//    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {
//        new ParamsParser().pramsParser("", "selectAll", null);
//
//    }
//}
