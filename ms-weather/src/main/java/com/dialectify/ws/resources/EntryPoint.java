package com.dialectify.ws.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/ms-weather/entry-point")
public class EntryPoint
{
	@GET
	@Path("test")
	@Produces(MediaType.APPLICATION_JSON)
	public WSResponse test()
	{
		return new WSResponse("Kansas City", "nice");
	}
}
