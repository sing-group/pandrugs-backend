package es.uvigo.ei.sing.pandrugsdb.util;

import java.util.function.Function;

@FunctionalInterface
public interface ThrowingFunction<T, R> extends Function<T, R> {
	@Override
	public default R apply(T t) throws WrapperRuntimeException {
		try {
			return this.throwingApply(t);
		} catch (Throwable e) {
			throw new WrapperRuntimeException(e);
		}
	}
	
	public R throwingApply(T t) throws Throwable;
	
	public static <T, R> Function<T, R> wrap(ThrowingFunction<T, R> function) {
		return function::apply;
	}
}
