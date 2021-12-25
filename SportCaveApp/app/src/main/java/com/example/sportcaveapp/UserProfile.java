package com.example.sportcaveapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class UserProfile extends AppCompatActivity {

    ImageView ivProfile;
    TextView tvName;
    TextView tvUsername;
    TextView tvEmail;
    TextView tvFavSports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        ivProfile = (ImageView) findViewById(R.id.ivProfile);
        tvName = (TextView) findViewById(R.id.tvName);
        tvUsername = (TextView) findViewById(R.id.tvUsername);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvFavSports = (TextView) findViewById(R.id.tvFavSports);

        String username = getIntent().getExtras().getString("username").split("\\@")[1];
        Log.i("UserProfile", username);

        ParseUser user = null;
        try {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("username", username);
            user = query.find().get(0);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (user.getParseFile("profilePicture") != null) {
            Glide.with(this).load(user.getParseFile("profilePicture").getUrl()).into(ivProfile); }
        else {
            Glide.with(this).load(R.drawable.user).into(ivProfile); }
        tvName.setText(user.get("profileName").toString());
        tvUsername.setText("@"+user.getUsername());
        tvEmail.setText(user.getEmail());
        tvFavSports.setText(user.get("favSports").toString());
    }

}
