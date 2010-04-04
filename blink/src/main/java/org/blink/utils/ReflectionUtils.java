package org.blink.utils;

import java.beans.Introspector;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ReflectionUtils {

    public static final Type[] EMPTY_TYPES = {};
    public static final Annotation[] EMPTY_ANNOTATIONS = {};
    public static final Class<?>[] EMPTY_CLASSES = new Class<?>[0];

    public static Map<Class<?>, Type> buildTypeMap(Set<Type> types) {
        Map<Class<?>, Type> map = new HashMap<Class<?>, Type>();
        for (Type type : types) {
            if (type instanceof Class<?>) {
                map.put((Class<?>) type, type);
            } else if (type instanceof ParameterizedType) {
                if (((ParameterizedType) type).getRawType() instanceof Class<?>) {
                    map.put((Class<?>) ((ParameterizedType) type).getRawType(),
                            type);
                }
            } else if (type instanceof TypeVariable<?>) {

            }
        }
        return map;
    }

    /**
     * Gets the property name from a getter method.
     *
     * We extend JavaBean conventions, allowing the getter method to have
     * parameters
     *
     * @param method
     *            The getter method
     * @return The name of the property. Returns null if method wasn't JavaBean
     *         getter-styled
     */
    public static String getPropertyName(Method method) {
        String methodName = method.getName();
        if (methodName.matches("^(get).*")) {
            return Introspector.decapitalize(methodName.substring(3));
        } else if (methodName.matches("^(is).*")) {
            return Introspector.decapitalize(methodName.substring(2));
        } else {
            return null;
        }

    }

    /**
     * Checks if class is final
     *
     * @param clazz
     *            The class to check
     * @return True if final, false otherwise
     */
    public static boolean isFinal(Class<?> clazz) {
        return Modifier.isFinal(clazz.getModifiers());
    }

    public static int getNesting(Class<?> clazz) {
        if (clazz.isMemberClass() && !isStatic(clazz)) {
            return 1 + getNesting(clazz.getDeclaringClass());
        }
        return 0;
    }

    /**
     * Checks if member is final
     *
     * @param member
     *            The member to check
     * @return True if final, false otherwise
     */
    public static boolean isFinal(Member member) {
        return Modifier.isFinal(member.getModifiers());
    }

    /**
     * Checks if type or member is final
     *
     * @param type
     *            Type or member
     * @return True if final, false otherwise
     */
    public static boolean isTypeOrAnyMethodFinal(Class<?> type) {
        if (isFinal(type)) {
            return true;
        }
        for (Method method : type.getDeclaredMethods()) {
            if (isFinal(method)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPackagePrivate(int mod) {
        return !(Modifier.isPrivate(mod) || Modifier.isProtected(mod) || Modifier
                .isPublic(mod));
    }

    /**
     * Checks if type is static
     *
     * @param type
     *            Type to check
     * @return True if static, false otherwise
     */
    public static boolean isStatic(Class<?> type) {
        return Modifier.isStatic(type.getModifiers());
    }

    /**
     * Checks if member is static
     *
     * @param member
     *            Member to check
     * @return True if static, false otherwise
     */
    public static boolean isStatic(Member member) {
        return Modifier.isStatic(member.getModifiers());
    }

    public static boolean isTransient(Member member) {
        return Modifier.isTransient(member.getModifiers());
    }

    /**
     * Checks if a method is abstract
     *
     * @param method
     * @return whether the method is abstract
     */
    public static boolean isAbstract(Method method) {
        return Modifier.isAbstract(method.getModifiers());
    }

    /**
     * Checks if raw type is array type
     *
     * @param rawType
     *            The raw type to check
     * @return True if array, false otherwise
     */
    public static boolean isArrayType(Class<?> rawType) {
        return rawType.isArray();
    }

    /**
     * Checks if type is parameterized type
     *
     * @param type
     *            The type to check
     * @return True if parameterized, false otherwise
     */
    public static boolean isParameterizedType(Class<?> type) {
        return type.getTypeParameters().length > 0;
    }

    public static boolean isParamerterizedTypeWithWildcard(Class<?> type) {
        if (isParameterizedType(type)) {
            return containsWildcards(type.getTypeParameters());
        }
        return false;
    }

    public static boolean containsWildcards(Type[] types) {
        for (Type type : types) {
            if (type instanceof WildcardType) {
                return true;
            }
        }
        return false;
    }


    public static boolean isSerializable(Class<?> clazz) {
        return clazz.isPrimitive()
                || Serializable.class.isAssignableFrom(clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> getRawType(Type type) {
        if (type instanceof Class<?>) {
            return (Class<T>) type;
        } else if (type instanceof ParameterizedType) {
            if (((ParameterizedType) type).getRawType() instanceof Class<?>) {
                return (Class<T>) ((ParameterizedType) type).getRawType();
            }
        }
        return null;
    }

}
