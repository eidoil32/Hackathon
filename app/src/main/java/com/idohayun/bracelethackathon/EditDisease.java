package com.idohayun.bracelethackathon;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class  editdisease extends DialogFragment
{
List<String> diseases;

    public editdisease(List<String> diseases) {
        this.diseases = diseases;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.editdisease,container,false);
        EditText newdiseasetxt=(EditText)view.findViewById(R.id.newDisease);
        String newDisease=newdiseasetxt.toString();
        boolean isExist=false;
        for(int i=0;i<diseases.size();i++)
        {
            if(newDisease==diseases.get(i))
            {
                Toast.makeText(getContext(),"disease allready exists",Toast.LENGTH_LONG).show();
                isExist=true;
                break;
            }

        }
        if(!isExist)
        {
            diseases.add(newDisease);
        }
        return view;
    }

    @Override
    public void onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
}
