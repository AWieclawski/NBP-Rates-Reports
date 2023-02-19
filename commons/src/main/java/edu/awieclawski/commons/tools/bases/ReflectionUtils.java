package edu.awieclawski.commons.tools.bases;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectionUtils {

	private final static Logger log = LoggerFactory.getLogger(ReflectionUtils.class);

	@SuppressWarnings({"unchecked"})
	public static <T> T getFirstArgumentInstance(BaseLoggingEvent<T> thisClass) {
		T t = null;

		try {
			t = (T) ((Class<
					?>) ((ParameterizedType) thisClass.getClass().getGenericSuperclass()).getActualTypeArguments()[0])
							.getDeclaredConstructor().newInstance();
		} catch (InstantiationException e) {
			log.error("InstantiationException {}", e.getMessage());
		} catch (IllegalAccessException e) {
			log.error("IllegalAccessException  {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error("IllegalArgumentException  {}", e.getMessage());
		} catch (InvocationTargetException e) {
			log.error("InvocationTargetException  {}", e.getMessage());
		} catch (NoSuchMethodException e) {
			log.error("NoSuchMethodException  {}", e.getMessage());
		} catch (SecurityException e) {
			log.error("SecurityException  {}", e.getMessage());
		}

		return t;
	}

}
