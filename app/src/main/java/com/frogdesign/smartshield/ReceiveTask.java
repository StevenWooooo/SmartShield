package com.frogdesign.smartshield;

import android.os.AsyncTask;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.exception.SlackResponseErrorException;
import allbegray.slack.rtm.CloseListener;
import allbegray.slack.rtm.Event;
import allbegray.slack.rtm.EventListener;
import allbegray.slack.rtm.FailureListener;
import allbegray.slack.rtm.SlackRealTimeMessagingClient;
import allbegray.slack.type.Authentication;
import allbegray.slack.type.Channel;
import allbegray.slack.type.User;
import allbegray.slack.webapi.SlackWebApiClient;
import allbegray.slack.webhook.SlackWebhookClient;


public class ReceiveTask extends AsyncTask<Void, Void, SlackWebApiClient> {

    SlackWebApiClient mWebApiClient;
    SlackRealTimeMessagingClient mRtmClient;

    protected SlackWebApiClient doInBackground(Void... v) {
        mWebApiClient = SlackClientFactory.createWebApiClient(
                "xoxb-425943026439-475474101397-vJHt9aRjQC6XEuYDxlpVagZS");
        //SlackWebhookClient webhookClient = SlackClientFactory.createWebhookClient(
        //        "https://hooks.slack.com/services/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");
        //SlackbotClient slackbotClient = SlackClientFactory.createSlackbotClient(
            //        "xoxb-425943026439-475474101397-vJHt9aRjQC6XEuYDxlpVagZS");

        String webSocketUrl = mWebApiClient.startRealTimeMessagingApi().findPath("url").asText();
        System.out.println("socket url = " + webSocketUrl );
        mRtmClient = new SlackRealTimeMessagingClient(webSocketUrl);

        mRtmClient.addListener(Event.HELLO, new EventListener() {
            @Override
            public void onMessage(JsonNode message) {
                Authentication authentication = mWebApiClient.auth();
                System.out.println("Team name: " + authentication.getTeam());
                System.out.println("User name: " + authentication.getUser());
            }
        });

        mRtmClient.addListener(Event.MESSAGE, new EventListener() {
            @Override
            public void onMessage(JsonNode message) {
                String channelId = message.findPath("channel").asText();
                String userId = message.findPath("user").asText();
                String text = message.findPath("text").asText();

                if (userId != null) {
                    Channel channel;
                    try {
                        channel = mWebApiClient.getChannelInfo(channelId);
                    } catch (SlackResponseErrorException e) {
                        channel = null;
                    }
                    User user = mWebApiClient.getUserInfo(userId);
                    String userName = user.getName();

                    System.out.println("Channel id: " + channelId);
                    System.out.println("Channel name: " + (channel != null ? "#" + channel.getName() : "DM"));
                    System.out.println("User id: " + userId);
                    System.out.println("User name: " + userName);
                    System.out.println("Text: " + text);

                    // Copy cat
                    mWebApiClient.meMessage(channelId, userName + ": " + text);
                }
            }
        });

        mRtmClient.addCloseListener(new CloseListener() {

            @Override
            public void onClose() {
                System.out.println("Connection closed");
            }
        });

        mRtmClient.addFailureListener(new FailureListener() {

            @Override
            public void onFailure(Throwable t) {
                Exception e = (Exception) t;

                System.out.println("Failure message: " + e.getMessage());
            }
        });

        mRtmClient.connect();
        mWebApiClient.postMessage("sandbox", "Hello from Android");

        return mWebApiClient;
    }

    protected void onProgressUpdate(Integer... progress) {

    }

    protected void onPostExecute(SlackWebApiClient mWebApiClient) {

    }
}
