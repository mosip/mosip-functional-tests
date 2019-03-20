<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1551174597335" ID="ID_758248646" MODIFIED="1551174642539" TEXT="Registration - UIN update">
<node CREATED="1551174647448" ID="ID_1974322329" LINK="https://mosipid.atlassian.net/browse/MOS-13660" MODIFIED="1551175140210" POSITION="right" TEXT="MOS-13660">
<edge COLOR="#66cc00"/>
<node COLOR="#66cc00" CREATED="1551174681349" HGAP="8" ID="ID_1848560293" MODIFIED="1551174782297" TEXT="UIN update as configurable parameter" VSHIFT="-59">
<node COLOR="#66cc00" CREATED="1551173723578" ID="ID_343609622" MODIFIED="1551174468954" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1551173735708" ID="ID_169409031" MODIFIED="1551174724706" TEXT="Set the parameter &quot;UIN_UPDATE_CONFIG_FLAG&quot; to Yes / No based on center requirement"/>
</node>
<node COLOR="#ff6600" CREATED="1551173726033" ID="ID_622846585" MODIFIED="1551175152204" TEXT="No">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1551173729731" ID="ID_1225650615" MODIFIED="1551174521839" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#66cc00" CREATED="1551174810714" ID="ID_1317374232" MODIFIED="1551175192739" TEXT="Logged in as RO / Sup">
<node COLOR="#66cc00" CREATED="1551174845499" ID="ID_1529518609" MODIFIED="1551175192738" TEXT="UIN Update">
<node COLOR="#66cc00" CREATED="1551174851092" HGAP="22" ID="ID_516645120" MODIFIED="1551175192738" TEXT="On" VSHIFT="-11">
<node COLOR="#66cc00" CREATED="1551174919025" ID="ID_1254951483" MODIFIED="1551175192738" TEXT="Verify  &quot;UIN Update&quot; tab is displayed.">
<node COLOR="#66cc00" CREATED="1551175012745" ID="ID_258783377" MODIFIED="1551175244818" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1551175047336" ID="ID_1112821673" MODIFIED="1551175244819" TEXT="Click the UIN update tab, the related fields should get displayed"/>
</node>
<node COLOR="#ff6600" CREATED="1551175014546" ID="ID_887340391" MODIFIED="1551175167363" TEXT="No">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1551173729731" ID="ID_1328881605" MODIFIED="1551174521839" TEXT="Raise a defect"/>
</node>
</node>
</node>
<node COLOR="#66cc00" CREATED="1551174853419" HGAP="21" ID="ID_80297367" MODIFIED="1551175192738" TEXT="Off" VSHIFT="16">
<node COLOR="#66cc00" CREATED="1551175082010" ID="ID_1235271518" MODIFIED="1551175192738" TEXT="Verify &quot;UIN Update&quot; tab is displayed ">
<node COLOR="#ff6600" CREATED="1551175012745" ID="ID_1148426384" MODIFIED="1551175167363" TEXT="Yes">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1551175047336" ID="ID_1279828293" MODIFIED="1551175175956" TEXT="Raise a defect"/>
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
<node CREATED="1552636640747" ID="ID_108998130" LINK="https://mosipid.atlassian.net/browse/MOS-12984" MODIFIED="1552636649339" POSITION="left" TEXT="MOS-12984">
<node COLOR="#33cc00" CREATED="1552641678466" HGAP="12" ID="ID_125924942" MODIFIED="1552642395476" TEXT="Verify the fields in UIN update tab is configurable" VSHIFT="-48">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1552641691982" ID="ID_59635235" MODIFIED="1552642395475" TEXT="Yes">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1552641708456" ID="ID_1337974136" MODIFIED="1552642395475" TEXT="Configure as per requirement using ">
<edge COLOR="#33cc00"/>
</node>
</node>
<node COLOR="#ff6600" CREATED="1551173726033" ID="ID_884756627" MODIFIED="1551175152204" TEXT="No">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1551173729731" ID="ID_827098064" MODIFIED="1551174521839" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#33cc00" CREATED="1552642155827" HGAP="16" ID="ID_1239077587" MODIFIED="1552642395475" TEXT="Behavior of UIN update node when no field is configurable" VSHIFT="-33">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1552642199471" ID="ID_1545688565" MODIFIED="1552642395475" TEXT="UIN update link should be clickable">
<edge COLOR="#33cc00"/>
<node COLOR="#ff6600" CREATED="1551173726033" ID="ID_753231785" MODIFIED="1551175152204" TEXT="No">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1551173729731" ID="ID_785640422" MODIFIED="1551174521839" TEXT="Raise a defect"/>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1552642155827" HGAP="16" ID="ID_1639793841" MODIFIED="1552642395473" TEXT="Behavior of UIN update node when only one field is configurable" VSHIFT="-33">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1552642199471" ID="ID_1857483005" MODIFIED="1552642395475" TEXT="The only available field should be selected and disabled">
<edge COLOR="#33cc00"/>
<node COLOR="#ff6600" CREATED="1551173726033" ID="ID_1577182706" MODIFIED="1551175152204" TEXT="No">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1551173729731" ID="ID_1645354382" MODIFIED="1551174521839" TEXT="Raise a defect"/>
</node>
<node COLOR="#33cc00" CREATED="1552642284142" ID="ID_341450464" MODIFIED="1552642395475" TEXT="Yes">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1552642287386" ID="ID_1210156518" MODIFIED="1552642395474" TEXT="Proceed with UIN update for the selected field">
<edge COLOR="#33cc00"/>
</node>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1552642302825" ID="ID_541948955" MODIFIED="1552642395474" TEXT="Behavior of UIN update when multiple fields are configurable">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1552642320425" ID="ID_1869789564" MODIFIED="1552642395474" TEXT="App allows the user to select as per user requirement">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1552642336584" ID="ID_191905849" MODIFIED="1552642395474" TEXT="Yes">
<edge COLOR="#33cc00"/>
<node COLOR="#33cc00" CREATED="1552642287386" ID="ID_481371015" MODIFIED="1552642395474" TEXT="Proceed with UIN update for the selected field">
<edge COLOR="#33cc00"/>
</node>
</node>
<node COLOR="#ff6600" CREATED="1551173726033" ID="ID_1503379144" MODIFIED="1551175152204" TEXT="No">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1551173729731" ID="ID_355532217" MODIFIED="1551174521839" TEXT="Raise a defect"/>
</node>
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
