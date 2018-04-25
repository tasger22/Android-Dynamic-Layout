package hu.bme.iit.dynamiclayout_prototype;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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

@RunWith(AndroidJUnit4.class)
public class NumericCodeActivityTest {

    Context appContext;
    String testCode = "1996";
    SharedPreferences.Editor appPrefEditor;
    NumericCodeDialog startedNumericActivity;

    @Before
    public void setUp() throws Exception {
        appContext = InstrumentationRegistry.getContext();

        startedNumericActivity = new NumericCodeDialog(mActivityRule.getActivity(),false);
        SharedPreferences appPref = PreferenceManager.getDefaultSharedPreferences(appContext);
        appPrefEditor = appPref.edit();
        appPrefEditor.putBoolean(SettingsActivity.KEY_PREF_USERCODE,true);
        appPrefEditor.putString(SettingsActivity.KEY_PREF_CODEINPUT,testCode);
        appPrefEditor.apply();

        //Since the check for preference values happen in onCreate, but that starts with the test we have to reset the variables and set the code to the one we gave in testCode
        startedNumericActivity.initialSetup();
        startedNumericActivity.setCodeToUserCode();
    }

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Test
    public void testCodeMatchesSetSecurityCode(){
        //Check if setting the code to test code really changed it meaning initialSetup really changed the userCode var and setting the code to it worked
        String setTestCode = startedNumericActivity.getCode(); //The security code which supposed to equal to testCode set above
        assertEquals(testCode,setTestCode);
    }

    @Test
    public void inputCodeMatchesPasswordLineAndSetSecurityCode(){
        //Pressing buttons to put in security code
        onView(withText("1")).perform(click());
        onView(withText("9")).perform(click());
        onView(withText("9")).perform(click());
        onView(withText("6")).perform(click());

        //Check if the password line holds the same values
        EditText passLine = startedNumericActivity.findViewById(R.id.passwordLine);
        assertEquals(testCode,passLine.getText().toString());
        assertEquals(startedNumericActivity.getCode(),passLine.getText().toString());

        //Testing if the code was really correct with checking if the Toast for the correct code appeared (throws exception if the Toast with the message was not found)
        onView(withId(R.id.acceptButton)).perform(click());
        onView(withText(R.string.code_accepted)).inRoot(withDecorView(not(startedNumericActivity.getWindow().getDecorView()))).check(matches(isDisplayed()));
    }

    @Test
    public void deleteCharacterButtonWorks(){
        //Pressing buttons to put in something
        onView(withText("1")).perform(click());
        onView(withText("9")).perform(click());

        //Pressing the delete button two times to undo the input
        onView(withId(R.id.deleteButton)).perform(click());
        onView(withId(R.id.deleteButton)).perform(click());

        //Check if the password line is empty
        EditText passLine = startedNumericActivity.findViewById(R.id.passwordLine);
        assertEquals("",passLine.getText().toString());
    }
}