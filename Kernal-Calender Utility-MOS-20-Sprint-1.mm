<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node COLOR="#009900" CREATED="1540622360941" ID="ID_1210272246" MODIFIED="1540626371600" TEXT="Kernal">
<node COLOR="#009900" CREATED="1540622954192" ID="ID_44445556" LINK="https://mosipid.atlassian.net/browse/MOS-20." MODIFIED="1540626371600" POSITION="right" TEXT="MOS-20">
<node COLOR="#009900" CREATED="1540622444271" ID="ID_207909348" MODIFIED="1540626371600" TEXT="Calendar utility">
<node COLOR="#009900" CREATED="1540624924490" ID="ID_693831857" MODIFIED="1540626371600" TEXT="Identify Calendar util methods">
<node COLOR="#009900" CREATED="1540624935405" HGAP="7" ID="ID_1968832029" MODIFIED="1540634268064" TEXT="Create wrapper class for methods defined in apache-commons Calendar util" VSHIFT="-6">
<node COLOR="#009900" CREATED="1540622456354" ID="ID_910920075" MODIFIED="1540626371600" TEXT="public static Calendar getCeiling(Calendar date, int field)">
<node COLOR="#cc0000" CREATED="1540625547723" ID="ID_1678467303" MODIFIED="1540626299734" TEXT="Error message-Calendar Argument in null-COK-UTL-CAL-001"/>
<node COLOR="#cc0000" CREATED="1540625824190" ID="ID_594419669" MODIFIED="1540626299734" TEXT="Error message-Year is over 280 Million-COK-UTL-CAL-002"/>
</node>
<node COLOR="#009900" CREATED="1540622456370" ID="ID_1766464442" MODIFIED="1540626371600" TEXT="public static long getFragmentInDays(Calendar calendar, int fragment)">
<node COLOR="#cc0000" CREATED="1540625547723" ID="ID_246994166" MODIFIED="1540626299734" TEXT="Error message-Calendar Argument in null-COK-UTL-CAL-003"/>
<node COLOR="#cc0000" CREATED="1540625896557" ID="ID_1510794759" MODIFIED="1540626299734" TEXT="Error message-fragment is not supported-COK-UTL-CAL-003"/>
</node>
<node COLOR="#009900" CREATED="1540622456370" ID="ID_1112093795" MODIFIED="1540626371600" TEXT="public static long getFragmentInHours(Calendar calendar, int fragment)">
<node COLOR="#cc0000" CREATED="1540625547723" ID="ID_388546795" MODIFIED="1540626431486" TEXT="Error message-Calendar Argument in null-COK-UTL-CAL-004"/>
<node COLOR="#cc0000" CREATED="1540625896557" ID="ID_582556757" MODIFIED="1540626435506" TEXT="Error message-fragment is not supported-COK-UTL-CAL-004"/>
</node>
<node COLOR="#009900" CREATED="1540622456370" ID="ID_1147916754" MODIFIED="1540626371600" TEXT="public static long getFragmentInMilliseconds(Calendar calendar, int fragment)">
<node COLOR="#cc0000" CREATED="1540625547723" ID="ID_195531488" MODIFIED="1540626454535" TEXT="Error message-Calendar Argument in null-COK-UTL-CAL-005"/>
<node COLOR="#cc0000" CREATED="1540625896557" ID="ID_1859360277" MODIFIED="1540626457804" TEXT="Error message-fragment is not supported-COK-UTL-CAL-005"/>
</node>
<node COLOR="#009900" CREATED="1540622456370" ID="ID_602283202" MODIFIED="1540626371600" TEXT="public static long getFragmentInMinutes(Calendar calendar, int fragment)">
<node COLOR="#cc0000" CREATED="1540625547723" ID="ID_1954683055" MODIFIED="1540626461550" TEXT="Error message-Calendar Argument in null-COK-UTL-CAL-006"/>
<node COLOR="#cc0000" CREATED="1540625896557" ID="ID_1302379850" MODIFIED="1540626465091" TEXT="Error message-fragment is not supported-COK-UTL-CAL-006"/>
</node>
<node COLOR="#009900" CREATED="1540622456370" ID="ID_840064415" MODIFIED="1540626371600" TEXT="public static long getFragmentInSeconds(Calendar calendar, int fragment)">
<node COLOR="#cc0000" CREATED="1540625547723" ID="ID_1591006935" MODIFIED="1540626474630" TEXT="Error message-Calendar Argument in null-COK-UTL-CAL-007"/>
<node COLOR="#cc0000" CREATED="1540625896557" ID="ID_1608074228" MODIFIED="1540626482130" TEXT="Error message-fragment is not supported-COK-UTL-CAL-007"/>
</node>
<node COLOR="#009900" CREATED="1540622456370" ID="ID_731555030" MODIFIED="1540626371600" TEXT="public static boolean isSameDay(Calendar cal1, Calendar cal2)">
<node COLOR="#cc0000" CREATED="1540626161986" ID="ID_1653948128" MODIFIED="1540626504398" TEXT="Error message-Input parameter is null-COK-UTL-CAL-008"/>
</node>
<node COLOR="#009900" CREATED="1540622456370" ID="ID_817885118" MODIFIED="1540626371600" TEXT="public static boolean isSameInstant(Calendar cal1, Calendar cal2)">
<node COLOR="#cc0000" CREATED="1540626161986" ID="ID_1661805595" MODIFIED="1540626541672" TEXT="Error message-Input parameter is null-COK-UTL-CAL-009"/>
</node>
<node COLOR="#009900" CREATED="1540622456370" ID="ID_1600198376" MODIFIED="1540626371600" TEXT="public static boolean isSameLocalTime(Calendar cal1, Calendar cal2)">
<node COLOR="#cc0000" CREATED="1540626161986" ID="ID_1721048214" MODIFIED="1540626558621" TEXT="Error message-Input parameter is null-COK-UTL-CAL-010"/>
</node>
<node COLOR="#009900" CREATED="1540622456370" ID="ID_710573426" MODIFIED="1540626371600" TEXT="public static Calendar getRound(Calendar date, int field)">
<node COLOR="#cc0000" CREATED="1540625547723" ID="ID_633619469" MODIFIED="1540626581752" TEXT="Error message-Calendar Argument in null-COK-UTL-CAL-011"/>
<node COLOR="#cc0000" CREATED="1540625896557" ID="ID_1490814517" MODIFIED="1540626585490" TEXT="Error message-fragment is not supported-COK-UTL-CAL-011"/>
</node>
<node COLOR="#009900" CREATED="1540622456370" ID="ID_1693354010" MODIFIED="1540626371600" TEXT="public static Calendar toCalendar(Date date)">
<node COLOR="#cc0000" CREATED="1540625547723" ID="ID_311575836" MODIFIED="1540626675260" TEXT="Error message-Date is null-COK-UTL-CAL-012"/>
</node>
<node COLOR="#009900" CREATED="1540622456370" ID="ID_390958328" MODIFIED="1540626371600" TEXT="public static Calendar toCalendar(Date date, TimeZone tz)">
<node COLOR="#cc0000" CREATED="1540625547723" ID="ID_45954034" MODIFIED="1540626701018" TEXT="Error message-Date or Timezone in null-COK-UTL-CAL-013"/>
</node>
<node COLOR="#009900" CREATED="1540622456370" ID="ID_307627852" MODIFIED="1540626371600" TEXT="public static Calendar truncate(Calendar date, int field)">
<node COLOR="#cc0000" CREATED="1540625547723" ID="ID_1447272335" MODIFIED="1540626720145" TEXT="Error message-Calendar Argument in null-COK-UTL-CAL-014"/>
</node>
<node COLOR="#009900" CREATED="1540622456370" ID="ID_73986192" MODIFIED="1540626371600" TEXT="public static boolean truncatedEquals(Calendar cal1, Calendar cal2, int field)">
<node COLOR="#cc0000" CREATED="1540626161986" ID="ID_1795399640" MODIFIED="1540626734163" TEXT="Error message-Input parameter is null-COK-UTL-CAL-015"/>
</node>
</node>
</node>
<node COLOR="#009900" CREATED="1540626886871" ID="ID_547735555" MODIFIED="1540626915903" TEXT="Validate below NFRs">
<node COLOR="#009900" CREATED="1540626905399" ID="ID_1885088232" MODIFIED="1540626915903" TEXT="Performance - Each utility function should not exceed 250 milliseconds"/>
<node COLOR="#009900" CREATED="1540626905399" ID="ID_1460621255" MODIFIED="1540626915903" TEXT="Scalability - Should support 10000 concurrent request"/>
<node COLOR="#009900" CREATED="1540626905399" ID="ID_1915866453" MODIFIED="1540626915903" TEXT="Extensibility - Should be able to add new functionalities"/>
</node>
</node>
</node>
</node>
</map>
