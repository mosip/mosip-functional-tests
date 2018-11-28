<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1542891959910" ID="ID_1608338141" MODIFIED="1542892828593" TEXT="OTP">
<node CREATED="1542347928502" ID="ID_1145351847" MODIFIED="1542892259933" POSITION="right" TEXT="MOS -33">
<arrowlink DESTINATION="ID_1145351847" ENDARROW="Default" ENDINCLINATION="0;0;" ID="Arrow_ID_658972308" STARTARROW="None" STARTINCLINATION="0;0;"/>
<linktarget COLOR="#b0b0b0" DESTINATION="ID_1145351847" ENDARROW="Default" ENDINCLINATION="0;0;" ID="Arrow_ID_658972308" SOURCE="ID_1145351847" STARTARROW="None" STARTINCLINATION="0;0;"/>
<icon BUILTIN="forward"/>
<node COLOR="#009900" CREATED="1542344601626" ID="ID_1233222504" MODIFIED="1542892289062" TEXT="facilitate a request to generate/regenerate an OTP">
<edge COLOR="#33ff33"/>
<node COLOR="#009900" CREATED="1542344880619" ID="ID_1297719544" MODIFIED="1542892359487" TEXT="Call OTP generator API with the input parameters (Key)">
<node COLOR="#009900" CREATED="1542344897773" ID="ID_1181847844" MODIFIED="1542892401242" TEXT="Verify the Received  OTP from the OTP generator">
<node CREATED="1542892502548" ID="ID_303147258" MODIFIED="1542892521658" TEXT="Verify OTP receiived is minimum number of digit"/>
<node CREATED="1542892866986" ID="ID_803791031" MODIFIED="1542893060278" TEXT="MOS-34">
<icon BUILTIN="forward"/>
<node CREATED="1542892937255" ID="ID_1411207919" MODIFIED="1542892952965" TEXT=" OTP as per the defined logic">
<node CREATED="1542892961354" ID="ID_569428117" MODIFIED="1542892981877" TEXT="Check OTP is stored in OTP database">
<node CREATED="1542893004513" ID="ID_1837685131" MODIFIED="1542893021140" TEXT="Verify OTP is expeired base on timeout policy"/>
</node>
</node>
<node CREATED="1542893060278" ID="ID_470485650" MODIFIED="1542893070964" TEXT="Verify OTP able to regenerate">
<node CREATED="1542893073344" ID="ID_1086010009" MODIFIED="1542893094861" TEXT="Verify exisiting OTP in databse in regenerated with new value"/>
</node>
</node>
<node CREATED="1542893148960" ID="ID_65369190" MODIFIED="1542893157200" TEXT="MOS-35">
<icon BUILTIN="forward"/>
<node CREATED="1542893158707" ID="ID_1107890748" MODIFIED="1542893237443" TEXT="REQeust to to check validity of the OTP with the required input parameter (OTP,Key)">
<node CREATED="1542893740481" ID="ID_943825122" MODIFIED="1542893774306" TEXT="Validate successfully the OTP and delete the OTP from Database"/>
</node>
</node>
<node COLOR="#cc0033" CREATED="1542344769663" ID="ID_1573402377" MODIFIED="1542894367520" TEXT="MOS-36- ">
<edge COLOR="#cc0033"/>
<icon BUILTIN="forward"/>
<node COLOR="#cc0000" CREATED="1542893856027" ID="ID_1367839099" MODIFIED="1542894355555" TEXT="Input Wrong OTP ">
<node COLOR="#cc0000" CREATED="1542893878013" ID="ID_1540701240" MODIFIED="1542894355555" TEXT="Validate error message received &quot;&quot;OTP Validation Failure&quot;&quot;">
<node CREATED="1542894548739" ID="ID_471982374" MODIFIED="1542894579535" TEXT="MOS-423 Error message &quot;Invalid&quot;"/>
</node>
</node>
</node>
<node CREATED="1542894098383" ID="ID_322050129" MODIFIED="1542894109846" TEXT="MOS-991">
<icon BUILTIN="forward"/>
<node COLOR="#cc0000" CREATED="1542894112188" ID="ID_1065888665" MODIFIED="1542894382084" TEXT="Input  wrong OTP 3 times">
<edge COLOR="#cc0000"/>
<node COLOR="#cc0000" CREATED="1542894139834" ID="ID_1018075495" MODIFIED="1542894355555" TEXT="check account is frozen ">
<node COLOR="#cc0000" CREATED="1542894172486" ID="ID_458405695" MODIFIED="1542894355555" TEXT="Check message &quot;Blocked&quot;"/>
<node COLOR="#cc0000" CREATED="1542894187376" ID="ID_832703401" MODIFIED="1542894355555" TEXT="Validate if able to generate the OTP for Frozen account">
<node COLOR="#cc0000" CREATED="1542894254260" ID="ID_666074812" MODIFIED="1542894355555" TEXT="reject the OTP generation request"/>
<node COLOR="#cc0000" CREATED="1542894270142" ID="ID_1343510266" MODIFIED="1542894355555" TEXT="Check time after ccount freeze timeout policy">
<node COLOR="#cc0000" CREATED="1542894288882" ID="ID_910132790" MODIFIED="1542894355555" TEXT="Verify able to generate OTP after freeze timeout time expires"/>
</node>
</node>
</node>
</node>
</node>
<node CREATED="1542895106405" ID="ID_596565366" MODIFIED="1542895117026" TEXT="MSO-443">
<icon BUILTIN="forward"/>
<node COLOR="#cc0033" CREATED="1542895118674" ID="ID_855748639" MODIFIED="1542895236014" TEXT="OTP is not present in databse">
<edge COLOR="#ff0000"/>
<node COLOR="#cc0033" CREATED="1542895137896" ID="ID_520928792" MODIFIED="1542895236014" TEXT="Error Message &quot;&quot;Invalid Input&quot;&quot;">
<edge COLOR="#ff0000"/>
</node>
</node>
<node COLOR="#cc0033" CREATED="1542895164983" ID="ID_367710231" MODIFIED="1542895236014" TEXT="Input empty OTP as input">
<edge COLOR="#ff0000"/>
<node COLOR="#cc0033" CREATED="1542895196436" ID="ID_890260582" MODIFIED="1542895236014" TEXT="Error Message &quot;OTP can&apos;t be empty or null&quot;">
<edge COLOR="#ff0000"/>
<node COLOR="#cc0033" CREATED="1542895210605" ID="ID_478916150" MODIFIED="1542895236014" TEXT="Error Code&quot;KER-OTM-OTV-003&quot;">
<edge COLOR="#ff0000"/>
</node>
</node>
</node>
<node COLOR="#ff0000" CREATED="1542895277255" ID="ID_1086288579" MODIFIED="1542895379442" TEXT="Input Non Numeric as OTP input ">
<edge COLOR="#cc3300"/>
<node COLOR="#ff0000" CREATED="1542895341962" ID="ID_664407660" MODIFIED="1542895379442" TEXT="Error Message &quot;OTP consists of only numeric characters. No other characters is allowed&quot;">
<edge COLOR="#cc3300"/>
<node COLOR="#ff0000" CREATED="1542895355120" ID="ID_1381752696" MODIFIED="1542895379442" TEXT="Error Code &quot;KER-OTM-OTV-004&quot;">
<edge COLOR="#cc3300"/>
</node>
</node>
</node>
</node>
</node>
</node>
<node COLOR="#cc0000" CREATED="1542894750666" ID="ID_747586616" MODIFIED="1542895013690" TEXT="Call OTP generator with range Key value as 2">
<edge COLOR="#cc0000"/>
<node COLOR="#cc0000" CREATED="1542894788569" ID="ID_1045738594" MODIFIED="1542895013690" TEXT="Cehack Error message received &quot;Length of key should be in the range of 3-255&quot;">
<edge COLOR="#cc0000"/>
<node COLOR="#cc0000" CREATED="1542894818297" ID="ID_781310178" MODIFIED="1542895013690" TEXT="Error Code &quot;KER-OTM-OTV-002&quot;">
<edge COLOR="#cc0000"/>
</node>
</node>
</node>
<node COLOR="#cc0000" CREATED="1542894921082" ID="ID_959211050" MODIFIED="1542895013690" TEXT="CALL OTP generator with empty Key">
<edge COLOR="#cc0000"/>
<node COLOR="#cc0000" CREATED="1542894957701" ID="ID_1899262641" MODIFIED="1542895013690" TEXT="Error Message &quot;Key can&apos;t be empty or null&quot;">
<edge COLOR="#cc0000"/>
<node COLOR="#cc0000" CREATED="1542894975407" ID="ID_1890834773" MODIFIED="1542895013690" TEXT="Error Code &quot;KER-OTV-001&quot;">
<edge COLOR="#cc0000"/>
</node>
</node>
</node>
</node>
</node>
</node>
</map>
