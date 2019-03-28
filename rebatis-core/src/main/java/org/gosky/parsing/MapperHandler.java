package org.gosky.parsing;

import org.gosky.util.ClassHelper;
import org.gosky.util.ClassUtil;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class MapperHandler {


    public void parsingInterface(String packageName) {
        Set<Class<?>> classSet = ClassUtil.getClassSet(packageName);
        if (classSet.size() >= 0) {
            Set<Class<?>> mapperInterfaceSet = ClassHelper.getMapperInterfaceSet();
        }

    }

}