package com.example.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ReceiveMessageActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();
        String message = intent.getStringExtra("com.example.sendmessage.MESSAGE");
        String [] messages = message.split(" ");
        String output = "Used " + messages[0] + " seconds. \n";
        if (messages[1].equals("lost"))
            output += "You lost.";
        else
            output += "You won. \n Good job!";

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(output);
    }

    public void backToMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
