<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1540687499635" ID="ID_443564635" MODIFIED="1540687578020" TEXT="Insert Encrypted Data into DB">
<node CREATED="1540687581758" ID="ID_1049600940" MODIFIED="1540687586962" POSITION="right" TEXT="MOS-170">
<node CREATED="1540687611604" ID="ID_755131189" MODIFIED="1540687613579" TEXT="MOSIP System receives a request to insert Packet data into the database ">
<node CREATED="1540687617068" ID="ID_837707363" MODIFIED="1540687631170" TEXT="Check successful structural validation">
<node COLOR="#009900" CREATED="1540687672247" ID="ID_1699244125" MODIFIED="1540688333229" TEXT="yes">
<node CREATED="1540687872719" ID="ID_692325818" MODIFIED="1540687874823" TEXT="extracts the Demo data, Audit Data, OSI data, and basic Biometric Data (excluding Biometric Images)">
<node CREATED="1540687902133" ID="ID_808603541" MODIFIED="1540687904076" TEXT="encrypts the data and stores in respective databases">
<node COLOR="#009900" CREATED="1540687916647" ID="ID_1162553400" MODIFIED="1540688389702" TEXT="yes">
<node CREATED="1540687934074" ID="ID_1874695055" MODIFIED="1540688004235" TEXT="Update the Registration table with Status &quot;Packet Decryption Successful&quot;.">
<node CREATED="1540688023889" ID="ID_913609420" MODIFIED="1540688049947" TEXT="Insert Encrypted data to Database">
<node COLOR="#009900" CREATED="1540688051732" ID="ID_518616002" MODIFIED="1540688403457" TEXT="yes">
<node CREATED="1540688229341" ID="ID_864077420" MODIFIED="1540688252743" TEXT="Encrypt data Inserted into database successfully"/>
</node>
<node COLOR="#cc0000" CREATED="1540688067890" ID="ID_157342065" MODIFIED="1540688415601" TEXT="no">
<node CREATED="1540688076654" ID="ID_364730698" MODIFIED="1540688080712" TEXT="Unable to Insert Data">
<node CREATED="1540688092708" ID="ID_1042399509" MODIFIED="1540688101641" TEXT="Error Message &quot;&#x201c;The Data is not getting inserted in the DB for Registration ID &quot;"/>
<node CREATED="1540688108916" ID="ID_452285364" MODIFIED="1540688125545" TEXT="Error Code &quot;RPR-PDE-002&quot;"/>
</node>
</node>
<node COLOR="#cc0000" CREATED="1540688145762" ID="ID_1295134315" MODIFIED="1540688423270" TEXT="Database connection Lost ">
<node CREATED="1540688154106" ID="ID_186091099" MODIFIED="1540688162761" TEXT="Error Message &quot;&#x201c;The Database Connection is Lost&#x201d;&quot;"/>
<node CREATED="1540688166842" ID="ID_1968449685" MODIFIED="1540688180402" TEXT="Error Code &quot;RPR-PSV-003&quot;"/>
</node>
</node>
</node>
</node>
<node COLOR="#cc0000" CREATED="1540687937441" ID="ID_1541847407" MODIFIED="1540688433662" TEXT="no">
<node CREATED="1540687941792" ID="ID_1532689621" MODIFIED="1540687995402" TEXT="Update the Registration table with Status &quot;Packet Decryption Failure&quot;  Retry"/>
</node>
</node>
</node>
</node>
<node COLOR="#cc0000" CREATED="1540685116739" ID="ID_159896628" MODIFIED="1540688443169" TEXT="no">
<node CREATED="1540685144033" ID="ID_500070688" MODIFIED="1540685178122" TEXT="Error message &quot;&#x201c;The File was not found in DFS&#x201d;"/>
<node CREATED="1540685158658" ID="ID_1296606645" MODIFIED="1540685189370" TEXT="Error code &quot;RPR-PSV-001&quot;"/>
<node CREATED="1540685325675" ID="ID_1589431477" MODIFIED="1540685340732" TEXT="unsuccessful comparison">
<node CREATED="1540686679131" ID="ID_952935760" MODIFIED="1540686682019" TEXT=" Registration table with File Validation Failed and Mark for Retry"/>
</node>
</node>
</node>
</node>
</node>
</node>
</map>
