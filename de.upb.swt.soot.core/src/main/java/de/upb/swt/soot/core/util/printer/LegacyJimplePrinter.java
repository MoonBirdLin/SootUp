package de.upb.swt.soot.core.util.printer;

import de.upb.swt.soot.core.jimple.common.stmt.Stmt;
import de.upb.swt.soot.core.jimple.javabytecode.stmt.JSwitchStmt;
import de.upb.swt.soot.core.model.Body;

/**
 * StmtPrinter implementation for normal (full) Jimple for OldSoot
 *
 * <p>List of differences between old and current Jimple: - tableswitch and lookupswitch got merged
 * into switch - now imports are possible - disabled
 *
 * @author Markus Schmidt
 */
public class LegacyJimplePrinter extends NormalStmtPrinter {

  public LegacyJimplePrinter(Body b) {
    super(b);
  }

  public LegacyJimplePrinter() {}

  @Override
  void enableImports(boolean enable) {
    if (enable) {
      throw new RuntimeException(
          "Imports are not supported in Legacy Jimple: don't enable UseImports");
    }
  }

  @Override
  public void stmt(Stmt currentStmt) {
    startStmt(currentStmt);
    // replace switch with lookupswitch
    if (currentStmt instanceof JSwitchStmt) {
      // prepend to switch Stmt
      literal(((JSwitchStmt) currentStmt).isTableSwitch() ? "table" : "lookup");
    }
    currentStmt.toString(this);
    endStmt(currentStmt);
    literal(";");
    newline();
  }
}