package edu.awieclawski.core.facades;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class BaseFacade {

	/**
	 * Function that returns a predicate that maintains state about what it's
	 * seen previously, and that returns whether the given element was seen for
	 * the first time. According to:
	 * https://stackoverflow.com/questions/23699371/java-8-distinct-by-property
	 * 
	 * @param <T>
	 * @param keyExtractor
	 * @return
	 */
	protected static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Set<Object> seen = ConcurrentHashMap.newKeySet();
		return t -> seen.add(keyExtractor.apply(t));
	}

}
