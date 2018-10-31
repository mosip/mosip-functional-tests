<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1540618217185" ID="ID_976253437" MODIFIED="1540638459713" TEXT="Induvidual Catagory Selection">
<node COLOR="#cc0000" CREATED="1539322228589" ID="ID_1147981513" MODIFIED="1539323975796" POSITION="left" TEXT="System exception: - Low">
<node COLOR="#cc0000" CREATED="1539322228589" ID="ID_1301099229" MODIFIED="1539322862178" TEXT="1. Unexpected Error"/>
<node COLOR="#cc0000" CREATED="1539322228605" ID="ID_1258186524" MODIFIED="1539322862178" TEXT="2.  Bad Gateway"/>
<node COLOR="#cc0000" CREATED="1539322228605" ID="ID_1977393218" MODIFIED="1539322862178" TEXT="3.  Service Unavailable"/>
<node COLOR="#cc0000" CREATED="1539322228605" ID="ID_1448633837" MODIFIED="1539322862178" TEXT="4. Server Error"/>
<node COLOR="#cc0000" CREATED="1539322228605" ID="ID_1580078245" MODIFIED="1539322862178" TEXT="5. Timeout/Session Expiry"/>
<node COLOR="#cc0000" CREATED="1539322228605" ID="ID_1003828698" MODIFIED="1539322862178" TEXT="6. Others if any"/>
</node>
<node CREATED="1540638462042" ID="ID_930662811" LINK="https://mosipid.atlassian.net/browse/MOS-1015" MODIFIED="1540638547814" POSITION="right" TEXT="MOS-1015">
<node COLOR="#66cc00" CREATED="1540620930212" ID="ID_1401440552" MODIFIED="1540638779430" TEXT="receives a request from the client UI to start a new enrolment">
<node COLOR="#66cc00" CREATED="1540621383254" ID="ID_848923906" MODIFIED="1540798402422" TEXT="validations" VSHIFT="-9">
<arrowlink COLOR="#b0b0b0" DESTINATION="ID_1474973575" ENDARROW="Default" ENDINCLINATION="25;-2;" ID="Arrow_ID_1963723907" STARTARROW="None" STARTINCLINATION="217;0;"/>
<node COLOR="#66cc00" CREATED="1540621419576" HGAP="14" ID="ID_1858907127" MODIFIED="1540634934238" TEXT="time since the last sync(Sync includes Master data, Login credentials, Pre-enrolment data, Enrolment centre config, Enrolment centre setup, User role setup, Policies, Enrolment packet status) from server to client has not exceeded the maximum duration(Configured) permitted" VSHIFT="-29">
<node COLOR="#ff6633" CREATED="1540626458757" HGAP="26" ID="ID_2139464" MODIFIED="1540798194070" TEXT="Time exceeds maximum limit" VSHIFT="12">
<node COLOR="#ff6633" CREATED="1540626501787" ID="ID_1167557645" MODIFIED="1540634988684" TEXT="Time since last sync exceeded maximum limit. Please sync from server before proceeding with this enrolment.(IIC-SHC-SYC&#x200c;-001)"/>
</node>
<node COLOR="#66cc00" CREATED="1540798168206" ID="ID_1483237199" MODIFIED="1540798184092" TEXT="Starts a new enrolment"/>
</node>
<node COLOR="#66cc00" CREATED="1540621538724" ID="ID_725218531" MODIFIED="1540634938869" TEXT="time since the last export of enrolment packets from client to server has not exceeded the maximum duration (configured) permitted">
<node COLOR="#ff6633" CREATED="1540627986034" ID="ID_1913782569" MODIFIED="1540635000377" TEXT="Time since last export of packet exceeded maximum limit">
<node COLOR="#ff6633" CREATED="1540628004331" HGAP="24" ID="ID_633203920" MODIFIED="1540634995915" TEXT="Time since last export of enrolment packets exceeded maximum limit. Please export or upload packets to server before proceeding with this enrolment(IIC-SHC-SYC&#x200c;-002)&#xa;&#xa;&#xa;&#xa;" VSHIFT="-11"/>
</node>
<node COLOR="#66cc00" CREATED="1540798312403" ID="ID_104952678" MODIFIED="1540798326576" TEXT="Start new enrolment"/>
</node>
<node COLOR="#66cc00" CREATED="1540621585232" ID="ID_1189321078" MODIFIED="1540634942907" TEXT="number of enrolment packets on the client yet to be exported to server has not exceeded the maximum(configured) limit">
<node COLOR="#ff6633" CREATED="1540628230519" ID="ID_322779361" MODIFIED="1540635031445" TEXT="Number of packets on client yet to be exported exceeded maximum limit">
<node COLOR="#ff6633" CREATED="1540628257719" ID="ID_1174998997" MODIFIED="1540635035893" TEXT="Maximum limit for enrolment packets on client reached. Please export or upload packets to server before proceeding with this enrolment(IIC-SHC-SYC&#x200c;-003)"/>
</node>
<node COLOR="#66cc00" CREATED="1540798433479" ID="ID_1159696617" MODIFIED="1540798453841" TEXT="System to proceed with the new enrolment"/>
</node>
<node COLOR="#66cc00" CREATED="1540621704303" ID="ID_1239858933" MODIFIED="1540634946906" TEXT="Read the config setting that determines if the geo-location of the machine needs to be captured before every enrolment">
<node COLOR="#ff6633" CREATED="1540629477804" ID="ID_935028171" MODIFIED="1540635045216" TEXT="Geo location mismatch">
<node COLOR="#ff6633" CREATED="1540629497854" ID="ID_815474190" MODIFIED="1540635040253" TEXT="Your client machine&#x2019;s location is outside the enrolment centre. Please note that enrolment can be done only from within the enrolment centre(IIC-SHC-SYC&#x200c;-004)"/>
</node>
<node COLOR="#66cc00" CREATED="1540798538702" ID="ID_134195794" MODIFIED="1540798551910" TEXT="Geo-location matched">
<node COLOR="#66cc00" CREATED="1540798558171" HGAP="23" ID="ID_686041915" MODIFIED="1540798580486" TEXT="Proceed with the new enrolment" VSHIFT="10"/>
</node>
</node>
<node COLOR="#66cc00" CREATED="1540621952645" ID="ID_22466959" MODIFIED="1540634951870" TEXT="Read the config setting that determines if the geo-location of the machine needs to be captured at beginning of day only">
<node COLOR="#66cc00" CREATED="1540623813375" ID="ID_867532866" MODIFIED="1540798871560" TEXT="validate that the beginning-of-day location is within x metres of the Enrolment Centre location">
<linktarget COLOR="#b0b0b0" DESTINATION="ID_867532866" ENDARROW="Default" ENDINCLINATION="538;0;" ID="Arrow_ID_1114312168" SOURCE="ID_1936124918" STARTARROW="None" STARTINCLINATION="538;0;"/>
</node>
<node COLOR="#ff0000" CREATED="1540798647094" HGAP="15" ID="ID_334123956" MODIFIED="1540798741303" TEXT="Geo-location exceeded the x meters of the enrolment centre location" VSHIFT="11"/>
</node>
<node COLOR="#66cc00" CREATED="1540623596048" ID="ID_1936124918" MODIFIED="1540798871559" TEXT="If , capture geo-location of the machine. Validate that the captured location is within x metres of the Enrolment Centre location">
<arrowlink DESTINATION="ID_867532866" ENDARROW="Default" ENDINCLINATION="538;0;" ID="Arrow_ID_1114312168" STARTARROW="None" STARTINCLINATION="538;0;"/>
</node>
<node COLOR="#66cc00" CREATED="1540624416903" HGAP="9" ID="ID_1917819227" MODIFIED="1540634967220" TEXT="On successful validation, send a response that allows the UI to proceed to the next step of choosing a pre-enroled or non pre-enroled applicant" VSHIFT="29"/>
</node>
</node>
</node>
<node CREATED="1540638572842" ID="ID_60194205" LINK="https://mosipid.atlassian.net/browse/MOS-1016" MODIFIED="1540638642195" POSITION="right" TEXT="MOS-1016">
<node COLOR="#66cc00" CREATED="1540632987588" ID="ID_1474973575" MODIFIED="1540798402422" TEXT="receives a request to start new enrolment for a non-pre-enroled applicant" VSHIFT="-19">
<linktarget COLOR="#b0b0b0" DESTINATION="ID_1474973575" ENDARROW="Default" ENDINCLINATION="25;-2;" ID="Arrow_ID_1963723907" SOURCE="ID_848923906" STARTARROW="None" STARTINCLINATION="217;0;"/>
</node>
</node>
<node CREATED="1540638660276" ID="ID_162067035" LINK="https://mosipid.atlassian.net/browse/MOS-1018" MODIFIED="1540638691478" POSITION="right" TEXT="MOS-1018">
<node COLOR="#66cc00" CREATED="1540633464535" ID="ID_1335645451" MODIFIED="1540638699296" TEXT="receives a request from the client UI to start a new enrolment" VSHIFT="12">
<node COLOR="#66cc00" CREATED="1540633517597" ID="ID_1796600987" MODIFIED="1540798929719" TEXT="System intutitively determines if the enrolment is for a child using the date of birth(age&lt;5yrs)" VSHIFT="13">
<node COLOR="#66cc00" CREATED="1540633801006" HGAP="15" ID="ID_890032365" MODIFIED="1540799442504" TEXT="Registration officer must be able to view the appropriate registration form based on the date of birth entered" VSHIFT="-7"/>
<node COLOR="#ff0000" CREATED="1540799332435" ID="ID_603972154" MODIFIED="1540799431731" TEXT="Registration Officer unable to view the forms based on Date of Birth">
<node COLOR="#ff0000" CREATED="1540799410580" HGAP="19" ID="ID_1359541853" MODIFIED="1540799447331" TEXT="Displays an error message" VSHIFT="13"/>
</node>
</node>
</node>
</node>
</node>
</map>
