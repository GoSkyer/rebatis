package org.gosky.util;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class Platform {
  private static final Platform PLATFORM = findPlatform();

  public static Platform get() {
    return PLATFORM;
  }

  private static Platform findPlatform() {
    if ("RoboVM".equals(System.getProperty("java.vm.name"))) {
      return new Platform(false);
    }
    return new Platform(true);
  }

  private final boolean hasJava8Types;
  private final Constructor<Lookup> lookupConstructor;

  public Platform(boolean hasJava8Types) {
    this.hasJava8Types = hasJava8Types;

    Constructor<Lookup> lookupConstructor = null;
    if (hasJava8Types) {
      try {
        // Because the service interface might not be public, we need to use a MethodHandle lookup
        // that ignores the visibility of the declaringClass.
        lookupConstructor = Lookup.class.getDeclaredConstructor(Class.class, int.class);
        lookupConstructor.setAccessible(true);
      } catch (NoClassDefFoundError ignored) {
        // Android API 24 or 25 where Lookup doesn't exist. Calling default methods on non-public
        // interfaces will fail, but there's nothing we can do about it.
      } catch (NoSuchMethodException ignored) {
        // Assume JDK 14+ which contains a fix that allows a regular lookup to succeed.
        // See https://bugs.openjdk.java.net/browse/JDK-8209005.
      }
    }
    this.lookupConstructor = lookupConstructor;
  }

  public boolean isDefaultMethod(Method method) {
    return hasJava8Types && method.isDefault();
  }


  public Object invokeDefaultMethod(Method method, Class<?> declaringClass, Object object, Object... args)
      throws Throwable {
    Lookup lookup =
        lookupConstructor != null
            ? lookupConstructor.newInstance(declaringClass, -1 /* trusted */)
            : MethodHandles.lookup();
    return lookup.unreflectSpecial(method, declaringClass).bindTo(object).invokeWithArguments(args);
  }
}