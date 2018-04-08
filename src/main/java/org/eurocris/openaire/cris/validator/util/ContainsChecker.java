package org.eurocris.openaire.cris.validator.util;

import java.util.Iterator;
import java.util.function.Predicate;

public class ContainsChecker<T> implements Iterable<T> {
	
	public static <T> ContainsChecker<T> over( final Iterable<T> list ) {
		return new ContainsChecker<T>() {

			@Override
			public Iterator<T> iterator() {
				return list.iterator();
			}

			@Override
			protected void close() {
				// no-op
			}
			
		};
	}
	
	public ContainsChecker<T> checkContains( final Predicate<T> predicate, final Error error ) {
		final ContainsChecker<T> parent = (ContainsChecker<T>) this;
		return new ContainsChecker<T>() {

			MatchCountingIterator<T> mci;
			
			@Override
			public Iterator<T> iterator() {
				return mci = new MatchCountingIterator<T>( parent.iterator(), predicate );
			}

			@Override
			protected void close() {
				parent.close();
				if ( mci.getCount() == 0L ) {
					throw error;
				}
			}
			
		};
	}
	
	protected void close() {
		// no-op
	}
	
	public Iterator<T> iterator() {
		return null;
	}

	public long run() {
		long n = 0;
		for ( @SuppressWarnings( "unused") final T x : this ) {
			++n;
		}
		close();
		return n;
	}
	
}

class MatchCountingIterator<T> implements Iterator<T> {
	private final Predicate<T> predicate;
	private final Iterator<T> parent;
	private long count = 0;

	MatchCountingIterator( final Iterator<T> parent, final Predicate<T> predicate ) {
		this.predicate = predicate;
		this.parent = parent;
	}

	@Override
	public boolean hasNext() {
		return parent.hasNext();
	}

	@Override
	public T next() {
		final T obj = parent.next();
		if ( predicate.test( obj ) ) ++count;
		return obj;
	}
	
	public long getCount() {
		return count;
	}

}