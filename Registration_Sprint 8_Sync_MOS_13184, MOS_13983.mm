<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1550482288447" ID="ID_1635280759" MODIFIED="1550482308030" TEXT="Registration - Audit Log">
<node CREATED="1550482412928" ID="ID_409528508" LINK="https://mosipid.atlassian.net/browse/MOS-13184" MODIFIED="1550482660890" POSITION="right" TEXT="MOS-13184">
<edge COLOR="#66cc00"/>
<node COLOR="#66cc00" CREATED="1550482450475" HGAP="18" ID="ID_1124866718" MODIFIED="1550482683442" TEXT="For first registration" VSHIFT="-62">
<node COLOR="#66cc00" CREATED="1550482505238" ID="ID_308101937" MODIFIED="1550482683443" TEXT="Verify all the log information that is available in app_audit_log">
<node COLOR="#66cc00" CREATED="1550482545133" ID="ID_652326681" MODIFIED="1550482683444" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1550482567494" ID="ID_1310093012" MODIFIED="1550482683440" TEXT="Proceed to next packet creation"/>
</node>
<node COLOR="#ff9900" CREATED="1550482547398" ID="ID_1869292326" MODIFIED="1550482693288" TEXT="No">
<node COLOR="#ff3300" CREATED="1550482549578" ID="ID_1221412875" MODIFIED="1550482705289" TEXT="Raise a defect"/>
</node>
</node>
</node>
<node COLOR="#66cc00" CREATED="1550482461514" HGAP="18" ID="ID_363065233" MODIFIED="1550482683442" TEXT="For subsequent registration" VSHIFT="17">
<node COLOR="#66cc00" CREATED="1550482505238" ID="ID_1694503041" MODIFIED="1550482683444" TEXT="Verify all the log information that is available in app_audit_log where timestamp &gt; first app log created time">
<node COLOR="#66cc00" CREATED="1550482545133" ID="ID_665841178" MODIFIED="1550482683445" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1550482567494" ID="ID_483664096" MODIFIED="1550482683445" TEXT="Proceed to next packet creation"/>
</node>
<node COLOR="#ff9900" CREATED="1550482547398" ID="ID_1924924470" MODIFIED="1550482693288" TEXT="No">
<node COLOR="#ff3300" CREATED="1550482549578" ID="ID_541286924" MODIFIED="1550482705288" TEXT="Raise a defect"/>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1538550071542" HGAP="9" ID="ID_1021783307" MODIFIED="1550474395630" TEXT="Verification of Txn details for Audit purpose" VSHIFT="34">
<edge COLOR="#00cc00"/>
<node COLOR="#33cc00" CREATED="1539003087723" ID="ID_163075645" MODIFIED="1550474395629" TEXT="System capture all Txn details">
<node COLOR="#33cc00" CREATED="1539003122483" ID="ID_1629456931" MODIFIED="1550474395625" TEXT="Store all the details under &quot;Audit_Log&quot; table such as User id or system account; Machine Details; Event Name; Application Name, and Event data including user entered fields."/>
</node>
<node COLOR="#ff0000" CREATED="1539060442854" ID="ID_1191661596" MODIFIED="1550474463694" TEXT="System fails to capture Txn details">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539089688847" ID="ID_795182154" MODIFIED="1550474463695" TEXT="Display appropriate error message / Raise a defect"/>
</node>
</node>
</node>
<node CREATED="1551337451626" ID="ID_325281251" LINK="https://mosipid.atlassian.net/browse/MOS-13983" MODIFIED="1551337471482" POSITION="left" TEXT="MOS-13983">
<node COLOR="#00cc00" CREATED="1546511989761" HGAP="11" ID="ID_780637345" MODIFIED="1551346299414" TEXT="Verify the packet status after packet sync service is run" VSHIFT="-45">
<node COLOR="#00cc00" CREATED="1546512388671" ID="ID_1017304903" MODIFIED="1551346313665" TEXT="Completed successfully">
<node COLOR="#00cc00" CREATED="1546512423427" ID="ID_1298271934" MODIFIED="1551346379397" TEXT="Change the packet status to Synced which were in Approved, Rejected, Re-Register approved packets"/>
</node>
<node COLOR="#ff6600" CREATED="1546512404218" ID="ID_413250447" MODIFIED="1551346340905" TEXT="Not completed">
<arrowlink DESTINATION="ID_958917305" ENDARROW="Default" ENDINCLINATION="345;0;" ID="Arrow_ID_1589371058" STARTARROW="None" STARTINCLINATION="345;0;"/>
</node>
</node>
<node COLOR="#ff6600" CREATED="1546512559848" ID="ID_958917305" MODIFIED="1551346340905" TEXT="Sync failure">
<linktarget COLOR="#b0b0b0" DESTINATION="ID_958917305" ENDARROW="Default" ENDINCLINATION="345;0;" ID="Arrow_ID_1589371058" SOURCE="ID_413250447" STARTARROW="None" STARTINCLINATION="345;0;"/>
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
<node COLOR="#ff6600" CREATED="1546513054638" HGAP="18" ID="ID_448364924" MODIFIED="1546513968029" TEXT="Offline" VSHIFT="29">
<node COLOR="#ff0000" CREATED="1546513391885" ID="ID_265186527" MODIFIED="1546513988701" TEXT="The sync should be queued up and executed later. When the client is next launched and is online, check if the previous scheduled sync was executed. If not executed earlier, immediately start the sync."/>
</node>
</node>
<node COLOR="#00cc00" CREATED="1538550071542" HGAP="9" ID="ID_194407712" MODIFIED="1546498495098" TEXT="Verification of Txn details for Audit purpose" VSHIFT="34">
<edge COLOR="#00cc00"/>
<node COLOR="#00cc00" CREATED="1539003087723" ID="ID_270034758" MODIFIED="1542005044938" TEXT="System capture all Txn details">
<node COLOR="#00cc00" CREATED="1539003122483" ID="ID_419781212" MODIFIED="1546498515692" TEXT="Store all the details under &quot;Audit_Log&quot; table such as User id or system account; Machine Details; Event Name; Application Name, and Event data including user entered fields."/>
</node>
<node COLOR="#ff0000" CREATED="1539060442854" ID="ID_1881009717" MODIFIED="1542005044938" TEXT="System fails to capture Txn details">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539089688847" ID="ID_103227748" MODIFIED="1542005044938" TEXT="Display appropriate error message / Raise a defect"/>
</node>
</node>
</node>
</node>
</map>
