package h06.hashFunctions;

public class DoubleHashingTableIndexFct<T> implements OtherAndIntToIntFunction<T> {
  private final HashCodeTableIndexFct<T> fct1;
  private final HashCodeTableIndexFct<T> fct2;

  public DoubleHashingTableIndexFct(HashCodeTableIndexFct<T> fct1, HashCodeTableIndexFct<T> fct2) {
    this.fct1 = fct1;
    this.fct2 = fct2;
  }

  @Override
  public int apply(T t, int i) {
    return Math.floorMod(Math.floorMod(fct1.apply(t), fct1.getTableSize()) + Math.floorMod(fct2.apply(t), fct2.getTableSize()) * i, fct1.getTableSize());
  }

  @Override
  public int getTableSize() {
    return fct1.getTableSize();
  }

  @Override
  public void setTableSize(int tableSize) {
    fct1.setTableSize(tableSize);
    fct2.setTableSize(tableSize);

  }
}
