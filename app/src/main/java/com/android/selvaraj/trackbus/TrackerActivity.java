package com.android.selvaraj.trackbus;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class TrackerActivity extends AppCompatActivity implements View.OnClickListener {
    private String email,password;
    int count=0;
    private final String TAG=TrackerActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST = 100;
    private TextInputEditText etEmail,etPassword;
    private TextInputLayout etem,etpas;
    private Button btnLogin;
    ProgressDialog progressLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        btnLogin.setOnClickListener(this);
        // Check GPS is enabled
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Check location permission is granted - if it is, start
        // the service, otherwise request the permission
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        etem = findViewById(R.id.et_til);
        etpas = findViewById(R.id.pass_til);
    }


    private void startTrackerService() {
        startService(new Intent(this,TrackerService.class));
        finish();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (getValidNames()) {
                    // progressLoading.show(this,"Please wait","Updating details",true,false);
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(
                            email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "firebase auth success");
                                startTrackerService();
                            } else {
                                // progressLoading.dismiss();
                                showInvalidAlert();
                                Log.d(TAG, "firebase auth failed");
                            }
                        }
                    });

                }
        }
    }

    private void showInvalidAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(TrackerActivity.this);
        builder.setTitle("Forgot Password?")
                .setMessage(" Firebase auth failed..Seems like you forgot your password..\n Please contact Your Admin for Details!!")
                .setNeutralButton(R.string.ok, null);
        builder.show();
        count =0;
    }

    private boolean getValidNames() {
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\\\.+[a-z]+";
        if(TextUtils.isEmpty(email)){
            etem.setEnabled(true);
            etem.setError("Invalid Email");
            return false;
        }
        if(password.length()<6){
            etpas.setEnabled(true);
            etpas.setError("Enter Valid password");
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Start the service when the permission is granted
        } else {
            finish();
        }
    }
}