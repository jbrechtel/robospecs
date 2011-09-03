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
    val snapshots = "snapshots" at "http://scala-tools.org/repo-snapshots"
    val releases  = "releases" at "http://scala-tools.org/repo-releases"


Add a Maven dependency
-----------

    <dependency>
        <groupId>com.github.jbrechtel</groupId>
        <artifactId>robospecs</artifactId>
        <version>0.1-SNAPSHOT</version>
        <scope>test</scope>
    </dependency>

WARNING: When you use RoboSpecs do not attempt to place Android SDK code within the "should {} codeblock" or outside of it. Robolectric shadows are only attached within the "in {} codeblocks", so Android SDK code only works within them.
-------

Example project
--------------

See a very simple, but working, example project here: [https://github.com/jbrechtel/robospecs_example](https://github.com/jbrechtel/robospecs_example)

Here's the specs from that projects

Then you can mix it into your Specs2 specs like so:

	package com.brechtel.toaster

	import org.specs2.mutable._
	import org.specs2.mock.Mockito
	import org.specs2.matcher.{Expectable, Matcher}
	import org.specs2.specification.BeforeExample
	import com.github.jbrechtel.robospecs.RoboSpecs
	import com.xtremelabs.robolectric.shadows._

	class MainActivitySpecs extends RoboSpecs with Mockito {

	  "onCreate" should {
	    "set the content view" in {
	      val activity = new MainActivity()
	      activity.onCreate(null)
	      activity.findViewById(R.id.message) must not beNull
	    }
	  }

	  "showMessageButton" should {
	    "be the Show Message button in the view" in {
	      val activity = new MainActivity()
	      activity.onCreate(null)
	      activity.showMessageButton == activity.findViewById(R.id.show_message)
	    }
	  }

	  "messageEditText" should {
	    "be the EditText in the view" in {
	      val activity = new MainActivity()
	      activity.onCreate(null)
	      activity.messageEditText == activity.findViewById(R.id.message)
	    }
	  }

	  "clicking the showMessageButton" should {
	    "show a toast popup with text from the message input field" in {
	      val activity = new MainActivity()
	      activity.onCreate(null)
	      activity.messageEditText.setText("expected message")
	      activity.showMessageButton.performClick()
	      ShadowToast.getTextOfLatestToast must beEqualTo("expected message")
	    }
	  }
	}
