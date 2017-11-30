package com.example.alecmcturk.zendesktest;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import io.fabric.sdk.android.Fabric;

import com.zendesk.sdk.support.ContactUsButtonVisibility;
import com.zopim.android.sdk.api.ZopimChat;

import com.zendesk.logger.Logger;
import com.zendesk.sdk.feedback.ui.ContactZendeskActivity;
import com.zendesk.sdk.model.access.AnonymousIdentity;
import com.zendesk.sdk.model.request.CreateRequest;
import com.zendesk.sdk.network.RequestProvider;
import com.zendesk.sdk.network.impl.ZendeskConfig;
import com.zendesk.sdk.support.SupportActivity;
import com.zendesk.service.ErrorResponse;
import com.zendesk.service.ZendeskCallback;
import com.zopim.android.sdk.model.VisitorInfo;
import com.zopim.android.sdk.prechat.PreChatForm;
import com.zopim.android.sdk.prechat.ZopimChatActivity;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    // just sticking some user info here for convenience
    String loggedInUserName = "Zoe Doe";
    String loggedInUserEmail = "zoedoe@example.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ZopimChat.init(getString(R.string.chat_account_key));

        Logger.setLoggable(BuildConfig.DEBUG);
        initializeZendesk();

        findViewById(R.id.launch_help_center_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Filter to only show "Maintenance Manual" section, remove the ContactUs button and article voting
                new SupportActivity.Builder()
                        .withArticlesForSectionIds(115000797632L)
                        .withContactUsButtonVisibility(ContactUsButtonVisibility.OFF)
                        .withArticleVoting(false)
                        .show(MainActivity.this);
            }
        });
        findViewById(R.id.launch_submit_ticket_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go straight into the form to create a new ticket
                ContactZendeskActivity.startActivity(MainActivity.this, null);
             }
        });

        findViewById(R.id.automatic_ticket_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go straight into the form to create a new ticket
                createAutoTicket();
            }
        });


        findViewById(R.id.launch_chat_button).setOnClickListener(new View.OnClickListener() {
            //Start a basic chat session
            @Override
            public void onClick(View v) {

                VisitorInfo visitorInfo = new VisitorInfo.Builder()
                        .name(loggedInUserName)
                        .email(loggedInUserEmail)
                        .phoneNumber("0877777777")
                        .build();
                ZopimChat.setVisitorInfo(visitorInfo);
                ZopimChatActivity.startActivity(MainActivity.this, null);
            }
        });
    }

    private void initializeZendesk() {
        // Initialize the Support SDK with your Zendesk Support subdomain, mobile SDK app ID, and client ID.
        // Get these details from your Zendesk Support dashboard: Admin -> Channels -> Mobile SDK
        ZendeskConfig.INSTANCE.init(getApplicationContext(),
                getString(R.string.com_zendesk_sdk_url),
                getString(R.string.com_zendesk_sdk_identifier),
                getString(R.string.com_zendesk_sdk_clientIdentifier));


        // Authenticate anonymously as a Zendesk Support user
        ZendeskConfig.INSTANCE.setIdentity(
                new AnonymousIdentity.Builder()
                        .withNameIdentifier(loggedInUserName)
                        .withEmailIdentifier(loggedInUserEmail)
                        .build()
        );
    }

    private void createAutoTicket() {
        // Simple test of the API to create a pre-populated request automatically and popup a success message
        RequestProvider provider = ZendeskConfig.INSTANCE.provider().requestProvider();
        CreateRequest request = new CreateRequest();

        request.setSubject(loggedInUserName+ " needs help");
        request.setDescription(loggedInUserName + " has issued a request for immediate assistance");
        request.setTags(Arrays.asList("fu", "bar", "emergency"));

        provider.createRequest(request, new ZendeskCallback<CreateRequest>() {

            @Override
            public void onSuccess(CreateRequest createRequest) {
                Context context = getApplicationContext();
                CharSequence text = "Help Request Created!";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            @Override
            public void onError(ErrorResponse errorResponse) {
                // Log the error
                Logger.e("MyLogTag", errorResponse);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
