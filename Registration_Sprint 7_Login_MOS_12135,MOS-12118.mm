<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1550383172580" ID="ID_630168907" MODIFIED="1550386793188" TEXT="Registration - Login">
<node CREATED="1550383193925" ID="ID_985469848" LINK="https://mosipid.atlassian.net/browse/MOS-12135" MODIFIED="1550384829799" POSITION="right" TEXT="MOS-12135">
<node CREATED="1550383206589" ID="ID_1244537693" MODIFIED="1550384853124" TEXT="Configured login Mode">
<edge COLOR="#66ff00"/>
<node COLOR="#66cc00" CREATED="1550383220044" HGAP="12" ID="ID_212812722" MODIFIED="1550384967721" TEXT="IRIS" VSHIFT="-10">
<node COLOR="#66cc00" CREATED="1550383388466" HGAP="7" ID="ID_1341000207" MODIFIED="1550384967720" TEXT="Enter username and click submit after placing one IRIS on device" VSHIFT="-119">
<node COLOR="#66cc00" CREATED="1550383592629" HGAP="19" ID="ID_656080102" MODIFIED="1550384967720" TEXT="IRIS match found" VSHIFT="-29">
<node COLOR="#66cc00" CREATED="1550383683136" ID="ID_323553133" MODIFIED="1550384967719" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1550383764602" ID="ID_1266125883" MODIFIED="1550384967718" TEXT="Display home screen based on role"/>
</node>
<node COLOR="#ff9900" CREATED="1550383691060" ID="ID_402791095" MODIFIED="1550385010607" TEXT="No">
<node COLOR="#ff6600" CREATED="1550383736422" ID="ID_967174252" MODIFIED="1550385042045" TEXT="Display &quot;IRIS match not found&quot; and display the same screen to re-capture upto 5 times and store the invalid attempts in DB">
<node COLOR="#ff9900" CREATED="1550384283709" ID="ID_1933422051" MODIFIED="1550385010606" TEXT="Invalid attempts  &gt; 5">
<node COLOR="#ff9900" CREATED="1550384314941" ID="ID_1784212772" MODIFIED="1550385010605" TEXT="Yes">
<node COLOR="#ff6600" CREATED="1550384326068" ID="ID_1783833255" MODIFIED="1550385042057" TEXT="Temp lock the acc for 30 mins"/>
</node>
<node COLOR="#66cc00" CREATED="1550384320466" ID="ID_1954233634" MODIFIED="1550384967716" TEXT="No">
<node COLOR="#66cc00" CREATED="1550384337147" ID="ID_697577877" MODIFIED="1550384967716" TEXT="Allow the user to retry the login with IRIS"/>
</node>
</node>
</node>
</node>
</node>
<node COLOR="#66cc00" CREATED="1550383777642" ID="ID_1822779403" MODIFIED="1550384967715" TEXT="Username match found">
<node COLOR="#66cc00" CREATED="1550383683136" ID="ID_131917620" MODIFIED="1550384967715" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1550383764602" ID="ID_1827675300" MODIFIED="1550384967715" TEXT="Display home screen based on role after IRIS matched"/>
</node>
<node COLOR="#ff9900" CREATED="1550383691060" ID="ID_1857354797" MODIFIED="1550385010607" TEXT="No">
<node COLOR="#ff6600" CREATED="1550383736422" ID="ID_972490921" MODIFIED="1550385042050" TEXT="Display &quot;You have not been onboarded to use this client&quot;"/>
</node>
</node>
<node COLOR="#66cc00" CREATED="1550384059013" HGAP="24" ID="ID_549954290" MODIFIED="1550384967714" TEXT="Device onboarded" VSHIFT="21">
<node COLOR="#66cc00" CREATED="1550383683136" ID="ID_1561556653" MODIFIED="1550384967711" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1550383764602" ID="ID_1612324043" MODIFIED="1550384967711" TEXT="Display home screen based on role based on username and IRIS validation"/>
</node>
<node COLOR="#ff9900" CREATED="1550383691060" ID="ID_1775781971" MODIFIED="1550385010606" TEXT="No">
<node COLOR="#ff6600" CREATED="1550383736422" ID="ID_207158122" MODIFIED="1550385042050" TEXT="Display &quot;Iris device not found &quot;"/>
</node>
</node>
<node COLOR="#66cc00" CREATED="1550384180562" HGAP="19" ID="ID_1809526801" MODIFIED="1550384967714" TEXT="More than one device found" VSHIFT="23">
<node COLOR="#66cc00" CREATED="1550383683136" ID="ID_462560914" MODIFIED="1550384967711" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1550383764602" ID="ID_1121189352" MODIFIED="1550384967711" TEXT="Proceed with the first iris capture device that the system finds as it scans the ports of the machine."/>
</node>
</node>
<node COLOR="#66cc00" CREATED="1550384424971" HGAP="21" ID="ID_1128315440" MODIFIED="1550384967714" TEXT="Blacklisted user" VSHIFT="34">
<node CREATED="1550383683136" ID="ID_1721763531" MODIFIED="1550383686592" TEXT="Yes">
<node COLOR="#ff6600" CREATED="1550383764602" ID="ID_1791273452" MODIFIED="1550385042051" TEXT="Display &#x201c;You are not authorized to perform registration.&#x201d;  "/>
</node>
<node COLOR="#ff9900" CREATED="1550383691060" ID="ID_1398475430" MODIFIED="1550385010607" TEXT="No">
<node COLOR="#66cc00" CREATED="1550383736422" ID="ID_1598214148" MODIFIED="1550384967713" TEXT="Display home screen based on role after IRIS matched "/>
</node>
</node>
<node COLOR="#66cc00" CREATED="1550384482964" HGAP="8" ID="ID_1482875482" MODIFIED="1550384967713" TEXT="Quality score for captured Iris" VSHIFT="37">
<node COLOR="#66cc00" CREATED="1550384508687" ID="ID_1981448140" MODIFIED="1550384967712" TEXT="Met threshold">
<node COLOR="#66cc00" CREATED="1550383683136" ID="ID_1323157027" MODIFIED="1550384967712" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1550383764602" ID="ID_1081903266" MODIFIED="1550384967712" TEXT="Display home screen based on role after IRIS matched"/>
</node>
<node COLOR="#ff9900" CREATED="1550384320466" ID="ID_1582953249" MODIFIED="1550385010608" TEXT="No">
<node COLOR="#ff6600" CREATED="1550384337147" ID="ID_210404417" MODIFIED="1550385042051" TEXT="Allow the user to retry the login with IRIS and should not lock acc for low score"/>
</node>
</node>
</node>
<node COLOR="#00cc00" CREATED="1538550071542" HGAP="9" ID="ID_795247067" MODIFIED="1546498495098" TEXT="Verification of Txn details for Audit purpose" VSHIFT="34">
<edge COLOR="#00cc00"/>
<node COLOR="#00cc00" CREATED="1539003087723" ID="ID_147614259" MODIFIED="1542005044938" TEXT="System capture all Txn details">
<node COLOR="#00cc00" CREATED="1539003122483" ID="ID_924179561" MODIFIED="1546498515692" TEXT="Store all the details under &quot;Audit_Log&quot; table such as User id or system account; Machine Details; Event Name; Application Name, and Event data including user entered fields."/>
</node>
<node COLOR="#ff0000" CREATED="1539060442854" ID="ID_1665046343" MODIFIED="1542005044938" TEXT="System fails to capture Txn details">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539089688847" ID="ID_1055579965" MODIFIED="1542005044938" TEXT="Display appropriate error message / Raise a defect"/>
</node>
</node>
</node>
</node>
<node CREATED="1550383581635" HGAP="16" ID="ID_923721582" MODIFIED="1550383633568" TEXT="FACE" VSHIFT="35">
<node COLOR="#66cc00" CREATED="1550383388466" HGAP="7" ID="ID_1638252970" MODIFIED="1550384967710" TEXT="Enter username and click submit after capturing face using camera" VSHIFT="-119">
<node COLOR="#66cc00" CREATED="1550383592629" HGAP="19" ID="ID_897620263" MODIFIED="1550384967710" TEXT="FACE match found" VSHIFT="-29">
<node COLOR="#66cc00" CREATED="1550383683136" ID="ID_861341892" MODIFIED="1550384967710" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1550383764602" ID="ID_1563323599" MODIFIED="1550384967709" TEXT="Display home screen based on role"/>
</node>
<node COLOR="#ff9900" CREATED="1550383691060" ID="ID_307668448" MODIFIED="1550385010610" TEXT="No">
<node COLOR="#ff6600" CREATED="1550383736422" ID="ID_1241183053" MODIFIED="1550385042051" TEXT="Display &quot;FACE match not found&quot; and display the same screen to re-capture upto 5 times and store the invalid attempts in DB">
<node COLOR="#ff9900" CREATED="1550384283709" ID="ID_876119007" MODIFIED="1550385010610" TEXT="Invalid attempts  &gt; 5">
<node COLOR="#ff9900" CREATED="1550384314941" ID="ID_1186106045" MODIFIED="1550385010609" TEXT="Yes">
<node COLOR="#ff6600" CREATED="1550384326068" ID="ID_1952768689" MODIFIED="1550385042056" TEXT="Temp lock the acc for 30 mins"/>
</node>
<node COLOR="#66cc00" CREATED="1550384320466" ID="ID_1482995790" MODIFIED="1550384967702" TEXT="No">
<node COLOR="#66cc00" CREATED="1550384337147" ID="ID_847689266" MODIFIED="1550384967700" TEXT="Allow the user to retry the login with FACE"/>
</node>
</node>
</node>
</node>
</node>
<node COLOR="#66cc00" CREATED="1550383777642" ID="ID_271708335" MODIFIED="1550384967709" TEXT="Username match found">
<node COLOR="#66cc00" CREATED="1550383683136" ID="ID_1783917682" MODIFIED="1550384967709" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1550383764602" ID="ID_243419460" MODIFIED="1550384967708" TEXT="Display home screen based on role after FACE matched"/>
</node>
<node COLOR="#ff9900" CREATED="1550383691060" ID="ID_1332560169" MODIFIED="1550385010610" TEXT="No">
<node COLOR="#ff6600" CREATED="1550383736422" ID="ID_834820395" MODIFIED="1550385042044" TEXT="Display &quot;You have not been onboarded to use this client&quot;"/>
</node>
</node>
<node COLOR="#66cc00" CREATED="1550384059013" HGAP="24" ID="ID_1386077101" MODIFIED="1550384967708" TEXT="Device onboarded" VSHIFT="21">
<node COLOR="#66cc00" CREATED="1550383683136" ID="ID_1193700802" MODIFIED="1550384967708" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1550383764602" ID="ID_1808210422" MODIFIED="1550384967707" TEXT="Display home screen based on role based on username and FACE validation"/>
</node>
<node COLOR="#ff9900" CREATED="1550383691060" ID="ID_939858113" MODIFIED="1550385010611" TEXT="No">
<node COLOR="#ff6600" CREATED="1550383736422" ID="ID_360077326" MODIFIED="1550385042044" TEXT="Display &quot;Camera not found &quot;"/>
</node>
</node>
<node COLOR="#66cc00" CREATED="1550384180562" HGAP="19" ID="ID_198770624" MODIFIED="1550384967707" TEXT="More than one device found" VSHIFT="23">
<node COLOR="#66cc00" CREATED="1550383683136" ID="ID_998136459" MODIFIED="1550384967707" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1550383764602" ID="ID_832668918" MODIFIED="1550384967706" TEXT="Proceed with the first camera that the system finds as it scans the ports of the machine."/>
</node>
</node>
<node COLOR="#66cc00" CREATED="1550384424971" HGAP="21" ID="ID_94914690" MODIFIED="1550384967706" TEXT="Blacklisted user" VSHIFT="34">
<node COLOR="#ff9900" CREATED="1550383683136" ID="ID_193244829" MODIFIED="1550385010611" TEXT="Yes">
<node COLOR="#ff6600" CREATED="1550383764602" ID="ID_647954392" MODIFIED="1550385042044" TEXT="Display &#x201c;You are not authorized to perform registration.&#x201d;  "/>
</node>
<node COLOR="#66cc00" CREATED="1550383691060" ID="ID_1981743896" MODIFIED="1550384967706" TEXT="No">
<node COLOR="#66cc00" CREATED="1550383736422" ID="ID_562697960" MODIFIED="1550384967705" TEXT="Display home screen based on role after IRIS matched "/>
</node>
</node>
<node COLOR="#66cc00" CREATED="1550384482964" HGAP="8" ID="ID_740603310" MODIFIED="1550384967705" TEXT="Quality score for captured Iris" VSHIFT="37">
<node COLOR="#66cc00" CREATED="1550384508687" ID="ID_42059597" MODIFIED="1550384967704" TEXT="Met threshold">
<node COLOR="#66cc00" CREATED="1550383683136" ID="ID_1878000297" MODIFIED="1550384967703" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1550383764602" ID="ID_128340150" MODIFIED="1550384967703" TEXT="Display home screen based on role after FACE matched"/>
</node>
<node COLOR="#ff9900" CREATED="1550384320466" ID="ID_1400573922" MODIFIED="1550385010611" TEXT="No">
<node COLOR="#ff6600" CREATED="1550384337147" ID="ID_1063717386" MODIFIED="1550385042043" TEXT="Allow the user to retry the login with FACE and should not lock acc for low score"/>
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
</node>
</node>
<node COLOR="#33cc00" CREATED="1538634468756" HGAP="33" ID="ID_835595492" MODIFIED="1542005439648" POSITION="left" STYLE="fork" TEXT="Ro / Supervisor logged in with Username + Password access - can be configured in admin portal" VSHIFT="131">
<node COLOR="#33cc00" CREATED="1538635063785" HGAP="2" ID="ID_907681410" MODIFIED="1542005263204" TEXT="Display password based login page" VSHIFT="-112">
<node COLOR="#33cc00" CREATED="1538635119762" ID="ID_1354168580" MODIFIED="1542005044922" TEXT=";">
<node COLOR="#33cc00" CREATED="1538635640418" HGAP="71" ID="ID_1838345890" MODIFIED="1542005251446" TEXT="Valid username and password" VSHIFT="35">
<node COLOR="#33cc00" CREATED="1538637915123" ID="ID_696930340" MODIFIED="1542005044922" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1539068953567" ID="ID_1292178457" MODIFIED="1542005044922" TEXT="&quot;Display Home page&quot;"/>
</node>
<node COLOR="#ff6600" CREATED="1539068957510" ID="ID_1224202991" MODIFIED="1542005044922" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539068966693" ID="ID_33066514" MODIFIED="1542005044922" TEXT="Display appropriate error message"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1539069046577" HGAP="70" ID="ID_616734290" MODIFIED="1542005257930" TEXT="Login with Invalid password" VSHIFT="18">
<node COLOR="#33cc00" CREATED="1539068843439" ID="ID_850602261" MODIFIED="1542005044922" TEXT="No">
<node COLOR="#33cc00" CREATED="1539068872840" ID="ID_1568567653" MODIFIED="1542005044922" TEXT="Display &quot;Home page&quot;"/>
</node>
<node COLOR="#ff6600" CREATED="1538635546306" HGAP="18" ID="ID_1984963153" MODIFIED="1542005044922" TEXT="Yes" VSHIFT="-14">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1538637930387" ID="ID_1531817917" MODIFIED="1542005609857" TEXT="Display &apos;Incorrect Password&quot; and set the count of invalid login = 1. The count gets increased for every invalid attempt"/>
</node>
</node>
<node COLOR="#00cc00" CREATED="1542004360480" HGAP="96" ID="ID_583298599" MODIFIED="1552037203658" TEXT="User tried to login with invalid password &gt;= 5 times" VSHIFT="18">
<node COLOR="#33cc00" CREATED="1539068843439" ID="ID_669487648" MODIFIED="1542005044922" TEXT="No">
<node COLOR="#33cc00" CREATED="1539068872840" ID="ID_1272610541" MODIFIED="1542005044922" TEXT="Display &quot;Login page&quot;"/>
</node>
<node COLOR="#ff6600" CREATED="1538635546306" HGAP="18" ID="ID_1243197174" MODIFIED="1542005044922" TEXT="Yes" VSHIFT="-14">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1538637930387" ID="ID_698555284" MODIFIED="1542005044922" TEXT="Display &apos;&#x201c;Your account has been temporarily locked as you have made 5 unsuccessful login attempts. Please try logging in after 30 minutes.&#x201d; "/>
</node>
</node>
<node COLOR="#00cc00" CREATED="1542004570856" HGAP="108" ID="ID_1543600331" MODIFIED="1542005246043" TEXT="Try login again with locked account before 30 mins" VSHIFT="21">
<node COLOR="#ff6600" CREATED="1538635546306" HGAP="18" ID="ID_1704675422" MODIFIED="1542005044922" TEXT="Yes" VSHIFT="3">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1538637930387" ID="ID_72313197" MODIFIED="1542005044922" TEXT="Display &apos;&#x201c;Your account has been temporarily locked as you have made 5 unsuccessful login attempts. Please try logging in after 30 minutes.&#x201d; "/>
</node>
</node>
<node COLOR="#00cc00" CREATED="1542004714307" HGAP="110" ID="ID_805432931" MODIFIED="1542005247884" TEXT="Try login again with locked account after 30 mins" VSHIFT="5">
<node COLOR="#33cc00" CREATED="1539068843439" ID="ID_420256155" MODIFIED="1542005044938" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1539068872840" ID="ID_105167878" MODIFIED="1542005044938" TEXT="Display &quot;Login page&quot; and reset the count of invalid login attempts to 0."/>
</node>
</node>
<node COLOR="#00cc00" CREATED="1538550071542" HGAP="112" ID="ID_318974386" MODIFIED="1542005236742" TEXT="Verification of Txn details for Audit purpose" VSHIFT="19">
<edge COLOR="#00cc00"/>
<node COLOR="#00cc00" CREATED="1539003087723" ID="ID_26594101" MODIFIED="1542005044938" TEXT="System capture all Txn details">
<node COLOR="#00cc00" CREATED="1539003122483" ID="ID_1933929321" MODIFIED="1542005044938" TEXT="Store all the details under &quot;Audit_Log&quot; table"/>
</node>
<node COLOR="#ff0000" CREATED="1539060442854" ID="ID_453835978" MODIFIED="1542005044938" TEXT="System fails to capture Txn details">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539089688847" ID="ID_739645953" MODIFIED="1542005044938" TEXT="Display appropriate error message / Raise a defect"/>
</node>
</node>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1538634468756" HGAP="79" ID="ID_387013996" MODIFIED="1542005432725" POSITION="left" STYLE="fork" TEXT="Ro / Supervisor logged in with Username + OTP access - can be configured in admin portal" VSHIFT="64">
<node COLOR="#33cc00" CREATED="1538635063785" HGAP="2" ID="ID_992510091" MODIFIED="1542005305383" TEXT="Display OTP based login page" VSHIFT="-112">
<node COLOR="#33cc00" CREATED="1538635119762" ID="ID_1406461104" MODIFIED="1542005044922" TEXT=";">
<node COLOR="#33cc00" CREATED="1538635640418" HGAP="71" ID="ID_218401053" MODIFIED="1542005312616" TEXT="Valid username and OTP" VSHIFT="35">
<node COLOR="#33cc00" CREATED="1538637915123" ID="ID_928741984" MODIFIED="1542005044922" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1539068953567" ID="ID_1245005356" MODIFIED="1542005044922" TEXT="&quot;Display Home page&quot;"/>
</node>
<node COLOR="#ff6600" CREATED="1539068957510" ID="ID_1869585665" MODIFIED="1542005044922" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539068966693" ID="ID_1532603233" MODIFIED="1542005044922" TEXT="Display appropriate error message"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1539069046577" HGAP="70" ID="ID_1430217988" MODIFIED="1542005320192" TEXT="Login with Invalid OTP" VSHIFT="18">
<node COLOR="#33cc00" CREATED="1539068843439" ID="ID_313082667" MODIFIED="1542005044922" TEXT="No">
<node COLOR="#33cc00" CREATED="1539068872840" ID="ID_89863324" MODIFIED="1542005044922" TEXT="Display &quot;Home page&quot;"/>
</node>
<node COLOR="#ff6600" CREATED="1538635546306" HGAP="18" ID="ID_1883832082" MODIFIED="1542005044922" TEXT="Yes" VSHIFT="-14">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1538637930387" ID="ID_76231208" MODIFIED="1542005602912" TEXT="Display &apos;Incorrect OTP&quot; and set the count of invalid login = 1.The count gets increased for every invalid attempt"/>
</node>
</node>
<node COLOR="#00cc00" CREATED="1542004360480" HGAP="96" ID="ID_1843642310" MODIFIED="1552037205183" TEXT="User tried to login with invalid OTP &gt;= 5 times" VSHIFT="18">
<node COLOR="#33cc00" CREATED="1539068843439" ID="ID_939074686" MODIFIED="1542005044922" TEXT="No">
<node COLOR="#33cc00" CREATED="1539068872840" ID="ID_542153096" MODIFIED="1542005044922" TEXT="Display &quot;Login page&quot;"/>
</node>
<node COLOR="#ff6600" CREATED="1538635546306" HGAP="18" ID="ID_1350211356" MODIFIED="1542005044922" TEXT="Yes" VSHIFT="-14">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1538637930387" ID="ID_109723176" MODIFIED="1542005044922" TEXT="Display &apos;&#x201c;Your account has been temporarily locked as you have made 5 unsuccessful login attempts. Please try logging in after 30 minutes.&#x201d; "/>
</node>
</node>
<node COLOR="#00cc00" CREATED="1542004570856" HGAP="108" ID="ID_1836495176" MODIFIED="1542005246043" TEXT="Try login again with locked account before 30 mins" VSHIFT="21">
<node COLOR="#ff6600" CREATED="1538635546306" HGAP="18" ID="ID_737018669" MODIFIED="1542005044922" TEXT="Yes" VSHIFT="3">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1538637930387" ID="ID_1928428992" MODIFIED="1542005044922" TEXT="Display &apos;&#x201c;Your account has been temporarily locked as you have made 5 unsuccessful login attempts. Please try logging in after 30 minutes.&#x201d; "/>
</node>
</node>
<node COLOR="#00cc00" CREATED="1542004714307" HGAP="110" ID="ID_1800753157" MODIFIED="1542005247884" TEXT="Try login again with locked account after 30 mins" VSHIFT="5">
<node COLOR="#33cc00" CREATED="1539068843439" ID="ID_618383467" MODIFIED="1542005044938" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1539068872840" ID="ID_1805417728" MODIFIED="1542005044938" TEXT="Display &quot;Login page&quot; and reset the count of invalid login attempts to 0."/>
</node>
</node>
<node COLOR="#00cc00" CREATED="1538550071542" HGAP="112" ID="ID_1282327894" MODIFIED="1542005236742" TEXT="Verification of Txn details for Audit purpose" VSHIFT="19">
<edge COLOR="#00cc00"/>
<node COLOR="#00cc00" CREATED="1539003087723" ID="ID_548853522" MODIFIED="1542005044938" TEXT="System capture all Txn details">
<node COLOR="#00cc00" CREATED="1539003122483" ID="ID_1086814999" MODIFIED="1542005044938" TEXT="Store all the details under &quot;Audit_Log&quot; table"/>
</node>
<node COLOR="#ff0000" CREATED="1539060442854" ID="ID_1200248677" MODIFIED="1542005044938" TEXT="System fails to capture Txn details">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539089688847" ID="ID_1008217879" MODIFIED="1542005044938" TEXT="Display appropriate error message / Raise a defect"/>
</node>
</node>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1538634468756" HGAP="198" ID="ID_1980749879" MODIFIED="1542005467467" POSITION="left" STYLE="fork" TEXT="RO / Supervisor logged in with Username + Biometric access - can be configured in admin portal" VSHIFT="-84">
<node COLOR="#33cc00" CREATED="1538635063785" HGAP="2" ID="ID_1192525556" MODIFIED="1542005477267" TEXT="Display Biometric based login page" VSHIFT="-112">
<node COLOR="#33cc00" CREATED="1538635119762" ID="ID_239144638" MODIFIED="1542005044922" TEXT=";">
<node COLOR="#33cc00" CREATED="1538635640418" HGAP="71" ID="ID_833024103" MODIFIED="1542005489914" TEXT="Valid username and Biometric" VSHIFT="35">
<node COLOR="#33cc00" CREATED="1538637915123" ID="ID_1771454500" MODIFIED="1542005044922" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1539068953567" ID="ID_399774979" MODIFIED="1542005044922" TEXT="&quot;Display Home page&quot;"/>
</node>
<node COLOR="#ff6600" CREATED="1539068957510" ID="ID_1743370204" MODIFIED="1542005044922" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539068966693" ID="ID_1734183705" MODIFIED="1542005044922" TEXT="Display appropriate error message"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1539069046577" HGAP="70" ID="ID_1817960262" MODIFIED="1542005503084" TEXT="Login with Invalid Biometric" VSHIFT="18">
<node COLOR="#33cc00" CREATED="1539068843439" ID="ID_621906743" MODIFIED="1542005044922" TEXT="No">
<node COLOR="#33cc00" CREATED="1539068872840" ID="ID_1174632843" MODIFIED="1542005044922" TEXT="Display &quot;Home page&quot;"/>
</node>
<node COLOR="#ff6600" CREATED="1538635546306" HGAP="18" ID="ID_1839204860" MODIFIED="1542005044922" TEXT="Yes" VSHIFT="-14">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1538637930387" ID="ID_906615106" MODIFIED="1542005593366" TEXT="Display appropriate alert message and set the count of invalid login = 1. The count gets increased for every invalid attempt"/>
</node>
</node>
<node COLOR="#00cc00" CREATED="1542004360480" HGAP="96" ID="ID_1574149187" MODIFIED="1552037206447" TEXT="User tried to login with invalid Biometric  &gt;= 5 times" VSHIFT="18">
<node COLOR="#33cc00" CREATED="1539068843439" ID="ID_167241346" MODIFIED="1542005044922" TEXT="No">
<node COLOR="#33cc00" CREATED="1539068872840" ID="ID_339243951" MODIFIED="1542005044922" TEXT="Display &quot;Login page&quot;"/>
</node>
<node COLOR="#ff6600" CREATED="1538635546306" HGAP="18" ID="ID_663204635" MODIFIED="1542005044922" TEXT="Yes" VSHIFT="-14">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1538637930387" ID="ID_474077573" MODIFIED="1542005044922" TEXT="Display &apos;&#x201c;Your account has been temporarily locked as you have made 5 unsuccessful login attempts. Please try logging in after 30 minutes.&#x201d; "/>
</node>
</node>
<node COLOR="#00cc00" CREATED="1542004570856" HGAP="108" ID="ID_571627172" MODIFIED="1542005246043" TEXT="Try login again with locked account before 30 mins" VSHIFT="21">
<node COLOR="#ff6600" CREATED="1538635546306" HGAP="18" ID="ID_668557979" MODIFIED="1542005044922" TEXT="Yes" VSHIFT="3">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1538637930387" ID="ID_1798015321" MODIFIED="1542005044922" TEXT="Display &apos;&#x201c;Your account has been temporarily locked as you have made 5 unsuccessful login attempts. Please try logging in after 30 minutes.&#x201d; "/>
</node>
</node>
<node COLOR="#00cc00" CREATED="1542004714307" HGAP="110" ID="ID_1234043457" MODIFIED="1542005247884" TEXT="Try login again with locked account after 30 mins" VSHIFT="5">
<node COLOR="#33cc00" CREATED="1539068843439" ID="ID_95080835" MODIFIED="1542005044938" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1539068872840" ID="ID_1867739197" MODIFIED="1542005044938" TEXT="Display &quot;Login page&quot; and reset the count of invalid login attempts to 0."/>
</node>
</node>
<node COLOR="#00cc00" CREATED="1538550071542" HGAP="112" ID="ID_569492131" MODIFIED="1542005236742" TEXT="Verification of Txn details for Audit purpose" VSHIFT="19">
<edge COLOR="#00cc00"/>
<node COLOR="#00cc00" CREATED="1539003087723" ID="ID_520354066" MODIFIED="1542005044938" TEXT="System capture all Txn details">
<node COLOR="#00cc00" CREATED="1539003122483" ID="ID_426336363" MODIFIED="1542005044938" TEXT="Store all the details under &quot;Audit_Log&quot; table"/>
</node>
<node COLOR="#ff0000" CREATED="1539060442854" ID="ID_1481608399" MODIFIED="1542005044938" TEXT="System fails to capture Txn details">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539089688847" ID="ID_371600325" MODIFIED="1542005044938" TEXT="Display appropriate error message / Raise a defect"/>
</node>
</node>
</node>
</node>
</node>
</node>
</map>
