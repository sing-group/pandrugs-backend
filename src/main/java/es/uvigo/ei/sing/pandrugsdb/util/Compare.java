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
		
		private void addComparisonBy(ToIntBiFunction<T, T> compare) {
			if (this.comparerFunction == null) {
				this.comparerFunction = (o1, o2) -> {
					return compare.applyAsInt(o1, o2);
				};
			} else {
				this.comparerFunction = this.comparerFunction.andThen(cmp -> {
					if (cmp == 0) {
						return compare.applyAsInt(obj1, obj2);
					} else {
						return cmp;
					}
				});
			}
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Builder<T> asComparables() {
			if (obj1 instanceof Comparable) {
				this.addComparisonBy((o1, o2) -> 
					((Comparable) o1).compareTo(o2)
				);
			} else {
				throw new IllegalStateException("Objects are not comparable");
			}
			
			return this;
		}
		
		public <C extends Comparable<C>> Builder<T> thenAsComparables() {
			return this.asComparables();
		}
		
		public <C extends Comparable<C>> Builder<T> by(final Function<T, C> f) {
			this.addComparisonBy(nullSafeCompare(f, (v1, v2) -> v1.compareTo(v2)));
			
			return this;
		}
		
		public <C extends Comparable<C>> Builder<T> thenBy(final Function<T, C> f) {
			return this.by(f);
		}
		
		public Builder<T> byInt(final ToIntFunction<T> f) {
			this.addComparisonBy((o1, o2) -> 
				Integer.compare(f.applyAsInt(o1), (f.applyAsInt(o2)))
			);
			
			return this;
		}
		
		public <C extends Comparable<C>> Builder<T> thenByInt(final ToIntFunction<T> f) {
			return this.byInt(f);
		}
		
		public Builder<T> byLong(final ToLongFunction<T> f) {
			this.addComparisonBy((o1, o2) -> 
				Long.compare(f.applyAsLong(o1), (f.applyAsLong(o2)))
			);
			
			return this;
		}
		
		public <C extends Comparable<C>> Builder<T> thenByLong(final ToLongFunction<T> f) {
			return this.byLong(f);
		}
		
		public Builder<T> byFloat(final ToFloatFunction<T> f) {
			this.addComparisonBy((o1, o2) -> 
				Float.compare(f.applyAsFloat(o1), (f.applyAsFloat(o2)))
			);
			
			return this;
		}
		
		public <C extends Comparable<C>> Builder<T> thenByFloat(final ToFloatFunction<T> f) {
			return this.byFloat(f);
		}
		
		public Builder<T> byDouble(final ToDoubleFunction<T> f) {
			this.addComparisonBy((o1, o2) -> 
				Double.compare(f.applyAsDouble(o1), (f.applyAsDouble(o2)))
			);
			
			return this;
		}
		
		public <C extends Comparable<C>> Builder<T> thenByDouble(final ToDoubleFunction<T> f) {
			return this.byDouble(f);
		}
		
		public <C extends Comparable<C>> Builder<T> byArray(final Function<T, C[]> f) {
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
			}));
			
			return this;
		}

		public <C extends Comparable<C>> Builder<T> thenByArray(final Function<T, C[]> f) {
			return this.byArray(f);
		}

		public Builder<T> byIntArray(final Function<T, int[]> f) {
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
			}));
			
			return this;
		}

		public Builder<T> thenByIntArray(final Function<T, int[]> f) {
			return this.byIntArray(f);
		}

		public Builder<T> byLongArray(final Function<T, long[]> f) {
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
			}));
			
			return this;
		}

		public Builder<T> thenByLongArray(final Function<T, long[]> f) {
			return this.byLongArray(f);
		}

		public Builder<T> byDoubleArray(final Function<T, double[]> f) {
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
			}));
			
			return this;
		}

		public Builder<T> thenByDoubleArray(final Function<T, double[]> f) {
			return this.byDoubleArray(f);
		}

		public Builder<T> byFloatArray(final Function<T, float[]> f) {
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
			}));
			
			return this;
		}

		public Builder<T> thenByFloatArray(final Function<T, float[]> f) {
			return this.byFloatArray(f);
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
