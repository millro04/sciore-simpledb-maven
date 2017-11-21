/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpledb.tx;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import simpledb.server.SimpleDB;
import simpledb.tx.Transaction;
/**
 *
 * @author administrator
 */
public class QuiescentCheckpointJUnitTest {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
       SimpleDB.init("testdb");
    }

    @After
    public void tearDown() {
    }
    
    public QuiescentCheckpointJUnitTest() {
    }
    
    @Test
    public void QCPUnitTest1() throws Exception {
        //Create 10 transactions in 10 different threads
        for (Integer i = 0; i < 9; i++) {
            Transaction tx;
            tx = new Transaction();
            Thread t = new Thread(tx);
            t.start();      
            tx.commit();
        }

        for (Integer i = 0; i < 4; i++) {
            Transaction tx = new Transaction();
            Thread t = new Thread(tx);
            t.start();
            tx.commit();
        }

    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
