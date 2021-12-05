package edu.neu.coe.huskySort.sort.chineseSort;

import edu.neu.coe.huskySort.sort.BaseHelper;
import edu.neu.coe.huskySort.sort.simple.QuickSort;

import java.io.*;
import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public void DualPivotQuickSortTest(int n) throws IOException {
        String[] xs = getWords("shuffledChinese.txt", DualPivotQuickSort::lineAsList);
        TimSort test = new TimSort();
        String[] ys = test.sort(xs);

        test.writeTxt("DualPivotQuickSortedResult(500).txt", ys, 500);
    }
    public void writeTxt(String fileName, String[] content, int n)  {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
            for (int i = 0; i < n; i++) {
                out.write(content[i]);
                out.write("\n");
            }
            out.close();
        } catch (IOException e) {
        }
    }

    static String[] getWords(final String resource, final Function<String, List<String>> stringListFunction) {
        try {
            final File file = new File(getPathname(resource, DualPivotQuickSort.class));
            final String[] result = getWordArray(file, stringListFunction, 2);
            System.out.println("getWords: testing with " + formatWhole(result.length) + " unique words: from " + file);
            return result;
        } catch (final FileNotFoundException e) {
            System.out.println("Cannot find resource: " + resource);
            return new String[0];
        }
    }

    private static List<String> getWordList(final FileReader fr, final Function<String,
            List<String>> stringListFunction, final int minLength) {
        final List<String> words = new ArrayList<>();
        for (final Object line : new BufferedReader(fr).lines().toArray())
            words.addAll(stringListFunction.apply((String) line));
        return words.stream().distinct().filter(s -> s.length() >= minLength).collect(Collectors.toList());
    }


    /**
     * Method to read given file and return a String[] of its content.
     *
     * @param file               the file to read.
     * @param stringListFunction a function which takes a String and splits into a List of Strings.
     * @param minLength          the minimum acceptable length for a word.
     * @return an array of Strings.
     */
    static String[] getWordArray(final File file, final Function<String, List<String>> stringListFunction,
                                 final int minLength) {
        try (final FileReader fr = new FileReader(file)) {
            return getWordList(fr, stringListFunction, minLength).toArray(new String[0]);
        } catch (final IOException e) {
            System.out.println("Cannot open file: " + file);
            return new String[0];
        }
    }

    static List<String> lineAsList(final String line) {
        final List<String> words = new ArrayList<>();
        words.add(line);
        return words;
    }

    private static String getPathname(final String resource, @SuppressWarnings("SameParameterValue") final Class<?> clazz)
            throws FileNotFoundException {
        final URL url = clazz.getClassLoader().getResource(resource);
        if (url != null) return url.getPath();
        throw new FileNotFoundException(resource + " in " + clazz);
    }

    public static String formatWhole(final int x) {
        return String.format("%,d", x);
    }

    private static String[] extendArray(String[] a, int size) {
        String[] b = new String[a.length * size];
        int j = 0;
        while (j < size) {
            System.arraycopy(a, 0, b, a.length * j, a.length);
            j++;
        }
        return b;
    }
}