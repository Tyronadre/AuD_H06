package h06;

import h06.test.RuntimeTest;

public class Main {
  public static void main(String[] args) {
    System.out.println("Hello World!");

    var runtime = new RuntimeTest();
    System.out.println("Starting Tests");
    runtime.Test(1,1,1,1);
    System.out.println("DONE 1/16");
    runtime.Test(1,1,1,2);
//    System.out.println("DONE 2/16");
//    runtime.Test(1,1,2,1);
//    System.out.println("DONE 3/16");
//    runtime.Test(1,1,2,2);
    System.out.println("DONE 4/16");
    runtime.Test(1,2,1,1);
    System.out.println("DONE 5/16");
    runtime.Test(1,2,1,2);
//    System.out.println("DONE 6/16");
//    runtime.Test(1,2,2,1);
//    System.out.println("DONE 7/16");
//    runtime.Test(1,2,2,2);
    System.out.println("DONE 8/16");
    runtime.Test(2,1,1,1);
    System.out.println("DONE 9/16");
    runtime.Test(2,1,1,2);
//    System.out.println("DONE 10/16");
//    runtime.Test(2,1,2,1);
//    System.out.println("DONE 11/16");
//    runtime.Test(2,1,2,2);
    System.out.println("DONE 12/16");
    runtime.Test(2,2,1,1);
    System.out.println("DONE 13/16");
    runtime.Test(2,2,1,2);
//    System.out.println("DONE 14/16");
//    runtime.Test(2,2,2,1);
//    System.out.println("DONE 15/16");
//    runtime.Test(2,2,2,2);
    System.out.println("DONE 16/16");
    System.out.println("Finished!");
  }
}
