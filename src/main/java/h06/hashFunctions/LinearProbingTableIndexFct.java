package h06.hashFunctions;

public class LinearProbingTableIndexFct<T> implements OtherAndIntToIntFunction<T>{
  private final OtherToIntFunction<T> tOtherToIntFunction;

  public LinearProbingTableIndexFct(OtherToIntFunction<T> tOtherToIntFunction) {
    this.tOtherToIntFunction = tOtherToIntFunction;
  }

  @Override
  public int apply(T t, int i) {
    return Math.floorMod(
      Math.floorMod(tOtherToIntFunction.apply(t) , tOtherToIntFunction.getTableSize()) +
        Math.floorMod(i, tOtherToIntFunction.getTableSize())
      , tOtherToIntFunction.getTableSize());
  }

  @Override
  public int getTableSize() {
    return tOtherToIntFunction.getTableSize();
  }

  @Override
  public void setTableSize(int tableSize) {
    tOtherToIntFunction.setTableSize(tableSize);
  }
}
