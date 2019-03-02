<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1551331761184" ID="ID_1339460245" MODIFIED="1551333520329" TEXT="Registration - Fingerprint flag">
<node CREATED="1551173599876" ID="ID_1544232411" LINK="https://mosipid.atlassian.net/browse/MOS-13659" MODIFIED="1551331788495" POSITION="right" TEXT="MOS-13624">
<edge COLOR="#66cc00"/>
<node COLOR="#66cc00" CREATED="1551173708742" HGAP="14" ID="ID_486461926" MODIFIED="1551332377742" TEXT="Verify fingerprint capture is configurable" VSHIFT="-27">
<node COLOR="#66cc00" CREATED="1551173723578" ID="ID_343609622" MODIFIED="1551174468954" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1551173735708" ID="ID_169409031" MODIFIED="1551331864793" TEXT="Set the parameter &quot;FINGERPRINT_DISABLE_FLAG&quot; to Yes / No based on center requirement"/>
</node>
<node COLOR="#ff6600" CREATED="1551173726033" ID="ID_622846585" MODIFIED="1551333328380" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff3300" CREATED="1551173729731" ID="ID_1225650615" MODIFIED="1551174521839" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#66cc00" CREATED="1551332344612" HGAP="18" ID="ID_1915978356" MODIFIED="1551333237711" TEXT="Fingerprint flag turn off" VSHIFT="-63">
<node COLOR="#66cc00" CREATED="1551332365852" HGAP="15" ID="ID_1154295718" MODIFIED="1551333237726" TEXT="Login Mode is only fingerprint" VSHIFT="-29">
<node COLOR="#ff6600" CREATED="1551332402742" ID="ID_489063477" MODIFIED="1551333328396" TEXT="Yes">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1551332419371" HGAP="22" ID="ID_778760460" MODIFIED="1551333352254" TEXT="Display &#x201c;The mode of authentication is not supported by the process level configuration. Please contact the administrator.&#x201d; error message" VSHIFT="-5"/>
</node>
<node COLOR="#66cc00" CREATED="1551332404300" ID="ID_593713852" MODIFIED="1551333237726" TEXT="No">
<node COLOR="#66cc00" CREATED="1551332431377" ID="ID_1067111316" MODIFIED="1551333237726" TEXT="Skip the fingerprint validation and other mode credentials are valid" VSHIFT="4">
<node COLOR="#66cc00" CREATED="1551332485211" ID="ID_303545850" MODIFIED="1551333237726" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1551332490116" ID="ID_290099853" MODIFIED="1551333237726" TEXT="Display home page based on the user role"/>
</node>
<node COLOR="#ff6600" CREATED="1551332486467" ID="ID_373335872" MODIFIED="1551333328396" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1551332500502" ID="ID_938093266" MODIFIED="1551333352254" TEXT="Display error message for incorrect credentials and capture the invalid attempt count. This is applicable for all modes like password, OTP, FACE, IRIS"/>
</node>
</node>
</node>
</node>
<node COLOR="#66cc00" CREATED="1551332562766" ID="ID_710205457" MODIFIED="1551333237726" TEXT="New Registration">
<node COLOR="#66cc00" CREATED="1551332576503" ID="ID_810005740" MODIFIED="1551333237726" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1551332581940" ID="ID_692637959" MODIFIED="1551333237726" TEXT="Verify the fingerprint is not displayed across the application like biometric marking, fingerprint capture, preview, acknowledgement">
<node COLOR="#66cc00" CREATED="1551332634658" ID="ID_1781619901" MODIFIED="1551333237726" TEXT="Not Displayed">
<node COLOR="#66cc00" CREATED="1551332650564" ID="ID_116224968" MODIFIED="1551333237726" TEXT="Application must navigate to packet authentication screen"/>
</node>
<node COLOR="#ff6600" CREATED="1551332640519" ID="ID_1881457190" MODIFIED="1551333328396" TEXT="Displayed">
<edge COLOR="#ff0000"/>
<node COLOR="#ff3300" CREATED="1551173729731" ID="ID_694774015" MODIFIED="1551174521839" TEXT="Raise a defect"/>
</node>
</node>
</node>
</node>
<node COLOR="#66cc00" CREATED="1551332668889" ID="ID_544203942" MODIFIED="1551333237726" TEXT="Packet Authentication with mode" VSHIFT="27">
<node COLOR="#ff6600" CREATED="1551332682095" HGAP="19" ID="ID_1564518168" MODIFIED="1551333328396" TEXT="Only Fingerprint for both RO and Sup" VSHIFT="-22">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1551332419371" HGAP="22" ID="ID_1676762466" MODIFIED="1551333352238" TEXT="Display &#x201c;The mode of authentication is not supported by the process level configuration. Please contact the administrator.&#x201d; error message" VSHIFT="-5"/>
</node>
<node COLOR="#66cc00" CREATED="1551332684897" HGAP="17" ID="ID_1309096608" MODIFIED="1551333237726" TEXT="Fingerprint + PWD/OTP/FACE/IRIS for both RO and Sup" VSHIFT="9">
<node COLOR="#66cc00" CREATED="1551332431377" ID="ID_836563082" MODIFIED="1551333237726" TEXT="Skip the fingerprint validation and other mode credentials are valid" VSHIFT="4">
<node COLOR="#66cc00" CREATED="1551332485211" ID="ID_485589647" MODIFIED="1551333237726" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1551332490116" ID="ID_177660523" MODIFIED="1551333237711" TEXT="Display home page based on the user role"/>
</node>
<node COLOR="#ff6600" CREATED="1551332486467" ID="ID_1756857817" MODIFIED="1551333328396" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1551332500502" ID="ID_701816070" MODIFIED="1551333352238" TEXT="Display error message for incorrect credentials and capture the invalid attempt count. This is applicable for all modes like password, OTP, FACE, IRIS"/>
</node>
</node>
</node>
</node>
<node COLOR="#66cc00" CREATED="1551332830846" ID="ID_1308398751" MODIFIED="1551333237711" TEXT="EOD Authentication with mode">
<node COLOR="#ff6600" CREATED="1551332682095" HGAP="19" ID="ID_1735648652" MODIFIED="1551333328396" TEXT="Only Fingerprint for Sup" VSHIFT="-22">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1551332419371" HGAP="22" ID="ID_1351661323" MODIFIED="1551333352238" TEXT="Display &#x201c;The mode of authentication is not supported by the process level configuration. Please contact the administrator.&#x201d; error message" VSHIFT="-5"/>
</node>
<node COLOR="#66cc00" CREATED="1551332684897" HGAP="17" ID="ID_1329858707" MODIFIED="1551333237711" TEXT="Fingerprint + PWD/OTP/FACE/IRIS for Sup" VSHIFT="9">
<node COLOR="#66cc00" CREATED="1551332431377" ID="ID_424419315" MODIFIED="1551333237711" TEXT="Skip the fingerprint validation and other mode credentials are valid" VSHIFT="4">
<node COLOR="#66cc00" CREATED="1551332485211" ID="ID_499257491" MODIFIED="1551333237711" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1551332490116" ID="ID_1400075999" MODIFIED="1551333392301" TEXT="The packet should get approved / rejected based on the reason selected"/>
</node>
<node COLOR="#ff6600" CREATED="1551332486467" ID="ID_644138251" MODIFIED="1551333328396" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1551332500502" ID="ID_690191113" MODIFIED="1551333352223" TEXT="Display error message for incorrect credentials and capture the invalid attempt count. This is applicable for all modes like password, OTP, FACE, IRIS"/>
</node>
</node>
</node>
</node>
<node COLOR="#66cc00" CREATED="1551332859240" HGAP="15" ID="ID_1398106029" MODIFIED="1551333237711" TEXT="Re-Register authentication with mode" VSHIFT="17">
<node COLOR="#ff6600" CREATED="1551332682095" HGAP="19" ID="ID_218126638" MODIFIED="1551333328396" TEXT="Only Fingerprint for Sup" VSHIFT="-22">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1551332419371" HGAP="22" ID="ID_793915278" MODIFIED="1551333352223" TEXT="Display &#x201c;The mode of authentication is not supported by the process level configuration. Please contact the administrator.&#x201d; error message" VSHIFT="-5"/>
</node>
<node COLOR="#66cc00" CREATED="1551332684897" HGAP="17" ID="ID_1740207024" MODIFIED="1551333237711" TEXT="Fingerprint + PWD/OTP/FACE/IRIS for Sup" VSHIFT="9">
<node COLOR="#66cc00" CREATED="1551332431377" ID="ID_156096750" MODIFIED="1551333237711" TEXT="Skip the fingerprint validation and other mode credentials are valid" VSHIFT="4">
<node COLOR="#66cc00" CREATED="1551332485211" ID="ID_14630545" MODIFIED="1551333237711" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1551332490116" ID="ID_719263813" MODIFIED="1551333419160" TEXT="The status of the packet will be marked as &apos;Informed&apos; or &quot;Can&apos;t Informed&apos;"/>
</node>
<node COLOR="#ff6600" CREATED="1551332486467" ID="ID_191548661" MODIFIED="1551333328380" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1551332500502" ID="ID_1737493491" MODIFIED="1551333352223" TEXT="Display error message for incorrect credentials and capture the invalid attempt count. This is applicable for all modes like password, OTP, FACE, IRIS"/>
</node>
</node>
</node>
</node>
<node COLOR="#66cc00" CREATED="1551332986803" HGAP="13" ID="ID_849683941" MODIFIED="1551333237711" TEXT="Onboard authentication with mode" VSHIFT="34">
<node COLOR="#ff6600" CREATED="1551332682095" HGAP="19" ID="ID_1785814615" MODIFIED="1551333328380" TEXT="Only Fingerprint for Sup" VSHIFT="-22">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1551332419371" HGAP="22" ID="ID_45791184" MODIFIED="1551333352223" TEXT="Display &#x201c;The mode of authentication is not supported by the process level configuration. Please contact the administrator.&#x201d; error message" VSHIFT="-5"/>
</node>
<node COLOR="#66cc00" CREATED="1551332684897" HGAP="17" ID="ID_1231011937" MODIFIED="1551333237711" TEXT="Fingerprint + PWD/OTP/FACE/IRIS for Sup" VSHIFT="9">
<node COLOR="#66cc00" CREATED="1551332431377" ID="ID_13880118" MODIFIED="1551333237711" TEXT="Skip the fingerprint validation and other mode credentials are valid" VSHIFT="4">
<node COLOR="#66cc00" CREATED="1551332485211" ID="ID_570850659" MODIFIED="1551333237711" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1551332490116" ID="ID_1494214962" MODIFIED="1551333237711" TEXT="Click On-board user">
<node COLOR="#66cc00" CREATED="1551332581940" ID="ID_874980132" MODIFIED="1551333237711" TEXT="Verify the fingerprint is not displayed across the application like biometric marking, fingerprint capture">
<node COLOR="#66cc00" CREATED="1551332634658" ID="ID_1083860896" MODIFIED="1551333237711" TEXT="Not Displayed">
<node COLOR="#66cc00" CREATED="1551332650564" ID="ID_1921521026" MODIFIED="1551333237711" TEXT="Application must display user onboarded successfully message"/>
</node>
<node COLOR="#ff6600" CREATED="1551332640519" ID="ID_205923924" MODIFIED="1551333328380" TEXT="Displayed">
<edge COLOR="#ff0000"/>
<node COLOR="#ff3300" CREATED="1551173729731" ID="ID_1087762518" MODIFIED="1551174521839" TEXT="Raise a defect"/>
</node>
</node>
</node>
</node>
<node COLOR="#ff6600" CREATED="1551332486467" ID="ID_1649344724" MODIFIED="1551333328380" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1551332500502" ID="ID_298146450" MODIFIED="1551333352207" TEXT="Display error message for incorrect credentials and capture the invalid attempt count. This is applicable for all modes like password, OTP, FACE, IRIS"/>
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
