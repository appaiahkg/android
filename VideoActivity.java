package net.akg.com.deviceunlockdetection;

import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;


public class VideoActivity extends Activity {
    private  VideoView vid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        vid = (VideoView) findViewById(R.id.videoview);

        String path = "android.resource://"+getPackageName()+"/"+R.raw.video_file;
        vid.setVideoURI(Uri.parse(path));
        vid.setZOrderOnTop(true);
        MediaController control = new MediaController(this);
        control.setAnchorView(vid);
        vid.setMediaController(control);
        vid.setZOrderMediaOverlay(true);

        vid.requestFocus();
        vid.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                vid.setVisibility(View.VISIBLE);
            }
        });
        vid.setBackgroundColor(Color.TRANSPARENT);
        vid.start();
        vid.setZOrderOnTop(true);
    }
}
