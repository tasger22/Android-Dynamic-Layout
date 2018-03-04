package hu.bme.iit.dynamiclayout_prototype;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button numericButton = (Button) findViewById(R.id.numericButton);
        Button graphicButton = (Button) findViewById(R.id.graphicButton);

        numericButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent numericActivityIntent = new Intent(getApplicationContext(),NumericCodeActivity.class);
                startActivity(numericActivityIntent);
            }
        });

        graphicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent graphicActivityIntent = new Intent(getApplicationContext(),GraphicCodeActivity.class);
                startActivity(graphicActivityIntent);
            }
        });
    }
}
