package org.jspringbot.jenkins.mvn.plugin.handler;

import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpStatus;
import org.jspringbot.jenkins.api.http.StatusOnlyHttpInvoker;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static org.jspringbot.jenkins.mvn.plugin.PropertyConstants.*;

/**
 * Verify the given http then add the jobs to successful reference
 */
public class VerifyHttpURLActionHandler extends AbstractActionHandler {

    public static final String TYPE = "verify-http";

    public VerifyHttpURLActionHandler() {
        super(TYPE);
    }

    @Override
    public void execute() {
        try {
            String urlString = HandlerUtils.INSTANCE.getRequiredProperty(action, URL_TO_VERIFY);

            logInfo(String.format("::>>> Verifying url %s...", urlString));

            URL url = new URL(urlString);

            int status = HttpStatus.SC_OK; 
            if(HandlerUtils.INSTANCE.isPropertyExist(action, HTTP_STATUS)) {
                status = HandlerUtils.INSTANCE.getRequiredInteger(action, HTTP_STATUS);
            }

            StatusOnlyHttpInvoker invoker = HandlerUtils.INSTANCE.createVerifyStatusInvoker(action, url, status);

            if(invoker.execute(url.getPath())) {
                logInfo(String.format("Successfully verified with status '%d'.", invoker.getReceivedStatus()));
                List<String> jobNames = getRelevantJobs();

                if(CollectionUtils.isEmpty(jobNames)) {
                    if(!isOnNoJobsContinue()) {
                        return;
                    }
                }

                logInfo(String.format("Added %d jobs on job reference list named '%s.result'.", jobNames.size(), action.getName()));
                for (String jobName : jobNames) {
                    addJobResult(jobName);
                }
            } else if(HandlerUtils.INSTANCE.isPropertyExist(action, FAIL_ON_URL_UNVERIFIED) &&
                    HandlerUtils.INSTANCE.getRequiredBoolean(action, FAIL_ON_URL_UNVERIFIED)) {
                logInfo(String.format("Failed verification, expected '%d' but received '%d'.", status, invoker.getReceivedStatus()));
                throw new IllegalArgumentException(String.format("URL '%s', expecting status '%d' but was '%d'.", urlString, status, invoker.getReceivedStatus()));
            }
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
}
