package edu.neu.coe.huskySort.sort.chineseSort;

import edu.neu.coe.huskySort.sort.simple.InsertionSortMSD;

import java.io.*;
import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MSDRadixSort {
    /**
     * Sort an array of Strings using MSDStringSort.
     *
     * @param a the array to be sorted.
     * @return
     */
    public static String[] sort(String[] a) {
        int n = a.length;
        aux = new String[n];
        sort(a, 0, n, 0);
        return a;
    }

    /**
     * Sort from a[lo] to a[hi] (exclusive), ignoring the first d characters of each String.
     * This method is recursive.
     *
     * @param a the array to be sorted.
     * @param lo the low index.
     * @param hi the high index (one above the highest actually processed).
     * @param d the number of characters in each String to be skipped.
     */
    private static void sort(String[] a, int lo, int hi, int d) {
        if (hi < lo + cutoff) InsertionSortMSD.sort(a, lo, hi, d);
        else {
            int[] count = new int[radix + 2];        // Compute frequency counts.
            for (int i = lo; i < hi; i++)
                count[charAt(a[i], d) + 2]++;
            for (int r = 0; r < radix + 1; r++)      // Transform counts to indices.
                count[r + 1] += count[r];
            for (int i = lo; i < hi; i++)     // Distribute.
                aux[count[charAt(a[i], d) + 1]++] = a[i];
            // Copy back.
            if (hi - lo >= 0) System.arraycopy(aux, 0, a, lo, hi - lo);
            // Recursively sort for each character value.
            // TO BE IMPLEMENTED
            for (int r = 0; r < radix; r++) {
                sort(a, lo + count[r], lo + count[r + 1], d + 1);
            }
        }
    }

    private static int charAt(String s, int d) {
        Collator collator = Collator.getInstance(Locale.CHINA);
        if (d < s.length()) {
            byte[] bytes = collator.getCollationKey(String.valueOf(s.charAt(d))).toByteArray();
            return (bytes[0] & 0xFF) * 255 + (bytes[1] & 0xFF);
        }else return -1;
    }

    private static final int radix = 32800;
    private static final int cutoff = 15;
    private static String[] aux;       // auxiliary array for distribution

    public static String[] preProcess(String[] xs) {
        return Arrays.copyOf(xs, xs.length);
    }

    public static String[] postProcess(String[] xs){
        return xs;
    }

    public void MSDRadixSortTest(int n) throws IOException {
        String[] xs = getWords("shuffledChinese.txt", MSDRadixSort::lineAsList);
        TimSort test = new TimSort();
        String[] ys = test.sort(xs);

        test.writeTxt("MSDSortedResult(500).txt", ys, 500);
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
            final File file = new File(getPathname(resource, MSDRadixSort.class));
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
