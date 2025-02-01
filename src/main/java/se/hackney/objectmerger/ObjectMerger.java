package se.hackney.objectmerger;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ObjectMerger {
    
    public static <T> T merge(T target, T source) {
        if (source == null) return target;
        if (target == null) return source;

        Class<?> clazz = target.getClass();
        mergeFields(target, source, clazz);
        return target;
    }

    private static void mergeFields(Object target, Object source, Class<?> clazz) {
        // Hantera alla fält i klassen
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object sourceValue = field.get(source);
                if (sourceValue != null) {
                    Object targetValue = field.get(target);
                    Object mergedValue = mergeValues(targetValue, sourceValue);
                    field.set(target, mergedValue);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Kunde inte komma åt fält: " + field.getName(), e);
            }
        }

        // Hantera även ärvda fält från superklassen
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && !superClass.equals(Object.class)) {
            mergeFields(target, source, superClass);
        }
    }

    private static Object mergeValues(Object targetValue, Object sourceValue) {
        if (sourceValue == null) return targetValue;
        if (targetValue == null) return sourceValue;

        Class<?> valueClass = sourceValue.getClass();

        // Hantera Collections
        if (sourceValue instanceof Collection) {
            return mergeCollections((Collection<?>) targetValue, (Collection<?>) sourceValue);
        }
        // Hantera Maps
        else if (sourceValue instanceof Map) {
            return mergeMaps((Map<?, ?>) targetValue, (Map<?, ?>) sourceValue);
        }
        // Hantera Arrays
        else if (valueClass.isArray()) {
            return mergeArrays(targetValue, sourceValue);
        }
        // För komplexa objekt, gör en rekursiv merge
        else if (!valueClass.isPrimitive() && !valueClass.equals(String.class) 
                 && !valueClass.equals(Boolean.class) && !valueClass.equals(Integer.class)
                 && !valueClass.equals(Long.class) && !valueClass.equals(Double.class)
                 && !valueClass.equals(Float.class)) {
            return merge(targetValue, sourceValue);
        }
        
        // För primitiva typer och deras wrappers, använd sourceValue
        return sourceValue;
    }

    @SuppressWarnings("unchecked")
    private static Collection<?> mergeCollections(Collection<?> target, Collection<?> source) {
        Collection result = new ArrayList<>(target);
        result.addAll(source);
        return result;
    }

    @SuppressWarnings("unchecked")
    private static Map<?, ?> mergeMaps(Map<?, ?> target, Map<?, ?> source) {
        Map result = new HashMap<>(target);
        result.putAll(source);
        return result;
    }

    private static Object mergeArrays(Object target, Object source) {
        int targetLength = Array.getLength(target);
        int sourceLength = Array.getLength(source);
        Object result = Array.newInstance(target.getClass().getComponentType(), 
                                        targetLength + sourceLength);
        
        System.arraycopy(target, 0, result, 0, targetLength);
        System.arraycopy(source, 0, result, targetLength, sourceLength);
        return result;
    }
}