<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node COLOR="#33cc00" CREATED="1554091565440" ID="ID_1929844334" MODIFIED="1554096373174" TEXT="Registration - Lost UIN">
<node COLOR="#33cc00" CREATED="1554091579733" ID="ID_572750451" LINK="https://mosipid.atlassian.net/browse/MOS-18117" MODIFIED="1554096373158" POSITION="right" TEXT="MOS-18117">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1554093317345" ID="ID_169074873" MODIFIED="1554793160608" TEXT="Click Lost UIN">
<node COLOR="#33cc00" CREATED="1554092587627" ID="ID_1634021544" MODIFIED="1554096373158" TEXT="The  time since the last sync from server to client has not exceeded the maximum duration permitted ">
<node COLOR="#33cc00" CREATED="1554092679834" ID="ID_1056988663" MODIFIED="1554096373158" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1554092575954" ID="ID_666350783" MODIFIED="1554096373158" TEXT="Proceed with Lost UIN request"/>
</node>
<node COLOR="#ff6600" CREATED="1554092681700" ID="ID_140245404" MODIFIED="1554096204471" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff3300" CREATED="1554092689946" ID="ID_1611775760" MODIFIED="1554096260240" TEXT="Display &#x201c;Time since last sync exceeded maximum limit. Please sync from server before proceeding with this registration.&#x201d;"/>
</node>
</node>
<node COLOR="#00cc00" CREATED="1553166911992" HGAP="17" ID="ID_1102206462" MODIFIED="1554793198375" TEXT="Max time since last sync as configurable parameter" VSHIFT="-22">
<node COLOR="#00cc00" CREATED="1553167000490" ID="ID_235437942" MODIFIED="1553167928882" TEXT="Yes">
<node COLOR="#00cc00" CREATED="1553166957818" ID="ID_1534846856" MODIFIED="1553167928883" TEXT="Use the parameter to set the limit based on which application allows the user to register / UIN update"/>
</node>
<node COLOR="#ff3300" CREATED="1553167002050" ID="ID_739782413" MODIFIED="1553167903699" TEXT="No">
<edge COLOR="#cc0000"/>
<node COLOR="#ff3300" CREATED="1553167006400" ID="ID_696346841" MODIFIED="1553167939903" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1554092728046" ID="ID_151234724" MODIFIED="1554096373158" TEXT="The time since the last export of registration packets from client to server has not exceeded the maximum duration permitted,">
<node COLOR="#33cc00" CREATED="1554092679834" ID="ID_1887836111" MODIFIED="1554096373158" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1554092575954" ID="ID_1292353183" MODIFIED="1554096373158" TEXT="Proceed with Lost UIN request"/>
</node>
<node COLOR="#ff6600" CREATED="1554092681700" ID="ID_1333618500" MODIFIED="1554096204471" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff3300" CREATED="1554092689946" ID="ID_699917454" MODIFIED="1554096260240" TEXT="Display &#x201c;Time since last export of registration packets exceeded maximum limit. Please export or upload packets to server before proceeding with this registration.&#x201d;"/>
</node>
</node>
<node COLOR="#00cc00" CREATED="1553166911992" HGAP="17" ID="ID_60677843" MODIFIED="1554793124351" TEXT="Max limit for export or upload as configurable parameter" VSHIFT="-22">
<node COLOR="#00cc00" CREATED="1553167000490" ID="ID_923797853" MODIFIED="1553167928882" TEXT="Yes">
<node COLOR="#00cc00" CREATED="1553166957818" ID="ID_1642109056" MODIFIED="1553167928883" TEXT="Use the parameter to set the limit based on which application allows the user to register / UIN update"/>
</node>
<node COLOR="#ff3300" CREATED="1553167002050" ID="ID_904141168" MODIFIED="1553167903699" TEXT="No">
<edge COLOR="#cc0000"/>
<node COLOR="#ff3300" CREATED="1553167006400" ID="ID_1082067701" MODIFIED="1553167939903" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1554092893110" ID="ID_1306143885" MODIFIED="1554096373158" TEXT="The number of registration packets on the client yet to be exported to server has not exceeded the maximum limit">
<node COLOR="#33cc00" CREATED="1554092977551" ID="ID_1334977591" MODIFIED="1554096373158" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1554092981508" ID="ID_22066108" MODIFIED="1554096373158" TEXT="Proceed with Lost UIN request"/>
</node>
<node COLOR="#ff6600" CREATED="1554093000300" ID="ID_553397419" MODIFIED="1554096204471" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff3300" CREATED="1554093002079" ID="ID_686642247" MODIFIED="1554096260240" TEXT="Display &#x201c;Maximum limit for registration packets on client reached. Please export or upload packets to server before proceeding with this registration.&#x201d;"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1554092936443" HGAP="16" ID="ID_580179747" MODIFIED="1554096373158" TEXT="Validate that the captured location is within x metres of the Registration Centre location" VSHIFT="27">
<node COLOR="#33cc00" CREATED="1554093041655" ID="ID_1149895263" MODIFIED="1554096373158" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1554092981508" ID="ID_385466664" MODIFIED="1554096373158" TEXT="Proceed with Lost UIN request"/>
</node>
<node COLOR="#ff6600" CREATED="1554093043512" ID="ID_1189239914" MODIFIED="1554096204471" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff3300" CREATED="1554093053663" ID="ID_1880418939" MODIFIED="1554096260224" TEXT="Display &#x201c;Your client machine&#x2019;s location is outside the registration centre. Please note that registration can be done only from within the registration centre.&#x201d;"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1552884678680" HGAP="12" ID="ID_1431094525" MODIFIED="1552885138850" TEXT="Key threshold value config parameter" VSHIFT="-32">
<node COLOR="#33cc00" CREATED="1552884693032" ID="ID_1545739678" MODIFIED="1552885138834" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1552884712360" HGAP="15" ID="ID_1559960656" MODIFIED="1552885138834" TEXT="Set the value as per the center&apos;s requirement using &quot;mosip.registration.key_policy_sync_threshold_value&quot; parameter in master.global_param" VSHIFT="-21"/>
</node>
<node COLOR="#ff0000" CREATED="1552884706251" ID="ID_241508853" MODIFIED="1552885168436" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1552884796698" ID="ID_1743172154" MODIFIED="1552885184955" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1552884815109" HGAP="19" ID="ID_8209145" MODIFIED="1554791510585" TEXT="Key expiry date is &gt;=  current date + threshold value " VSHIFT="15">
<node COLOR="#33cc00" CREATED="1552884839539" HGAP="17" ID="ID_700372156" MODIFIED="1552885138834" TEXT="Yes" VSHIFT="-22">
<node COLOR="#33cc00" CREATED="1552884868026" ID="ID_322099829" MODIFIED="1552885138834" TEXT="Allow the user to proceed with new registration"/>
</node>
<node COLOR="#ff0000" CREATED="1552884840687" HGAP="19" ID="ID_943951204" MODIFIED="1552885168436" TEXT="No" VSHIFT="17">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1552884967476" ID="ID_170816185" MODIFIED="1552885184940" TEXT="Display &quot;Please sync to get latest key from the server before proceeding with this registration.&#x201d; error message"/>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1554092548719" HGAP="15" ID="ID_1876689007" MODIFIED="1554096373158" TEXT="Verify whether the RO has access to Lost UIN node" VSHIFT="-14">
<node COLOR="#33cc00" CREATED="1554092567593" ID="ID_245621436" MODIFIED="1554096373158" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1554092575954" ID="ID_1782272938" MODIFIED="1554096373158" TEXT="Proceed with Lost UIN request"/>
</node>
<node COLOR="#ff6600" CREATED="1554092569312" ID="ID_1401739783" MODIFIED="1554096204471" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff3300" CREATED="1554092570412" ID="ID_583572568" MODIFIED="1554096260240" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1554093284791" ID="ID_1602322787" MODIFIED="1554096373158" TEXT="Verify all the fields in demo screen are optional by default and validation takes places only when user enters any specific field">
<node COLOR="#33cc00" CREATED="1554093415218" ID="ID_1363765255" MODIFIED="1554096373158" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1554093427243" ID="ID_710416225" MODIFIED="1554096373158" TEXT="Validate only the entered field"/>
</node>
<node COLOR="#ff6600" CREATED="1554093417411" ID="ID_1303956032" MODIFIED="1554096204471" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff3300" CREATED="1554093423204" ID="ID_866834924" MODIFIED="1554096260224" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1554093461813" ID="ID_202307582" MODIFIED="1554096373158" TEXT="Verify for child lost UIN, has &#x2018;Parent/Guardian RID&#x2019; and &#x2018;Parent/Guardian UIN&#x2019; as a toggle button" VSHIFT="9">
<node COLOR="#33cc00" CREATED="1554093511535" ID="ID_1528816488" MODIFIED="1554096373158" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1554093520269" ID="ID_1367277050" MODIFIED="1554096373158" TEXT="Enter the respective field based on user input"/>
</node>
<node COLOR="#ff6600" CREATED="1554093513384" ID="ID_1323957111" MODIFIED="1554096204471" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff3300" CREATED="1554093423204" ID="ID_312942326" MODIFIED="1554096260224" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1550385227780" HGAP="8" ID="ID_419643996" MODIFIED="1554096373158" TEXT="Different language" VSHIFT="-15">
<node COLOR="#33cc00" CREATED="1552037744534" ID="ID_1760577554" MODIFIED="1554096373158" TEXT="Primary as arabic">
<node COLOR="#33cc00" CREATED="1550385341904" ID="ID_1207227946" MODIFIED="1554096373158" TEXT="Display all static text including headers, labels, action buttons and alert messages in arabic"/>
</node>
<node COLOR="#33cc00" CREATED="1552037754357" ID="ID_1524280348" MODIFIED="1554096373158" TEXT="Secondary as french">
<node COLOR="#33cc00" CREATED="1550385341904" ID="ID_1117215381" MODIFIED="1554096373142" TEXT="Display all static text including headers, labels, action buttons and alert messages in french (Demographic, Preview and Acknowledgement screen alone)"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1550385488814" HGAP="12" ID="ID_803591941" MODIFIED="1554096373142" TEXT="Both in same language" VSHIFT="23">
<node COLOR="#33cc00" CREATED="1550385341904" ID="ID_619888658" MODIFIED="1554096373142" TEXT="Display all static text including headers, labels, action buttons and alert messages in language as per config"/>
</node>
<node COLOR="#33cc00" CREATED="1554093922510" ID="ID_1241281459" MODIFIED="1554096373142" TEXT="Verify user is not allowed to upload any document">
<node COLOR="#33cc00" CREATED="1554093934075" ID="ID_1333185756" MODIFIED="1554096373142" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1554093936884" ID="ID_1796215951" MODIFIED="1554096373142" TEXT="Proceed with biometric details"/>
</node>
<node COLOR="#ff6600" CREATED="1554093943498" ID="ID_1889898422" MODIFIED="1554096204471" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff3300" CREATED="1554093945017" ID="ID_12824487" MODIFIED="1554096260224" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1554093962070" ID="ID_1335454127" MODIFIED="1554096373142" TEXT="Biometric exception button should be displayed and editable">
<node COLOR="#33cc00" CREATED="1554093978780" ID="ID_1113065057" MODIFIED="1554096373142" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1554093988327" ID="ID_799311334" MODIFIED="1554096373142" TEXT="Mark based on the resident&apos;s input with reason as &quot;Missing biometrics&quot;"/>
</node>
<node COLOR="#ff6600" CREATED="1554093979970" ID="ID_1912749771" MODIFIED="1554096204471" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff3300" CREATED="1554093981721" ID="ID_51245671" MODIFIED="1554096260224" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1554094060478" ID="ID_1435027525" MODIFIED="1554096373142" TEXT="Verify user is able to capture all fingerprint and iris except which are marked as expcetion">
<node COLOR="#33cc00" CREATED="1554094085739" ID="ID_138673007" MODIFIED="1554096373142" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1554094102473" ID="ID_889227841" MODIFIED="1554096373142" TEXT="check if quality is below the threshold">
<node COLOR="#33cc00" CREATED="1554094126501" ID="ID_1113419320" MODIFIED="1554096373142" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1554094139486" ID="ID_1587880623" MODIFIED="1554096373142" TEXT="Max retry limit reached">
<node COLOR="#33cc00" CREATED="1554094808286" ID="ID_1160572041" MODIFIED="1554096373142" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1554094871764" ID="ID_230236242" MODIFIED="1554096373142" TEXT="Quality is still below the threshold">
<node COLOR="#33cc00" CREATED="1554094883682" ID="ID_1275278900" MODIFIED="1554096373142" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1554094916748" ID="ID_637448630" MODIFIED="1554791666145" TEXT="Mark them as biometric exception with reason as Low quality biometrics. Also store the best score"/>
</node>
<node COLOR="#33cc00" CREATED="1554094889215" ID="ID_169846006" MODIFIED="1554096373142" TEXT="No">
<node COLOR="#33cc00" CREATED="1554094892375" ID="ID_900029939" MODIFIED="1554096373142" TEXT="Record the best score"/>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1554094814635" ID="ID_482611307" MODIFIED="1554096373142" TEXT="No">
<node COLOR="#33cc00" CREATED="1554094817633" ID="ID_435330928" MODIFIED="1554096373142" TEXT="Retry to capture the biometric again"/>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1554094128413" ID="ID_304323934" MODIFIED="1554096373142" TEXT="No">
<node COLOR="#33cc00" CREATED="1554094132925" ID="ID_1172368507" MODIFIED="1554096373142" TEXT="Proceed to preview page"/>
</node>
</node>
</node>
<node COLOR="#ff6600" CREATED="1554094086996" ID="ID_1507379206" MODIFIED="1554096204471" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff3300" CREATED="1554093981721" ID="ID_1909982775" MODIFIED="1554096260224" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1554094958193" ID="ID_366235553" MODIFIED="1554096373142" TEXT="Verify preview screen displays all captured details with a link to modify them" VSHIFT="19">
<node COLOR="#33cc00" CREATED="1554094981335" ID="ID_12066273" MODIFIED="1554096373142" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1554095021065" ID="ID_1560171461" MODIFIED="1554096373142" TEXT="The preview must display the demographic details, actual scans of fingerprints and irises and the face and exception photographs. Click modify link if something needs to be modified"/>
</node>
<node COLOR="#ff6600" CREATED="1554094982678" ID="ID_1979762288" MODIFIED="1554096204471" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff3300" CREATED="1554095041805" ID="ID_1938559359" MODIFIED="1554096260224" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1554095107868" HGAP="17" ID="ID_658156913" MODIFIED="1554096373142" TEXT="Packets authentication" VSHIFT="28">
<node COLOR="#33cc00" CREATED="1554095153744" HGAP="17" ID="ID_1402552349" MODIFIED="1554096373142" TEXT="Packet without biometric exception" VSHIFT="-35">
<node COLOR="#33cc00" CREATED="1554095186314" ID="ID_413713273" MODIFIED="1554096373142" TEXT="Needs only operator authentication"/>
</node>
<node COLOR="#33cc00" CREATED="1554095166086" ID="ID_215426853" MODIFIED="1554096373142" TEXT="Packet with biometric exception / low quality biometrics">
<node COLOR="#33cc00" CREATED="1554095201683" ID="ID_1288630917" MODIFIED="1554096373142" TEXT="Needs sup authentication (if config)"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1554095231012" HGAP="11" ID="ID_1237628235" MODIFIED="1554096373142" TEXT="Acknowledgement screen" VSHIFT="36">
<node COLOR="#33cc00" CREATED="1554095302803" ID="ID_1833483705" MODIFIED="1554096373142" TEXT="The Registration ID, demographic details entered, placeholder images of fingerprints and irises indicating the ones that have been captured, and the face and exception photograph. Fingerprint rankings should not be displayed.">
<node COLOR="#33cc00" CREATED="1554095341998" ID="ID_1659005079" MODIFIED="1554096373142" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1554095351728" ID="ID_1089797697" MODIFIED="1554096373142" TEXT="Validate the details and provide a copy to the resident by taking print out"/>
</node>
<node COLOR="#ff6600" CREATED="1554095344255" ID="ID_685987555" MODIFIED="1554096204471" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff3300" CREATED="1554095347169" ID="ID_754214991" MODIFIED="1554096260224" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1554095386416" ID="ID_655636276" MODIFIED="1554096373142" TEXT="Registration Officer name and photo should display.">
<node COLOR="#33cc00" CREATED="1554095341998" ID="ID_940662813" MODIFIED="1554096373142" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1554095351728" ID="ID_451341575" MODIFIED="1554096373142" TEXT="Validate the details and provide a copy to the resident by taking print out"/>
</node>
<node COLOR="#ff6600" CREATED="1554095344255" ID="ID_1161516696" MODIFIED="1554096204471" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff3300" CREATED="1554095347169" ID="ID_1121435822" MODIFIED="1554096260224" TEXT="Raise a defect"/>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1554095428393" ID="ID_700754164" MODIFIED="1554096373127" TEXT="SMS and email confirmations are sent only if the email and/or mobile number are captured in the demographic details.">
<node COLOR="#33cc00" CREATED="1554095438087" ID="ID_55308555" MODIFIED="1554791844920" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1554791844912" ID="ID_1575560061" MODIFIED="1554791899580" TEXT="Online">
<node COLOR="#33cc00" CREATED="1554095441168" ID="ID_917561647" MODIFIED="1554791860671" TEXT="Send the email or sms "/>
</node>
<node COLOR="#33cc00" CREATED="1554791849967" ID="ID_1165507593" MODIFIED="1554791899580" TEXT="Offline">
<node COLOR="#33cc00" CREATED="1554791863655" ID="ID_1711485175" MODIFIED="1554791899580" TEXT="All the SMS and Email should be queued up and should be sent when machine comes online"/>
</node>
</node>
<node COLOR="#ff6600" CREATED="1554095344255" ID="ID_1227606372" MODIFIED="1554096204471" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff3300" CREATED="1554095347169" ID="ID_825596977" MODIFIED="1554096260224" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1554095493687" ID="ID_1864637554" MODIFIED="1554096373127" TEXT="Verify whether the RO is able to sent notification using the SMS / Email  buton in Ack screen">
<node COLOR="#33cc00" CREATED="1554095516918" ID="ID_1268556403" MODIFIED="1554096373127" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1554095520875" ID="ID_834712348" MODIFIED="1554096373127" TEXT="Max of 5 members at a time and can repeat this for multiple attempts as long as RC displays Ack screen. Display error message when the recipient is &gt; 5"/>
</node>
<node COLOR="#33cc00" CREATED="1554095561371" ID="ID_1531049278" MODIFIED="1554096373127" TEXT="No">
<node COLOR="#33cc00" CREATED="1554095566531" ID="ID_1497707337" MODIFIED="1554096373127" TEXT="Check the field is configured">
<node COLOR="#33cc00" CREATED="1554095579536" ID="ID_1758118049" MODIFIED="1554096373127" TEXT="Yes">
<node COLOR="#ff6600" CREATED="1554095583300" ID="ID_1802850149" MODIFIED="1554096204471" TEXT="Online">
<edge COLOR="#ff0000"/>
<node COLOR="#ff3300" CREATED="1554095593001" ID="ID_206560044" MODIFIED="1554096260224" TEXT="Raise a defect"/>
</node>
<node COLOR="#33cc00" CREATED="1554095641013" ID="ID_1758118655" MODIFIED="1554096373127" TEXT="Offline">
<node COLOR="#33cc00" CREATED="1554095647471" ID="ID_834486797" MODIFIED="1554096373127" TEXT="Connect to online and try again"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1554095597887" ID="ID_1043374134" MODIFIED="1554096373127" TEXT="No">
<node COLOR="#33cc00" CREATED="1554095605229" ID="ID_436663528" MODIFIED="1554096373127" TEXT="Proceed with next request"/>
</node>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1554095696488" ID="ID_1854372241" MODIFIED="1554096373127" TEXT="EOD approval">
<node COLOR="#33cc00" CREATED="1554095704563" HGAP="19" ID="ID_1641465346" MODIFIED="1554096373127" TEXT="Enabled" VSHIFT="-28">
<node COLOR="#33cc00" CREATED="1554095717289" ID="ID_804752947" MODIFIED="1554096373127" TEXT="Approve the packets manually">
<node COLOR="#33cc00" CREATED="1554095750229" ID="ID_144979821" MODIFIED="1554096373127" TEXT="online">
<node COLOR="#33cc00" CREATED="1554095771440" ID="ID_1988637932" MODIFIED="1554096373127" TEXT="packet will get pushed to server"/>
</node>
<node COLOR="#33cc00" CREATED="1554095755757" ID="ID_1244750509" MODIFIED="1554096373127" TEXT="offline">
<node COLOR="#33cc00" CREATED="1554095758932" ID="ID_1499051591" MODIFIED="1554792010643" TEXT="packet needs to selected and pushed manually when machine comes online again"/>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1554095706971" ID="ID_524089929" MODIFIED="1554096373127" TEXT="Disabled" VSHIFT="32">
<node COLOR="#33cc00" CREATED="1554095737980" ID="ID_669735239" MODIFIED="1554096373127" TEXT="Packets will get approved by default">
<node COLOR="#33cc00" CREATED="1554095750229" ID="ID_265762008" MODIFIED="1554096373127" TEXT="online">
<node COLOR="#33cc00" CREATED="1554095771440" ID="ID_388909311" MODIFIED="1554096373127" TEXT="packet will get pushed to server"/>
</node>
<node COLOR="#33cc00" CREATED="1554095755757" ID="ID_369482450" MODIFIED="1554096373127" TEXT="offline">
<node COLOR="#33cc00" CREATED="1554095758932" ID="ID_1832623652" MODIFIED="1554792010643" TEXT="packet needs to selected and pushed manually when machine comes online again"/>
</node>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1554095820113" ID="ID_1614089222" MODIFIED="1554096373127" TEXT="Lost UIN as config property">
<node COLOR="#33cc00" CREATED="1554095833960" ID="ID_684926907" MODIFIED="1554096373127" TEXT="Yes">
<node COLOR="#33cc00" CREATED="1554095846375" ID="ID_1228806033" MODIFIED="1554096373127" TEXT="use the property to set it on or off as per center&apos;s requirement"/>
</node>
<node COLOR="#ff6600" CREATED="1554095835071" ID="ID_1173044846" MODIFIED="1554096204471" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff3300" CREATED="1554095837482" ID="ID_1927490620" MODIFIED="1554096260224" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1538550071542" HGAP="9" ID="ID_455711452" MODIFIED="1550474395630" TEXT="Verification of Txn details for Audit purpose" VSHIFT="34">
<edge COLOR="#00cc00"/>
<node COLOR="#33cc00" CREATED="1539003087723" ID="ID_702214481" MODIFIED="1550474395629" TEXT="System capture all Txn details">
<node COLOR="#33cc00" CREATED="1539003122483" ID="ID_635478647" MODIFIED="1550474395625" TEXT="Store all the details under &quot;Audit_Log&quot; table such as User id or system account; Machine Details; Event Name; Application Name, and Event data including user entered fields."/>
</node>
<node COLOR="#ff0000" CREATED="1539060442854" ID="ID_1387756172" MODIFIED="1550474463694" TEXT="System fails to capture Txn details">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539089688847" ID="ID_1837414142" MODIFIED="1550474463695" TEXT="Display appropriate error message / Raise a defect"/>
</node>
</node>
</node>
</node>
</map>
