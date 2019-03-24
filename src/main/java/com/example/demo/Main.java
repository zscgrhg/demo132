package com.example.demo;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpConnection;
import com.offbytwo.jenkins.model.*;

import java.net.URI;
import java.util.List;

public class Main {
    public static void main(String[] args)throws Exception {
        JenkinsServer js = new JenkinsServer(URI.create("http://localhost:8080"),
                "admin", "admin");


        View hello = js.getView("hello");
        JenkinsHttpConnection client = hello.getClient();


        PluginManager pluginManager = js.getPluginManager();
        List<Plugin> plugins = pluginManager.getPlugins();
        System.out.println(plugins);

        for (Plugin plugin : plugins) {
            System.out.println(plugin.getShortName());
        }

        JobWithDetails job = js.getJob("helloJks");
        QueueReference queueRef = job.build(true);
        System.out.println("Ref:" + queueRef.getQueueItemUrlPart());


        QueueItem queueItem = js.getQueueItem(queueRef);
        while (!queueItem.isCancelled() && job.isInQueue()) {
            System.out.println("In Queue " + job.isInQueue());
            Thread.sleep(200);
            job = js.getJob("maven-test");
            queueItem = js.getQueueItem(queueRef);
        }
        System.out.println("ended waiting.");


        Build lastBuild = job.getLastBuild();

        boolean isBuilding = lastBuild.details().isBuilding();
        while (isBuilding) {
            System.out.println("Is building...(" + lastBuild.getNumber() + ")");
            Thread.sleep(200);
            isBuilding = lastBuild.details().isBuilding();
        }

        System.out.println("Finished.");
        System.out.println(" Result: " + lastBuild.details().getResult());
    }
}
