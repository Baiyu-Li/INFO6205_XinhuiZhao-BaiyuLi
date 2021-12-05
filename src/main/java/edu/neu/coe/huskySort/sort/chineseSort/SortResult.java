package edu.neu.coe.huskySort.sort.chineseSort;

import edu.neu.coe.huskySort.sort.BaseHelper;
import edu.neu.coe.huskySort.sort.Helper;
import edu.neu.coe.huskySort.sort.chineseSort.TimSort;
import edu.neu.coe.huskySort.util.Benchmark_Test;
import org.junit.Test;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class SortResult {
    public static void main(String[] args) throws IOException {
        TimSort testTim = new TimSort();
        testTim.TimSortTest(999998);

        DualPivotQuickSort testQuick = new DualPivotQuickSort();
        testQuick.DualPivotQuickSortTest(999998);

        PureHuskySort<String> testHusky = new PureHuskySort<>(HuskySortCoderFactory.chineseUnicodeCoder, false, false);
        testHusky.PureHuskySortTest(999998);

        LSDRadixSort testLSD = new LSDRadixSort();
        testLSD.LSDRadixSortTest(999998);

        MSDRadixSort testMSD = new MSDRadixSort();
        testMSD.MSDRadixSortTest(999998);
    }
}
