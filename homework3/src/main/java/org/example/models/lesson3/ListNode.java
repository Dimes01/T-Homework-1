package org.example.models.lesson3;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ListNode<T> {
    private T value;
    private ListNode<T> previousNode;
    private ListNode<T> nextNode;
}
