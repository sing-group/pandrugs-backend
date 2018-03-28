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

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

public final class Compare {
	private Compare() {}

	public static <T> Builder<T> objects(T obj1, T obj2) {
		return new Builder<T>(obj1, obj2);
	}
	
	public final static class Builder<T> {
		private final T obj1;
		private final T obj2;
		
		private BiFunction<T, T, Integer> comparerFunction;
		
		public Builder(T obj1, T obj2) {
			this.obj1 = obj1;
			this.obj2 = obj2;
		}
		
		private void addComparisonBy(ToIntBiFunction<T, T> compare, boolean reverseOrder) {
			if (this.comparerFunction == null) {
				if (reverseOrder)
					this.comparerFunction = (o1, o2) -> -compare.applyAsInt(o1, o2);
				else
					this.comparerFunction = (o1, o2) -> compare.applyAsInt(o1, o2);
			} else {
				if (reverseOrder)
					this.comparerFunction = this.comparerFunction.andThen(cmp -> 
						cmp == 0 ? -compare.applyAsInt(obj1, obj2) : cmp
					);
				else 
					this.comparerFunction = this.comparerFunction.andThen(cmp -> 
						cmp == 0 ? compare.applyAsInt(obj1, obj2) : cmp
					);
			}
		}
		
		public Builder<T> asComparables() {
			return this.asComparables(false);
		}
		
