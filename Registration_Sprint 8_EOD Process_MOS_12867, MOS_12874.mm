<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1550402314426" ID="ID_985731472" MODIFIED="1550480607033" TEXT="MOSIP - Registration">
<node CREATED="1550474824163" ID="ID_1661413612" MODIFIED="1550474829740" POSITION="right" TEXT="RC Freeze">
<node CREATED="1550402753895" ID="ID_577079117" LINK="https://mosipid.atlassian.net/browse/MOS-12867" MODIFIED="1550402778718" TEXT="MOS-12867">
<node COLOR="#33cc00" CREATED="1550402578893" HGAP="78" ID="ID_1770097019" MODIFIED="1550402904511" TEXT="Behavior of RC for above scenario when EOD" VSHIFT="-17">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1550402630306" HGAP="16" ID="ID_1339807700" MODIFIED="1550402904511" TEXT="On" VSHIFT="-20">
<node COLOR="#33cc00" CREATED="1550402359914" ID="ID_1944266516" MODIFIED="1550402904511" TEXT="Verify whether the application restricts to create registration when the pending packet count exceeds the configured limit">
<node COLOR="#33cc00" CREATED="1550402406591" ID="ID_1033428925" MODIFIED="1550402904495" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1550402432999" ID="ID_880553784" MODIFIED="1550402904495" TEXT="Display &#x201c;Maximum number of registration packets pending approval on client reached. Please approve or reject packets before proceeding with this registration.&#x201d;"/>
</node>
<node COLOR="#ff9900" CREATED="1550402415393" ID="ID_1473358686" MODIFIED="1550402953420" TEXT="No">
<node COLOR="#ff6600" CREATED="1550402442966" ID="ID_733823264" MODIFIED="1550402923546" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1550402460050" ID="ID_345965370" MODIFIED="1550402904511" TEXT="Verify whether the application restricts to create registration when the pending packet approval time is exceeded">
<node COLOR="#33cc00" CREATED="1550402406591" ID="ID_1376470852" MODIFIED="1550402904495" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1550402432999" ID="ID_1224529162" MODIFIED="1550402904495" TEXT="Display &#x201c;Maximum duration for registration packets pending approval on client reached.Please approve or reject packets before proceeding with this registration.&#x201d;"/>
</node>
<node COLOR="#ff9900" CREATED="1550402415393" ID="ID_276949542" MODIFIED="1550402953420" TEXT="No">
<node COLOR="#ff6600" CREATED="1550402442966" ID="ID_134245323" MODIFIED="1550402923546" TEXT="Raise a defect"/>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1550402634934" HGAP="21" ID="ID_1302406095" MODIFIED="1550402904511" TEXT="Off" VSHIFT="32">
<node COLOR="#33cc00" CREATED="1550402359914" ID="ID_1114730354" MODIFIED="1550402904511" TEXT="Verify whether the application restricts to create registration when the pending packet count exceeds the configured limit">
<node COLOR="#33cc00" CREATED="1550402406591" ID="ID_1335676974" MODIFIED="1550402904495" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1550402432999" ID="ID_1895648090" MODIFIED="1550402904480" TEXT="Display &#x201c;Maximum number of registration packets pending approval on client reached. Please approve or reject packets before proceeding with this registration.&#x201d;"/>
</node>
<node COLOR="#ff9900" CREATED="1550402415393" ID="ID_623694189" MODIFIED="1550402953420" TEXT="No">
<node COLOR="#ff6600" CREATED="1550402442966" ID="ID_172846921" MODIFIED="1550402923546" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1550402460050" ID="ID_266139700" MODIFIED="1550402904495" TEXT="Verify whether the application restricts to create registration when the pending packet approval time is exceeded">
<node COLOR="#33cc00" CREATED="1550402406591" ID="ID_1983042154" MODIFIED="1550402904495" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1550402432999" ID="ID_545314601" MODIFIED="1550402904495" TEXT="Display &#x201c;Maximum duration for registration packets pending approval on client reached.Please approve or reject packets before proceeding with this registration.&#x201d;"/>
</node>
<node COLOR="#ff9900" CREATED="1550402415393" ID="ID_1876115769" MODIFIED="1550402953420" TEXT="No">
<node COLOR="#ff6600" CREATED="1550402442966" ID="ID_951669199" MODIFIED="1550402923546" TEXT="Raise a defect"/>
</node>
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
<node CREATED="1550480606998" ID="ID_1240238276" LINK="https://mosipid.atlassian.net/browse/MOS-12874" MODIFIED="1550480650390" POSITION="left" TEXT="MOS-12874">
<node CREATED="1550474846219" ID="ID_1729714204" MODIFIED="1550480617414" TEXT="EOD Process">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1550474887550" HGAP="29" ID="ID_781524290" MODIFIED="1550480600070" TEXT="EOD On to Off" VSHIFT="-86">
<node COLOR="#33cc00" CREATED="1550474908590" HGAP="17" ID="ID_92378080" MODIFIED="1550480536320" TEXT="Packets in pending approval status changed" VSHIFT="-19">
<node COLOR="#ff6600" CREATED="1550475820286" ID="ID_1755566875" MODIFIED="1550480582743" TEXT="Yes">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1550475830137" ID="ID_367428549" MODIFIED="1550480571382" TEXT="Raise a defect">
<edge COLOR="#ff0000"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1550475822153" ID="ID_1783889754" MODIFIED="1550480536317" TEXT="No">
<node COLOR="#33cc00" CREATED="1550475835427" ID="ID_50102842" MODIFIED="1550480536317" TEXT="Supervisor needs to approve"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1550475923650" ID="ID_1812904531" MODIFIED="1550480536320" TEXT="New packets status will set to Ready to Upload" VSHIFT="17">
<node COLOR="#33cc00" CREATED="1550475820286" ID="ID_1800831920" MODIFIED="1550480536318" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1550475830137" ID="ID_1819224381" MODIFIED="1550480536318" TEXT="Verify the same in DB"/>
</node>
<node COLOR="#ff6600" CREATED="1550475822153" ID="ID_28285571" MODIFIED="1550480582742" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1550475835427" ID="ID_849710456" MODIFIED="1550480571381" TEXT="Raise a defect">
<edge COLOR="#ff0000"/>
</node>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1550480395477" ID="ID_949149670" MODIFIED="1550480600069" TEXT="EOD Off to On">
<node COLOR="#33cc00" CREATED="1550474908590" HGAP="17" ID="ID_215597221" MODIFIED="1550480536319" TEXT="Packets in ready to upload status changed" VSHIFT="-19">
<node COLOR="#ff6600" CREATED="1550475820286" ID="ID_1563013174" MODIFIED="1550480582741" TEXT="Yes">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1550475830137" ID="ID_1886433963" MODIFIED="1550480571381" TEXT="Raise a defect">
<edge COLOR="#ff0000"/>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1550475923650" ID="ID_445543814" MODIFIED="1550480536319" TEXT="New packets status will set to pending approval" VSHIFT="17">
<node COLOR="#33cc00" CREATED="1550475820286" ID="ID_1900070459" MODIFIED="1550480536319" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1550475830137" ID="ID_1168969069" MODIFIED="1550480536319" TEXT="Verify the same in DB"/>
</node>
<node COLOR="#ff6600" CREATED="1550475822153" ID="ID_1071723213" MODIFIED="1550480582740" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1550475835427" ID="ID_910026216" MODIFIED="1550480571381" TEXT="Raise a defect">
<edge COLOR="#ff0000"/>
</node>
</node>
</node>
</node>
<node COLOR="#00cc00" CREATED="1538550071542" HGAP="9" ID="ID_1964705177" MODIFIED="1546498495098" TEXT="Verification of Txn details for Audit purpose" VSHIFT="34">
<edge COLOR="#00cc00"/>
<node COLOR="#00cc00" CREATED="1539003087723" ID="ID_1525496166" MODIFIED="1542005044938" TEXT="System capture all Txn details">
<node COLOR="#00cc00" CREATED="1539003122483" ID="ID_1827557145" MODIFIED="1546498515692" TEXT="Store all the details under &quot;Audit_Log&quot; table such as User id or system account; Machine Details; Event Name; Application Name, and Event data including user entered fields."/>
</node>
<node COLOR="#ff0000" CREATED="1539060442854" ID="ID_1633927491" MODIFIED="1542005044938" TEXT="System fails to capture Txn details">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539089688847" ID="ID_1490814303" MODIFIED="1542005044938" TEXT="Display appropriate error message / Raise a defect"/>
</node>
</node>
</node>
</node>
</node>
</map>
