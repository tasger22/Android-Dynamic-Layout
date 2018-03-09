package hu.bme.iit.dynamiclayout_prototype;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import hu.bme.iit.dynamiclayout_prototype.MainActivity.CodeResolveDifficulty;

public class NumericCodeActivity extends CodeActivityBase {

    private EditText passwordLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.numeric_layout);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                 currentDifficulty = CodeResolveDifficulty.EASY;
                 isTestMode = false;
            } else {
                currentDifficulty = (CodeResolveDifficulty) extras.get("difficulty");
                isTestMode = extras.getBoolean("testMode");
            }
        } else {
            currentDifficulty = (CodeResolveDifficulty) savedInstanceState.getSerializable("difficulty");
            isTestMode = savedInstanceState.getBoolean("testMode");
        }

        if(isTestMode){
            tries = initialTries = 10;
            fails = 0;
            testStartTime = System.currentTimeMillis();
        }

        TextView codeView = (TextView) findViewById(R.id.randomCodeText);
        passwordLine = (EditText) findViewById(R.id.passwordLine);

        Button acceptButton = (Button) findViewById(R.id.acceptButton);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!passwordLine.getText().toString().equals("")) {
                    compareCodeToInput(passwordLine.getText().toString());
                }
            }
        });

        ImageButton deleteButton = (ImageButton) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!passwordLine.getText().toString().equals("")){
                    int passLength = passwordLine.getText().toString().length();
                    String newPassLineText = passwordLine.getText().toString().substring(0,passLength-1);
                    passwordLine.setText(newPassLineText);
                }
            }
        });

        setCodeToRandom();
        codeView.setText(code);

        if(currentDifficulty != CodeResolveDifficulty.EASY)
            randomizeButtons();

    }

    public void processNumberPress(View view) {
        Button numberButton = (Button) view;

        EditText passwordLine = (EditText) findViewById(R.id.passwordLine);

        passwordLine.append(numberButton.getText().toString());

        if(currentDifficulty == CodeResolveDifficulty.EVIL)
            randomizeButtons();
    }

    protected void setCodeToRandom(){
        Random rand = new Random();
        int codeLength = rand.nextInt(8);
        code = "";

        while(codeLength < 4) codeLength = rand.nextInt(8);

        for(int i = 0; i < codeLength; i++){
            code = code + Integer.toString(rand.nextInt(10));
        }

    }

    protected void compareCodeToInput(String input){
        if(input.length() < 4){
            Toast.makeText(getApplicationContext(),"Code must be 4-8 characters long",Toast.LENGTH_LONG).show();
            return;
        }

        if (isTestMode) {
            if(!code.equals("")){
                if(code.equals(input)){
                    --tries;
                    Toast.makeText(getApplicationContext(), getString(R.string.code_accepted_test_mode,initialTries - tries,initialTries),Toast.LENGTH_SHORT).show();
                    passwordLine.setText("");
                }
                else{
                    Toast.makeText(getApplicationContext(),getString(R.string.code_incorrect_test_mode),Toast.LENGTH_LONG).show();
                    ++fails;
                }
            }

            if(tries <= 0){

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

                String toastText = minutes + ":" + secondsString + "." + milliString ;
                Toast.makeText(getApplicationContext(),toastText,Toast.LENGTH_LONG).show();
                //TODO: Make a new activity and start it as an Intent here with the necessary extra content
            }

        } else {
            if(!code.equals("")){
                if(code.equals(input)){
                    Toast.makeText(getApplicationContext(), R.string.code_accepted,Toast.LENGTH_SHORT).show();
                    tries = 2;
                    return;
                }

                else if(tries > 0){
                    Toast.makeText(getApplicationContext(),getString(R.string.code_incorrect,tries),Toast.LENGTH_LONG).show();
                    --tries;
                }

                else
                    System.exit(1);
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
