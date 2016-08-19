/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package API;

import org.datavec.api.util.ClassPathResource;

/**
 *
 * @author Bengisu
 */
public class Main {
   
    public static void main(String[] args) throws Exception {
    
    FinancialClassifier fcTest = FinancialClassifier.getReference(); //makeParagraphVectors()
            
     
    ClassPathResource unClassified = new ClassPathResource("financialClassifier/UNK");
    IFinancialClassifier.FinancialTypeEnum fcType = fcTest.identifyFile(unClassified);
    }
}
