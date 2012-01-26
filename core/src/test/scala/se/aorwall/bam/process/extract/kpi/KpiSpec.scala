package se.aorwall.bam.process.extract.kpi
import org.junit.runner.RunWith
import org.scalatest.matchers.MustMatchers
import org.scalatest.WordSpec
import se.aorwall.bam.model.events.DataEvent
import grizzled.slf4j.Logging
import se.aorwall.bam.model.events.Event
import org.scalatest.junit.JUnitRunner
import se.aorwall.bam.model.events.KpiEvent

@RunWith(classOf[JUnitRunner])
class KpiSpec extends WordSpec with MustMatchers with Logging{

	val event = new DataEvent("eventName", "id", 0L, "{ \"doubleField\": \"123.42\", \"intField\": \"123\", \"anotherField\": \"anothervalue\"}");

	"Keyword" must {

		"extract float from json messages" in {
			val kpi = new Kpi("keyword", None, "doubleField")
			val kpiEvent = kpi.extract(event)

			kpiEvent match {
				case Some(e: KpiEvent) => e.value must be (123.42)
				case e => fail("expected an instance of se.aorwall.bam.model.events.KpiEvent but found: " + e)
			}		
			
		}
		
		
		"extract int (as float from json messages" in {
		   val kpi = new Kpi("keyword", None, "intField")
			val kpiEvent = kpi.extract(event)

			kpiEvent match {
				case Some(e: KpiEvent) => e.value must be (123)
				case e => fail("expected an instance of se.aorwall.bam.model.events.KpiEvent but found: " + e)
			}					
		}
				
		"receive None when a non-numeric value is provided" in {
		   val kpi = new Kpi("keyword", None, "anotherField")
			val kpiEvent = kpi.extract(event)

			kpiEvent match {
				case None => 
				case e => fail("expected None but found: " + e)
			}					
		}
	}

}