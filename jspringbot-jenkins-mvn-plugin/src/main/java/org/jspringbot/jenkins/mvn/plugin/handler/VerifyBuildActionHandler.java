package org.jspringbot.jenkins.mvn.plugin.handler;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jspringbot.jenkins.api.JenkinsClient;
import org.jspringbot.jenkins.api.model.BuildDetails;
import org.jspringbot.jenkins.api.model.Job;
import org.jspringbot.jenkins.api.model.JobDetails;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Verifies the build for the job
 */
public class VerifyBuildActionHandler extends AbstractForEachJobActionHandler {

    public static final String TYPE = "verify-build";

    private List<FailedBuild> failures = new ArrayList<FailedBuild>();


    public VerifyBuildActionHandler() {
        super(TYPE);
    }

    @Override
    public void execute() throws IOException {
        super.execute();

        if(CollectionUtils.isNotEmpty(failures)) {
            StringBuilder buf = new StringBuilder(String.format("%d failed builds: ", failures.size()));
            String indent = StringUtils.repeat(" ", 10);

            for(FailedBuild failedBuild : failures) {
                buf.append("\n").append(indent).append(String.format("[%s] Build Number: %d", failedBuild.job.getName(), failedBuild.build.getNumber()));
                buf.append("\n").append(indent).append(String.format("[%s] URL: %s", failedBuild.job.getName(), String.valueOf(failedBuild.build.getUrl())));
            }

            logInfo(buf.toString());
            
            throw new IllegalArgumentException(String.format("%d failed builds found.", failures.size()));
        }
    }

    @Override
    protected void doOnJob(Job job) throws IOException, InterruptedException, TransformerException, ParserConfigurationException {
        JobDetails details = client.getJobDetails(job.getName(), false);

        if(details.getLastBuild() == null) {
            logInfo(String.format("Verified successful since no last build."));

            return;
        }

        BuildDetails build = client.getBuildDetails(details.getLastBuild());

        // wait for build
        if(build.getBuilding()) {
            waitForBuild(job);
            build = client.getBuildDetails(details.getLastBuild());
        }

        String message = String.format("last build '%d' with result '%s'.", build.getNumber(), build.getResult());
        if(JenkinsClient.RESULT_FAILURE.equals(build.getResult())) {
            message = String.format("Failed %s", message);
            logInfo(message);

            failures.add(new FailedBuild(details, build));
        } else {
            logInfo(String.format("Verified successful %s", message));
        }
    }

    private class FailedBuild {
        protected JobDetails job;

        protected BuildDetails build;

        private FailedBuild(JobDetails job, BuildDetails build) {
            this.build = build;
            this.job = job;
        }
    }
}
