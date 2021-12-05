package edu.neu.coe.huskySort.sort.chineseSort;

import java.io.*;
import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public void TimSortTest(int n) throws IOException {
        String[] xs = getWords("shuffledChinese.txt", TimSort::lineAsList);
        TimSort test = new TimSort();
        String[] ys = test.sort(xs);

        test.writeTxt("TimSortedResult(500).txt", ys, 500);
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
            final File file = new File(getPathname(resource, TimSort.class));
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
