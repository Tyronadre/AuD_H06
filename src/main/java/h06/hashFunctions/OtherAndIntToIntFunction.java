package h06.hashFunctions;

public interface OtherAndIntToIntFunction<T> {
  int apply (T t,int i);

  int getTableSize();

  void setTableSize(int tableSize);

}
