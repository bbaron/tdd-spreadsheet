package app.impl;

import app.exceptions.CircularityError;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

class References {
  private final Map<Key, Set<Key>> dependsOnMap = new LinkedHashMap<>();
  private final Map<Key, Set<Key>> referencedByMap = new LinkedHashMap<>();

  void addDependsOn(Key dependent, Key dependsOn) {
    if (dependent.equals(dependsOn)) {
      throw new CircularityError(dependent + "depends on itself");
    }
    checkForCircularity(dependent, dependsOn);
    dependsOnMap.computeIfAbsent(dependent, k -> new LinkedHashSet<>()).add(dependsOn);
    referencedByMap.computeIfAbsent(dependsOn, k -> new LinkedHashSet<>()).add(dependent);
  }

  Set<Key> getDependsOn(Key key) {
    return Set.copyOf(dependsOnMap.getOrDefault(key, Set.of()));
  }

  Set<Key> getReferencedBy(Key key) {
    return Set.copyOf(referencedByMap.getOrDefault(key, Set.of()));
  }

  private void checkForCircularity(Key dependent, Key dependsOn) {
    Set<Key> marked = new HashSet<>();
    Queue<Key> q = new ArrayDeque<>(getDependsOn(dependsOn));
    while (!q.isEmpty()) {
      var v = q.remove();
       if (v.equals(dependent)) {
        throw new CircularityError(dependent + " is dependent on itself");
      }
      marked.add(v);
      for (var w : getDependsOn(v)) {
        if (!marked.contains(w)) {
          q.add(w);
        }
      }
    }
  }

}
