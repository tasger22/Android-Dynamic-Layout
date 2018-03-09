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

    private String codeInput = "";
    private int tries = 2;
    private CodeResolveDifficulty currentDifficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graphic_layout);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                currentDifficulty = MainActivity.CodeResolveDifficulty.EASY;
            } else {
                currentDifficulty = (MainActivity.CodeResolveDifficulty) extras.get("difficulty");
            }
        } else {
            currentDifficulty = (MainActivity.CodeResolveDifficulty) savedInstanceState.getSerializable("difficulty");
        }

        TextView codeView = (TextView) findViewById(R.id.randomCodeText);

        setCodeToRandom();
        codeView.setText(code);

        buttonSetup();
    }

    private void buttonSetup() {
        RelativeLayout codeInsertLayout = (RelativeLayout) findViewById(R.id.codeInsertLayout);
        codeInsertLayout.removeAllViews();
        final ArrayList<Button> buttonList = new ArrayList<>();

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int buttonSize = buttonSizeChanger();
        Random rand = new Random();
        float dx = rand.nextFloat() * displayMetrics.widthPixels - dpToPx(buttonSize);
        float dy = rand.nextFloat() * 1000;

        for(int i = 0; i < 2*code.length();i++){ //start of button creation
            Button b = new Button(getApplicationContext());
            boolean isLetter = rand.nextBoolean();


            b.setBackgroundColor(getResources().getColor(R.color.defaultButtonColor));
            b.setTextColor(Color.BLACK);
            if(i < code.length()){
                Character c = code.charAt(i);
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
                dy = rand.nextFloat() * 1000; //TODO: find better way to calculate this value instead of just 1000

                if(dx < dpToPx(buttonSize))
                    b.setX(dx + dpToPx(buttonSize));
                else if (dx > displayMetrics.widthPixels - dpToPx(buttonSize))
                    b.setX(dx - dpToPx(buttonSize));
                else
                    b.setX(dx);

                b.setY(dy);
            }

            dx = rand.nextFloat() * displayMetrics.widthPixels - dpToPx(buttonSize);
            dy = rand.nextFloat() * 1000;

            buttonList.add(b);
            codeInsertLayout.addView(b);

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Button clickedButton = (Button) view;
                    codeInput = codeInput + clickedButton.getText().toString();
                    compareCodeToInput(codeInput);

                    if(currentDifficulty == CodeResolveDifficulty.EVIL)
                        buttonSetup();
                }
            });
        }
    }

    private int buttonSizeChanger() { // returning a value depending on the length of the code, to set the button size (length is between 4 and 8)
        if(code.length() < 6)   return 80;
        else return 65;
    }

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
        code = "";

        while(codeLength < 4) codeLength = rand.nextInt(8);

        for(int i = 0; i < codeLength; i++){
            if(isLetter)    code = code + (char)((int)'A'+rand.nextDouble()*((int)'Z'-(int)'A'+1));
            else    code = code + Integer.toString(rand.nextInt(10));
            isLetter = rand.nextBoolean();
        }
    }

    protected void compareCodeToInput(String input) {
        String codeSnippet = code.substring(0,codeInput.length());

        if(codeSnippet.equals(codeInput)){
            if(code.equals(codeInput)){
                Toast.makeText(getApplicationContext(), R.string.code_accepted,Toast.LENGTH_SHORT).show();
                codeInput = "";
                tries = 2;
                return;
            }
            return;
        }

        else{
            if(tries > 0){
                Toast.makeText(getApplicationContext(),getString(R.string.code_incorrect_0) + " " + tries + " " + getString(R.string.code_incorrect_1),Toast.LENGTH_LONG).show(); //TODO: change "x tries" to represent an actual number
                codeInput = "";
                --tries;
            }

            else
                System.exit(1);


            return;
        }
    }

    private int dpToPx(int dp) { //Convert a DP value to pixel value
        float density = getApplicationContext().getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }
}
