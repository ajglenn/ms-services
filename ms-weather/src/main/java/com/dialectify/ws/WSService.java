package com.dialectify.ws;

public class WSService extends STrackerServer
{

	public static void main(String[] args)
	{
		if (args.length != 1)
		{
			usageStatement();
			return;
		}

		System.setProperty("archaius.deployment.applicationId", "middletier");

		new WSService().start(args[0]);
	}

	private static void usageStatement()
	{
		System.out.println("Application class name required");
	}

}
