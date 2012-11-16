import org.scalatra.LifeCycle
import com.abbink.langpop.web.ComponentRegistry
import com.abbink.langpop.web.LangpopServlet
import com.abbink.langpop.web.AuthServlet
import javax.servlet.ServletContext
import com.abbink.langpop.web.StatusServlet

/**
  * This is the Scalatra bootstrap file. You can use it to mount servlets or
  * filters. It's also a good place to put initialization code which needs to
  * run at application start (e.g. database configurations), and init params.
  */
class Scalatra extends LifeCycle with ComponentRegistry {
	override def init(context: ServletContext) {
		
		try{
			println("doing Quick & Dirty init")
			aggregate.aggregator.init()
			val token = stackOverflowAuth.token()
			if (token != None)
				query.querySystem.startStackOverflow(token.get, stackOverflowAuth.appKey())
		}
		
		// Mount one or more servlets
		context.mount(new LangpopServlet, "/langpop/*")
		context.mount(new AuthServlet, "/auth/*")
		context.mount(new StatusServlet, "/status/*")
	}
}
