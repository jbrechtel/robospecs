RoboSpecs
=========

RoboSpecs lets you use [Robolectric](http://pivotal.github.com/robolectric/) with [Specs2](http://specs2.org).

Using Robolectric with Specs2 means that you mean write unit tests for your Android apps 
in Scala and run them in a JVM.  Without Robolectric, Android unit tests must be run in 
an emulator.  This is slow and painful enough to make TDD impossible with Android.

Robolectric provides a JUnit test runner which does some class loader magic at runtime 
to give your unit tests fake versions of Android framework classes.  Robospecs is 
similar to Robolectric's JUnit test runner.  Robospecs does not use JUnit.

Using RoboSpecs is really easy.

Download the latest jar
----------------
Get it here [GitHub](https://github.com/jbrechtel/robospecs/archives/master)

Add an SBT dependency
-----------

    val robospecs = "com.github.jbrechtel" %% "robospecs" % "0.1-SNAPSHOT" % "test"
    val robospecsSnapshots  = "snapshots" at "http://jbrechtel.github.com/repo/snapshots"

Add a Maven dependency
-----------

    <dependency>
        <groupId>com.github.jbrechtel</groupId>
        <artifactId>robospecs</artifactId>
        <version>0.1-SNAPSHOT</version>
        <scope>test</scope>
    </dependency>
      
Then you can mix it into your Specs2 specs like so:

      package com.wtfware.disperse

      import org.specs2.mutable._
      import org.specs2.mock.Mockito
      import org.specs2.matcher.{Expectable, Matcher}
      import org.specs2.specification.BeforeExample
      import org.mockito.Matchers.{anyInt, argThat, eq => isEq }
      import org.brechtel.electricspecs.RoboSpecs
      import android.content.Context
      import android.appwidget.AppWidgetManager
      import android.widget.RemoteViews
      import providers.BigWidgetProvider

      class BigWidgetProviderSpecs extends Specification with Mockito with RoboSpecs {

        "onUpdate" should {

          "tell the appWidgetManager to update each appWidget provided" in {
            val provider = new BigWidgetProvider()
            val fakeContext = mock[Context]
            val fakeManager = mock[AppWidgetManager]
            provider.onUpdate(fakeContext, fakeManager, Array(1,2))
            there was one(fakeManager).updateAppWidget(isEq(1), any[RemoteViews])
            there was one(fakeManager).updateAppWidget(isEq(2), any[RemoteViews])
          }
        }
      }