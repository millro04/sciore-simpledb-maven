package simpledb.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import simpledb.parse.Parser;
import simpledb.server.SimpleDB;
import simpledb.tx.Transaction;

/**
 *
 * @author yasiro01
 */
public class RenamePlanTest {

    public RenamePlanTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        SimpleDB.init("studentdb");
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void TestRenamePlanAndScan() {
        System.out.println("RENAME");
        Transaction tx = new Transaction();
        TablePlan studentTblPlan = new TablePlan("student", tx);
        tx.commit();
        Object[] expected = {"majorid", "gradyear", "studentName", "sid"};
        //    Plan p = SimpleDB.planner().createQueryPlan(qry, tx);
        Plan renamePlan = new RenamePlan(studentTblPlan, "sname", "studentName");
        Scan renameScan = renamePlan.open();
        int records = 0;
        System.out.println();
        for (String field : renamePlan.schema().fields()) {
            System.out.println(field);
        }
        Object[] actual = renamePlan.schema().fields().toArray();
        Boolean areEqual = Arrays.equals(actual, expected);
        assertEquals(true, areEqual);

    }
}
