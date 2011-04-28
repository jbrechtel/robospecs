package org.brechtel.specs2
import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import com.xtremelabs.robolectric.bytecode.RobolectricClassLoader

class RoboRunnerSpecs extends Specification with Mockito {
  "a RoboRunner's instrumentedClass" should {
    "have been built using the RobolectricClassLoader" in {
      val spec = new InternalRoboRunnerSpecs()

      val expectedClass = classOf[RobolectricClassLoader].getName
      val actualClass = spec.instrumentedInstance.getClass.getClassLoader.getClass.getName

      actualClass must beEqualTo(expectedClass)
    }
  }

  "a RoboRunner's fragments" should {
     "be the same structure as its instrumentedInstance's fragments" in {
       val spec = new InternalRoboRunnerSpecs()
       spec.is.toString must beEqualTo(spec.instrumentedInstance.instrumentedFragments.toString)
     }
  }
}
