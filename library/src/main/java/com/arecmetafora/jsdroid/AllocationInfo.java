package com.arecmetafora.jsdroid;

/**
 * Holds informations about the JNI reference allocation.
 */
class AllocationInfo {

	/**
	 * Number of references to this object.
	 */
	private int numberOfReferences;

	/**
	 * The object reference itself.
	 */
	private Object objectReference;

	/**
	 * Constructs a new allocation information instance.
	 *
	 * @param objectReference The object reference itself.
	 */
	AllocationInfo(Object objectReference) {
		this.numberOfReferences = 1;
		this.objectReference = objectReference;
	}

	/**
	 * Increase by one the number of references to this allocation.
	 */
	void allocReference() {
		this.numberOfReferences++;
	}

	/**
	 * Decrease by one the number of references to this allocation.
	 */
	void deallocReference() {
		this.numberOfReferences--;
	}

	/**
	 * Gets the object reference related with this allocation.
	 *
	 * @return The object reference.
	 */
	Object getObjectReference() {
		return this.objectReference;
	}

	/**
	 * Returns if this allocation has only one reference.
	 *
	 * @return True, if this allocation contains only one reference to the object. False, otherwise.
	 */
	boolean isUniqueReference() {
		return this.numberOfReferences == 1;
	}

}
