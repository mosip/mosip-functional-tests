<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1551175427770" ID="ID_1227162637" MODIFIED="1551175427770" TEXT="New Mindmap">
<node CREATED="1551174647448" ID="ID_1974322329" LINK="https://mosipid.atlassian.net/browse/MOS-13661" MODIFIED="1551175881397" POSITION="right" TEXT="MOS-13661">
<edge COLOR="#66cc00"/>
<node COLOR="#66cc00" CREATED="1551174681349" HGAP="8" ID="ID_1848560293" MODIFIED="1551175894605" TEXT="GEO location capture as configurable parameter" VSHIFT="-59">
<node COLOR="#66cc00" CREATED="1551173723578" ID="ID_343609622" MODIFIED="1551174468954" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1551173735708" ID="ID_169409031" MODIFIED="1551175915965" TEXT="Set the parameter &quot;GEO_CAP_FREQ&quot; to Yes / No based on center requirement"/>
</node>
<node COLOR="#ff6600" CREATED="1551173726033" ID="ID_622846585" MODIFIED="1551175152204" TEXT="No">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1551173729731" ID="ID_1225650615" MODIFIED="1551174521839" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#66cc00" CREATED="1551174810714" ID="ID_1317374232" MODIFIED="1551182336065" TEXT="GEO location status">
<node COLOR="#66cc00" CREATED="1551182336065" ID="ID_575308782" MODIFIED="1551182369776" TEXT="New Registration">
<node COLOR="#66cc00" CREATED="1551174851092" HGAP="18" ID="ID_516645120" MODIFIED="1551179075084" TEXT="On" VSHIFT="-46">
<node COLOR="#66cc00" CREATED="1551174919025" ID="ID_1254951483" MODIFIED="1551179946072" TEXT="Verify the system captures the geo location and validates location is within configured limits of the master data.">
<node COLOR="#66cc00" CREATED="1551175012745" ID="ID_258783377" MODIFIED="1551175244818" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1551175047336" ID="ID_1112821673" MODIFIED="1551179877561" TEXT="If the GEO location is within limit, then allow the registration and store it in packet"/>
</node>
<node COLOR="#ff6600" CREATED="1551175014546" ID="ID_887340391" MODIFIED="1551175167363" TEXT="No">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1551173729731" ID="ID_1328881605" MODIFIED="1551174521839" TEXT="Raise a defect"/>
</node>
</node>
</node>
<node COLOR="#66cc00" CREATED="1551174853419" HGAP="22" ID="ID_484681038" MODIFIED="1551179965681" TEXT="Off" VSHIFT="14">
<node COLOR="#66cc00" CREATED="1551174919025" ID="ID_1524152742" MODIFIED="1551182183834" TEXT="Verify the system captures the geo location and validates location is within configured limits of the master data." VSHIFT="-9">
<node COLOR="#66cc00" CREATED="1551175012745" ID="ID_1130835149" MODIFIED="1551182147692" TEXT="No">
<node COLOR="#66cc00" CREATED="1551175047336" ID="ID_1964275075" MODIFIED="1551182171993" TEXT="Allow the registration and do not store any geo location information in packet"/>
</node>
<node COLOR="#ff6600" CREATED="1551175014546" ID="ID_1169246562" MODIFIED="1551182144615" TEXT="Yes">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1551173729731" ID="ID_707340592" MODIFIED="1551174521839" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#66cc00" CREATED="1551182190714" ID="ID_347265008" MODIFIED="1551182320583" TEXT="Geo location capture at beginning of day / registration available in logs">
<node COLOR="#66cc00" CREATED="1551175012745" ID="ID_649706076" MODIFIED="1551182147692" TEXT="No">
<node COLOR="#66cc00" CREATED="1551175047336" ID="ID_1767064296" MODIFIED="1551182258928" TEXT="packet should not store any geo location information in packet"/>
</node>
<node COLOR="#ff6600" CREATED="1551175014546" ID="ID_1858754825" MODIFIED="1551182144615" TEXT="Yes">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1551173729731" ID="ID_1730424903" MODIFIED="1551174521839" TEXT="Raise a defect"/>
</node>
</node>
</node>
</node>
<node COLOR="#66cc00" CREATED="1551182353881" ID="ID_456006384" MODIFIED="1551182369776" TEXT="UIN Update">
<node COLOR="#66cc00" CREATED="1551174851092" HGAP="18" ID="ID_1817243081" MODIFIED="1551179075084" TEXT="On" VSHIFT="-46">
<node COLOR="#66cc00" CREATED="1551174919025" ID="ID_1736058686" MODIFIED="1551179946072" TEXT="Verify the system captures the geo location and validates location is within configured limits of the master data.">
<node COLOR="#66cc00" CREATED="1551175012745" ID="ID_417416591" MODIFIED="1551175244818" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1551175047336" ID="ID_1809134954" MODIFIED="1551179877561" TEXT="If the GEO location is within limit, then allow the registration and store it in packet"/>
</node>
<node COLOR="#ff6600" CREATED="1551175014546" ID="ID_1999902165" MODIFIED="1551175167363" TEXT="No">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1551173729731" ID="ID_884167116" MODIFIED="1551174521839" TEXT="Raise a defect"/>
</node>
</node>
</node>
<node COLOR="#66cc00" CREATED="1551174853419" HGAP="22" ID="ID_1848034819" MODIFIED="1551179965681" TEXT="Off" VSHIFT="14">
<node COLOR="#66cc00" CREATED="1551174919025" ID="ID_1802136581" MODIFIED="1551182183834" TEXT="Verify the system captures the geo location and validates location is within configured limits of the master data." VSHIFT="-9">
<node COLOR="#66cc00" CREATED="1551175012745" ID="ID_629746806" MODIFIED="1551182147692" TEXT="No">
<node COLOR="#66cc00" CREATED="1551175047336" ID="ID_1321789944" MODIFIED="1551182171993" TEXT="Allow the registration and do not store any geo location information in packet"/>
</node>
<node COLOR="#ff6600" CREATED="1551175014546" ID="ID_314860777" MODIFIED="1551182144615" TEXT="Yes">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1551173729731" ID="ID_1653413424" MODIFIED="1551174521839" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#66cc00" CREATED="1551182190714" ID="ID_1359077734" MODIFIED="1551182320583" TEXT="Geo location capture at beginning of day / registration available in logs">
<node COLOR="#66cc00" CREATED="1551175012745" ID="ID_557130233" MODIFIED="1551182147692" TEXT="No">
<node COLOR="#66cc00" CREATED="1551175047336" ID="ID_578138824" MODIFIED="1551182258928" TEXT="packet should not store any geo location information in packet"/>
</node>
<node COLOR="#ff6600" CREATED="1551175014546" ID="ID_738554134" MODIFIED="1551182144615" TEXT="Yes">
<edge COLOR="#ff3300"/>
<node COLOR="#ff3300" CREATED="1551173729731" ID="ID_1912619383" MODIFIED="1551174521839" TEXT="Raise a defect"/>
</node>
</node>
</node>
</node>
</node>
<node COLOR="#33cc00" CREATED="1538550071542" HGAP="13" ID="ID_1021783307" MODIFIED="1551182186745" TEXT="Verification of Txn details for Audit purpose" VSHIFT="77">
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
