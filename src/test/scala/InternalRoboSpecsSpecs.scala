package org.brechtel.electricspecs

import java.io.File
import com.xtremelabs.robolectric.{RobolectricConfig, Robolectric}

class InternalRoboSpecsSpecs extends RoboSpecs {
  override lazy val robolectricConfig = new RobolectricConfig(new File("./src/test/emptyAndroidProject"))

  "a running spec" should {
    "have an Android application" in {
      Robolectric.application must not beNull
    }
  }
}