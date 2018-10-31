<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1538717987698" ID="ID_475687349" MODIFIED="1539168707863" TEXT="Registration - Logout">
<node COLOR="#33cc00" CREATED="1538634468756" HGAP="25" ID="ID_10857501" MODIFIED="1539330272429" POSITION="right" TEXT="Ro / Supervisor logged in with Username + Password access" VSHIFT="-13">
<edge COLOR="#00cc00"/>
<node COLOR="#33cc00" CREATED="1538635063785" HGAP="21" ID="ID_1869120666" MODIFIED="1539090150806" TEXT="Verify user is able to logout using logout button" VSHIFT="-57">
<node COLOR="#33cc00" CREATED="1538720278094" ID="ID_25925478" MODIFIED="1539090150806" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1538720278094" ID="ID_572490988" MODIFIED="1539090150806" TEXT="Display the login screen with username and password option"/>
</node>
<node COLOR="#ff6600" CREATED="1539089637435" ID="ID_218573507" MODIFIED="1539330244952" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539089688847" ID="ID_285530157" MODIFIED="1539330406352" TEXT="Display appropriate error message / Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1538718380339" HGAP="22" ID="ID_439443996" MODIFIED="1539330328001" TEXT="Verify user is able to logout by closing the client (user can verify this by re-logging in)" VSHIFT="-62">
<node COLOR="#33cc00" CREATED="1538720278094" ID="ID_1144728513" MODIFIED="1539090150806" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1538720278094" ID="ID_1806687691" MODIFIED="1539330277802" TEXT="Display the login screen with username and password option"/>
</node>
<node COLOR="#ff6600" CREATED="1539089637435" ID="ID_1573912092" MODIFIED="1539330244952" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539089688847" ID="ID_1862235239" MODIFIED="1539330406352" TEXT="Display appropriate error message / Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1538718583557" HGAP="23" ID="ID_482229417" MODIFIED="1539090150806" TEXT="Verify client gets logged out automatically when user remains idle for X minutes as configured in Admin portal" VSHIFT="-41">
<node COLOR="#33cc00" CREATED="1538720278094" ID="ID_1495556914" MODIFIED="1539090150806" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1538720278094" ID="ID_406608441" MODIFIED="1539090150806" TEXT="Display the login screen with username and password option"/>
</node>
<node COLOR="#ff6600" CREATED="1539089637435" ID="ID_615987223" MODIFIED="1539330244952" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539089688847" ID="ID_1851145102" MODIFIED="1539330406352" TEXT="Display appropriate error message / Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1538719600470" HGAP="22" ID="ID_1287259957" MODIFIED="1539090150791" TEXT="Verify user is alerted with &#x2018;x&#x2019; minutes before reaching the auto logout time limit and the same can be configured in admin portal." VSHIFT="-24">
<node COLOR="#33cc00" CREATED="1538720251094" ID="ID_1412897208" MODIFIED="1539090150806" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1538720251094" ID="ID_1934726552" MODIFIED="1539090150806" TEXT="Display &#x201c;You will be automatically logged out in [Countdown timer]. Any unsaved data will be lost. Click on OK to continue working.&#x201d;"/>
</node>
<node COLOR="#ff6600" CREATED="1539089753672" ID="ID_1808306576" MODIFIED="1539330244952" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539089688847" ID="ID_1142513555" MODIFIED="1539330406352" TEXT="Display appropriate error message / Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1538719637450" ID="ID_1816241036" MODIFIED="1539090150791" TEXT="Verify the application display a countdown timer in the alert. The user can choose to dismiss the alert and continue working. ">
<node COLOR="#33cc00" CREATED="1538720251094" ID="ID_1122201771" MODIFIED="1539090150806" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1538720251094" ID="ID_739657257" MODIFIED="1539330383135" TEXT="Reset the timer to zero (can be verified by sitting idle for X min or the machine should not get logged out as mentioned in alert message"/>
</node>
<node COLOR="#ff6600" CREATED="1539089753672" ID="ID_1661670712" MODIFIED="1539330244952" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539089688847" ID="ID_801722783" MODIFIED="1539330406352" TEXT="Display appropriate error message / Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1538720479079" HGAP="12" ID="ID_474145326" MODIFIED="1539090150806" TEXT="Verify upon logout, any unsaved data will be lost. " VSHIFT="33">
<node COLOR="#33cc00" CREATED="1539089906285" ID="ID_1884598671" MODIFIED="1539090150806" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1539089909604" ID="ID_714012816" MODIFIED="1539090150806" TEXT="Data will not be automatically saved in the database and will not be retained in memory"/>
</node>
</node>
<node COLOR="#00cc00" CREATED="1538550071542" HGAP="16" ID="ID_1167196337" MODIFIED="1539004933785" TEXT="Verification of Txn details for Audit purpose" VSHIFT="86">
<edge COLOR="#00cc00"/>
<node COLOR="#00cc00" CREATED="1539003087723" ID="ID_648252266" MODIFIED="1539060406447" TEXT="System capture all Txn details">
<node COLOR="#00cc00" CREATED="1539003122483" ID="ID_1079503882" MODIFIED="1539004902947" TEXT="Store all the details under &quot;Audit_Log&quot; table"/>
</node>
<node COLOR="#ff0000" CREATED="1539060442854" ID="ID_1951538127" MODIFIED="1539330244952" TEXT="System fails to capture Txn details">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539089688847" ID="ID_1339153807" MODIFIED="1539330406352" TEXT="Display appropriate error message / Raise a defect"/>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1538634478361" HGAP="31" ID="ID_1068162797" MODIFIED="1539330306547" POSITION="left" STYLE="fork" TEXT="Ro / Supervisor logged in with Username + OTP access" VSHIFT="-5">
<edge COLOR="#00cc00"/>
<node COLOR="#33cc00" CREATED="1538635063785" HGAP="21" ID="ID_1051600268" MODIFIED="1539090150806" TEXT="Verify user is able to logout using logout button" VSHIFT="-57">
<node COLOR="#33cc00" CREATED="1538720278094" ID="ID_238240294" MODIFIED="1539090150806" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1538720278094" ID="ID_735413990" MODIFIED="1539090238679" TEXT="Display the login screen with username and OTP option"/>
</node>
<node COLOR="#ff6600" CREATED="1539089637435" ID="ID_1527286471" MODIFIED="1539330227487" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539089688847" ID="ID_1578790423" MODIFIED="1539330406352" TEXT="Display appropriate error message / Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1538718380339" HGAP="22" ID="ID_1471981313" MODIFIED="1539322526211" TEXT="Verify user is able to logout by closing the client (user can verify this by re-logging in)" VSHIFT="-62">
<node COLOR="#33cc00" CREATED="1538720278094" ID="ID_1157237668" MODIFIED="1539090150806" TEXT="Yes">
<node COLOR="#339900" CREATED="1539263749825" ID="ID_1739164666" MODIFIED="1539263764271" TEXT="Display the login screen with username and OTP option"/>
</node>
<node COLOR="#ff6600" CREATED="1539089637435" ID="ID_1045051895" MODIFIED="1539330227487" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539089688847" ID="ID_798749250" MODIFIED="1539330406352" TEXT="Display appropriate error message / Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1538718583557" HGAP="23" ID="ID_1383900417" MODIFIED="1539090150806" TEXT="Verify client gets logged out automatically when user remains idle for X minutes as configured in Admin portal" VSHIFT="-41">
<node COLOR="#33cc00" CREATED="1538720278094" ID="ID_302374744" MODIFIED="1539090150806" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1538720278094" ID="ID_606731144" MODIFIED="1539590283376" TEXT="Display the login screen with username and OTP option and verify the default values are set again"/>
</node>
<node COLOR="#ff6600" CREATED="1539089637435" ID="ID_1236260050" MODIFIED="1539330227487" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539089688847" ID="ID_432426402" MODIFIED="1539330406352" TEXT="Display appropriate error message / Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1538719600470" HGAP="22" ID="ID_1408101140" MODIFIED="1539090150791" TEXT="Verify user is alerted with &#x2018;x&#x2019; minutes before reaching the auto logout time limit and the same can be configured in admin portal." VSHIFT="-24">
<node COLOR="#33cc00" CREATED="1538720251094" ID="ID_303235529" MODIFIED="1539090150806" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1538720251094" ID="ID_1119843760" MODIFIED="1539090150806" TEXT="Display &#x201c;You will be automatically logged out in [Countdown timer]. Any unsaved data will be lost. Click on OK to continue working.&#x201d;"/>
</node>
<node COLOR="#ff6600" CREATED="1539089753672" ID="ID_924015245" MODIFIED="1539330227487" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539089688847" ID="ID_480850779" MODIFIED="1539330406352" TEXT="Display appropriate error message / Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1538719637450" ID="ID_1347790964" MODIFIED="1539090150791" TEXT="Verify the application display a countdown timer in the alert. The user can choose to dismiss the alert and continue working. ">
<node COLOR="#33cc00" CREATED="1538720251094" ID="ID_208263027" MODIFIED="1539090150806" TEXT="Yes">
<node COLOR="#339900" CREATED="1539263896384" ID="ID_362369856" MODIFIED="1539263905968" TEXT="Reset the timer to zero (can be verified by sitting idle for X min or the machine should not get logged out as mentioned in alert message"/>
</node>
<node COLOR="#ff6600" CREATED="1539089753672" ID="ID_339721420" MODIFIED="1539330227487" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539089688847" ID="ID_782740502" MODIFIED="1539330406352" TEXT="Display appropriate error message / Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1538720479079" HGAP="12" ID="ID_1592490976" MODIFIED="1539090150806" TEXT="Verify upon logout, any unsaved data will be lost. " VSHIFT="33">
<node COLOR="#33cc00" CREATED="1539089906285" ID="ID_246409937" MODIFIED="1539090150806" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1539089909604" ID="ID_1128636186" MODIFIED="1539090150806" TEXT="Data will not be automatically saved in the database and will not be retained in memory"/>
</node>
</node>
<node COLOR="#00cc00" CREATED="1538550071542" HGAP="16" ID="ID_1717347501" MODIFIED="1539004933785" TEXT="Verification of Txn details for Audit purpose" VSHIFT="86">
<edge COLOR="#00cc00"/>
<node COLOR="#00cc00" CREATED="1539003087723" ID="ID_1590151171" MODIFIED="1539060406447" TEXT="System capture all Txn details">
<node COLOR="#00cc00" CREATED="1539003122483" ID="ID_600837917" MODIFIED="1539004902947" TEXT="Store all the details under &quot;Audit_Log&quot; table"/>
</node>
<node COLOR="#ff0000" CREATED="1539060442854" ID="ID_572458533" MODIFIED="1539330227487" TEXT="System fails to capture Txn details">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539089688847" ID="ID_417566809" MODIFIED="1539330406352" TEXT="Display appropriate error message / Raise a defect"/>
</node>
</node>
</node>
</node>
</map>
