/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2018 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña and Miguel Reboiro-Jato
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package es.uvigo.ei.sing.pandrugs.util;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Stream.concat;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.reportMatcher;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.easymock.IExpectationSetters;

public final class ExpectWithVarargs {
	private ExpectWithVarargs() {}
	
	public static <T, V> IExpectationSetters<T> expectWithUnorderedVarargs(Object mock, String methodName, V[] varargs, Object ... additionalParams) {
		final Class<?>[] additionalParamTypes = stream(additionalParams).map(Object::getClass).toArray(Class[]::new);
		
		return expectWithUnorderedVarargs(mock, methodName, varargs, additionalParams, additionalParamTypes);
	}
	
	public static <T, V> IExpectationSetters<T> expectWithUnorderedVarargs(Object mock, String methodName, V[] varargs, Class<?> ... additionalParamTypes) {
		final Object[] additionalParameters = stream(additionalParamTypes)
			.map(EasyMock::anyObject)
		.toArray();
		
		return expectWithUnorderedVarargs(mock, methodName, varargs, additionalParameters, additionalParamTypes);
	}
	
	@SuppressWarnings("unchecked")
	public static <T, V> IExpectationSetters<T> expectWithUnorderedVarargs(Object mock, String methodName, V[] varargs, Object[] additionalParams, Class<?>[] additionalParamTypes) {
		final Class<?>[] parameterTypes = concat(stream(additionalParamTypes), Stream.of(varargs.getClass()))
			.toArray(Class[]::new);

		try {
			final Method method = mock.getClass().getMethod(methodName, parameterTypes);

			final InMatcherBuilder<V> builder = new InMatcherBuilder<>(varargs);
			final V[] varargsMatcher = stream(varargs).map(__ -> builder.matcher())
				.toArray(length -> (V[]) Array.newInstance(varargs.getClass().getComponentType(), length));
			
			final Object[] params = new Object[additionalParams.length + 1];
			System.arraycopy(additionalParams, 0, params, 0, additionalParams.length);
			params[params.length - 1] = varargsMatcher;
			
			return (IExpectationSetters<T>) expect(method.invoke(mock, params));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static class InMatcherBuilder<V> {
		private final InMatcher<V> matcher;
		
		public InMatcherBuilder(V[] values) {
			this.matcher = new InMatcher<>(values);
		}
		
		public V matcher() {
			matcher.register();
			
			reportMatcher(matcher);
			
			return null;
		}
	}
	
	private static class InMatcher<V> implements IArgumentMatcher {
		private final List<V> values;
		private final int expectedCount;
		private int actualCount;
		
		private Object lastValue;
		
		public InMatcher(V[] values) {
			this.values = new ArrayList<>(asList(values));
			this.expectedCount = this.values.size();
			this.actualCount = 0;
		}
		
		public void register() {
			this.actualCount++;
		}

		@Override
		public boolean matches(Object argument) {
			this.lastValue = argument;
			
			return this.actualCount == this.expectedCount && this.values.remove(argument);
		}

		@Override
		public void appendTo(StringBuffer buffer) {
			if (this.actualCount < this.values.size()) {
				buffer.append(String.format("insufficient varargs parameters. Expected %d but got %d", this.expectedCount, this.actualCount));
			} else if (this.actualCount > this.values.size()) {
				buffer.append(String.format("Too much varargs parameters. Expected %d but got %d", this.expectedCount, this.actualCount));
			} else {
				buffer.append("value '" + this.lastValue +  "'not included in: " + values);
			}
		}
		
	}
}
