<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1553166867575" ID="ID_1408711253" MODIFIED="1553166877268" TEXT="Registration - New Registration">
<node CREATED="1553166893435" ID="ID_512227747" LINK="https://mosipid.atlassian.net/browse/MOS-15918" MODIFIED="1553167876408" POSITION="right" TEXT="MOS-15918">
<edge COLOR="#00cc00"/>
<node COLOR="#00cc00" CREATED="1553166911992" HGAP="17" ID="ID_60677843" MODIFIED="1553167928882" TEXT="Max limit for export or upload as configurable parameter" VSHIFT="-22">
<node COLOR="#00cc00" CREATED="1553167000490" ID="ID_923797853" MODIFIED="1553167928882" TEXT="Yes">
<node COLOR="#00cc00" CREATED="1553166957818" ID="ID_1642109056" MODIFIED="1553167928883" TEXT="Use the parameter to set the limit based on which application allows the user to register / UIN update"/>
</node>
<node COLOR="#ff3300" CREATED="1553167002050" ID="ID_904141168" MODIFIED="1553167903699" TEXT="No">
<edge COLOR="#cc0000"/>
<node COLOR="#ff3300" CREATED="1553167006400" ID="ID_1082067701" MODIFIED="1553167939903" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#00cc00" CREATED="1553166930786" HGAP="16" ID="ID_658123726" MODIFIED="1553167928882" TEXT="Max limit for software update as configurable parameter" VSHIFT="16">
<node COLOR="#00cc00" CREATED="1553166947361" ID="ID_1929631372" MODIFIED="1553167928880" TEXT="Yes">
<node COLOR="#00cc00" CREATED="1553166957818" ID="ID_1465694846" MODIFIED="1553167928881" TEXT="Use the parameter to set the limit based on which application allows the user to register / UIN update"/>
</node>
<node COLOR="#ff3300" CREATED="1553166949225" ID="ID_1840915267" MODIFIED="1553167903698" TEXT="No">
<edge COLOR="#cc0000"/>
<node COLOR="#ff3300" CREATED="1553166951006" ID="ID_702709510" MODIFIED="1553167939904" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#00cc00" CREATED="1553167036468" HGAP="19" ID="ID_713934317" MODIFIED="1553167928881" TEXT="Maximum duration to which registration is permitted without export or upload of packets is exceeded" VSHIFT="30">
<node COLOR="#ff3300" CREATED="1553167059647" ID="ID_1936302157" MODIFIED="1553167903698" TEXT="Yes" VSHIFT="-16">
<edge COLOR="#cc0000"/>
<node COLOR="#ff3300" CREATED="1553167697070" ID="ID_1157455878" MODIFIED="1553167939904" TEXT="Display &#x201c;Time since last export or upload of registration packets exceeded maximum limit. Please export or upload packets to server before proceeding with this registration.&#x201d;"/>
</node>
<node COLOR="#00cc00" CREATED="1553167066555" HGAP="22" ID="ID_388327770" MODIFIED="1553167928880" TEXT="No" VSHIFT="22">
<node COLOR="#00cc00" CREATED="1553167716369" ID="ID_88322798" MODIFIED="1553167928880" TEXT="Allow the user to proceed with the new registration or UIN update"/>
</node>
</node>
<node COLOR="#00cc00" CREATED="1553167733056" HGAP="16" ID="ID_6270220" MODIFIED="1553167928879" TEXT="Maximum duration to which registration is permitted without check for software update is exceeded" VSHIFT="36">
<node COLOR="#ff3300" CREATED="1553167059647" ID="ID_1306832455" MODIFIED="1553167903697" TEXT="Yes" VSHIFT="-16">
<edge COLOR="#cc0000"/>
<node COLOR="#ff3300" CREATED="1553167697070" ID="ID_418698653" MODIFIED="1553167939913" TEXT="Display &#x201c;Time since last check for software update exceeded maximum limit. Please update before proceeding with this registration.&#x201d;"/>
</node>
<node COLOR="#00cc00" CREATED="1553167066555" HGAP="22" ID="ID_1578276333" MODIFIED="1553167928879" TEXT="No" VSHIFT="22">
<node COLOR="#00cc00" CREATED="1553167716369" ID="ID_32600538" MODIFIED="1553167928878" TEXT="Allow the user to proceed with the new registration or UIN update"/>
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
