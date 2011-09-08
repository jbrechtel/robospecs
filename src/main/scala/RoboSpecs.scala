package com.github.jbrechtel.robospecs

import com.xtremelabs.robolectric.res.ResourceLoader
import org.specs2.mutable.Specification
import com.xtremelabs.robolectric.bytecode.{RobolectricClassLoader, ShadowWrangler}
import android.net.Uri__FromAndroid
import org.specs2.specification._
import com.xtremelabs.robolectric.util.DatabaseConfig
import org.specs2.mutable.SpecificationFeatures
import com.xtremelabs.robolectric.internal.RealObject
import android.R
import java.io.File
import com.xtremelabs.robolectric.shadows.ShadowApplication
import com.xtremelabs.robolectric.{ApplicationResolver, Robolectric, RobolectricConfig}
import com.xtremelabs.robolectric.util.DatabaseConfig.DatabaseMap
import com.xtremelabs.robolectric.util.H2Map
import com.xtremelabs.robolectric.internal.RobolectricTestRunnerInterface
import javassist.Loader
import com.xtremelabs.robolectric.RobolectricTestRunner

trait RoboDb {def dbMap: DatabaseMap}

/**
 * Requires robolectric-sqlite.jar (com.seventheye.robolectric.sqlite.util.SQLiteMap)
 */
trait RoboSQLite extends RoboDb {
  //support for SQLite (https://github.com/cessationoftime/robolectric-sqlite)
  def dbMap = Class.forName("com.seventheye.robolectric.sqlite.util.SQLiteMap").newInstance().asInstanceOf[DatabaseMap]
}

//Support for H2
trait RoboH2 extends RoboDb {
  def dbMap = new H2Map()
}

//extend mutable Specification
trait RoboSpecs extends Specification with RoboSpecsWithInstrumentation { this:RoboDb =>  
    val loader = RoboSpecs.getClassLoader(this.getClass)
    val instrumentedClass = loader.bootstrap(this.getClass)
    Thread.currentThread().setContextClassLoader(loader);
    lazy val instrumentedInstance =    instrumentedClass.newInstance.asInstanceOf[RoboSpecsWithInstrumentation]
   def instrumentedFragments = super.is //to get the instrumentedFragments from the instrumentedInstance
  override def is = instrumentedInstance.setup(instrumentedInstance.instrumentedFragments) //Called By SpecificationStructure.content
}

//extend immutable Specification
trait RoboAcceptanceSpecs  extends org.specs2.Specification with RoboSpecsWithInstrumentation { this:RoboDb =>  
  val loader = RoboSpecs.getClassLoader(this.getClass)
  val instrumentedClass = loader.bootstrap(this.getClass)
  Thread.currentThread().setContextClassLoader(loader);
  
  lazy val instrumentedInstance = instrumentedClass.newInstance.asInstanceOf[RoboSpecsWithInstrumentation]
  override def map(f: => Fragments) = instrumentedInstance.setup(instrumentedInstance.is)
  def instrumentedFragments = is
}

trait RoboSpecsWithInstrumentation extends SpecificationStructure { this:RoboDb =>
  lazy val setup = new BeforeEach {
    def before { setupApplicationState() }
    def ^(fs: Fragments) = this(fs)
  }
  def instrumentedFragments: Fragments
    
  lazy val robolectricConfig = new RobolectricConfig(new File("."))
  
  //Use option to box and obscure the ResourceLoader type, this makes Maven Surefire (JUnit) happier when running individual tests with the -Dtest option
  private lazy val resourceLoader : Option[ResourceLoader] = {
    val rClassName: String = robolectricConfig.getRClassName
    val rClass: Class[_] = Class.forName(rClassName)
    Some(new ResourceLoader(robolectricConfig.getSdkVersion, rClass, robolectricConfig.getResourceDirectory, robolectricConfig.getAssetsDirectory))
  }

  def setupApplicationState() {
    robolectricConfig.validate()
    Robolectric.bindDefaultShadowClasses()    
    Robolectric.resetStaticState()    
    DatabaseConfig.setDatabaseMap(dbMap);//Set static DatabaseMap in DBConfig
    Robolectric.application = ShadowApplication.bind(new ApplicationResolver(robolectricConfig).resolveApplication, resourceLoader.get)
  }
}

object RoboSpecs {
  type RoboLoader = Loader {def bootstrap(testClass : Class[_] ) : Class[_]}
  
  lazy val classHandler = ShadowWrangler.getInstance
  
  private def isInstrumentedClass(clazz : Class[_]) =
   clazz.getClassLoader().getClass().getName().contains("RobolectricClassLoader") 
  
   private var loader :RoboLoader = null;

  
  def getClassLoader(clazz : Class[_]) : RoboLoader = {
 //Handle the case where normal Robolectric and Robospecs tests are both run by Maven Surefire and they must share an instrumented classloader
    if (loader == null) {
    loader = useCurrentClassLoader(clazz).orElse(getNewClassLoader).get;   
    
     ("org.specs2." :: "org.mockito." :: "scala." 
      :: classOf[RoboSpecsWithInstrumentation].getName() :: Nil)
     .foreach { x => loader.delegateLoadingOf(x) }
    }
  loader
  }
  
  private def useCurrentClassLoader(clazz : Class[_]) :Option[RoboLoader]  = 
    if (isInstrumentedClass(clazz)) {
      Some(clazz.getClassLoader().asInstanceOf[RoboLoader])
    } else None
  
  
  private def getNewClassLoader :Option[RoboLoader]  = {
    val loader = new RobolectricClassLoader(classHandler)

     List(classOf[Uri__FromAndroid],
          classOf[RobolectricTestRunnerInterface],
          classOf[RobolectricClassLoader],
          classOf[RealObject],
          classOf[ShadowWrangler],
          classOf[RobolectricConfig],
          classOf[R]).foreach { classToDelegate => loader.delegateLoadingOf(classToDelegate.getName) }
     RobolectricTestRunner.setDefaultLoader(loader);
     Some(loader)
  }
}