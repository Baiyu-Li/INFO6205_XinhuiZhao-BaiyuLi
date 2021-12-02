package edu.neu.coe.huskySort.sort.chineseSort;

import edu.neu.coe.huskySort.sort.BaseHelper;
import edu.neu.coe.huskySort.sort.Helper;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class LSDRadixSortTest {
    private LSDRadixSort ls;
    private String[] strArr;

    @Before
    public void init() {
        ls = new LSDRadixSort();
    }

    @Given("the {string} array of String {string}")
    public void the_array_of_String(String string, String string2) {
        // Write code here that turns the phrase above into concrete actions
        strArr = string2.split(",");
    }

    @When("LSD Radix sort is performed within {int} and {int}")
    public void lsd_Radix_sort_is_performed_within_and(Integer rangeStart, Integer rangeEnd) {
        // Write code here that turns the phrase above into concrete actions
        ls.sort(strArr, rangeStart, rangeEnd);
    }

    @Then("validate if the elements within {int} and {int} statisfy sorting invariance")
    public void validate_if_the_elements_within_and_statisfy_sorting_invariance(Integer rangeStart, Integer rangeEnd) {
        // Write code here that turns the phrase above into concrete actions
        assertTrue(stringSortingInvarianceCheck(rangeStart, rangeEnd));
    }

    public boolean stringSortingInvarianceCheck(int rangeStart, int rangeEnd) {
        for (int i = rangeStart + 1; i <= rangeEnd; i++) {
            if (strArr[i - 1].compareTo(strArr[i]) > 0) return false;
        }
        return true;
    }

    String[] input = new String[]{"刘持平", "洪文胜", "樊辉辉", "苏会敏", "高民政", "曹玉德", "袁继鹏",
            "舒冬梅", "杨腊香", "许凤山", "王广风", "黄锡鸿", "罗庆富", "顾芳芳", "宋雪光", "王诗卉"};
    String[] expected = new String[]{"曹玉德", "樊辉辉", "高民政", "顾芳芳", "洪文胜", "黄锡鸿", "刘持平",
            "罗庆富", "舒冬梅", "宋雪光", "苏会敏", "王广风", "王诗卉", "许凤山", "杨腊香", "袁继鹏"};

    @Test
    public void sort1() {
        LSDRadixSort lsd = new LSDRadixSort();
        String[] res = lsd.sort(input);
        assertArrayEquals(expected, res);
    }

    @Test
    public void sort2() {
        String[] xs = getWords("shuffledChinese.txt", LSDRadixSortTest::lineAsList);
        LSDRadixSort test = new LSDRadixSort();
        String[] ys = test.sort(xs);
        assertEquals("阿安", ys[0]);
        assertEquals("阿彬", ys[1]);
        assertEquals("瞿麟曼", ys[999997]);
    }

    @Test
    public void sort3() {
        int n = 200;
        final Helper<String> helper = new BaseHelper<>("test", n, 1L);
        helper.init(n);
        String[] words = getWords("Chinese_Test.txt", LSDRadixSortTest::lineAsList);
        final String[] xs = helper.random(String.class, r -> words[r.nextInt(words.length)]);
        assertEquals(n, xs.length);
        LSDRadixSort test = new LSDRadixSort();
        String[] ys = test.sort(xs);
        assertEquals("阿安", ys[0]);
        assertEquals("阿赤", ys[199]);
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
    static String[] getWords(final String resource, final Function<String, List<String>> stringListFunction) {
        try {
            final File file = new File(getPathname(resource, LSDRadixSortTest.class));
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
}