		public Builder<T> asComparablesInReverseOrder() {
			return this.asComparables(true);
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		private Builder<T> asComparables(boolean reverseOrder) {
			if (obj1 instanceof Comparable) {
				this.addComparisonBy((o1, o2) -> ((Comparable) o1).compareTo(o2), reverseOrder);
			} else {
				throw new IllegalStateException("Objects are not comparable");
			}
			
			return this;
		}
		
		public <C extends Comparable<C>> Builder<T> thenAsComparables() {
			return this.asComparables();
		}
		
		public <C extends Comparable<C>> Builder<T> thenAsComparablesInReverseOrder() {
			return this.asComparablesInReverseOrder();
		}
		
		public <C extends Comparable<C>> Builder<T> by(final Function<T, C> f) {
			return this.by(f, false);
		}
		
		public <C extends Comparable<C>> Builder<T> byReverseOrderOf(final Function<T, C> f) {
			return this.by(f, true);
		}
		
		private <C extends Comparable<C>> Builder<T> by(final Function<T, C> f, boolean reverseOrder) {
			this.addComparisonBy(nullSafeCompare(f, (v1, v2) -> v1.compareTo(v2)), reverseOrder);
			
			return this;
		}
		
		public <C extends Comparable<C>> Builder<T> thenBy(final Function<T, C> f) {
			return this.by(f);
		}
		
		public <C extends Comparable<C>> Builder<T> thenByReverseOrderOf(final Function<T, C> f) {
			return this.byReverseOrderOf(f);
		}
		
		public Builder<T> byInt(final ToIntFunction<T> f) {
			return this.byInt(f, false);
		}
		
		public Builder<T> byReverseOrderOfInt(final ToIntFunction<T> f) {
			return this.byInt(f, true);
		}
		
		private Builder<T> byInt(final ToIntFunction<T> f, boolean reverseOrder) {
			this.addComparisonBy((o1, o2) -> 
				Integer.compare(f.applyAsInt(o1), (f.applyAsInt(o2))),
				reverseOrder
			);
			
			return this;
		}
		
		public <C extends Comparable<C>> Builder<T> thenByInt(final ToIntFunction<T> f) {
			return this.byInt(f);
		}
		
		public <C extends Comparable<C>> Builder<T> thenByReverseOrderOfInt(final ToIntFunction<T> f) {
			return this.byReverseOrderOfInt(f);
		}
		
		public Builder<T> byLong(final ToLongFunction<T> f) {
			return this.byLong(f, false);
		}
		
		public Builder<T> byReverseOrderOfLong(final ToLongFunction<T> f) {
			return this.byLong(f, true);
		}
		
		private Builder<T> byLong(final ToLongFunction<T> f, boolean reverseOrder) {
			this.addComparisonBy((o1, o2) -> 
				Long.compare(f.applyAsLong(o1), (f.applyAsLong(o2))),
				reverseOrder
			);
			
			return this;
		}
		
		public <C extends Comparable<C>> Builder<T> thenByLong(final ToLongFunction<T> f) {
			return this.byLong(f);
		}
		
		public <C extends Comparable<C>> Builder<T> thenByReverseOrderOfLong(final ToLongFunction<T> f) {
			return this.byReverseOrderOfLong(f);
		}
		
		public Builder<T> byFloat(final ToFloatFunction<T> f) {
			return this.byFloat(f, false);
		}
		
		public Builder<T> byReverseOrderOfFloat(final ToFloatFunction<T> f) {
			return this.byFloat(f, true);
		}
		
		private Builder<T> byFloat(final ToFloatFunction<T> f, boolean reverseOrder) {
			this.addComparisonBy((o1, o2) -> 
				Float.compare(f.applyAsFloat(o1), (f.applyAsFloat(o2))),
				reverseOrder
			);
			
			return this;
		}
		
		public <C extends Comparable<C>> Builder<T> thenByFloat(final ToFloatFunction<T> f) {
			return this.byFloat(f);
		}
		
		public <C extends Comparable<C>> Builder<T> thenByReverseOrderOfFloat(final ToFloatFunction<T> f) {
			return this.byReverseOrderOfFloat(f);
		}
		
		public Builder<T> byDouble(final ToDoubleFunction<T> f) {
			return this.byDouble(f, false);
		}
		
		public Builder<T> byReverseOrderOfDouble(final ToDoubleFunction<T> f) {
			return this.byDouble(f, true);
		}
		
		private Builder<T> byDouble(final ToDoubleFunction<T> f, boolean reverseOrder) {
			this.addComparisonBy((o1, o2) -> 
				Double.compare(f.applyAsDouble(o1), (f.applyAsDouble(o2))),
				reverseOrder
			);
			
			return this;
		}

		public <C extends Comparable<C>> Builder<T> thenByDouble(final ToDoubleFunction<T> f) {
			return this.byDouble(f);
		}
		
		public <C extends Comparable<C>> Builder<T> thenByReverseOrderOfDouble(final ToDoubleFunction<T> f) {
			return this.byReverseOrderOfDouble(f);
		}
		
		public <C extends Comparable<C>> Builder<T> byArray(final Function<T, C[]> f) {
			return this.byArray(f, false);
		}
		
		public <C extends Comparable<C>> Builder<T> byReverseOrderOfArray(final Function<T, C[]> f) {
			return this.byArray(f, true);
		}
		
		private <C extends Comparable<C>> Builder<T> byArray(final Function<T, C[]> f, boolean reverseOrder) {
			this.addComparisonBy(nullSafeCompare(f, (a1, a2) -> {
				for (int i = 0; i < Math.max(a1.length, a2.length); i++) {
					final int index = i;
					
					final int cmp = nullSafeCompare(
						(C[] a) -> index < a.length ? a[index] : null,
						(C v1, C v2) -> v1.compareTo(v2)
					).applyAsInt(a1, a2);
					
					if (cmp != 0) return cmp;
				}
				return 0;
			}), reverseOrder);
			
			return this;
		}

		public <C extends Comparable<C>> Builder<T> thenByArray(final Function<T, C[]> f) {
			return this.byArray(f);
		}
		
		public <C extends Comparable<C>> Builder<T> thenByReverseOrderOfArray(final Function<T, C[]> f) {
			return this.byReverseOrderOfArray(f);
		}

		public Builder<T> byIntArray(final Function<T, int[]> f) {
			return this.byIntArray(f, false);
		}
		
		public Builder<T> byReverseOrderOfIntArray(final Function<T, int[]> f) {
			return this.byIntArray(f, true);
		}

		private Builder<T> byIntArray(final Function<T, int[]> f, boolean reverseOrder) {
			this.addComparisonBy(nullSafeCompare(f, (a1, a2) -> {
				for (int i = 0; i < Math.max(a1.length, a2.length); i++) {
					final int index = i;
					
					final int cmp = nullSafeCompare(
						(int[] a) -> index < a.length ? a[index] : null,
						Integer::compare
					).applyAsInt(a1, a2);
					
					if (cmp != 0) return cmp;
				}
				return 0;
			}), reverseOrder);
			
			return this;
		}

		public Builder<T> thenByIntArray(final Function<T, int[]> f) {
			return this.byIntArray(f);
		}
		
		public Builder<T> thenByReverseOrderOfIntArray(final Function<T, int[]> f) {
			return this.byReverseOrderOfIntArray(f);
		}

		public Builder<T> byLongArray(final Function<T, long[]> f) {
			return this.byLongArray(f, false);
		}
		
		public Builder<T> byReverseOrderOfLongArray(final Function<T, long[]> f) {
			return this.byLongArray(f, true);
		}

		private Builder<T> byLongArray(final Function<T, long[]> f, boolean reverseOrder) {
			this.addComparisonBy(nullSafeCompare(f, (a1, a2) -> {
				for (int i = 0; i < Math.max(a1.length, a2.length); i++) {
					final int index = i;
					
					final int cmp = nullSafeCompare(
						(long[] a) -> index < a.length ? a[index] : null,
						Long::compare
					).applyAsInt(a1, a2);
					
					if (cmp != 0) return cmp;
				}
				return 0;
			}), reverseOrder);
			
			return this;
		}

		public Builder<T> thenByLongArray(final Function<T, long[]> f) {
			return this.byLongArray(f);
		}
		
		public Builder<T> thenByReverseOrderOfLongArray(final Function<T, long[]> f) {
			return this.byReverseOrderOfLongArray(f);
		}

		public Builder<T> byDoubleArray(final Function<T, double[]> f) {
			return this.byDoubleArray(f, false);
		}
		
		public Builder<T> byReverseOrderOfDoubleArray(final Function<T, double[]> f) {
			return this.byDoubleArray(f, true);
		}

		private Builder<T> byDoubleArray(final Function<T, double[]> f, boolean reverseOrder) {
			this.addComparisonBy(nullSafeCompare(f, (a1, a2) -> {
				for (int i = 0; i < Math.max(a1.length, a2.length); i++) {
					final int index = i;
					
					final int cmp = nullSafeCompare(
						(double[] a) -> index < a.length ? a[index] : null,
						Double::compare
					).applyAsInt(a1, a2);
					
					if (cmp != 0) return cmp;
				}
				return 0;
			}), reverseOrder);
			
			return this;
		}

		public Builder<T> thenByDoubleArray(final Function<T, double[]> f) {
			return this.byDoubleArray(f);
		}
		
		public Builder<T> thenByReverseOrderOfDoubleArray(final Function<T, double[]> f) {
			return this.byReverseOrderOfDoubleArray(f);
		}

		public Builder<T> byFloatArray(final Function<T, float[]> f) {
			return this.byFloatArray(f, false);
		}
		
		public Builder<T> byReverseOrderOfFloatArray(final Function<T, float[]> f) {
			return this.byFloatArray(f, true);
		}

		private Builder<T> byFloatArray(final Function<T, float[]> f, boolean reverseOrder) {
			this.addComparisonBy(nullSafeCompare(f, (a1, a2) -> {
				for (int i = 0; i < Math.max(a1.length, a2.length); i++) {
					final int index = i;
					
					final int cmp = nullSafeCompare(
						(float[] a) -> index < a.length ? a[index] : null,
						Float::compare
					).applyAsInt(a1, a2);
					
					if (cmp != 0) return cmp;
				}
				return 0;
			}), reverseOrder);
			
			return this;
		}

		public Builder<T> thenByFloatArray(final Function<T, float[]> f) {
			return this.byFloatArray(f);
		}
		
		public Builder<T> thenByReverseOrderOfFloatArray(final Function<T, float[]> f) {
			return this.byReverseOrderOfFloatArray(f);
		}

		public int andGet() {
			try {
				return this.comparerFunction.apply(this.obj1, this.obj2);
			} finally {
				this.comparerFunction = null;
			}
		}
		
		public int andGetInReverseOrder() {
			try {
				return -this.comparerFunction.apply(this.obj1, this.obj2);
			} finally {
				this.comparerFunction = null;
			}
		}
	}

	@FunctionalInterface
	public interface ToFloatFunction<T> {
	    /**
	     * Applies this function to the given argument.
	     *
	     * @param value the function argument
	     * @return the function result
	     */
	    public float applyAsFloat(T value);
	}
	
	public final static <T, C> ToIntBiFunction<T, T> nullSafeCompare(
		Function<T, C> f, ToIntBiFunction<C, C> cmp
	) {
		return (o1, o2) -> {
			final C v1 = f.apply(o1);
			final C v2 = f.apply(o2);
			
			if (v1 == null && v2 == null) {
				return 0;
			} else if (v1 == null) {
				return Integer.MIN_VALUE;
			} else if (v2 == null) {
				return Integer.MAX_VALUE;
			} else {
				return cmp.applyAsInt(v1, v2);
			}
		};
	}
}
