package laiden.fanfiction;

import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import laiden.fanfiction.project.Story;

public class MainActivity extends AppCompatActivity {
    StoryView storyView;
    public static MainActivity instance;
    public static String package_name;
    public static float density;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }
    private void init(){
        instance = this;
        package_name = getApplicationContext().getPackageName();
        density = getResources().getDisplayMetrics().density;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        storyView = new StoryView(this);
        setContentView(storyView);

        StoryManager.deleteStory("test");
        StoryManager.createStory("test");
        Story test = StoryManager.loadStory("test");

        storyView.play(test);

    }

    @Override
    protected void onResume(){
        super.onResume();
        setTaskColor();
    }

    @Override
    protected void onPause(){
        super.onPause();
        setTaskColor();
    }

    private void setTaskColor(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bitmap i = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            String s = storyView.story != null ? getString(R.string.app_name) + ": " + storyView.story.name : getString(R.string.app_name);
            ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription(s, i, Utils.darker(Color.parseColor(storyView.s.background), 0.25f));
            MainActivity.instance.setTaskDescription(taskDesc);
        }
    }
}
