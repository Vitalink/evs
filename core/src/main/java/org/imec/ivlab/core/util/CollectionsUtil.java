package org.imec.ivlab.core.util;

import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;
import java.util.List;

public class CollectionsUtil {

    public static int size(Collection collection) {

        if (collection == null) {
            return 0;
        }

        return CollectionUtils.size(collection);

    }

    public static boolean emptyOrNull(Collection collection) {
        if (collection == null || collection.isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean notEmptyOrNull(Collection collection) {
        return !emptyOrNull(collection);
    }

    public static <E> void addAllIfNotNull(List<E> list, Collection<? extends E> c) {
        if (c != null) {
            list.addAll(c);
        }
    }

}
