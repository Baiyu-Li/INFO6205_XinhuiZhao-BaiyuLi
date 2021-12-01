package edu.neu.coe.huskySort.sort.chineseSort;

import edu.neu.coe.huskySort.sort.huskySortUtils.*;
import edu.neu.coe.huskySort.util.PrivateMethodInvoker;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static edu.neu.coe.huskySort.sort.huskySortUtils.HuskyCoderFactory.asciiToLong;
import static edu.neu.coe.huskySort.sort.huskySortUtils.HuskyCoderFactory.utf8ToLong;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;

public class HuskySortTest {

    @Test
    public void testStringToLong() {
        final PrivateMethodInvoker invoker = new PrivateMethodInvoker(HuskyCoderFactory.class);
        assertEquals(0x48cbb36000000000L, ((Long) invoker.invokePrivate("stringToLong", "Hell", 9, 7, 0x7F)).longValue());
        assertEquals(0x48cbb366f0000000L, ((Long) invoker.invokePrivate("stringToLong", "Hello", 9, 7, 0x7F)).longValue());
        assertEquals(0x48cbb366f58823efL, ((Long) invoker.invokePrivate("stringToLong", "Hello, Go", 9, 7, 0x7F)).longValue());
        assertEquals(0x48CBB366F58823EFL, ((Long) invoker.invokePrivate("stringToLong", "Hello, Goodbye", 9, 7, 0x7F)).longValue());
    }

