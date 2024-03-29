package cc.iotkit.plugins.dlt645.utils;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

/**
 * @Author：tfd
 * @Date：2023/12/13 17:50
 * 集合操作的工具类
 */
public class ContainerUtils {
    public ContainerUtils() {
    }

    /**
     * 获取类函数的名称
     * 例如：getMethodName(Integer.toHexString)，返回的就是toHexString
     *
     * @param function
     * @param <E>
     * @param <R>
     * @return
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    private static <E, R> String getMethodName(SerializableFunction<E, R> function) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method method = function.getClass().getDeclaredMethod("writeReplace");
        method.setAccessible(Boolean.TRUE);
        SerializedLambda serializedLambda = (SerializedLambda) method.invoke(function);
        String implMethodName = serializedLambda.getImplMethodName();

        return implMethodName;
    }

    /**
     * 获取类的函数
     *
     * @param clazz
     * @param function
     * @param <E>
     * @param <R>
     * @param <T>
     * @return
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    private static <E, R, T> Method getMethod(Class<T> clazz, SerializableFunction<E, R> function) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String methodName = ContainerUtils.getMethodName(function);
        return clazz.getMethod(methodName);
    }

    /**
     * 根据对象列表中的对象的getXxxx()函数，取出成员
     *
     * @param objList
     * @param clazz
     * @param method  method是clazz的成员函数
     * @param <K>
     * @param <T>
     * @return
     */
    private static <K, T> List<K> buildListByGetField(List<T> objList, Class<K> clazz, Method method) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<K> keyList = new ArrayList<K>();
        for (T obj : objList) {
            // 接下来就该执行该方法了，第一个参数是具体调用该方法的对象, 第二个参数是执行该方法的具体参数
            Object keyObject = method.invoke(obj);
            if (clazz.isInstance(keyObject)) {
                K key = clazz.cast(keyObject);
                keyList.add(key);
            }
        }

