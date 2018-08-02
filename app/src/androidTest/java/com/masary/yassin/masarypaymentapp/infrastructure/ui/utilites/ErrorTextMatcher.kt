package com.masary.yassin.masarypaymentapp.infrastructure.ui.utilites

import android.view.View
import android.widget.EditText

import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * Created by yassin on 08/9/18.
 *
 */

class ErrorTextMatcher private constructor(private val expectedError: String) : TypeSafeMatcher<View>() {

    public override fun matchesSafely(view: View): Boolean {
        return view is EditText && expectedError == view.error
    }

    override fun describeTo(description: Description) {
        description.appendText("with error: $expectedError")
    }

    companion object {

        fun hasErrorText(expectedError: String): Matcher<in View> {
            return ErrorTextMatcher(expectedError)
        }
    }
}
