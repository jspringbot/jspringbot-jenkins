package org.jspringbot.jenkins.mvn.plugin.handler;

import org.jspringbot.jenkins.api.model.Job;

/**
 * Verify if jenkins jobs exists.
 */
public class VerifyJobsActionHandler extends AbstractForEachJobActionHandler {

    public static final String TYPE = "verify-exists";

    public VerifyJobsActionHandler() {
        super(TYPE, "verified");
    }

    @Override
    protected void doOnJob(Job job) {
        // do nothing.
    }
}
