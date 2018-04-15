package hu.bme.iit.dynamiclayout_prototype;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.view.KeyEvent;

import java.security.InvalidParameterException;

import hu.bme.iit.dynamiclayout_prototype.MainActivity.CodeResolveDifficulty;

public abstract class CodeActivityBase extends AppCompatActivity  {

    private String code;
    private CodeResolveDifficulty currentDifficulty;
    private boolean isTestMode;
    private int tries = 2;
    private int fails;
    private long testStartTime;
    private int initialTries;
    private boolean isCodeUserCode;
    private String userCode;
    private boolean isBroadcastStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initialSetup(Bundle savedInstanceState) {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        Bundle b = getIntent().getExtras();
        isBroadcastStart = b.getBoolean("broadcastReceiverStart",false);

        if(isBroadcastStart)    isTestMode = false;
        else isTestMode = settings.getBoolean(SettingsActivity.KEY_PREF_TESTMODE,false);

        currentDifficulty = MainActivity.getCodeResolveDifficultyFromString(settings.getString(SettingsActivity.KEY_PREF_DIFFICULTY,"EASY"));
        isCodeUserCode = settings.getBoolean(SettingsActivity.KEY_PREF_USERCODE,false);
        userCode = settings.getString(SettingsActivity.KEY_PREF_CODEINPUT,"0000");
        while(userCode.length() < 4)
            userCode = "0" + userCode;



        /*if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                 currentDifficulty = CodeResolveDifficulty.EASY;
                 isTestMode = false;
                 isCodeUserCode = false;

            } else {
                currentDifficulty = (CodeResolveDifficulty) extras.get(getString(R.string.diff_key));
                isTestMode = extras.getBoolean(getString(R.string.test_mode_key));
                isCodeUserCode = extras.getBoolean(getString(R.string.randomized_code_key));
                if(isCodeUserCode)   userCode = extras.getString(getString(R.string.user_code_key));
            }
        } else {
            currentDifficulty = (CodeResolveDifficulty) savedInstanceState.getSerializable(getString(R.string.diff_key));
            isTestMode = savedInstanceState.getBoolean(getString(R.string.test_mode_key));
            isCodeUserCode = savedInstanceState.getBoolean(getString(R.string.randomized_code_key));
            if(isCodeUserCode)   userCode = savedInstanceState.getString(getString(R.string.user_code_key));
        }*/

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

    protected void setCode(String code) { this.code = code; }

    protected String getCode() {
        return code;
    }

    protected boolean isCodeUserCode(){ return isCodeUserCode; }

    protected void setCodeToUserCode() { code = userCode; }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0
                && isBroadcastStart) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
