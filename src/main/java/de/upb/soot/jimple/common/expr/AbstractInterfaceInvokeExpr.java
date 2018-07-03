/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
 * Copyright (C) 2004 Ondrej Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

package de.upb.soot.jimple.common.expr;

import de.upb.soot.StmtPrinter;
import de.upb.soot.jimple.Jimple;
import de.upb.soot.jimple.basic.ValueBox;
import de.upb.soot.jimple.common.ref.SootMethodRef;
import de.upb.soot.jimple.common.type.DoubleType;
import de.upb.soot.jimple.common.type.LongType;
import de.upb.soot.jimple.common.type.Type;
import de.upb.soot.jimple.common.type.VoidType;
import de.upb.soot.jimple.visitor.IExprVisitor;
import de.upb.soot.jimple.visitor.IVisitor;

@SuppressWarnings("serial")
public abstract class AbstractInterfaceInvokeExpr extends AbstractInstanceInvokeExpr {
  protected AbstractInterfaceInvokeExpr(ValueBox baseBox, SootMethodRef methodRef, ValueBox[] argBoxes) {
    super(methodRef, baseBox, argBoxes);
    if (methodRef.isStatic()) {
      throw new RuntimeException("wrong static-ness");
    }
  }

  @Override
  public boolean equivTo(Object o) {
    if (o instanceof AbstractInterfaceInvokeExpr) {
      AbstractInterfaceInvokeExpr ie = (AbstractInterfaceInvokeExpr) o;
      if (!(baseBox.getValue().equivTo(ie.baseBox.getValue()) && getMethod().equals(ie.getMethod())
          && (argBoxes == null ? 0 : argBoxes.length) == (ie.argBoxes == null ? 0 : ie.argBoxes.length))) {
        return false;
      }
      if (argBoxes != null) {
        for (int i = 0; i < argBoxes.length; i++) {
          if (!(argBoxes[i]).getValue().equivTo(ie.argBoxes[i].getValue())) {
            return false;
          }
        }
      }
      return true;
    }
    return false;
  }

  /**
   * Returns a hash code for this object, consistent with structural equality.
   */
  @Override
  public int equivHashCode() {
    return baseBox.getValue().equivHashCode() * 101 + getMethod().equivHashCode() * 17;
  }

  @Override
  public abstract Object clone();

  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer();

    buffer.append(Jimple.INTERFACEINVOKE + " " + baseBox.getValue().toString() + "." + methodRef.getSignature() + "(");

    if (argBoxes != null) {
      for (int i = 0; i < argBoxes.length; i++) {
        if (i != 0) {
          buffer.append(", ");
        }

        buffer.append(argBoxes[i].getValue().toString());
      }
    }

    buffer.append(")");

    return buffer.toString();
  }

  @Override
  public void toString(StmtPrinter up) {
    up.literal(Jimple.INTERFACEINVOKE);
    up.literal(" ");
    baseBox.toString(up);
    up.literal(".");
    up.methodRef(methodRef);
    up.literal("(");

    if (argBoxes != null) {
      for (int i = 0; i < argBoxes.length; i++) {
        if (i != 0) {
          up.literal(", ");
        }

        argBoxes[i].toString(up);
      }
    }

    up.literal(")");
  }

  @Override
  public void accept(IVisitor sw) {
    ((IExprVisitor) sw).caseInterfaceInvokeExpr(this);
  }

  private static int sizeOfType(Type t) {
    if (t instanceof DoubleType || t instanceof LongType) {
      return 2;
    } else if (t instanceof VoidType) {
      return 0;
    } else {
      return 1;
    }
  }

  private static int argCountOf(SootMethodRef m) {
    int argCount = 0;
    for (Type t : m.parameterTypes()) {
      argCount += sizeOfType(t);
    }

    return argCount;
  }

}