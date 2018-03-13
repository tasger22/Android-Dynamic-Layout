package hu.bme.iit.dynamiclayout_prototype;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public abstract class CodeActivityBase extends AppCompatActivity {

    //TODO: encapsulate all protected fields and create setter/getter or inc/dec for them
    protected String code;
    protected MainActivity.CodeResolveDifficulty currentDifficulty;
    protected boolean isTestMode;
    protected int tries = 2;
    protected int fails;
    private long testStartTime;
    protected int initialTries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initialSetup(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                 currentDifficulty = MainActivity.CodeResolveDifficulty.EASY;
                 isTestMode = false;
            } else {
                currentDifficulty = (MainActivity.CodeResolveDifficulty) extras.get(getString(R.string.diff_key));
                isTestMode = extras.getBoolean(getString(R.string.test_mode_key));
            }
        } else {
            currentDifficulty = (MainActivity.CodeResolveDifficulty) savedInstanceState.getSerializable(getString(R.string.diff_key));
            isTestMode = savedInstanceState.getBoolean(getString(R.string.test_mode_key));
        }

        if(isTestMode){
            tries = initialTries = 10;
            fails = 0;
            testStartTime = System.currentTimeMillis();
        }
    }

    protected abstract void setCodeToRandom();
    protected abstract void compareCodeToInput(String input);

    protected void compileResults() {
        long timeDifference = System.currentTimeMillis() - testStartTime;
        long tempVar;

        long minutes = timeDifference / 60000;
        tempVar = timeDifference % 60000;

        long seconds = tempVar / 1000;
        String secondsString = Long.toString(seconds);
        if(secondsString.length() < 2)
            secondsString = "0" + secondsString;

        long milliseconds = timeDifference % 1000;
        String milliString = Long.toString(milliseconds);
        while(milliString.length() < 3)
            milliString = "0" + milliString;

        String compTimeText = minutes + ":" + secondsString + "." + milliString ;
        Toast.makeText(getApplicationContext(),compTimeText,Toast.LENGTH_LONG).show();
        //TODO: Make a new activity and start it as an Intent here with the necessary extra content
        Intent resultIntent = new Intent(this,TestResultActivity.class);

        resultIntent.putExtra(getString(R.string.diff_key),currentDifficulty)
                    .putExtra(getString(R.string.code_length_key),code.length())
                    .putExtra(getString(R.string.comp_time_key),compTimeText)
                    .putExtra(getString(R.string.comp_time_sec_key),(int)timeDifference/1000)
                    .putExtra(getString(R.string.fails_key),fails);

        startActivity(resultIntent);
    }
}
