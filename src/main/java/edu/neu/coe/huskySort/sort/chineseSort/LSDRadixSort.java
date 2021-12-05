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

public class LSDRadixSort {
    private static final int ASCII_RANGE = 32800;

    /**
     * findMaxLength method returns maximum length of all available strings in an array
     *
     * @param strArr It contains an array of String from which maximum length needs to be found
     * @return int Returns maximum length value
     */
    private static int findMaxLength(String[] strArr) {
        int maxLength = strArr[0].length();
        for (String str : strArr)
            maxLength = Math.max(maxLength, str.length());
        return maxLength;
    }

    /**
     * charAsciiVal method returns ASCII value of particular character in a String.
     *
     * @param str          String input for which ASCII Value need to be found
     * @param charPosition Character position of which ASCII value needs to be found. If character
     *                     doesn't exist then ASCII value of null i.e. 0 is returned
     * @return int Returns ASCII value
     */
    private static int charAsciiVal(String str, int charPosition) {
        Collator collator = Collator.getInstance(Locale.CHINA);
        if (charPosition >= str.length()) {
            return 0;
        }else {
            byte[] bytes = collator.getCollationKey(String.valueOf(str.charAt(charPosition))).toByteArray();
            return (bytes[0] & 0xFF) * 255 + (bytes[1] & 0xFF);
        }
    }

    /**
     * charSort method is implementation of LSD sort algorithm at particular character.
     *
     * @param strArr       It contains an array of String on which LSD char sort needs to be performed
     * @param charPosition This is the character position on which sort would be performed
     * @param from         This is the starting index from which sorting operation will begin
     * @param to           This is the ending index up until which sorting operation will be continued
     */
    private static void charSort(String[] strArr, int charPosition, int from, int to) {
        int[] count = new int[ASCII_RANGE + 2];
        String[] result = new String[strArr.length];

        for (int i = from; i <= to; i++) {
            int c = charAsciiVal(strArr[i], charPosition);
            count[c + 2]++;
        }

        // transform counts to indices
        for (int r = 1; r < ASCII_RANGE + 2; r++)
            count[r] += count[r - 1];

        // distribute
        for (int i = from; i <= to; i++) {
            int c = charAsciiVal(strArr[i], charPosition);
            result[count[c + 1]++] = strArr[i];
        }

        // copy back
        if (to + 1 - from >= 0) System.arraycopy(result, 0, strArr, from, to + 1 - from);
    }

    /**
     * sort method is implementation of LSD String sort algorithm.
     *
     * @param strArr It contains an array of String on which LSD sort needs to be performed
     * @param from   This is the starting index from which sorting operation will begin
     * @param to     This is the ending index up until which sorting operation will be continued
     */
    public static void sort(String[] strArr, int from, int to) {
        int maxLength = findMaxLength(strArr);
        for (int i = maxLength - 1; i >= 0; i--)
            charSort(strArr, i, from, to);
    }

    /**
     * sort method is implementation of LSD String sort algorithm.
     *
     * @param strArr It contains an array of String on which LSD sort needs to be performed
     * @return
     */
    public static String[] sort(String[] strArr) {
        sort(strArr, 0, strArr.length - 1);
        return strArr;
    }

    public static String[] preProcess(String[] xs) {
        return Arrays.copyOf(xs, xs.length);
    }

    public static String[] postProcess(String[] xs){
        return xs;
    }

    public void LSDRadixSortTest(int n) throws IOException {
        String[] xs = getWords("shuffledChinese.txt", LSDRadixSort::lineAsList);
        TimSort test = new TimSort();
        String[] ys = test.sort(xs);

        test.writeTxt("LSDSortedResult(500).txt", ys, 500);
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
            final File file = new File(getPathname(resource, LSDRadixSort.class));
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
