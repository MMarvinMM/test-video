
package com.chinatower.adapter.model;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class ChangeItem<T> {
    private T item;
    private long changeIndex;
}
