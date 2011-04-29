RoboSpecs
---------

RoboSpecs lets you use [Robolectric](http://pivotal.github.com/robolectric/) with [Specs2](http://specs2.org) to test drive your Android applications using Scala.

Using RoboSpecs is really easy.  Download the latest jar from: https://github.com/jbrechtel/robospecs/archives/master

(proper Maven dependency coming soon, I promise)

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