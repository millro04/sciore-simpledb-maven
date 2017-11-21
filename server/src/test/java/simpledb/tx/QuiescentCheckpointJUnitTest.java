/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpledb.tx;

import java.util.ArrayList;
import java.util.List;
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
        for (Integer i = 0; i < 10; i++) {
            Transaction tx;

            tx = new Transaction();

            Thread t = new Thread(tx);
            t.start();
            tx.commit();
        }

        for (Integer i = 0; i < 5; i++) {
            Transaction tx = new Transaction();
            Thread t = new Thread(tx);
            t.start();
            tx.commit();
        }

    }

    @Test
    public void QCPUnitTest2() throws Exception {
        //Create 10 transactions in 10 different threads
        List<Transaction> actives = new ArrayList<Transaction>();

        for (Integer i = 0; i < 10; i++) {
            Transaction tx;

            tx = new Transaction();

            Thread t = new Thread(tx);
            actives.add(tx);
            t.start();

        }

        for (Transaction t : actives) {
            t.commit();
            
        }
        
        for (Integer i = 0; i<999999999; i++) {
            //Give the test some time to let the quiescent checkpoint finish before exiting the test
        }

    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
