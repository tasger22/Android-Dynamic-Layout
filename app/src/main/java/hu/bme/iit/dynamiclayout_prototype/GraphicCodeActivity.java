package hu.bme.iit.dynamiclayout_prototype;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Dimension;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;
import hu.bme.iit.dynamiclayout_prototype.MainActivity.CodeResolveDifficulty;

public class GraphicCodeActivity extends CodeActivityBase {

    private String codeInput = ""; //String which contains the character from all the previously pressed buttons

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graphic_layout);

        initialSetup();

        TextView codeView = (TextView) findViewById(R.id.randomCodeText);

        if(isCodeNotUserCode())  setCodeToRandom();
        else setCodeToUserCode();
        codeView.setText(getCode());

        buttonSetup();
    }

    //Randomize the location of on screen buttons
    private void buttonSetup() {
        RelativeLayout codeInsertLayout = (RelativeLayout) findViewById(R.id.codeInsertLayout);
        codeInsertLayout.removeAllViews();
        final ArrayList<Button> buttonList = new ArrayList<>();

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int buttonSize = buttonSizeChanger();
        int buttonAreaHeight = 380; //The height of the area in which the buttons from 'buttonList' are going (380 is a tested dpi value which works)

        Random rand = new Random();
        float dx = rand.nextFloat() * displayMetrics.widthPixels - dpToPx(buttonSize);
        float dy = rand.nextFloat() * dpToPx(buttonAreaHeight);

        for(int i = 0; i < 2*getCode().length();i++){ //start of button creation
            Button b = new Button(getApplicationContext());
            boolean isLetter = rand.nextBoolean();


            b.setBackgroundColor(getResources().getColor(R.color.defaultButtonColor));
            b.setTextColor(Color.BLACK);

            //Sets the text of the button to the next character of the code, otherwise it creates a random digit or letter
            if(i < getCode().length()){
                Character c = getCode().charAt(i);
                b.setText(c.toString());
            }
            else{
                Character randChar = (char)((int)'A'+rand.nextDouble()*((int)'Z'-(int)'A'+1));
                if(isLetter)     b.setText( randChar.toString() );
                else    b.setText( Integer.toString(rand.nextInt(10)) );
            }

            b.setTextSize(Dimension.SP,20);
            b.setLayoutParams(new ViewGroup.LayoutParams(dpToPx(buttonSize),dpToPx(buttonSize)));

            if(dx < dpToPx(buttonSize))
                b.setX(dx + dpToPx(buttonSize));
            else if (dx > displayMetrics.widthPixels - dpToPx(buttonSize))
                b.setX(dx - dpToPx(buttonSize));
            else
                b.setX(dx);

            b.setY(dy);

            while(isButtonColliding(buttonList,b,buttonSize)){
                dx = rand.nextFloat() * displayMetrics.widthPixels - dpToPx(buttonSize);
                dy = rand.nextFloat() * dpToPx(buttonAreaHeight);

                if(dx < dpToPx(buttonSize))
                    b.setX(dx + dpToPx(buttonSize));
                else if (dx > displayMetrics.widthPixels - dpToPx(buttonSize))
                    b.setX(dx - dpToPx(buttonSize));
                else
                    b.setX(dx);

                b.setY(dy);
            }

            dx = rand.nextFloat() * displayMetrics.widthPixels - dpToPx(buttonSize);
            dy = rand.nextFloat() * dpToPx(buttonAreaHeight);

            buttonList.add(b);
            codeInsertLayout.addView(b);

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Button clickedButton = (Button) view;
                    codeInput = codeInput + clickedButton.getText().toString();
                    compareCodeToInput(codeInput);

                    if(getCurrentDifficulty() == CodeResolveDifficulty.EVIL)
                        buttonSetup();
                }
            });
        }
    }

    // returning a value depending on the length of the code, to set the button size (length is between 4 and 8)
    private int buttonSizeChanger() {
        if(getCode().length() < 6)   return 80;
        else return 65;
    }

    //Checks if 'currentButton' is touching any button in the 'buttons' array depending how big is the button in pixels given by 'buttonSize'
    private boolean isButtonColliding(ArrayList<Button> buttons, Button currentButton, int buttonSize){
        Rect currentButtonBounds = new Rect((int)currentButton.getX(),(int)currentButton.getY(),(int)currentButton.getX() + dpToPx(buttonSize),(int)currentButton.getY() + dpToPx(buttonSize));
        for (Button b:
             buttons) {
            Rect bBounds = new Rect((int)b.getX(),(int)b.getY(),(int)b.getX() + dpToPx(buttonSize),(int)b.getY() + dpToPx(buttonSize));
            if(Rect.intersects(currentButtonBounds,bBounds)){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void setCodeToRandom() {
        Random rand = new Random();
        boolean isLetter = rand.nextBoolean();
        int codeLength = rand.nextInt(8);
        setCode("");

        while(codeLength < 4) codeLength = rand.nextInt(8);

        for(int i = 0; i < codeLength; i++){
            if(isLetter)    setCode(getCode()+ (char)((int)'A'+rand.nextDouble()*((int)'Z'-(int)'A'+1)));
            else    setCode(getCode() + Integer.toString(rand.nextInt(10)));
            isLetter = rand.nextBoolean();
        }
    }

    protected void compareCodeToInput(String input) {
        String codeSnippet = getCode().substring(0,codeInput.length());

        if (isTestMode()) {
            if(codeSnippet.equals(codeInput)){
                if(getCode().equals(codeInput)){
                    decrementTries();
                    codeInput = "";

                    if(getTries() > 0)
                        Toast.makeText(getApplicationContext(), getString(R.string.code_accepted_test_mode,getInitialTries() - getTries(),getInitialTries()),Toast.LENGTH_SHORT).show();
                    else
                        compileResults();

                    if(getCurrentDifficulty() == CodeResolveDifficulty.HARD)    buttonSetup();
                }
            }

            else{
                incrementFails();
                Toast.makeText(getApplicationContext(),getString(R.string.code_incorrect_test_mode),Toast.LENGTH_LONG).show();
                codeInput = "";
            }
        }

        else{
            if(codeSnippet.equals(codeInput)){
                if(getCode().equals(codeInput)){
                    Toast.makeText(getApplicationContext(), R.string.code_accepted,Toast.LENGTH_SHORT).show();
                    codeInput = "";
                    setTries(getInitialTries());
                    if(getCurrentDifficulty() == CodeResolveDifficulty.HARD)    buttonSetup();
                    if(wasStartedByBroadcastReceiver()) finish();
                }
            }

            else{
                if(getTries() > 0){
                    Toast.makeText(getApplicationContext(),getString(R.string.code_incorrect,getTries()),Toast.LENGTH_LONG).show();
                    codeInput = "";
                    decrementTries();
                }

                else
                    finish();
            }
        }
    }

    //Convert a DP value to pixel value
    private int dpToPx(int dp) {
        float density = getApplicationContext().getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }
}
