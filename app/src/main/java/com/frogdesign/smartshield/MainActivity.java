package com.frogdesign.smartshield;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.ViewPager;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    private MyApplication application;

    private View alertView;
    private Menu defaultMenu;

    HomeFragment homeFragment;
    Fragment devicesFragment;
    Fragment usersFragment;

    private  ViewPager viewPager;
    private MenuItem prevMenuItem;
    private BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        application = (MyApplication)this.getApplication();
        application.state = R.color.colorSafe;

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("SmartShield");
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorSafe)));
        actionBar.setHomeButtonEnabled(true);

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        alertView = inflater.inflate(R.layout.alert_item, null);

        AWSMobileClient.getInstance().initialize(this).execute();

        // runSlack();

//        Bundle bundle = new Bundle();
//        bundle.putString("traffic", "From Activity blabla");
//        homeFragment.setArguments(bundle);
        viewPager = findViewById(R.id.fragment_container);
        setupViewPager(viewPager);

        runNotification();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        homeFragment = new HomeFragment();
        devicesFragment = new DevicesFragment();
        //usersFragment = new UsersFragment();
        adapter.addFragment(homeFragment);
        adapter.addFragment(devicesFragment);
        //adapter.addFragment(usersFragment);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null)
                    prevMenuItem.setChecked(false);
                else
                    navigation.getMenu().getItem(0).setChecked(false);

                navigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navigation.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void openNotificationActivity() {
        Intent intent = new Intent(this, NotificationActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void setCount(Context context, String count) {
        MenuItem menuItem = defaultMenu.findItem(R.id.notification_button);
        LayerDrawable icon = (LayerDrawable) menuItem.getIcon();

        CountDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_count);
        if (reuse != null && reuse instanceof CountDrawable) {
            badge = (CountDrawable) reuse;
        } else {
            badge = new CountDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_count, badge);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        setCount(this, "4");
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        defaultMenu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.notification_button) {
//            Toast.makeText(MainActivity.this, "Action clicked", Toast.LENGTH_LONG).show();
            openNotificationActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    * Notification
    **/
    public void runNotification() {
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                try {
                    Thread.sleep(24000);
                } catch (InterruptedException e) {
                    return;
                }

                application.state = R.color.colorAlert;
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(application.state)));

                sendNotification();
                showAlert();
                Looper.loop();
            }
        }).start();
    }

    public void showAlert() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        final AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(this);

        LineChart mLineChart = alertView.findViewById(R.id.alert_chart);
        LimitLine upper_limit = new LimitLine(60, "suspicious");
        upper_limit.setLineWidth(2f);
        upper_limit.enableDashedLine(10f, 10f, 0f);
        upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upper_limit.setTextSize(10f);
        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(upper_limit);

        LineData lineData = homeFragment.getChartData();
        mLineChart.setData(lineData);

        alertDialogBuilder.setMessage("Increased network activity on WyzeCam")
                .setView(alertView)
                .setCancelable(false)
                .setNegativeButton("Block", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        homeFragment.setMeans(0);

                        alertDialogBuilder1.setTitle("WyzeCam has been blocked")
                                .setMessage("Next steps: \n 1. Change WyzeCam password \n 2. Change network password \n 3. Update firmware")
                                .setNegativeButton("Got it", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        application.state = R.color.colorSafe;
                                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(application.state)));
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert1 = alertDialogBuilder1.create();
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
    }

    private final String CHANNEL_ID = "default";
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void sendNotification() {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Get an instance of NotificationManager
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("Alert")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("We’ve noticed some unusual activity."))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(this);
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
                mWebApiClient.postMessage("sandbox", "Hello from Android");

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
        switch (menuItem.getItemId()) {
            case R.id.navigation_activity:
                viewPager.setCurrentItem(0);
                break;
            case R.id.navigation_devices:
                viewPager.setCurrentItem(1);
                break;
//            case R.id.navigation_users:
//                viewPager.setCurrentItem(2);
//                break;
        }
        return false;
    }
}
