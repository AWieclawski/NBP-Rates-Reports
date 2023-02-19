package edu.awieclawski.commons.tools;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import edu.awieclawski.commons.tools.bases.BaseLoggingEvent;

/**
 * Each logger test method should start with <setupMemoryAppender> method
 * 
 * Each logger test method should finish with <cleanUpMemoryAppender> method
 * 
 * @author awieclawski
 *
 * @param <T>
 */
public class MemmoryAppender<T> extends BaseLoggingEvent<T> {

	public void initMemoryAppender(Level LEVEL) {
		Logger logger = (Logger) LoggerFactory.getLogger(getPackageNameOfGenericClass());
		this.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
		logger.setLevel(LEVEL);
		logger.addAppender(this);
		super.start();
	}

	public void cleanUpMemoryAppender() {
		this.reset();
		super.stop();
	}

	private void reset() {
		this.list.clear();
	}

	public boolean contains(String string, Level level) {
		return this.list.stream()
				.anyMatch(event -> event.toString().contains(string)
						&& event.getLevel().equals(level));
	}

	public int countEventsForLogger() {
		return (int) this.list.stream()
				.filter(event -> event.getLoggerName().contains(getPackageNameOfGenericClass())).count();
	}

	public List<ILoggingEvent> search(String string) {
		return this.list.stream()
				.filter(event -> event.toString().contains(string))
				.collect(Collectors.toList());
	}

	public List<ILoggingEvent> search(String string, Level level) {
		return this.list.stream()
				.filter(event -> event.toString().contains(string)
						&& event.getLevel().equals(level))
				.collect(Collectors.toList());
	}

	public int getSize() {
		return this.list.size();
	}

	public List<ILoggingEvent> getLoggedEvents() {
		return Collections.unmodifiableList(this.list);
	}

}
