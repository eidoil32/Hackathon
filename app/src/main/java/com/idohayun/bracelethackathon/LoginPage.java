package com.idohayun.bracelethackathon;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class LoginPage extends Fragment {

    public static boolean login_page_login_button(String name,String password)
    {
       return userExistance(name,password);
    }
    public static void ErrorToast(Context context)
    {
        Toast.makeText(context,"wrong password or username", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_screen,container,false);
        EditText username=(EditText)view.findViewById(R.id.login_page_user_name);
        EditText userPassword=(EditText)view.findViewById(R.id.login_page_user_name);
        if(username==null||userPassword==null)
            ErrorToast(view.getContext());
        if(!(login_page_login_button(username.getText().toString(),userPassword.getText().toString())));
        {
            ErrorToast(view.getContext());
        }

        return view;
    }
}
