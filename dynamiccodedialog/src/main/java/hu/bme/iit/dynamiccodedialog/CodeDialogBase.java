package hu.bme.iit.dynamiccodedialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import hu.bme.iit.dynamiccodedialog.cryptography.Cryptography;

//Class which provides all the necessary components for a CodeActivity
public abstract class CodeDialogBase extends AlertDialog {

    private byte[] code; //Security code for the CodeDialogs, encrypted
    private Cryptography crypter;
    private ViewGroup codeButtonContainer;
    private ArrayList<View> codeButtonList;
    private int initialTries = 2;
    private int tries = initialTries; //How many times you can try to input the code until it rejects input


    protected CodeDialogBase(@NonNull Context context, int themeId ,Cryptography cryptographyImplementation) {
        super(context, themeId);
        crypter = cryptographyImplementation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setCodeButtonList(int viewContainerId){
        codeButtonContainer = findViewById(viewContainerId);
        codeButtonList = getChildrenViews(codeButtonContainer);
        for (View button:
                codeButtonList) {
            button.setOnClickListener(this::processCodeButtonPress);
        }
    }

    /**
     * @param viewContainer Must contain all the views necessary for code input
     */
    protected void setCodeButtonList(ViewGroup viewContainer){
        codeButtonContainer = viewContainer;
        codeButtonList = getChildrenViews(codeButtonContainer);
        for (View button:
             codeButtonList) {
            button.setOnClickListener(this::processCodeButtonPress);
        }
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

    /**
     * @param view View responsible for code input
     * @implNote This method is assigned to all codeButtonList views as OnClickListener
     */
    protected abstract void processCodeButtonPress(View view);

    protected void randomizeButtons(){
        try {
            Random rand = new Random();
            ArrayList<ViewGroup.LayoutParams> params = new ArrayList<>();
            for (View v:
                    codeButtonList) {
                params.add(v.getLayoutParams());
            }
            for (View v:
                    codeButtonList) {
                codeButtonContainer.removeView(v);
                ViewGroup.LayoutParams randomParam = params.get(rand.nextInt(params.size()));
                v.setLayoutParams(randomParam);
                params.remove(randomParam);
                codeButtonContainer.addView(v);
            }
        }  catch (ClassCastException e){
            Log.w("Failed casting", e);
        }

    }

    protected boolean compareCodeToInput(String input){
        boolean result = false;
        if(!isInputCodeCorrect("")){
            if(isInputCodeCorrect(input)){
                Toast.makeText(getContext(), "The code is right, you have done " + tries+1 + " out of " + initialTries+1 , Toast.LENGTH_SHORT).show();
                setTries(initialTries);
                result = true;
            }
            else if(tries > 0){
                Toast.makeText(getContext(),"Incorrect code, " + tries + "tries remaining", Toast.LENGTH_SHORT).show();
                decrementTries();
                result = false;
            }
            else{
                authenticationFailed();
                result = false;
            }
        }
        return result;
    }

    protected void authenticationFailed(){
        dismiss();
    }

    protected void setCode(String code) {
        byte[] newCodeBytes = crypter.encrypt(code);
        this.code = newCodeBytes;
    }

    protected byte[] getCode(){
        return code;
    }

    protected boolean isInputCodeCorrect(String inputCode) {
        byte[] inputCodeBytes = crypter.encrypt(inputCode);
        return Arrays.equals(code, inputCodeBytes);
    }

    protected ArrayList<View> getCodeButtonList() {
        return codeButtonList;
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

    protected int getInitialTries() {
        return initialTries;
    }

    protected void setInitialTries(int initialTries) {
        this.initialTries = initialTries;
        this.tries = initialTries;
    }

    protected void decrementTries(){
        --tries;
    }
}
