<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1553842164819" ID="ID_1224876261" MODIFIED="1553844036195" STYLE="fork" TEXT="MosipTestFramework">
<node CREATED="1553842973212" HGAP="32" ID="ID_1578236195" MODIFIED="1553843982798" POSITION="right" TEXT="Project" VSHIFT="9">
<node CREATED="1553842567845" HGAP="21" ID="ID_588724119" MODIFIED="1553843937161" TEXT="create" VSHIFT="10">
<node CREATED="1553843338912" ID="ID_71237982" MODIFIED="1553843465436" TEXT="Reads QAMill BaseURL, ExcelFilePath and TestCaseCollectionName from Property file and creates TestCaseCollection in QAMill DB"/>
<node CREATED="1553844121726" ID="ID_355129233" MODIFIED="1553844128820" TEXT="createTestCases">
<node CREATED="1553844161705" HGAP="17" ID="ID_286459311" MODIFIED="1553844358094" TEXT="RestClient.makeRestCall (SDCFromExcelURL, ExcelFilePath, TCCName)" VSHIFT="45"/>
<node CREATED="1553844387359" ID="ID_1675684550" MODIFIED="1553844423783" TEXT="RestClient.makeRestCall (TestCaseGeneratorURL, TCCName)"/>
</node>
</node>
<node CREATED="1553842613120" ID="ID_1113764703" MODIFIED="1553843935614" TEXT="fetch">
<node CREATED="1553843516273" HGAP="35" ID="ID_231210020" MODIFIED="1553844464965" TEXT="Reads QAMill BaseURL, TestCaseCollectionsPath, TestCaseCollectionName from Property file and fetch TestCaseCollection from QAMill DB. It creates testcase folders and request, response and stitching jsons in each testcase folders" VSHIFT="27"/>
<node CREATED="1553844479795" ID="ID_1620243742" MODIFIED="1553844486126" TEXT="fetchTestCases">
<node CREATED="1553844584817" ID="ID_647877144" MODIFIED="1553844642574" TEXT="QAMill Session api call to get Session Id" VSHIFT="38"/>
<node CREATED="1553844756032" ID="ID_140178091" MODIFIED="1553844834708" TEXT="Prepares map of api Id to URL and request method type for all APIs"/>
<node CREATED="1553844884895" ID="ID_1687654420" MODIFIED="1553844932532" TEXT="fetches TestCaseCollection json from QA Mill DB by calling TestCaseCollection GET api"/>
<node CREATED="1553844978352" ID="ID_140212410" MODIFIED="1553845021604" TEXT="Parse TestCaseCollection json and creates testcase folders and request, response and stitching jsons in each testcase folder"/>
<node CREATED="1553845091952" ID="ID_704700640" MODIFIED="1553845219126" TEXT="Creates TestCaseExecutionSequence file which contains testcase names (along with information whether the testcase is used for stitching for future) in the execution sequence"/>
</node>
</node>
<node CREATED="1553842640355" ID="ID_1160316886" MODIFIED="1553843931317" TEXT="execute">
<node CREATED="1553843787370" HGAP="43" ID="ID_1384980971" MODIFIED="1553845303528" TEXT="Reads QAMill BaseURL, Mosip BaseURL, TestCaseCollectionsPath, TestCaseCollectionName, TestCaseExecutionsPath, TestCaseExecutionName from Property file and executes testcases by traversing each testcase folders." VSHIFT="41"/>
<node CREATED="1553845310784" ID="ID_1467137619" MODIFIED="1553845316184" TEXT="executeTestCases">
<node CREATED="1553845325887" ID="ID_1507165407" MODIFIED="1553845389824" TEXT="Travers each testcase folders as per execution sequence and fetches request, response and stitching jsons"/>
<node CREATED="1553845394050" ID="ID_1361624147" MODIFIED="1553847877084" TEXT="Creates final request and expected response jsons by substituting values from already executed testcases for the keys mentioned in stitching json, and store these in executedtestcase folder. Also reads URL of the API, requestmethodtype, request parameters and headers from stitching json, and MosipbaseURL from property file"/>
<node CREATED="1553845635903" ID="ID_1648457543" MODIFIED="1553847827650" TEXT="Execute testcase, store actual response json in executedtestcase folder"/>
</node>
</node>
</node>
<node CREATED="1553842894144" HGAP="73" ID="ID_1665180769" MODIFIED="1553847940923" POSITION="left" TEXT="PropertyFile" VSHIFT="7">
<node CREATED="1553847965283" HGAP="29" ID="ID_1646197803" MODIFIED="1553847984728" TEXT="QAMill BaseURL" VSHIFT="28"/>
<node CREATED="1553848125134" ID="ID_595744894" MODIFIED="1553848136977" TEXT="QAMill UserName"/>
<node CREATED="1553848142800" ID="ID_1131071420" MODIFIED="1553848147945" TEXT="QAMill Password"/>
<node CREATED="1553848016370" ID="ID_1679028439" MODIFIED="1553848023132" TEXT="Mosip BaseURL"/>
<node CREATED="1553848177237" ID="ID_693079294" MODIFIED="1553848180506" TEXT="Operation"/>
<node CREATED="1553848158598" ID="ID_158618459" MODIFIED="1553848184906" TEXT="TestCaseCollectionsPath"/>
<node CREATED="1553848190719" ID="ID_1282200099" MODIFIED="1553848197854" TEXT="TestCaseCollectionsName"/>
<node CREATED="1553848201405" ID="ID_1856719665" MODIFIED="1553848209799" TEXT="TestCaseExecutionsPath"/>
<node CREATED="1553848212772" ID="ID_65569102" MODIFIED="1553848219546" TEXT="TestCaseExecutionsName"/>
<node CREATED="1553848233243" ID="ID_298354743" MODIFIED="1553848241506" TEXT="TestCaseExcelFilePath"/>
</node>
</node>
</map>
