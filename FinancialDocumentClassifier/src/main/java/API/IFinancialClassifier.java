package API;

import java.io.FileNotFoundException;
import org.datavec.api.util.ClassPathResource;

/**
 * The interface of the Financial Classifier
 * @author Bengisu
 */
public interface IFinancialClassifier {
    
    public enum FinancialTypeEnum {DEKONT, FATURA, EKSTRE, UNKNOWN};
    
    FinancialTypeEnum identifyFile(ClassPathResource unClassifiedResource) throws FileNotFoundException ;   
    
}
