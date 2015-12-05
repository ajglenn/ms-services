package com.dialectify.ws.resources;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class WSApplication extends Application
{

    @Override
    public Set<Class<?>> getClasses()
    {
        Set<Class<?>> s = new HashSet<Class<?>>();
        s.add(EntryPoint.class);
        s.add(WSResponseConverter.class);
        return s;
    }

    @Override
    public Set<Object> getSingletons()
    {
        // TODO Auto-generated method stub
        return super.getSingletons();
    }

}
