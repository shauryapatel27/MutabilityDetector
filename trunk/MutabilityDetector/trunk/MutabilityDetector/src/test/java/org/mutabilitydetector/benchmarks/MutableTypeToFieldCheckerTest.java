/* 
 * Mutability Detector
 *
 * Copyright 2009 Graham Allan
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.mutabilitydetector.benchmarks;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY_NOT;
import static org.mutabilitydetector.ImmutableAssert.assertDefinitelyNotImmutable;
import static org.mutabilitydetector.ImmutableAssert.assertImmutable;
import static org.mutabilitydetector.ImmutableAssert.assertNotImmutable;
import static org.mutabilitydetector.TestUtil.analysisDatabase;
import static org.mutabilitydetector.TestUtil.runChecker;
import static org.mutabilitydetector.checkers.info.AnalysisDatabase.TYPE_STRUCTURE;

import org.junit.Before;
import org.junit.Test;
import org.mutabilitydetector.IAnalysisSession;
import org.mutabilitydetector.benchmarks.MutableByAssigningAbstractTypeToField.AbstractStringContainer;
import org.mutabilitydetector.checkers.MutableTypeToFieldChecker;
import org.mutabilitydetector.checkers.info.TypeStructureInformation;

public class MutableTypeToFieldCheckerTest {

	private IAnalysisSession mockSession;
	private MutableTypeToFieldChecker checker;

	@Before public void setUp() {
		mockSession = mock(IAnalysisSession.class);
		TypeStructureInformation info = analysisDatabase().requestInformation(TYPE_STRUCTURE);
		checker = new MutableTypeToFieldChecker(mockSession, info);
	}
	
	@Test public void requestsMutableStatusOfPublishedField() throws Exception {
		when(mockSession.isImmutable(MutableExample.class.getCanonicalName())).thenReturn(DEFINITELY_NOT);
		runChecker(checker, MutableByHavingMutableFieldAssigned.class);
		
		verify(mockSession).isImmutable(MutableExample.class.getCanonicalName());
	}
	
	@Test public void failsCheckWhenMutableTypeIsAssignedToField() throws Exception {
		when(mockSession.isImmutable(MutableExample.class.getCanonicalName())).thenReturn(DEFINITELY_NOT);
		runChecker(checker, MutableByHavingMutableFieldAssigned.class);
		
		assertThat(checker.reasons().size(), is(1));
		assertNotImmutable(checker.result());
	}
	
	@Test public void failsCheckIfAnyFieldsHaveMutableAssignedToThem() throws Exception {
		when(mockSession.isImmutable(MutableExample.class.getCanonicalName())).thenReturn(DEFINITELY_NOT);

		runChecker(checker, MutableByHavingMutableFieldAssigned.class);
		
		assertNotImmutable(checker.result());
		assertTrue(checker.reasons().size() > 0);
	}
	
	@Test public void instanceFieldWhichHasAMutatedArrayIsMutable() throws Exception {
		runChecker(checker, MutableByHavingArrayTypeAsField.class);
		assertDefinitelyNotImmutable(checker.result());
	}
	
	@Test public void staticFieldWhichHasAMutatedArrayIsImmutable() throws Exception {
		runChecker(checker, ImmutableWhenArrayFieldIsStatic.class);
		assertImmutable(checker.result());
	}
	
	@Test public void doesNotRaiseErrorForAbstractTypeSinceThisIsRaisedByAbstractTypeToFieldChecker() throws Exception {
		when(mockSession.isImmutable(AbstractStringContainer.class.getName())).thenReturn(DEFINITELY_NOT);
		runChecker(checker, MutableByAssigningAbstractTypeToField.class);
		
		assertImmutable(checker.result());
		assertThat(checker.reasons().size(), is(0));
	}
	
}
