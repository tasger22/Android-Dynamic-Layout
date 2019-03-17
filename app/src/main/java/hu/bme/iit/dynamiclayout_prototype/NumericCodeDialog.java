package hu.bme.iit.dynamiclayout_prototype;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import hu.bme.iit.dynamiclayout_prototype.MainActivity.CodeResolveDifficulty;

//Code activity in which the user has to input the code with a number pad
public class NumericCodeDialog extends CodeDialogBase {

    private EditText passwordLine;

    public NumericCodeDialog(@NonNull Context context, boolean wasStartedByBroadcastReceiver, SharedPreferences customSharedPref) {
        super(context,wasStartedByBroadcastReceiver,customSharedPref);
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

        TextView codeView = findViewById(R.id.randomCodeText);
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

        if(getCurrentDifficulty() != CodeResolveDifficulty.EASY)
            randomizeButtons();


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
        for (View b:
             getCodeButtonList()) {
            b.setOnClickListener(this::processCodeButtonPress);
        }
    }

    @Override
    protected void processCodeButtonPress(View view) {
        Button numberButton = (Button) view;
        EditText passwordLine = findViewById(R.id.passwordLine);

        passwordLine.append(numberButton.getText().toString());

        if(getCurrentDifficulty() == CodeResolveDifficulty.EVIL)
            randomizeButtons();
    }

    protected void compareCodeToInput(String input){
        if(input.length() < 4){
            Toast.makeText(getContext(),"Code must be 4-8 characters long",Toast.LENGTH_SHORT).show();
            return;
        }

        if (isTestMode()) {
            if(!isInputCodeCorrect("")){
                if(isInputCodeCorrect(input)){
                    decrementTries();
                    if(getTries() > 0)
                        Toast.makeText(getContext(), getContext().getString(R.string.code_accepted_test_mode,getInitialTries() - getTries(),getInitialTries()),Toast.LENGTH_SHORT).show();
                    passwordLine.setText("");
                    if(getCurrentDifficulty() == CodeResolveDifficulty.HARD) randomizeButtons();
                }
                else{
                    Toast.makeText(getContext(),getContext().getString(R.string.code_incorrect_test_mode),Toast.LENGTH_SHORT).show();
                    incrementFails();
                }
            }

            if(getTries() <= 0){

                compileResults();
            }

        } else {
            if(!isInputCodeCorrect("")){
                if(isInputCodeCorrect(input)){
                    Toast.makeText(getContext(), R.string.code_accepted,Toast.LENGTH_SHORT).show();
                    setTries(getInitialTries());
                    if(getCurrentDifficulty() == CodeResolveDifficulty.HARD) randomizeButtons();
                    if(wasStartedByBroadcastReceiver()) {
                        dismiss();
                    }
                }

                else if(getTries() > 0){
                    Toast.makeText(getContext(),getContext().getString(R.string.code_incorrect,getTries()),Toast.LENGTH_SHORT).show();
                    decrementTries();
                }

                else
                    dismiss();
            }
        }
    }

    private boolean areAllValuesTrue(boolean[] values){
        for (boolean b:
             values) {
            if(!b)  return false;
        }

        return true;
    }
}
