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
            CodeResolveDifficulty difficultyResult = (CodeResolveDifficulty)extras.getSerializable(getString(R.string.diff_key));
            int codeLengthResult = extras.getInt(getString(R.string.code_length_key));
            String completionTimeResult = extras.getString(getString(R.string.comp_time_key));
            int failsResult = extras.getInt(getString(R.string.fails_key));

            String diffLocalizedString = getLocalizedDifficultyString(difficultyResult);
            int completionTimeInSecs = extras.getInt(getString(R.string.comp_time_sec_key)); //only for performance calculation

            int performance = (int)((difficultyResult.getDifficultyMultiplier() * completionTimeInSecs * (codeLengthResult/2) )/ ((failsResult*0.5)+1));


            difficultyResultText.setText(diffLocalizedString);
            codeLengthResultText.setText(Integer.toString(codeLengthResult));
            completionTimeResultText.setText(completionTimeResult);
            failsResultText.setText(Integer.toString(failsResult));
            finalScoreResultText.setText(Integer.toString(performance));
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
