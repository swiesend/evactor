package se.aorwall.bam.test.local

import se.aorwall.bam.process.build.simpleprocess.SimpleProcess
import scala.util.Random
import java.util.UUID
import akka.actor.ActorSystem
import akka.actor.Props
import se.aorwall.bam.collect.Collector
import se.aorwall.bam.process.ProcessorHandler
import se.aorwall.bam.process.build.request.Request
import se.aorwall.bam.model.events.SimpleProcessEvent
import se.aorwall.bam.process.ProcessorEventBusExtension
import se.aorwall.bam.test.TestKernel
import se.aorwall.bam.test.RequestGenerator
import akka.testkit.TestProbe
import akka.testkit.TestKit
import org.scalatest.matchers.MustMatchers
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FunSuite
import grizzled.slf4j.Logging
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import akka.util.duration._
import akka.actor.ActorRef
import se.aorwall.bam.model.events.RequestEvent
import se.aorwall.bam.process.Subscription

@RunWith(classOf[JUnitRunner])
class LocalTest(_system: ActorSystem) extends TestKit(_system) with FunSuite with MustMatchers with BeforeAndAfterAll with Logging {
  import TestKernel._
  
  def this() = this(ActorSystem("LocalTest"))

  override protected def afterAll(): scala.Unit = {
    system.shutdown()
  }

  test("Load test") {   
	  
    val noOfChannels: Int = channels.size
    val noOfRequestsPerChannel: Int = 10
    val threads = 10
    
    // initiate local environment
    val collector = system.actorOf(Props[Collector], name = "collect")
    val processorHandler = system.actorOf(Props[ProcessorHandler], name = "process")
    
    // set up request processors
    for(channel <- channels){
      processorHandler ! new Request(channel, List(new Subscription(Some("LogEvent"), Some(channel), None)), requestTimeout)
    }
    
	  // run test
	  val countProbe = TestProbe()
	  
    val processCountProbe = TestProbe()    
    val classifier = new Subscription(Some("RequestEvent"), None, None)
	  ProcessorEventBusExtension(system).subscribe(processCountProbe.ref, classifier)
	  
	  val requestGenerators = List.fill(threads)(system.actorOf(Props(new RequestGenerator(channels, collector, countProbe.ref, noOfRequestsPerChannel))))
        	  
	  Thread.sleep(100)	  
	  
    val start = System.currentTimeMillis
    
    for(requestGen <- requestGenerators) {
      requestGen ! None  
    }
    
	  // verify that all processes were created
	  val count = noOfChannels * noOfRequestsPerChannel * threads
	  
	  countProbe.receiveN(count, 2 minutes)    
	  println("Finished sending log events in " + (System.currentTimeMillis - start) + "ms")
  
	  processCountProbe.receiveN(count, 2 minutes)
    println("Finished processing "+ count +" process events in " + (System.currentTimeMillis - start) + "ms")
  }
}