package ca.mcgill.cs.comp409.a4.q1.grid.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

public class CollectionUtils {

    /**
     * Utility method for flattening a 2D array into a list using streams
     * @param pObstacleFreePoints 2D array of type T
     * @param c Class of type T
     * @param <T> Generic type of 2D array and output list
     * @return Flattened list of 2D array
     */
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
