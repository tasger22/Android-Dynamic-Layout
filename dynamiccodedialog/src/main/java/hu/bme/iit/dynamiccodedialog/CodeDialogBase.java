package hu.bme.iit.dynamiccodedialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Random;

import hu.bme.iit.dynamiccodedialog.cryptography.Cryptography;

//Class which provides all the necessary components for a CodeActivity
public abstract class CodeDialogBase <CodeContainerType, CodeInputType> extends AlertDialog {

    private CodeContainerType code; //Security code for the CodeDialogs, encrypted
    private Cryptography<CodeContainerType,CodeInputType> crypter;
    private ViewGroup codeInputViewContainer;
    private ArrayList<View> codeInputViewList;
    private int initialTries;
    private int tries; //How many times you can try to input the code until it rejects input


    protected CodeDialogBase(@NonNull Context context, int themeId ,Cryptography cryptographyImplementation) {
        super(context, themeId);
        crypter = cryptographyImplementation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * @param viewContainer ViewGroup which must contain all the views necessary for code input
     */
    protected void setUpCodeInputInterface(ViewGroup viewContainer){
        codeInputViewContainer = viewContainer;
        codeInputViewList = getChildrenViews(codeInputViewContainer);
        for (View view:
             codeInputViewList) {
            view.setOnClickListener(this::processCodeInputViewPress);
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
     * @implNote This method is assigned to all codeInputViewContainer children views as OnClickListener
     */
    protected abstract void processCodeInputViewPress(View view);



    protected void randomizeInputViews(){
        Random rand = new Random();
        ArrayList<ViewGroup.LayoutParams> params = new ArrayList<>();
        for (View v:
                codeInputViewList) {
            params.add(v.getLayoutParams());
        }
        for (View v:
                codeInputViewList) {
            ViewGroup.LayoutParams randomParam = params.get(rand.nextInt(params.size()));
            v.setLayoutParams(randomParam);
            params.remove(randomParam);
        }
    }

    protected boolean compareCodeToInput(CodeInputType input){
        boolean result = false;
        if(isInputCodeCorrect(input)){
            result = true;
        }
        else if(initialTries > 0){
            if (tries <= 0){
                authenticationFailed();
            }
        }
        return result;
    }

    protected void authenticationFailed(){
    }

    protected void setCode(CodeInputType code) {
        this.code = crypter.encrypt(code);
    }

    protected CodeContainerType getCode(){
        return code;
    }

    protected boolean isInputCodeCorrect(CodeInputType inputCode) {
        CodeContainerType encryptedInputCode = crypter.encrypt(inputCode);
        return crypter.equals(code ,encryptedInputCode);
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
