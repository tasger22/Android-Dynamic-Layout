package hu.bme.iit.dynamiclayout_prototype;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
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

        TextView codeView = (TextView) findViewById(R.id.randomCodeText);
        passwordLine = (EditText) findViewById(R.id.passwordLine);

        Button acceptButton = (Button) findViewById(R.id.acceptButton);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String codeInput = passwordLine.getText().toString();
                if (!"".equals(codeInput)) {
                    compareCodeToInput(codeInput);
                }
            }
        });

        ImageButton deleteButton = (ImageButton) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String codeInput = passwordLine.getText().toString();
                if(!"".equals(codeInput)){
                    int passLength = codeInput.length();
                    String newPassLineText = codeInput.substring(0,passLength-1);
                    passwordLine.setText(newPassLineText);
                }
            }
        });

        if(getCurrentDifficulty() != CodeResolveDifficulty.EASY)
            randomizeButtons();


    }

    private void setUpAllButtons(){
        ArrayList<Button> buttonList = getViewButtons();

        for (Button b:
             buttonList) {
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    processNumberPress(view);
                }
            });
        }
    }

    @NonNull
    private ArrayList<Button> getViewButtons() {
        ArrayList<Button> buttonList = new ArrayList<>();

        //TODO: change the following code into more practical
        buttonList.add((Button) findViewById(R.id.button1));
        buttonList.add((Button) findViewById(R.id.button2));
        buttonList.add((Button) findViewById(R.id.button3));
        buttonList.add((Button) findViewById(R.id.button4));
        buttonList.add((Button) findViewById(R.id.button5));
        buttonList.add((Button) findViewById(R.id.button6));
        buttonList.add((Button) findViewById(R.id.button7));
        buttonList.add((Button) findViewById(R.id.button8));
        buttonList.add((Button) findViewById(R.id.button9));
        buttonList.add((Button) findViewById(R.id.button10));
        return buttonList;
    }

    public void processNumberPress(View view) {
        Button numberButton = (Button) view;

        EditText passwordLine = (EditText) findViewById(R.id.passwordLine);

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

    private void randomizeButtons(){
        ArrayList<Button> buttonList = getViewButtons();

        boolean[] numberWasUsed = new boolean[10];
        Random rand = new Random();

        for (Button b : buttonList
             ) {
            int tempRandInt = rand.nextInt(10);

            while(numberWasUsed[tempRandInt]){
                if(areAllValuesTrue(numberWasUsed)) return;

                tempRandInt = rand.nextInt(10);
            }

            numberWasUsed[tempRandInt] = true;
            b.setText(Integer.toString(tempRandInt));
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
