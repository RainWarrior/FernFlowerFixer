package net.minecraftforge.lex.fffixer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Util
{
    public static Iterator<String> sortStrings(Iterator<String> itr)
    {
        List<String> list = new ArrayList<String>();
    
        while (itr.hasNext())
            list.add(itr.next());
    
        Collections.sort(list);
        return list.iterator();
    }

    public static void putIntercept(HashMap<Integer, Integer> map, Integer key, Integer value)
    {
        System.out.println("====================== " + key + " " + value);
        map.put(key, value);
    }
    public static Iterator<Object> sortLvs(Iterator<Object> itr)
    {
        List<Object> list = new ArrayList<Object>();
    
        while (itr.hasNext())
            list.add(itr.next());
    
        Collections.sort(list);
        return list.iterator();
    }
}
