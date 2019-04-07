package hu.bme.iit.dynamiclayout_prototype;

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

import hu.bme.iit.dynamiclayout_prototype.cryptography.Cryptography;

//Class which provides all the necessary components for a CodeActivity
public abstract class CodeDialogBase extends AlertDialog {

    private byte[] code; //Security code for the CodeDialogs, encrypted
    private Cryptography crypter;
    private ViewGroup codeButtonContainer;
    private ArrayList<View> codeButtonList;
    private int initialTries = 2;
    private int tries = initialTries; //How many times you can try to input the code until it rejects input


    protected CodeDialogBase(@NonNull Context context, Cryptography cryptographyImplementation) {
        super(context,R.style.AppTheme);
        crypter = cryptographyImplementation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected abstract void initialSetup() throws Exception;

    protected void setCodeButtonList(int viewContainerId){
        codeButtonContainer = findViewById(viewContainerId);
        codeButtonList = getChildrenViews(codeButtonContainer);
    }

    protected void setCodeButtonList(ViewGroup viewContainer){
        codeButtonContainer = viewContainer;
        codeButtonList = getChildrenViews(codeButtonContainer);
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

    protected void compareCodeToInput(String input){
        if(!isInputCodeCorrect("")){
            if(isInputCodeCorrect(input)){
                Toast.makeText(getContext(), R.string.code_accepted,Toast.LENGTH_SHORT).show();
                setTries(initialTries);
            }
            else if(tries > 0){
                Toast.makeText(getContext(),getContext().getString(R.string.code_incorrect,tries),Toast.LENGTH_SHORT).show();
                decrementTries();
            }
            else{
                authenticationFailed();
            }
        }
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
