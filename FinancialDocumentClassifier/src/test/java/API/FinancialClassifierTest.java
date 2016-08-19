package API;

import org.datavec.api.util.ClassPathResource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * FinancialClassifierTest class generates the paragraph vectors according to the training set
 * and tests the given inputs according to the FinancialTypeEnum
 * @author Bengisu
 */

public class FinancialClassifierTest {
   
    FinancialClassifier fcTest = null;
    
    public FinancialClassifierTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        fcTest = FinancialClassifier.getReference();
    }
        
    @After
    public void tearDown() {
    }

    /**
     * Test of "DEKONT" type files
     * 
     * @throws Exception 
     */
    @Test
    public void testDEKONT() throws Exception {
        
        ClassPathResource unClassifiedDEKONT = new ClassPathResource("financialClassifier/DEKONT");
        IFinancialClassifier.FinancialTypeEnum fcType = fcTest.identifyFile(unClassifiedDEKONT);
        assertEquals(IFinancialClassifier.FinancialTypeEnum.DEKONT,fcType);
    }
    
    /**
     * Test of "EKSTRE" type files
     * 
     * @throws Exception 
     */
    @Test
    public void testEKSTRE() throws Exception {
        
        ClassPathResource unClassifiedEKSTRE = new ClassPathResource("financialClassifier/EKSTRE");
        IFinancialClassifier.FinancialTypeEnum fcType = fcTest.identifyFile(unClassifiedEKSTRE);
        assertEquals(IFinancialClassifier.FinancialTypeEnum.EKSTRE,fcType);
    }
    
    /**
     * Test of "FATURA" type files
     * 
     * @throws Exception 
     */
    @Test
    public void testFATURA() throws Exception {
        
        ClassPathResource unClassifiedFATURA = new ClassPathResource("financialClassifier/FATURA");
        IFinancialClassifier.FinancialTypeEnum fcType = fcTest.identifyFile(unClassifiedFATURA);
        assertEquals(IFinancialClassifier.FinancialTypeEnum.FATURA,fcType);
    }
    
}
