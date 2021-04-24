package com.example.grocery;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.rudderstack.android.sdk.core.RudderClient;
import com.rudderstack.android.sdk.core.RudderConfig;
import com.rudderstack.android.sdk.core.RudderLogger;

import java.util.UUID;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        String writeKey = "907e6509-8fd6-419e-bf07-af3ccbb8d06c";
        String host = "https://aixp-rudder-api.digitallab.id/025b0b33-5c2b-4406-ac4b-1e5aadf9b3c5/13df1ee6-04a3-4486-9666-f8a2a13196b4";
        RudderClient.setAnonymousId(UUID.randomUUID().toString());
        RudderClient rudderClient = RudderClient.getInstance(
                this,
                writeKey,
                new RudderConfig.Builder()
                        .withDataPlaneUrl(host)
                        .withControlPlaneUrl(host)
                        .withTrackLifecycleEvents(true)
                        .withRecordScreenViews(true)
                        .withLogLevel(RudderLogger.RudderLogLevel.VERBOSE)
                        .build()
        );
        RudderClient.setSingletonInstance(rudderClient);
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("app_config_aixp", Context.MODE_PRIVATE);

        SharedPreferences.Editor ed;
        if(!sharedPref.contains("initialized")){
            ed = sharedPref.edit();

            //Indicate that the default shared prefs have been set
            ed.putBoolean("initialized", true);

            //Set some default shared pref
            ed.putString("regisId", UUID.randomUUID().toString());
            ed.putString("installedFrom", "default");
            ed.putString("address", "Jalan Kemakmuran raya " + UUID.randomUUID().toString());

            ed.commit();
        }
    }
}
