package h06.hashFunctions;

public class HashCodeTableIndexFct <T> implements OtherToIntFunction<T>{
  private int tableSize;
  private final int offset;

  public HashCodeTableIndexFct(int initialTableSize, int offset) {
    this.offset = offset;
    this.tableSize = initialTableSize;
  }

  @Override
  public int apply(T t) {
    return Math.floorMod(t.hashCode()+offset, tableSize);
  }

  @Override
  public int getTableSize() {
    return tableSize;
  }

  @Override
  public void setTableSize(int tableSize) {
    this.tableSize = tableSize;
  }
}
