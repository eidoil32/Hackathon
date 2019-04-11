package com.idohayun.bracelethackathon;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class AdapterParam extends ArrayAdapter {
    private List<String> data;
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private Context context;

    public AdapterParam(Context context, int resource, List<String> data) {
        super(context, resource);
        this.data = data;
        this.context = context;
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.description.setText(data.get(position));

        viewHolder.description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String edited = viewHolder.description.getText().toString();
                if(!edited.isEmpty()) {
                    data.add(position,edited);
                    setNotifyOnChange(true);
                }
            }
        });

        return super.getView(position, convertView, parent);
    }

    private class ViewHolder {
        final EditText description;

        ViewHolder(View view) {
            this.description = view.findViewById(R.id.adapter_edit_text_parm);
        }
    }
}
