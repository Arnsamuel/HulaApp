package e.aaronsamuel.hulaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NameInputActivity extends AppCompatActivity {

    Button btn;
    EditText username;

    DatabaseReference databaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_input);

        databaseUser = FirebaseDatabase.getInstance().getReference("Users");

        username = findViewById(R.id.username);

        btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getName();

                if(data()) {
                    startActivity(new Intent(NameInputActivity.this,MainActivity.class));
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

        if(!TextUtils.isEmpty(usernameInput)) {

            PushUserDb pushUser = new PushUserDb(userIdInput, usernameInput);
            databaseUser.child(userIdInput).setValue(pushUser);

            return true;

        } else {
            Toast.makeText(this, "Please Complete all field",Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
