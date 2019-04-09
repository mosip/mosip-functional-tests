<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node COLOR="#33cc00" CREATED="1554118352086" ID="ID_1175766533" MODIFIED="1554119021311" TEXT="Registration - blacklisted words">
<node COLOR="#33cc00" CREATED="1554118375569" ID="ID_212629650" LINK="https://mosipid.atlassian.net/browse/MOS-20066" MODIFIED="1554119021311" POSITION="right" TEXT="MOS-20066">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1554118406402" ID="ID_648438204" MODIFIED="1554119021311" TEXT="Verify the user is able to configure the blacklisted words in admin portal">
<node COLOR="#33cc00" CREATED="1554118427706" ID="ID_1748793950" MODIFIED="1554119021311" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1554118435001" ID="ID_1585104235" MODIFIED="1554119021311" TEXT="Configure all the blacklisted words both for primary and secondary"/>
</node>
<node COLOR="#ff6600" CREATED="1554118429428" ID="ID_390751115" MODIFIED="1554119054414" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1554118431197" ID="ID_95239249" MODIFIED="1554119070772" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1554118527707" ID="ID_1476337997" MODIFIED="1554119021311" TEXT="Verify through master data sync all the words will get saved into local DB">
<node COLOR="#33cc00" CREATED="1554118542587" ID="ID_1150149878" MODIFIED="1554119021311" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1554118549144" ID="ID_885413151" MODIFIED="1554119021311" TEXT="Verify the same in reg.global_param table"/>
</node>
<node COLOR="#ff6600" CREATED="1554118543958" ID="ID_1775129308" MODIFIED="1554119054414" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1554118545695" ID="ID_605415010" MODIFIED="1554119070772" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1554118572249" HGAP="18" ID="ID_332652343" MODIFIED="1554119021311" TEXT="Complete the demo screen and click next button" VSHIFT="28">
<node COLOR="#33cc00" CREATED="1554118597882" ID="ID_1518038224" MODIFIED="1554119021311" TEXT="Blacklisted words found">
<node COLOR="#33cc00" CREATED="1554118606498" ID="ID_234430356" MODIFIED="1554119021311" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1554118623852" ID="ID_1596021381" MODIFIED="1554119021311" TEXT="Check Full name, Address Line 1,2 and 3 in both primary and secondary language against master data">
<node COLOR="#33cc00" CREATED="1554118693692" ID="ID_1265305931" MODIFIED="1554119021295" TEXT="Display &#x201c;XXX word(s) are not permitted in the &#x2018;ABC&#x2019; field by the system. Please correct. In case this is a mistake of the system, please inform the system administrator via email &lt;email id&gt;.&#x201d;"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1554118608011" ID="ID_1009479983" MODIFIED="1554119021295" TEXT="No">
<node COLOR="#33cc00" CREATED="1554118617031" ID="ID_146966233" MODIFIED="1554119021295" TEXT="Proceed to next screen"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1554118779461" ID="ID_821406320" MODIFIED="1554119021295" TEXT="Multiple blacklisted words found">
<node COLOR="#33cc00" CREATED="1554118792174" ID="ID_839574547" MODIFIED="1554119021295" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1554118826860" ID="ID_384704052" MODIFIED="1554119021280" TEXT="The error message should display all the blacklisted words and the user should delete the words and re-enter valid data."/>
</node>
</node>
<node COLOR="#ff6600" CREATED="1554118950328" ID="ID_164836161" MODIFIED="1554119078513" TEXT="No message displayed even after a blacklisted word is entered either in primary / secondary or in both">
<edge COLOR="#33cc00"/>
<node COLOR="#ff0000" CREATED="1554118955341" ID="ID_1243182362" MODIFIED="1554119070772" TEXT="Yes">
<node COLOR="#ff0000" CREATED="1554118960258" ID="ID_1417594532" MODIFIED="1554119070772" TEXT="Raise a defect"/>
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
</map>
