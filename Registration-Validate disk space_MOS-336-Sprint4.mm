<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1540624445820" ID="ID_1860717240" MODIFIED="1540629336683" TEXT="Validate disk space">
<node COLOR="#ff3300" CREATED="1540629339784" ID="ID_701161734" MODIFIED="1540629546882" POSITION="left" TEXT="System Exception">
<node COLOR="#ff3300" CREATED="1540629381093" ID="ID_1745298578" MODIFIED="1540629553469" TEXT="Unexpected Error"/>
<node COLOR="#ff3300" CREATED="1540629416656" ID="ID_1218193348" MODIFIED="1540629559543" TEXT="Bad Gateway"/>
<node COLOR="#ff3300" CREATED="1540629420485" ID="ID_1259604675" MODIFIED="1540629564939" TEXT="Service Un-available"/>
<node COLOR="#ff3300" CREATED="1540629430195" ID="ID_1184785876" MODIFIED="1540629570315" TEXT="Server Error"/>
<node COLOR="#ff3300" CREATED="1540629433943" ID="ID_47831026" MODIFIED="1540629575774" TEXT="Timeout/Session expiry"/>
<node COLOR="#ff3300" CREATED="1540629439277" ID="ID_943465890" MODIFIED="1540629581836" TEXT="Others if any"/>
</node>
<node COLOR="#66cc00" CREATED="1540635308538" ID="ID_1846948433" LINK="https://mosipid.atlassian.net/browse/MOS-336" MODIFIED="1540636737451" POSITION="right" TEXT="MOS-336">
<node COLOR="#66cc00" CREATED="1540627352104" ID="ID_1403360760" MODIFIED="1540629602773" TEXT="UI request to create enrolment packet">
<node COLOR="#66cc00" CREATED="1540627680524" ID="ID_276033892" MODIFIED="1540634810386" TEXT="System calculates the size of the potential enrolment packet.">
<arrowlink DESTINATION="ID_276033892" ENDARROW="Default" ENDINCLINATION="0;0;" ID="Arrow_ID_1406220872" STARTARROW="None" STARTINCLINATION="0;0;"/>
<linktarget COLOR="#b0b0b0" DESTINATION="ID_276033892" ENDARROW="Default" ENDINCLINATION="0;0;" ID="Arrow_ID_1406220872" SOURCE="ID_276033892" STARTARROW="None" STARTINCLINATION="0;0;"/>
<node COLOR="#66cc00" CREATED="1540627829513" ID="ID_242523040" MODIFIED="1540634821068" TEXT=" System calculates the disk space available in the configured packet storage location.">
<node COLOR="#66cc00" CREATED="1540627985133" ID="ID_993866823" MODIFIED="1540634974495" TEXT="System validates that the disk space available  is sufficient to store a packet of the size calculated.">
<node COLOR="#66cc00" CREATED="1540628245031" ID="ID_615790940" MODIFIED="1540629733679" TEXT="Yes ">
<node COLOR="#66cc00" CREATED="1540628371278" ID="ID_861171129" MODIFIED="1540629739504" TEXT="System returns a success response "/>
</node>
<node COLOR="#ff3300" CREATED="1540628254484" ID="ID_1942031315" MODIFIED="1540629753033" TEXT="No">
<node COLOR="#ff3300" CREATED="1540628418784" ID="ID_1103783527" MODIFIED="1540629759361" TEXT="Insufficient disk space to store enrolment packet. Please retry after deleting unwanted files from your disk."/>
</node>
</node>
</node>
</node>
</node>
</node>
</node>
</map>
