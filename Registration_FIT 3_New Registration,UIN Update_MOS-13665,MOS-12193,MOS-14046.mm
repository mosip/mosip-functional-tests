<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1552576404395" ID="ID_968769222" MODIFIED="1552642998857" TEXT="Registration-New Registration">
<node COLOR="#33cc00" CREATED="1552642912319" HGAP="27" ID="ID_498456255" MODIFIED="1552643256721" POSITION="right" TEXT="IRIS Disable flag" VSHIFT="406">
<node CREATED="1552576507512" ID="ID_1561587129" LINK="https://mosipid.atlassian.net/browse/MOS-13655" MODIFIED="1552577457188" TEXT="MOS-13655">
<edge COLOR="#33cc00"/>
<node COLOR="#66cc00" CREATED="1551174681349" HGAP="8" ID="ID_1848560293" MODIFIED="1552576563867" TEXT="IRIS capture as configurable parameter" VSHIFT="-59">
<node COLOR="#66cc00" CREATED="1551173723578" ID="ID_343609622" MODIFIED="1551174468954" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1551173735708" ID="ID_169409031" MODIFIED="1552576586122" TEXT="Set the parameter &quot;IRIS_DISABLE_FLAG&quot; to Y / N based on center requirement"/>
</node>
<node COLOR="#ff6600" CREATED="1551173726033" ID="ID_622846585" MODIFIED="1551175152204" TEXT="No">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1551173729731" ID="ID_1225650615" MODIFIED="1551174521839" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#66cc00" CREATED="1551332344612" HGAP="18" ID="ID_1915978356" MODIFIED="1552576794235" TEXT="IRIS flag turn off" VSHIFT="-63">
<node COLOR="#66cc00" CREATED="1551332365852" HGAP="15" ID="ID_1154295718" MODIFIED="1552576732465" TEXT="Login Mode is only IRIS" VSHIFT="-29">
<node COLOR="#ff6600" CREATED="1551332402742" ID="ID_489063477" MODIFIED="1551333328396" TEXT="Yes">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1551332419371" HGAP="22" ID="ID_778760460" MODIFIED="1551333352254" TEXT="Display &#x201c;The mode of authentication is not supported by the process level configuration. Please contact the administrator.&#x201d; error message" VSHIFT="-5"/>
</node>
<node COLOR="#66cc00" CREATED="1551332404300" ID="ID_593713852" MODIFIED="1551333237726" TEXT="No">
<node COLOR="#66cc00" CREATED="1551332431377" ID="ID_1067111316" MODIFIED="1552576815752" TEXT="Skip the IRIS validation and other mode credentials are valid" VSHIFT="4">
<edge COLOR="#00ff00"/>
<node COLOR="#66cc00" CREATED="1551332485211" ID="ID_303545850" MODIFIED="1552576753005" TEXT="Yes">
<edge COLOR="#00ff00"/>
<node COLOR="#66cc00" CREATED="1551332490116" ID="ID_290099853" MODIFIED="1551333237726" TEXT="Display home page based on the user role"/>
</node>
<node COLOR="#ff6600" CREATED="1551332486467" ID="ID_373335872" MODIFIED="1551333328396" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1551332500502" ID="ID_938093266" MODIFIED="1552576833570" TEXT="Display error message for incorrect credentials and capture the invalid attempt count. This is applicable for all modes like password, OTP, FACE, BIO"/>
</node>
</node>
</node>
</node>
<node COLOR="#66cc00" CREATED="1551332562766" ID="ID_710205457" MODIFIED="1551333237726" TEXT="New Registration">
<node COLOR="#66cc00" CREATED="1551332576503" ID="ID_810005740" MODIFIED="1551333237726" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1551332581940" ID="ID_692637959" MODIFIED="1552576927095" TEXT="Verify the iris is not displayed across the application like biometric marking, iris capture, preview, acknowledgement">
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
<node COLOR="#66cc00" CREATED="1551332668889" ID="ID_544203942" MODIFIED="1552576868760" TEXT="Packet Authentication" VSHIFT="27">
<node COLOR="#ff6600" CREATED="1551332682095" HGAP="19" ID="ID_1564518168" MODIFIED="1552576939223" TEXT="With only IRIS for both RO and Sup" VSHIFT="-22">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1551332419371" HGAP="22" ID="ID_1676762466" MODIFIED="1551333352238" TEXT="Display &#x201c;The mode of authentication is not supported by the process level configuration. Please contact the administrator.&#x201d; error message" VSHIFT="-5"/>
</node>
<node COLOR="#66cc00" CREATED="1551332684897" HGAP="17" ID="ID_1309096608" MODIFIED="1552576892953" TEXT="IRIS + PWD/OTP/FACE/BIO both RO and Sup" VSHIFT="9">
<node COLOR="#66cc00" CREATED="1551332431377" ID="ID_836563082" MODIFIED="1552576950209" TEXT="Skip the iris validation and other mode credentials are valid" VSHIFT="4">
<node COLOR="#66cc00" CREATED="1551332485211" ID="ID_485589647" MODIFIED="1551333237726" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1551332490116" ID="ID_177660523" MODIFIED="1551333237711" TEXT="Display home page based on the user role"/>
</node>
<node COLOR="#ff6600" CREATED="1551332486467" ID="ID_1756857817" MODIFIED="1551333328396" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1551332500502" ID="ID_534810378" MODIFIED="1552576833570" TEXT="Display error message for incorrect credentials and capture the invalid attempt count. This is applicable for all modes like password, OTP, FACE, BIO"/>
</node>
</node>
</node>
</node>
<node COLOR="#66cc00" CREATED="1551332830846" ID="ID_1308398751" MODIFIED="1552577004394" TEXT="EOD Authentication">
<node COLOR="#ff6600" CREATED="1551332682095" HGAP="19" ID="ID_1735648652" MODIFIED="1552577022932" TEXT="With only Iris for Sup" VSHIFT="-22">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1551332419371" HGAP="22" ID="ID_1351661323" MODIFIED="1551333352238" TEXT="Display &#x201c;The mode of authentication is not supported by the process level configuration. Please contact the administrator.&#x201d; error message" VSHIFT="-5"/>
</node>
<node COLOR="#66cc00" CREATED="1551332684897" HGAP="17" ID="ID_1329858707" MODIFIED="1552577039903" TEXT="IRIS + PWD/OTP/FACE/BIO for Sup" VSHIFT="9">
<node COLOR="#66cc00" CREATED="1551332431377" ID="ID_424419315" MODIFIED="1552578115936" TEXT="Skip the iris validation and other mode credentials are valid" VSHIFT="4">
<node COLOR="#66cc00" CREATED="1551332485211" ID="ID_499257491" MODIFIED="1551333237711" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1551332490116" ID="ID_1400075999" MODIFIED="1551333392301" TEXT="The packet should get approved / rejected based on the reason selected"/>
</node>
<node COLOR="#ff6600" CREATED="1551332486467" ID="ID_644138251" MODIFIED="1551333328396" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1551332500502" ID="ID_870756922" MODIFIED="1552576833570" TEXT="Display error message for incorrect credentials and capture the invalid attempt count. This is applicable for all modes like password, OTP, FACE, BIO"/>
</node>
</node>
</node>
</node>
<node COLOR="#66cc00" CREATED="1551332859240" HGAP="15" ID="ID_1398106029" MODIFIED="1552577053847" TEXT="Re-Register authentication" VSHIFT="17">
<node COLOR="#ff6600" CREATED="1551332682095" HGAP="19" ID="ID_218126638" MODIFIED="1552577064086" TEXT="with only iris for Sup" VSHIFT="-22">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1551332419371" HGAP="22" ID="ID_793915278" MODIFIED="1551333352223" TEXT="Display &#x201c;The mode of authentication is not supported by the process level configuration. Please contact the administrator.&#x201d; error message" VSHIFT="-5"/>
</node>
<node COLOR="#66cc00" CREATED="1551332684897" HGAP="17" ID="ID_1740207024" MODIFIED="1552577080002" TEXT="IRIS + PWD/OTP/FACE/BIO for Sup" VSHIFT="9">
<node COLOR="#66cc00" CREATED="1551332431377" ID="ID_156096750" MODIFIED="1552578120899" TEXT="Skip the iris validation and other mode credentials are valid" VSHIFT="4">
<node COLOR="#66cc00" CREATED="1551332485211" ID="ID_14630545" MODIFIED="1551333237711" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1551332490116" ID="ID_719263813" MODIFIED="1551333419160" TEXT="The status of the packet will be marked as &apos;Informed&apos; or &quot;Can&apos;t Informed&apos;"/>
</node>
<node COLOR="#ff6600" CREATED="1551332486467" ID="ID_191548661" MODIFIED="1551333328380" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1551332500502" ID="ID_1380442468" MODIFIED="1552576833570" TEXT="Display error message for incorrect credentials and capture the invalid attempt count. This is applicable for all modes like password, OTP, FACE, BIO"/>
</node>
</node>
</node>
</node>
<node COLOR="#66cc00" CREATED="1551332986803" HGAP="13" ID="ID_849683941" MODIFIED="1551333237711" TEXT="Onboard authentication with mode" VSHIFT="34">
<node COLOR="#66cc00" CREATED="1551332684897" HGAP="17" ID="ID_1231011937" MODIFIED="1552040423449" TEXT="PWD + OTP" VSHIFT="9">
<node COLOR="#66cc00" CREATED="1551332431377" ID="ID_13880118" MODIFIED="1552040445679" TEXT="valid credentials" VSHIFT="4">
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
<node COLOR="#ff0000" CREATED="1551332500502" ID="ID_298146450" MODIFIED="1552577175065" TEXT="Display error message for incorrect credentials and capture the invalid attempt count. This is applicable for all modes like password, OTP, FACE, Bio"/>
</node>
</node>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1552577187786" HGAP="26" ID="ID_1772463530" MODIFIED="1552643260003" TEXT="IRIS + Bio turned off" VSHIFT="-149">
<node COLOR="#33cc00" CREATED="1552577205316" HGAP="18" ID="ID_633574194" MODIFIED="1552577479272" TEXT="Bio exception toggle button displayed" VSHIFT="-42">
<node COLOR="#ff6600" CREATED="1552577215502" ID="ID_1562230120" MODIFIED="1552577553697" TEXT="Yes">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1552577239558" ID="ID_263765870" MODIFIED="1552577505305" TEXT="Raise a defect"/>
</node>
<node COLOR="#33cc00" CREATED="1552577217464" ID="ID_883212572" MODIFIED="1552577479272" TEXT="No">
<node COLOR="#33cc00" CREATED="1552577220271" ID="ID_250947684" MODIFIED="1552577479272" TEXT="Proceed with fingerprint capture"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1552577252101" HGAP="18" ID="ID_977094391" MODIFIED="1552577479272" TEXT="User is able to capture biometric exception photo" VSHIFT="-21">
<node COLOR="#ff6600" CREATED="1552577215502" ID="ID_1498003028" MODIFIED="1552577553682" TEXT="Yes">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1552577239558" ID="ID_1524542755" MODIFIED="1552577505305" TEXT="Raise a defect"/>
</node>
<node COLOR="#33cc00" CREATED="1552577217464" ID="ID_236200958" MODIFIED="1552577479272" TEXT="No">
<node COLOR="#33cc00" CREATED="1552577220271" ID="ID_1335777672" MODIFIED="1552577479272" TEXT="Proceed with preview screen verification"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1552577314943" ID="ID_1416650749" MODIFIED="1552577479272" TEXT="Biometric exception screen displayed for user onboarding">
<node COLOR="#ff6600" CREATED="1552577215502" ID="ID_526262319" MODIFIED="1552577553682" TEXT="Yes">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1552577239558" ID="ID_1440211931" MODIFIED="1552577505305" TEXT="Raise a defect"/>
</node>
<node COLOR="#33cc00" CREATED="1552577217464" ID="ID_183044048" MODIFIED="1552577479272" TEXT="No">
<node COLOR="#33cc00" CREATED="1552577220271" ID="ID_73147325" MODIFIED="1552577479272" TEXT="Proceed with fingerprint capture"/>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1538550071542" HGAP="24" ID="ID_1021783307" MODIFIED="1552643262194" TEXT="Verification of Txn details for Audit purpose" VSHIFT="-110">
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
<node COLOR="#33cc00" CREATED="1552642502960" HGAP="-70" ID="ID_633418275" MODIFIED="1552643192029" POSITION="left" TEXT="Foreigner field" VSHIFT="-259">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1552642511414" ID="ID_1028898831" LINK="https://mosipid.atlassian.net/browse/MOS-12193" MODIFIED="1552642853139" TEXT="MOS-12193">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1552642518252" HGAP="17" ID="ID_1770964016" MODIFIED="1552642853141" TEXT="Foreigner as a new field in Demographic" VSHIFT="-51">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1552642597074" ID="ID_1552417360" MODIFIED="1552642853141" TEXT="Yes">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1552642615757" ID="ID_1039004943" MODIFIED="1552642853141" TEXT="Verify whether it a checkbox and user can select it">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1552642778271" ID="ID_662837321" MODIFIED="1552642853140" TEXT="Yes">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1552642784859" ID="ID_146848393" MODIFIED="1552642853140" TEXT="Proceed with the application">
<edge COLOR="#33cc00"/>
</node>
</node>
<node COLOR="#ff3300" CREATED="1552642605707" ID="ID_1905884414" MODIFIED="1552642838959" TEXT="No">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1552642610354" ID="ID_733882718" MODIFIED="1552642863920" TEXT="Raise a defect"/>
</node>
</node>
</node>
<node COLOR="#ff3300" CREATED="1552642605707" ID="ID_1824914312" MODIFIED="1552642838959" TEXT="No">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1552642610354" ID="ID_1233763044" MODIFIED="1552642863920" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1538550071542" HGAP="9" ID="ID_1066490130" MODIFIED="1550474395630" TEXT="Verification of Txn details for Audit purpose" VSHIFT="34">
<edge COLOR="#00cc00"/>
<node COLOR="#33cc00" CREATED="1539003087723" ID="ID_1417691009" MODIFIED="1550474395629" TEXT="System capture all Txn details">
<node COLOR="#33cc00" CREATED="1539003122483" ID="ID_977118878" MODIFIED="1550474395625" TEXT="Store all the details under &quot;Audit_Log&quot; table such as User id or system account; Machine Details; Event Name; Application Name, and Event data including user entered fields."/>
</node>
<node COLOR="#ff0000" CREATED="1539060442854" ID="ID_532015024" MODIFIED="1550474463694" TEXT="System fails to capture Txn details">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539089688847" ID="ID_684948162" MODIFIED="1550474463695" TEXT="Display appropriate error message / Raise a defect"/>
</node>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1552643196880" HGAP="18" ID="ID_471958715" MODIFIED="1552645202789" POSITION="right" TEXT="Applicant&apos;sConsent" VSHIFT="-552">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1552643268869" HGAP="17" ID="ID_10049981" LINK="https://mosipid.atlassian.net/browse/MOS-14046" MODIFIED="1552645202790" TEXT="MOS-14046" VSHIFT="40">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1552643348795" ID="ID_1257632966" MODIFIED="1552645202789" TEXT="Verify a check box is placed in Ack to capture applicant&apos;s consent">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1552643359594" ID="ID_263875505" MODIFIED="1552645202787" TEXT="Yes">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1552643390823" ID="ID_1193765600" MODIFIED="1552645202786" TEXT="Verify it is unchecked by default">
<edge COLOR="#33cc00"/>
</node>
</node>
<node COLOR="#ff6600" CREATED="1552643361374" ID="ID_125130976" MODIFIED="1552645242805" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff3300" CREATED="1552577239558" ID="ID_160495191" MODIFIED="1552577505305" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1552643412722" HGAP="14" ID="ID_1020364425" MODIFIED="1552645202785" TEXT="Verify the packet meta json file when applicant&apos;s checkbox is checked" VSHIFT="36">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1552643525925" ID="ID_720904162" MODIFIED="1552645202784" TEXT="Attribute applicant&apos;s consent is displayed">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1552643557904" ID="ID_933068231" MODIFIED="1552645202783" TEXT="Yes">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1552643570699" ID="ID_979052195" MODIFIED="1552645282531" TEXT="The value for the attribute &quot;consentOfApplicant&quot; is &quot;Yes&quot;"/>
</node>
<node COLOR="#ff6600" CREATED="1552643563406" ID="ID_859534430" MODIFIED="1552645242804" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1552643565376" ID="ID_1520536620" MODIFIED="1552645258702" TEXT="Raise a defect"/>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1552643412722" HGAP="14" ID="ID_856788067" MODIFIED="1552645202780" TEXT="Verify the packet meta json file when applicant&apos;s checkbox is not checked" VSHIFT="36">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1552643525925" ID="ID_50761026" MODIFIED="1552645202781" TEXT="Attribute applicant&apos;s consent is not displayed">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1552643557904" ID="ID_1448193470" MODIFIED="1552645202781" TEXT="Yes">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1552643570699" ID="ID_507159721" MODIFIED="1552645288017" TEXT="The value for the attribute &quot;consentOfApplicant&quot; is &quot;No&quot;">
<edge COLOR="#33cc00"/>
</node>
</node>
<node COLOR="#ff6600" CREATED="1552643563406" ID="ID_737175132" MODIFIED="1552645242803" TEXT="No">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1552643565376" ID="ID_1920415866" MODIFIED="1552645258701" TEXT="Raise a defect"/>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1538550071542" HGAP="24" ID="ID_1555361299" MODIFIED="1552643262194" TEXT="Verification of Txn details for Audit purpose" VSHIFT="-110">
<edge COLOR="#00cc00"/>
<node COLOR="#33cc00" CREATED="1539003087723" ID="ID_742205553" MODIFIED="1550474395629" TEXT="System capture all Txn details">
<node COLOR="#33cc00" CREATED="1539003122483" ID="ID_1419378128" MODIFIED="1550474395625" TEXT="Store all the details under &quot;Audit_Log&quot; table such as User id or system account; Machine Details; Event Name; Application Name, and Event data including user entered fields."/>
</node>
<node COLOR="#ff0000" CREATED="1539060442854" ID="ID_553509914" MODIFIED="1550474463694" TEXT="System fails to capture Txn details">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539089688847" ID="ID_1415896086" MODIFIED="1550474463695" TEXT="Display appropriate error message / Raise a defect"/>
</node>
</node>
</node>
</node>
</node>
</map>
