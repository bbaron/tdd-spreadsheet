package leetcode;

import java.util.Arrays;
import java.util.concurrent.RecursiveAction;

public class ParallelMergeSort extends RecursiveAction {

  // Decides when to fork or compute directly:
  private static final int SORT_THRESHOLD = 4;

  private final int[] values;
  private final int from;
  private final int to;

  public ParallelMergeSort(int[] values) {
    this(values, 0, values.length - 1);
  }

  public ParallelMergeSort(int[] values, int from, int to) {
    System.out.printf("ctor: thread=%s from=%s, to=%s values=%s%n",
        Thread.currentThread().getName(), from, to, values);
    this.values = values;
    this.from = from;
    this.to = to;
  }

  public void sort() {
    compute();
  }

  @Override
  protected void compute() {
    System.out.printf("compute: thread=%s from=%s, to=%s values=%s%n",
        Thread.currentThread().getName(), from, to, values);
    if (from < to) {
      int size = to - from;
      if (size < SORT_THRESHOLD) {
        insertionSort();
      } else {
        int mid = from + Math.floorDiv(size, 2);
        invokeAll(
            new ParallelMergeSort(values, from, mid),
            new ParallelMergeSort(values, mid + 1, to));
        merge(mid);
      }
    }
  }

  private void insertionSort() {
    System.out.printf("insertionSort: thread=%s from=%s, to=%s values=%s%n",
        Thread.currentThread().getName(), from, to, values);
    for (int i = from + 1; i <= to; ++i) {
      int current = values[i];
      int j = i - 1;
      while (from <= j && current < values[j]) {
        values[j + 1] = values[j--];
      }
      values[j + 1] = current;
    }
  }

  private void merge(int mid) {
    System.out.printf("merge: thread=%s from=%s, to=%s, mid=%s values=%s%n",
        Thread.currentThread().getName(), from, to, mid, values);
    int[] left = Arrays.copyOfRange(values, from, mid + 1);
    int[] right = Arrays.copyOfRange(values, mid + 1, to + 1);
    int f = from;

    int li = 0, ri = 0;
    while (li < left.length && ri < right.length) {
      if (left[li] <= right[ri]) {
        values[f++] = left[li++];
      } else {
        values[f++] = right[ri++];
      }
    }

    while (li < left.length) {
      values[f++] = left[li++];
    }

    while (ri < right.length) {
      values[f++] = right[ri++];
    }
  }
}