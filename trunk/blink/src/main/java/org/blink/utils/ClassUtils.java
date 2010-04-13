package org.blink.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.decorator.Decorator;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Named;
import javax.inject.Qualifier;
import javax.interceptor.Interceptor;

import org.apache.commons.lang.Validate;
import org.blink.core.AnyLiteral;
import org.blink.core.DefaultLiteral;
import org.blink.core.NewLiteral;
import org.blink.exceptions.BlinkException;

import com.google.common.collect.Sets;

/**
 * Utility classes with respect to the class operations.
 *
 * @author <a href="mailto:gurkanerdogdu@yahoo.com">Gurkan Erdogdu</a>
 * @since 1.0
 */
@SuppressWarnings("unchecked")
public final class ClassUtils {
    public static final Map<Class<?>, Object> PRIMITIVE_CLASS_DEFAULT_VALUES = new HashMap<Class<?>, Object>();;

    public static final Set<Class<?>> VALUE_TYPES = new HashSet<Class<?>>();

    public static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPERS_MAP = new HashMap<Class<?>, Class<?>>();

    public static final String WEBBEANS_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    static {
        PRIMITIVE_CLASS_DEFAULT_VALUES.put(Integer.class, Integer.MIN_VALUE);
        PRIMITIVE_CLASS_DEFAULT_VALUES.put(Float.class, Float.MIN_VALUE);
        PRIMITIVE_CLASS_DEFAULT_VALUES.put(Double.class, Double.MIN_VALUE);
        PRIMITIVE_CLASS_DEFAULT_VALUES
                .put(Character.class, Character.MIN_VALUE);
        PRIMITIVE_CLASS_DEFAULT_VALUES.put(String.class, new String());
        PRIMITIVE_CLASS_DEFAULT_VALUES.put(BigDecimal.class, BigDecimal.ZERO);
        PRIMITIVE_CLASS_DEFAULT_VALUES.put(BigInteger.class, BigInteger.ZERO);
        PRIMITIVE_CLASS_DEFAULT_VALUES.put(Long.class, Long.MIN_VALUE);
        PRIMITIVE_CLASS_DEFAULT_VALUES.put(Byte.class, Byte.MIN_VALUE);
        PRIMITIVE_CLASS_DEFAULT_VALUES.put(Short.class, Short.MIN_VALUE);
        PRIMITIVE_CLASS_DEFAULT_VALUES.put(Boolean.class, Boolean.FALSE);

        VALUE_TYPES.add(String.class);
        VALUE_TYPES.add(Date.class);
        VALUE_TYPES.add(Calendar.class);
        VALUE_TYPES.add(Class.class);
        VALUE_TYPES.add(List.class);
        VALUE_TYPES.add(Enum.class);
        VALUE_TYPES.add(java.sql.Date.class);
        VALUE_TYPES.add(Time.class);
        VALUE_TYPES.add(Timestamp.class);
        VALUE_TYPES.add(BigDecimal.class);
        VALUE_TYPES.add(BigInteger.class);

        PRIMITIVE_TO_WRAPPERS_MAP.put(Integer.TYPE, Integer.class);
        PRIMITIVE_TO_WRAPPERS_MAP.put(Float.TYPE, Float.class);
        PRIMITIVE_TO_WRAPPERS_MAP.put(Double.TYPE, Double.class);
        PRIMITIVE_TO_WRAPPERS_MAP.put(Character.TYPE, Character.class);
        PRIMITIVE_TO_WRAPPERS_MAP.put(Long.TYPE, Long.class);
        PRIMITIVE_TO_WRAPPERS_MAP.put(Byte.TYPE, Byte.class);
        PRIMITIVE_TO_WRAPPERS_MAP.put(Short.TYPE, Short.class);
        PRIMITIVE_TO_WRAPPERS_MAP.put(Boolean.TYPE, Boolean.class);
        PRIMITIVE_TO_WRAPPERS_MAP.put(Void.TYPE, Void.class);
    }

    /*
     * Private constructor
     */
    private ClassUtils() {
        throw new UnsupportedOperationException();
    }

    public static ClassLoader getCurrentClassLoader() {
        ClassLoader loader = AccessController
                .doPrivileged(new PrivilegedAction<ClassLoader>() {
                    public ClassLoader run() {
                        try {
                            return Thread.currentThread()
                                    .getContextClassLoader();

                        } catch (Exception e) {
                            return null;
                        }
                    }
                });

        if (loader == null) {
            loader = ClassUtils.class.getClassLoader();
        }

        return loader;
    }

    public static Class<?> getClass(String name) {
        Class<?> clazz = null;

        try {
            ClassLoader loader = ClassUtils.getCurrentClassLoader();
            clazz = loader.loadClass(name);

            return clazz;

        } catch (ClassNotFoundException e) {
            try {
                clazz = ClassUtils.class.getClassLoader().loadClass(name);

                return clazz;

            } catch (ClassNotFoundException e1) {
                try {
                    clazz = ClassLoader.getSystemClassLoader().loadClass(name);

                    return clazz;

                } catch (ClassNotFoundException e2) {
                    return null;
                }

            }
        }
    }

    /**
     * Check the parametrized type actual arguments equals with the class type
     * variables at the injection point.
     *
     * @param variables
     *            type variable
     * @param types
     *            type
     * @return true if equal
     */
    public static boolean checkEqual(TypeVariable<?>[] variables, Type[] types) {
        Validate.notNull(variables, "variables parameter can not be null");
        Validate.notNull(types, "types parameter can not be null");

        for (TypeVariable<?> variable : variables) {
            for (Type type : types) {
                if (type instanceof TypeVariable) {
                    TypeVariable<?> t = ((TypeVariable<?>) type);
                    if (t.getGenericDeclaration().equals(
                            variable.getGenericDeclaration())) {
                        if (t.getName().equals(variable.getName())) {
                            continue;
                        }
                        return false;
                    }
                    return false;
                }
            }
        }

        return true;

    }

