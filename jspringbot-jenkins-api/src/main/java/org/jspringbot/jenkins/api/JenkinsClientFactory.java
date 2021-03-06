package org.jspringbot.jenkins.api;

import org.apache.commons.lang.Validate;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.IOException;
import java.net.URI;

/**
 * Jenkins client factory
 */
public final class JenkinsClientFactory {

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.getSerializationConfig().withSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        mapper.configure(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES, false);
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_NULL_FOR_PRIMITIVES, false);

        return mapper;
    }

    public static AbstractHttpClient createHttpClient(int defaultMaxPerRoute, int maxTotal) {
        ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager();
        manager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        manager.setMaxTotal(maxTotal);

        return new DefaultHttpClient(manager);
    }

    public static JenkinsClient create(String path, int defaultMaxPerRoute, int maxTotal) throws IOException {
        return create(path, null, defaultMaxPerRoute, maxTotal);
    }

    public static JenkinsClient create(String path, String contextPath, int defaultMaxPerRoute, int maxTotal) throws IOException {
        Validate.notNull(path, "path should not be null.");

        HttpContext context = new BasicHttpContext();

        return new JenkinsClient(createHttpClient(defaultMaxPerRoute, maxTotal), context, createObjectMapper(), URI.create(path), contextPath);
    }


    public static JenkinsClient create(String path) throws IOException {
        JenkinsClient jenkinsClient = create(path, null, 2, 2);
        jenkinsClient.init();

        return jenkinsClient;
    }

    public static JenkinsClient createWithCredentials(String path, String username, String password) throws IOException {
        JenkinsClient jenkinsClient = create(path, null, 2, 2);
        jenkinsClient.authenticate(username, password);
        jenkinsClient.init();
        return jenkinsClient;
    }


    public static JenkinsClient create(String path, String contextPath) throws IOException {
        return create(path, contextPath, 2, 2);
    }
}
