<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1551173502223" ID="ID_85474130" MODIFIED="1551173614950" TEXT="Registration - Supervisor Authentication for Biometric Exception">
<node CREATED="1551173599876" ID="ID_1544232411" LINK="https://mosipid.atlassian.net/browse/MOS-13659" MODIFIED="1551174409118" POSITION="right" TEXT="MOS-13659">
<edge COLOR="#66cc00"/>
<node COLOR="#66cc00" CREATED="1551173708742" HGAP="21" ID="ID_486461926" MODIFIED="1551174468954" TEXT="Verify supervisor authentication is configurable" VSHIFT="-17">
<node COLOR="#66cc00" CREATED="1551173723578" ID="ID_343609622" MODIFIED="1551174468954" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1551173735708" ID="ID_169409031" MODIFIED="1551174468953" TEXT="Set the parameter &quot;supervisorVerificationRequiredForExceptions&quot; to Yes / No based on center requirement"/>
</node>
<node COLOR="#ff6600" CREATED="1551173726033" ID="ID_622846585" MODIFIED="1551174488445" TEXT="No">
<node COLOR="#ff3300" CREATED="1551173729731" ID="ID_1225650615" MODIFIED="1551174521839" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#66cc00" CREATED="1551173791176" ID="ID_1065362856" MODIFIED="1551174468952" TEXT="Application with Biometric Exception">
<node COLOR="#66cc00" CREATED="1551173832731" HGAP="17" ID="ID_1987593396" MODIFIED="1551174468952" TEXT="Supervisor Authentication status" VSHIFT="-8">
<node COLOR="#66cc00" CREATED="1551173862417" HGAP="11" ID="ID_1272171432" MODIFIED="1551174468951" TEXT="ON" VSHIFT="4">
<node COLOR="#66cc00" CREATED="1551173919163" ID="ID_1215434335" MODIFIED="1551174468949" TEXT="Verify application displays Supervisor authentication screen for registration completion">
<node COLOR="#66cc00" CREATED="1551173935149" ID="ID_538726300" MODIFIED="1551174468940" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1551173950116" ID="ID_1724789998" MODIFIED="1551174468939" TEXT="Provide valid credentials to authenticate the packet"/>
</node>
<node COLOR="#ff6600" CREATED="1551173937169" ID="ID_304860590" MODIFIED="1551174769600" TEXT="No">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1551173992437" ID="ID_1535087598" MODIFIED="1551174521839" TEXT="Raise a defect"/>
</node>
</node>
</node>
<node COLOR="#66cc00" CREATED="1551173905645" HGAP="10" ID="ID_530783886" MODIFIED="1551174468946" TEXT="OFF" VSHIFT="8">
<node COLOR="#66cc00" CREATED="1551173919163" ID="ID_760747023" MODIFIED="1551174468940" TEXT="Verify application displays Supervisor authentication screen for registration completion">
<node COLOR="#ff6600" CREATED="1551173935149" ID="ID_345761483" MODIFIED="1551174769601" TEXT="Yes">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1551173992437" ID="ID_836977069" MODIFIED="1551174521838" TEXT="Raise a defect"/>
</node>
<node COLOR="#66cc00" CREATED="1551173937169" ID="ID_1290864604" MODIFIED="1551174468938" TEXT="No">
<node COLOR="#66cc00" CREATED="1551173992437" ID="ID_682124750" MODIFIED="1551174468938" TEXT="Based on EOD status packet will either get auto uploaded / moved to pending approval queue"/>
</node>
</node>
</node>
</node>
</node>
<node COLOR="#66cc00" CREATED="1551173791176" ID="ID_1660840705" MODIFIED="1551174468925" TEXT="Application without Biometric Exception">
<node COLOR="#66cc00" CREATED="1551173832731" HGAP="10" ID="ID_669640638" MODIFIED="1551174468925" TEXT="Supervisor Authentication status" VSHIFT="-7">
<node COLOR="#66cc00" CREATED="1551173862417" HGAP="10" ID="ID_1839483241" MODIFIED="1551174468926" TEXT="ON" VSHIFT="-7">
<node COLOR="#66cc00" CREATED="1551173919163" ID="ID_1751102777" MODIFIED="1551174468926" TEXT="Verify application displays Supervisor authentication screen for registration completion">
<node COLOR="#ff6600" CREATED="1551173935149" ID="ID_1728853421" MODIFIED="1551174769601" TEXT="Yes">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1551173950116" ID="ID_1312113384" MODIFIED="1551174521838" TEXT="Raise a defect"/>
</node>
<node COLOR="#66cc00" CREATED="1551173937169" ID="ID_889272329" MODIFIED="1551174468929" TEXT="No">
<node COLOR="#66cc00" CREATED="1551173992437" ID="ID_937546349" MODIFIED="1551174468927" TEXT="Based on EOD status packet will either get auto uploaded / moved to pending approval queue"/>
</node>
</node>
</node>
<node COLOR="#66cc00" CREATED="1551173905645" HGAP="14" ID="ID_522199463" MODIFIED="1551174468920" TEXT="OFF" VSHIFT="12">
<node COLOR="#66cc00" CREATED="1551173919163" ID="ID_1200237568" MODIFIED="1551174468921" TEXT="Verify application displays Supervisor authentication screen for registration completion">
<node COLOR="#ff6600" CREATED="1551173935149" ID="ID_1740150260" MODIFIED="1551174769601" TEXT="Yes">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1551173992437" ID="ID_1702470702" MODIFIED="1551174521838" TEXT="Raise a defect"/>
</node>
<node COLOR="#66cc00" CREATED="1551173937169" ID="ID_195589250" MODIFIED="1551174468919" TEXT="No">
<node COLOR="#66cc00" CREATED="1551173992437" ID="ID_1116890428" MODIFIED="1551174468919" TEXT="Based on EOD status packet will either get auto uploaded / moved to pending approval queue"/>
</node>
</node>
</node>
</node>
</node>
<node COLOR="#66cc00" CREATED="1551173791176" ID="ID_1898677785" MODIFIED="1551174468924" TEXT="UIN update with Biometric Exception">
<node COLOR="#66cc00" CREATED="1551173832731" HGAP="17" ID="ID_44981537" MODIFIED="1551174468924" TEXT="Supervisor Authentication status" VSHIFT="-8">
<node COLOR="#66cc00" CREATED="1551173862417" HGAP="11" ID="ID_1266771403" MODIFIED="1551174468921" TEXT="ON" VSHIFT="4">
<node COLOR="#66cc00" CREATED="1551173919163" ID="ID_779306239" MODIFIED="1551174468920" TEXT="Verify application displays Supervisor authentication screen for registration completion">
<node COLOR="#66cc00" CREATED="1551173935149" ID="ID_35094353" MODIFIED="1551174468918" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1551173950116" ID="ID_414670458" MODIFIED="1551174468917" TEXT="Provide valid credentials to authenticate the packet"/>
</node>
<node COLOR="#ff6600" CREATED="1551173937169" ID="ID_1490382792" MODIFIED="1551174769602" TEXT="No">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1551173992437" ID="ID_1685622768" MODIFIED="1551174521837" TEXT="Raise a defect"/>
</node>
</node>
</node>
<node COLOR="#66cc00" CREATED="1551173905645" HGAP="10" ID="ID_88379052" MODIFIED="1551174468921" TEXT="OFF" VSHIFT="8">
<node COLOR="#66cc00" CREATED="1551173919163" ID="ID_1031784899" MODIFIED="1551174468922" TEXT="Verify application displays Supervisor authentication screen for registration completion">
<node COLOR="#ff6600" CREATED="1551173935149" ID="ID_776231137" MODIFIED="1551174769602" TEXT="Yes">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1551173992437" ID="ID_1300655108" MODIFIED="1551174521837" TEXT="Raise a defect"/>
</node>
<node COLOR="#66cc00" CREATED="1551173937169" ID="ID_763511791" MODIFIED="1551174468915" TEXT="No">
<node COLOR="#66cc00" CREATED="1551173992437" ID="ID_1970906950" MODIFIED="1551174468914" TEXT="Based on EOD status packet will either get auto uploaded / moved to pending approval queue"/>
</node>
</node>
</node>
</node>
</node>
<node COLOR="#66cc00" CREATED="1551173791176" ID="ID_824800678" MODIFIED="1551174468924" TEXT="UIN update without Biometric Exception">
<node COLOR="#66cc00" CREATED="1551173832731" HGAP="10" ID="ID_727884290" MODIFIED="1551174468923" TEXT="Supervisor Authentication status" VSHIFT="-7">
<node COLOR="#66cc00" CREATED="1551173862417" HGAP="10" ID="ID_606523403" MODIFIED="1551174468922" TEXT="ON" VSHIFT="-7">
<node COLOR="#66cc00" CREATED="1551173919163" ID="ID_1791179465" MODIFIED="1551174468922" TEXT="Verify application displays Supervisor authentication screen for registration completion">
<node COLOR="#ff6600" CREATED="1551173935149" ID="ID_977847664" MODIFIED="1551174769602" TEXT="Yes">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1551173950116" ID="ID_248708137" MODIFIED="1551174521837" TEXT="Raise a defect"/>
</node>
<node COLOR="#66cc00" CREATED="1551173937169" ID="ID_1172696718" MODIFIED="1551174468913" TEXT="No">
<node COLOR="#66cc00" CREATED="1551173992437" ID="ID_1123756796" MODIFIED="1551174468911" TEXT="Based on EOD status packet will either get auto uploaded / moved to pending approval queue"/>
</node>
</node>
</node>
<node COLOR="#66cc00" CREATED="1551173905645" HGAP="14" ID="ID_1444206406" MODIFIED="1551174468923" TEXT="OFF" VSHIFT="12">
<node COLOR="#66cc00" CREATED="1551173919163" ID="ID_1631222426" MODIFIED="1551174468923" TEXT="Verify application displays Supervisor authentication screen for registration completion">
<node COLOR="#ff6600" CREATED="1551173935149" ID="ID_1795790950" MODIFIED="1551174769603" TEXT="Yes">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1551173992437" ID="ID_1760727604" MODIFIED="1551174521836" TEXT="Raise a defect"/>
</node>
<node COLOR="#66cc00" CREATED="1551173937169" ID="ID_816938407" MODIFIED="1551174468909" TEXT="No">
<node COLOR="#66cc00" CREATED="1551173992437" ID="ID_819780257" MODIFIED="1551174468910" TEXT="Based on EOD status packet will either get auto uploaded / moved to pending approval queue"/>
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
</map>
