package com.dialectify.ws.resources;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class WSResponseConverter implements MessageBodyWriter<WSResponse>
{

	private static final String UTF_8 = "UTF-8";

	@Override
	public long getSize(WSResponse arg0, Class<?> arg1, Type arg2,
			Annotation[] arg3, MediaType arg4)
	{
		return -1;
	}

	@Override
	public boolean isWriteable(Class<?> arg0, Type arg1, Annotation[] arg2,
			MediaType arg3)
	{
		return true;
	}

	@Override
	public void writeTo(WSResponse arg0, Class<?> arg1, Type arg2,
			Annotation[] arg3, MediaType arg4,
			MultivaluedMap<String, Object> arg5, OutputStream arg6)
			throws IOException, WebApplicationException
	{
		OutputStreamWriter writer = new OutputStreamWriter(arg6, UTF_8);
		Gson gson = new GsonBuilder().create();
		gson.toJson(arg0, writer);
		writer.close();

	}

}
