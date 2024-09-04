package pu.jpa.dynamicquery.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Plamen Uzunov
 */
public final class StringUtil {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Logger LOG = LoggerFactory.getLogger(StringUtil.class);

    @SuppressWarnings("unchecked")
    public static <T> T objectFromString(Class<T> type, String val) {
        if (val == null) {
            return null;
        }
        if (String.class.equals(type)) {
            return (T) val;
        } else if (LocalDate.class.equals(type)) {
            return (T) LocalDate.parse(val, DATE_FORMAT);
        } else if (Integer.class.equals(type)) {
            return (T) Integer.valueOf(val);
        } else if (Long.class.equals(type)) {
            return (T) Long.valueOf(val);
        } else if (Byte.class.equals(type)) {
            return (T) Byte.valueOf(val);
        } else if (Double.class.equals(type)) {
            return (T) Double.valueOf(val);
        } else if (BigInteger.class.equals(type)) {
            return (T) new BigInteger(val);
//        } else if (Enum.class.isAssignableFrom(type)) {
            // Handle specific enum types here
        }

        T object = null;
        try {
            Constructor<T> constructor = type.getDeclaredConstructor(String.class);
            object = constructor.newInstance(val);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                 | InvocationTargetException | NoSuchMethodException ex) {
            LOG.error("Error instantiating class:{} from String value: {}", type, val, ex);
        }
        return object;
    }

    private StringUtil() {
        //empty
    }

}
