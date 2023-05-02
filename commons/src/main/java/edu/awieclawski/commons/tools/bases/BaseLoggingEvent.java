package edu.awieclawski.commons.tools.bases;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

/**
 * source: https://www.baeldung.com/junit-asserting-logs
 * 
 * Convenient appender to be able to check slf4j invocations
 */
public abstract class BaseLoggingEvent<T> extends ListAppender<ILoggingEvent> {

	public final String NO_PACKAGE = "package.not.exists";

	protected String getPackageNameOfGenericClass() {
		T t = getGenericInstance();

		return t != null
				? t.getClass().getPackage().getName()
				: NO_PACKAGE;
	}

	private T getGenericInstance() {
		return ReflectionUtils.getFirstArgumentInstance(this);
	}

}
