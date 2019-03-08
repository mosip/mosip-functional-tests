<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1550380402397" ID="ID_177412883" MODIFIED="1550386862906" TEXT="Registration - New Registration">
<node COLOR="#33cc00" CREATED="1550380446973" ID="ID_1534355009" LINK="https://mosipid.atlassian.net/browse/MOS-1204" MODIFIED="1550381075947" POSITION="right" TEXT="MOS-1204">
<node COLOR="#33cc00" CREATED="1550380507489" ID="ID_816727000" MODIFIED="1550381122124" TEXT="Enter PRID and click Fetch Data">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1550380698614" HGAP="11" ID="ID_1846198890" MODIFIED="1550381122123" TEXT="Machine online" VSHIFT="-73">
<node COLOR="#33cc00" CREATED="1550380742581" HGAP="21" ID="ID_229908141" MODIFIED="1550381386058" TEXT="Available in server" VSHIFT="-31">
<node COLOR="#33cc00" CREATED="1550380785719" ID="ID_1624471319" MODIFIED="1550381122122" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1550380805629" ID="ID_392744348" MODIFIED="1550381122122" TEXT="Download the Pre-registration details and display in UI"/>
</node>
<node COLOR="#66cc00" CREATED="1550380799855" ID="ID_611598873" MODIFIED="1552037404087" TEXT="No">
<node COLOR="#66cc00" CREATED="1550380838851" ID="ID_688796713" MODIFIED="1552037404087" TEXT="Check whether it is available in DB">
<arrowlink DESTINATION="ID_281963655" ENDARROW="Default" ENDINCLINATION="290;0;" ID="Arrow_ID_967221988" STARTARROW="None" STARTINCLINATION="305;0;"/>
<arrowlink DESTINATION="ID_1423286618" ENDARROW="Default" ENDINCLINATION="290;0;" ID="Arrow_ID_1859952251" STARTARROW="None" STARTINCLINATION="305;0;"/>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1550380758506" HGAP="29" ID="ID_281963655" MODIFIED="1550381122122" TEXT="Available in DB" VSHIFT="65">
<linktarget COLOR="#b0b0b0" DESTINATION="ID_281963655" ENDARROW="Default" ENDINCLINATION="290;0;" ID="Arrow_ID_967221988" SOURCE="ID_688796713" STARTARROW="None" STARTINCLINATION="305;0;"/>
<node COLOR="#33cc00" CREATED="1550380785719" HGAP="8" ID="ID_934331195" MODIFIED="1550381122121" TEXT="Yes" VSHIFT="-28">
<node COLOR="#33cc00" CREATED="1550380805629" ID="ID_133001650" MODIFIED="1550381122121" TEXT="Download the Pre-registration details and display in UI"/>
</node>
<node COLOR="#ff6600" CREATED="1550380944994" HGAP="17" ID="ID_758586558" MODIFIED="1550381138987" TEXT="No" VSHIFT="31">
<node COLOR="#ff0000" CREATED="1550380967514" ID="ID_708518196" MODIFIED="1550381360829" TEXT="Display &quot;The Pre-registration ID entered does not exist in the database. Please enter the correct Pre-registration ID.&#x201d; error message"/>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1550380712029" HGAP="17" ID="ID_1241061990" MODIFIED="1550381122120" TEXT="Machine Offline" VSHIFT="25">
<node COLOR="#33cc00" CREATED="1550380758506" HGAP="29" ID="ID_1423286618" MODIFIED="1550381122120" TEXT="Available in DB" VSHIFT="65">
<linktarget COLOR="#b0b0b0" DESTINATION="ID_1423286618" ENDARROW="Default" ENDINCLINATION="290;0;" ID="Arrow_ID_1859952251" SOURCE="ID_688796713" STARTARROW="None" STARTINCLINATION="305;0;"/>
<node COLOR="#33cc00" CREATED="1550380785719" HGAP="8" ID="ID_968888519" MODIFIED="1550381122120" TEXT="Yes" VSHIFT="-28">
<node COLOR="#33cc00" CREATED="1550380805629" ID="ID_849386819" MODIFIED="1550381122119" TEXT="Download the Pre-registration details and display in UI"/>
</node>
<node COLOR="#ff6600" CREATED="1550380944994" HGAP="17" ID="ID_1629468112" MODIFIED="1550381138986" TEXT="No" VSHIFT="31">
<node COLOR="#ff0000" CREATED="1550380967514" ID="ID_1618245594" MODIFIED="1550381360825" TEXT="Display &quot;The Pre-registration ID entered does not exist in the database. Please enter the correct Pre-registration ID.&#x201d; error message"/>
</node>
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
