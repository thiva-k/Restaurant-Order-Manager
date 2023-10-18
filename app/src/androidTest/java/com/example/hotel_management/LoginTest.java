package com.example.hotel_management;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginTest {
    @Before
    public void setUp() {
        ActivityScenario.launch(MainActivity.class);
    }

    @Test
    public void testWaiterLoginSuccess() {
        // Enter email and password
        Espresso.onView(ViewMatchers.withId(R.id.editTextTextEmailAddress))
                .perform(ViewActions.typeText("waiter@gmail.com"), ViewActions.closeSoftKeyboard());

        Espresso.onView(ViewMatchers.withId(R.id.editTextTextPassword))
                .perform(ViewActions.typeText("password"), ViewActions.closeSoftKeyboard());

        // Click the login button
        Espresso.onView(ViewMatchers.withId(R.id.loginButton)).perform(ViewActions.click());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if the waiter activity is launched after successful login
        Espresso.onView(ViewMatchers.withId(R.id.waiterViewPager)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
    @Test
    public void testWaiterLoginFailure() {
        // Enter email and password
        Espresso.onView(ViewMatchers.withId(R.id.editTextTextEmailAddress))
                .perform(ViewActions.typeText("waiter@gmail.com"), ViewActions.closeSoftKeyboard());

        Espresso.onView(ViewMatchers.withId(R.id.editTextTextPassword))
                .perform(ViewActions.typeText("wrong password"), ViewActions.closeSoftKeyboard());

        // Click the login button
        Espresso.onView(ViewMatchers.withId(R.id.loginButton)).perform(ViewActions.click());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if the waiter activity is launched after failed login. it it is, then the test should fail
        Espresso.onView(ViewMatchers.withId(R.id.waiterViewPager)).check(ViewAssertions.doesNotExist());
    }
    @Test
    public void testChefLoginSuccess() {
        // Enter email and password
        Espresso.onView(ViewMatchers.withId(R.id.editTextTextEmailAddress))
                .perform(ViewActions.typeText("chef@gmail.com"), ViewActions.closeSoftKeyboard());

        Espresso.onView(ViewMatchers.withId(R.id.editTextTextPassword))
                .perform(ViewActions.typeText("password"), ViewActions.closeSoftKeyboard());

        // Click the login button
        Espresso.onView(ViewMatchers.withId(R.id.loginButton)).perform(ViewActions.click());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if the chef activity is launched after successful login
        Espresso.onView(ViewMatchers.withId(R.id.chefOrderRecycleView)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
    @Test
    public void testChefLoginFailure() {
        // Enter email and password
        Espresso.onView(ViewMatchers.withId(R.id.editTextTextEmailAddress))
                .perform(ViewActions.typeText("chef@gmail.com"), ViewActions.closeSoftKeyboard());

        Espresso.onView(ViewMatchers.withId(R.id.editTextTextPassword))
                .perform(ViewActions.typeText("wrong password"), ViewActions.closeSoftKeyboard());

        // Click the login button
        Espresso.onView(ViewMatchers.withId(R.id.loginButton)).perform(ViewActions.click());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if the chef activity is launched after failed login. it it is, then the test should fail
        Espresso.onView(ViewMatchers.withId(R.id.chefOrderRecycleView)).check(ViewAssertions.doesNotExist());
    }

    @Test
    public void testAdminLoginSuccess() {
        // Enter email and password
        Espresso.onView(ViewMatchers.withId(R.id.editTextTextEmailAddress))
                .perform(ViewActions.typeText("admin@gmail.com"), ViewActions.closeSoftKeyboard());

        Espresso.onView(ViewMatchers.withId(R.id.editTextTextPassword))
                .perform(ViewActions.typeText("password"), ViewActions.closeSoftKeyboard());

        // Click the login button
        Espresso.onView(ViewMatchers.withId(R.id.loginButton)).perform(ViewActions.click());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if the admin activity is launched after successful login
        Espresso.onView(ViewMatchers.withId(R.id.adminViewPager)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
    @Test
    public void testAdminLoginFailure() {
        // Enter email and password
        Espresso.onView(ViewMatchers.withId(R.id.editTextTextEmailAddress))
                .perform(ViewActions.typeText("admin@gmail.com"), ViewActions.closeSoftKeyboard());

        Espresso.onView(ViewMatchers.withId(R.id.editTextTextPassword))
                .perform(ViewActions.typeText("wrong password"), ViewActions.closeSoftKeyboard());

        // Click the login button
        Espresso.onView(ViewMatchers.withId(R.id.loginButton)).perform(ViewActions.click());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if the admin activity is launched after failed login. it it is, then the test should fail
        Espresso.onView(ViewMatchers.withId(R.id.adminViewPager)).check(ViewAssertions.doesNotExist());
    }
}
