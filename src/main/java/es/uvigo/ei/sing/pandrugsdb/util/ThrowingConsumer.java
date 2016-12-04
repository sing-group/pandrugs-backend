package es.uvigo.ei.sing.pandrugsdb.util;

import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowingConsumer<T> extends Consumer<T> {
	@Override
	public default void accept(T value) throws WrapperRuntimeException {
		try {
			this.throwingAccept(value);
		} catch (Throwable e) {
			throw new WrapperRuntimeException(e);
		}
	}
	
	public void throwingAccept(T value) throws Throwable;
	
	public static <T> Consumer<T> wrap(ThrowingConsumer<T> consumer) {
		return consumer::accept;
	}
}