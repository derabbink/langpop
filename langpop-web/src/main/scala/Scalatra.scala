import org.scalatra.LifeCycle

import com.abbink.langpop.web.ComponentRegistry
import com.abbink.langpop.web.LangpopServlet
import com.abbink.langpop.web.AuthServlet

import javax.servlet.ServletContext

/**
 * This is the Scalatra bootstrap file. You can use it to mount servlets or
 * filters. It's also a good place to put initialization code which needs to
 * run at application start (e.g. database configurations), and init params.
 */
class Scalatra extends LifeCycle with ComponentRegistry {
	override def init(context: ServletContext) {
		
		// Mount one or more servlets
		context.mount(new LangpopServlet, "/langpop/*")
		context.mount(new AuthServlet, "/auth/*")
	}
}
