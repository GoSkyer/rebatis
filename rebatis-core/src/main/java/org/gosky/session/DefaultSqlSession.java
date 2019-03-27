/*
 *    Copyright 2009-2014 the original author or authors.
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
package org.gosky.session;

import java.io.IOException;
import java.sql.Connection;

/**
 * 默认SqlSession实现
 *
 */
public class DefaultSqlSession implements SqlSession {

  /**
   * 是否自动提交
   */
  private boolean autoCommit;

  @Override
  public Connection getConnection() {
    return null;
  }

  @Override
  public void close() throws IOException {

  }
}
