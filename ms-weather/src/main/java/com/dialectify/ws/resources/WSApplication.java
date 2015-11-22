package com.dialectify.ws.resources;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

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
