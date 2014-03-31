package net.minecraftforge.lex.fffixer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Util
{
    public static interface Indexed
    {
        public int getIndex();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Iterator sortIndexed(Iterator itr)
    {
        List prefix = new ArrayList();
        List infix = new ArrayList<Indexed>();
        List suffix = new ArrayList();

        Object next;
        boolean inPrefix = true;

        while(itr.hasNext())
        {
            next = itr.next();
            if(inPrefix)
            {
                if(next instanceof Indexed) inPrefix = false;
                else prefix.add(next);
            }
            if(!inPrefix)
            {
                if(next instanceof Indexed) infix.add((Indexed)next);
                else suffix.add(next);
            }
        }

        Collections.sort(infix, new Comparator<Indexed>() {
            @Override
            public int compare(Indexed i1, Indexed i2)
            {
                return i1.getIndex() - i2.getIndex();
            }
        });
        prefix.addAll(infix);
        prefix.addAll(suffix);
        return prefix.iterator();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T extends Comparable> Iterator<T> sortComparable(Iterator<T> itr)
    {
        List<T> list = new ArrayList<T>();
    
        while (itr.hasNext())
            list.add(itr.next());
    
        Collections.sort(list);
        return list.iterator();
    }
}
