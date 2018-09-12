package service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import model.Patient;
import utility.GlobalEnums;
import utility.GlobalEnums.Organ;
import utility.GlobalEnums.Region;

public class OrganWaitlist implements Iterable<OrganWaitlist.OrganRequest> {

	private SortedSet<OrganRequest> requests;

	
	public OrganWaitlist() {
	    requests = new TreeSet<>();
	}

	public SortedSet<OrganRequest> getRequests() {
		return requests;
	}
	
	/**
	 * Adds a request for a organ to the waiting list.
	 * @param receiver	- Patient requesting the organ.
	 * @param organ		- The organ requested.
	 * @return			- Returns true if Collection changed otherwise false.
	 */
	public boolean add(Patient receiver, Organ organ) {
		OrganRequest request = new OrganRequest(receiver, organ);
	    return requests.add(request);
	}

    /**
     * Adds a request for a organ to the waiting list.
     * ONLY FOR USE BY THE DATABASE.
     * @param name      - The name of the patient requesting an organ.
     * @param organ     - The organ requested.
     * @param date      - The date of the request.
     * @param region    - The region of the organ request.
     * @param nhi       - The NHI of the patient requesting an organ.
     * @return          - Returns true if Collection changed, otherwise false.
     */
	public boolean add(String name, Organ organ, LocalDate date, Region region, String nhi, String address) {
		Patient patient = new PatientDataService().getPatientByNhi(nhi);
		return requests.add(new OrganRequest(name, organ, date, region, nhi, patient.getAge(), address, patient.getBloodGroup(), patient.getBirth()));
	}

	/**
	 * Returns the number of OrganRequest objects in the waitlist
	 * @return int size of waitlist
	 */
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
	 */
	public class OrganRequest implements Comparable<OrganRequest>{
		LocalDate date;
		Region region;
		Organ organ;
		String name;
		String nhi;
		int age;
		String address;
		GlobalEnums.BloodGroup bloodGroup;
		LocalDate birth;
		
		public OrganRequest(Patient receiver, Organ organ) {
			date = LocalDate.now();
			region = receiver.getRegion();
			this.organ = organ;
			name = receiver.getNameConcatenated();
			nhi = receiver.getNhiNumber();
			age = receiver.getAge();
			birth = receiver.getBirth();
			address = receiver.getAddressString();
			bloodGroup = receiver.getBloodGroup();
		}

		public OrganRequest(String name, Organ organ, LocalDate date, Region region, String nhi, int age, String address, GlobalEnums.BloodGroup bloodGroup, LocalDate birth) {
			this.date = date;
			this.region = region;
			this.organ = organ;
			this.name = name;
			this.nhi = nhi;
			this.age = age;
			this.address = address;
			this.bloodGroup = bloodGroup;
			this.birth = birth;
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
			if (date.isBefore(o.getRequestDate())) {
				return -1;
			}
			if (this.getReceiverNhi().equals(o.getReceiverNhi()) && this.getRequestedOrgan().equals(o.getRequestedOrgan())){
				return 0;
			}
			return 1;
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

		/**
		 * Returns the age of the patient of the request.
		 * @return	- The age of the patient requesting a organ.
		 */
		public int getAge() {
			return age;
		}

		/**
		 * Returns the address of the patient of the request.
		 * @return	- The address of the patient requesting a organ.
		 */
		public String getAddress() {
			return address;
		}

		/**
		 * Returns the bloodGroup of the patient of the request.
		 * @return	- The bloodGroup of the patient requesting a organ.
		 */
		public GlobalEnums.BloodGroup getBloodGroup() {
			return bloodGroup;
		}

		/**
		 * Returns the date of the request.
		 * @return	- The date of the request.
		 */
		public LocalDate getDate() {
			return date;
		}

		/**
		 * Returns the birth date of the patient of the request.
		 * @return	- The birth date of the patient requesting a organ.
		 */
		public LocalDate getBirth() {
			return birth;
		}
	}
}