    @Test
    public void testAsciiToLong() {
        String word = "a";
        assertEquals(0x6100000000000000L, asciiToLong(word));
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Test
    public void testAsciiCoder2() {
        final String apostroph = "apostroph";
        final int length = apostroph.length();
        final long expected1 = 0x61E1BF9F4E5BF868L;
        HuskySequenceCoder<String> coder = HuskyCoderFactory.asciiCoder;
        assertEquals(expected1, coder.huskyEncode(apostroph));
        assertTrue(coder.perfectForLength(length));
        final long expected2 = 0x61E1BF9F4E5BF868L;
        assertEquals(expected2, coder.huskyEncode(apostroph + "e"));
        assertFalse(coder.perfectForLength(length + 1));
    }

    @Test
    public void testEnglishCoder1() {
        HuskyCoder<String> coder = HuskyCoderFactory.englishCoder;
        assertEquals(0x0840000000000000L, coder.huskyEncode("a"));
        assertEquals(0x0880000000000000L, coder.huskyEncode("b"));
        assertEquals(0x0040000000000000L, coder.huskyEncode("A"));
        assertEquals(0x0080000000000000L, coder.huskyEncode("B"));
    }

    @Test
    public void testEnglishCoder2() {
        final String apostrophe = "apostrophe";
        final int length = apostrophe.length();
        final long expected1 = 0x870BF3D32BF0A25L;
        final long expected2 = 0x870BF3D32BF0A25L;
        HuskySequenceCoder<String> coder = HuskyCoderFactory.englishCoder;
        assertEquals(expected1, coder.huskyEncode(apostrophe));
        assertTrue(coder.perfectForLength(length));
        assertEquals(expected2, coder.huskyEncode(apostrophe + "s"));
        assertFalse(coder.perfectForLength(length + 1));
    }

    @Test
    public void testUTF8ToLong() {
        String[] words = {"中文", "太长的中文", "asdfghjkl", "¥", "c", "a𐍈", "𝒑𝒒"};
        long[] codes = new long[7];
        int bitWidth = 8;
        long[] expected = {
                // Here we manually encode some strings to utf-8 format.
                // Chinese string
                (0xE4B8ADE69687L << (2 * bitWidth)) >>> 1,  // 中文
                // Too long Chinese string
                0xE5A4AAE995BFE79AL >>> 1,                  // 太长的中文
                // Too long English string
                0x6173646667686A6BL >>> 1,                  // asdfghjkl
                // A special symbol
                (0xC2A5L << (6 * bitWidth)) >>> 1,          // ¥
                // short enough English string
                (0x63L << (7 * bitWidth)) >>> 1,            // c
                // English and a special character which takes 4 bytes to encode in UTF-8
                (0x61F0908D88L << (3 * bitWidth)) >>> 1,    // a𐍈
                // Special characters which take 4 bytes to encode in UTF-8
                0xf09d9291f09d9292L >>> 1
        };

        // We test if they are correctly encoded.
        for (int i = 0; i < words.length; i++) {
            codes[i] = utf8ToLong(words[i]);
        }

        Assert.assertArrayEquals(expected, codes);
        Arrays.sort(codes);

        // We test if they are correctly sorted.
        long[] sortedExpected = {
                0x6173646667686A6BL >>> 1,                  // asdfghjkl
                (0x61F0908D88L << (3 * bitWidth)) >>> 1,    // a𐍈
                (0x63L << (7 * bitWidth)) >>> 1,            // c
                (0xC2A5L << (6 * bitWidth)) >>> 1,          // ¥
                (0xE4B8ADE69687L << (2 * bitWidth)) >>> 1,  // 中文
                0xE5A4AAE995BFE79AL >>> 1,                  // 太长的中文
                0xf09d9291f09d9292L >>> 1                   // 𝒑𝒒
        };
        Assert.assertArrayEquals(sortedExpected, codes);
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Test
    public void testUnicodeCoder1() {
        HuskySequenceCoder<String> coder = HuskyCoderFactory.unicodeCoder;
        boolean java8 = HuskySortHelper.isPreJava11;
        final String sAase = "Åse";
        final int lAase = sAase.length();
        long expectedAase1 = 0x62803980328000L;
        assertEquals(expectedAase1, coder.huskyEncode(sAase));
        assertTrue(coder.perfectForLength(lAase));
        long expectedAase2 = 0x6280398032803CL;
        assertEquals(expectedAase2, coder.huskyEncode(sAase + "x"));
        final String sMoskva = "Mосква";
        final int lMoskva = sMoskva.length();
        long expectedM = 0x26821F0220821DL;
        assertEquals(expectedM, coder.huskyEncode(sMoskva));
        assertFalse(coder.perfectForLength(lMoskva));
        final String sSrebrenica = "Сребреница";
        final int lSrebrenica = sSrebrenica.length();
        long expectedS = 0x2108220021A8218L;
        assertEquals(expectedS, coder.huskyEncode(sSrebrenica));
        assertFalse(coder.perfectForLength(lSrebrenica));
    }

    @Test
    public void testUnicodeCoder2() {
        HuskySequenceCoder<String> coder = HuskyCoderFactory.unicodeCoder;
        final String s你好世界 = "你好世界";
        final int l你好世界 = s你好世界.length();
        long expected你好世界 = 0x27B02CBEA70B3AA6L;
        assertEquals(expected你好世界, coder.huskyEncode(s你好世界));
        assertFalse(coder.perfectForLength(l你好世界));
        assertEquals(expected你好世界, coder.huskyEncode(s你好世界 + "人"));
    }

    @Test
    public void testUnicodeCoder3() {
        HuskySequenceCoder<String> coder = HuskyCoderFactory.unicodeCoder;
        final String s送别 = "送别\n" + // requires 4 bytes of unicode
                "下馬飲君酒\n" + // requires 10 bytes of unicode of which we can only pack 7 bytes and 7 bits.
                "問君何所之\n" + // ditto
                "君言不得意\n" + // ditto
                "歸臥南山陲\n" + // ditto
                "但去莫復問\n" + // ditto
                "白雲無盡時"; // ditto
        String[] xs = s送别.split("\n");
        Coding coding = coder.huskyEncode(xs);
        long[] longs = coding.longs;
        assertFalse(coding.perfect);
        assertEquals(7, longs.length);
        assertEquals(0x4800A91580000000L, longs[0]);
        assertEquals(0x2705CCD64C792A0DL, longs[1]);
        assertEquals(0x2AA7AA0DA7AAB120L, longs[2]);
        assertEquals(0x2A0DC5002706AFCBL, longs[3]);
        assertEquals(0x35BC40F2A9ABAE38L, longs[4]);
        assertEquals(0x27A329DDC1D5AFD4L, longs[5]);
        assertEquals(0x3B3ECB793890BB70L, longs[6]);
    }

    @Test
    public void testLongCoder() {
        HuskyCoder<Long> coder = HuskyCoderFactory.longCoder;
        assertTrue(coder.perfect());
        assertEquals(Long.MAX_VALUE, coder.huskyEncode(Long.MAX_VALUE));
        assertEquals(Long.MIN_VALUE, coder.huskyEncode(Long.MIN_VALUE));
    }

    @Test
    public void testDoubleCoder() {
        Long[] expectedOrder = {ldMinusMax, ldZero, ldMin, ldOne, ldMax};
        HuskyCoder<Double> coder = HuskyCoderFactory.doubleCoder;
        assertFalse(coder.perfect());
        assertEquals(ldZero, coder.huskyEncode(dZero));
        assertEquals(ldOne, coder.huskyEncode(dOne));
        assertEquals(ldMinusMax, coder.huskyEncode(dMaxMinus));
        assertEquals(ldMax, coder.huskyEncode(dMax));
        assertEquals(ldMin, coder.huskyEncode(dMin));
        List<Double> doubles = Arrays.asList(dZero, dMax, dMaxMinus, dOne, dMin);
        final Object[] result = doubles.stream().map(coder::huskyEncode).sorted().toArray();
        assertArrayEquals(expectedOrder, result);
    }

    @Test
    public void testDoubleCoder2() {
        double bigMaxMinus = BigInteger.valueOf(Long.MAX_VALUE).negate().doubleValue();
        double bigOneMinus = BigInteger.ONE.negate().doubleValue();
        double bigZero = BigInteger.ZERO.doubleValue();
        double bigRedOne = BigInteger.ONE.doubleValue();
        double bigMax = BigInteger.valueOf(Long.MAX_VALUE).doubleValue();
        Long[] expectedOrder = {llMaxMinus, llOneMinus, llZero, llOne, llMax};
        HuskyCoder<Double> coder = HuskyCoderFactory.doubleCoder;
        assertFalse(coder.perfect());
        assertEquals(llZero, coder.huskyEncode(bigZero));
        assertEquals(llOne, coder.huskyEncode(bigRedOne));
        assertEquals(llMaxMinus, coder.huskyEncode(bigMaxMinus));
        assertEquals(llMax, coder.huskyEncode(bigMax));
        assertEquals(llOneMinus, coder.huskyEncode(bigOneMinus));
        List<Double> doubles = Arrays.asList(bigZero, bigMax, bigMaxMinus, bigRedOne, bigOneMinus);
        final Object[] result = doubles.stream().map(coder::huskyEncode).sorted().toArray();
        assertArrayEquals(expectedOrder, result);
    }

    @Test
    public void testBigDecimalCoder() {
        Long[] expectedOrder = {lllMin, 0L, ldZero, 0L, 0L, 1L, lllMax};
        HuskyCoder<BigDecimal> coder = HuskyCoderFactory.bigDecimalCoder;
        assertFalse(coder.perfect());
        assertEquals(ldZero, coder.huskyEncode(BigDecimal.valueOf(dZero)));
        assertEquals(1L, coder.huskyEncode(BigDecimal.valueOf(dOne)));
        assertEquals(0L, coder.huskyEncode(BigDecimal.valueOf(dMaxMinus)));
        assertEquals(0L, coder.huskyEncode(BigDecimal.valueOf(dMax)));
        assertEquals(0L, coder.huskyEncode(BigDecimal.valueOf(dMin)));
        assertEquals(lllMax, coder.huskyEncode(BigDecimal.valueOf(Long.MAX_VALUE)));
        assertEquals(lllMin, coder.huskyEncode(BigDecimal.valueOf(Long.MIN_VALUE)));
        List<BigDecimal> bigDecimals = Arrays.asList(BigDecimal.valueOf(0.0), BigDecimal.valueOf(Long.MAX_VALUE), BigDecimal.valueOf(Double.MAX_VALUE), BigDecimal.valueOf(-Double.MAX_VALUE), BigDecimal.valueOf(1.0), BigDecimal.valueOf(Long.MIN_VALUE), BigDecimal.valueOf(Double.MIN_VALUE));
        final Object[] result = bigDecimals.stream().map(coder::huskyEncode).sorted().toArray();
        assertArrayEquals(expectedOrder, result);
    }

    @Test
    public void testScaledBigDecimalCoder() {
        long lSmallPos = 163720000000000000L;
        long lLargePos = 828340000000000000L;
        long lLargeNeg = -636670000000000000L;
        Long[] expectedOrder = {lLargeNeg, lSmallPos, lLargePos};
        // NOTE we make the resulting Longs 1000x bigger, thus better able to discriminate.
        HuskyCoder<BigDecimal> coder = HuskyCoderFactory.scaledBigDecimalCoder(18);
        assertFalse(coder.perfect());
        double smallPositive = 0.16372;
        double largePositive = 0.82834;
        double largeNegative = -0.63667;
        assertEquals(lSmallPos, coder.huskyEncode(BigDecimal.valueOf(smallPositive)));
        assertEquals(lLargePos, coder.huskyEncode(BigDecimal.valueOf(largePositive)));
        assertEquals(lLargeNeg, coder.huskyEncode(BigDecimal.valueOf(largeNegative)));
        List<BigDecimal> bigDecimals = Arrays.asList(BigDecimal.valueOf(smallPositive), BigDecimal.valueOf(largeNegative), BigDecimal.valueOf(largePositive));
        final Object[] result = bigDecimals.stream().map(coder::huskyEncode).sorted().toArray();
        assertArrayEquals(expectedOrder, result);
    }

    @Test
    public void testBigIntegerCoder() {
        Long[] expectedOrder = {llMaxMinus, llOneMinus, llZero, llOne, llMax};
        HuskyCoder<BigInteger> coder = HuskyCoderFactory.bigIntegerCoder;
        assertFalse(coder.perfect());
        assertEquals(llZero, coder.huskyEncode(BigInteger.ZERO));
        assertEquals(llOne, coder.huskyEncode(BigInteger.ONE));
        assertEquals(llMaxMinus, coder.huskyEncode(BigInteger.valueOf(Long.MAX_VALUE).negate()));
        assertEquals(llMax, coder.huskyEncode(BigInteger.valueOf(Long.MAX_VALUE)));
        assertEquals(llOneMinus, coder.huskyEncode(BigInteger.ONE.negate()));
        List<BigInteger> bigints = Arrays.asList(BigInteger.ZERO, BigInteger.valueOf(Long.MAX_VALUE), BigInteger.valueOf(Long.MAX_VALUE).negate(), BigInteger.ONE, BigInteger.ONE.negate());
        final Object[] result = bigints.stream().map(coder::huskyEncode).sorted().toArray();
        assertArrayEquals(expectedOrder, result);
    }

    @Test
    public void testUTF8EncodingFromStringFromFile()
            throws IOException {
        String file = "src/test/resources/SentiWS_v2.0_Positive.txt";
        String encoding = "UTF-8";
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
        String line1 = reader.readLine();
        String line2 = reader.readLine();
        reader.close();
        long long1 = utf8ToLong(line1);
        long long2 = utf8ToLong(line2);
        // "               A-b-m-a-c-h-u-n-g|NN\t0.0040\tAbmachungen";
        long expected1 = 0x41626D616368756EL >>> 1;
        assertEquals(expected1, long1);
        // "               A-b-s-c-h-l-u-ß-|NN\t0.0040\tAbschluss,Abschlusse,Abschlusses,Abschlüsse,Abschlüssen";
        long expected2 = 0x41627363686C75C3L >>> 1;
        assertEquals(expected2, long2);
        assertTrue(long1 < long2);
    }

    @Test
    public void testDoubleToLong() {
        compareEncodings(Math.PI, Math.E, this::doubleToLong, Double::compare);
        compareEncodings(-Math.PI, -Math.E, this::doubleToLong, Double::compare);
        compareEncodings(1E300, 1E301, this::doubleToLong, Double::compare);
        compareEncodings(1E-300, 1E-301, this::doubleToLong, Double::compare);
        compareEncodings(-1E300, -1E301, this::doubleToLong, Double::compare);
        compareEncodings(-1E-300, -1E-301, this::doubleToLong, Double::compare);
        compareEncodings(1E300, -1E301, this::doubleToLong, Double::compare);
        compareEncodings(1E-300, -1E-301, this::doubleToLong, Double::compare);
    }

    @Test
    public void testProbabilisticEncoder() {
        HuskyCoder<Byte> byteCoder = new HuskyCoderFactory.ProbabilisticEncoder(0.15, 1L) {
        };
        assertFalse(byteCoder.perfect());
        assertEquals(1L, byteCoder.huskyEncode(Byte.valueOf((byte) 1)));
        assertEquals((long) Byte.MIN_VALUE, byteCoder.huskyEncode(Byte.MIN_VALUE));
        assertEquals(0L, byteCoder.huskyEncode(Byte.valueOf((byte) 0)));
        assertEquals((long) Byte.MAX_VALUE, byteCoder.huskyEncode(Byte.MAX_VALUE));
        assertEquals(2L, byteCoder.huskyEncode(Byte.valueOf((byte) 2)));
        assertEquals(-4L, byteCoder.huskyEncode(Byte.valueOf((byte) 3)));
        assertEquals(4L, byteCoder.huskyEncode(Byte.valueOf((byte) 4)));
        assertEquals(5L, byteCoder.huskyEncode(Byte.valueOf((byte) 5)));
        assertEquals(6L, byteCoder.huskyEncode(Byte.valueOf((byte) 6)));
        assertEquals(1L, byteCoder.huskyEncode(Byte.valueOf((byte) 1)));
        HuskyCoder<Integer> integerHuskyCoder = new HuskyCoderFactory.ProbabilisticEncoder(0.15, 1L) {
        };
        assertEquals((long) Integer.MIN_VALUE, integerHuskyCoder.huskyEncode(Integer.MIN_VALUE));
        assertEquals(0L, integerHuskyCoder.huskyEncode(Integer.valueOf(0)));
        assertEquals((long) Integer.MAX_VALUE, integerHuskyCoder.huskyEncode(Integer.MAX_VALUE));
        assertEquals(2L, integerHuskyCoder.huskyEncode(Integer.valueOf(2)));
        assertEquals(3L, integerHuskyCoder.huskyEncode(Integer.valueOf(3)));
        assertEquals(-5L, integerHuskyCoder.huskyEncode(Integer.valueOf(4)));
        assertEquals(5L, integerHuskyCoder.huskyEncode(Integer.valueOf(5)));
        assertEquals(6L, integerHuskyCoder.huskyEncode(Integer.valueOf(6)));
    }

    @Test
    public void testBigDecimalEncoder() {
        compareHuskyEncodings(BigDecimal.valueOf(Math.PI), BigDecimal.valueOf(Math.E), HuskyCoderFactory.bigDecimalCoder, (x1, x2) -> x1.compareTo(x2));
        compareHuskyEncodings(BigDecimal.valueOf(Math.PI).negate(), BigDecimal.valueOf(Math.E).negate(), HuskyCoderFactory.bigDecimalCoder, (x1, x2) -> x1.compareTo(x2));
        compareHuskyEncodings(BigDecimal.valueOf(Math.PI).movePointLeft(100), BigDecimal.valueOf(Math.E).movePointLeft(100), HuskyCoderFactory.scaledBigDecimalCoder(118), (x1, x2) -> x1.compareTo(x2));
        compareHuskyEncodings(BigDecimal.valueOf(Math.PI).movePointRight(100), BigDecimal.valueOf(Math.E).movePointRight(100), HuskyCoderFactory.scaledBigDecimalCoder(-82), (x1, x2) -> x1.compareTo(x2));
        compareHuskyEncodings(BigDecimal.valueOf(Math.PI).movePointLeft(100).negate(), BigDecimal.valueOf(Math.E).movePointLeft(100).negate(), HuskyCoderFactory.scaledBigDecimalCoder(118), (x1, x2) -> x1.compareTo(x2));
        compareHuskyEncodings(BigDecimal.valueOf(Math.PI).movePointRight(100).negate(), BigDecimal.valueOf(Math.E).movePointRight(100).negate(), HuskyCoderFactory.scaledBigDecimalCoder(-82), (x1, x2) -> x1.compareTo(x2));
    }

    @Test
    public void testBigDecimalToLong() {
        // NOTE we don't have a scaled version of bigDecimalToLong
        compareEncodings(BigDecimal.valueOf(Math.PI), BigDecimal.valueOf(Math.E), this::bigDecimalToLong, (x1, x2) -> x1.compareTo(x2));
        compareEncodings(BigDecimal.valueOf(Math.PI).negate(), BigDecimal.valueOf(Math.E).negate(), this::bigDecimalToLong, (x1, x2) -> x1.compareTo(x2));
        compareEncodings(BigDecimal.valueOf(Math.PI).movePointRight(20), BigDecimal.valueOf(Math.E).movePointRight(20), this::bigDecimalToLong, (x1, x2) -> x1.compareTo(x2));
        compareEncodings(BigDecimal.valueOf(Math.PI).movePointRight(20).negate(), BigDecimal.valueOf(Math.E).movePointRight(20).negate(), this::bigDecimalToLong, (x1, x2) -> x1.compareTo(x2));
    }

    public <X> void compareEncodings(X x1, X x2, Function<X, Long> encoder, Comparator<X> comparator) {
        assertEquals(comparator.compare(x1, x2), Long.compare(encoder.apply(x1), encoder.apply(x2)));
    }

    public <X> void compareHuskyEncodings(X x1, X x2, HuskyCoder encoder, Comparator<X> comparator) {
        assertEquals(comparator.compare(x1, x2), Long.compare(encoder.huskyEncode(x1), encoder.huskyEncode(x2)));
    }

    private long doubleToLong(double x) {
        return ((Long) huskyCoderFactoryinvoker.invokePrivate("doubleToLong", x)).longValue();
    }

    /**
     * NOTE that this will not work precisely for small or large BigDecimals.
     *
     * @param x
     * @return
     */
    private long bigDecimalToLong(BigDecimal x) {
        return x.longValue();
    }


    static final PrivateMethodInvoker huskyCoderFactoryinvoker = new PrivateMethodInvoker(HuskyCoderFactory.class);
    static final long llMaxMinus = 0xBC20000000000000L; // -4890909195324358656
    static final long llOneMinus = 0xC010000000000000L; // -4616189618054758400
    static final long llZero = 0L;
    static final long llOne = 0x3FF0000000000000L; // 4607182418800017408
    static final long llMax = 0x43E0000000000000L; // 4890909195324358656
    static final double dZero = 0.0;
    static final double dOne = 1.0;
    static final double dMax = Double.MAX_VALUE;
    static final double dMaxMinus = -dMax;
    static final double dMin = Double.MIN_VALUE;
    static final long ldMinusMax = 0x8010000000000001L; // -9218868437227405311
    static final long ldZero = 0L;
    static final long ldMin = 1L;
    static final long ldOne = 0x3FF0000000000000L; // 4607182418800017408
    static final long ldMax = 0x7FEFFFFFFFFFFFFFL; // 9218868437227405311

    public static final long lllMax = 0x7FFFFFFFFFFFFFFFL;
    public static final long lllMin = 0x8000000000000000L;

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
        Assert.assertEquals("阿安", ys[0]);
        Assert.assertEquals("阿彬", ys[1]);
        Assert.assertEquals("瞿麟曼", ys[999997]);
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
}
