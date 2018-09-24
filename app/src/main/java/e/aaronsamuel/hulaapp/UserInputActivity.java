package e.aaronsamuel.hulaapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class UserInputActivity extends AppCompatActivity {

    Button btn;
    EditText username;
    ImageView profilepic;

    String encoded;
    int GET_FROM_GALLERY;

    DatabaseReference databaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_input);

        isNetworkAvailable();

        databaseUser = FirebaseDatabase.getInstance().getReference("Users");

        username = findViewById(R.id.username);
        profilepic = findViewById(R.id.image_view);

        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GET_FROM_GALLERY = 1;

                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });

        btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getName();

                if(data()) {
                    startActivity(new Intent(UserInputActivity.this,MainActivity.class));
                    finish();
                }
            }
        });
    }

    protected void getName(){
        FirebaseUser userId = FirebaseAuth.getInstance().getCurrentUser();

        String user = username.getText().toString();
        String id = userId.getUid();

        SharedPreferences.Editor editor = getSharedPreferences("USERNAME", MODE_PRIVATE).edit();
        editor.putString("username",user);
        editor.putString("userid",id);
        editor.commit();
    }

    private boolean data(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String userIdInput = user.getUid();
        String usernameInput = username.getText().toString();
        String profileInput = encoded;

        if(!TextUtils.isEmpty(usernameInput)) {

            PushUserDb pushUser = new PushUserDb(userIdInput, usernameInput, profileInput);
            databaseUser.child(userIdInput).setValue(pushUser);

            return true;

        } else {
            Toast.makeText(this, "Please Complete all field",Toast.LENGTH_LONG).show();
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == this.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                profilepic.setImageBitmap(bitmap);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.WEBP, 25, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();

                encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
