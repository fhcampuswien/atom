atom - Advanced Transparent Object Manager
====

atom is a web-based CRUD-Tool.<br\>
The Metadata = Data-Structure can be defined centrally in the sub-project atom-domain.
An example of such a definition can be found here:
https://github.com/fhcampuswien/atom/blob/master/atom-domain/src/main/java/at/ac/fhcampuswien/atom/shared/domain/Message.java

These data-structures are persisted by hibernate into a relational database. The connection information needs to be entered here:
https://github.com/fhcampuswien/atom/blob/master/atom-server/src/main/resources/META-INF/persistence.xml

The GWT web-frontend can work with arbritary data-structures through the help of reflection (enabled by the project gwt-ent https://code.google.com/p/gwt-ent/)

====

There's a live demo (with only one defined demo DomainObject "Message") you can look at here:
https://atomdemo.cloudapp.net

and yes there are a lot of german strings in there (since our users are german speakers) but they can easily translated by adapting
https://github.com/fhcampuswien/atom/blob/master/atom-core/src/main/resources/at/ac/fhcampuswien/atom/shared/AtomMessages.properties
and / or:
https://github.com/fhcampuswien/atom/blob/master/atom-core/src/main/resources/at/ac/fhcampuswien/atom/shared/AtomMessages_en.properties

====

The motivation of this project is to allow easy data management of data with quickly evolving data structures.
This is achieved by eliminating the need to manually propagate changing metadata through all tiers.
The developer only needs to change the structure in one place, the "domain object" classes.

I've wrote up a developer setup guide you can find in the wiki tab here on this github page:
https://github.com/fhcampuswien/atom/wiki/Developer-Setup-Guide

(As you can see, I have not pushed any updates in a long time. This is not because development stopped, but because I don't see a point doing that work if as it looks to me right now nobody cares anyway. If sombody might stumble upon this repository who is actually interested in looking at the latest version of my project, just contact me by mail or enter an issue in githubs issue tracker here)

If you've got any questions, feel free to contact me by email: <br\>
thomas dot kaefer at fh minus campuswien dot ac dot at
