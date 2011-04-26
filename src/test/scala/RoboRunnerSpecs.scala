package org.brechtel.specs2
import org.specs2.mutable.Specification
import org.specs2.mock.Mockito

class RoboRunnerSpecs extends RoboRunner with Mockito {
  "getting fragments" should {
    "result in fragments from a RobolectricClassLoader" in { pending }
  }

  "executing fragments" should {
    "cause Android environment to be setup" in { pending }
    "cause Android environment to be recreated" in { pending }
  }
}
