package com.abbink.langpop.query

import com.abbink.langpop.query.specific.CombinedSpecificEventExtractorFactoryComponent
import com.abbink.langpop.query.specific.CombinedSpecificEventExtractorFactory
import com.abbink.langpop.query.specific.github.GithubEventExtractorFactory
import com.abbink.langpop.query.specific.stackoverflow.StackoverflowEventExtractorFactory
import com.abbink.langpop.query.specific.github.GithubEventExtractorComponent
import com.abbink.langpop.query.specific.stackoverflow.StackoverflowEventExtractorComponent

trait ComponentRegistry extends
	QuerySystemComponent with
	CombinedSpecificEventExtractorFactoryComponent with
	GithubEventExtractorComponent with
	StackoverflowEventExtractorComponent
{
	def querySystem : QuerySystem = QuerySystemImpl
	def combinedSpecificEventExtractorFactory : CombinedSpecificEventExtractorFactory = CombinedSpecificEventExtractorFactoryImpl
	def githubEventExtractorFactory : GithubEventExtractorFactory = GithubEventExtractorFactoryImpl
	def stackoverflowEventExtractorFactory : StackoverflowEventExtractorFactory = StackoverflowEventExtractorFactoryImpl
}
