package com.company.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IteratorUtil {
    public static <N> List<N> iterator2List(Iterator<N> itr) {
        List<N> list = new ArrayList<N>();
        while (itr.hasNext()) {
            list.add(itr.next());
        }
        return list;
    }
}
