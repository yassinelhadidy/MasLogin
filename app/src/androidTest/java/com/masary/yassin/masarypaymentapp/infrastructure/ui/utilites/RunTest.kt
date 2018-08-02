package com.masary.yassin.masarypaymentapp.infrastructure.ui.utilites

import android.support.test.filters.Suppress

import org.junit.runner.RunWith

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber

/**
 * Created by yassin on 28/9/18.
 *
 */
@Suppress
@RunWith(Cucumber::class)
@CucumberOptions(features = ["features"], glue = ["com.masary.yassin.masarypaymentapp.ui"])
class RunTest