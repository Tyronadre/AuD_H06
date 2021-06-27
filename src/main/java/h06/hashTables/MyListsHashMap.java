package h06.hashTables;

import h06.hashFunctions.OtherToIntFunction;

import java.util.LinkedList;

public class MyListsHashMap<K, V> implements MyMap<K, V> {
  private final LinkedList<KeyValuePair<K, V>>[] table;
  private final OtherToIntFunction<K> kOtherToIntFunction;

  @SuppressWarnings("unchecked")
  public MyListsHashMap(OtherToIntFunction<K> kOtherToIntFunction) {
    this.table = new LinkedList[kOtherToIntFunction.getTableSize()];
    for (int i = 0; i < kOtherToIntFunction.getTableSize(); i++)
      table[i] = new LinkedList<>();
    this.kOtherToIntFunction = kOtherToIntFunction;
  }

  @Override
  public boolean containsKey(K key) {
    return table[kOtherToIntFunction.apply(key)].stream().anyMatch((var t) -> (t.getKey().equals(key)));
  }

  @Override
  public V getValue(K key) {
    for (var t : table[kOtherToIntFunction.apply(key)])
      if (t.getKey().equals(key))
        return t.getValue();
    return null;
  }

  @Override
  public V put(K key, V value) {
    for (var t : table[kOtherToIntFunction.apply(key)])
      if (t.getKey().equals(key)) {
        var temp = t.getValue();
        t.setValue(value);
        return temp;
      }
    table[kOtherToIntFunction.apply(key)].add(0, new KeyValuePair<>(key, value));
    return null;
  }

  @Override
  public V remove(K key) {
    for (var t : table[kOtherToIntFunction.apply(key)])
      if (t.getKey().equals(key)) {
        var temp = t.getValue();
        table[kOtherToIntFunction.apply(key)].remove(t);
        return temp;
      }
    return null;
  }
}
