# FinancialDocumentClassifier

FinancialDocumentClassifier is a java application which classifies the given documents according to their topics, by using word2vec and deeplearning. 

The library from [DeepLearning4j] is used during the development. ([dl4j - GitHub])

For safety concerns, the financial documents are replaced with Onur Dayıbaşı's Turkish [blogs].

### INFO - Main Topics and Training Files
Main topics are folders which contain training files in txt format. These training files are used to generate *paragraphVectors*.

In the *FinancialDocumentClassifier* there are three main topics:
- DEKONT
- FATURA
- EKSTRE

These main topics are referred in *FinancialTypeEnum* under the interface IFinancialClassifier, and can be edited.

### Resources for Training Set
The source packages can be reached under src/main/resources

The training files should be added under the folder: financialClassifier/trainingSet/[MAIN_TOPIC_NAME]

- Every main topic should be named as a specific folder. Such as;   
 
    financialClassifier/trainingSet/**DEKONT**

    financialClassifier/trainingSet/**FATURA**
 
    financialClassifier/trainingSet/**EKSTRE**
    
- Every training txt file should be under the folder [MAIN_TOPIC_NAME]

**It is recommended to have equal amount of training files under every main topic folder, in order to increase reliability.

### Resources for unClassified Set
The source packages can be reached under src/main/resources

The unClassified files should be added under the folder: financialClassifier/[unk]/unClassified
 
- Every un-classified txt file should be located as in the following:
    
    financialClassifier/[unk]/unClassified
 
- For testing purposes, [unk] contains the [MAIN_TOPIC_NAME] in the *FinancialDocumentClassifier*
 
    financialClassifier/**DEKONT**/unClassified

    financialClassifier/**FATURA**/unClassified
 
    financialClassifier/**EKSTRE**/unClassified
    
- Every un-classified txt file should be under the folder **unClassified**. 
 
### Setting the ClassPathResource for trainingSet files

The ClassPathResource should be set as:

    ClassPathResource resource = new ClassPathResource("financialClassifier/trainingSet");

- ** **DO NOT** ** declare the source as: ClassPathResource("financialClassifier/trainingSet/[MAIN_TOPIC_NAME]");

### Setting the ClassPathResource for unClassified files
The ClassPathResource should be set as:

    ClassPathResource unClassifiedResource = new ClassPathResource("financialClassifier/[unk]");

- ** **DO NOT** ** declare the source as: ClassPathResource("financialClassifier/[unk]/unClassified");

### Process

The *FinancialDocumentClassifier* works by firstly generating paragraph vectors from the training set, with the method *makeParagraphVectors*. 

The reliability of the learning process can be increased by setting the ideal "epoch" number *(default=10)*.

Later, the un-classified files are identified with the method *identifyFile* and returns the fileType as an enumeration. 

Since the learning process can be long, in order to prevent re-learning for every un-classified file, the reference from the *FinancialClassifier* should be used for calling the "makeParagraphVectors" method. Such as:

    FinancialClassifier fcTest = null;
    fcTest = FinancialClassifier.getReference();
    
### TESTING

*FinancialClassifierTest* test class demonstrates the learning process and document classification for every un-classified file.

You don't have to create seperate [unk] folders since you might not know their topics, you can just have one folder and put every un-classified file under financialClassifier/[unk]/unClassified .

However, for clarity the test class includes the three main topics.



   [DeepLearning4j]: <http://deeplearning4j.org/>
   [dl4j - GitHub]: <https://github.com/deeplearning4j/dl4j-examples>

