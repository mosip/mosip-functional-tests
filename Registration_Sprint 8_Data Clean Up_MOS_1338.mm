<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1550401817437" ID="ID_1867531552" MODIFIED="1550401854766" TEXT="Registration - Audit log deletion">
<node CREATED="1550385909571" ID="ID_1946649974" LINK="https://mosipid.atlassian.net/browse/MOS-1338" MODIFIED="1550401906370" POSITION="right" TEXT="MOS-1338">
<edge COLOR="#66cc00"/>
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
<node COLOR="#00cc00" CREATED="1546511644906" HGAP="4" ID="ID_805431123" MODIFIED="1550400105620" TEXT="Verify RC allows the user to send client sync data request" VSHIFT="-27">
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
<node COLOR="#33cc00" CREATED="1550401967967" HGAP="14" ID="ID_928836013" MODIFIED="1550402165432" TEXT="Verify user is able to configure the time to delete the logs" VSHIFT="-6">
<node COLOR="#33cc00" CREATED="1550401990312" ID="ID_1135680628" MODIFIED="1550402165432" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1550401995848" ID="ID_38140186" MODIFIED="1550402165432" TEXT="Configure the time to delete everyday at 8 AM"/>
</node>
<node COLOR="#ff9900" CREATED="1550401991690" ID="ID_1201089098" MODIFIED="1550402181436" TEXT="No">
<node COLOR="#ff6600" CREATED="1550402013544" ID="ID_381104796" MODIFIED="1550402191938" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1550402025011" ID="ID_463265310" MODIFIED="1550402165432" TEXT="Verify the audit logs are deleted as per config when sync data is run and complete" VSHIFT="41">
<node COLOR="#33cc00" CREATED="1550401990312" ID="ID_1846680202" MODIFIED="1550402165432" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1550401995848" ID="ID_1063630950" MODIFIED="1550402165432" TEXT="Verify the same in app_audit_log, audit_log_control, registration_transaction, registration tables"/>
</node>
<node COLOR="#ff9900" CREATED="1550401991690" ID="ID_1461757399" MODIFIED="1550402181436" TEXT="No">
<node COLOR="#ff6600" CREATED="1550402013544" ID="ID_763044411" MODIFIED="1550402191938" TEXT="Raise a defect"/>
</node>
</node>
</node>
</node>
</map>
