package e.aaronsamuel.hulaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import e.aaronsamuel.hulaapp.R;

public class AccountActivity extends AppCompatActivity {

    Button btn;
    EditText username;
    ImageView profilepic;

    String encoded;
    String picCode;
    int GET_FROM_GALLERY;

    DatabaseReference databaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        SharedPreferences preferences = getSharedPreferences("USERNAME", MODE_PRIVATE);
        String userName = preferences.getString("username",null);
        final String userId = preferences.getString("userid",null);

        databaseUser = FirebaseDatabase.getInstance().getReference("Users");

        username = findViewById(R.id.username);
        profilepic = findViewById(R.id.image_view);

        //GET PROFILE PIC BASE64 FROM DB
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                picCode = dataSnapshot.child(userId).child("profilepic").getValue(String.class);

                //SET PROFILE PICTURE INSIDE DRAWER
                if (!TextUtils.isEmpty(picCode)) {
                    ImageView imageView = findViewById(R.id.image_view);
                    try {
                        byte[] data = Base64.decode(picCode, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(data, 0, data.length);

                        imageView.setImageBitmap(decodedByte);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

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
                data();
                startActivity(new Intent(AccountActivity.this, MainActivity.class));
            }
        });
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
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();

                encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
