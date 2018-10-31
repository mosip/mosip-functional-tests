<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1540270309291" ID="ID_1475792555" MODIFIED="1540270341737" TEXT="Connectivity check">
<node CREATED="1540270323955" ID="ID_1483052246" LINK="https://mosipid.atlassian.net/browse/MOS-63" MODIFIED="1540271823477" POSITION="right" TEXT="MOS-63">
<node COLOR="#999900" CREATED="1540270511519" HGAP="29" ID="ID_236189947" LINK="https://mosipid.atlassian.net/browse/MOS-63" MODIFIED="1540281668218" TEXT="Receive request to determine if client machine is online" VSHIFT="-18">
<node COLOR="#999900" CREATED="1540270744752" HGAP="26" ID="ID_1426035294" MODIFIED="1540271969975" TEXT="Performs checks to determine online connectivity" VSHIFT="-13">
<node COLOR="#999900" CREATED="1540270788631" HGAP="23" ID="ID_430066581" MODIFIED="1540271980194" TEXT="try to access URL for service being called" VSHIFT="-11">
<node COLOR="#999900" CREATED="1540271276784" ID="ID_1070526722" MODIFIED="1540271985429" TEXT="Wait for a configured number of seconds for a response" VSHIFT="-13">
<node COLOR="#999900" CREATED="1540271304043" HGAP="24" ID="ID_1918464827" MODIFIED="1540271997675" TEXT=" response received within configured time" VSHIFT="4">
<node COLOR="#999900" CREATED="1540271340543" ID="ID_1693681716" MODIFIED="1540272012986" TEXT="return result as online" VSHIFT="-14">
<node COLOR="#999900" CREATED="1540271559613" HGAP="21" ID="ID_1652668166" MODIFIED="1540272017635" TEXT="Provides results of validation to the requesting source." VSHIFT="-12">
<node COLOR="#999900" CREATED="1540271772482" HGAP="23" ID="ID_737686247" MODIFIED="1540272021351" TEXT="Capture audit logs" VSHIFT="-11"/>
</node>
</node>
</node>
<node COLOR="#cc3300" CREATED="1540271381242" ID="ID_1035074909" MODIFIED="1540272083586" TEXT="response not received within configured time">
<node COLOR="#cc3300" CREATED="1540271418048" HGAP="22" ID="ID_329799284" MODIFIED="1540272080490" TEXT="return result as offline" VSHIFT="14">
<node COLOR="#cc3300" CREATED="1540271599697" HGAP="22" ID="ID_1769853459" MODIFIED="1540272071431" TEXT="Provides results of validation to the requesting source." VSHIFT="-14">
<node COLOR="#cc3300" CREATED="1540271772482" HGAP="22" ID="ID_1856850420" MODIFIED="1540272066833" TEXT="Capture audit logs" VSHIFT="-7"/>
</node>
</node>
</node>
<node COLOR="#cc3300" CREATED="1540271427184" ID="ID_624501102" MODIFIED="1540272089959" TEXT="unable to determine the state">
<node COLOR="#cc3300" CREATED="1540271454932" ID="ID_595640464" MODIFIED="1540272093490" TEXT="return result as offline" VSHIFT="15">
<node COLOR="#cc3300" CREATED="1540271607628" HGAP="18" ID="ID_440169490" MODIFIED="1540272097339" TEXT="Provides results of validation to the requesting source." VSHIFT="17">
<node COLOR="#cc3300" CREATED="1540271772482" ID="ID_629942584" MODIFIED="1540272102769" TEXT="Capture audit logs" VSHIFT="-3"/>
</node>
</node>
</node>
</node>
</node>
</node>
</node>
</node>
<node COLOR="#cc0000" CREATED="1539322412824" ID="ID_111338964" MODIFIED="1539322548173" POSITION="left" TEXT="System exception:">
<node COLOR="#cc0000" CREATED="1539322412825" ID="ID_718497256" MODIFIED="1539322562699" TEXT="Unexpected Error"/>
<node COLOR="#cc0000" CREATED="1539322412826" ID="ID_1871026545" MODIFIED="1539322559359" TEXT="Bad Gateway"/>
<node COLOR="#cc0000" CREATED="1539322412826" ID="ID_62534182" MODIFIED="1539322555514" TEXT="Service Unavailable"/>
<node COLOR="#cc0000" CREATED="1539322412826" ID="ID_597416733" MODIFIED="1539322567147" TEXT=" Server Error"/>
<node COLOR="#cc0000" CREATED="1539322412827" ID="ID_1039566042" MODIFIED="1539322571359" TEXT=" Timeout/Session Expiry"/>
<node COLOR="#cc0000" CREATED="1539322412827" ID="ID_630191292" MODIFIED="1539322575543" TEXT="Others if any"/>
</node>
</node>
</map>
