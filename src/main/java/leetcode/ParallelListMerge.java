package leetcode;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

class ParallelListMerge extends RecursiveTask<ListNode> {
  private final ListNode[] lists;
  private final ListMerge lm = new ListMerge();

  ParallelListMerge(ListNode[] lists) {
    String t = Thread.currentThread().getName();
    if (t.indexOf('-') > 0) {
      t = t.substring(t.indexOf('-') + 1);
    }
    System.out.printf("%s: PLM of with %s lists%n",
        t,
        lists.length);
    this.lists = lists;
  }


  @Override
  protected ListNode compute() {
    if (lists.length <= 8) {
      return lm.mergeKLists(lists);
    }
    int mid = lists.length / 2;
    ListNode[] l1 = Arrays.copyOfRange(lists, 0, mid);
    ListNode[] l2 = Arrays.copyOfRange(lists, mid + 1, lists.length);
    var t1 = new ParallelListMerge(l1);
    t1.fork();
    var t2 = new ParallelListMerge(l2);
    ListNode r1 = t2.compute();
    ListNode r2 = t1.join();
    return lm.mergeTwoLists(r1, r2);
  }

  public static void main(String[] args) {
    ParallelListMerge plm = new ParallelListMerge(ListMerge.lol(800, 10));
    ForkJoinPool commonPool = ForkJoinPool.commonPool();
    ListNode result = commonPool.invoke(plm);
    System.out.println(result);
  }
}
