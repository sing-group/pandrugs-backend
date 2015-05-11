/*
 * #%L
 * PanDrugsDB Backend
 * %%
 * Copyright (C) 2015 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña and Miguel Reboiro-Jato
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
package es.uvigo.ei.sing.pandrugsdb.util;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Utility class with several methods for type checking. These methods are
 * based on the {@link Objects#requireNonNull} methods.
 * 
 * @author Miguel Reboiro-Jato
 *
 */
public final class Checks {
	public final static String EMAIL_PATTERN = 
	"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	public final static String MD5_PATTERN =
	"^[a-f0-9]{32}$";
	public final static String UUID_PATTERN = 
	"^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$";

	private Checks() {}
	
	/**
	 * Checks if a string is empty. This method supports {@code null} strings,
	 * in which case the value returned is {@code true}.
	 * 
	 * @param string the string checked.
	 * @return {@code true} if the string is {@code null}. Otherwise, the
	 * return value of the {@code isEmpty()} method of the string.
	 */
	public static boolean isEmpty(String string) {
		return string == null || string.isEmpty();
	}
	
	/**
	 * Checks if the provided array is empty.
	 * 
	 * @param values the array checked.
	 * @return {@code true} if the array is {@code null} or is empty.
	 * {@code false} otherwise.
	 */
	public static boolean isEmpty(byte[] values) {
		return values == null || values.length == 0;
	}

	/**
	 * Checks if the provided collection is empty.
	 * 
	 * @param collection the collection checked.
	 * @return {@code true} if the collection is {@code null}. Otherwise, the
	 * return value of the {@code isEmpty()} method of the collection.
	 */
	public static boolean isEmpty(Collection<?> collection) {
		return !checkRequisite(Checks::requireNonEmpty, collection);
	}
	
	/**
	 * Checks if the provided string is a valid UUID.
	 * 
	 * @param uuid string to be checked.
	 * @return {@code true} if the string is a valid UUID. {@code false}
	 * otherwise.
	 */
	public static boolean isUUID(String uuid) {
		return checkRequisite(Checks::requireUUID, uuid);
	}
	
