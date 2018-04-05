package ca.mcgill.cs.comp409.a4.q1.grid.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

public class CollectionUtils {
    public static <T> LinkedList<T> flattenArray2D(T[][] pObstacleFreePoints, Class<T> c) {
        LinkedList<T> indices = new LinkedList<>(Arrays.asList(
                Arrays.stream(pObstacleFreePoints).flatMap(Arrays::stream).toArray(length -> {
                    @SuppressWarnings("unchecked") final T[] arr = (T[]) Array.newInstance(c, length);
                    return arr;
                })));
        indices.removeAll(Collections.singleton(null));
        return indices;
    }
}
