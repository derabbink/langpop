import org.scalatra.LifeCycle

import com.abbink.langpop.aggregate.Aggregator
import com.abbink.langpop.web.LangpopServlet

import javax.servlet.ServletContext

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
