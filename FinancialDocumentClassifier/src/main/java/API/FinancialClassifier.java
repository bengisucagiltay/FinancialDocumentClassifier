package API;

import dl4j.tools.FileLabelAwareIterator;
import dl4j.tools.LabelSeeker;
import dl4j.tools.MeansBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.berkeley.Pair;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.text.documentiterator.LabelAwareIterator;
import org.deeplearning4j.text.documentiterator.LabelledDocument;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static java.nio.file.StandardCopyOption.*;
import java.util.logging.Level;
import org.apache.uima.pear.tools.PackageBrowser;

/**
 * class: FinancialClassifier
 * Classifies all the files and places them in required folders
 * 
 */
public class FinancialClassifier implements IFinancialClassifier{
    
    FinancialTypeEnum fileType;
    ParagraphVectors paragraphVectors;
    LabelAwareIterator iterator;
    TokenizerFactory tokenizerFactory;
    
    private static final Logger log = LoggerFactory.getLogger(FinancialClassifier.class);
    private static FinancialClassifier reference = null;

    
    /**
     * makeParagraphVectors() generates the training set from the given documents
     * 
     */
    public void makeParagraphVectors() {
      ClassPathResource resource = new ClassPathResource("financialClassifier/trainingSet");

        try {
            // build a iterator for our dataset
            iterator = new FileLabelAwareIterator.Builder()
                    .addSourceFolder(resource.getFile())
                    .build();
        } catch (FileNotFoundException ex) {
        }

      tokenizerFactory = new DefaultTokenizerFactory();
      tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

      // ParagraphVectors training configuration
      paragraphVectors = new ParagraphVectors.Builder()
              .learningRate(0.025)
              .minLearningRate(0.001)
              .batchSize(1000)
              .epochs(10)
              .iterate(iterator)
              .trainWordVectors(true)
              .tokenizerFactory(tokenizerFactory)
              .stopWords(stopwords)
              .build();

      // Start model training
      paragraphVectors.fit();
    }
    
    /**
    * identifyFile(ClassPathResource unClassifiedResource): takes the path of the source file and 
    * determines the topic of the file and classifies it by comparing their score.
    *      
    * @return FinancialTypeEnum
    * @throws java.io.FileNotFoundException
    */
    @Override
    public FinancialTypeEnum identifyFile(ClassPathResource unClassifiedResource) throws FileNotFoundException{
        int n = 0;
        fileType = FinancialTypeEnum.UNKNOWN;
        //ClassPathResource unClassifiedResource = new ClassPathResource("unk");
        
        FileLabelAwareIterator unClassifiedIterator = new FileLabelAwareIterator.Builder()
            .addSourceFolder(unClassifiedResource.getFile())
            .build();

        MeansBuilder meansBuilder = new MeansBuilder(
            (InMemoryLookupTable<VocabWord>)paragraphVectors.getLookupTable(),
              tokenizerFactory);
        LabelSeeker seeker = new LabelSeeker(iterator.getLabelsSource().getLabels(),
            (InMemoryLookupTable<VocabWord>) paragraphVectors.getLookupTable());

        while (unClassifiedIterator.hasNextDocument()) {
            LabelledDocument document = unClassifiedIterator.nextDocument();
            INDArray documentAsCentroid = meansBuilder.documentAsVector(document);
            List<Pair<String, Double>> scores = seeker.getScores(documentAsCentroid);
            double largest = (scores.get(0)).getSecond();
            for(int i = 0; i < scores.size();i++){
               if((scores.get(i)).getSecond() >= largest){   // getSecond() -> vectoral distance
                   fileType = FinancialTypeEnum.valueOf(((scores.get(i)).getFirst()).toUpperCase(Locale.ENGLISH));      // getFirst() -> name of 
                   largest = (scores.get(i)).getSecond();
                }
            }
            System.out.println("File '" + unClassifiedIterator.getFileName() + "' from document '" + document.getLabel() + "(index:" + n +")' falls into the following category: ");
            System.out.println("\t" + fileType.name());
            
            File source = new File ("src/main/resources/financialClassifier/UNK/unClassified/" + unClassifiedIterator.getFileName());
            Path sourcePath = FileSystems.getDefault().getPath(source.getAbsolutePath());
            
            File target = new File ("src/main/resources/classified/" + fileType.name() + "/" + unClassifiedIterator.getFileName()); // resources/classified/FATURA/XX.txt
            Path targetPath = FileSystems.getDefault().getPath(target.getAbsolutePath());
            
            try {
                Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                //Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
            }
            
            n++;
            
            
            //print all file occurance and details
            
            /*
            log.info("Document '" + document.getLabel() + "' falls into the following categories: ");
            for (Pair<String, Double> score: scores) {
            log.info("        " + score.getFirst() + ": " + score.getSecond());
            }*/
        }
    return fileType;
    }
    

    
    //Singleton Pattern for learning process
	public static FinancialClassifier getReference(){
        if(reference == null){
            reference = new FinancialClassifier();
            reference.makeParagraphVectors();
        }
        return reference;
	}
    
