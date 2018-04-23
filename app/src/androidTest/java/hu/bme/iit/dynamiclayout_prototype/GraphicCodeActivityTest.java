package hu.bme.iit.dynamiclayout_prototype;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.EditText;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class GraphicCodeActivityTest {

    Context appContext;
    String testCode = "EZER96";
    SharedPreferences.Editor appPrefEditor;
    GraphicCodeActivity startedGraphicActivity;

    @Before
    public void setUp() throws Exception {
        appContext = InstrumentationRegistry.getContext();

        startedGraphicActivity = mActivityRule.launchActivity(new Intent(appContext,GraphicCodeActivity.class));
        SharedPreferences appPref = PreferenceManager.getDefaultSharedPreferences(startedGraphicActivity);
        appPrefEditor = appPref.edit();
        appPrefEditor.putBoolean(SettingsActivity.KEY_PREF_USERCODE,true);
        appPrefEditor.putString(SettingsActivity.KEY_PREF_CODEINPUT,testCode);
        appPrefEditor.apply();

        //Since the check for preference values happen in onCreate, but that starts with the test we have to reset the variables and set the code to the one we gave in testCode
        startedGraphicActivity.initialSetup();
        startedGraphicActivity.setCodeToUserCode();
        startedGraphicActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startedGraphicActivity.buttonSetup();
            }
        });
    }

    @Rule
    public ActivityTestRule<GraphicCodeActivity> mActivityRule = new ActivityTestRule<>(
            GraphicCodeActivity.class);

    @Test
    public void testCodeMatchesSetSecurityCode(){
        //Check if setting the code to test code really changed it meaning initialSetup really changed the userCode var and setting the code to it worked
        String setTestCode = startedGraphicActivity.getCode(); //The security code which supposed to equal to testCode set above
        assertEquals(testCode,setTestCode);
    }

    @Test
    public void inputCodeMatchesSetSecurityCode() throws InterruptedException{
        //Pressing buttons to put in security code
        onView(withIndex(withText("E"),0)).perform(click());
        onView(withIndex(withText("Z"),0)).perform(click());
        onView(withIndex(withText("E"),0)).perform(click());
        onView(withIndex(withText("R"),0)).perform(click());
        onView(withIndex(withText("9"),0)).perform(click());
        onView(withIndex(withText("6"),0)).perform(click());

        //Testing if the code was really correct with checking if the Toast for the correct code appeared (throws exception if the Toast with the message was not found)
        onView(withText(R.string.code_accepted)).inRoot(withDecorView(not(startedGraphicActivity.getWindow().getDecorView()))).check(matches(isDisplayed()));

        Thread.sleep(1000); //Wait for the Toast to disappear
    }

    //This test shows that when we fail, then we try again, out previously failed attempt does not affect the input code
    @Test
    public void inputCodeSnippetClearsAfterFail() throws InterruptedException{
        onView(withIndex(withText("E"),0)).perform(click());
        onView(withIndex(withText("Z"),0)).perform(click());
        onView(withIndex(withText("Z"),0)).perform(click());

        //Check if the Toast popped up with saying how many tries left (have to add 1 to tries since a fail already decreases the amount right when the Toast appears)
        onView(withText(startedGraphicActivity.getString(R.string.code_incorrect,startedGraphicActivity.getTries()+1))).inRoot(withDecorView(not(startedGraphicActivity.getWindow().getDecorView()))).check(matches(isDisplayed()));

        Thread.sleep(1000); //Wait for the Toast to disappear

        onView(withIndex(withText("E"),0)).perform(click());
        onView(withIndex(withText("Z"),0)).perform(click());
        onView(withIndex(withText("E"),0)).perform(click());
        onView(withIndex(withText("R"),0)).perform(click());
        onView(withIndex(withText("9"),0)).perform(click());
        onView(withIndex(withText("6"),0)).perform(click());

        onView(withText(R.string.code_accepted)).inRoot(withDecorView(not(startedGraphicActivity.getWindow().getDecorView()))).check(matches(isDisplayed()));
    }

    public static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("with index: ");
                description.appendValue(index);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == index;
            }
        };
    }

    @After
    public void tearDown(){
        appPrefEditor.putString(SettingsActivity.KEY_PREF_CODEINPUT,"0000"); //We have to set it back since NumeriCodeActivity only deals in digits
        appPrefEditor.apply();
    }
}