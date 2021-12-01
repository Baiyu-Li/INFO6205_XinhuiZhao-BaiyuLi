package edu.neu.coe.huskySort.sort.chineseSort;

import edu.neu.coe.huskySort.sort.BaseHelper;
import edu.neu.coe.huskySort.sort.Helper;
import edu.neu.coe.huskySort.sort.SortTest;
import edu.neu.coe.huskySort.sort.SortWithHelper;
import edu.neu.coe.huskySort.sort.simple.MergeSortBasicTest;
import edu.neu.coe.huskySort.util.Config;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class TimSortTest {
    static class TimTest extends SortWithHelper<Integer> {
        public TimTest(String description, int N, boolean instrumenting, Config config) {
            super(description, N, config);
        }

        /**
         * Generic, mutating sort method which operates on a sub-array
         *
         * @param xs   sort the array xs from "from" to "to".
         * @param from the index of the first element to sort
         * @param to   the index of the first element not to sort
         */
        @Override
        public void sort(Integer[] xs, int from, int to) {
            Arrays.sort(xs, from, to);
        }
    }

    String[] input = new String[]{"刘持平", "洪文胜", "樊辉辉", "苏会敏", "高民政", "曹玉德", "袁继鹏",
            "舒冬梅", "杨腊香", "许凤山", "王广风", "黄锡鸿", "罗庆富", "顾芳芳", "宋雪光", "王诗卉"};
    String[] expected = new String[]{"曹玉德", "樊辉辉", "高民政", "顾芳芳", "洪文胜", "黄锡鸿", "刘持平",
            "罗庆富", "舒冬梅", "宋雪光", "苏会敏", "王广风", "王诗卉", "许凤山", "杨腊香", "袁继鹏"};

    @Test
    public void sort() {
        String[] res = TimSort.sort(input);
        assertArrayEquals(expected, res);
    }

    @Test
    public void sort1() {
        String[] xs = getWords(TimSortTest::lineAsList);
        TimSort test = new TimSort();
        String[] ys = test.sort(xs);
        assertEquals("阿安", ys[0]);
        assertEquals("阿彬", ys[1]);
        //assertEquals("瞿麟曼", ys[999997]);
    }

    /**
     * Create a string representing an integer, with commas to separate thousands.
     *
     * @param x the integer.
     * @return a String representing the number with commas.
     */
    public static String formatWhole(final int x) {
        return String.format("%,d", x);
    }

    /**
     * Method to open a resource relative to this class and from the corresponding File, get an array of Strings.
     *
     * @param stringListFunction a function which takes a String and splits into a List of Strings.
     * @return an array of Strings.
     */
    static String[] getWords(final Function<String, List<String>> stringListFunction) {
        try {
            final File file = new File(getPathname(TimSortTest.class));
            final String[] result = getWordArray(file, stringListFunction);
            System.out.println("getWords: testing with " + formatWhole(result.length) + " unique words: from " + file);
            return result;
        } catch (final FileNotFoundException e) {
            System.out.println("Cannot find resource: " + "shuffledChinese.txt");
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
     * @return an array of Strings.
     */
    static String[] getWordArray(final File file, final Function<String, List<String>> stringListFunction) {
        try (final FileReader fr = new FileReader(file)) {
            return getWordList(fr, stringListFunction, 2).toArray(new String[0]);
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

    private static String getPathname(@SuppressWarnings("SameParameterValue") final Class<?> clazz)
            throws FileNotFoundException {
        final URL url = clazz.getClassLoader().getResource("shuffledChinese.txt");
        if (url != null) return url.getPath();
        throw new FileNotFoundException("shuffledChinese.txt" + " in " + clazz);
    }


    @Test
    public void getHelper() {
    }

    @Test
    public void init() {
    }

    @Test
    public void preProcess() {
    }

    @Test
    public void close() {
    }

    private static Config config;

    @BeforeClass
    public static void beforeClass() throws IOException {
        config = Config.load(MergeSortBasicTest.class);
    }
}
