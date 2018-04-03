package hu.bme.iit.dynamiclayout_prototype;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.security.InvalidParameterException;

import hu.bme.iit.dynamiclayout_prototype.MainActivity.CodeResolveDifficulty;

public abstract class CodeActivityBase extends AppCompatActivity {

    private String code;
    private CodeResolveDifficulty currentDifficulty;
    private boolean isTestMode;
    private int tries = 2;
    private int fails;
    private long testStartTime;
    private int initialTries;
    private boolean isCodeRandomized;
    private String userCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initialSetup(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                 currentDifficulty = CodeResolveDifficulty.EASY;
                 isTestMode = false;
                 isCodeRandomized = true;

            } else {
                currentDifficulty = (CodeResolveDifficulty) extras.get(getString(R.string.diff_key));
                isTestMode = extras.getBoolean(getString(R.string.test_mode_key));
                isCodeRandomized = extras.getBoolean(getString(R.string.randomized_code_key));
                if(!isCodeRandomized)   userCode = extras.getString(getString(R.string.user_code_key));
            }
        } else {
            currentDifficulty = (CodeResolveDifficulty) savedInstanceState.getSerializable(getString(R.string.diff_key));
            isTestMode = savedInstanceState.getBoolean(getString(R.string.test_mode_key));
            isCodeRandomized = savedInstanceState.getBoolean(getString(R.string.randomized_code_key));
            if(!isCodeRandomized)   userCode = savedInstanceState.getString(getString(R.string.user_code_key));
        }

        if(isTestMode){
            tries = initialTries = 10;
            fails = 0;
            testStartTime = System.currentTimeMillis();
        }

        else{
            tries = initialTries = 2;
            fails = 0;
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
        Intent resultIntent = new Intent(this,TestResultActivity.class);

        resultIntent.putExtra(getString(R.string.diff_key),currentDifficulty)
                    .putExtra(getString(R.string.code_length_key),code.length())
                    .putExtra(getString(R.string.comp_time_key),compTimeText)
                    .putExtra(getString(R.string.comp_time_sec_key),(int)timeDifference/1000)
                    .putExtra(getString(R.string.fails_key),fails);

        startActivity(resultIntent);
    }

    protected int getInitialTries() {
        return initialTries;
    }

    protected void incrementFails(){
        ++fails;
    }

    protected int getTries() {
        return tries;
    }

    protected void setTries(int tries) {
        try{
            if(tries < 0)   throw new InvalidParameterException("'tries' value cannot be negative");
        }
        catch (InvalidParameterException e){
            //TODO: write the catch clause
        }
        this.tries = tries;
    }

    protected void decrementTries(){
        --tries;
    }

    protected boolean isTestMode(){
        return isTestMode;
    }

    protected CodeResolveDifficulty getCurrentDifficulty(){
        return currentDifficulty;
    }

    protected void setCode(String code) {
        this.code = code;
    }

    protected String getCode() {
        return code;
    }
}
