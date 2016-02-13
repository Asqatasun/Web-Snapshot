# Howto use Asqatasun-Web-Snapshot 

## Create snapshot and its thumbnail (With GET Request Method)
Open your browser.

Fill the address field with :

`http://localhost:8080/websnapshot-webapp-1.0-SNAPSHOT/?width=[size]&height=[size]&date=[date]&status=[boolean]url=[url]`

* (Required Parameter) Replace [width] with the width that you want.
* (Required Parameter) Replace [height] with the height that you want.*
* (Optional Parameter) Replace [date] with the date with format ([yyyy]-[MM]-[dd]T[HH]:[mm]:[ss]). That return the next thumbnail creating from the date.
  * Replace [yyyy] by the years (minus 1900)
  * Replace [MM] by the month (between 00 and 11)
  * Replace [dd] by the day (between 01 and 31)
  * Replace [HH] by the hours (between 00 and 23)
  * Replace [mm] by the minutes (between 0 and 59)
  * Replace [ss] by the seconds (between 0 and 59)
* (Optional Parameter) Replace [boolean] with `false` has no effect (if is not set too), just return the thumbnail. Replacing [boolean] with `true` returning a JSON with all informations about the thumbnail, and his status of creation.
* (Required Parameter) Replace [url] with the url that you want to create thumbnail. This parameter must be the last parameter to deal with errors cases or fragments in url (usage of `#` characters for example).

## Create snapshot and its thumbnail (With POST Request Method)

To explain this usage, we choose to use [cURL](http://fr.wikipedia.org/wiki/CURL) software to send POST request.

Open your console, and fill it with :

`curl -X POST -d "url=[url]&width=[width]&height=[height]&date=[date]&status=[boolean]" http://localhost:8080/websnapshot-webapp-1.0-SNAPSHOT`

This operation start the creation of the snapshot and its thumbnail and return instantly the status of creation of the thumbnail. If you send the same request, just the thumbnail will be create.

The parameters have the same working that the GET request : if you set [boolean] with `true`, the request return instantly a JSON with the informations.