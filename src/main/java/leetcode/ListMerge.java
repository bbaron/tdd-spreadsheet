package leetcode;

import java.util.Arrays;
import java.util.Random;

public class ListMerge {
  public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
    if (l1 == null) return l2;
    if (l2 == null) return l1;
    ListNode head = new ListNode(Integer.MIN_VALUE);
    ListNode cursor = head;
    ListNode n1 = l1;
    ListNode n2 = l2;
    while (n1 != null && n2 != null) {
      if (n1.val <= n2.val) {
        cursor.next = n1;
        n1 = n1.next;
      } else {
        cursor.next = n2;
        n2 = n2.next;
      }
      cursor = cursor.next;
    }
    if (n1 != null) cursor.next = n1;
    else cursor.next = n2;
    return head.next;
  }

  ListNode mergeKLists(ListNode... lists) {
    if (lists.length == 0) return null;
    if (lists.length == 1) return lists[0];
//    if (lists.length == 2) return mergeTwoLists(lists[0], lists[1]);
    ListNode merged = mergeTwoLists(lists[0], lists[1]);
    ListNode[] newList = new ListNode[lists.length - 1];
    newList[0] = merged;
    System.arraycopy(lists, 2, newList, 1, newList.length - 1);
    return mergeKLists(newList);
  }

  public static void main(String[] args) {
    ListMerge lm = new ListMerge();
    System.out.println(list(1));
    System.out.println(list(1, 2));
    System.out.println(list(1, 2, 3));
//    System.out.println(lm.mergeTwoLists(list(1, 2, 4, -6, 7, 8), list(-1, 3, 4, 5, 6)));
    System.out.println(lm.mergeKLists(lol(0, 0)));
    System.out.println(lm.mergeKLists(lol(10, 1)));
  }

  static ListNode[] lol(int numberOfLists, int sizeOf1list) {
    Random random = new Random();
    var result = new ListNode[numberOfLists];
    for (int i = 0; i < numberOfLists; i++) {
      int[] ns = random.ints(0, 100)
          .limit(sizeOf1list)
          .toArray();
      Arrays.sort(ns);
      result[i] = list(ns);
    }
    return result;
  }

  private static ListNode list(int... xs) {
    if (xs.length == 0) return null;
    ListNode head = new ListNode(xs[0]);
    ListNode curr = head;
    for (int i = 1; i < xs.length; i++) {
      curr.next = new ListNode(xs[i]);
      curr = curr.next;
    }
    return head;
  }

  private static String toString(ListNode ln) {
    if (ln == null) return null;
    StringBuilder s = new StringBuilder();
    s.append(ln.val);
    while (ln.next != null) {
      ln = ln.next;
      s.append("->").append(ln.val);
    }
    return s.toString();
  }
}

class ListNode {
  int val;
  ListNode next;

  ListNode(int x) {
    val = x;
  }

  @Override
  public String toString() {
    var s = val + "";
    if (next == null) return s;
    return s + "->" + next.toString();
  }
}