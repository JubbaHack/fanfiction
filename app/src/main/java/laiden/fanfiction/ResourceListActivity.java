package laiden.fanfiction;

import android.app.Activity;
import android.app.ListActivity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ResourceListActivity extends ListActivity {

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        String[] values = new String[] { "horse.png" };
        ResourceListArrayAdapter adapter = new ResourceListArrayAdapter(this, values);
        setListAdapter(adapter);
    }
    private static class ResourceListArrayAdapter extends ArrayAdapter<String>{
            private final Activity context;
            private final String[] resources;

            public ResourceListArrayAdapter(Activity context, String[] resources) {
                super(context, R.layout.rowlayout, resources);
                this.context = context;
                this.resources = resources;
            }

            // Класс для сохранения во внешний класс и для ограничения доступа
            // из потомков класса
            static class ViewHolder {
                public ImageView icon;
                public TextView title;
                public TextView summary;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // ViewHolder буферизирует оценку различных полей шаблона элемента

                ViewHolder holder;
                // Очищает сущетсвующий шаблон, если параметр задан
                // Работает только если базовый шаблон для всех классов один и тот же
                View rowView = convertView;
                if (rowView == null) {
                    LayoutInflater inflater = context.getLayoutInflater();
                    rowView = inflater.inflate(R.layout.rowlayout, null, true);
                    holder = new ViewHolder();
                    holder.title = (TextView) rowView.findViewById(R.id.title);
                    holder.summary = (TextView) rowView.findViewById(R.id.summary);
                    holder.icon = (ImageView) rowView.findViewById(R.id.icon);
                    rowView.setTag(holder);
                } else {
                    holder = (ViewHolder) rowView.getTag();
                }

                holder.title.setText(resources[position]);
                holder.summary.setText("Summary for " + resources[position]);
                holder.icon.setImageDrawable(StoryView.drawables.get(resources[position]));

                return rowView;
            }
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        Toast.makeText(this, item + " выбран", Toast.LENGTH_LONG).show();
    }
}