	private static <T> boolean checkRequisite(Function<T, T> require, T value) {
		try {
			require.apply(value);
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Checks if a string contains a valid email address.
	 * 
	 * @param email string to be checked.
	 * @return the provided string.
	 * @throws NullPointerException if {@code email} is {@code null}.
	 * @throws IllegalArgumentException if {@code email} does not contains a
	 * valid email address.
	 * @see #EMAIL_PATTERN
	 */
	public static String requireEmail(String email) {
		return requireEmail(email, null);
	}

	/**
	 * Checks if a string contains a valid email address.
	 * 
	 * @param email string to be checked.
	 * @param message the error message.
	 * @return the provided string.
	 * @throws NullPointerException if {@code email} is {@code null}.
	 * @throws IllegalArgumentException if {@code email} does not contains a
	 * valid email address.
	 * @see #EMAIL_PATTERN
	 */
	public static String requireEmail(String email, String message) {
		return requirePattern(email, Checks.EMAIL_PATTERN, message);
	}

	/**
	 * Checks if a string contains a valid MD5 value.
	 * 
	 * @param md5 string to be checked.
	 * @return the provided string.
	 * @throws NullPointerException if {@code md5} is {@code null}.
	 * @throws IllegalArgumentException if {@code md5} does not contains a
	 * valid MD5 value.
	 * @see #MD5_PATTERN
	 */
	public static String requireMD5(String md5) {
		return requireMD5(md5, null);
	}

	/**
	 * Checks if a string contains a valid MD5 value.
	 * 
	 * @param md5 string to be checked.
	 * @param message the error message.
	 * @return the provided string.
	 * @throws NullPointerException if {@code md5} is {@code null}.
	 * @throws IllegalArgumentException if {@code md5} does not contains a
	 * valid MD5 value.
	 * @see #MD5_PATTERN
	 */
	public static String requireMD5(String md5, String message) {
		return requirePattern(md5, MD5_PATTERN, message);
	}

	/**
	 * Checks if a string contains a valid UUID.
	 * 
	 * @param uuid string to be checked.
	 * @return the provided string.
	 * @throws NullPointerException if {@code uuid} is {@code null}.
	 * @throws IllegalArgumentException if {@code uuid} does not contains a
	 * valid UUID.
	 * @see #UUID_PATTERN
	 */
	public static String requireUUID(String uuid) {
		return requireUUID(uuid, null);
	}

	/**
	 * Checks if a string contains a valid UUID.
	 * 
	 * @param uuid string to be checked.
	 * @param message the error message.
	 * @return the provided string.
	 * @throws NullPointerException if {@code uuid} is {@code null}.
	 * @throws IllegalArgumentException if {@code uuid} does not contains a
	 * valid UUID.
	 * @see #UUID_PATTERN
	 */
	public static String requireUUID(String uuid, String message) {
		return requirePattern(uuid, UUID_PATTERN, message);
	}

	/**
	 * Checks if a string matches a regular expression pattern.
	 * 
	 * @param value string to be checked.
	 * @param pattern the regular expression used to check the provided string.
	 * @param message the error message.
	 * @return the provided string.
	 * @throws NullPointerException if {@code value} is {@code null}.
	 * @throws IllegalArgumentException if {@code value} does not match the
	 * pattern.
	 */
	public static String requirePattern(String value, String pattern, String message) {
		if (requireNonNull(value, message).matches(pattern)) {
			return value;
		} else {
			throw new IllegalArgumentException(message == null ?
				"Value did not match pattern" : message);
		}
	}
	
	/**
	 * Checks if the length of a string is between two values.
	 * 
	 * @param string string to be checked.
	 * @param min minimum length of the string.
	 * @param max maximum length of the string.
	 * @return the provided string.
	 * @throws NullPointerException if {@code string} is {@code null}.
	 * @throws IllegalArgumentException if {@code string} does not have the 
	 * required length.
	 */
	public static String requireStringSize(String string, int min, int max) {
		return requireStringSize(string, min, max,
			String.format("string length must be between %d and %d", min, max)
		);
	}

	
	/**
	 * Checks if the length of a string is between two values.
	 * 
	 * @param string string to be checked.
	 * @param min minimum length of the string.
	 * @param max maximum length of the string.
	 * @param message the error message.
	 * @return the provided string.
	 * @throws NullPointerException if {@code string} is {@code null}.
	 * @throws IllegalArgumentException if {@code string} does not have the 
	 * required length.
	 */
	public static String requireStringSize(String string, int min, int max, String message) {
		return check(requireNonNull(string, message), 
			s -> s.length() >= min && s.length() <= max,
			message
		);
	}
	
	/**
	 * Checks if the provided string is not empty.
	 * 
	 * @param string the string checked.
	 * @return the provided string.
	 * @throws NullPointerException if the string is {@code null}.
	 * @throws IllegalArgumentException if the string is empty.
	 */
	public static String requireNonEmpty(String string) {
		return requireNonEmpty(string, "string can't be empty");
	}
	
	/**
	 * Checks if the provided string is not empty.
	 * 
	 * @param string the string checked.
	 * @param message the error message.
	 * @return the provided string.
	 * @throws NullPointerException if the string is {@code null}.
	 * @throws IllegalArgumentException if the string is empty.
	 */
	public static String requireNonEmpty(String string, String message) {
		return check(requireNonNull(string, message), 
			not(String::isEmpty),
			message
		);
	}
	
	/**
	 * Checks if the provided map is not empty.
	 * 
	 * @param map the map checked.
	 * @param <K> type of the map's keys.
	 * @param <V> type of the map's values.
	 * @param <M> type of the mapl
	 * @return the provided map.
	 * @throws NullPointerException if the map is {@code null}.
	 * @throws IllegalArgumentException if the map is empty.
	 */
	public static <K, V, M extends Map<K, V>> M requireNonEmpty(M map) {
		return check(requireNonNull(map), 
			not(Map::isEmpty),
			"map can't be empty"
		);
	}

	/**
	 * Checks if the provided collection is not empty.
	 * 
	 * @param collection the collection checked.
	 * @param <C> type of the collection.
	 * @return the provided collection.
	 * @throws NullPointerException if the collection is {@code null}.
	 * @throws IllegalArgumentException if the collection is empty.
	 */
	public static <C extends Collection<?>> C requireNonEmpty(C collection) {
		return requireNonEmpty(collection, "collection can't be empty");
	}
	
	/**
	 * Checks if the provided collection is not empty.
	 * 
	 * @param collection the collection checked.
	 * @param message the message included in the exception thrown.
	 * @param <C> type of the collection.
	 * @return the provided collection.
	 * @throws NullPointerException if the collection is {@code null}.
	 * @throws IllegalArgumentException if the collection is empty.
	 */
	public static <C extends Collection<?>> C requireNonEmpty(
		C collection, String message) {
		return check(requireNonNull(collection, message), 
			not(Collection::isEmpty),
			message
		);
	}
	
	/**
	 * Checks if the provided array is not empty.
	 * 
	 * @param values the array checked.
	 * @param <T> type of the array items.
	 * @return the provided array.
	 * @throws NullPointerException if the array is {@code null}.
	 * @throws IllegalArgumentException if the array is empty.
	 */
	public static <T> T[] requireNonEmpty(T[] values) {
		return requireNonEmpty(values, "array can't be empty");
	}
	
	/**
	 * Checks if the provided array is not empty.
	 * 
	 * @param values the array checked.
	 * @param message the message included in the exception thrown.
	 * @param <T> type of the array items.
	 * @return the provided array.
	 * @throws NullPointerException if the array is {@code null}.
	 * @throws IllegalArgumentException if the array is empty.
	 */
	public static <T> T[] requireNonEmpty(T[] values, String message) {
		return check(requireNonNull(values), 
			x -> x.length > 0,
			message
		);
	}
	
	/**
	 * Checks if the provided array is not empty.
	 * 
	 * @param values the array checked.
	 * @return the provided array.
	 * @throws NullPointerException if the array is {@code null}.
	 * @throws IllegalArgumentException if the array is empty.
	 */
	public static int[] requireNonEmpty(int[] values) {
		return check(requireNonNull(values), 
			x -> x.length > 0,
			"array can't be empty"
		);
	}
	
	/**
	 * Checks if the provided array is neither {@code null} and not contains
	 * {@code null} values.
	 * 
	 * @param values the array to be checked.
	 * @param <T> type of the array items.
	 * @return the provided array.
	 * @throws NullPointerException if the array is {@code null} or if the
	 * array contains a {@code null} value.
	 */
	public static <T> T[] requireNonNullArray(T[] values) {
		return check(requireNonNull(values), 
			x -> Stream.of(x).allMatch(y -> y != null),
			() -> new NullPointerException(
				"array can't be null and can't contain a null value")
		);
	}

	/**
	 * Checks if the provided number is not a NaN.
	 * 
	 * @param number the number to be checked.
	 * @return the provided number.
	 * @throws IllegalArgumentException if the number is NaN.
	 */
	public static double requireNonNaN(double number) {
		return requireNonNaN(number, "number can't be NaN");
	}

	/**
	 * Checks if the provided number is not a NaN.
	 * 
	 * @param number the number to be checked.
	 * @param message the message of the exception thrown if the number is
	 * NaN. 
	 * @return the provided number.
	 * @throws IllegalArgumentException if the number is NaN. The message of
	 * this exception will be the provided.
	 */
	public static double requireNonNaN(double number, String message) {
		return check(number, notDouble(Double::isNaN), message);
	}

	/**
	 * Checks if the provided number is finite.
	 * 
	 * @param number the number to be checked.
	 * @return the provided number.
	 * @throws IllegalArgumentException if the number is not finite.
	 */
	public static double requireFinite(double number) {
		return requireNonNegative(number, "number must be finite");
	}

	/**
	 * Checks if the provided number is finite.
	 * 
	 * @param number the number to be checked.
	 * @param message the message of the exception thrown if the number is
	 * infinite.
	 * @return the provided number.
	 * @throws IllegalArgumentException if the number is infinite. The message
	 * of this exception will be the provided.
	 */
	public static double requireFinite(double number, String message) {
		return check(number, Double::isFinite, message);
	}

	/**
	 * Checks if the provided number is greater than or equals to 0.
	 * 
	 * @param number the number to be checked.
	 * @return the provided number.
	 * @throws IllegalArgumentException if the number is lower than 0.
	 */
	public static double requireNonNegative(double number) {
		return requireNonNegative(number, 
			"number must be greater than or equals to 0"
		);
	}

	/**
	 * Checks if the provided number is greater than or equals to 0.
	 * 
	 * @param number the number to be checked.
	 * @param message the message of the exception thrown if the number is
	 * lower than 0.
	 * @return the provided number.
	 * @throws IllegalArgumentException if the number is lower than 0. The
	 * message of this exception will be the provided.
	 */
	public static double requireNonNegative(double number, String message) {
		return check(number, x -> x >= 0, message);
	}

	/**
	 * Checks if the provided number is greater than or equals to 0.
	 * 
	 * @param number the number to be checked.
	 * @return the provided number.
	 * @throws IllegalArgumentException if the number is lower than 0.
	 */
	public static int requireNonNegative(int number) {
		return requireNonNegative(number,
			"number must be greater than or equals to 0"
		);
	}

	/**
	 * Checks if the provided number is greater than or equals to 0.
	 * 
	 * @param number the number to be checked.
	 * @param message the message of the exception thrown if the number is
	 * lower than 0.
	 * @return the provided number.
	 * @throws IllegalArgumentException if the number is lower than 0. The
	 * message of this exception will be the provided.
	 */
	public static int requireNonNegative(int number, String message) {
		return check(number, x -> x >= 0, message);
	}

	/**
	 * Checks if the provided number is greater than or 0.
	 * 
	 * @param number the number to be checked.
	 * @return the provided number.
	 * @throws IllegalArgumentException if the number is lower than or equals
	 * to 0.
	 */
	public static long requirePositive(long number) {
		return requirePositive(number, "number must be greater than 0");
	}

	/**
	 * Checks if the provided number is greater than 0.
	 * 
	 * @param number the number to be checked.
	 * @param message the message of the exception thrown if the number is
	 * lower than or equals to 0.
	 * @return the provided number.
	 * @throws IllegalArgumentException if the number is lower than 0 or equals
	 * to 0. The message of this exception will be the provided.
	 */
	public static long requirePositive(long number, String message) {
		return check(number, x -> x > 0, message);
	}

	/**
	 * Checks if the provided number is greater than or 0.
	 * 
	 * @param number the number to be checked.
	 * @return the provided number.
	 * @throws IllegalArgumentException if the number is lower than or equals
	 * to 0.
	 */
	public static int requirePositive(int number) {
		return requirePositive(number, "number must be greater than 0");
	}

	/**
	 * Checks if the provided number is greater than 0.
	 * 
	 * @param number the number to be checked.
	 * @param message the message of the exception thrown if the number is
	 * lower than or equals to 0.
	 * @return the provided number.
	 * @throws IllegalArgumentException if the number is lower than 0 or equals
	 * to 0. The message of this exception will be the provided.
	 */
	public static int requirePositive(int number, String message) {
		return check(number, x -> x > 0, message);
	}

	/**
	 * Checks if the provided number is greater than or 0.
	 * 
	 * @param number the number to be checked.
	 * @return the provided number.
	 * @throws IllegalArgumentException if the number is lower than or equals
	 * to 0.
	 */
	public static double requirePositive(double number) {
		return requirePositive(number, "number must be greater than 0");
	}

	/**
	 * Checks if the provided number is greater than 0.
	 * 
	 * @param number the number to be checked.
	 * @param message the message of the exception thrown if the number is
	 * lower than or equals to 0.
	 * @return the provided number.
	 * @throws IllegalArgumentException if the number is lower than 0 or equals
	 * to 0. The message of this exception will be the provided.
	 */
	public static double requirePositive(double number, String message) {
		return check(number, x -> x > 0, message);
	}

	/**
	 * Checks if the provided number is finite and greater than or equals to 0.
	 * 
	 * @param number the number to be checked.
	 * @return the provided number.
	 * @throws IllegalArgumentException if the number is infinite or lower than
	 * 0.
	 */
	public static double requireFiniteNonNegative(double number) {
		return requireNonNegative(requireFinite(number));
	}

	/**
	 * Checks if the provided number is finite and greater than or equals to 0.
	 * 
	 * @param number the number to be checked.
	 * @param message the message of the exception thrown if the number is
	 * infinite or lower than 0.
	 * @return the provided number.
	 * @throws IllegalArgumentException if the number is infinite or lower than
	 * 0. The message of this exception will be the provided.
	 */
	public static double requireFiniteNonNegative(double number, String message) {
		return requireNonNegative(requireFinite(number, message), message);
	}

	/**
	 * Checks if the provided number is finite and greater than or equals to 0.
	 * 
	 * @param number the number to be checked.
	 * @return the provided number.
	 * @throws IllegalArgumentException if the number is infinite or lower than
	 * 0.
	 */
	public static long requireNonNegative(long number) {
		return requireNonNegative(number);
	}

	/**
	 * Checks if the provided number is greater than or equals to 0.
	 * 
	 * @param number the number to be checked.
	 * @param message the message of the exception thrown if the number is
	 * lower than 0.
	 * @return the provided number.
	 * @throws IllegalArgumentException if the number is lower than 0. The
	 * message of this exception will be the provided.
	 */
	public static long requireNonNegative(long number, String message) {
		return check(number, x -> x >= 0, message);
	}
	
	private static <T> T check(
		T value, Function<T, Boolean> validator,
		Supplier<RuntimeException> exceptionProvider
	) {
		if (validator.apply(value)) return value;
		else throw exceptionProvider.get();
	}
	
	private static <T> T check(
		T value, Function<T, Boolean> validator, String message
	) {
		if (validator.apply(value)) return value;
		else throw new IllegalArgumentException(message);
	}
	
	private static double check(
		double value, DoubleFunction<Boolean> validator, String message
	) {
		if (validator.apply(value)) return value;
		else throw new IllegalArgumentException(message);
	}
	
	private static int check(
		int value, DoubleFunction<Boolean> validator, String message
	) {
		if (validator.apply(value)) return value;
		else throw new IllegalArgumentException(message);
	}
	
	private static long check(
		long value, DoubleFunction<Boolean> validator, String message
	) {
		if (validator.apply(value)) return value;
		else throw new IllegalArgumentException(message);
	}
	
	private static <T> Function<T, Boolean> not(Function<T, Boolean> function) {
		return x -> !function.apply(x);
	}
	
	private static DoubleFunction<Boolean> notDouble(DoubleFunction<Boolean> function) {
		return x -> !function.apply(x);
	}
}
