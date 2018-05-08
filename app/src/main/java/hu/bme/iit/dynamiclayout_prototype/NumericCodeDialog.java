package hu.bme.iit.dynamiclayout_prototype;

import android.content.Context;
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

    protected NumericCodeDialog(@NonNull Context context, boolean wasStartedByBroadcastReceiver) {
        super(context,wasStartedByBroadcastReceiver);
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
                if (!codeInput.equals("")) {
                    compareCodeToInput(codeInput);
                }
            }
        });

        ImageButton deleteButton = (ImageButton) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String codeInput = passwordLine.getText().toString();
                if(!codeInput.equals("")){
                    int passLength = codeInput.length();
                    String newPassLineText = codeInput.substring(0,passLength-1);
                    passwordLine.setText(newPassLineText);
                }
            }
        });

        codeView.setText(getCode());

        if(getCurrentDifficulty() != CodeResolveDifficulty.EASY)
            randomizeButtons();


    }

    private void setUpAllButtons(){
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

    public void processNumberPress(View view) {
        Button numberButton = (Button) view;

        EditText passwordLine = (EditText) findViewById(R.id.passwordLine);

        passwordLine.append(numberButton.getText().toString());

        if(getCurrentDifficulty() == CodeResolveDifficulty.EVIL)
            randomizeButtons();
    }

    @Deprecated
    protected void setCodeToRandom(){
        Random rand = new Random();
        int codeLength = rand.nextInt(8);
        setCode("");

        while(codeLength < 4) codeLength = rand.nextInt(8);

        for(int i = 0; i < codeLength; i++){
            setCode(getCode() + Integer.toString(rand.nextInt(10)));
        }

    }

    protected void compareCodeToInput(String input){
        if(input.length() < 4){
            Toast.makeText(getContext(),"Code must be 4-8 characters long",Toast.LENGTH_SHORT).show();
            return;
        }

        if (isTestMode()) {
            if(!getCode().equals("")){
                if(getCode().equals(input)){
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
            if(!getCode().equals("")){
                if(getCode().equals(input)){
                    Toast.makeText(getContext(), R.string.code_accepted,Toast.LENGTH_SHORT).show();
                    setTries(getInitialTries());
                    if(getCurrentDifficulty() == CodeResolveDifficulty.HARD) randomizeButtons();
                    if(wasStartedByBroadcastReceiver()) {
                        dismiss();
                        //getCallerActivity().finish();
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
