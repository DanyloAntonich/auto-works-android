package autoworks.app.controller;

import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

import autoworks.app.R;

public class ExceptoinViewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exceptoin_view);

        TextView textView = (TextView)findViewById(R.id.tv_exception_view);
        String exception = getIntent().getStringExtra("error");
        textView.setText(exception);
    }

}
