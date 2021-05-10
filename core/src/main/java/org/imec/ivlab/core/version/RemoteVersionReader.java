package org.imec.ivlab.core.version;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.imec.ivlab.core.constants.CoreConstants;
import org.imec.ivlab.core.exceptions.ExternalConnectionException;
import org.imec.ivlab.core.exceptions.RemoteVersionCheckFailedException;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoteVersionReader {

    private final static Logger LOG = Logger.getLogger(RemoteVersionReader.class);

    private final static String EVS_VERSION_WIKI_PAGE_ID = "5407680";
    private final static String CONFLUENCE_CONTENT_API_PATH = "/rest/api/content/";
    private final static String CONFLUENCE_CONTENT_API_QUERY_PARAMS = "?expand=body.storage";
    private final static int CONNECTION_TIMEOUT = 2000;
    private final static int SOCKET_TIMEOUT = 2000;

    protected static String getRemoteVersion() throws RemoteVersionCheckFailedException {

        // example URL to get the content of a page: http://wiki.ivlab.ilabt.imec.be/rest/api/content/5407659?expand=body.storage

        String url = CoreConstants.EVS_WIKI_URL + CONFLUENCE_CONTENT_API_PATH + EVS_VERSION_WIKI_PAGE_ID + CONFLUENCE_CONTENT_API_QUERY_PARAMS;
        String apiResonseString = null;
        try {
            apiResonseString = sendGetRequest(url);
        } catch (ExternalConnectionException e) {
            throw new RemoteVersionCheckFailedException(e);
        }

        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        ApiResponse response = gson.fromJson(apiResonseString, ApiResponse.class);

        if (response != null && response.getBody() != null && response.getBody().getStorage() != null) {
            String body = response.getBody().getStorage().getValue();
            return parseVersionFromPageBody(body);
        }

        throw new RemoteVersionCheckFailedException("No remote version returned: " + apiResonseString);

    }

    private static  String parseVersionFromPageBody(String pageBody) {

        Pattern pattern = Pattern.compile("((?:\\d+\\.?){1,3})");

        Matcher matcher = pattern.matcher(pageBody);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;

    }

    private static String sendGetRequest(String url) throws ExternalConnectionException {

        try {

            RequestConfig.Builder requestBuilder = RequestConfig.custom().setConnectTimeout(CONNECTION_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT);

            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
            httpClientBuilder.setDefaultRequestConfig(requestBuilder.build());

            CloseableHttpClient httpClient = httpClientBuilder.build();

            HttpGet request = new HttpGet(url);
            request.addHeader("content-type", "application/json");
            CloseableHttpResponse result = httpClient.execute(request);
            return EntityUtils.toString(result.getEntity(), "UTF-8");

        } catch (IOException e) {
            throw new ExternalConnectionException(e);
        }

    }

    private class ApiResponse {

        private Body Body;

        public RemoteVersionReader.Body getBody() {
            return Body;
        }

        public void setBody(RemoteVersionReader.Body body) {
            Body = body;
        }
    }

    private class Body {

        private Storage storage;

        public Storage getStorage() {
            return storage;
        }

        public void setStorage(Storage storage) {
            this.storage = storage;
        }
    }

    private class Storage {

        private String representation;
        private String value;

        public String getRepresentation() {
            return representation;
        }

        public void setRepresentation(String representation) {
            this.representation = representation;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }



}
