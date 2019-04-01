package org.goskyer.rebatis.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Auther: guozhong
 * @Date: 2019-04-01 14:49
 * @Description:
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Entity {

}
