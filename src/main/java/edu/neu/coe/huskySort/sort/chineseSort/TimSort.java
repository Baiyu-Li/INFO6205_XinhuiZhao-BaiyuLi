package edu.neu.coe.huskySort.sort.chineseSort;

import java.text.Collator;
import java.util.Arrays;
import java.util.Locale;

public class TimSort {
    Collator collator = Collator.getInstance(Locale.CHINA);

    public int compare(String s1, String s2) {
        return collator.compare(s1, s2);
    }

    static TimSort tim = new TimSort();
    static int min(int a, int b){
        if(a<b) {
            return a;
        }else {
            return b;
        }
    }

    static void insertionSort(String a[], int beg, int end){
        int i, j;
        String temp;
        for (i = beg + 1; i <= end; i++) {
            temp = a[i];
            j = i - 1;

            while(j >= beg && !(tim.compare(temp,a[j]) > 0)) {
                a[j+1] = a[j];
                j = j-1;
            }
            a[j+1] = temp;
        }
    }

    static void merge(String a[], int beg, int mid, int end)  {
        int i, j, k;
        int n1 = mid - beg + 1;
        int n2 = end - mid;
        String[] LeftArray = new String[n1];
        String[] RightArray = new String[n2];

        for (i = 0; i < n1; i++){
            LeftArray[i] = a[beg + i];
        }
        for (j = 0; j < n2; j++) {
            RightArray[j] = a[mid + 1 + j];
        }
        i = 0;
        j = 0;
        k = beg;
        while (i < n1 && j < n2) {
            if(!(tim.compare(LeftArray[i],RightArray[j])>0)) {
                a[k] = LeftArray[i];
                i++;
            }else {
                a[k] = RightArray[j];
                j++;
            }
            k++;
        }
        while (i<n1) {
            a[k] = LeftArray[i];
            i++;
            k++;
        }
        while (j<n2) {
            a[k] = RightArray[j];
            j++;
            k++;
        }
    }

    public static String[] sort(String[] a){
        int n = a.length;

        for (int i = 0; i < n; i+=32) {
            insertionSort(a, i, min((i+32-1), (n-1)));
        }

        for (int size = 32; size < n; size = 2*size) {
            for (int beg = 0; beg < n; beg += 2*size) {
                int mid = beg + size - 1;
                int end = min((beg + 2*size - 1),(n-1));

                if(mid < end) {
                    merge(a, beg, mid, end);
                }
            }
        }
        return a;
    }

    public static String[] preProcess(String[] xs) {
        return Arrays.copyOf(xs, xs.length);
    }

    public static String[] postProcess(String[] xs){
        return xs;
    }
}
