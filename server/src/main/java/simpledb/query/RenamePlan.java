/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpledb.query;
//Take project as your skeleton

import simpledb.record.Schema;

/**
 *
 * @author administrator
 */
public class RenamePlan implements Plan {

    private final TablePlan tblPlan;
    private final String field1;
    private final String field2;

    public RenamePlan(TablePlan tblPlan, String field1, String field2) {
        this.tblPlan = tblPlan;
        this.field1 = field1;
        this.field2 = field2;
    }

    @Override
    public Scan open() {
        tblPlan.schema().rename(field1, field2);
        return tblPlan.open();
    }

    @Override
    public int blocksAccessed() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int recordsOutput() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int distinctValues(String fldname) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Schema schema() {
        return tblPlan.schema();
    }

}
