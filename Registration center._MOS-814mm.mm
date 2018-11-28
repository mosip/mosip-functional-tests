<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1542279159077" ID="ID_207839998" MODIFIED="1542279228369" TEXT="Registration center">
<node COLOR="#339900" CREATED="1542279698948" ID="ID_1191680768" MODIFIED="1542282290638" POSITION="right" TEXT="Individual navigates to the &#x201c;Home Page&#x201d; post Login">
<node COLOR="#339900" CREATED="1542280092277" ID="ID_519804181" MODIFIED="1542282307828" TEXT="Individual will search for Registration Center which he wants take appointment from" VSHIFT="18">
<node COLOR="#339900" CREATED="1542280581347" HGAP="12" ID="ID_431839828" MODIFIED="1542282350062" TEXT="System should provide the User with the drop-down of search criteria, with which user can search" VSHIFT="-13">
<node COLOR="#339900" CREATED="1542280914907" ID="ID_1396491366" MODIFIED="1542282353185" TEXT="System should allow the user to input the search text and search" VSHIFT="18">
<node COLOR="#339900" CREATED="1542280109274" HGAP="19" ID="ID_1957661364" MODIFIED="1542282356551" TEXT="Individual can select one of the search criteria(Province, City, Local Administrative Authority, Postal code) and will input the search text in the text field" VSHIFT="-23">
<node COLOR="#339900" CREATED="1542280127439" ID="ID_716533204" MODIFIED="1542282360021" TEXT="Individual can chose to search nearby centers by clicking on &apos;show nearby centers&apos;">
<node COLOR="#339900" CREATED="1542281332056" ID="ID_1206539877" MODIFIED="1542282363679" TEXT="System will prompt the user to enable location, if the Location sharing is disabled in the device/machine">
<node COLOR="#339900" CREATED="1542281094784" ID="ID_1632954040" MODIFIED="1542282369652" TEXT="System should check for Lat-Long values of the User. then system should fetch all the Registration centers within 2 KM Radius">
<node COLOR="#339900" CREATED="1542280351943" HGAP="22" ID="ID_1322816687" MODIFIED="1542282374136" TEXT="System will display the Registration centers based on the search in an tabular format(Registration center name, Address,Working Hours, Contact Person, Center Type, Contact Number)." VSHIFT="11">
<node COLOR="#339900" CREATED="1542280426825" ID="ID_1080921335" MODIFIED="1542282382236" TEXT="Individual can chose to view the Registration center on a Map by selecting Registration center">
<node COLOR="#339900" CREATED="1542281401693" HGAP="22" ID="ID_139942772" MODIFIED="1542282385699" TEXT="system should audit data" VSHIFT="25"/>
</node>
</node>
<node COLOR="#cc0000" CREATED="1542282020050" ID="ID_196056184" MODIFIED="1542282403837" TEXT="No registration centers displayed">
<node COLOR="#cc0000" CREATED="1542282043591" HGAP="25" ID="ID_1068367110" MODIFIED="1542692982335" TEXT="System displays a message &quot;No results found&quot; with error code : PRG-ECI&#x200c;-001" VSHIFT="23"/>
</node>
</node>
<node COLOR="#cc0000" CREATED="1542282144346" ID="ID_1833266242" MODIFIED="1542282411239" TEXT="Location disabled">
<node COLOR="#cc0000" CREATED="1542282166850" HGAP="19" ID="ID_1797963312" MODIFIED="1542282415313" TEXT="System displays an error message &quot; Location not enabled.&quot; with error code : PRG-ECI&#x200c;-002" VSHIFT="11"/>
</node>
</node>
</node>
<node COLOR="#cc0000" CREATED="1542281802020" ID="ID_330455760" MODIFIED="1542282421330" TEXT="User unable to select the seach criteria">
<node COLOR="#cc0000" CREATED="1542281881980" ID="ID_1101683015" MODIFIED="1542282424762" TEXT="System throws an exception" VSHIFT="12"/>
</node>
</node>
<node COLOR="#cc0000" CREATED="1542281644041" ID="ID_74346342" MODIFIED="1542282430767" TEXT="User unable to input the text">
<node COLOR="#cc0000" CREATED="1542281685011" ID="ID_243984823" MODIFIED="1542282433534" TEXT="System throws an exception" VSHIFT="25"/>
</node>
</node>
</node>
<node COLOR="#cc0000" CREATED="1542281506661" HGAP="13" ID="ID_1505555346" MODIFIED="1542282440570" TEXT="Registration center not found" VSHIFT="-70">
<node COLOR="#cc0000" CREATED="1542281527565" HGAP="29" ID="ID_945618305" MODIFIED="1542692820546" TEXT="System shows &quot;No results found&quot;" VSHIFT="16"/>
</node>
</node>
</node>
</node>
</map>
