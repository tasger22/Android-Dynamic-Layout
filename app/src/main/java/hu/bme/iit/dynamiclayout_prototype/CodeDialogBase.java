package hu.bme.iit.dynamiclayout_prototype;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;

import java.security.InvalidParameterException;

import hu.bme.iit.dynamiclayout_prototype.MainActivity.CodeResolveDifficulty;

import static android.view.WindowManager.LayoutParams.FLAG_LOCAL_FOCUS_MODE;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
import static android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;

//Class which provides all the necessary components for a CodeActivity
public abstract class CodeDialogBase extends AlertDialog {

    private String code; //Security code for the CodeActivities
    private CodeResolveDifficulty currentDifficulty;
    private boolean isTestMode;
    private int tries = 2; //How many times you can try to input the code until it rejects input
    private int fails; //(Only in TestMode) Counting how many times you failed to input the right code
    private long testStartTime;
    private int initialTries;
    private boolean isCodeUserCode;
    private String userCode; //Custom security code by the user (encrypted) TODO: Actually make it encrypted or never use it only SharedPref
    private boolean wasStartedByBroadcastReceiver = false;
    private CryptClass decrypter = new CryptClass();

    protected CodeDialogBase(@NonNull Context context, boolean wasStartedByBroadcastReceiver) {
        super(context,R.style.AppTheme);
        this.wasStartedByBroadcastReceiver = wasStartedByBroadcastReceiver;
        if(wasStartedByBroadcastReceiver){
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.type = TYPE_SYSTEM_ERROR;
            params.dimAmount = 0.0F; // transparent
            params.gravity = Gravity.BOTTOM;
            getWindow().setAttributes(params);
            getWindow().setFlags(FLAG_SHOW_WHEN_LOCKED | FLAG_NOT_TOUCH_MODAL | FLAG_LOCAL_FOCUS_MODE, 0xffffff);
            setCancelable(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //Method to initialize all the private variables from the SharedPreferences
    protected void initialSetup() throws Exception {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());

        if(wasStartedByBroadcastReceiver)    isTestMode = false;
        else isTestMode = settings.getBoolean(SettingsActivity.KEY_PREF_TESTMODE,false);

        currentDifficulty = MainActivity.getCodeResolveDifficultyFromString(settings.getString(SettingsActivity.KEY_PREF_DIFFICULTY,"EASY"));
        isCodeUserCode = settings.getBoolean(SettingsActivity.KEY_PREF_USERCODE,false);
        userCode = settings.getString(getContext().getString(R.string.encrypted_code_key),CryptClass.byteArrayToHexString(decrypter.encrypt("0000")));

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

    //Unique implementation for any CodeActivity to generate a random security code
    protected abstract void setCodeToRandom();

    protected abstract void compareCodeToInput(String input);

    //Used when the user is in test mode where we have to compile the result screen
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
        Intent resultIntent = new Intent(getContext(),TestResultActivity.class);

        resultIntent.putExtra(getContext().getString(R.string.diff_key),currentDifficulty)
                    .putExtra(getContext().getString(R.string.code_length_key),code.length())
                    .putExtra(getContext().getString(R.string.comp_time_key),compTimeText)
                    .putExtra(getContext().getString(R.string.comp_time_sec_key),(int)timeDifference/1000)
                    .putExtra(getContext().getString(R.string.fails_key),fails);

        getOwnerActivity().startActivity(resultIntent);
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
        setTries(tries-1);
    }

    protected boolean isTestMode(){
        return isTestMode;
    }

    protected CodeResolveDifficulty getCurrentDifficulty(){
        return currentDifficulty;
    }
    protected void setCode(String code) {
        try {
            byte[] newCodeBytes = decrypter.encrypt(code);
            String newCodeStr = CryptClass.byteArrayToHexString(newCodeBytes);
            this.code = newCodeStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected String getCode() {
        try {
            String decryptedCode = new String(decrypter.decrypt(userCode));
            //Toast.makeText(this,decryptedCode,Toast.LENGTH_SHORT).show();
            return decryptedCode.trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String ("");
    }

    protected boolean isCodeNotUserCode(){ return !isCodeUserCode; }

    protected void setCodeToUserCode() { code = userCode; }

    protected boolean wasStartedByBroadcastReceiver(){return wasStartedByBroadcastReceiver;}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0
                && wasStartedByBroadcastReceiver) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
