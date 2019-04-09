<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1553603964954" ID="ID_1325314456" MODIFIED="1553603980041" TEXT="Registration - Upload specific packets">
<node CREATED="1553603987936" ID="ID_1085403860" LINK="https://mosipid.atlassian.net/browse/MOS-13561" MODIFIED="1553604890425" POSITION="right" TEXT="MOS-13561">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1553604004389" HGAP="15" ID="ID_1239627266" MODIFIED="1553604964660" TEXT="Verify the upload packet screen displays packets that are only in synced status" VSHIFT="-19">
<node COLOR="#33cc00" CREATED="1553604023013" ID="ID_947922569" MODIFIED="1553604964659" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1553604031849" ID="ID_1561364670" MODIFIED="1553604964659" TEXT="Proceed with upload packet activity"/>
</node>
<node COLOR="#ff3300" CREATED="1553604024075" ID="ID_1537073231" MODIFIED="1553604927438" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff3300" CREATED="1553604027234" ID="ID_705554233" MODIFIED="1553604981568" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1553604061948" ID="ID_931038734" MODIFIED="1553604964658" TEXT="Verify whether upload screen has checkboxes for each packet">
<node COLOR="#33cc00" CREATED="1553604102144" ID="ID_1586332888" MODIFIED="1553604964658" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1553604120076" ID="ID_956724648" MODIFIED="1553604964658" TEXT="Using the checkbox user can select the packet which needs to be uploaded as per the requirement">
<linktarget COLOR="#b0b0b0" DESTINATION="ID_956724648" ENDARROW="Default" ENDINCLINATION="498;0;" ID="Arrow_ID_1033993672" SOURCE="ID_516601542" STARTARROW="None" STARTINCLINATION="498;0;"/>
</node>
</node>
<node COLOR="#ff3300" CREATED="1553604108075" ID="ID_399414973" MODIFIED="1553604927437" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff3300" CREATED="1553604109632" ID="ID_1571467572" MODIFIED="1553604981569" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1553604157876" ID="ID_213243964" MODIFIED="1553604964657" TEXT="Verify whether user has an option to select all packets / deselect all packets">
<node COLOR="#33cc00" CREATED="1553604182012" ID="ID_516601542" MODIFIED="1553604964657" TEXT="Yes">
<arrowlink DESTINATION="ID_956724648" ENDARROW="Default" ENDINCLINATION="498;0;" ID="Arrow_ID_1033993672" STARTARROW="None" STARTINCLINATION="498;0;"/>
</node>
<node COLOR="#ff3300" CREATED="1553604184019" ID="ID_957817141" MODIFIED="1553604927437" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff3300" CREATED="1553604185953" ID="ID_1389261388" MODIFIED="1553604981570" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1553604258313" ID="ID_306704184" MODIFIED="1553604964657" TEXT="The &quot;Export&quot; button should get disabled when user selects the checkbox">
<node COLOR="#33cc00" CREATED="1553604284150" ID="ID_208018255" MODIFIED="1553604964657" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1553604293727" ID="ID_1399188481" MODIFIED="1554789989453" TEXT="Click export without selecting any checkbox and observe">
<node COLOR="#33cc00" CREATED="1553604356117" ID="ID_289774681" MODIFIED="1553604964656" TEXT="External disk has enough space">
<node COLOR="#33cc00" CREATED="1553604374432" ID="ID_1203229713" MODIFIED="1553604964655" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1553604387234" ID="ID_65587076" MODIFIED="1553604964651" TEXT="All the packet should get exported to the selected path and it should display a pop up with packet id and status as &quot;Exported&quot;"/>
</node>
<node COLOR="#ff3300" CREATED="1553604375759" ID="ID_547352123" MODIFIED="1553604927434" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff3300" CREATED="1553604378282" ID="ID_1221673192" MODIFIED="1553604981570" TEXT="Display appropriate alert message"/>
</node>
</node>
</node>
</node>
<node COLOR="#ff3300" CREATED="1553604285296" ID="ID_140776652" MODIFIED="1553604927436" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff3300" CREATED="1553604185953" ID="ID_1469113693" MODIFIED="1553604981570" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1553604642738" ID="ID_1791019401" MODIFIED="1553604964650" TEXT="User clicks upload button without selecting any packet">
<node COLOR="#33cc00" CREATED="1553604668651" ID="ID_93985301" MODIFIED="1553604964649" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1553604670339" ID="ID_1861815269" MODIFIED="1553604964649" TEXT="Display &quot;Please select the packets to upload&#x201d; alert message"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1553604735490" ID="ID_206284030" MODIFIED="1553604964650" TEXT="Verify whether the packets are uploaded to the server after user clicks upload button" VSHIFT="17">
<node COLOR="#33cc00" CREATED="1553604745727" ID="ID_98999174" MODIFIED="1553604964648" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1553604846947" ID="ID_1559192527" MODIFIED="1553604964642" TEXT="Packet should get uploaded to the server and the same will be displayed as pop up with packet id and status"/>
</node>
<node COLOR="#33cc00" CREATED="1553604788763" ID="ID_1624731621" MODIFIED="1553604964642" TEXT="No">
<node COLOR="#33cc00" CREATED="1553604792281" ID="ID_1312461903" MODIFIED="1553604964641" TEXT="Check machine is online">
<node COLOR="#ff3300" CREATED="1553604820340" ID="ID_1228712960" MODIFIED="1553604927436" TEXT="Yes">
<edge COLOR="#ff0000"/>
<node COLOR="#ff3300" CREATED="1553604832954" ID="ID_1280376253" MODIFIED="1553604981570" TEXT="Display appropriate error message"/>
</node>
<node COLOR="#33cc00" CREATED="1553604821984" ID="ID_1802605169" MODIFIED="1553604964641" TEXT="No">
<node COLOR="#33cc00" CREATED="1553604824274" ID="ID_937893440" MODIFIED="1553604964640" TEXT="Connect to internet and retry"/>
</node>
</node>
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
