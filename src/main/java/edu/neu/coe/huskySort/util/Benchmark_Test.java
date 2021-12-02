/*
 * Copyright (c) 2018. Phasmid Software
 */

package edu.neu.coe.huskySort.util;

import edu.neu.coe.huskySort.sort.BaseHelper;
import edu.neu.coe.huskySort.sort.Helper;
import edu.neu.coe.huskySort.sort.chineseSort.*;
import edu.neu.coe.huskySort.sort.simple.QuickSort;
import edu.neu.coe.huskySort.sort.simple.QuickSort_DualPivot;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/** 
 * This class implements a simple Benchmark utility for measuring the running time of algorithms.
 * It is part of the repository for the INFO6205 class, taught by Prof. Robin Hillyard
 * <p>
 * It requires Java 8 as it uses function types, in particular, UnaryOperator&lt;T&gt; (a function of T => T),
 * Consumer&lt;T&gt; (essentially a function of T => Void) and Supplier&lt;T&gt; (essentially a function of Void => T).
 * <p>
 * In general, the benchmark class handles three phases of a "run:"
 * <ol>
 *     <li>The pre-function which prepares the input to the study function (field fPre) (may be null);</li>
 *     <li>The study function itself (field fRun) -- assumed to be a mutating function since it does not return a result;</li>
 *     <li>The post-function which cleans up and/or checks the results of the study function (field fPost) (may be null).</li>
 * </ol>
 * <p>
 * Note that the clock does not run during invocations of the pre-function and the post-function (if any).
 *
 */
public class Benchmark_Test {
	private static Config config;
	public static void main(String[] args) {
		sortChinese();
	}

	public static void sortChinese() {
		for (int i = 250000; i <= 4000000; i *= 2) {
			Helper<String> helper = new BaseHelper<>("Chinese Helper", i);
			QuickSort<String> sorter = new QuickSort_DualPivot<>(helper);
			String[] words = getWords("shuffledChinese.txt", Benchmark_Test::lineAsList);
			String[] xs;
			if (i < 1000000) {
				xs = Arrays.copyOfRange(words, 0, i);
			} else if (i == 1000000) {
				xs = words;
			} else {
				xs = extendArray(words, i / 1000000);
			}

			System.out.println("----------------------- Name List with " + i + " Long -----------------------");

			Benchmark<String[]> bmTim = new Benchmark<>("Chinese Tim sort", TimSort::preProcess, TimSort::sort);
			double times = bmTim.run(xs, 5);
			System.out.println("Run time is " + times);

			PureHuskySort<String> pureHusky = new PureHuskySort<>(HuskySortCoderFactory.chineseUnicodeCoder, false, false);
			Benchmark<String[]> bmPureHusky = new Benchmark<>("Chinese Pure Husky Sort", pureHusky::preProcess, pureHusky::sort);
			times = bmPureHusky.run(xs, 5);
			System.out.println("Run time is " + times);

			Benchmark<String[]> bmDualPivotQuick = new Benchmark<>("Chinese Dual-Pivot Quick sort", DualPivotQuickSort::preProcess, DualPivotQuickSort::sort);
			times = bmDualPivotQuick.run(xs, 5);
			System.out.println("Run time is " + times);

			Benchmark<String[]> bmLSDRadix = new Benchmark<>("Chinese LSD Radix sort", LSDRadixSort::preProcess, LSDRadixSort::sort);
			times = bmLSDRadix.run(xs, 5);
			System.out.println("Run time is " + times);

			Benchmark<String[]> bmMSDRadix = new Benchmark<>("Chinese MSD Radix sort", MSDRadixSort::preProcess, MSDRadixSort::sort);
			times = bmMSDRadix.run(xs, 5);
			System.out.println("Run time is " + times);
		}
	}

	static String[] getWords(final String resource, final Function<String, List<String>> stringListFunction) {
		try {
			final File file = new File(getPathname(resource, Benchmark_Test.class));
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