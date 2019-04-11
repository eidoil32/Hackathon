package com.idohayun.bracelethackathon;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class EditDisease extends Activity {
    private List<String> diseases;

    public EditDisease() {

    }

    EditDisease(List<String> diseases) {
        this.diseases = diseases;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_diseases);

        EditText newDiseasesText = (EditText) findViewById(R.id.newDisease);
        String newDisease = newDiseasesText.toString();

        boolean isExist = false;
        if(diseases != null) {
            for (int i = 0; i < diseases.size(); i++) {
                if (newDisease == diseases.get(i)) {
                    Toast.makeText(this, "disease allready exists", Toast.LENGTH_LONG).show();
                    isExist = true;
                    break;
                }

            }
            if (!isExist) {
                diseases.add(newDisease);
            }
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int)(width*0.8),(int)(height*0.3));

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.x = 0;
        layoutParams.y = 100;

        getWindow().setAttributes(layoutParams);

    }

}
