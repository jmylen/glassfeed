GlassFeedApp
============
- A fork of the quickstart app for Java.
https://developers.google.com/glass/quickstart/java

## Application Prerequisites
* Java 1.6
* Apache Maven greater than 3.1
* Add oAuth Client ID and Client Secret
    * Create a oauth.properties file in /src/main/resources/
    * Add the following to oauth.properties adding your client_id and secret.

    client_id=YOUR_CLIENT_ID<br>
    client_secret=YOUR_CLIENT_SECRET

## Commands
* Run locally: mvn jetty:run
* Build war file: mvn war:war
* Deploy to AppEngine: mvn appengine:update


## License
Code for this project is licensed under [APL 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
and content is licensed under the
[Creative Commons Attribution 3.0 License](http://creativecommons.org/licenses/by/3.0/).
