package hu.bme.iit.dynamiclayout_prototype;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class TestResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        TextView difficultyResultText = (TextView) findViewById(R.id.difficultyResultText);
        TextView codeLengthResultText = (TextView) findViewById(R.id.codeLengthResultText);
        TextView completionTimeResultText = (TextView) findViewById(R.id.completionTimeResultText);
        TextView failsResultText = (TextView) findViewById(R.id.failsResultText);
        TextView finalScoreResultText = (TextView) findViewById(R.id.finalScoreResultText);

        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();

            if(extras == null){
                difficultyResultText.setText("");
                codeLengthResultText.setText("");
                completionTimeResultText.setText("");
                failsResultText.setText("");
                finalScoreResultText.setText("");
            }

            else{
                

                difficultyResultText.setText(extras.getSerializable("difficulty").toString());
                codeLengthResultText.setText(extras.getInt("code length"));
                completionTimeResultText.setText(extras.getString("completion time"));
                failsResultText.setText(extras.getInt("fails"));

            }
        }

    }
}
