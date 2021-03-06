package org.jspringbot.jenkins.mvn.plugin.handler;


import org.jspringbot.jenkins.api.model.Job;

import java.io.IOException;

public class TestJobReferenceDependencyBuildJobActionHandler extends JobReferenceDependencyBuildJobActionHandler {

    public static final String TYPE = "test-reference-dependency-build";

    public TestJobReferenceDependencyBuildJobActionHandler() {
        super(TYPE);
    }

    @Override
    protected void doOnJob(Job job) throws IOException, InterruptedException {
        //int random = (int) (1 + Math.random() * (6 - 1));

        int random = 1;

        logInfo(String.format("[thread=%s] Building %s job, random time %ds", Thread.currentThread().getName(), job.getName(), random));

        Thread.sleep(random * 1000);
    }
}
