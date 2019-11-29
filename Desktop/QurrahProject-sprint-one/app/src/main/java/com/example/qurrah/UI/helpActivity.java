package com.example.qurrah.UI;

import android.os.Bundle;
import android.view.MenuItem;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.Spanned;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.qurrah.R;

public class helpActivity extends AppCompatActivity {
    TextView twitter,email;
    Spanned twitterURL,emailURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        twitter = (TextView)findViewById(R.id.twitter);
        email = (TextView)findViewById(R.id.email);

        twitterURL = Html.fromHtml("<a href='https://www.twitter.com/QurrahApp/'>تويتر</a>");
        twitter.setMovementMethod(LinkMovementMethod.getInstance());
        twitter.setText(twitterURL);

        emailURL = Html.fromHtml("<a href='mailto:qurrah.app@gmail.com\'>البريد الالكتروني</a>");
        email.setMovementMethod(LinkMovementMethod.getInstance());
        email.setText(emailURL);

}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
