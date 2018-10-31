<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1539155462887" ID="ID_216817901" MODIFIED="1539171167184" TEXT="Registration - Acknowledgement">
<node COLOR="#009900" CREATED="1539155484478" ID="ID_1559507588" MODIFIED="1539330486946" POSITION="right" TEXT="Printing Registration Receipt">
<edge COLOR="#00cc00"/>
<node COLOR="#009900" CREATED="1539155501954" ID="ID_1548913945" MODIFIED="1539156186045" TEXT="RID generated">
<node COLOR="#009900" CREATED="1539155513652" HGAP="1" ID="ID_265846392" MODIFIED="1539156186045" TEXT="Yes" VSHIFT="-117">
<node COLOR="#009900" CREATED="1539155677617" HGAP="14" ID="ID_921951229" MODIFIED="1539156186029" TEXT="Verify except Name and address all other fields are available only in default language" VSHIFT="-64">
<node COLOR="#009900" CREATED="1539155803737" ID="ID_1237601233" MODIFIED="1539156186029" TEXT="Yes">
<node COLOR="#009900" CREATED="1539156059494" ID="ID_1420924221" MODIFIED="1539156186045" TEXT="Proceed to take print out"/>
</node>
<node COLOR="#ff6600" CREATED="1539156046679" ID="ID_1047696700" MODIFIED="1539330510652" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539089688847" ID="ID_285530157" MODIFIED="1539330406352" TEXT="Display appropriate error message / Raise a defect"/>
</node>
</node>
<node COLOR="#009900" CREATED="1539155748557" HGAP="21" ID="ID_1323507974" MODIFIED="1539156186029" TEXT="Verify Name and address are displayed both in default and secondary language" VSHIFT="-24">
<node COLOR="#009900" CREATED="1539155803737" ID="ID_781539382" MODIFIED="1539156186029" TEXT="Yes">
<node COLOR="#009900" CREATED="1539156059494" ID="ID_667613664" MODIFIED="1539156186029" TEXT="Proceed to take print out"/>
</node>
<node COLOR="#ff6600" CREATED="1539156046679" ID="ID_724026285" MODIFIED="1539330510652" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539089688847" ID="ID_1637627040" MODIFIED="1539330406352" TEXT="Display appropriate error message / Raise a defect"/>
</node>
</node>
<node COLOR="#009900" CREATED="1539155806873" ID="ID_654943065" MODIFIED="1539156186029" TEXT="Verify 2D barcode is displayed and has fields details mentioned in the user story attachment" VSHIFT="38">
<node COLOR="#009900" CREATED="1539155909921" ID="ID_1038678579" MODIFIED="1539156186029" TEXT="Yes">
<node COLOR="#009900" CREATED="1539156020794" ID="ID_1275127010" MODIFIED="1539156186029" TEXT="Proceed to take print out"/>
</node>
<node COLOR="#ff6600" CREATED="1539155992950" ID="ID_1525736190" MODIFIED="1539330510652" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539089688847" ID="ID_610184157" MODIFIED="1539330406352" TEXT="Display appropriate error message / Raise a defect"/>
</node>
</node>
<node COLOR="#009900" CREATED="1539155913452" HGAP="19" ID="ID_1606341445" MODIFIED="1539156186029" TEXT="Verify user is able to print the receipt" VSHIFT="35">
<node COLOR="#009900" CREATED="1539155942969" ID="ID_913599042" MODIFIED="1539156186029" TEXT="Yes">
<node COLOR="#009900" CREATED="1539155969995" ID="ID_1731365626" MODIFIED="1539156186029" TEXT="Print the receipt with all relevant fields and 2D barcode"/>
</node>
<node COLOR="#ff6600" CREATED="1539155953811" ID="ID_1078873254" MODIFIED="1539330510652" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539155958638" ID="ID_1564908951" MODIFIED="1539156219541" TEXT="Display &quot;There was an error printing your document.&#x201d;"/>
</node>
</node>
<node COLOR="#00cc00" CREATED="1538550071542" HGAP="16" ID="ID_1167196337" MODIFIED="1539004933785" TEXT="Verification of Txn details for Audit purpose" VSHIFT="86">
<edge COLOR="#00cc00"/>
<node COLOR="#00cc00" CREATED="1539003087723" ID="ID_648252266" MODIFIED="1539060406447" TEXT="System capture all Txn details">
<node COLOR="#00cc00" CREATED="1539003122483" ID="ID_1079503882" MODIFIED="1539004902947" TEXT="Store all the details under &quot;Audit_Log&quot; table"/>
</node>
<node COLOR="#ff0000" CREATED="1539060442854" ID="ID_1951538127" MODIFIED="1539330510652" TEXT="System fails to capture Txn details">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539089688847" ID="ID_253954705" MODIFIED="1539330406352" TEXT="Display appropriate error message / Raise a defect"/>
</node>
</node>
</node>
</node>
</node>
</node>
</map>
