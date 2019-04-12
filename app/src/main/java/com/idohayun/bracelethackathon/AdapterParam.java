package com.idohayun.bracelethackathon;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterParam extends ArrayAdapter {
    private List<String> data;
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private Context context;
    private static final String TAG = "AdapterParam";
    private final int patient;

    public AdapterParam(Context context, int resource, List<String> data, int patient) {
        super(context, resource);
        this.data = data;
        this.context = context;
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.patient = patient;
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
        
        viewHolder.moreImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPopupWindow(v);
                Log.d(TAG, "onClick: more description");
            }
        });
        
        viewHolder.delImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue queue = Volley.newRequestQueue(context);
                Map<String, String> map = new HashMap<>();
                map.put("ID", Integer.toString(patient));
                map.put("Description", data.get(position));
                Log.d(TAG, "onDateSet: " + map.toString());
                final JSONObject jsonObject = new JSONObject(map);
                Log.d(TAG, "updateListOfData: jsonObject: " + jsonObject.toString());
                JsonObjectRequest request = new JsonObjectRequest(
                        Request.Method.POST, // the request method
                        ServerManager.DeleteDisease, jsonObject,
                        new Response.Listener<JSONObject>() { // the response listener
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, "onResponse: " + response.toString());
                                try {
                                    if (response.getString("status").equals("true")) {
                                        Log.d(TAG, "onResponse: successed");
                                        notifyDataSetChanged();
                                    } else {
                                        Log.d(TAG, "onResponse: " + response.getString("data"));
                                        Toast.makeText(context, "error from server!", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() { // the error listener
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "onErrorResponse: error " + error.toString());
                                Toast.makeText(context, "Oops! Got error from server!", Toast.LENGTH_SHORT).show();
                            }
                        });
                queue.add(request);
                Log.d(TAG, "onClick: delete this...");
            }
        });
        

        return convertView;
    }

    private void displayPopupWindow(View anchorView) {
        PopupWindow popup = new PopupWindow(context);
        View layout = layoutInflater.inflate(R.layout.more_details, null);
        popup.setContentView(layout);
        // Set content width and height
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        // Show anchored to button
        //popup.setBackgroundDrawable(new BitmapDrawable(context.getResources(),));
        popup.showAsDropDown(anchorView);
    }

    private class ViewHolder {
        final TextView description;
        final ImageView delImage, moreImage;

        ViewHolder(View view) {
            this.delImage = view.findViewById(R.id.image_delete_btn);
            this.moreImage = view.findViewById(R.id.image_more_description);
            this.description = view.findViewById(R.id.text_name_disease);
        }
    }
}
