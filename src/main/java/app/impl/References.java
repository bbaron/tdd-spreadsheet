package app.impl;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

class References {
  private final Map<Key, Set<Key>> dependsOnMap = new LinkedHashMap<>();
  private final Map<Key, Set<Key>> referencedByMap = new LinkedHashMap<>();

  void addDependsOn(Key dependent, Key dependsOn) {
    dependsOnMap.computeIfAbsent(dependent, k -> new LinkedHashSet<>()).add(dependsOn);
    referencedByMap.computeIfAbsent(dependsOn, k -> new LinkedHashSet<>()).add(dependent);
  }

  Set<Key> getDependsOn(Key key) {
    return Set.copyOf(dependsOnMap.getOrDefault(key, Set.of()));
  }

  Set<Key> getReferencedBy(Key key) {
    return Set.copyOf(referencedByMap.getOrDefault(key, Set.of()));
  }
}
