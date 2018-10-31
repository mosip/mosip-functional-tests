<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1538546432226" ID="ID_1289374765" MODIFIED="1539176644249" TEXT="Registration - Registration Client Installation">
<node COLOR="#00cc00" CREATED="1538552664561" ID="ID_1575628185" MODIFIED="1539002220653" POSITION="right" TEXT="Download setup kit">
<node COLOR="#00cc00" CREATED="1538546560614" HGAP="-99" ID="ID_1911580426" MODIFIED="1539002227629" TEXT="Setup Kit should be available and user must logged in as RO / Supervisor" VSHIFT="-139">
<icon BUILTIN="attach"/>
<node COLOR="#00cc00" CREATED="1538546638685" ID="ID_1493952769" MODIFIED="1538996448050" TEXT="RO / Supervisor should have access to the download portal">
<edge COLOR="#00cc33"/>
<arrowlink DESTINATION="ID_1493952769" ENDARROW="Default" ENDINCLINATION="0;0;" ID="Arrow_ID_933818428" STARTARROW="None" STARTINCLINATION="0;0;"/>
<linktarget COLOR="#b0b0b0" DESTINATION="ID_1493952769" ENDARROW="Default" ENDINCLINATION="0;0;" ID="Arrow_ID_933818428" SOURCE="ID_1493952769" STARTARROW="None" STARTINCLINATION="0;0;"/>
<node COLOR="#00cc00" CREATED="1538546900673" ID="ID_1414288116" MODIFIED="1539060159710" TEXT="Verify whether user is able to download the kit to the local client machine based on OS">
<node COLOR="#00cc00" CREATED="1538549281635" HGAP="11" ID="ID_150300847" MODIFIED="1539001972153" TEXT="Download status" VSHIFT="-44">
<node COLOR="#33cc00" CREATED="1539060206720" ID="ID_1167318419" MODIFIED="1539060220755" TEXT="Success - User should be able to download and store in local client"/>
<node COLOR="#ff0000" CREATED="1539329247495" ID="ID_599375420" MODIFIED="1539329306583" TEXT="Display &quot;Appropriate error message&#x201d; / raise a defect">
<edge COLOR="#ff0000"/>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1539060266124" HGAP="15" ID="ID_1976803277" MODIFIED="1539060312171" TEXT="Verify whether user is able to unzip the kit and verify the content as same as design document" VSHIFT="29">
<node COLOR="#33cc00" CREATED="1539060314962" ID="ID_211828018" MODIFIED="1539060324775" TEXT="Unzip status">
<node COLOR="#33cc00" CREATED="1539060330945" ID="ID_4476642" MODIFIED="1539060363835" TEXT="Success - User should be able to unzip without any issues"/>
<node COLOR="#ff0000" CREATED="1539329247495" ID="ID_1174896445" MODIFIED="1539329314329" TEXT="Failure - Display &quot;Appropriate error message&#x201d; / raise a defect">
<edge COLOR="#ff0000"/>
</node>
</node>
</node>
</node>
<node COLOR="#00cc00" CREATED="1538546677233" HGAP="31" ID="ID_1365059240" MODIFIED="1538997679199" TEXT="Setup Kit should be available in zipped format" VSHIFT="84">
<edge COLOR="#00cc00"/>
<node COLOR="#00cc00" CREATED="1538979499147" ID="ID_768481814" MODIFIED="1539002177707" TEXT="Yes">
<node COLOR="#00cc00" CREATED="1538979533397" ID="ID_923667343" MODIFIED="1538996501083" TEXT="Donwload the file as mentioned above"/>
</node>
<node COLOR="#ff0000" CREATED="1538979511274" ID="ID_1954346100" MODIFIED="1539002186155" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539329247495" ID="ID_1251597489" MODIFIED="1539329258739" TEXT="Display &quot;Appropriate error message&#x201d; / raise a defect"/>
</node>
</node>
<node COLOR="#00cc00" CREATED="1538550071542" HGAP="16" ID="ID_1167196337" MODIFIED="1539004933785" TEXT="Verification of Txn details for Audit purpose" VSHIFT="86">
<edge COLOR="#00cc00"/>
<node COLOR="#00cc00" CREATED="1539003087723" ID="ID_648252266" MODIFIED="1539060406447" TEXT="System capture all Txn details">
<node COLOR="#00cc00" CREATED="1539003122483" ID="ID_1079503882" MODIFIED="1539004902947" TEXT="Store all the details under &quot;Audit_Log&quot; table"/>
</node>
<node COLOR="#ff0000" CREATED="1539060442854" ID="ID_1951538127" MODIFIED="1539329324690" TEXT="System fails to capture Txn details">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539060455389" ID="ID_934288027" MODIFIED="1539329324690" TEXT="Display &quot;Appropriate error message&#x201d; / raise a defect">
<edge COLOR="#ff0000"/>
</node>
</node>
</node>
</node>
</node>
</node>
</map>
