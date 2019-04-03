<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1553848586602" ID="ID_1861178875" MODIFIED="1553848722042" TEXT="MosipTestFramework">
<node CREATED="1553848820546" ID="ID_904446504" MODIFIED="1553848825991" POSITION="right" TEXT="QAMillWrapper">
<node CREATED="1553848921765" ID="ID_1031552739" MODIFIED="1553848961731" TEXT="TestCaseCollectionParser">
<node CREATED="1553849519992" ID="ID_842914895" MODIFIED="1553849586254" TEXT="Constructor">
<node CREATED="1553851255626" ID="ID_29852844" MODIFIED="1553851336924" TEXT="Reads property file and stores informations like QAMill BaseURL, Mosip BaseURL, ExcelFilePath, TCCPath, TCCName, TCEPath, TCEName"/>
</node>
<node CREATED="1553851239452" ID="ID_1031372176" MODIFIED="1553851245990" TEXT="createTestCases">
<node CREATED="1553851427183" ID="ID_1435923551" MODIFIED="1553851546992" TEXT="Calls QAMill SourcedDataCollection api to source data from excel file&#xa;Calls QAMill TestCaseGenerator apo to generate testcases for the data sourced from excelfile"/>
</node>
<node CREATED="1553849875994" ID="ID_1047249833" MODIFIED="1553850075796" TEXT="fetchTestCases">
<node CREATED="1553849572219" ID="ID_1917934044" MODIFIED="1553849576859" TEXT="getSessionId">
<node CREATED="1553851557712" ID="ID_39894349" MODIFIED="1553851738016" TEXT="Prepares and stores the sessionId needed to execute QAMill APIs, by making RestCall to QAMill Session POST api."/>
</node>
<node CREATED="1553849614085" ID="ID_1661825192" MODIFIED="1553849621280" TEXT="prepareApiToUrlmap">
<node CREATED="1553851626166" ID="ID_1829261064" MODIFIED="1553851760114" TEXT="URL and RequestMethodType for each API Id prepared and stored in a map"/>
</node>
<node CREATED="1553849591697" ID="ID_707277350" MODIFIED="1553849598271" TEXT="getTestCaseCollection">
<node CREATED="1553851771789" ID="ID_1897851025" MODIFIED="1553851811894" TEXT="Makes RestCall to TestCaseCollection GET api and returns the response json"/>
</node>
<node CREATED="1553849754797" ID="ID_1556270337" MODIFIED="1553849766681" TEXT="prepareExecutionOrderToTestCaseNameMap">
<node CREATED="1553851905567" ID="ID_1222019852" MODIFIED="1553852119318" TEXT="This will travers the TCC and prepares the list of ExecutionOrder and testCaseName. This is needed for 2 fold purpose; one to execute testcases in sequence, and other to get testcase name for execution sequence in case of stitching data"/>
</node>
<node CREATED="1553849671834" ID="ID_1392640967" MODIFIED="1553849679233" TEXT="parseTestCaseCollection">
<node CREATED="1553852201876" ID="ID_1161133259" MODIFIED="1553852234465" TEXT="Traverse each TestCase and calls parseTestCase"/>
<node CREATED="1553849685343" ID="ID_964819663" MODIFIED="1553850113047" TEXT="parseTestCase" VSHIFT="21">
<node CREATED="1553849698778" HGAP="12" ID="ID_1252212007" MODIFIED="1553850125797" TEXT="parseTestCaseRequestData" VSHIFT="20"/>
</node>
</node>
</node>
</node>
<node CREATED="1553848980179" ID="ID_1012992366" MODIFIED="1553849496782" TEXT="TestCaseExecutor"/>
</node>
<node CREATED="1553849264083" HGAP="27" ID="ID_107810164" MODIFIED="1553849281675" POSITION="left" TEXT="TestFramework" VSHIFT="23">
<node CREATED="1553849304387" FOLDED="true" HGAP="14" ID="ID_388913797" MODIFIED="1553850706501" TEXT="TestFramework" VSHIFT="31">
<node CREATED="1553849332559" HGAP="14" ID="ID_1625658608" MODIFIED="1553849476837" TEXT="MainMethod" VSHIFT="21"/>
</node>
</node>
<node CREATED="1553849292309" FOLDED="true" ID="ID_1018168333" MODIFIED="1553851175489" POSITION="left" TEXT="Utility" VSHIFT="-31">
<node CREATED="1553848854536" ID="ID_1361684921" MODIFIED="1553849375727" TEXT="FileClient" VSHIFT="19">
<node CREATED="1553850247846" FOLDED="true" ID="ID_1021295169" MODIFIED="1553850700384" TEXT="writeToFile">
<node CREATED="1553850417957" ID="ID_845397339" MODIFIED="1553850445096" TEXT="Accepts FileName &amp; FileContent and writes the content to file"/>
</node>
<node CREATED="1553850276878" FOLDED="true" ID="ID_1717454724" MODIFIED="1553850697521" TEXT="readFromFile">
<node CREATED="1553850466041" ID="ID_822220938" MODIFIED="1553850493903" TEXT="Accepts FileName and returns FileContent as string"/>
</node>
<node CREATED="1553850291989" FOLDED="true" ID="ID_574028161" MODIFIED="1553850696286" TEXT="readFromCsvFile">
<node CREATED="1553850499457" ID="ID_1615302804" MODIFIED="1553850549416" TEXT="Accepts FileName and returns FileContent as stringarray where each element contains comma separated string"/>
</node>
<node CREATED="1553850319970" FOLDED="true" ID="ID_1708888560" MODIFIED="1553850694755" TEXT="getMosipTestFrameworkProperties">
<node CREATED="1553850593922" ID="ID_569676472" MODIFIED="1553850659437" TEXT="Reads propertyFilePath from environment variable and reads all &lt;key,value&gt; pairs in the property file"/>
</node>
</node>
<node CREATED="1553848864077" ID="ID_754247337" MODIFIED="1553848873021" TEXT="RestClient">
<node CREATED="1553850730292" ID="ID_1442332914" MODIFIED="1553850735082" TEXT="makeRestCall">
<node CREATED="1553850822287" ID="ID_1350267239" MODIFIED="1553850892757" TEXT="Accepts RestClientInput. Depending upon URL string, it calls makeHttpRestCall or makeHttpsRestCall. Returns RestClientOutput."/>
</node>
<node CREATED="1553850762181" ID="ID_1804201348" MODIFIED="1553850783659" TEXT="makeHttpRestCall">
<node CREATED="1553850928052" ID="ID_1967334510" MODIFIED="1553850952331" TEXT="Makes Http Rest Call"/>
</node>
<node CREATED="1553850788234" ID="ID_1864363876" MODIFIED="1553850795645" TEXT="makeHttpsRestCall">
<node CREATED="1553850960306" ID="ID_1664212453" MODIFIED="1553850986290" TEXT="Makes Https Rest Call"/>
</node>
</node>
<node CREATED="1553848877310" ID="ID_512631021" MODIFIED="1553848883489" TEXT="RestClientInput">
<node CREATED="1553851036180" ID="ID_1958312787" MODIFIED="1553851110563" TEXT="Contains data needed for Rest call, like Url, requestMethod, requestBody, params, headers, isRequestBodyFileAttachment, filePath, fieldName, mimeType"/>
</node>
<node CREATED="1553848887899" ID="ID_705023068" MODIFIED="1553848893422" TEXT="RestClientOutput">
<node CREATED="1553851124922" ID="ID_701501046" MODIFIED="1553851151451" TEXT="Contains response of a Rest Call like, responseCode, responseBody, responseHeaders"/>
</node>
</node>
</node>
</map>
