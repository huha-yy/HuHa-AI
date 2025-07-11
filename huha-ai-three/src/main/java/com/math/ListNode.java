package com.math;

import java.util.HashSet;

public class ListNode {
    int val;
    ListNode next;
    ListNode() {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }

    // 将 Solution 改为 static 静态内部类
    public static class Solution {
        public ListNode deleteDuplicates(ListNode head) {
            if (head == null) return null;

            HashSet<Integer> set = new HashSet<>();
            ListNode dummy = new ListNode(0); // 哨兵节点
            ListNode current = dummy;

            while (head != null) {
                int value = head.val;
                if (!set.contains(value)) {
                    set.add(value);
                    current.next = new ListNode(value);
                    current = current.next;
                }
                head = head.next;
            }

            return dummy.next;
        }

        public static void main(String[] args) {
            // 示例测试用例
            ListNode head = new ListNode(1, new ListNode(2, new ListNode(2, new ListNode(3))));
            Solution solution = new Solution();
            ListNode result = solution.deleteDuplicates(head);

            // 打印结果
            while (result != null) {
                System.out.print(result.val + " ");
                result = result.next;
            }
        }
    }
}
