/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.benchmarks.settermethod;

@SuppressWarnings("unused")
public class ImmutableButSetsPrivateFieldOfInstanceOfSelf {

	private int myField = 0;
	private ImmutableButSetsPrivateFieldOfInstanceOfSelf fieldOfSelfType = null;
	
	public ImmutableButSetsPrivateFieldOfInstanceOfSelf setPrivateFieldOnInstanceOfSelf() {
		ImmutableButSetsPrivateFieldOfInstanceOfSelf i = new ImmutableButSetsPrivateFieldOfInstanceOfSelf();
		this.hashCode();
		i.myField = 10;
		this.hashCode();
		i.myField = 11;
		return i;
	}
		
}

class MutableBySettingFieldOnThisInstanceAndOtherInstance {
	@SuppressWarnings("unused")
	private int myField = 0;
	
	public void setMyField(int newMyField, MutableBySettingFieldOnThisInstanceAndOtherInstance otherInstance) {
		this.myField = newMyField;
		otherInstance.myField = 42;
		
	}
}