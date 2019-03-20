<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1552578365295" ID="ID_878655213" MODIFIED="1552578394024" TEXT="Registration - Notification">
<node CREATED="1552578395606" ID="ID_1242252474" LINK="https://mosipid.atlassian.net/browse/MOS-12989" MODIFIED="1552636185437" POSITION="right" TEXT="MOS-12989">
<edge COLOR="#33cc00"/>
<node COLOR="#66cc00" CREATED="1551174681349" HGAP="12" ID="ID_1848560293" MODIFIED="1552633865059" TEXT="Send Email / SMS in ack as configurable parameter" VSHIFT="-49">
<node COLOR="#66cc00" CREATED="1551173723578" ID="ID_343609622" MODIFIED="1551174468954" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1551173735708" ID="ID_169409031" MODIFIED="1552632897496" TEXT="Set the parameter &quot;mosip.registration.send_notification_disable_flagmosip.registration.send_notification_disable_flag&quot; to Y / N based on center requirementmo"/>
</node>
<node COLOR="#ff6600" CREATED="1551173726033" ID="ID_622846585" MODIFIED="1551175152204" TEXT="No">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1551173729731" ID="ID_1225650615" MODIFIED="1551174521839" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1552632902160" ID="ID_614764431" MODIFIED="1552636304020" TEXT="Send Email / SMS off">
<node COLOR="#33cc00" CREATED="1552635682414" HGAP="17" ID="ID_181568885" MODIFIED="1552636304020" TEXT="Yes" VSHIFT="17">
<node COLOR="#33cc00" CREATED="1552635691449" ID="ID_1158071274" MODIFIED="1552636304019" TEXT="Ack screen should not display the button to send notification to additional recipients">
<node COLOR="#33cc00" CREATED="1552635745886" HGAP="18" ID="ID_167337278" MODIFIED="1552636304019" TEXT="Yes" VSHIFT="-8">
<node COLOR="#33cc00" CREATED="1552635768011" ID="ID_1459547262" MODIFIED="1552636304019" TEXT="Proceed with New Registration / print the current acknowledgement"/>
</node>
<node COLOR="#ff3300" CREATED="1552635747842" HGAP="19" ID="ID_720823095" MODIFIED="1552636330285" TEXT="No" VSHIFT="20">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1552635788613" ID="ID_1491890439" MODIFIED="1552636345364" TEXT="Raise a defect"/>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1552635685063" HGAP="17" ID="ID_510410950" MODIFIED="1552636304020" TEXT="No" VSHIFT="-13">
<node COLOR="#33cc00" CREATED="1552635691449" ID="ID_1263741790" MODIFIED="1552636304019" TEXT="Ack screen should display the button to send notification to additional recipients">
<node COLOR="#33cc00" CREATED="1552635745886" HGAP="18" ID="ID_1637605166" MODIFIED="1552636304018" TEXT="Yes" VSHIFT="-8">
<node COLOR="#33cc00" CREATED="1552635768011" ID="ID_1160977253" MODIFIED="1552636304018" TEXT="Verify whether user able to send SMS and Email to additional recipients"/>
</node>
<node COLOR="#ff3300" CREATED="1552635747842" HGAP="19" ID="ID_1870715649" MODIFIED="1552636330284" TEXT="No" VSHIFT="20">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1552635788613" ID="ID_775148787" MODIFIED="1552636345364" TEXT="Raise a defect"/>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1552635859237" ID="ID_1933685623" MODIFIED="1552636304020" TEXT="Recipient added is &gt;5">
<node COLOR="#33cc00" CREATED="1552635891935" ID="ID_1622577502" MODIFIED="1552636304018" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1552635910352" HGAP="13" ID="ID_99323469" MODIFIED="1552636304015" TEXT="Display &#x201c;Maximum recipients exceeded. You can send the acknowledgement to a maximum of 5 email addresses and 5 phone numbers only.&#x201d; error message separately for SMS and Email" VSHIFT="-15"/>
</node>
<node COLOR="#33cc00" CREATED="1552635893580" HGAP="24" ID="ID_1868483301" MODIFIED="1552636304014" TEXT="No" VSHIFT="10">
<node COLOR="#33cc00" CREATED="1552635942739" ID="ID_1150160591" MODIFIED="1552636304014" TEXT="Machine Online">
<node COLOR="#33cc00" CREATED="1552635957781" HGAP="18" ID="ID_13384363" MODIFIED="1552636304014" TEXT="Yes" VSHIFT="-12">
<node COLOR="#33cc00" CREATED="1552635969293" ID="ID_1463248359" MODIFIED="1552636304014" TEXT="Valid email and sms">
<node COLOR="#33cc00" CREATED="1552635987114" ID="ID_479565740" MODIFIED="1552636304012" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1552636011810" ID="ID_1026294616" MODIFIED="1552636482596" TEXT="Send the notification template as configured in DB to both email and SMS for all the id individually if it has duplicate address with individual name in &quot;To&quot; and not in &quot;CC/BCC&quot;"/>
</node>
<node COLOR="#ff3300" CREATED="1552635988420" ID="ID_1310228502" MODIFIED="1552636330284" TEXT="No">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1552635993811" ID="ID_221623780" MODIFIED="1552636345363" TEXT="Display appropriate alert message"/>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1552635959959" HGAP="17" ID="ID_245345790" MODIFIED="1552636304013" TEXT="No" VSHIFT="20">
<node COLOR="#33cc00" CREATED="1552635969293" ID="ID_467320205" MODIFIED="1552636304014" TEXT="Valid email and sms">
<node COLOR="#33cc00" CREATED="1552635987114" ID="ID_1855438409" MODIFIED="1552636304013" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1552636011810" ID="ID_846320537" MODIFIED="1552636304013" TEXT="Display that SMS / Email could not be sent alert message"/>
</node>
<node COLOR="#ff3300" CREATED="1552635988420" ID="ID_803573693" MODIFIED="1552636330284" TEXT="No">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1552635993811" ID="ID_1139414633" MODIFIED="1552636345363" TEXT="Display appropriate alert message"/>
</node>
</node>
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
