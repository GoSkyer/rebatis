/**
 *    Copyright 2009-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.reflection.wrapper;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;

import java.util.Collection;

/**
 * @author Clinton Begin
 */
public class CollectionWrapper implements ObjectWrapper {

  private final Collection<Object> object;

  public CollectionWrapper(MetaObject metaObject, Collection<Object> object) {
    this.object = object;
  }

  @Override
  public Object get(PropertyTokenizer prop) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isCollection() {
    return true;
  }

  @Override
  public void add(Object element) {
    object.add(element);
  }

//  @Override
//  public <E> void addAll(List<E> element) {
//    object.addAll(element);
//  }

}
