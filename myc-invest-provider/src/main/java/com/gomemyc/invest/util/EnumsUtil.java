package com.gomemyc.invest.util;

public class EnumsUtil {
    
    
    
    public static <T extends Enum<T> > T getEnumByNameOrNull(Class<T> enumType, String name) {
        if (enumType == null || name == null) {
            return null;
        }

        //must be subclass of Enum
        if (!Enum.class.isAssignableFrom(enumType)) {
            throw new IllegalArgumentException("class " + enumType.getName() + " must be a subclass of Enum.");
        }

        T result = null;
        try {
            result = T.valueOf(enumType, name);
        } catch (IllegalArgumentException e) {
        }
        return result;
    }

}
