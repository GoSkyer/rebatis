/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.gosky.basemapper;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 通用Mapper模板类，扩展通用Mapper时需要继承该类
 *
 * */
public abstract class MapperTemplate {


    /**
     * 获取返回值类型 - 实体类型
     *
     * @return
     */
    public Class<?> getEntityClass(Class<?> mapper) {

            Type[] types = mapper.getGenericInterfaces();
            for (Type type : types) {
                if (type instanceof ParameterizedType) {
                    ParameterizedType t = (ParameterizedType) type;
                        Class<?> returnType = (Class<?>) t.getActualTypeArguments()[0];
                        //获取该类型后，第一次对该类型进行初始化
                        EntityHelper.initEntityNameMap(returnType);
//                        entityClassMap.put(msId, returnType);
                        return returnType;
                }
            }
        throw new MapperException("无法获取 " + mapper + " 方法的泛型信息!");
    }

}
