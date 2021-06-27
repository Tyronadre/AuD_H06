package h06.hashTables;

public interface MyMap <K,V>{
  /**
   * containsKey: hat einen Parameter key vom formalen Typ K und Rückgabetyp boolean; liefert genau dann true
   * zurück, wenn es ein Paar (key,value) in dem MyMap-Objekt, mit dem containsKey aufgerufen wird, gibt.
   * @param key The Key
   * @return If the map contains this Key
   */
  boolean containsKey(K key);

  /**
   * getValue: hat einen Parameter key vom formalen Typ K und Rückgabetyp V; falls key momentan im MyMap-Objekt
   * enthalten, also irgendein Wert value vom formalen Typ V momentan mit key assoziiert ist, wird value zurückgeliefert; ansonsten wird null zurückgeliefert.
   * @param key The Key
   * @return The Value for this Key
   */
  V getValue(K key);

  /**
   * put: der erste Parameter, key, ist vom formalen Typ K, der zweite Parameter ist vom formalen Typ V; Rückgabetyp
   * ist V; falls key unmittelbar vor Aufruf von put im MyMap-Objekt enthalten war, wird die bisher mit key assoziierte
   * Information zurückgeliefert, und der zweite aktuale Parameter von put wird die neue mit key assoziierte Information; andernfalls wird ein neues (K,V)-Paar bestehend aus den beiden aktualen Parametern von put in das
   * MyMap-Objekt eingefügt und null zurückgeliefert.
   * @param key The Key
   * @param value The Value
   * @return The Old Value if this Key already contained a value, or null
   */
  V put (K key, V value);

  /**
   * remove: hat einen Parameter key vom formalen Typ K und Rückgabetyp V; falls key in dem MyMap-Objekt, mit dem
   * remove aufgerufen wird, enthalten ist, werden key und die damit assoziierte Information aus dem MyMap-Objekt
   * entfernt und letztere zurückgeliefert; andernfalls wird der Inhalt des MyMap-Objektes nicht verändert und null
   * zurückgeliefert.
   */
  V remove (K key);
}
