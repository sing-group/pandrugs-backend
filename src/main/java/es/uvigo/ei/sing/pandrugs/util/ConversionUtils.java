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

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class to convert between types.
 * 
 * @author Miguel Reboiro-Jato
 *
 */
public final class ConversionUtils {
	private ConversionUtils() {}

	/**
	 * Converts an input array into a linked map ({@link LinkedHashMap} using
	 * the array values as the values of the map and converting then into a key
	 * using a provided function. 
	 * 
	 * @param values an array with the values of the map.
	 * @param valueToKey a function to convert the values into the keys of the
	 * map.
	 * @param <K> type of the map's key.
	 * @param <V> type of the map's and array's values.
	 * @return a linked hash map with the values of the array and the generated
	 * keys.
	 */
	public static <K, V> Map<K, V> arrayToLinkedMap(
		V[] values, Function<V, K> valueToKey
	) {
		return Stream.of(requireNonNull(values))
			.collect(toLinkedHashMap(valueToKey));
	}
	
	/**
     * Returns a {@code Collector} that accumulates elements into a
     * {@code LinkedHashMap} whose keys and values are the result of applying
     * the provided mapping functions to the input elements.
     * 
     * This method relies on the
     * {@link Collectors#toMap(Function, Function, BinaryOperator, Supplier)}
     * method.
	 * 
     * @param keyMapper a mapping function to produce keys.
     * @param valueMapper a mapping function to produce values.
     * @param <T> the type of the input elements.
     * @param <K> the output type of the key mapping function.
     * @param <U> the output type of the value mapping function.
	 * @return a {@code Collector} which collects elements into a
	 * {@code LinkedHashMap} whose keys are the result of applying a key
	 * mapping function to the input elements, and whose values are the result
	 * of applying a value mapping function to all input elements equal to the
	 * key and combining them using the merge function.
	 */
	public static <T, K, U> Collector<T, ?, Map<K,U>> toLinkedHashMap(
		Function<? super T, ? extends K> keyMapper,
		Function<? super T, ? extends U> valueMapper
	) {
		return Collectors.toMap(
			keyMapper,
			valueMapper,
			(U u, U v) -> {
				throw new IllegalStateException(
					String.format("Duplicate key")
				);
			},
			LinkedHashMap::new
		);
	}

	/**
     * Returns a {@code Collector} that accumulates elements into a
     * {@code LinkedHashMap} whose keys and values are the result of applying
     * the provided mapping functions to the input elements.
     * 
     * This method relies on the
     * {@link Collectors#toMap(Function, Function, BinaryOperator, Supplier)}
     * method.
	 * 
	 * @param valueToKey function to convert a value into a key.
     * @param <K> the output type of the key mapping function
     * @param <V> the output type of the value mapping function
	 * @return a {@code Collector} which collects elements into a
	 * {@code LinkedHashMap} whose keys are the result of applying a key
	 * mapping function to the input elements, and whose values are the result
	 * of applying a value mapping function to all input elements equal to the
	 * key and combining them using the merge function.
	 */
	public static <V, K> Collector<V, ?, LinkedHashMap<K, V>> toLinkedHashMap(
		Function<V, K> valueToKey
	) {
		return Collectors.toMap(
			valueToKey,
			Function.identity(),
			(V u, V v) -> {
				throw new IllegalStateException(
					String.format("Duplicate key")
				);
			},
			LinkedHashMap::new
		);
	}
	
    /**
     * Returns a {@code Collector} that accumulates the input elements into a
     * new {@code LinkedHashSet}. There are no guarantees on the type,
     * mutability, serializability, or thread-safety of the
     * {@code LinkedHashSet} returned.
     *
     * @param <T> the type of the input elements.
     * @return a {@code Collector} which collects all the input elements into a
     * {@code LinkedHashSet}.
     */
	public static <T> Collector<T, ?, Set<T>> toLinkedHashSet() {
		return new Collector<T, Set<T>, Set<T>>() {
	        @Override
	        public BiConsumer<Set<T>, T> accumulator() {
	            return Set::add;
	        }
	
	        @Override
	        public Supplier<Set<T>> supplier() {
	            return LinkedHashSet::new;
	        }
	
	        @Override
	        public BinaryOperator<Set<T>> combiner() {
	            return (left, right) -> { left.addAll(right); return left; };
	        }
	
	        @Override
	        public Function<Set<T>, Set<T>> finisher() {
	            return x -> x;
	        }
	
	        @Override
	        public Set<Characteristics> characteristics() {
	            return Collections.singleton(Characteristics.IDENTITY_FINISH);
	        }
	    };
	}
}
