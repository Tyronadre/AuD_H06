package h06.hashFunctions;

import java.util.Calendar;

public class MyDate {
  private final int year;
  private final int month;
  private final int day;
  private final int hour;
  private final int minute;
  private final boolean b;
  private final long lYear;
  private final long lMonth;
  private final long lDay;
  private final long lHour;
  private final long lMinute;
  private final long lSum;

  public int getYear() {
    return year;
  }

  public int getMonth() {
    return month;
  }

  public int getDay() {
    return day;
  }

  public int getHour() {
    return hour;
  }

  public int getMinute() {
    return minute;
  }

  public MyDate(Calendar calendar, boolean b) {
    year = calendar.get(Calendar.YEAR);
    month = calendar.get(Calendar.MONTH);
    day = calendar.get(Calendar.DAY_OF_MONTH);
    hour = calendar.get(Calendar.HOUR_OF_DAY);
    minute = calendar.get(Calendar.MINUTE);
    this.b = b;
    lYear = 4563766470487200L;
    lMonth = 83231L;
    lDay = 3L;
    lHour = 1234L;
    lMinute = 99991L;
    lSum = 98927;
  }

  @Override
  public int hashCode() {
    return b ? Math.floorMod(
      Math.floorMod((long) Math.floorMod( (long) Math.floorMod( (long) Math.floorMod( (long)
        Math.floorMod((year * lYear),Integer.MAX_VALUE) +
        Math.floorMod((month * lMonth),Integer.MAX_VALUE),Integer.MAX_VALUE) +
        Math.floorMod((day * lDay),Integer.MAX_VALUE) ,Integer.MAX_VALUE)+
        Math.floorMod((hour * lHour),Integer.MAX_VALUE),Integer.MAX_VALUE) +
        Math.floorMod((minute * lMinute),Integer.MAX_VALUE),Integer.MAX_VALUE), Integer.MAX_VALUE):
      Math.floorMod(((year + month + day + hour + minute) * lSum), Integer.MAX_VALUE);
  }
}
