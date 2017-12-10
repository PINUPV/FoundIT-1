package Comercio;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import foundit.foundit.R;

/**
 * Created by erica on 21/11/2017.
 */

public class ComercioAdapater extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<Comercio> comercio;

    public ComercioAdapater(Activity activity, ArrayList<Comercio> items) {
        this.activity = activity;
        this.comercio = items;
    }

    @Override
    public int getCount() {
        return comercio.size();
    }

    public void clear() {
        comercio.clear();
    }

    public void addAll(ArrayList<Comercio> category) {
        for (int i = 0; i < category.size(); i++) {
            comercio.add(category.get(i));
        }
    }

    @Override
    public Object getItem(int arg0) {
        return comercio.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.fragment_frag_favoritos, null);
        }

        Comercio com = comercio.get(position);

        //TextView name = (TextView) v.findViewById(R.id.textcomer);
        //name.setText(com.getName());

        //ImageButton bt_like2 = (ImageButton) v.findViewById(R.id.bt_like2);
        //bt_like2.setImageDrawable(activity.getResources().getDrawable(R.drawable.likevacio));

        ImageView imagen = (ImageView) v.findViewById(R.id.imageView);
        imagen.setImageDrawable(activity.getResources().getDrawable(R.drawable.comercio));

        return v;
    }


}
