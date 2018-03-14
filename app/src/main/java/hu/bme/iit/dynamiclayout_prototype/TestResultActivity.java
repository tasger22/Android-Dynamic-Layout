package hu.bme.iit.dynamiclayout_prototype;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import hu.bme.iit.dynamiclayout_prototype.MainActivity.*;

public class TestResultActivity extends AppCompatActivity implements KeyListener{

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

            int performance = (int)((difficultyResult.getDifficultyMultiplier() * 120/completionTimeInSecs * (codeLengthResult/2) )/ ((failsResult*0.5)+1));


            difficultyResultText.setText(diffLocalizedString);
            codeLengthResultText.setText(Integer.toString(codeLengthResult));
            completionTimeResultText.setText(completionTimeResult);
            failsResultText.setText(Integer.toString(failsResult));
            finalScoreResultText.setText(Integer.toString(performance));

            Button backButton = (Button) findViewById(R.id.backButton);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mainActivity = new Intent(getApplicationContext(),MainActivity.class);
                    mainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainActivity);
                }
            });
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(getApplicationContext(),MainActivity.class);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(setIntent);
    }

    @Override
    public int getInputType() {
        return 0;
    }

    @Override
    public boolean onKeyDown(View view, Editable text, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyUp(View view, Editable text, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyOther(View view, Editable text, KeyEvent event) {
        return false;
    }

    @Override
    public void clearMetaKeyState(View view, Editable content, int states) {

    }
}
