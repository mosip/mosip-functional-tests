<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1550385856818" ID="ID_1096938559" MODIFIED="1550385904152" TEXT="Registration-Sync Config Details">
<node CREATED="1550385909571" ID="ID_1946649974" LINK="https://mosipid.atlassian.net/browse/MOS-1227" MODIFIED="1550386573563" POSITION="right" TEXT="MOS-1227">
<edge COLOR="#66cc00"/>
<node COLOR="#00cc00" CREATED="1546511989761" HGAP="11" ID="ID_780637345" MODIFIED="1546513948233" TEXT="Verify whether RC receives a response for the request sent" VSHIFT="-45">
<node COLOR="#00cc00" CREATED="1546512388671" ID="ID_1017304903" MODIFIED="1546513948233" TEXT="Yes">
<node COLOR="#00cc00" CREATED="1546512423427" ID="ID_1298271934" MODIFIED="1550386385941" TEXT="Only the incremental changes (additions, deletions and modifications) made since the last sync should be sent and save the data in the local machine overwriting previous values of the config settings received."/>
</node>
<node COLOR="#ff6600" CREATED="1546512404218" ID="ID_413250447" MODIFIED="1546513968029" TEXT="No">
<node COLOR="#ff0000" CREATED="1546511797367" HGAP="40" ID="ID_256788422" MODIFIED="1550386244350" TEXT="Display appropriate error message" VSHIFT="3"/>
</node>
</node>
<node COLOR="#ff6600" CREATED="1546512559848" ID="ID_958917305" MODIFIED="1546513968029" TEXT="Sync failure">
<node COLOR="#ff6600" CREATED="1546512693165" ID="ID_251228526" MODIFIED="1546513968029" TEXT="Yes">
<node COLOR="#ff0000" CREATED="1546512661355" ID="ID_871721761" MODIFIED="1550386418094" TEXT="Display &quot;Sync Failure&quot; error message and Detailed errors can be viewed in the transaction logs."/>
</node>
<node COLOR="#66cc00" CREATED="1550386433092" ID="ID_1658628535" MODIFIED="1550386470892" TEXT="No">
<node COLOR="#66cc00" CREATED="1550386442362" ID="ID_689324428" MODIFIED="1550386470891" TEXT="Display &quot;Sync success&quot;"/>
</node>
</node>
<node COLOR="#00cc00" CREATED="1546512739929" HGAP="9" ID="ID_1515494922" MODIFIED="1546513948220" TEXT="End user able to perform other action when sync is running" VSHIFT="26">
<node COLOR="#ff6600" CREATED="1546512754387" ID="ID_720860815" MODIFIED="1546513968029" TEXT="Yes">
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
<node COLOR="#66cc00" CREATED="1546513054638" HGAP="18" ID="ID_448364924" MODIFIED="1552037498845" TEXT="Offline" VSHIFT="29">
<node COLOR="#66cc00" CREATED="1546513391885" ID="ID_265186527" MODIFIED="1552037498845" TEXT="The sync should be queued up and executed later. When the client is next launched and is online, check if the previous scheduled sync was executed. If not executed earlier, immediately start the sync."/>
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
<node COLOR="#00cc00" CREATED="1546511644906" HGAP="4" ID="ID_805431123" MODIFIED="1546513948236" TEXT="Verify RC allows the user to send sync data request" VSHIFT="-27">
<node COLOR="#00cc00" CREATED="1546511670537" HGAP="17" ID="ID_1411179247" MODIFIED="1546513948236" TEXT="Scheduled" VSHIFT="-27">
<node COLOR="#00cc00" CREATED="1546511678659" HGAP="18" ID="ID_185048801" MODIFIED="1546513948235" TEXT="Yes" VSHIFT="-24">
<node COLOR="#00cc00" CREATED="1546511845038" ID="ID_696253761" MODIFIED="1550386546214" TEXT="Sync request should be sent as scheduled to server for config details with data store servers"/>
</node>
<node COLOR="#00cc00" CREATED="1546511705926" ID="ID_1455155737" MODIFIED="1546513948235" TEXT="No">
<node COLOR="#00cc00" CREATED="1546511711495" ID="ID_184302647" MODIFIED="1546513948235" TEXT="Check the client is up and running">
<node COLOR="#00cc00" CREATED="1546511678659" ID="ID_1718376406" MODIFIED="1546513948235" TEXT="Yes">
<node COLOR="#00cc00" CREATED="1546511797367" ID="ID_94651022" MODIFIED="1546513948234" TEXT="Display &#x201c;You must be connected to the internet to sync data.&#x201d;"/>
</node>
<node COLOR="#ff6600" CREATED="1546511705926" ID="ID_1464300702" MODIFIED="1546513968028" TEXT="No">
<node COLOR="#ff0000" CREATED="1546511771301" ID="ID_1387343892" MODIFIED="1546513988704" TEXT="Launch the application and connect to internet"/>
</node>
</node>
</node>
</node>
<node COLOR="#00cc00" CREATED="1546511683198" HGAP="24" ID="ID_428683583" MODIFIED="1546513948234" TEXT="Manual" VSHIFT="35">
<node COLOR="#00cc00" CREATED="1546511678659" HGAP="21" ID="ID_1951445205" MODIFIED="1546513948234" TEXT="Yes" VSHIFT="-21">
<node COLOR="#00cc00" CREATED="1546511928883" ID="ID_1145651150" MODIFIED="1550386554056" TEXT="Sync request should be sent to server for config details with data store servers"/>
</node>
<node COLOR="#00cc00" CREATED="1546511705926" ID="ID_1092767317" MODIFIED="1546513948236" TEXT="No">
<node COLOR="#00cc00" CREATED="1546511711495" ID="ID_1433242561" MODIFIED="1546513948234" TEXT="Check the client is up and running and online">
<node COLOR="#00cc00" CREATED="1546511678659" ID="ID_1062897209" MODIFIED="1546513948233" TEXT="Yes">
<node COLOR="#00cc00" CREATED="1546511797367" ID="ID_1050932167" MODIFIED="1546513948233" TEXT="Display &#x201c;You must be connected to the internet to sync data.&#x201d;"/>
</node>
<node COLOR="#ff6600" CREATED="1546511705926" ID="ID_1916005897" MODIFIED="1546513968028" TEXT="No">
<node COLOR="#ff0000" CREATED="1546511771301" ID="ID_823807204" MODIFIED="1546513988704" TEXT="Launch the application and connect to internet"/>
</node>
</node>
</node>
</node>
</node>
</node>
</node>
</map>
