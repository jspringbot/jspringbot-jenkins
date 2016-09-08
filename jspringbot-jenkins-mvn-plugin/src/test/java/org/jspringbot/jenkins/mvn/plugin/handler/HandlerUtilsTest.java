package org.jspringbot.jenkins.mvn.plugin.handler;

import org.jspringbot.jenkins.mvn.plugin.Action;
import org.jspringbot.jenkins.mvn.plugin.PropertyConstants;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Properties;

import static junit.framework.Assert.assertEquals;

/**
 * Test for {@link HandlerUtils} class.
 */
@Ignore
public class HandlerUtilsTest {

    private static final String SVN_LINK = "http://svn.host/repo/branches/";

    @Test(expected = IllegalArgumentException.class)
    public void testNoReplacement() throws Exception {
        Action action = new Action();
        HandlerUtils.INSTANCE.getReplacement("hello", action);
    }
    
    @Test
    public void testDirectReplacement() throws Exception {
        Action action = createAction("action");
        
        String replacement = HandlerUtils.INSTANCE.getReplacement("hello", action);
        assertEquals("not replaced.", "action", replacement);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegexReplacementNotFound() throws Exception {
        Action action = createAction("2.24-rc", "\\d+\\.\\d+(\\-rc)?");

        HandlerUtils.INSTANCE.getReplacement("not found", action);
    }

    @Test
    public void testRegexReplacement() throws Exception {
        Action action = createAction("2.24-rc", "\\d+\\.\\d+(\\-rc)?");

        String replacement = HandlerUtils.INSTANCE.getReplacement(SVN_LINK, action);

        assertEquals(
                "not replaced.",
                replacement,
                SVN_LINK
        );
    }

    @Test
    public void testRegexReplacementWithGroup() throws Exception {
        Action action = createAction("2.24-rc", "sample-(\\d+\\.\\d+(\\-rc)?)", "1");

        String replacement = HandlerUtils.INSTANCE.getReplacement(SVN_LINK, action);

        assertEquals(
                "not replaced.",
                replacement,
                SVN_LINK
        );
    }

    private Action createAction(String replacement) {
        return createAction(replacement, null);
    }

    private Action createAction(String replacement, String regexPattern) {
        return createAction(replacement, regexPattern, null);
    }

    private Action createAction(String replacement, String regexPattern, String group) {
        Action action = new Action();
        action.setName("action");

        Properties properties = new Properties();
        properties.setProperty(PropertyConstants.REPLACEMENT, replacement);

        action.setProperties(properties);

        if(regexPattern != null) {
            properties.setProperty(PropertyConstants.REGEX_REPLACE_PATTERN, regexPattern);

            if(group != null) {
                properties.setProperty(PropertyConstants.REGEX_REPLACE_PATTERN_GROUP, group);
            }
        }

        return action;
    }
}
