<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1550385155223" ID="ID_1893015231" MODIFIED="1550386824108" TEXT="Registration - Translation">
<node CREATED="1550385183914" ID="ID_973835936" LINK="https://mosipid.atlassian.net/browse/MOS-1202" MODIFIED="1550385540286" POSITION="right" TEXT="MOS-1202">
<node COLOR="#66cc00" CREATED="1550385215254" ID="ID_1745525584" MODIFIED="1550385586056" TEXT="Configured language">
<edge COLOR="#66cc00"/>
<node COLOR="#66cc00" CREATED="1550385227780" HGAP="8" ID="ID_419643996" MODIFIED="1552037744534" TEXT="Different language" VSHIFT="-15">
<node COLOR="#66cc00" CREATED="1552037744534" ID="ID_1760577554" MODIFIED="1552037776659" TEXT="Primary as arabic">
<node COLOR="#66cc00" CREATED="1550385341904" ID="ID_1207227946" MODIFIED="1552037650407" TEXT="Display all static text including headers, labels, action buttons and alert messages in arabic"/>
</node>
<node COLOR="#66cc00" CREATED="1552037754357" ID="ID_1524280348" MODIFIED="1552037776659" TEXT="Secondary as french">
<node COLOR="#66cc00" CREATED="1550385341904" ID="ID_1117215381" MODIFIED="1552037808172" TEXT="Display all static text including headers, labels, action buttons and alert messages in french (Demographic, Preview and Acknowledgement screen alone)"/>
</node>
</node>
<node COLOR="#66cc00" CREATED="1550385449610" HGAP="8" ID="ID_269399462" MODIFIED="1550385624490" TEXT="Field not available for configuration" VSHIFT="24">
<node COLOR="#ff9900" CREATED="1550385624483" ID="ID_1300234327" MODIFIED="1550385697618" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1550385471482" ID="ID_1063187745" MODIFIED="1550385574024" TEXT="Display the corresponding display on the client will be empty."/>
</node>
<node COLOR="#66cc00" CREATED="1550385631945" ID="ID_31062438" MODIFIED="1550385680378" TEXT="No">
<linktarget COLOR="#b0b0b0" DESTINATION="ID_31062438" ENDARROW="Default" ENDINCLINATION="530;0;" ID="Arrow_ID_476644804" SOURCE="ID_619888658" STARTARROW="None" STARTINCLINATION="530;0;"/>
</node>
</node>
<node COLOR="#66cc00" CREATED="1550385488814" HGAP="12" ID="ID_803591941" MODIFIED="1552037823860" TEXT="Both in same language" VSHIFT="23">
<node COLOR="#66cc00" CREATED="1550385341904" ID="ID_619888658" MODIFIED="1550385655304" TEXT="Display all static text including headers, labels, action buttons and alert messages in language as per config">
<arrowlink DESTINATION="ID_31062438" ENDARROW="Default" ENDINCLINATION="530;0;" ID="Arrow_ID_476644804" STARTARROW="None" STARTINCLINATION="530;0;"/>
</node>
</node>
<node COLOR="#00cc00" CREATED="1538550071542" HGAP="9" ID="ID_1021783307" MODIFIED="1546498495098" TEXT="Verification of Txn details for Audit purpose" VSHIFT="34">
<edge COLOR="#00cc00"/>
<node COLOR="#00cc00" CREATED="1539003087723" ID="ID_163075645" MODIFIED="1542005044938" TEXT="System capture all Txn details">
<node COLOR="#00cc00" CREATED="1539003122483" ID="ID_1629456931" MODIFIED="1546498515692" TEXT="Store all the details under &quot;Audit_Log&quot; table such as User id or system account; Machine Details; Event Name; Application Name, and Event data including user entered fields."/>
</node>
<node COLOR="#ff0000" CREATED="1539060442854" ID="ID_1191661596" MODIFIED="1542005044938" TEXT="System fails to capture Txn details">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539089688847" ID="ID_795182154" MODIFIED="1542005044938" TEXT="Display appropriate error message / Raise a defect"/>
</node>
</node>
</node>
</node>
</node>
</map>
