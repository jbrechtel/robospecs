package com.github.jbrechtel.robospecs

import org.specs2.mutable.Specification
import com.xtremelabs.robolectric.bytecode.{RobolectricClassLoader, ShadowWrangler}
import android.net.Uri__FromAndroid
import com.xtremelabs.robolectric.internal.RealObject
import android.R
import java.io.File
import com.xtremelabs.robolectric.res.ResourceLoader
import com.xtremelabs.robolectric.shadows.ShadowApplication
import com.xtremelabs.robolectric.{ApplicationResolver, Robolectric, RobolectricConfig}
import org.specs2.specification._

trait RoboSpecs extends Specification with RoboSpecsWithInstrumentation { 
  lazy val instrumentedClass = RoboSpecs.classLoader.bootstrap(this.getClass)
  lazy val instrumentedInstance = instrumentedClass.newInstance.asInstanceOf[RoboSpecsWithInstrumentation]
  def instrumentedFragments = super.is
  override def is = instrumentedInstance.setup(instrumentedInstance.instrumentedFragments)
}

trait RoboAcceptanceSpecs extends org.specs2.Specification with RoboSpecsWithInstrumentation { 
  lazy val instrumentedClass = RoboSpecs.classLoader.bootstrap(this.getClass)
  lazy val instrumentedInstance = instrumentedClass.newInstance.asInstanceOf[RoboSpecsWithInstrumentation]
  override def map(f: Fragments) = instrumentedInstance.setup(instrumentedInstance.is)
  def instrumentedFragments = is
}

trait RoboSpecsWithInstrumentation extends SpecificationStructure {
  lazy val setup = new BeforeEach {
    def before { setupApplicationState() }
	  def ^(fs: Fragments) = this(fs)
  }
  def instrumentedFragments: Fragments
  lazy val robolectricConfig = new RobolectricConfig(new File("./src/main"))
  lazy val resourceLoader = {
    val rClassName: String = robolectricConfig.getRClassName
    val rClass: Class[_] = Class.forName(rClassName)
    new ResourceLoader(robolectricConfig.getSdkVersion, rClass, robolectricConfig.getResourceDirectory, robolectricConfig.getAssetsDirectory)
  }

  def setupApplicationState() {
    robolectricConfig.validate()
    Robolectric.bindDefaultShadowClasses()
    Robolectric.resetStaticState()
    Robolectric.application = ShadowApplication.bind(new ApplicationResolver(robolectricConfig).resolveApplication, resourceLoader)
  }
}

object RoboSpecs {
  lazy val classHandler = ShadowWrangler.getInstance
  lazy val classLoader = {
     val loader = new RobolectricClassLoader(classHandler)
     loader.delegateLoadingOf("org.specs2.")
     loader.delegateLoadingOf("org.mockito.")
     loader.delegateLoadingOf("scala.")

     List(classOf[Uri__FromAndroid],
          classOf[RoboSpecsWithInstrumentation],
          classOf[RobolectricClassLoader],
          classOf[RealObject],
          classOf[ShadowWrangler],
          classOf[RobolectricConfig],
          classOf[R]).foreach { classToDelegate => loader.delegateLoadingOf(classToDelegate.getName) }

     loader
  }

}
