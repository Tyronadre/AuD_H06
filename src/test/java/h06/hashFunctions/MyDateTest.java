package h06.hashFunctions;

import h06.util.TestClass;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Calendar;

import static h06.util.Assertions.*;
import static java.lang.reflect.Modifier.*;

public class MyDateTest extends TestClass {

    public final Field boolParameter;
    public final Method getYear, getMonth, getDay, getHour, getMinute, hashCode;

    public MyDateTest() {
        super("h06.hashFunctions.MyDate", constructor -> {
            Type[] types = constructor.getGenericParameterTypes();

            return types.length == 2 &&
                   types[0].equals(Calendar.class) &&
                   types[1].equals(boolean.class);
        });

        boolParameter = getFieldByCriteria("boolParameter", field -> {
            int modifiers = field.getModifiers();

            return !isStatic(modifiers) && isFinal(modifiers) && field.getType().equals(boolean.class);
        });

        getYear = getMethodByName("getYear()");
        getMonth = getMethodByName("getMonth()");
        getDay = getMethodByName("getDay()");
        getHour = getMethodByName("getHour()");
        getMinute = getMethodByName("getMinute()");
        hashCode = getMethodByName("hashCode()");
    }

    @Test
    @Override
    public void testDefinition() {
        // class
        assertHasModifiers(testedClass, PUBLIC);

        // constructor
        assertHasModifiers(testedClassConstructor, PUBLIC);

        // fields
        int numberOfIntConstants = 0,
            numberOfIntArrayConstants = 0,
            numberOfBooleanConstants = 0,
            numberOfLongConstants = 0,
            numberOfLongArrayConstants = 0;

        for (Field field : fields.values()) {
            int modifiers = field.getModifiers();
            Class<?> type = field.getType();

            if (isPrivate(modifiers) && !isStatic(modifiers) && isFinal(modifiers) && type.equals(int.class))
                numberOfIntConstants++;
            else if (!isStatic(modifiers) && isFinal(modifiers) && (type.equals(int[].class) || type.equals(Integer[].class)))
                numberOfIntArrayConstants++;
            else if (!isStatic(modifiers) && isFinal(modifiers) && type.equals(boolean.class))
                numberOfBooleanConstants++;
            else if (isPrivate(modifiers) && !isStatic(modifiers) && isFinal(modifiers) && type.equals(long.class))
                numberOfLongConstants++;
            else if (!isStatic(modifiers) && isFinal(modifiers) && (type.equals(long[].class) || type.equals(Long[].class)))
                numberOfLongArrayConstants++;
        }

        assertTrue(numberOfIntConstants >= 5 || numberOfIntArrayConstants >= 1,
                className + " is missing either int constant(s) or an integer array constant");
        assertTrue(numberOfBooleanConstants >= 1, className + " is missing a boolean constant");
        assertTrue(numberOfLongConstants >= 6 || numberOfLongArrayConstants >= 1,
                className + " is missing either long constant(s) or an long array constant");

        assertHasModifiers(boolParameter, FINAL);
        assertDoesNotHaveModifiers(boolParameter, STATIC);
        assertType(boolParameter, boolean.class.getTypeName());

        // methods
        assertReturnType(getYear, int.class.getTypeName());

        assertReturnType(getMonth, int.class.getTypeName());

        assertReturnType(getDay, int.class.getTypeName());

        assertReturnType(getHour, int.class.getTypeName());

        assertReturnType(getMinute, int.class.getTypeName());

        assertHasModifiers(hashCode, PUBLIC);
        assertReturnType(hashCode, int.class.getTypeName());
    }

    @Test
    @Override
    public void testInstance() throws Exception {
        Calendar calendar1 = Calendar.getInstance(), calendar2 = Calendar.getInstance();

        calendar1.set(1970, Calendar.JANUARY, 1, 0, 0);
        calendar2.set(2021, Calendar.DECEMBER, 31, 23, 59);

        Object instance1True = newInstance(calendar1, true), instance2True = newInstance(calendar2, true),
               instance1False = newInstance(calendar1, false), instance2False = newInstance(calendar2, false);
        Calendar[] calendars = new Calendar[] {calendar1, calendar2};
        Object[] instances = new Object[] {instance1True, instance2True, instance1False, instance2False};
        int[] hashCodes = new int[] {212375983, 6698879, 194985117, 212198415};

        for (int i = 0; i < instances.length; i++) {
            assertEquals(calendars[i % 2].get(Calendar.YEAR), getYear.invoke(instances[i]),
                    "getYear() did not return the correct value");
            assertEquals(calendars[i % 2].get(Calendar.MONTH), getMonth.invoke(instances[i]),
                    "getMonth() did not return the correct value");
            assertEquals(calendars[i % 2].get(Calendar.DAY_OF_MONTH), getDay.invoke(instances[i]),
                    "getDay() did not return the correct value");
            assertEquals(calendars[i % 2].get(Calendar.HOUR_OF_DAY), getHour.invoke(instances[i]),
                    "getHour() did not return the correct value");
            assertEquals(calendars[i % 2].get(Calendar.MINUTE), getMinute.invoke(instances[i]),
                    "getMinute() did not return the correct value");
            assertEquals(hashCodes[i], hashCode.invoke(instances[i]),
                    "hashCode() did not return the correct value");
        }
    }
}
