<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node COLOR="#33cc00" CREATED="1550472651882" ID="ID_191085608" MODIFIED="1550474395654" TEXT="Registration - EOD ON and OFF">
<node COLOR="#33cc00" CREATED="1550472761187" ID="ID_48275430" LINK="https://mosipid.atlassian.net/browse/MOS-12961" MODIFIED="1550474395652" POSITION="right" TEXT="MOS-12961">
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
</node>
</node>
</map>
