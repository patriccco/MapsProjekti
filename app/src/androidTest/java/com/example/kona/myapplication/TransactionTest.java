package com.example.kona.myapplication;



import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Rahis on 7.3.2018.
 */

/*@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(JUnit4.class)*/
public class TransactionTest {

    Transaction trans = new Transaction();
    /*Set up the connection to Transaction.class and database*/

    /*Test UserHP getter*/
    @Test
    public void getUserHP() throws Exception {
        trans.setUserHP(100);
        trans.getUserHP();
        assertEquals("Get the userHP: ", 100, trans.getUserHP());


    }

    /*Test userHP setter -> 100hp*/
    @Before
    public void setUserHP() throws Exception {
        trans.setUserHP(100);
        assertEquals("Set userHP to 100: ", 100, trans.getUserHP());

    }

    /*Test ItemHP getter*/
    @Test
    public void getItemhp() throws Exception {
        trans.setItemhp(100);
        trans.getItemhp();
        assertEquals("Get the userHP: ", 100, trans.getItemhp());
    }

    /*Test ItemHP setter to 100hp*/
    @Test
    public void setItemhp() throws Exception {
        trans.setItemhp(100);
        assertEquals("Get the userHP: ", 100, trans.getItemhp());
    }

    /*Test Price getter*/
    @Test
    public void getPrice() throws Exception {
        trans.setPrice(100);
        trans.getPrice();
        assertEquals("Get the userHP: ", 100, trans.getPrice());
    }

    /*Test price setter to 100*/
    @Test
    public void setPrice() throws Exception {
        trans.setPrice(100);
        assertEquals("Get the userHP: ", 100, trans.getPrice());
    }

    /*Test price getter*/
    @Test
    public void getMoney() throws Exception {
        trans.setMoney(100);
        trans.getMoney();
        assertEquals("Get the userHP: ", 100, trans.getMoney());
    }

    /*Test price setter to 100*/
    @Test
    public void setMoney() throws Exception {
        trans.setMoney(100);
        assertEquals("Get the userHP: ", 100, trans.getMoney());
    }

    /*Set up the connection to Transaction.class*/
    @Test
    public void checkHP() throws Exception {

    }

    /*Set up the connection to Transaction.class*/
    @Test
    public void addMoney() throws Exception {
    }

    /*Set up the connection to Transaction.class*/
    @Test
    public void addHP() throws Exception {
    }

    /*Set up the connection to Transaction.class*/
    @Test
    public void checkmoney() throws Exception {
    }

    /*Set up the connection to Transaction.class*/
    @Test
    public void checkPrice() throws Exception {
    }

    /*Set up the connection to Transaction.class*/
    @Test
    public void valid() throws Exception {
    }

}
