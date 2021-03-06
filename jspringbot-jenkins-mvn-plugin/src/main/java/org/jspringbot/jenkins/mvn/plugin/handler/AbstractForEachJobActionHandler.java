package org.jspringbot.jenkins.mvn.plugin.handler;

import org.apache.commons.collections.CollectionUtils;
import org.jspringbot.jenkins.api.model.Job;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.List;

/**
 * Base class for job action handler with for each support.
 */
public abstract class AbstractForEachJobActionHandler extends AbstractActionHandler {
    
    private String actionMessage;

    public AbstractForEachJobActionHandler(String actionType) {
        this(actionType, null);
    }

    public AbstractForEachJobActionHandler(String actionType, String actionMessage) {
        super(actionType);

        this.actionMessage = actionMessage;
    }
    
    protected abstract void doOnJob(Job job) throws IOException, InterruptedException, TransformerException, ParserConfigurationException;

    protected void initRelevantJobs(List<String> jobNames) throws IOException {
    }

    @Override
    public void execute() throws IOException {
        List<String> jobNames = getRelevantJobs();

        if(CollectionUtils.isEmpty(jobNames) && !isOnNoJobsContinue()) {
            return;
        }

        logInfo(String.format("::>>> %d job%s to '%s'.", jobNames.size(), jobNames.size() > 1 ? "s" : "", getActionType()));
        initRelevantJobs(jobNames);

        for(int i = 0; i < jobNames.size(); i++) {
            String jobName = jobNames.get(i);

            Job job = client.getJob(jobName);
            
            if(jobNames.size() > 1) {
                logPrefix = String.format("[%d of %d][%s]:", i + 1, jobNames.size(), jobName);
            } else {
                logPrefix = String.format("[%s]:", jobName);
            }

            // do something on the job
            try {
                doOnJob(job);
            } catch(Exception e) {
                throw new IllegalStateException(String.format("Error execution action '%s' job '%s' with url '%s'.",
                        action.getName(), job.getName(), job.getUrl().toString()), e);
            }

            if(actionMessage != null) {
                logInfo(String.format("Job %s.", actionMessage));
            }
        }

        logPrefix = "";
    }
}
