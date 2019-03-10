package hu.bme.iit.dynamiclayout_prototype;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Random;

import hu.bme.iit.dynamiclayout_prototype.MainActivity.CodeResolveDifficulty;

import static android.view.WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
import static android.view.WindowManager.LayoutParams.FLAG_LOCAL_FOCUS_MODE;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
import static android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
import static android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;


//Class which provides all the necessary components for a CodeActivity
public abstract class CodeDialogBase extends AlertDialog {

    private String code; //Security code for the CodeDialogs, encrypted
    private CodeResolveDifficulty currentDifficulty;
    private boolean isTestMode;
    private int tries = 2; //How many times you can try to input the code until it rejects input
    private int fails; //(Only in TestMode) Counting how many times you failed to input the right code
    private long testStartTime;
    private int initialTries;
    private boolean wasStartedByBroadcastReceiver = false;
    private CryptClass crypter = new CryptClass();
    private SharedPreferences settings;
    private Context ownerContext;

    private ArrayList<View> codeButtonList;

    protected CodeDialogBase(@NonNull Context context, boolean wasStartedByBroadcastReceiver, SharedPreferences customSharedPref) {
        super(context,R.style.AppTheme);
        ownerContext = context;
        this.wasStartedByBroadcastReceiver = wasStartedByBroadcastReceiver;
        settings = customSharedPref;
        if(wasStartedByBroadcastReceiver){
            WindowManager.LayoutParams params = getWindow().getAttributes();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                params.type = TYPE_APPLICATION_OVERLAY;
            }
            else{
                params.type = TYPE_SYSTEM_ERROR;
            }
            params.dimAmount = 0.0F; // transparent
            params.gravity = Gravity.BOTTOM;
            getWindow().setAttributes(params);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().addFlags(FLAG_LOCAL_FOCUS_MODE);
            }
            getWindow().addFlags(FLAG_SHOW_WHEN_LOCKED | FLAG_NOT_TOUCH_MODAL | FLAG_DISMISS_KEYGUARD);
            setCancelable(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //Method to initialize all the private variables from the SharedPreferences
    protected void initialSetup() throws Exception {

        if(wasStartedByBroadcastReceiver)    isTestMode = false;
        else isTestMode = settings.getBoolean(SettingsActivity.KEY_PREF_TESTMODE,false);

        currentDifficulty = MainActivity.getCodeResolveDifficultyFromString(settings.getString(SettingsActivity.KEY_PREF_DIFFICULTY,"EASY"));
        code = settings.getString(getContext().getString(R.string.encrypted_code_key),CryptClass.byteArrayToHexString(crypter.encrypt("0000")));

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

    protected void setCodeButtonList(int viewContainerId){
        ViewGroup viewContainer = findViewById(viewContainerId);
        codeButtonList = getChildrenViews(viewContainer);
    }

    protected void setCodeButtonList(ViewGroup viewContainer){
        codeButtonList = getChildrenViews(viewContainer);
    }

    private ArrayList<View> getChildrenViews(ViewGroup viewContainer){
        ArrayList<View> childrenViews = new ArrayList<>();
        if (viewContainer != null){
            for (int i = 0; i < viewContainer.getChildCount(); i++) {
                View childView = viewContainer.getChildAt(i);
                childrenViews.add(childView);
            }
        }
        return childrenViews;
    }

    protected void processCodeButtonPress(View view) {
        Button numberButton = (Button) view;
        EditText passwordLine = findViewById(R.id.passwordLine);

        passwordLine.append(numberButton.getText().toString());

        if(getCurrentDifficulty() == CodeResolveDifficulty.EVIL)
            randomizeButtons();
    }

    protected void randomizeButtons(){

    }

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
        int codeLength = 4;
        try{
            String decryptedStr = new String(crypter.decrypt(code));
            codeLength = decryptedStr.trim().length();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        resultIntent.putExtra(getContext().getString(R.string.diff_key),currentDifficulty)
                    .putExtra(getContext().getString(R.string.code_length_key),codeLength)
                    .putExtra(getContext().getString(R.string.comp_time_key),compTimeText)
                    .putExtra(getContext().getString(R.string.comp_time_sec_key),(int)timeDifference/1000)
                    .putExtra(getContext().getString(R.string.fails_key),fails);

        ownerContext.startActivity(resultIntent);
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
            e.printStackTrace();
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
            byte[] newCodeBytes = crypter.encrypt(code);
            String newCodeStr = CryptClass.byteArrayToHexString(newCodeBytes);
            this.code = newCodeStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected boolean isInputCodeCorrect(String inputCode) {
        try {
            byte[] inputCodeBytes = crypter.encrypt(inputCode);
            String inputCodeEncryptedStr = CryptClass.byteArrayToHexString(inputCodeBytes);
            return code.equals(inputCodeEncryptedStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    protected boolean wasStartedByBroadcastReceiver(){return wasStartedByBroadcastReceiver;}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0
                && wasStartedByBroadcastReceiver) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected ArrayList<View> getCodeButtonList() {
        return codeButtonList;
    }
}