    public static Class<?> getClassFromName(String name) {
        Class<?> clazz = null;

        try {
            ClassLoader loader = ClassUtils.getCurrentClassLoader();
            clazz = loader.loadClass(name);

            return clazz;

        } catch (ClassNotFoundException e) {
            try {
                clazz = ClassUtils.class.getClassLoader().loadClass(name);

                return clazz;

            } catch (ClassNotFoundException e1) {
                try {
                    clazz = ClassLoader.getSystemClassLoader().loadClass(name);

                    return clazz;

                } catch (ClassNotFoundException e2) {
                    return null;
                }

            }
        }

    }

    /**
     * Check final modifier.
     *
     * @param modifier
     *            modifier
     * @return true or false
     */
    public static boolean isFinal(Integer modifier) {
        Validate.notNull(modifier);

        return Modifier.isFinal(modifier);
    }

    public static boolean isTransient(Integer modifier) {
        Validate.notNull(modifier);

        return Modifier.isTransient(modifier);
    }

    /**
     * Check abstract modifier.
     *
     * @param modifier
     *            modifier
     * @return true or false
     */
    public static boolean isAbstract(Integer modifier) {
        Validate.notNull(modifier);

        return Modifier.isAbstract(modifier);
    }

    /**
     * Check interface modifier.
     *
     * @param modifier
     *            modifier
     * @return true or false
     */
    public static boolean isInterface(Integer modifier) {
        Validate.notNull(modifier);

        return Modifier.isInterface(modifier);
    }

