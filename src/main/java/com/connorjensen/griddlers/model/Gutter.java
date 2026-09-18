package com.connorjensen.griddlers.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record Gutter(List<List<Integer>> rowClues, List<List<Integer>> columnClues) {
  public Gutter {
    rowClues = deepCopyClues(rowClues);
    columnClues = deepCopyClues(columnClues);
  }

  private static List<List<Integer>> deepCopyClues(List<List<Integer>> clues) {
    List<List<Integer>> newClues = new ArrayList<>();

    for (List<Integer> subList : clues) {
      if (subList != null) {
        List<Integer> newSubList = new ArrayList<>(subList);
        newClues.add(Collections.unmodifiableList(newSubList));
      }
    }
    return Collections.unmodifiableList(newClues);
  }
}
