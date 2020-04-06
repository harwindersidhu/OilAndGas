package com.example.khush.oilandgasproject;

import java.util.ArrayList;
import java.util.Comparator;
/**
 * This class is used to calculate the order of the index values of the sorted array
 */

public class ArrayIndexComparator implements Comparator<Integer> {

    private final ArrayList<Double> array;

    public ArrayIndexComparator(ArrayList<Double> array)
    {
        this.array = array;
    }
    /**
     * This method will create the index of arrays
     * @return
     */
    public Integer[] createIndexArray()
    {
        Integer[] indexes = new Integer[array.size()];
        for (int i = 0; i < array.size(); i++)
        {
            indexes[i] = i; // Autoboxing
        }
        return indexes;
    }

    @Override
    public int compare(Integer index1, Integer index2) {
        // Autounbox from Integer to int to use as array indexes
        return array.get(index1).compareTo(array.get(index2));
    }
}