    /**
     * Check for class that has a final method or not.
     *
     * @param clazz
     *            check methods of it
     * @return true or false
     */
    public static boolean hasFinalMethod(Class<?> clazz) {
        Validate.notNull(clazz);

        Method[] methods = clazz.getDeclaredMethods();
        for (Method m : methods) {
            if (isFinal(m.getModifiers())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check the class is inner or not
     *
     * @param modifier
     *            modifier
     * @return true or false
     */
    public static boolean isInnerClass(Class<?> clazz) {
        Validate.notNull(clazz);

        return clazz.isMemberClass();
    }

    /**
     * Check the modifier contains static keyword.
     *
     * @param modifier
     *            modifier
     * @return true or false
     */
    public static boolean isStatic(Integer modifier) {
        Validate.notNull(modifier);

        return Modifier.isStatic(modifier);
    }

    /**
     * Check the modifier contains static keyword.
     *
     * @param modifier
     *            modifier
     * @return true or false
     */
    public static boolean isPublic(Integer modifier) {
        Validate.notNull(modifier);

        return Modifier.isPublic(modifier);
    }

    /**
     * Check the modifier contains static keyword.
     *
     * @param modifier
     *            modifier
     * @return true or false
     */
    public static boolean isPrivate(Integer modifier) {
        Validate.notNull(modifier);

        return Modifier.isPrivate(modifier);
    }

    /**
     * Gets the Java Standart Class default value.
     *
     * @param <T>
     *            parametrized type
     * @param clazz
     *            class instance
     * @return default value of the class
     */
    public static <T> T defaultJavaValues(Class<T> clazz) {
        Validate.notNull(clazz);

        Set<Class<?>> keySet = PRIMITIVE_CLASS_DEFAULT_VALUES.keySet();
        Iterator<Class<?>> it = keySet.iterator();
        while (it.hasNext()) {
            Class<?> obj = it.next();
            if (clazz.equals(obj)) {
                return (T) PRIMITIVE_CLASS_DEFAULT_VALUES.get(obj);
            }
        }

        return null;

    }

    public static Class<?> getPrimitiveWrapper(Class<?> clazz) {
        Validate.notNull(clazz);

        return PRIMITIVE_TO_WRAPPERS_MAP.get(clazz);

    }

    public static Class<?> getWrapperPrimitive(Class<?> clazz) {
        Validate.notNull(clazz);

        Set<Class<?>> keySet = PRIMITIVE_TO_WRAPPERS_MAP.keySet();

        for (Class<?> key : keySet) {
            if (PRIMITIVE_TO_WRAPPERS_MAP.get(key).equals(clazz)) {
                return key;
            }
        }

        return null;
    }

    /**
     * Gets the class of the given type arguments.
     * <p>
     * If the given type {@link Type} parameters is an instance of the
     * {@link ParameterizedType}, it returns the raw type otherwise it return
     * the casted {@link Class} of the type argument.
     * </p>
     *
     * @param type
     *            class or parametrized type
     * @return Class for the given type
     */
    public static Class<?> getClass(Type type) {
        return getClazz(type);
    }

    /**
     * Gets the declared methods of the given class.
     *
     * @param clazz
     *            class instance
     * @return the declared methods
     */
    public static Method[] getDeclaredMethods(Class<?> clazz) {
        Validate.notNull(clazz);
        return clazz.getDeclaredMethods();
    }

    /**
     * Check that method has any formal arguments.
     *
     * @param method
     *            method instance
     * @return true or false
     */
    public static boolean isMethodHasParameter(Method method) {
        Validate.notNull(method);

        Class<?>[] types = method.getParameterTypes();
        if (types.length != 0) {
            return true;
        }

        return false;
    }

    /**
     * Gets the return type of the method.
     *
     * @param method
     *            method instance
     * @return return type
     */
    public static Class<?> getReturnType(Method method) {
        Validate.notNull(method);
        return method.getReturnType();
    }

    /**
     * Check method throws checked exception or not.
     *
     * @param method
     *            method instance
     * @return true or false
     */
    public static boolean isMethodHasCheckedException(Method method) {
        Validate.notNull(method);

        Class<?>[] et = method.getExceptionTypes();

        if (et.length > 0) {
            for (Class<?> type : et) {
                if (Error.class.isAssignableFrom(type)
                        || RuntimeException.class.isAssignableFrom(type)) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Check method throws Exception or not.
     *
     * @param method
     *            method instance
     * @return trur or false
     */
    public static boolean isMethodHasException(Method method) {
        Validate.notNull(method);

        Class<?>[] et = method.getExceptionTypes();

        if (et.length == 1) {
            if (et[0].equals(Exception.class))
                return true;
        }

        return false;
    }

    /**
     * Call method on the instance with given arguments.
     *
     * @param method
     *            method instance
     * @param instance
     *            object instance
     * @param args
     *            arguments
     * @return the method result
     */
    public static Object callInstanceMethod(Method method, Object instance,
            Object[] args) {
        Validate.notNull(method);
        Validate.notNull(instance, "instance parameter can not be null");

        try {
            if (args == null) {
                args = new Object[] {};
            }
            return method.invoke(instance, args);

        } catch (Exception e) {
            throw new BlinkException(
                    "Exception occurs in the method call with method : "
                            + method.getName() + " in class : "
                            + instance.getClass().getName());
        }

    }

    public static List<Class<?>> getSuperClasses(Class<?> clazz,
            List<Class<?>> list) {
        Validate.notNull(clazz);

        Class<?> sc = clazz.getSuperclass();
        if (sc != null) {
            list.add(sc);
            getSuperClasses(sc, list);
        }

        return list;

    }

    public static Class<?>[] getMethodParameterTypes(Method method) {
        Validate.notNull(method);
        return method.getParameterTypes();
    }

    public static List<String> getObjectMethodNames() {
        List<String> list = new ArrayList<String>();
        Class<?> clazz = Object.class;

        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            list.add(method.getName());
        }

        return list;
    }

    public static boolean isObjectMethod(String methodName) {
        Validate.notNull(methodName, "methodName parameter can not be null");
        return getObjectMethodNames().contains(methodName);
    }

    public static boolean isMoreThanOneMethodWithName(String methodName,
            Class<?> clazz) {
        Validate.notNull(methodName, "methodName parameter can not be null");
        Validate.notNull(clazz);

        Method[] methods = clazz.getDeclaredMethods();
        int i = 0;
        for (Method m : methods) {
            if (m.getName().equals(methodName)) {
                i++;
            }
        }

        if (i > 1) {
            return true;
        }

        return false;

    }

    public static <T> Constructor<T> isContaintNoArgConstructor(Class<T> clazz) {
        Validate.notNull(clazz);
        try {
            return clazz.getDeclaredConstructor(new Class<?>[] {});

        } catch (Exception e) {
            return null;
        }

    }

    /**
     * Check the modifiers contains the public keyword.
     *
     * @param modifs
     *            modifiers
     * @return true or false
     */
    public static boolean isPublic(int modifs) {
        return Modifier.isPublic(modifs);
    }

    /**
     * Gets java package if exist.
     *
     * @param packageName
     *            package name
     * @return the package with given name
     */
    public Package getPackage(String packageName) {
        Validate.notNull(packageName, "packageName parameter can not be null");

        return Package.getPackage(packageName);
    }

    public static Set<Annotation> getMetaAnnotations(Set<Annotation> annotations,
            Class<? extends Annotation> metaAnnotationType) {
        Set<Annotation> subset = Sets.newHashSet();

        for (Annotation annotation : annotations) {
            if (annotation.annotationType().isAnnotationPresent(metaAnnotationType)) {
                subset.add(annotation);
            }
        }

        return subset;
    }

    /**
     * Returns true if type is an instance of <code>ParameterizedType</code>
     * else otherwise.
     *
     * @param type
     *            type of the artifact
     * @return true if type is an instance of <code>ParameterizedType</code>
     */
    public static boolean isParametrizedType(Type type) {
        Validate.notNull(type, "type parameter can not be null");
        if (type instanceof ParameterizedType) {
            return true;
        }

        return false;
    }

    /**
     * Returns true if type is an instance of <code>WildcardType</code> else
     * otherwise.
     *
     * @param type
     *            type of the artifact
     * @return true if type is an instance of <code>WildcardType</code>
     */
    public static boolean isWildCardType(Type type) {
        Validate.notNull(type, "type parameter can not be null");

        if (type instanceof WildcardType) {
            return true;
        }

        return false;
    }

    public static boolean isUnboundedTypeVariable(Type type) {
        Validate.notNull(type, "type parameter can not be null");

        if (type instanceof TypeVariable) {
            TypeVariable wc = (TypeVariable) type;
            Type[] upper = wc.getBounds();

            if (upper.length > 1) {
                return false;
            }

            Type arg = upper[0];
            if (!(arg instanceof Class)) {
                return false;
            }

            Class<?> clazz = (Class<?>) arg;
            if (!clazz.equals(Object.class)) {
                return false;
            }
        } else {
            return false;
        }

        return true;
    }

    /**
     * Returns true if type is an instance of <code>TypeVariable</code> else
     * otherwise.
     *
     * @param type
     *            type of the artifact
     * @return true if type is an instance of <code>TypeVariable</code>
     */
    public static boolean isTypeVariable(Type type) {
        Validate.notNull(type, "type parameter can not be null");

        if (type instanceof TypeVariable) {
            return true;
        }

        return false;

    }

    /**
     * Returna true if the class is not abstract and interface.
     *
     * @param clazz
     *            class type
     * @return true if the class is not abstract and interface
     */
    public static boolean isConcrete(Class<?> clazz) {
        Validate.notNull(clazz);

        Integer modifier = clazz.getModifiers();

        if (!isAbstract(modifier) && !isInterface(modifier)) {
            return true;
        }

        return false;
    }

    /**
     * Returns class constructor array.
     *
     * @param <T>
     *            class type arfument
     * @param clazz
     *            class that is searched for constructor.
     * @return class constructor array
     */
    public static <T> Constructor<T>[] getConstructors(Class<T> clazz) {
        Validate.notNull(clazz);

        return (Constructor<T>[]) clazz.getDeclaredConstructors();
    }

    /**
     * Returns true if class has a default constructor.
     *
     * @param <T>
     *            type argument of class
     * @param clazz
     *            class type
     * @return true if class has a default constructor.
     */
    public static <T> boolean hasDefaultConstructor(Class<T> clazz) {
        Validate.notNull(clazz);

        try {
            clazz.getDeclaredConstructor(new Class<?>[] {});

        } catch (SecurityException e) {
            throw new BlinkException(e);
        } catch (NoSuchMethodException e) {
            return false;
        }

        return true;
    }

    public static boolean isAssignable(Type beanType, Type requiredType) {
        Validate.notNull(beanType, "beanType parameter can not be null");
        Validate
                .notNull(requiredType, "requiredType parameter can not be null");

        if (beanType instanceof ParameterizedType
                && requiredType instanceof ParameterizedType) {
            return isAssignableForParametrized((ParameterizedType) beanType,
                    (ParameterizedType) requiredType);
        } else if (beanType instanceof Class && requiredType instanceof Class) {
            Class<?> clzBeanType = (Class<?>) beanType;
            Class<?> clzReqType = (Class<?>) requiredType;

            if (clzBeanType.isPrimitive()) {
                clzBeanType = getPrimitiveWrapper(clzBeanType);
            }

            if (clzReqType.isPrimitive()) {
                clzReqType = getPrimitiveWrapper(clzReqType);
            }

            return clzReqType.equals(clzBeanType);
        } else if (beanType instanceof ParameterizedType
                && requiredType instanceof Class) {
            boolean ok = true;
            ParameterizedType ptBean = (ParameterizedType) beanType;
            Class<?> clazzBeanType = (Class<?>) ptBean.getRawType();
            Class<?> clazzReqType = (Class<?>) requiredType;
            if (isAssignable(clazzReqType, clazzBeanType)) {
                Type[] beanTypeArgs = ptBean.getActualTypeArguments();
                for (Type actual : beanTypeArgs) {
                    if (!ClassUtils.isUnboundedTypeVariable(actual)) {
                        if (actual instanceof Class) {
                            Class<?> clazz = (Class<?>) actual;
                            if (clazz.equals(Object.class)) {
                                continue;
                            }
                            ok = false;
                            break;
                        }
                        ok = false;
                        break;
                    }
                }
            } else {
                ok = false;
            }

            return ok;
        } else if (beanType instanceof Class
                && requiredType instanceof ParameterizedType) {
            Class<?> clazzBeanType = (Class<?>) beanType;
            ParameterizedType ptReq = (ParameterizedType) requiredType;
            Class<?> clazzReqType = (Class<?>) ptReq.getRawType();

            if (isAssignable(clazzReqType, clazzBeanType)) {
                return true;
            }

            return false;
        } else {
            return false;
        }
    }

    public static boolean checkEventTypeAssignability(Type eventType,
            Type observerType) {
        if (isTypeVariable(observerType)) {
            Class<?> eventClass = getClass(eventType);

            TypeVariable<?> tvBeanTypeArg = (TypeVariable<?>) observerType;
            Type tvBound = tvBeanTypeArg.getBounds()[0];

            if (tvBound instanceof Class) {
                Class<?> clazzTvBound = (Class<?>) tvBound;

                if (clazzTvBound.isAssignableFrom(eventClass)) {
                    return true;
                }
            }
        } else if (observerType instanceof ParameterizedType
                && eventType instanceof ParameterizedType) {
            return isAssignableForParametrized((ParameterizedType) eventType,
                    (ParameterizedType) observerType);
        } else if (observerType instanceof Class
                && eventType instanceof ParameterizedType) {
            Class<?> clazzBeanType = (Class<?>) observerType;
            ParameterizedType ptEvent = (ParameterizedType) eventType;
            Class<?> eventClazz = (Class<?>) ptEvent.getRawType();

            if (isAssignable(clazzBeanType, eventClazz)) {
                return true;
            }

            return false;
        } else if (observerType instanceof Class && eventType instanceof Class) {
            return isAssignable((Class<?>) observerType, (Class<?>) eventType);
        }

        return false;
    }

    /**
     * Returns true if rhs is assignable type to the lhs, false otherwise.
     *
     * @param lhs
     *            left hand side class
     * @param rhs
     *            right hand side class
     * @return true if rhs is assignable to lhs
     */
    public static boolean isAssignable(Class<?> lhs, Class<?> rhs) {
        Validate.notNull(lhs, "lhs parameter can not be null");
        Validate.notNull(rhs, "rhs parameter can not be null");

        if (lhs.isPrimitive()) {
            lhs = getPrimitiveWrapper(lhs);
        }

        if (rhs.isPrimitive()) {
            rhs = getPrimitiveWrapper(rhs);
        }

        if (lhs.isAssignableFrom(rhs)) {
            return true;
        }

        return false;
    }

    /**
     * Returns true if given bean's api type is injectable to injection point
     * required type.
     *
     * @param beanType
     *            bean parametrized api type
     * @param requiredType
     *            injection point parametrized api type
     * @return if injection is possible false otherwise
     */
    public static boolean isAssignableForParametrized(
            ParameterizedType beanType, ParameterizedType requiredType) {
        Class<?> beanRawType = (Class<?>) beanType.getRawType();
        Class<?> requiredRawType = (Class<?>) requiredType.getRawType();

        if (ClassUtils.isAssignable(requiredRawType, beanRawType)) {
            // Bean api type actual type arguments
            Type[] beanTypeArgs = beanType.getActualTypeArguments();

            // Injection point type actual arguments
            Type[] requiredTypeArgs = requiredType.getActualTypeArguments();

            if (beanTypeArgs.length != requiredTypeArgs.length) {
                return false;
            }
            return isAssignableForParametrizedCheckArguments(beanTypeArgs,
                        requiredTypeArgs);
        }

        return false;
    }

    private static boolean isAssignableForParametrizedCheckArguments(
            Type[] beanTypeArgs, Type[] requiredTypeArgs) {
        Type requiredTypeArg = null;
        Type beanTypeArg = null;
        for (int i = 0; i < requiredTypeArgs.length; i++) {
            requiredTypeArg = requiredTypeArgs[i];
            beanTypeArg = beanTypeArgs[i];

            // Required type is parametrized
            if (ClassUtils.isParametrizedType(requiredTypeArg)
                    && ClassUtils.isParametrizedType(beanTypeArg)) {
                return checkBeanAndRequiredTypeisParametrized(beanTypeArg,
                        requiredTypeArg);
            }
            // Required type is wildcard
            else if (ClassUtils.isWildCardType(requiredTypeArg)) {
                return checkRequiredTypeisWildCard(beanTypeArg, requiredTypeArg);
            }
            // Required type is actual type
            else if (requiredTypeArg instanceof Class
                    && ClassUtils.isTypeVariable(beanTypeArg)) {
                return checkRequiredTypeIsClassAndBeanTypeIsVariable(
                        beanTypeArg, requiredTypeArg);
            }
            // Required type is Type variable
            else if (ClassUtils.isTypeVariable(requiredTypeArg)
                    && ClassUtils.isTypeVariable(beanTypeArg)) {
                return checkBeanTypeAndRequiredIsTypeVariable(beanTypeArg,
                        requiredTypeArg);
            } else if ((beanTypeArg instanceof Class)
                    && (requiredTypeArg instanceof Class)) {
                if (isAssignable(beanTypeArg, requiredTypeArg)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean checkBeanAndRequiredTypeisParametrized(
            Type beanTypeArg, Type requiredTypeArg) {
        ParameterizedType ptRequiredTypeArg = (ParameterizedType) requiredTypeArg;
        ParameterizedType ptBeanTypeArg = (ParameterizedType) beanTypeArg;

        // Equal raw types
        if (ptRequiredTypeArg.getRawType().equals(ptBeanTypeArg.getRawType())) {
            // Check arguments
            Type[] actualArgsRequiredType = ptRequiredTypeArg
                    .getActualTypeArguments();
            Type[] actualArgsBeanType = ptRequiredTypeArg
                    .getActualTypeArguments();

            if (actualArgsRequiredType.length > 0
                    && actualArgsBeanType.length == actualArgsRequiredType.length) {
                return isAssignableForParametrizedCheckArguments(
                        actualArgsBeanType, actualArgsRequiredType);
            }
            return true;
        }

        return false;
    }

    public static boolean checkRequiredTypeisWildCard(Type beanTypeArg,
            Type requiredTypeArg) {
        WildcardType wctRequiredTypeArg = (WildcardType) requiredTypeArg;
        Type upperBoundRequiredTypeArg = wctRequiredTypeArg.getUpperBounds()[0];
        Type[] lowerBoundRequiredTypeArgs = wctRequiredTypeArg.getLowerBounds();

        if (beanTypeArg instanceof Class) {
            Class<?> clazzBeanTypeArg = (Class<?>) beanTypeArg;
            if (upperBoundRequiredTypeArg instanceof Class) {
                Class<?> clazzUpperBoundTypeArg = (Class<?>) upperBoundRequiredTypeArg;
                if (clazzUpperBoundTypeArg.isAssignableFrom(clazzBeanTypeArg)) {
                    if (lowerBoundRequiredTypeArgs.length > 0
                            && lowerBoundRequiredTypeArgs[0] instanceof Class) {
                        Class<?> clazzLowerBoundTypeArg = (Class<?>) lowerBoundRequiredTypeArgs[0];
                        if (clazzBeanTypeArg
                                .isAssignableFrom(clazzLowerBoundTypeArg)) {
                            return true;
                        }
                    } else {
                        return true;
                    }

                }
            }
        } else if (ClassUtils.isTypeVariable(beanTypeArg)) {
            TypeVariable<?> tvBeanTypeArg = (TypeVariable<?>) beanTypeArg;
            Type tvBound = tvBeanTypeArg.getBounds()[0];

            if (tvBound instanceof Class) {
                Class<?> clazzTvBound = (Class<?>) tvBound;

                if (upperBoundRequiredTypeArg instanceof Class) {
                    Class<?> clazzUpperBoundTypeArg = (Class<?>) upperBoundRequiredTypeArg;
                    if (clazzUpperBoundTypeArg.isAssignableFrom(clazzTvBound)) {
                        if (lowerBoundRequiredTypeArgs.length > 0
                                && lowerBoundRequiredTypeArgs[0] instanceof Class) {
                            Class<?> clazzLowerBoundTypeArg = (Class<?>) lowerBoundRequiredTypeArgs[0];
                            if (clazzTvBound
                                    .isAssignableFrom(clazzLowerBoundTypeArg)) {
                                return true;
                            }
                        } else {
                            return true;
                        }

                    }
                }
            }
        }

        return false;
    }

    public static boolean checkRequiredTypeIsClassAndBeanTypeIsVariable(
            Type beanTypeArg, Type requiredTypeArg) {
        Class<?> clazzRequiredType = (Class<?>) requiredTypeArg;

        TypeVariable<?> tvBeanTypeArg = (TypeVariable<?>) beanTypeArg;
        Type tvBound = tvBeanTypeArg.getBounds()[0];

        if (tvBound instanceof Class) {
            Class<?> clazzTvBound = (Class<?>) tvBound;

            if (clazzRequiredType.isAssignableFrom(clazzTvBound)) {
                return true;
            }
        }

        return false;
    }

    public static boolean checkBeanTypeAndRequiredIsTypeVariable(
            Type beanTypeArg, Type requiredTypeArg) {
        TypeVariable<?> tvBeanTypeArg = (TypeVariable<?>) beanTypeArg;
        Type tvBeanBound = tvBeanTypeArg.getBounds()[0];

        TypeVariable<?> tvRequiredTypeArg = (TypeVariable<?>) requiredTypeArg;
        Type tvRequiredBound = tvRequiredTypeArg.getBounds()[0];

        if (tvBeanBound instanceof Class && tvRequiredBound instanceof Class) {
            Class<?> clazzTvBeanBound = (Class<?>) tvBeanBound;
            Class<?> clazzTvRequiredBound = (Class<?>) tvRequiredBound;

            if (clazzTvRequiredBound.isAssignableFrom(clazzTvBeanBound)) {
                return true;
            }
        }

        return false;
    }

    public static boolean classHasFieldWithName(Class<?> clazz, String fieldName) {
        Validate.notNull(clazz);
        Validate.notNull(fieldName, "fieldName parameter can not be null");
        try {

            clazz.getDeclaredField(fieldName);

        } catch (SecurityException e) {
            // we must throw here!
            throw new BlinkException(e);
        } catch (NoSuchFieldException e2) {
            return false;
        }

        return true;
    }

    public static boolean classHasMoreThanOneFieldWithName(Class<?> clazz,
            String fieldName) {
        Validate.notNull(clazz);
        Validate.notNull(fieldName, "fieldName parameter can not be null");

        Field[] fields = clazz.getDeclaredFields();
        boolean ok = false;
        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                if (ok) {
                    return true;
                }
                ok = true;
            }
        }

        return false;
    }

    public static Field getFieldWithName(Class<?> clazz, String fieldName) {
        Validate.notNull(clazz);
        Validate.notNull(fieldName, "fieldName parameter can not be null");
        try {

            return clazz.getDeclaredField(fieldName);

        } catch (SecurityException e) {
            // we must throw here!
            throw new BlinkException(e);
        } catch (NoSuchFieldException e2) {
            return null;
        }

    }

    /**
     * @param clazz
     *            implementation class
     * @param methodName
     *            name of the method that is searched
     * @param parameterTypes
     *            parameter types of the method(it can be subtype of the actual
     *            type arguments of the method)
     * @return the list of method that satisfies the condition
     */
    public static List<Method> getClassMethodsWithTypes(Class<?> clazz,
            String methodName, List<Class<?>> parameterTypes) {
        Validate.notNull(clazz);
        Validate.notNull(methodName, "methodName parameter can not be null");
        Validate.notNull(parameterTypes,
                "parameterTypes parameter can not be null");

        List<Method> methodList = new ArrayList<Method>();

        Method[] methods = clazz.getDeclaredMethods();

        int j = 0;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class<?>[] defineTypes = method.getParameterTypes();

                if (defineTypes.length != parameterTypes.size()) {
                    continue;
                }

                boolean ok = true;

                if (parameterTypes.size() > 0) {
                    ok = false;
                }

                if (!ok) {
                    for (Class<?> defineType : defineTypes) {
                        if (defineType.isAssignableFrom(parameterTypes.get(j))) {
                            ok = true;
                        } else {
                            ok = false;
                        }

                        j++;
                    }
                }

                if (ok) {
                    methodList.add(method);
                }
            }
        }

        return methodList;
    }

    public static Method getClassMethodWithTypes(Class<?> clazz,
            String methodName, List<Class<?>> parameterTypes) {
        Validate.notNull(clazz);
        Validate.notNull(methodName, "methodName parameter can not be null");
        Validate.notNull(parameterTypes,
                "parameterTypes parameter can not be null");

        Method[] methods = clazz.getDeclaredMethods();

        int j = 0;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                if (parameterTypes != null && parameterTypes.size() > 0) {
                    Class<?>[] defineTypes = method.getParameterTypes();

                    if (defineTypes.length != parameterTypes.size()) {
                        continue;
                    }

                    boolean ok = false;

                    for (Class<?> defineType : defineTypes) {
                        if (defineType.equals(parameterTypes.get(j))) {
                            ok = true;
                        } else {
                            ok = false;
                        }
                    }

                    if (ok) {
                        return method;
                    }
                } else {
                    return method;
                }
            }
        }

        return null;
    }

    public static boolean hasMethodWithName(Class<?> clazz, String methodName) {
        Validate.notNull(clazz);
        Validate.notNull(methodName, "methodName parameter can not be null");

        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isPrimitive(Class<?> clazz) {
        Validate.notNull(clazz);

        return clazz.isPrimitive();
    }

    public static boolean isPrimitiveWrapper(Class<?> clazz) {
        Validate.notNull(clazz);

        return PRIMITIVE_TO_WRAPPERS_MAP.containsValue(clazz);

    }

    public static boolean isArray(Class<?> clazz) {
        Validate.notNull(clazz);

        return clazz.isArray();
    }

    public static boolean isEnum(Class<?> clazz) {
        return clazz.isEnum();
    }

    public static boolean isInValueTypes(Class<?> clazz) {
        boolean result = VALUE_TYPES.contains(clazz);

        if (!result) {
            result = clazz.isPrimitive();
        }

        if (!result) {
            if (Enum.class.isAssignableFrom(clazz)) {
                return true;
            }
        }

        return result;
    }

    /**
     * Gets the primitive/wrapper value of the parsed {@link String} parameter.
     *
     * @param type
     *            primitive or wrapper of the primitive type
     * @param value
     *            value of the type
     * @return the parse of the given {@link String} value into the
     *         corresponding value, if any exception occurs, returns null as the
     *         value.
     */
    public static Object isValueOkForPrimitiveOrWrapper(Class<?> type,
            String value) {
        if (type.equals(Integer.TYPE) || type.equals(Integer.class)) {
            return Integer.valueOf(value);
        }

        if (type.equals(Float.TYPE) || type.equals(Float.class)) {
            return Float.valueOf(value);
        }

        if (type.equals(Double.TYPE) || type.equals(Double.class)) {
            return Double.valueOf(value);
        }

        if (type.equals(Character.TYPE) || type.equals(Character.class)) {
            return value.toCharArray()[0];
        }

        if (type.equals(Long.TYPE) || type.equals(Long.class)) {
            return Long.valueOf(value);
        }

        if (type.equals(Byte.TYPE) || type.equals(Byte.class)) {
            return Byte.valueOf(value);
        }

        if (type.equals(Short.TYPE) || type.equals(Short.class)) {
            return Short.valueOf(value);
        }

        if (type.equals(Boolean.TYPE) || type.equals(Boolean.class)) {
            return Boolean.valueOf(value);
        }

        return null;
    }

    public static Enum isValueOkForEnum(Class clazz, String value) {
        Validate.notNull(clazz);
        Validate.notNull(value, "value parameter can not be null");

        return Enum.valueOf(clazz, value);
    }

    public static Date isValueOkForDate(String value) throws ParseException {
        try {
            Validate.notNull(value, "value parameter can not be null");
            return DateFormat.getDateTimeInstance().parse(value);

        } catch (ParseException e) {
            // Check for simple date format
            SimpleDateFormat format = new SimpleDateFormat(WEBBEANS_DATE_FORMAT);

            return format.parse(value);
        }
    }

    public static Calendar isValueOkForCalendar(String value)
            throws ParseException {
        Calendar calendar = null;

        Validate.notNull(value, "value parameter can not be null");
        Date date = isValueOkForDate(value);

        if (date == null) {
            return null;
        }
        calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar;
    }

    public static Object isValueOkForBigDecimalOrInteger(Class<?> type,
            String value) {
        Validate.notNull(type);
        Validate.notNull(value);

        if (type.equals(BigInteger.class)) {
            return new BigInteger(value);
        } else if (type.equals(BigDecimal.class)) {
            return new BigDecimal(value);
        } else {
            return new BlinkException(new IllegalArgumentException(
                    "Argument is not valid"));
        }
    }

    public static boolean isDefinitionConstainsTypeVariables(Class<?> clazz) {
        Validate.notNull(clazz);

        return (clazz.getTypeParameters().length > 0) ? true : false;
    }

    public static TypeVariable<?>[] getTypeVariables(Class<?> clazz) {
        Validate.notNull(clazz);

        return clazz.getTypeParameters();
    }

    public static Type[] getActualTypeArguements(Class<?> clazz) {
        Validate.notNull(clazz);

        if (clazz.getGenericSuperclass() instanceof ParameterizedType) {
            return ((ParameterizedType) clazz.getGenericSuperclass())
                    .getActualTypeArguments();

        }
        return new Type[0];
    }

    public static Type[] getActualTypeArguements(Type type) {
        Validate.notNull(type, "type parameter can not be null");

        if (type instanceof ParameterizedType) {
            return ((ParameterizedType) type).getActualTypeArguments();

        }
        return new Type[0];
    }

    public static Class<?> getFirstRawType(Type type) {
        Validate.notNull(type, "type argument can not be null");

        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            return (Class<?>) pt.getRawType();
        }

        return (Class<?>) type;
    }

    public static Set<Type> setTypeHierarchy(Set<Type> set, Type clazz) {
        Class<?> raw = getClazz(clazz);

        if (raw == null) {
            return null;
        }

        set.add(clazz);

        Type sc = raw.getGenericSuperclass();

        if (sc != null) {
            setTypeHierarchy(set, sc);
        }

        Type[] interfaces = raw.getGenericInterfaces();
        for (Type cl : interfaces) {
            setTypeHierarchy(set, cl);
        }

        return set;
    }

    /**
     * Return raw class type for given type.
     *
     * @param type
     *            base type instance
     * @return class type for given type
     */
    public static Class<?> getClazz(Type type) {
        Class<?> raw = null;

        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            raw = (Class<?>) pt.getRawType();
        } else if (type instanceof Class) {
            raw = (Class<?>) type;
        } else if (type instanceof GenericArrayType) {
            GenericArrayType arrayType = (GenericArrayType) type;
            raw = getClazz(arrayType.getGenericComponentType());
        }

        return raw;
    }

    // For Ejb API Type
    public static Set<Type> setClassTypeHierarchy(Set<Type> set, Class<?> clazz) {
        Validate.notNull(clazz);

        set.add(clazz);

        Class<?> sc = clazz.getSuperclass();

        if (sc != null) {
            setTypeHierarchy(set, sc);
        }

        return set;
    }

    public static Set<Type> setInterfaceTypeHierarchy(Set<Type> set,
            Class<?> clazz) {
        Validate.notNull(clazz);

        Class<?>[] interfaces = clazz.getInterfaces();

        for (Class<?> cl : interfaces) {
            set.add(cl);

            setTypeHierarchy(set, cl);
        }

        return set;
    }

    public static Type[] getGenericSuperClassTypeArguments(Class<?> clazz) {
        Validate.notNull(clazz);
        Type type = clazz.getGenericSuperclass();

        if (type != null) {
            if (type instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) type;

                // if (checkParametrizedType(pt))
                // {
                return pt.getActualTypeArguments();
                // }
            }
        }

        return new Type[0];

    }

    /**
     * Return true if it does not contain type variable for wildcard type false
     * otherwise.
     *
     * @param pType
     *            parameterized type
     * @return true if it does not contain type variable for wildcard type
     */
    public static boolean checkParametrizedType(ParameterizedType pType) {
        Validate.notNull(pType, "pType argument can not be null");

        Type[] types = pType.getActualTypeArguments();

        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                return checkParametrizedType((ParameterizedType) type);
            } else if ((type instanceof TypeVariable)
                    || (type instanceof WildcardType)) {
                return false;
            }
        }

