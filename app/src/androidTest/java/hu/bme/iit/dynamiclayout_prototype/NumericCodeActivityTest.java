package hu.bme.iit.dynamiclayout_prototype;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.nio.file.AccessDeniedException;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

public class NumericCodeActivityTest {

    Context appContext = InstrumentationRegistry.getContext();
    String testCode = "1996";

    @Rule
    ActivityTestRule<NumericCodeActivity> mActivityRule = new ActivityTestRule<>(
            NumericCodeActivity.class);

    @Before
    public void setUp() throws Exception {

        SharedPreferences appPref = PreferenceManager.getDefaultSharedPreferences(appContext);
        SharedPreferences.Editor appPrefEditor = appPref.edit();
        appPrefEditor.putBoolean(SettingsActivity.KEY_PREF_USERCODE,true);
        appPrefEditor.putString(SettingsActivity.KEY_PREF_CODEINPUT,testCode);
        Boolean commitWasSuccessful = appPrefEditor.commit();
        if(!commitWasSuccessful) throw new IllegalAccessException("Could not write persistent storage"); //TODO: Maybe its not illegal access, look it up
    }

    @Test
    public void inputCodeMatchesSetSecurityCode(){
        Intent numericActivityIntent = new Intent(appContext,NumericCodeActivity.class);
        appContext.startActivity(numericActivityIntent);

        //Pressing buttons to put in security code
        onView(withText("1")).perform(click());
        onView(withText("9")).perform(click());
        onView(withText("9")).perform(click());
        onView(withText("6")).perform(click());

        EditText passLine = mActivityRule.getActivity().findViewById(R.id.passwordLine);

        if(passLine != null)    assertEquals(testCode,passLine.getText().toString());
        else throw new AssertionError("Password line couldn't be read, find another way");

        onView(withText(R.string.code_accepted)).
                inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView()))).
                check(matches(isDisplayed()));
    }
}