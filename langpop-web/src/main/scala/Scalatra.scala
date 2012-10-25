import com.abbink.langpop.web._

import org.scalatra._
import javax.servlet.ServletContext
import com.abbink.langpop.aggregate.Aggregator

/**
 * This is the Scalatra bootstrap file. You can use it to mount servlets or
 * filters. It's also a good place to put initialization code which needs to
 * run at application start (e.g. database configurations), and init params.
 */
class Scalatra extends LifeCycle {
	override def init(context: ServletContext) {
		
		Aggregator.start()
		
		// Mount one or more servlets
		context.mount(new LangpopServlet, "/langpop/*")
	}
}
