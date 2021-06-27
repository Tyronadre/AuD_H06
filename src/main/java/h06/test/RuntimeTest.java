package h06.test;

import h06.hashFunctions.DoubleHashingTableIndexFct;
import h06.hashFunctions.HashCodeTableIndexFct;
import h06.hashFunctions.LinearProbingTableIndexFct;
import h06.hashFunctions.MyDate;
import h06.hashTables.MyIndexHoppingHashMap;
import h06.hashTables.MyListsHashMap;
import h06.hashTables.MyMap;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RuntimeTest {

  private final int SIZE = 60000;

  private final List<MyDate> myDateListTrue;
  private final List<MyDate> myDateListFalse;

  public RuntimeTest() {
    Random random = new Random();

    myDateListTrue = new LinkedList<>();
    myDateListFalse = new LinkedList<>();

    for (int i = 0; i < SIZE; i++) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(random.nextLong());
      calendar.set(Calendar.YEAR, Math.floorMod(calendar.get(Calendar.YEAR), 2021));
      myDateListTrue.add(new MyDate(calendar, true));
      myDateListFalse.add(new MyDate(calendar, false));
    }
  }

  public void Test(int i, int j, int k, int l) {
    int initSizeHashTable = l == 1 ? SIZE * 3 : (int) (SIZE * 0.1);
    var list = i == 1 ? myDateListTrue : myDateListFalse;
    MyMap<MyDate, MyDate> map = j == 1 ?
      new MyIndexHoppingHashMap<>(initSizeHashTable, 2, 0.75,
        k == 1 ? new LinearProbingTableIndexFct<>(new HashCodeTableIndexFct<>(initSizeHashTable, 0)) :
          new DoubleHashingTableIndexFct<>(new HashCodeTableIndexFct<>(initSizeHashTable, 0), new HashCodeTableIndexFct<>(initSizeHashTable, 42))) :
      new MyListsHashMap<>(new HashCodeTableIndexFct<>(initSizeHashTable, 0));

    System.out.println("\n\nTEST FILLING");
    int FIRST_FILL = 45000;

    for (int c = 0; c < FIRST_FILL; c++) {
      map.put(list.get(c), list.get(c));

      System.out.print((c+1) * 100 / FIRST_FILL + "%    ("+(c+1)+"/"+ FIRST_FILL +")\r");
    }


    System.out.println("\n" + "\nTEST EXISTENCE");
    int t = 0;
    for (int c = 0; c < SIZE; c++) {
      if (map.containsKey(list.get(c))) t++;
      System.out.print((c+1) * 100 / SIZE + "%\r");
    }

    System.out.println("\n" + t + " are existing\n\nTEST GET VALUE");
    t = 0;
    for (int c = 0; c < SIZE; c++) {
      if (map.getValue(list.get(c)) != null) t++;
      System.out.print((c+1) * 100 / SIZE + "%\r");
    }

    System.out.println("\n" + t + " were read \n\nTEST DELETE");
    t = 0;
    for (int c = 0; c < SIZE; c++) {
      if (map.remove(list.get(c)) != null) t++;
      System.out.print((c+1) * 100 / SIZE + "%\r");
    }
    System.out.println("\n" + t + " were deleted\n");

  }
}

