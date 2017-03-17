package laiden.fanfiction;

import android.app.Activity;
import android.app.Dialog;
import android.media.Image;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Undust4able on 01.03.2017.
 */

public class DialogWindow{
    public void showDialog(Activity activity, String msg){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);

        //TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        //text.setText(msg);

        ImageButton back = (ImageButton) dialog.findViewById(R.id.button_back);
                    back.setOnClickListener(new View.OnClickListener() {
                    @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

        dialog.show();

    }
}