        return keyList;
    }

    public static <K, T> List<?> buildKeyList(List<T> objList, Method method) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<K> keyList = new ArrayList<K>();
        Class<?> returnType = method.getReturnType();
        for (T obj : objList) {
            // 接下来就该执行该方法了，第一个参数是具体调用该方法的对象, 第二个参数是执行该方法的具体参数
            Object keyObject = method.invoke(obj);
            if (returnType.isInstance(keyObject)) {
                keyList.add((K) returnType.cast(keyObject));
            }
        }

        return keyList;
    }

    /**
     * 根据对象的getXxxx()，取出类型列表中的数据
     *
     * @param objList  AClass对象列表
     * @param clazz    TClass AClass::getXxxx()中，TClass这样的成员
     * @param function AClass::getXxxx 这样的函数
     * @param <E>
     * @param <R>
     * @param <K>
     * @param <T>
     * @return
     */
    public static <E, R, K, T> List<K> buildListByGetField(List<T> objList, SerializableFunction<E, R> function, Class<K> clazz) {
        if (objList.isEmpty()) {
            return new ArrayList<K>();
        }

        try {
            // 取得函数对应的方法
            Method method = ContainerUtils.getMethod(objList.get(0).getClass(), function);

            // 使用方法返回对应的数组
            return ContainerUtils.buildListByGetField(objList, clazz, method);
        } catch (NoSuchMethodException e) {
            return new ArrayList<K>();
        } catch (SecurityException e) {
            return new ArrayList<K>();
        } catch (IllegalAccessException e) {
            return new ArrayList<K>();
        } catch (IllegalArgumentException e) {
            return new ArrayList<K>();
        } catch (InvocationTargetException e) {
            return new ArrayList<K>();
        }
    }

    /**
     * 根据Key生成Map，该方法是是具体类的函数，（不具备多态能力，不是反射，速度很快）
     *
     * @param objList
     * @param clazz
     * @param method  使用obj.getClass().getMethod("getTnlKey", new Class[0])获取Method
     * @return
     */
    public static <K, T> Map<K, T> buildMapByKeyAndFinalMethod(List<T> objList, Class<K> clazz, Method method) {
        try {
            Map<K, T> uid2deviceMap = new HashMap<K, T>();
            for (T obj : objList) {
                // 接下来就该执行该方法了，第一个参数是具体调用该方法的对象, 第二个参数是执行该方法的具体参数
                Object keyObject = method.invoke(obj);
                if (clazz.isInstance(keyObject)) {
                    K key = clazz.cast(keyObject);
                    uid2deviceMap.put(key, obj);
                }
            }

            return uid2deviceMap;
        } catch (IllegalAccessException e) {
            return Collections.emptyMap();
        } catch (InvocationTargetException e) {
            return Collections.emptyMap();
        }
    }


    public static <E, R, K, T> Map<K, T> buildMapByKey(List<T> objList, SerializableFunction<E, R> function) {
        if (objList.isEmpty()) {
            return new HashMap<>();
        }

        try {
            // 取得函数对应的方法
            Method method = ContainerUtils.getMethod(objList.get(0).getClass(), function);

            // 使用方法返回对应的数组
            return ContainerUtils.buildMapByKey(objList, method);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | IllegalArgumentException |
                SecurityException e) {
            return new HashMap<K, T>();
        }
    }

    public static <K, T> Map<K, T> buildMapByKey(List<T> objList, Method method) {
        try {
            Map<K, T> uid2deviceMap = new HashMap<K, T>();
            for (T obj : objList) {
                // 接下来就该执行该方法了，第一个参数是具体调用该方法的对象, 第二个参数是执行该方法的具体参数
                Object keyObject = method.invoke(obj);
                uid2deviceMap.put((K) keyObject, obj);
            }

            return uid2deviceMap;
        } catch (IllegalAccessException | InvocationTargetException e) {
            return Collections.emptyMap();
        }
    }

    /**
     * 分类
     *
     * @param objList
     * @param clazz
     * @param method
     * @param <K>
     * @param <T>
     * @return
     */
    public static <K, T> Map<K, List<T>> buildMapByTypeAndFinalMethod(List<T> objList, Class<K> clazz, Method method) {
        try {
            Map<K, List<T>> uid2deviceMap = new HashMap<>();
            for (T obj : objList) {
                // 接下来就该执行该方法了，第一个参数是具体调用该方法的对象, 第二个参数是执行该方法的具体参数
                Object keyObject = method.invoke(obj);
                if (clazz.isInstance(keyObject)) {
                    K key = clazz.cast(keyObject);
                    List<T> list = uid2deviceMap.computeIfAbsent(key, k -> new ArrayList<>());
                    list.add(obj);
                }
            }
            return uid2deviceMap;
        } catch (IllegalAccessException | InvocationTargetException e) {
            return Collections.emptyMap();
        }
    }

    /**
     * 根据Key生成Map，该方法是是具体类的函数，（不具备多态能力，不是反射，速度很快）
     *
     * @param objList  类型AClass的列表容器
     * @param function 类型AClass中的某个成员getxxxx()
     * @param clazz    类型AClass中的某个成员BClass getxxxx()中的BClass
     * @param <E>
     * @param <R>
     * @param <K>
     * @param <T>
     * @return
     */
    public static <E, R, K, T> Map<K, T> buildMapByKeyAndFinalMethod(List<T> objList, SerializableFunction<E, R> function, Class<K> clazz) {
        if (objList.isEmpty()) {
            return new HashMap<>();
        }

        try {
            // 取得函数对应的方法
            Method method = ContainerUtils.getMethod(objList.get(0).getClass(), function);

            // 使用方法返回对应的数组
            return ContainerUtils.buildMapByKeyAndFinalMethod(objList, clazz, method);
        } catch (NoSuchMethodException e) {
            return new HashMap<K, T>();
        } catch (SecurityException e) {
            return new HashMap<K, T>();
        } catch (IllegalAccessException e) {
            return new HashMap<K, T>();
        } catch (IllegalArgumentException e) {
            return new HashMap<K, T>();
        } catch (InvocationTargetException e) {
            return new HashMap<K, T>();
        }
    }

    public static <E, R, K, T> Map<K, List<T>> buildMapByTypeAndFinalMethod(List<T> objList, SerializableFunction<E, R> function, Class<K> clazz) {
        if (objList.isEmpty()) {
            return new HashMap<>();
        }

        try {
            // 取得函数对应的方法
            Method method = ContainerUtils.getMethod(objList.get(0).getClass(), function);

            // 使用方法返回对应的数组
            return ContainerUtils.buildMapByTypeAndFinalMethod(objList, clazz, method);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalArgumentException | IllegalAccessException |
                SecurityException e) {
            return new HashMap<>();
        }
    }

    /**
     * 根据map中的某个元素，将列表转换成以这个元素为key的map
     *
     * @param objList
     * @param mapKey
     * @param clazz
     * @param <K>
     * @param <T>
     * @return
     */
    public static <K, T> Map<K, Map<String, Object>> buildMapByMapAt(List<Map<String, Object>> objList, String mapKey, Class<K> clazz) {
        if (objList.isEmpty()) {
            return new HashMap<>();
        }

        Map<K, Map<String, Object>> result = new HashMap<>();
        for (Map<String, Object> obj : objList) {
            Object value = obj.get(mapKey);
            if (!clazz.isInstance(value)) {
                continue;
            }

            result.put((K) value, obj);
        }

        return result;
    }

    /**
     * 从列表中获取：某个字段等于某个值的对象
     *
     * @param objList
     * @param function
     * @param key
     * @param <E>
     * @param <R>
     * @param <K>
     * @param <T>
     * @return
     */
    public static <E, R, K, T> T getObjectByKey(List<T> objList, SerializableFunction<E, R> function, K key) {
        try {
            if (objList.isEmpty()) {
                return null;
            }

            Method method = ContainerUtils.getMethod(objList.get(0).getClass(), function);

            for (T obj : objList) {
                // 先获取相应的method对象,getMethod第一个参数是方法名，第二个参数是该方法的参数类型，
                //   Method method = obj.getClass().getMethod(getKeyMathName, new Class[0]);

                // 接下来就该执行该方法了，第一个参数是具体调用该方法的对象, 第二个参数是执行该方法的具体参数
                Object keyObject = method.invoke(obj);
                if (key.getClass().isInstance(keyObject)) {
                    if (keyObject.equals(key)) {
                        return obj;
                    }
                }
            }

            return null;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    /**
     * 交换Key-Value
     *
     * @param key2value key2value
     * @return value2key
     */
    public static <K, V> Map<V, K> exchange(Map<K, V> key2value) {
        Map<V, K> result = new HashMap<>();

        for (Map.Entry<K, V> entry : key2value.entrySet()) {
            result.put(entry.getValue(), entry.getKey());
        }

        return result;
    }

    public static <K, V> Map<V, List<K>> exchanges(Map<K, V> key2value) {
        Map<V, List<K>> result = new HashMap<>();

        for (Map.Entry<K, V> entry : key2value.entrySet()) {
            List<K> list = result.get(entry.getValue());
            if (list == null) {
                list = new ArrayList<>();
                result.put(entry.getValue(), list);
            }

            list.add(entry.getKey());
        }

        return result;
    }

    /**
     * 根据Key提取出相关的值列表
     *
     * @param key2value key2value
     * @param keyList   key列表
     * @return 跟key相关的values
     */
    public static <K, V> List<V> buildValueListByKey(Map<K, V> key2value, Collection<K> keyList) {
        List<V> resultList = new ArrayList<V>();
        for (K key : keyList) {
            V value = key2value.get(key);
            if (value != null) {
                resultList.add(value);
            }
        }

        return resultList;
    }

    /**
     * 从A类型列表转换成B类型列表:A/B是派生类关系
     *
     * @param aClazzList
     * @param bClazz
     * @return
     */
    public static <A, B> List<B> buildClassList(List<A> aClazzList, Class<B> bClazz) {
        List<B> bInstanceList = new ArrayList<B>();

        for (A aInstance : aClazzList) {
            if (bClazz.isInstance(aInstance)) {
                bInstanceList.add(bClazz.cast(aInstance));
            }
        }

        return bInstanceList;
    }

    /**
     * 定义一个函数接口
     *
     * @param <E>
     * @param <R>
     */
    @FunctionalInterface
    public interface SerializableFunction<E, R> extends Function<E, R>, Serializable {
    }
}
