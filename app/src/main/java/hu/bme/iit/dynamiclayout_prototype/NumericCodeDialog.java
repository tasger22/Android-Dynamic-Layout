package hu.bme.iit.dynamiclayout_prototype;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import hu.bme.iit.dynamiccodedialog.CodeDialogBase;
import hu.bme.iit.dynamiclayout_prototype.MainActivity.CodeResolveDifficulty;

import static android.view.WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
import static android.view.WindowManager.LayoutParams.FLAG_LOCAL_FOCUS_MODE;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
import static android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
import static android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;

//Code activity in which the user has to input the code with a number pad
public class NumericCodeDialog extends CodeDialogBase {

    private EditText passwordLine;
    private CodeResolveDifficulty currentDifficulty;
    private boolean isTestMode;
    private SharedPreferences settings;
    private boolean wasStartedByBroadcastReceiver;
    private Context ownerContext;
    private CryptographyImplementation crypter;
    private int fails; //(Only in TestMode) Counting how many times you failed to input the right code
    private long testStartTime;

    public NumericCodeDialog(@NonNull Context context, boolean wasStartedByBroadcastReceiver, SharedPreferences customSharedPref, CryptographyImplementation cryptographyImplementation) {
        super(context, R.style.AppTheme, cryptographyImplementation);
        crypter = cryptographyImplementation;
        this.wasStartedByBroadcastReceiver = wasStartedByBroadcastReceiver;
        settings = customSharedPref;
        ownerContext = context;

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
        setContentView(R.layout.numeric_layout);

        try {
            initialSetup();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setUpAllButtons();

        passwordLine = findViewById(R.id.passwordLine);

        Button acceptButton = findViewById(R.id.acceptButton);
        acceptButton.setOnClickListener(view -> {
            String codeInput = passwordLine.getText().toString();
            if (!"".equals(codeInput)) {
                compareCodeToInput(codeInput);
            }
        });

        ImageButton deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(view -> {
            String codeInput = passwordLine.getText().toString();
            if(!"".equals(codeInput)){
                int passLength = codeInput.length();
                String newPassLineText = codeInput.substring(0,passLength-1);
                passwordLine.setText(newPassLineText);
            }
        });

        if(currentDifficulty != CodeResolveDifficulty.EASY){
            randomizeButtons();
        }
    }

    //Method to initialize all the private variables from the SharedPreferences
    protected void initialSetup() {

        if(wasStartedByBroadcastReceiver){
            isTestMode = false;
        }
        else {
            isTestMode = settings.getBoolean(SettingsActivity.KEY_PREF_TESTMODE,false);
        }

        currentDifficulty = MainActivity.getCodeResolveDifficultyFromString(settings.getString(SettingsActivity.KEY_PREF_DIFFICULTY,"EASY"));
        String savedCode = settings.getString(getContext().getString(R.string.encrypted_code_key),crypter.byteArrayToHexString(crypter.encrypt("0000")));
        setCode(savedCode);

        if(isTestMode){
            setInitialTries(10);
            fails = 0;
            testStartTime = System.currentTimeMillis();
        }
        else{
            setInitialTries(2);
        }
    }

    private void setUpAllButtons(){
        ViewGroup buttonGridLayout = findViewById(R.id.buttonLayout);
        View acceptButton = null;
        for (int i = 0; i < buttonGridLayout.getChildCount(); i++) {
            View child = buttonGridLayout.getChildAt(i);
            if (child.getId() == R.id.acceptButton){
                acceptButton = child;
                buttonGridLayout.removeView(child);
            }
        }
        setCodeButtonList(buttonGridLayout);
        buttonGridLayout.addView(acceptButton);
    }

    protected void processCodeButtonPress(View view) {
        Button numberButton = (Button) view;
        EditText passwordLine = findViewById(R.id.passwordLine);

        passwordLine.append(numberButton.getText().toString());

        if(currentDifficulty == CodeResolveDifficulty.EVIL)
            randomizeButtons();
    }

    @Override
    protected void compareCodeToInput(String input){
        if(input.length() < 4){
            Toast.makeText(getContext(),"Code must be 4-8 characters long",Toast.LENGTH_SHORT).show();
            return;
        }
        byte[] inputEncryptedBytes = crypter.encrypt(input);
        String hexaInputString = crypter.byteArrayToHexString(inputEncryptedBytes);
        if (isTestMode) {
            if(!isInputCodeCorrect("")){
                if(isInputCodeCorrect(hexaInputString)){
                    decrementTries();
                    if(getTries() > 0){
                        Toast.makeText(getContext(), getContext().getString(R.string.code_accepted_test_mode,getInitialTries() - getTries(),getInitialTries()),Toast.LENGTH_SHORT).show();
                    }
                    passwordLine.setText("");
                    if(currentDifficulty == CodeResolveDifficulty.HARD) randomizeButtons();
                }
                else{
                    Toast.makeText(getContext(),getContext().getString(R.string.code_incorrect_test_mode),Toast.LENGTH_SHORT).show();
                    ++fails;
                }
            }

            if(getTries() <= 0){
                compileResults();
            }

        } else {
            if(!isInputCodeCorrect("")){
                if(isInputCodeCorrect(hexaInputString)){
                    Toast.makeText(getContext(), R.string.code_accepted,Toast.LENGTH_SHORT).show();
                    setTries(getInitialTries());
                    if(currentDifficulty == CodeResolveDifficulty.HARD) randomizeButtons();
                    if(wasStartedByBroadcastReceiver) {
                        dismiss();
                    }
                }
                else if(getTries() > 0){
                    Toast.makeText(getContext(),getContext().getString(R.string.code_incorrect,getTries()),Toast.LENGTH_SHORT).show();
                    decrementTries();
                }
                else{
                    authenticationFailed();
                }
            }
        }
    }

    @Override
    protected boolean isInputCodeCorrect(String inputCode) {
        return super.isInputCodeCorrect(inputCode);
    }

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
            String decryptedStr = crypter.decrypt(getCode());
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
}
