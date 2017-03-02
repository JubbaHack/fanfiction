package laiden.fanfiction;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Undust4able on 01.03.2017.
 */

public class ProjectSettingsWindow{
    public void showDialog(final Activity activity, String msg){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.project_settings);

        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        text.setText(msg);

        final TabHost tabhost = (TabHost) dialog.findViewById(R.id.tabhost);
        tabhost.setup();
        TabHost.TabSpec ts = tabhost.newTabSpec("tag1");
        ts.setContent(R.id.tab1);
        ts.setIndicator("First Tab");
        tabhost.addTab(ts);
        ts = tabhost.newTabSpec("tag2");
        ts.setContent(R.id.tab2);
        ts.setIndicator("Second Tab");
        tabhost.addTab(ts);
        ts= tabhost.newTabSpec("tag3");
        ts.setContent(R.id.tab3);
        ts.setIndicator("Third Tab");
        tabhost.addTab(ts);


        tabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {

                int i = tabhost.getCurrentTab();
                Toast.makeText(activity.getApplicationContext(), i + " ", Toast.LENGTH_SHORT).show();
            }
        });

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}