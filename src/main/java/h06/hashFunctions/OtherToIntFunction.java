package h06.hashFunctions;

public interface OtherToIntFunction <T>{
  int apply(T t);

  int getTableSize();

  void setTableSize(int tableSize);
}
