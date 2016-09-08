package org.jspringbot.jenkins.mvn.plugin;

import org.apache.commons.collections.MapUtils;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Ignore
public class PropertyMojoTest {

    public static final String HUDSON_URL = "http://jenkins.host:8080/";

    private static final String PROPERTY_NAME = "a";
    public static final String JENKINS_JOB = "job-a";

    private PropertyMojo mojo;

    @Before
    public void setUp() throws Exception {
        mojo = new PropertyMojo();
        mojo.properties = new ArrayList<Property>();
        mojo.variables = new ArrayList<String>();
        mojo.project = mock(MavenProject.class);

        when(mojo.project.getProperties()).thenReturn(new Properties());

        mojo.jenkinsUrl = new URL(HUDSON_URL);

    }

    @Test
    public void testExecuteNoChanges() throws Exception {
        mojo.properties.add(new Property(PROPERTY_NAME, JENKINS_JOB));
        mojo.execute();

        assertTrue(MapUtils.isEmpty(mojo.project.getProperties()));
    }

    @Test
    public void testExecuteWithChanges() throws Exception {
        mojo.properties.add(new Property(PROPERTY_NAME, JENKINS_JOB));
        mojo.project.getProperties().put(PROPERTY_NAME, "latest");

        mojo.execute();

        assertTrue(MapUtils.isNotEmpty(mojo.project.getProperties()));
    }

    @Test
    public void testExecuteWithChangesExpression() throws Exception {
        mojo.properties.add(new Property(PROPERTY_NAME, "$[type eq '" + PROPERTY_NAME + "' ? '" + JENKINS_JOB + "' : '" +JENKINS_JOB + "-branch']"));
        mojo.variables.add("type");
        mojo.project.getProperties().put(PROPERTY_NAME, "latest");
        mojo.project.getProperties().put("type", "a-branch");

        mojo.execute();

        assertTrue(MapUtils.isNotEmpty(mojo.project.getProperties()));
    }
}
