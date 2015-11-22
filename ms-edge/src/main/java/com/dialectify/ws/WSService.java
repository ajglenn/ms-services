package com.dialectify.ws;

public class WSService extends STrackerServer
{

	public static void main(String[] args)
	{
		System.setProperty("archaius.deployment.applicationId", "zuul");

		new WSService().start("zuul");
	}
}
