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
package org.mutabilitydetector.checkers;

import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.locations.ClassNameConvertor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Type;

/**
 * This checker visits types and fields. Types can be visited separately, fields
 * should be visited as part of visiting an outer type.
 * 
 * The rules of inherent mutability are defined as:
 * 
 * Inherently mutable: Interfaces; Abstract classes; primitive Array types
 * 
 * Inherently immutable: Enum types; primitive types ie. boolean, char, byte,
 * short, int, long, float, double
 * 
 * @author graham
 * 
 */
public class InherentTypeMutabilityChecker extends AbstractMutabilityChecker {

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		if (isAbstract(access) || isInterface(access)) {
			String dottedName = new ClassNameConvertor().dotted(name);
			addResult(dottedName + " is inherently mutable, as declared as an abstract type.", null,
					MutabilityReason.ABSTRACT_TYPE_INHERENTLY_MUTABLE);
		}
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		/*
		 * Static fields should not count against the instance.
		 */
		if (!isStatic(access)) {
			/*
			 * This check was causing far too many classes to be called mutable.
			 * It would be better if it was possible to check that an inherently
			 * mutable type was *actually mutated*. Calling an entire class
			 * mutable for having a mutable field which it doesn't mutate is a
			 * bit rubbish.
			 */
			if (isPrimitiveArray(desc) && !("ENUM$VALUES".equals(name))) {
				addResult("Field [" + name + "] is a primitive array.", null,
						MutabilityReason.ARRAY_TYPE_INHERENTLY_MUTABLE);
			}
		}

		return super.visitField(access, name, desc, signature, value);
	}

	private boolean isPrimitiveArray(String desc) {
		return Type.ARRAY == Type.getType(desc).getSort();
	}

}