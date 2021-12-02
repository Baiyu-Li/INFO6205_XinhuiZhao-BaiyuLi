package edu.neu.coe.huskySort.sort.chineseSort;

import edu.neu.coe.huskySort.sort.BaseHelper;
import edu.neu.coe.huskySort.sort.simple.QuickSort;

import java.text.Collator;
import java.util.Arrays;
import java.util.Locale;

public class DualPivotQuickSort {
    Collator collator = Collator.getInstance(Locale.CHINA);

    public int compare(String s1, String s2) {
        return collator.compare(s1, s2);
    }

    // CONSIDER invoke swap in BaseHelper.
    private static void swap(final String[] ys, final int i, final int j) {
        final String temp = ys[i];
        ys[i] = ys[j];
        ys[j] = temp;
    }

    static void sort(String[] A, int left, int right) {
        DualPivotQuickSort quick = new DualPivotQuickSort();
        if (right > left) {
            if (quick.compare(A[left], A[right])>0) DualPivotQuickSort.swap(A, left, right);
            String p = A[left];
            String q = A[right];
            int l = left + 1, g = right - 1, k = l;
            while (k <= g) {
                if (quick.compare(A[k],p)<0) {
                    DualPivotQuickSort.swap(A, k, l);
                    ++l;
                } else if (quick.compare(A[k],q)>=0) {
                    while (quick.compare(A[g], q)>0 && k < g) --g;
                    DualPivotQuickSort.swap(A, k, g);
                    --g;
                    if (quick.compare(A[k], p)<0) {
                        DualPivotQuickSort.swap(A, k, l);
                        ++l;
                    }
                }
                ++k;
            }
            --l;
            ++g;
            DualPivotQuickSort.swap(A, left, l);
            DualPivotQuickSort.swap(A, right, g);
            sort(A, left, l - 1);
            sort(A, l + 1, g - 1);
            sort(A, g + 1, right);
        }
    }

    public static String[] sort(String[] arr) {
        sort(arr,0,arr.length-1);
        return arr;
    }

    public static String[] preProcess(String[] xs) {
        return Arrays.copyOf(xs, xs.length);
    }

    public static String[] postProcess(String[] xs){
        return xs;
    }
}