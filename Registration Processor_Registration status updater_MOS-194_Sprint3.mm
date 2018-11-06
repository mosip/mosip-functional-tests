<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1539317706602" ID="ID_1294764880" LINK="../../Exception.png" MODIFIED="1539319020004" TEXT="Registration status updater">
<node CREATED="1539321547675" ID="ID_1538534513" LINK="https://mosipid.atlassian.net/browse/MOS-182" MODIFIED="1539321578519" POSITION="right" TEXT="MOS-182">
<node COLOR="#339900" CREATED="1539318014637" ID="ID_1954538797" MODIFIED="1539320331484" TEXT="receives a request to update the status of the Registration Packet from a Component internal to the Registration Processor">
<node COLOR="#339900" CREATED="1539318069521" HGAP="14" ID="ID_638093809" MODIFIED="1539319592180" TEXT="using the Registration Id updates the Registration Table" VSHIFT="-3">
<node COLOR="#339900" CREATED="1539319932790" HGAP="22" ID="ID_1321863157" MODIFIED="1539319959076" TEXT="create Log and Audit" VSHIFT="10"/>
</node>
<node COLOR="#339900" CREATED="1539318086842" HGAP="30" ID="ID_748928109" MODIFIED="1539320091517" TEXT="creates a Transaction Record in the Registration Transaction Table" VSHIFT="-1">
<node COLOR="#339900" CREATED="1539319932790" HGAP="22" ID="ID_152680081" MODIFIED="1539319959076" TEXT="create Log and Audit" VSHIFT="10"/>
</node>
<node COLOR="#cc0000" CREATED="1539318487176" ID="ID_1177855633" MODIFIED="1539319633972" TEXT="Registration Packet Id Not Found&#x9;">
<node COLOR="#cc0000" CREATED="1539318595772" ID="ID_1328834221" MODIFIED="1539319639837" TEXT="The Registration Packet Id is not present in Registration Table" VSHIFT="19">
<node COLOR="#cc0000" CREATED="1539319932790" HGAP="22" ID="ID_1139670315" MODIFIED="1539320172104" TEXT="create Log and Audit" VSHIFT="10"/>
</node>
</node>
</node>
</node>
<node COLOR="#cc0000" CREATED="1539322412824" ID="ID_111338964" MODIFIED="1539322600151" POSITION="right" TEXT="System exception:">
<node COLOR="#cc0000" CREATED="1539322412825" ID="ID_718497256" MODIFIED="1539322604536" TEXT="Unexpected Error"/>
<node COLOR="#cc0000" CREATED="1539322412826" ID="ID_1871026545" MODIFIED="1539322607513" TEXT="Bad Gateway"/>
<node COLOR="#cc0000" CREATED="1539322412826" ID="ID_62534182" MODIFIED="1539322610565" TEXT="Service Unavailable"/>
<node COLOR="#cc0000" CREATED="1539322412826" ID="ID_597416733" MODIFIED="1539322614401" TEXT=" Server Error"/>
<node COLOR="#cc0000" CREATED="1539322412827" ID="ID_1039566042" MODIFIED="1539322622263" TEXT=" Timeout/Session Expiry"/>
<node COLOR="#cc0000" CREATED="1539322412827" ID="ID_630191292" MODIFIED="1539322625424" TEXT="Others if any"/>
</node>
</node>
</map>
