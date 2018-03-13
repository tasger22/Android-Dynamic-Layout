package hu.bme.iit.dynamiclayout_prototype;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import hu.bme.iit.dynamiclayout_prototype.MainActivity.*;

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

        Bundle extras = getIntent().getExtras();

        if(extras == null){
            difficultyResultText.setText("");
            codeLengthResultText.setText("");
            completionTimeResultText.setText("");
            failsResultText.setText("");
            finalScoreResultText.setText("");
        }
        else{
            CodeResolveDifficulty difficultyResult = (CodeResolveDifficulty)extras.getSerializable("difficulty");
            int codeLengthResult = extras.getInt("code length");
            String completionTimeResult = extras.getString("completion time");
            int failsResult = extras.getInt("fails");

            String diffLocalizedString = getLocalizedDifficultyString(difficultyResult);
            int completionTimeInSecs = extras.getInt("sec completion time"); //only for performance calculation


            difficultyResultText.setText(diffLocalizedString);
            codeLengthResultText.setText(extras.getInt("code length"));
            completionTimeResultText.setText(extras.getString("completion time"));
            failsResultText.setText(extras.getInt("fails"));
        }

    }

    private String getLocalizedDifficultyString(CodeResolveDifficulty difficulty){
        switch(difficulty){
            case EASY: return getString(R.string.easy_difficulty);
            case HARD: return getString(R.string.hard_difficulty);
            case EVIL: return getString(R.string.evil_difficulty);
            default: return "Unknown difficulty";
        }
    }
}
