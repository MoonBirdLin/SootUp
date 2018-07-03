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
import de.upb.soot.core.SootClass;
import de.upb.soot.core.SootMethod;
import de.upb.soot.jimple.Jimple;
import de.upb.soot.jimple.basic.Value;
import de.upb.soot.jimple.basic.ValueBox;
import de.upb.soot.jimple.common.ref.SootMethodRef;
import de.upb.soot.jimple.visitor.IExprVisitor;
import de.upb.soot.jimple.visitor.IVisitor;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;

@SuppressWarnings("serial")
public class JDynamicInvokeExpr extends AbstractInvokeExpr {
  protected SootMethodRef bsmRef;
  protected ValueBox[] bsmArgBoxes;
  protected int tag;

  public JDynamicInvokeExpr(SootMethodRef bootstrapMethodRef, List<? extends Value> bootstrapArgs, SootMethodRef methodRef,
      int tag, List<? extends Value> methodArgs) {
    super(methodRef, new ValueBox[methodArgs.size()]);

    if (!methodRef.getSignature().startsWith("<" + SootClass.INVOKEDYNAMIC_DUMMY_CLASS_NAME + ": ")) {
      throw new IllegalArgumentException(
          "Receiver type of JDynamicInvokeExpr must be " + SootClass.INVOKEDYNAMIC_DUMMY_CLASS_NAME + "!");
    }

    this.bsmRef = bootstrapMethodRef;
    this.bsmArgBoxes = new ValueBox[bootstrapArgs.size()];
    this.tag = tag;

    for (int i = 0; i < bootstrapArgs.size(); i++) {
      this.bsmArgBoxes[i] = Jimple.getInstance().newImmediateBox(bootstrapArgs.get(i));
    }
    for (int i = 0; i < methodArgs.size(); i++) {
      this.argBoxes[i] = Jimple.getInstance().newImmediateBox(methodArgs.get(i));
    }
  }

  public JDynamicInvokeExpr(SootMethodRef bootstrapMethodRef, List<? extends Value> bootstrapArgs, SootMethodRef methodRef,
      List<? extends Value> methodArgs) {
    /*
     * Here the static-handle is chosen as default value, because this works for Java.
     */
    this(bootstrapMethodRef, bootstrapArgs, methodRef, Opcodes.H_INVOKESTATIC, methodArgs);
  }

  public int getBootstrapArgCount() {
    return bsmArgBoxes.length;
  }

  public Value getBootstrapArg(int index) {
    return bsmArgBoxes[index].getValue();
  }

  @Override
  public Object clone() {
    List<Value> clonedBsmArgs = new ArrayList<Value>(getBootstrapArgCount());
    for (int i = 0; i < getBootstrapArgCount(); i++) {
      clonedBsmArgs.add(i, getBootstrapArg(i));
    }

    List<Value> clonedArgs = new ArrayList<Value>(getArgCount());
    for (int i = 0; i < getArgCount(); i++) {
      clonedArgs.add(i, getArg(i));
    }

    return new JDynamicInvokeExpr(bsmRef, clonedBsmArgs, methodRef, tag, clonedArgs);
  }

  @Override
  public boolean equivTo(Object o) {
    if (o instanceof JDynamicInvokeExpr) {
      JDynamicInvokeExpr ie = (JDynamicInvokeExpr) o;
      if (!(getMethod().equals(ie.getMethod()) && bsmArgBoxes.length == ie.bsmArgBoxes.length)) {
        return false;
      }
      int i = 0;
      for (ValueBox element : bsmArgBoxes) {
        if (!(element.getValue().equivTo(ie.getBootstrapArg(i)))) {
          return false;
        }
        i++;
      }
      if (!(getMethod().equals(ie.getMethod())
          && (argBoxes == null ? 0 : argBoxes.length) == (ie.argBoxes == null ? 0 : ie.argBoxes.length))) {
        return false;
      }
      if (argBoxes != null) {
        i = 0;
        for (ValueBox element : argBoxes) {
          if (!(element.getValue().equivTo(ie.getArg(i)))) {
            return false;
          }
          i++;
        }
      }
      if (!methodRef.equals(ie.methodRef)) {
        return false;
      }
      if (!bsmRef.equals(ie.bsmRef)) {
        return false;
      }
      return true;
    }
    return false;
  }

  public SootMethod getBootstrapMethod() {
    return bsmRef.resolve();
  }

  /**
   * Returns a hash code for this object, consistent with structural equality.
   */
  @Override
  public int equivHashCode() {
    return getBootstrapMethod().equivHashCode() * getMethod().equivHashCode() * 17;
  }

  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer();

    buffer.append(Jimple.DYNAMICINVOKE);
    buffer.append(" \"");
    buffer.append(methodRef.name()); // quoted method name (can be any UTF8
    // string)
    buffer.append("\" <");
    buffer
        .append(SootMethod.getSubSignature(""/* no method name here */, methodRef.parameterTypes(), methodRef.returnType()));
    buffer.append(">(");

    if (argBoxes != null) {
      for (int i = 0; i < argBoxes.length; i++) {
        if (i != 0) {
          buffer.append(", ");
        }

        buffer.append(argBoxes[i].getValue().toString());
      }
    }

    buffer.append(") ");

    buffer.append(bsmRef.getSignature());
    buffer.append("(");
    for (int i = 0; i < bsmArgBoxes.length; i++) {
      if (i != 0) {
        buffer.append(", ");
      }

      buffer.append(bsmArgBoxes[i].getValue().toString());
    }
    buffer.append(")");

    return buffer.toString();
  }

  @Override
  public void toString(StmtPrinter up) {
    up.literal(Jimple.DYNAMICINVOKE);
    up.literal(" \"" + methodRef.name() + "\" <"
        + SootMethod.getSubSignature(""/* no method name here */, methodRef.parameterTypes(), methodRef.returnType())
        + ">(");

    if (argBoxes != null) {
      for (int i = 0; i < argBoxes.length; i++) {
        if (i != 0) {
          up.literal(", ");
        }

        argBoxes[i].toString(up);
      }
    }

    up.literal(") ");
    up.methodRef(bsmRef);
    up.literal("(");

    for (int i = 0; i < bsmArgBoxes.length; i++) {
      if (i != 0) {
        up.literal(", ");
      }

      bsmArgBoxes[i].toString(up);
    }

    up.literal(")");
  }

  @Override
  public void accept(IVisitor sw) {
    ((IExprVisitor) sw).caseDynamicInvokeExpr(this);
  }

  public SootMethodRef getBootstrapMethodRef() {
    return bsmRef;
  }

  public List<Value> getBootstrapArgs() {
    List<Value> l = new ArrayList<Value>();
    for (ValueBox element : bsmArgBoxes) {
      l.add(element.getValue());
    }

    return l;
  }

  public int getHandleTag() {
    return tag;
  }
}