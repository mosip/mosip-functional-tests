<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1553096316477" ID="ID_1433435395" MODIFIED="1553096324696" TEXT="Registration - Virus Scan">
<node CREATED="1553096337023" ID="ID_1143120503" LINK="https://mosipid.atlassian.net/browse/MOS-13526" MODIFIED="1553096650835" POSITION="right" TEXT="MOS-13526">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1553096363244" HGAP="12" ID="ID_1556157525" MODIFIED="1553096728476" TEXT="Virus scan as a config parameter" VSHIFT="-33">
<node COLOR="#33cc00" CREATED="1553096379812" ID="ID_771834079" MODIFIED="1553096728476" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1553096391680" ID="ID_302320371" MODIFIED="1553096728476" TEXT="Set the cron expression to make the sync run automatically"/>
</node>
<node COLOR="#ff6600" CREATED="1553096381511" ID="ID_735229347" MODIFIED="1553096689195" TEXT="No">
<edge COLOR="#ff3300"/>
<node COLOR="#ff0000" CREATED="1553096383799" ID="ID_1906208076" MODIFIED="1553096708008" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1553096537955" ID="ID_1267397850" MODIFIED="1553096728476" TEXT="Verify whether the virus scan, scans the pre-reg, reg and transaction logs">
<node COLOR="#33cc00" CREATED="1553096581810" ID="ID_29444435" MODIFIED="1553096728460" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1553096591978" ID="ID_1950085176" MODIFIED="1553096728460" TEXT="if virus detected, it will alert the user with &#x201c;Security scan detected viruses in the following files [List of files]. Please take necessary action or contact the administrator&#x201d;."/>
</node>
<node COLOR="#ff6600" CREATED="1553096583350" ID="ID_793480852" MODIFIED="1553096689195" TEXT="No">
<edge COLOR="#ff3300"/>
<node COLOR="#ff0000" CREATED="1553096383799" ID="ID_245165558" MODIFIED="1553096708008" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#00cc00" CREATED="1538550071542" HGAP="9" ID="ID_194407712" MODIFIED="1546498495098" TEXT="Verification of Txn details for Audit purpose" VSHIFT="34">
<edge COLOR="#00cc00"/>
<node COLOR="#00cc00" CREATED="1539003087723" ID="ID_270034758" MODIFIED="1542005044938" TEXT="System capture all Txn details">
<node COLOR="#00cc00" CREATED="1539003122483" ID="ID_419781212" MODIFIED="1546498515692" TEXT="Store all the details under &quot;Audit_Log&quot; table such as User id or system account; Machine Details; Event Name; Application Name, and Event data including user entered fields."/>
</node>
<node COLOR="#ff6600" CREATED="1539060442854" ID="ID_1881009717" MODIFIED="1553096689211" TEXT="System fails to capture Txn details">
<edge COLOR="#ff3300"/>
<node COLOR="#ff0000" CREATED="1539089688847" ID="ID_103227748" MODIFIED="1542005044938" TEXT="Display appropriate error message / Raise a defect"/>
</node>
</node>
<node COLOR="#ff6600" CREATED="1546512559848" ID="ID_958917305" MODIFIED="1551346340905" TEXT="Sync failure">
<node COLOR="#ff6600" CREATED="1546512693165" ID="ID_251228526" MODIFIED="1553096679554" TEXT="Yes">
<edge COLOR="#ff3300"/>
<node COLOR="#ff0000" CREATED="1546512661355" ID="ID_871721761" MODIFIED="1550386418094" TEXT="Display &quot;Sync Failure&quot; error message and Detailed errors can be viewed in the transaction logs."/>
</node>
<node COLOR="#66cc00" CREATED="1550386433092" ID="ID_1658628535" MODIFIED="1550386470892" TEXT="No">
<node COLOR="#66cc00" CREATED="1550386442362" ID="ID_689324428" MODIFIED="1550386470891" TEXT="Display &quot;Sync success&quot;"/>
</node>
</node>
<node COLOR="#00cc00" CREATED="1546512739929" HGAP="9" ID="ID_1515494922" MODIFIED="1546513948220" TEXT="End user able to perform other action when sync is running" VSHIFT="26">
<node COLOR="#ff6600" CREATED="1546512754387" ID="ID_720860815" MODIFIED="1553096679554" TEXT="Yes">
<edge COLOR="#ff3300"/>
<node COLOR="#ff0000" CREATED="1546512772169" ID="ID_45526298" MODIFIED="1546513988703" TEXT="Display error message / Raise a defect"/>
</node>
<node COLOR="#00cc00" CREATED="1546512808301" ID="ID_155994106" MODIFIED="1546513948220" TEXT="No">
<node COLOR="#00cc00" CREATED="1546512827907" ID="ID_1159198216" MODIFIED="1546517537946" TEXT="Verify whether the UI displays the status of the sync"/>
</node>
</node>
<node COLOR="#00cc00" CREATED="1546513016723" HGAP="14" ID="ID_1832477793" MODIFIED="1546513948220" TEXT="Behavior of scheduled sync" VSHIFT="34">
<node COLOR="#00cc00" CREATED="1546513047164" HGAP="17" ID="ID_181515142" MODIFIED="1546513948220" TEXT="Online" VSHIFT="-20">
<node COLOR="#00cc00" CREATED="1546511845038" ID="ID_1620851675" MODIFIED="1546517537946" TEXT="Sync request should be sent as scheduled to server for master data sync"/>
</node>
<node COLOR="#ff6600" CREATED="1546513054638" HGAP="18" ID="ID_448364924" MODIFIED="1553096679554" TEXT="Offline" VSHIFT="29">
<edge COLOR="#ff3300"/>
<node COLOR="#ff0000" CREATED="1546513391885" ID="ID_265186527" MODIFIED="1546513988701" TEXT="The sync should be queued up and executed later. When the client is next launched and is online, check if the previous scheduled sync was executed. If not executed earlier, immediately start the sync."/>
</node>
</node>
</node>
</node>
</map>
