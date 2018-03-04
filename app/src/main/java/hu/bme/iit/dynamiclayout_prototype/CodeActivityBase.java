package hu.bme.iit.dynamiclayout_prototype;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public abstract class CodeActivityBase extends AppCompatActivity {
    protected String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    protected abstract void setCodeToRandom();
    protected abstract void compareCodeToInput(String input);
}
