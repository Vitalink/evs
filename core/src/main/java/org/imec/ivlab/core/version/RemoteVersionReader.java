package org.imec.ivlab.core.version;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.io.entity.EntityUtils;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.exceptions.ExternalConnectionException;
import org.imec.ivlab.core.exceptions.RemoteVersionCheckFailedException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RemoteVersionReader {

    //private final static Logger LOG = LogManager.getLogger(RemoteVersionReader.class);

    private final static String GITHUB_API_BASE_URL = "https://api.github.com/repos/";
    private final static String OWNER = "smals-jy"; // Replace with your GitHub username or organization name
    private final static String REPO_NAME = "evs"; // Replace with your repository name
    private final static String LATEST_RELEASE_PATH = "/releases/latest";
    private final static int CONNECTION_TIMEOUT = 2;
    private final static int SOCKET_TIMEOUT = 2;
    private final static TimeUnit TIMEOUT_UNIT = TimeUnit.SECONDS;

    protected static String getRemoteVersion() throws RemoteVersionCheckFailedException {
        GitHubRelease latesGitHubRelease = getLatestGitHubRelease();
        return latesGitHubRelease.getTagName();
    }

    private static GitHubRelease getLatestGitHubRelease() throws RemoteVersionCheckFailedException {

        String url = GITHUB_API_BASE_URL + OWNER + "/" + REPO_NAME + LATEST_RELEASE_PATH;

        try {
            String apiResponseString = sendGetRequest(url);
            Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
            return gson.fromJson(apiResponseString, GitHubRelease.class);
        } catch (ExternalConnectionException e) {
            throw new RemoteVersionCheckFailedException(e);
        }

    }

    private static String sendGetRequest(String url) throws ExternalConnectionException {

        try {

            ConnectionConfig connectionConfig = ConnectionConfig
                .custom()
                .setConnectTimeout(CONNECTION_TIMEOUT, TIMEOUT_UNIT)
                .setSocketTimeout(SOCKET_TIMEOUT, TIMEOUT_UNIT)
                .build();

            CloseableHttpClient httpClient = HttpClientBuilder
                .create()
                .setConnectionManager(
                    PoolingHttpClientConnectionManagerBuilder
                        .create()
                        .setDefaultConnectionConfig(connectionConfig)
                        .build()
                )
                .build();
            HttpGet request = new HttpGet(url);
            request.addHeader("Accept", "application/vnd.github.v3+json"); // Specify GitHub API version
            return httpClient.execute(request, response -> {
                response.getEntity();
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            });
        } catch (IOException e) {
            throw new ExternalConnectionException(e);
        }

    }

    
    // https://api.github.com/repos/smals-jy/evs/releases/latest
    private static class GitHubRelease {
        private String tag_name;

        public String getTagName() {
            return tag_name;
        }

    }
}
