### Create snapshot and its thumbnail (With Get Request Method)
Open your browser.

Fill the address field with :

<code>http://localhost:8080/websnapshot-webapp-1.0-SNAPSHOT/?url=[url]&width=[size]&height=[size]&date=[date]&status=[boolean]</code>

* (Required Parameter) Replace [url] with the url that you want to create thumbnail.
* (Required Parameter) Replace [width] with the width that you want.
* (Required Parameter) Replace [height] with the height that you want.*
* (Optional parameter) Replace [date] with the date with format ([yyyy]-[MM]-[dd]T[HH]:[mm]:[ss]). That return the next thumbnail creating from the date.
  * Replace [yyyy] by the years (minus 1900)
  * Replace [MM] by the month (between 00 and 11)
  * Replace [dd] by the day (between 01 and 31)
  * Replace [HH] by the hours (between 00 and 23)
  * Replace [mm] by the minutes (between 0 and 59)
  * Replace [ss] by the seconds (between 0 and 59)
* (Optional parameter)