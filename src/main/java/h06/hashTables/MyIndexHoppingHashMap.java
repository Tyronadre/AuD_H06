package h06.hashTables;

import h06.hashFunctions.OtherAndIntToIntFunction;

public class MyIndexHoppingHashMap<K, V> implements MyMap<K, V> {
  private final double resizeFactor;
  private final double maxFill;
  private final int initialLength;
  private K[] theKeys;
  private V[] theValues;
  private boolean[] occupiedSinceLastRehash;
  private int occupiedSinceLastRehashCounter;
  private OtherAndIntToIntFunction<K> kOtherAndIntToIntFunction;

  @SuppressWarnings("unchecked")
  public MyIndexHoppingHashMap(int arrayLength, double rehashGrowth, double maxFill, OtherAndIntToIntFunction<K> kOtherAndIntToIntFunction) {
    theKeys = (K[]) new Object[arrayLength];
    theValues = (V[]) new Object[arrayLength];
    occupiedSinceLastRehash = new boolean[arrayLength];
    occupiedSinceLastRehashCounter = 0;
    this.resizeFactor = rehashGrowth;
    this.maxFill = maxFill;
    this.kOtherAndIntToIntFunction = kOtherAndIntToIntFunction;
    initialLength = arrayLength;
  }

  @SuppressWarnings("unchecked")
  private void rehash() {
    K[] oldTheKeys = theKeys;
    theKeys = (K[]) new Object[(int) (theKeys.length * resizeFactor)];
    V[] oldTheValues = theValues;
    theValues = (V[]) new Object[(int) (theValues.length * resizeFactor)];
    occupiedSinceLastRehash = new boolean[(int) (occupiedSinceLastRehash.length * resizeFactor)];
    kOtherAndIntToIntFunction.setTableSize((int) (kOtherAndIntToIntFunction.getTableSize() * resizeFactor));
    occupiedSinceLastRehashCounter = 0;
    for (int i = 0; i < oldTheKeys.length; i++)
      if (oldTheKeys[i] != null)
        this.put(oldTheKeys[i], oldTheValues[i]);
  }

  @Override
  public boolean containsKey(K key) {
    for (int i = 0; ; i++) {
      var t = kOtherAndIntToIntFunction.apply(key, i);
      if (theKeys[t] == null) {
        if (!occupiedSinceLastRehash[t])
          return false;
      } else if (theKeys[t].equals(key))
        return true;
    }
  }

  @Override
  public V getValue(K key) {
    for (int i = 0; ; i++) {
      var t = kOtherAndIntToIntFunction.apply(key, i);
      if (theKeys[t] == null) {
        if (!occupiedSinceLastRehash[t])
          return null;
      } else if (theKeys[t].equals(key))
        return theValues[t];
    }
  }

  @Override
  public V put(K key, V value) {
    var emptyIndex = -1;
    for (int i = 0; ; i++) {
      var t = kOtherAndIntToIntFunction.apply(key, i);
      if (theKeys[t] == null) {
        if (emptyIndex == -1)
          emptyIndex = t;
        if (!occupiedSinceLastRehash[t])
          if (occupiedSinceLastRehashCounter++ > (maxFill * theKeys.length)) {
            rehash();
            return put(key, value);
          }
        theKeys[emptyIndex] = key;
        theValues[emptyIndex] = value;
        occupiedSinceLastRehash[emptyIndex] = true;
        return null;
      } else if (theKeys[t].equals(key)) {
        var temp = theValues[t];
        theValues[t] = value;
        return temp;
      }
    }

  }

  @Override
  public V remove(K key) {
    for (int i = 0; ; i++) {
      var t = kOtherAndIntToIntFunction.apply(key, i);
      if (theKeys[t] == null) {
        if (!occupiedSinceLastRehash[t])
          return null;
      } else if (theKeys[t].equals(key)) {
        theKeys[t] = null;
        var temp = theValues[t];
        theValues[t] = null;
        return temp;
      }
    }
  }
}
