package com.example.koffi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.koffi.R;
import com.google.firebase.auth.FirebaseAuth;

public class CancelOrderDialog extends Dialog {

    public Context context;

    public CancelOrderDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_cancel_order);

        TextView noBtn = findViewById(R.id.cancel_noBtn);
        TextView yesBtn = findViewById(R.id.cancel_yesBtn);

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
