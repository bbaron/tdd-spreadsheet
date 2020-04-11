package leetcode;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParallelMergeSortTest {
  private MergeSort sorter = new MergeSort();

  @Test
  public void performanceTest() {
    int[] serial = new Random().ints(8).toArray();
    int[] parallel = Arrays.copyOf(serial, serial.length);

    MergeSort mergeSort = new MergeSort();
    long start = System.nanoTime();
    mergeSort.sort(serial);
    System.out.println("Merge Sort done in: "
        + (System.nanoTime() - start));

    ParallelMergeSort sorter = new ParallelMergeSort(parallel);
    start = System.nanoTime();
    sorter.sort();
    System.out.println("Parallel Merge Sort done in: "
        + (System.nanoTime() - start));

    assertArrayEquals(parallel, serial);
  }

  @Test
  public void shouldDoNothingWithEmptyArray() {
    int[] values = {};

    sorter.sort(values);

    assertEquals(values.length, 0);
  }

  @Test
  public void shouldDoNothingWithOneElementArray() {
    int[] values = {42};

    sorter.sort(values);

    assertArrayEquals(new int[]{42}, values);
  }

  @Test
  public void shouldSortValues() {
    int[] values = new int[]{9, -3, 5, 0, 1};
    int[] expectedOrder = new int[]{-3, 0, 1, 5, 9};

    sorter.sort(values);
    assertArrayEquals(expectedOrder, values);
  }

  @Test
  void sorts1value() {
    int[] values = new int[]{1};
    int[] expected = new int[]{1};
    sorter.sort(values);
    assertArrayEquals(expected, values);
  }

  @Test
  void sorts2values() {
    int[] values = new int[]{2, 1};
    int[] expected = new int[]{1, 2};
    sorter.sort(values);
    assertArrayEquals(expected, values);
  }

  @Test
  void sortsEmpty() {
    int[] values = new int[0];
    int[] expected = new int[0];
    sorter.sort(values);
    assertArrayEquals(expected, values);
  }
}
