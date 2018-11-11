package com.frogdesign.smartshield;

import android.app.NotificationManager;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.fasterxml.jackson.databind.JsonNode;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.bot.SlackbotClient;
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

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    private View alertView;
    Fragment homeFragment;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar_top);
//        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("SmartShield");

        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorSafe)));
        actionBar.setHomeButtonEnabled(true);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        alertView = inflater.inflate(R.layout.alert_item, null);

        AWSMobileClient.getInstance().initialize(this).execute();

        mHandler = new Handler();

        runSlack();

        Bundle bundle = new Bundle();
        bundle.putString("traffic", "From Activity blabla");

        homeFragment = new HomeFragment();
        homeFragment.setArguments(bundle);

        loadFragment(homeFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favorite) {
            Toast.makeText(MainActivity.this, "Action clicked", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendNotification(View view) {

        //Get an instance of NotificationManager//

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.);
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        // Gets an instance of the NotificationManager service//

        NotificationManager mNotificationManager =

                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // When you issue multiple notifications about the same type of event,
        // it’s best practice for your app to try to update an existing notification
        // with this new information, rather than immediately creating a new notification.
        // If you want to update this notification at a later date, you need to assign it an ID.
        // You can then use this ID whenever you issue a subsequent notification.
        // If the previous notification is still visible, the system will update this existing notification,
        // rather than create a new one. In this example, the notification’s ID is 001//

        NotificationManager.notify().

                mNotificationManager.notify(001, mBuilder.build());
    }


    void runSlack() {
        new Thread(new Runnable() {

            SlackWebApiClient mWebApiClient;
            SlackRealTimeMessagingClient mRtmClient;

            public void run() {
                Looper.prepare();
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
                        System.out.println("File create");
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
                // mWebApiClient.postMessage("sandbox", "Hello from Android");
                Looper.loop();
            }
        }).start();
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        switch (menuItem.getItemId()) {
            case R.id.navigation_activity:
                fragment = homeFragment;
                break;
            case R.id.navigation_devices:
                fragment = new DevicesFragment();
                break;
            case R.id.navigation_users:
                fragment = new UsersFragment();
                break;
            case R.id.navigation_more:
                //fragment = new MoreFragment();

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Suspicious activity")
                        .setCancelable(false)
                        .setNegativeButton("Check", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // loadFragment(new AlertFragment());

                                alertDialogBuilder.setMessage("An unusual activity detected.\nIP address: 124.56.78.110\nLocation: New York, NY")
                                        .setView(alertView)
                                        .setCancelable(false)
                                        .setNegativeButton("Block user's access", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        })
                                        .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alert1 = alertDialogBuilder.create();
                                alert1.setTitle("AT RISK");
                                alert1.show();

                            }
                        })
                        .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = alertDialogBuilder.create();
                alert.setTitle("ALERT!");
                alert.show();
                break;
        }
        return loadFragment(fragment);
    }
}
