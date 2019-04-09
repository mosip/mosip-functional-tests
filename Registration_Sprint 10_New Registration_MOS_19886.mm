<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1554098014286" ID="ID_1990904814" MODIFIED="1554098045596" TEXT="Registration - Supervisor authentication">
<node CREATED="1554098090151" ID="ID_1794039749" LINK="https://mosipid.atlassian.net/browse/MOS-19886" MODIFIED="1554098101172" POSITION="right" TEXT="MOS-19886">
<node COLOR="#66cc00" CREATED="1551173708742" HGAP="9" ID="ID_486461926" MODIFIED="1554098408835" TEXT="Verify supervisor authentication is configurable" VSHIFT="-61">
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
<node COLOR="#66cc00" CREATED="1551173919163" ID="ID_1215434335" MODIFIED="1554099346783" TEXT="Verify application displays Supervisor authentication screen for registration completion after operator authentication">
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
<node COLOR="#66cc00" CREATED="1551173919163" ID="ID_760747023" MODIFIED="1554099358743" TEXT="Verify application displays Supervisor authentication screen for registration completion after operator authentication">
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
<node COLOR="#33cc00" CREATED="1554099114626" ID="ID_1229020520" MODIFIED="1554099170318" TEXT="Application with quality score below threshold after max retries">
<node COLOR="#66cc00" CREATED="1551173832731" HGAP="17" ID="ID_1128574449" MODIFIED="1551174468952" TEXT="Supervisor Authentication status" VSHIFT="-8">
<node COLOR="#66cc00" CREATED="1551173862417" HGAP="11" ID="ID_1142424759" MODIFIED="1551174468951" TEXT="ON" VSHIFT="4">
<node COLOR="#66cc00" CREATED="1551173919163" ID="ID_70574230" MODIFIED="1554099366570" TEXT="Verify application displays Supervisor authentication screen for registration completion after operator authentication after operator authentication">
<node COLOR="#66cc00" CREATED="1551173935149" ID="ID_23120355" MODIFIED="1551174468940" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1551173950116" ID="ID_456909249" MODIFIED="1551174468939" TEXT="Provide valid credentials to authenticate the packet"/>
</node>
<node COLOR="#ff6600" CREATED="1551173937169" ID="ID_261348660" MODIFIED="1551174769600" TEXT="No">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1551173992437" ID="ID_403043546" MODIFIED="1551174521839" TEXT="Raise a defect"/>
</node>
</node>
</node>
<node COLOR="#66cc00" CREATED="1551173905645" HGAP="10" ID="ID_811284950" MODIFIED="1551174468946" TEXT="OFF" VSHIFT="8">
<node COLOR="#66cc00" CREATED="1551173919163" ID="ID_1217620876" MODIFIED="1554099371860" TEXT="Verify application displays Supervisor authentication screen for registration completion after operator authentication">
<node COLOR="#ff6600" CREATED="1551173935149" ID="ID_401728091" MODIFIED="1551174769601" TEXT="Yes">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1551173992437" ID="ID_254738327" MODIFIED="1551174521838" TEXT="Raise a defect"/>
</node>
<node COLOR="#66cc00" CREATED="1551173937169" ID="ID_1072342010" MODIFIED="1551174468938" TEXT="No">
<node COLOR="#66cc00" CREATED="1551173992437" ID="ID_314065424" MODIFIED="1551174468938" TEXT="Based on EOD status packet will either get auto uploaded / moved to pending approval queue"/>
</node>
</node>
</node>
</node>
</node>
<node COLOR="#66cc00" CREATED="1551173791176" ID="ID_1660840705" MODIFIED="1551174468925" TEXT="Application without Biometric Exception">
<node COLOR="#66cc00" CREATED="1551173832731" HGAP="10" ID="ID_669640638" MODIFIED="1551174468925" TEXT="Supervisor Authentication status" VSHIFT="-7">
<node COLOR="#66cc00" CREATED="1551173862417" HGAP="10" ID="ID_1839483241" MODIFIED="1551174468926" TEXT="ON" VSHIFT="-7">
<node COLOR="#66cc00" CREATED="1551173919163" ID="ID_1751102777" MODIFIED="1554099376227" TEXT="Verify application displays Supervisor authentication screen for registration completion after operator authentication">
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
<node COLOR="#66cc00" CREATED="1551173919163" ID="ID_1200237568" MODIFIED="1554099379732" TEXT="Verify application displays Supervisor authentication screen for registration completion after operator authentication">
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
<node COLOR="#33cc00" CREATED="1554099388654" ID="ID_1223536206" MODIFIED="1554099562507" TEXT="Verify exception photo becomes mandatory for both biometric exception scenarios">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1554099423749" ID="ID_1835985162" MODIFIED="1554099562507" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1554099434294" ID="ID_1882628907" MODIFIED="1554099562507" TEXT="Capture the biometric image"/>
</node>
<node COLOR="#ff6600" CREATED="1554099426300" ID="ID_1021178303" MODIFIED="1554099579649" TEXT="No">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1554099430709" ID="ID_56441637" MODIFIED="1554099592270" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1554099467033" ID="ID_122030242" MODIFIED="1554099562507" TEXT="Reason for biometric exception">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1554099475754" ID="ID_643217500" MODIFIED="1554099562507" TEXT="Missing biometrics">
<node COLOR="#33cc00" CREATED="1554099514129" ID="ID_1859305834" MODIFIED="1554099562507" TEXT="Marked as &quot;Missing biometrics&quot;"/>
</node>
<node COLOR="#33cc00" CREATED="1554099487328" ID="ID_779873888" MODIFIED="1554099562507" TEXT="Low quality">
<node COLOR="#33cc00" CREATED="1554099498468" ID="ID_1531270321" MODIFIED="1554099562507" TEXT="Marked as &quot;Low quality biometrics&quot;"/>
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
