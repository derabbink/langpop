package com.abbink.langpop.web.auth

import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.ArrayList
import java.util.Date
import java.util.Properties
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.utils.URIBuilder
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.apache.http.HttpResponse
import org.apache.http.NameValuePair
import com.typesafe.config.ConfigFactory
import org.apache.http.util.EntityUtils
import org.apache.http.client.utils.URLEncodedUtils
import java.net.URI
import java.nio.charset.Charset

trait StackOverflowAuth {
	
	def isAuthenticated() : Boolean
	
	def buildOAuthUrl() : String
	
	def clearAuth() : Unit
	
	def finalizeAuth(code : String) : Unit
}

trait StackOverflowAuthComponent {
	def stackOverflowAuth:StackOverflowAuth
	
	object StackOverflowAuthImpl extends StackOverflowAuth {
		
		val config = ConfigFactory.load()
		val mergedConfig = config.getConfig("langpop-web").withFallback(config)
		
		val client_id = mergedConfig.getString("langpop.web.auth.stackoverflow.client_id")
		val client_secret = mergedConfig.getString("langpop.web.auth.stackoverflow.client_secret")
		val scope = "no_expiry"
		val redirect_uri = mergedConfig.getString("langpop.web.auth.stackoverflow.redirect_uri")
		val credentialsFileName = mergedConfig.getString("langpop.web.auth.stackoverflow.credentialsFile")
		
		var access_token : Option[String] = None
		var expires : Option[Long] = None
		readAuth()
		
		def isAuthenticated() = {
			access_token match {
				case None => false
				case Some(t) => expires match {
					case None => true
					case Some(e) => e > (new Date().getTime()/1000).toLong
				}
			}
		}
		
		def buildOAuthUrl() = {
			val uriBuilder = new URIBuilder();
			uriBuilder.setScheme("https").setHost("stackexchange.com").setPath("/oauth")
				.setParameter("client_id", client_id)
				.setParameter("scope", scope)
				.setParameter("redirect_uri", redirect_uri)
			uriBuilder.build.toString()
		}
		
		/**
		  * reads access token and expiration timestamp from properties file
		  */
		private def readAuth() : Unit = {
			access_token = None
			expires = None
			try {
				val fs : InputStream = new FileInputStream(credentialsFileName);
				val props : Properties = new Properties()
				props.load(fs);
				fs.close()
				
				val token = props.getProperty("access_token")
				val exp = props.getProperty("expires")
				access_token = token match {
					case null => None
					case x => Some(x)
				}
				expires = exp match {case null => None case x => Some(x.toLong)}
			}
			catch {
				case e => //TODO
			}
			(access_token, expires)
		}
		
		/**
		  * clears all auth data (i.e. signs out)
		  */
		def clearAuth() = {
			access_token = None
			expires = None
			try {
				val props = new Properties()
				val fs : OutputStream = new FileOutputStream(credentialsFileName)
				props.store(fs, null)
				fs.close()
			}
			catch {
				case e => //TODO
			}
		}
		
		def finalizeAuth(code : String) = {
			val uriBuilder = new URIBuilder()
			uriBuilder.setScheme("https").setHost("stackexchange.com").setPath("/oauth/access_token")
			val client : HttpClient = new DefaultHttpClient()
			val formparams : java.util.List[NameValuePair] = new ArrayList[NameValuePair]()
			formparams.add(new BasicNameValuePair("client_id", client_id))
			formparams.add(new BasicNameValuePair("client_secret", client_secret))
			formparams.add(new BasicNameValuePair("code", code))
			formparams.add(new BasicNameValuePair("redirect_uri", redirect_uri))
			val entity : UrlEncodedFormEntity = new UrlEncodedFormEntity(formparams, "UTF-8")
			val post = new HttpPost(uriBuilder.build())
			post.setEntity(entity)
			val response : HttpResponse = client.execute(post)
			
			if (response.getStatusLine().getStatusCode() != 400) {
				var access_token : Option[String] = None
				var expires : Option[Long] = None
				
				val entity = response.getEntity()
				val entityContent = EntityUtils.toString(entity)
				val entities : java.util.List[NameValuePair] = URLEncodedUtils.parse(
						entityContent,
						Charset.forName(entity.getContentEncoding() match {
							case null => "ISO-8859-1" //default from EntityUtils
							case x => x.getValue()
						}))
				val iter = entities.iterator()
				while (iter.hasNext()) {
					val pair : NameValuePair = iter.next()
					pair.getName() match {
						case "access_token" => access_token = Some(pair.getValue())
						case "expires" => expires = Some(pair.getValue().toLong)
						case _ => //ignore
					}
				}
				
				writeAuth(access_token, expires)
			}
		}
		
		private def writeAuth(accessToken : Option[String], expires : Option[Long]) = {
			if (accessToken == None) {
				clearAuth
			}
			else {
				this.access_token = accessToken
				this.expires = expires
				try {
					val props = new Properties()
					props.setProperty("access_token", accessToken.get);
					expires match {
						case Some(timestamp) => props.setProperty("expires", timestamp.toString())
						case _ => //ignore
					}
					val fs : OutputStream = new FileOutputStream(credentialsFileName);
					props.store(fs, null);
					fs.close()
				}
				catch {
					case e => //TODO
				}
			}
		}
	}
}
