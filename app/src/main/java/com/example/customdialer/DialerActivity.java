package com.example.customdialer;

import static android.Manifest.permission.CALL_PHONE;
import static android.telecom.TelecomManager.ACTION_CHANGE_DEFAULT_DIALER;
import static android.telecom.TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.PermissionChecker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.customdialer.databinding.ActivityCallBinding;
import com.example.customdialer.databinding.ActivityDialerBinding;

import java.net.URI;

import kotlin.collections.ArraysKt;

public class DialerActivity extends AppCompatActivity {

    ActivityDialerBinding binding;
    EditText etPhoneNumber;
    public static int REQUEST_PERMISSION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDialerBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        etPhoneNumber = binding.etPhoneNumber;

        if (getIntent() != null && getIntent().getData() != null) {
            etPhoneNumber.setText(getIntent().getData().getSchemeSpecificPart());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        offerReplacingDefaultDialer();

        etPhoneNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                makeCall();
                return true;
            }
        });
    }

    private void offerReplacingDefaultDialer() {
        TelecomManager telecomManager = (TelecomManager) getSystemService(TELECOM_SERVICE);

        if (!getPackageName().equals(telecomManager.getDefaultDialerPackage())) {
            Intent intent = new Intent(ACTION_CHANGE_DEFAULT_DIALER)
                    .putExtra(EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, getPackageName());
            startActivity(intent);
        }
    }

    private void makeCall() {
        if (PermissionChecker.checkSelfPermission(this, CALL_PHONE) == PERMISSION_GRANTED) {
            Uri uri = Uri.parse("tel:" + etPhoneNumber.getText().toString().trim());
            startActivity(new Intent(Intent.ACTION_CALL, uri));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION && ArraysKt.contains(grantResults,PERMISSION_GRANTED)){
            makeCall();
        }
    }
}