<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1550386714894" ID="ID_1674102245" MODIFIED="1550387379304" TEXT="Registration - Face / Exception Recapture">
<node CREATED="1550386751654" ID="ID_95783531" LINK="https://mosipid.atlassian.net/browse/MOS-1177" MODIFIED="1550387486893" POSITION="right" TEXT="MOS-1177">
<edge COLOR="#66cc00"/>
<node COLOR="#66cc00" CREATED="1550386929273" HGAP="14" ID="ID_1229946467" MODIFIED="1550387537442" TEXT="Retry count available / stores in DB" VSHIFT="-20">
<node COLOR="#66cc00" CREATED="1550386963347" ID="ID_213393610" MODIFIED="1550387537441" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1550386963347" ID="ID_150883811" MODIFIED="1550387537441" TEXT="Verify the count matches"/>
</node>
<node COLOR="#ff9900" CREATED="1550386985724" ID="ID_1627230752" MODIFIED="1550387563115" TEXT="No">
<node COLOR="#ff0000" CREATED="1550386993462" ID="ID_817968619" MODIFIED="1550387576758" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#66cc00" CREATED="1550387023638" HGAP="18" ID="ID_1439462861" MODIFIED="1550387537442" TEXT="Quality score behavior after retry" VSHIFT="-41">
<arrowlink DESTINATION="ID_806196482" ENDARROW="Default" ENDINCLINATION="191;0;" ID="Arrow_ID_139706900" STARTARROW="None" STARTINCLINATION="191;0;"/>
<node COLOR="#66cc00" CREATED="1550387421251" ID="ID_1039931479" MODIFIED="1550387537441" TEXT="Below threshold">
<node COLOR="#66cc00" CREATED="1550387041875" ID="ID_1495337472" MODIFIED="1552036858001" TEXT="Update the existing score whichever is greater (both image and score)">
<node COLOR="#66cc00" CREATED="1550386963347" ID="ID_555244605" MODIFIED="1550387537433" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1550386963347" ID="ID_468854930" MODIFIED="1550387537432" TEXT="Verify the score gets updated in DB"/>
</node>
<node COLOR="#ff9900" CREATED="1550386985724" ID="ID_125322190" MODIFIED="1550387563116" TEXT="No">
<node COLOR="#ff0000" CREATED="1550386993462" ID="ID_757170442" MODIFIED="1550387576759" TEXT="Raise a defect"/>
</node>
</node>
</node>
<node COLOR="#66cc00" CREATED="1550387437350" ID="ID_365305490" MODIFIED="1550387537440" TEXT="Above threshold">
<node COLOR="#66cc00" CREATED="1550387455863" ID="ID_1114083579" MODIFIED="1550387537434" TEXT="The system should not allow further capture if a photo of sufficient quality is obtained -that is, quality score is greater than or equal to the threshold."/>
</node>
</node>
<node COLOR="#66cc00" CREATED="1550387086801" ID="ID_731945071" MODIFIED="1550387537443" TEXT="RC behavior when retry">
<node COLOR="#66cc00" CREATED="1550387106250" ID="ID_12437708" MODIFIED="1550387537443" TEXT="Time delay between each retry">
<node COLOR="#66cc00" CREATED="1550386963347" ID="ID_1282708478" MODIFIED="1550387537431" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1550386963347" ID="ID_40856871" MODIFIED="1550387537432" TEXT="Verify the time delay works as set in DB"/>
</node>
<node COLOR="#ff9900" CREATED="1550386985724" ID="ID_482270096" MODIFIED="1550387563116" TEXT="No">
<node COLOR="#ff0000" CREATED="1550386993462" ID="ID_637696411" MODIFIED="1550387576759" TEXT="Raise a defect"/>
</node>
</node>
<node COLOR="#66cc00" CREATED="1550387162094" ID="ID_806196482" MODIFIED="1550387537431" TEXT="Exceeds the max limit" VSHIFT="21">
<linktarget COLOR="#b0b0b0" DESTINATION="ID_806196482" ENDARROW="Default" ENDINCLINATION="191;0;" ID="Arrow_ID_139706900" SOURCE="ID_1439462861" STARTARROW="None" STARTINCLINATION="191;0;"/>
<node COLOR="#66cc00" CREATED="1550386963347" ID="ID_1909268862" MODIFIED="1550387537430" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1550386963347" ID="ID_379863092" MODIFIED="1550387537429" TEXT="Display &#x201c;You have reached the maximum number of retries. Click on Next to proceed with forced capture.&#x201d;"/>
</node>
<node COLOR="#ff9900" CREATED="1550386985724" ID="ID_565073355" MODIFIED="1550387563117" TEXT="No">
<node COLOR="#ff0000" CREATED="1550386993462" ID="ID_1925542904" MODIFIED="1550387576760" TEXT="Allow the user to retry after X seconds as config"/>
</node>
<node COLOR="#ff9900" CREATED="1550387289981" HGAP="14" ID="ID_1058148685" MODIFIED="1550387563117" TEXT="Still Quality does not met the threshold" VSHIFT="28">
<node COLOR="#ff9900" CREATED="1550386963347" ID="ID_35084642" MODIFIED="1550387563116" TEXT="Yes">
<node COLOR="#66cc00" CREATED="1550386963347" ID="ID_1989497576" MODIFIED="1550387588932" TEXT="The best quality photo should be retained. The best photo should be displayed on screen along with its quality score."/>
</node>
</node>
</node>
</node>
<node COLOR="#00cc00" CREATED="1538550071542" HGAP="9" ID="ID_1021783307" MODIFIED="1546498495098" TEXT="Verification of Txn details for Audit purpose" VSHIFT="34">
<edge COLOR="#00cc00"/>
<node COLOR="#00cc00" CREATED="1539003087723" ID="ID_163075645" MODIFIED="1542005044938" TEXT="System capture all Txn details">
<node COLOR="#00cc00" CREATED="1539003122483" ID="ID_1629456931" MODIFIED="1546498515692" TEXT="Store all the details under &quot;Audit_Log&quot; table such as User id or system account; Machine Details; Event Name; Application Name, and Event data including user entered fields."/>
</node>
<node COLOR="#ff0000" CREATED="1539060442854" ID="ID_1191661596" MODIFIED="1542005044938" TEXT="System fails to capture Txn details">
<edge COLOR="#ff0000"/>
<node COLOR="#ff0000" CREATED="1539089688847" ID="ID_795182154" MODIFIED="1542005044938" TEXT="Display appropriate error message / Raise a defect"/>
</node>
</node>
</node>
</node>
</map>