        return true;
    }

    public static boolean isFirstParametricTypeArgGeneric(ParameterizedType type) {
        Validate.notNull(type, "type parameter can not be null");

        Type[] args = type.getActualTypeArguments();

        if (args.length == 0) {
            return false;
        }

        Type arg = args[0];

        if ((arg instanceof TypeVariable) || (arg instanceof WildcardType)) {
            return true;
        }

        return false;
    }

    public static List<Type[]> getGenericSuperInterfacesTypeArguments(
            Class<?> clazz) {
        Validate.notNull(clazz);
        List<Type[]> list = new ArrayList<Type[]>();

        Type[] types = clazz.getGenericInterfaces();
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) type;

                // if (checkParametrizedType(pt))
                // {
                list.add(pt.getActualTypeArguments());
                // }
            }
        }

        return list;
    }

    public static Field getFieldWithAnnotation(Class<?> clazz,
            Class<? extends Annotation> annotation) {
        Validate.notNull(clazz);
        Validate.notNull(annotation, "annotation parameter can not be null");

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (AnnotationUtil
                    .hasAnnotation(field.getAnnotations(), annotation)) {
                return field;
            }

        }

        return null;

    }

    public static Field[] getFieldsWithType(Class<?> clazz, Type type) {
        Validate.notNull(clazz);
        Validate.notNull(type, "type parameter can not be null");

        List<Field> fieldsWithType = new ArrayList<Field>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().equals(type)) {
                fieldsWithType.add(field);
            }
        }

        return fieldsWithType.toArray(new Field[0]);

    }

    public static boolean checkForTypeArguments(Class<?> src,
            Type[] typeArguments, Class<?> target) {
        Validate.notNull(src, "src parameter can not be null");
        Validate.notNull(typeArguments,
                "typeArguments parameter can not be null");
        Validate.notNull(target, "target parameter can not be null");

        Type[] types = getGenericSuperClassTypeArguments(target);

        boolean found = false;

        if (Arrays.equals(typeArguments, types)) {
            return true;
        }
        Class<?> superClazz = target.getSuperclass();
        if (superClazz != null) {
            found = checkForTypeArguments(src, typeArguments, superClazz);
        }

        if (!found) {
            List<Type[]> list = getGenericSuperInterfacesTypeArguments(target);
            if (!list.isEmpty()) {
                Iterator<Type[]> it = list.iterator();
                while (it.hasNext()) {
                    types = it.next();
                    if (Arrays.equals(typeArguments, types)) {
                        found = true;
                        break;
                    }
                }

            }
        }

        if (!found) {
            Class<?>[] superInterfaces = target.getInterfaces();
            for (Class<?> inter : superInterfaces) {
                found = checkForTypeArguments(src, typeArguments, inter);
                if (found) {
                    break;
                }
            }
        }

        return found;
    }

    public static void setField(Object instance, Field field, Object value) {
        Validate.notNull(instance);
        Validate.notNull(field);

        if (!field.isAccessible()) {
            field.setAccessible(true);
        }

        try {
            field.set(instance, value);
        } catch (Exception e) {
            throw new BlinkException(e);
        }
    }

    public static Throwable getRootException(Throwable throwable) {
        if (throwable.getCause() == null) {
            return throwable;
        }
        return getRootException(throwable.getCause());
    }

    /**
     * Returns injection point raw type.
     *
     * @param injectionPoint
     *            injection point definition
     * @return injection point raw type
     */
    public static Class<?> getRawTypeForInjectionPoint(
            InjectionPoint injectionPoint) {
        Class<?> rawType = null;
        Type type = injectionPoint.getType();

        if (type instanceof Class) {
            rawType = (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            rawType = (Class<?>) pt.getRawType();
        }

        return rawType;
    }

    public static boolean isOverriden(Method subClassMethod,
            Method superClassMethod) {
        if (subClassMethod.getName().equals(superClassMethod.getName())
                && Arrays.equals(subClassMethod.getParameterTypes(),
                        superClassMethod.getParameterTypes())) {
            int modifiers = superClassMethod.getModifiers();
            if (Modifier.isPrivate(modifiers)) {
                return false;
            }

            if (!Modifier.isProtected(modifiers)
                    && !Modifier.isPublic(modifiers)) {
                Class<?> superClass = superClassMethod.getDeclaringClass();
                Class<?> subClass = subClassMethod.getDeclaringClass();

                // Same package
                if (!subClass.getPackage().getName().equals(
                        superClass.getPackage().getName())) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    public static Method getDeclaredMethod(Class<?> clazz, String methodName,
            Class<?>[] parameters) {
        try {
            return clazz.getDeclaredMethod(methodName, parameters);

        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static Constructor<?> getConstructor(Class<?> clazz,
            Class<?>[] parameterTypes) {
        try {
            return clazz.getConstructor(parameterTypes);

        } catch (NoSuchMethodException e) {
            return null;
        }
    }
    public static boolean isDecorator(Class clazz) {
        return clazz.isAnnotationPresent(Decorator.class);
    }

    public static boolean isInterceptor(Class<?> clazz) {
        return clazz.isAnnotationPresent(Interceptor.class);
    }

    public static Set<Annotation> getQualifiers(Set<Annotation> annotations) {
        Set<Annotation> qualifiers = Sets.newHashSet();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType()
                    .isAnnotationPresent(Qualifier.class)) {
                qualifiers.add(annotation);
            }
        }

        if (qualifiers.size() == 0
                || (qualifiers.size() == 1 && qualifiers.iterator().next()
                        .annotationType() == Named.class)) {
            qualifiers.add(DefaultLiteral.INSTANCE);
        }

        if (!qualifiers.contains(NewLiteral.INSTANCE)) {
            qualifiers.add(AnyLiteral.INSTANCE);
        }

        return qualifiers;
    }
}
