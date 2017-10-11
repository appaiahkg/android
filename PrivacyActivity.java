package net.akg.com.deviceunlockdetection;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;


public class PrivacyActivity extends Activity {
    private WebView privacyContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        privacyContainer = (WebView)findViewById(R.id.privacyContainer);
        privacyContainer.loadUrl("file:///android_asset/privacypolicy.htm");
    }
}
