# INFO 6205 Fall 2021 Team Project  

## Baiyu Li 001569441 & Xinhui Zhao 001560851  

### Implement five sorts and related benchmark for a natural language (Simplified Chinese) which uses Unicode characters.  
- Benchmark   
   src/main/java/edu/neu/coe/huskySort/util/Benchmark_Test.java   


- Sort
1. Timsort   
   Java class: src/main/java/edu/neu/coe/huskySort/sort/chineseSort/TimSort.java   
   Test class: src/test/java/edu/neu/coe/huskySort/sort/chineseSort/TimSortTest.java   

2. Dual-Pivot Quicksort   
   Java class: src/main/java/edu/neu/coe/huskySort/sort/chineseSort/DualPivotQuickSort.java   
   Test class: src/test/java/edu/neu/coe/huskySort/sort/chineseSort/DualPivotQuickSortTest.java   

3. Pure Huskysort    
   Java class: src/main/java/edu/neu/coe/huskySort/sort/chineseSort/PureHuskySort.java   
   Test class: src/test/java/edu/neu/coe/huskySort/sort/chineseSort/PureHuskySortTest.java   

4. LSD Radix Sort   
   Java class: src/main/java/edu/neu/coe/huskySort/sort/chineseSort/LSDRadixSort.java   
   Test class: src/test/java/edu/neu/coe/huskySort/sort/chineseSort/LSDRadixSortTest.java   

5. MSD Radix Sort
   Java class: src/main/java/edu/neu/coe/huskySort/sort/chineseSort/MSDRadixSort.java   
   Test class: src/test/java/edu/neu/coe/huskySort/sort/chineseSort/MSDRadixSortTest.java   

### Modify the HuskyCoderFactory.java to implement the PureHuskySort
- New HuskyCoderFactory: src/main/java/edu/neu/coe/huskySort/sort/chineseSort/HuskySortCoderFactory.java   