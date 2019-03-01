<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1550400578449" ID="ID_296741896" MODIFIED="1550400639950" TEXT="Registration - Export to External Device">
<node CREATED="1550400657915" ID="ID_1862888220" LINK="https://mosipid.atlassian.net/browse/MOS-1305" MODIFIED="1550401667780" POSITION="right" TEXT="MOS-1305">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1550400684246" HGAP="15" ID="ID_1444840415" MODIFIED="1550401733446" TEXT="Verify whether user is able to export packets to external device" VSHIFT="-35">
<node COLOR="#33cc00" CREATED="1550400719898" ID="ID_1463738921" MODIFIED="1550401733446" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1550400727133" HGAP="14" ID="ID_455405768" MODIFIED="1550401733446" TEXT="Save the packets to the external device" VSHIFT="-18"/>
</node>
<node COLOR="#ff9900" CREATED="1550400722285" ID="ID_1862256995" MODIFIED="1550401748886" TEXT="No">
<node COLOR="#ff6600" CREATED="1550400885477" ID="ID_1887528700" MODIFIED="1550401757936" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1550400895537" ID="ID_516994435" MODIFIED="1550401733446" TEXT="Export packet">
<node COLOR="#33cc00" CREATED="1550400946966" HGAP="17" ID="ID_1954291426" MODIFIED="1550401733446" TEXT="EOD Status" VSHIFT="3">
<node COLOR="#33cc00" CREATED="1550400963667" ID="ID_258937590" MODIFIED="1550401733431" TEXT="On">
<node COLOR="#33cc00" CREATED="1550401007578" ID="ID_51303702" MODIFIED="1550401733431" TEXT="The packets which have been Approved or Rejected AND packet ID sync is completed are considered &#x2018;Ready to Upload&#x2019;."/>
</node>
<node COLOR="#33cc00" CREATED="1550400993868" ID="ID_937360885" MODIFIED="1550401733431" TEXT="Off">
<node COLOR="#33cc00" CREATED="1550401027070" ID="ID_736046239" MODIFIED="1550401733431" TEXT="The packets are considered &#x2018;Ready to Upload&#x2019; as soon as the registration is submitted AND packet ID sync is completed."/>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1550401061620" HGAP="12" ID="ID_1353922445" MODIFIED="1550401733431" TEXT="Verify whether user is able to export from external device" VSHIFT="45">
<node COLOR="#33cc00" CREATED="1550401120274" ID="ID_1701652788" MODIFIED="1550401733431" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1550401126558" ID="ID_1092085098" MODIFIED="1550401733431" TEXT="Export the packets to the server"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1550401182616" HGAP="12" ID="ID_845280531" MODIFIED="1550401733431" TEXT="Upload packets are acknowledged by server" VSHIFT="31">
<node COLOR="#33cc00" CREATED="1550401120274" ID="ID_41887661" MODIFIED="1550401733431" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1550401126558" ID="ID_1966054692" MODIFIED="1550401733431" TEXT="The status of packets in the client will be marked as uploaded">
<linktarget COLOR="#b0b0b0" DESTINATION="ID_1966054692" ENDARROW="Default" ENDINCLINATION="467;0;" ID="Arrow_ID_1451201166" SOURCE="ID_1433457718" STARTARROW="None" STARTINCLINATION="467;0;"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1550401265108" ID="ID_306930997" MODIFIED="1550401733431" TEXT="No">
<node COLOR="#33cc00" CREATED="1550401269749" ID="ID_281796885" MODIFIED="1550401733431" TEXT="The status of the packets will remain synced / Registered / Approved"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1550401317713" ID="ID_42564266" MODIFIED="1550401733431" TEXT="Can export be automated" VSHIFT="25">
<node COLOR="#33cc00" CREATED="1550401265108" ID="ID_1722051347" MODIFIED="1550401733431" TEXT="No">
<node COLOR="#33cc00" CREATED="1550401269749" ID="ID_408254855" MODIFIED="1550401733431" TEXT="It should be a manual process always"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1550401395108" HGAP="19" ID="ID_812090064" MODIFIED="1550401733431" TEXT="Packet status and export" VSHIFT="22">
<node COLOR="#33cc00" CREATED="1550401408057" HGAP="17" ID="ID_1531563979" MODIFIED="1550401733431" TEXT="Ready to upload" VSHIFT="-24">
<node COLOR="#33cc00" CREATED="1550401419378" ID="ID_249950538" MODIFIED="1550401733431" TEXT="Get exported in the next export"/>
</node>
<node COLOR="#33cc00" CREATED="1550401414950" HGAP="16" ID="ID_1436945938" MODIFIED="1550401733431" TEXT="Uploaded or any other state" VSHIFT="29">
<node COLOR="#33cc00" CREATED="1550401485890" ID="ID_491890710" MODIFIED="1550401733431" TEXT="Packets in &#x2018;Uploaded&#x2019; or any other status will not be exported again."/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1550401507686" HGAP="22" ID="ID_1510711894" MODIFIED="1550401733431" TEXT="Packet upload status" VSHIFT="46">
<node COLOR="#33cc00" CREATED="1550401531308" ID="ID_1433457718" MODIFIED="1550401733431" TEXT="Complete and success">
<arrowlink DESTINATION="ID_1966054692" ENDARROW="Default" ENDINCLINATION="467;0;" ID="Arrow_ID_1451201166" STARTARROW="None" STARTINCLINATION="467;0;"/>
</node>
<node COLOR="#33cc00" CREATED="1550401546232" ID="ID_1430660965" MODIFIED="1550401733431" TEXT="Partial or failure">
<node COLOR="#33cc00" CREATED="1550401579780" ID="ID_1902203614" MODIFIED="1550401733431" TEXT="Display &#x201c;Export could not be completed. Please try again.&#x201d;"/>
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
<node CREATED="1550474569125" ID="ID_1647345008" MODIFIED="1550474575860" POSITION="left" TEXT="EOD ON and OFF">
<node COLOR="#33cc00" CREATED="1550472761187" ID="ID_48275430" LINK="https://mosipid.atlassian.net/browse/MOS-12961" MODIFIED="1550474395652" TEXT="MOS-12961">
<node COLOR="#33cc00" CREATED="1550473209889" ID="ID_429379628" MODIFIED="1550474395652" TEXT="EOD Status">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1550473219302" HGAP="14" ID="ID_421088821" MODIFIED="1550474395651" TEXT="On" VSHIFT="-60">
<node COLOR="#33cc00" CREATED="1550473240677" HGAP="16" ID="ID_432166357" MODIFIED="1550474395651" TEXT="Online" VSHIFT="-37">
<node COLOR="#33cc00" CREATED="1550473283477" HGAP="9" ID="ID_19803071" MODIFIED="1550474395647" TEXT="Verify the packet status are marked as &quot;Ready to upload&quot; and auto uploaded to server after it is authenticated" VSHIFT="-16">
<node COLOR="#33cc00" CREATED="1550473372055" ID="ID_1034075705" MODIFIED="1550474395647" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1550473384273" ID="ID_1984940226" MODIFIED="1550474395647" TEXT="Check the same in DB and confirm the status are marked as &quot;Pushed&quot;"/>
</node>
<node COLOR="#ff6600" CREATED="1550473374271" ID="ID_466074594" MODIFIED="1550474483955" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1550473415321" ID="ID_1070014213" MODIFIED="1550474463697" TEXT="Raise a defect">
<edge COLOR="#ff0000"/>
</node>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1550473245112" HGAP="17" ID="ID_432944006" MODIFIED="1550474395646" TEXT="Offline" VSHIFT="40">
<node COLOR="#33cc00" CREATED="1550473433677" ID="ID_414535717" MODIFIED="1550474395646" TEXT="Verify the packets are in Registered status">
<node COLOR="#33cc00" CREATED="1550473372055" ID="ID_1308486820" MODIFIED="1550474395645" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1550473384273" ID="ID_1049875712" MODIFIED="1550474395645" TEXT="Await for the system to become online and then the status will get updated to &quot;Ready to upload&quot;">
<node COLOR="#33cc00" CREATED="1550473992829" ID="ID_171080803" MODIFIED="1550474395645" TEXT="Status updated">
<node COLOR="#33cc00" CREATED="1550474029864" ID="ID_1611645234" MODIFIED="1550474395644" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1550474039799" ID="ID_395508503" MODIFIED="1550474395637" TEXT="Ways to upload">
<node COLOR="#33cc00" CREATED="1550474051505" ID="ID_1121124066" MODIFIED="1550474395637" TEXT="Through external by exporting the packet to external device"/>
<node COLOR="#33cc00" CREATED="1550474072377" ID="ID_915872948" MODIFIED="1550474395637" TEXT="Through UI by manually initiating the upload service"/>
</node>
</node>
<node COLOR="#ff6600" CREATED="1550474031751" ID="ID_658915712" MODIFIED="1550474483955" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1550473415321" ID="ID_73286151" MODIFIED="1550474463696" TEXT="Raise a defect">
<edge COLOR="#ff0000"/>
</node>
</node>
</node>
</node>
</node>
<node COLOR="#ff6600" CREATED="1550473374271" ID="ID_1447860646" MODIFIED="1550474483955" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1550473415321" ID="ID_1063840889" MODIFIED="1550474463696" TEXT="Raise a defect">
<edge COLOR="#ff0000"/>
</node>
</node>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1550473223260" HGAP="13" ID="ID_1908327361" MODIFIED="1550474395635" TEXT="Off" VSHIFT="21">
<node COLOR="#33cc00" CREATED="1550473240677" HGAP="16" ID="ID_1421574069" MODIFIED="1550474395635" TEXT="Online" VSHIFT="-37">
<node COLOR="#33cc00" CREATED="1550473283477" HGAP="9" ID="ID_940430701" MODIFIED="1550474395634" TEXT="Verify the packet status are marked as &quot;Ready to upload&quot; and auto uploaded to server without authentication" VSHIFT="-16">
<node COLOR="#33cc00" CREATED="1550473372055" ID="ID_1680744104" MODIFIED="1550474395634" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1550473384273" ID="ID_1483656122" MODIFIED="1550474395634" TEXT="Check the same in DB and confirm the status are marked as &quot;Pushed&quot;"/>
</node>
<node COLOR="#ff6600" CREATED="1550473374271" ID="ID_1834598747" MODIFIED="1550474483955" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1550473415321" ID="ID_123770250" MODIFIED="1550474463696" TEXT="Raise a defect">
<edge COLOR="#ff0000"/>
</node>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1550473245112" HGAP="17" ID="ID_1983048398" MODIFIED="1550474395633" TEXT="Offline" VSHIFT="40">
<node COLOR="#33cc00" CREATED="1550473433677" ID="ID_1117128841" MODIFIED="1550474395632" TEXT="Verify the packets are in synced status ">
<node COLOR="#33cc00" CREATED="1550473372055" ID="ID_717510351" MODIFIED="1550474395632" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1550473384273" ID="ID_1131759278" MODIFIED="1550474395632" TEXT="Await for the system to become online and push the packets to server">
<node COLOR="#33cc00" CREATED="1550474039799" ID="ID_510191289" MODIFIED="1550474395631" TEXT="Ways to upload">
<node COLOR="#33cc00" CREATED="1550474051505" ID="ID_1865588902" MODIFIED="1550474395631" TEXT="Through external by exporting the packet to external device"/>
<node COLOR="#33cc00" CREATED="1550474072377" ID="ID_1201596206" MODIFIED="1550474395631" TEXT="Through UI by manually initiating the upload service"/>
</node>
</node>
</node>
<node COLOR="#ff6600" CREATED="1550473374271" ID="ID_74587860" MODIFIED="1550474483955" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1550473415321" ID="ID_426426951" MODIFIED="1550474463696" TEXT="Raise a defect">
<edge COLOR="#ff0000"/>
</node>
</node>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1538550071542" HGAP="9" ID="ID_1314480294" MODIFIED="1550474395630" TEXT="Verification of Txn details for Audit purpose" VSHIFT="34">
<edge COLOR="#00cc00"/>
<node COLOR="#33cc00" CREATED="1539003087723" ID="ID_815859203" MODIFIED="1550474395629" TEXT="System capture all Txn details">
<node COLOR="#33cc00" CREATED="1539003122483" ID="ID_54060583" MODIFIED="1550474395625" TEXT="Store all the details under &quot;Audit_Log&quot; table such as User id or system account; Machine Details; Event Name; Application Name, and Event data including user entered fields."/>
</node>
<node COLOR="#ff0000" CREATED="1539060442854" ID="ID_245417979" MODIFIED="1550474463694" TEXT="System fails to capture Txn details">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539089688847" ID="ID_873106332" MODIFIED="1550474463695" TEXT="Display appropriate error message / Raise a defect"/>
</node>
</node>
</node>
</node>
</node>
</node>
</map>
