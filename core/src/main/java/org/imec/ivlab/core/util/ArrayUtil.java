package org.imec.ivlab.core.util;

import javax.xml.bind.JAXBElement;

public class ArrayUtil {


    public static final int INDEX_NOT_FOUND = -1;

    public static <T> int indexOf(Object[] array, Class<T> classToFind, int startIndex) {

        if (array == null) {
            return INDEX_NOT_FOUND;
        }

        if (classToFind == null) {
            return INDEX_NOT_FOUND;
        }

        for (int i = startIndex; i < array.length; i++) {
            if (classToFind.isInstance(array[i])) {
                return i;
            }
        }

        return INDEX_NOT_FOUND;

    }

    public static <T> int indexOfJAXBElement(Object[] array, Class<T> classToFind, int startIndex) {

        if (array == null) {
            return INDEX_NOT_FOUND;
        }

        if (classToFind == null) {
            return INDEX_NOT_FOUND;
        }

        for (int i = startIndex; i < array.length; i++) {

            if (array[i] instanceof JAXBElement) {
                JAXBElement jaxbElement = (JAXBElement) array[i];
                if (classToFind.isInstance(jaxbElement.getValue())) {
                    return i;
                }
            }
        }

        return INDEX_NOT_FOUND;

    }

}
