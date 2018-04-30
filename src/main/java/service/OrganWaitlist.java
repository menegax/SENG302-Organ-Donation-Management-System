package service;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import model.Patient;
import utility.GlobalEnums.Organ;
import utility.GlobalEnums.Region;

public class OrganWaitlist implements Iterable<OrganWaitlist.OrganRequest> {

	private SortedSet<OrganRequest> requests;

	
	public OrganWaitlist() {
		requests = new TreeSet<OrganRequest>();
	}
	
	/**
	 * Adds a request for a organ to the waiting list.
	 * @param receiver	- Patient requesting the organ.
	 * @param organ		- The organ requested.
	 * @return			- Returns true if Collection changed otherwise false.
	 */
	public boolean add(Patient receiver, Organ organ) {
		return requests.add(new OrganRequest(receiver, organ));
	}
	
	public int size() {
		return requests.size();
	}
	
	/**
	 * Allows the waiting list to be used in for-each loops.
	 * @return	- Returns the iterator for the Collection.
	 */
	@Override
	public Iterator<OrganWaitlist.OrganRequest> iterator() {
		return requests.iterator();
	}
	
	/**
	 * A organ request for the waiting list.
	 * @author PGLaffey
	 */
	public class OrganRequest implements Comparable<OrganRequest> {
		LocalDate date;
		Region region;
		Organ organ;
		String name;
		String nhi;
		
		public OrganRequest(Patient receiver, Organ organ) {
			date = LocalDate.now();
			region = receiver.getRegion();
			this.organ = organ;
			name = receiver.getNameConcatenated();
			nhi = receiver.getNhiNumber();
		}
		
		/**
		 * Override of equals method, to enable checking for duplicates.
		 * @param other	- The object to compare for equality.
		 * @return		- True if the requests are considered equivalent, false otherwise. 
		 */
		@Override
		public boolean equals(Object other) {
			if (other == null) return false;
			if (getClass() != other.getClass()) return false;
			OrganRequest otherRequest = (OrganRequest) other;
			if (nhi.equals(otherRequest.getReceiverNhi())) {
				if (organ.equals(otherRequest.getRequestedOrgan())) {
					return true;
				}
			}
			return false;
		}
		
		/**
		 * Override of hashCode method.
		 * @return	- Hashcode for the request.
		 */
		@Override
		public int hashCode() {
			return (organ + nhi + date.toString()).hashCode();
		}
		
		/**
		 * Override of compareTo method for ordering in the Collection.
		 * @return	- "-1" if should be before, "0" if equivalent, "1" if should be after.  
		 */
		@Override
		public int compareTo(OrganRequest o) {
			return date.compareTo(o.getRequestDate());
		}
		
		/**
		 * Returns the region of the request.
		 * @return	- The region of the request.
		 */
		public Region getRequestRegion() {
			return region;
		}
		
		/**
		 * Returns the organ requested.
		 * @return	- The organ requested.
		 */
		public Organ getRequestedOrgan() {
			return organ;
		}
		
		/**
		 * Returns the name of the patient of the request.
		 * @return	- The name of the patient requesting a organ.
		 */
		public String getReceiverName() {
			return name;
		}
		
		/**
		 * Returns The date of the request.
		 * @return 	- The date of the request.
		 */
		public LocalDate getRequestDate() {
			return date;
		}
		
		/**
		 * Returns the NHI of the patient of the request.
		 * @return	- The NHI of the patient requesting a organ.
		 */
		public String getReceiverNhi() {
			return nhi;
		}
	}
}