        //a list of the stopwords
        List<String> stopwords = Arrays.asList
		(
				"a",
				"aynı",
				"acaba",
				"altı",
				"ama",
				"ancak",
				"artık",
				"asla",
				"aslında",
				"aşağıdaki",
				"az",
				"b",
				"bana",
				"bazen",
				"bazı",
				"bazıları",
				"bazısı",
				"belki",
				"ben",
				"beni",
				"benim",
				"beş",
				"bile",
				"bir",
				"birçoğu",
				"birçok",
				"birçokları",
				"biri",
				"birisi",
				"birkaç",
				"birkaçı",
				"birşey",
				"birşeyi",
				"biz",
				"bize",
				"bizi",
				"bizim",
				"böyle",
				"böylece",
				"bu",
				"buna",
				"bunda",
				"bundan",
				"bunu",
				"bunun",
				"burada",
				"bütün",
				"c",
				"ç",
				"çoğu",
				"çoğuna",
				"çoğunu",
				"çok",
				"çünkü",
				"d",
				"da",
				"daha",
				"de",
				"değil",
				"demek",
				"diğer",
				"diğeri",
				"diğerleri",
				"diye",
				"dokuz",
				"dolayı",
				"dört",
				"e",
				"elbette",
				"en",
				"eğer",
				"f",
				"farklı",
				"fakat",
				"falan",
				"felan",
				"filan",
				"g",
				"gene",
				"gerçek",
				"gerekiyor",
				"gerekir",
				"gibi",
				"ğ",
				"h",
				"hâlâ",
				"hale",
				"hangi",
				"hangisi",
				"hani",
				"hatta",
				"hem",
				"henüz",
				"hep",
				"hepsi",
				"hepsine",
				"hepsini",
				"her",
				"herhangi",
				"her biri",
				"herkes",
				"herkese",
				"herkesi",
				"hiç",
				"hiç kimse",
				"hiçbiri",
				"hiçbirine",
				"hiçbirini",
				"ı",
				"i",
				"için",
				"içinde",
				"içerisine",
				"içerisinde",
				"iki",
				"ile",
				"ilgili",
				"ise",
				"işte",
				"j",
				"k",
				"kaç",
				"kadar",
				"kendi",
				"kendine",
				"kendini",
				"ki",
				"kim",
				"kime",
				"kimi",
				"kimin",
				"kimisi",
				"l",
				"m",
				"madem",
				"mesela",
				"mı",
				"mi",
				"mu",
				"mü",
				"mü",
				"n",
				"nasıl",
				"ne",
				"ne kadar",
				"ne zaman",
				"neden",
				"nedir",
				"nerde",
				"nerede",
				"nereden",
				"nereye",
				"nesi",
				"neyse",
				"niçin",
				"niye",
				"o",
				"on",
				"ona",
				"ondan",
				"olan",
				"oldukça",
				"olarak",
				"olacak",
				"olması",
				"onlar",
				"onlara",
				"onlardan",
				"onların",
				"onların",
				"onu",
				"onun",
				"orada",
				"oysa",
				"oysaki",
				"ö",
				"öbürü",
				"ön",
				"önce",
				"ötürü",
				"öyle",
				"örneğin",
				"p",
				"r",
				"rağmen",
				"s",
				"sana",
				"sağlar",
				"sekiz",
				"sen",
				"senden",
				"seni",
				"senin",
				"sıkça",
				"siz",
				"sizden",
				"size",
				"sizi",
				"sizin",
				"son",
				"sonra",
				"ş",
				"şayet",
				"şey",
				"şeyden",
				"şeye",
				"şeyi",
				"şeyler",
				"şekilde",
				"şimdi",
				"şöyle",
				"şu",
				"şuna",
				"şunda",
				"şundan",
				"şunlar",
				"şunu",
				"şunun",
				"t",
				"tabi",
				"tamam",
				"tüm",
				"tümü",
				"u",
				"ü",
				"üç",
				"üzere",
				"üzerine",
				"üzerinde",
				"üzerinden",
				"üzerindeki",
				"v",
				"var",
				"ve",
				"veya",
				"veyahut",
				"y",
				"ya",
				"ya da",
				"yani",
				"yedi",
				"yerine",
				"yine",
				"yoksa",
				"z",
				"zaten",
				"zira",	
				
				"information",
				"www.",
				".com",
				".tr",
				"the",
				"your",
				"and",
				"you",
				"for",
				"this",
				"please",
				"with",
				"from",
				"are",
				"may",
				"obj",
				"endobj",
				"endstreamendobj"
		);
}

