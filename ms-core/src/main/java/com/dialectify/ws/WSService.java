package com.dialectify.ws;

import com.dialectify.ws.build.STBuild;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileInputStream;

public class WSService extends STrackerServer
{

    public static void main(String[] args)
    {
        if (args.length != 1)
        {
            System.out.println("WSService <yaml file>");
            return;
        }

        try
        {
            Yaml yaml = new Yaml(new Constructor(STBuild.class));
            STBuild build = (STBuild) yaml.load(new FileInputStream(args[0]));
            build.validate();

            System.setProperty("archaius.deployment.applicationId", build.getApplicationName());

            new WSService().start(build);
        }
        catch (Exception e)
        {
            handleException(e);
        }
    }

    private static void handleException(Exception e)
    {
        e.printStackTrace();
    }
}
