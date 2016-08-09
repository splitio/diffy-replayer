# Diffy Replayer

Module for replaying Rest API endpoint with [Twitter's Diffy](https://github.com/twitter/diffy).

Diffy is a tool that lets you execute GET requests between two servers, in general one of them is your production server and the other one is the "candidate" server that contains the new code that will be tested. Diffy multicasts the requests that it receives into both servers, compares the responses and displays on a dashboard if any differences were found.  

Here at [Split.io](http://www.split.io/) we use to certify our different micro services.

## What is Diffy Replayer?

Diffy Replayer is the module we use to intercept request made to our production JAVA Restful servers and replay them through Twitter's Diffy.  
Set up is pretty straightforward:  

* Simply add [DiffyReplayerFilter](https://github.com/splitio/diffy-replayer/blob/master/src/main/java/io/split/diffyreplayer/DiffyReplayerFilter.java) to your JAVA Restful Server (if you are using Guice, simply binding the [DiffyReplayerModule](https://github.com/splitio/diffy-replayer/blob/master/src/main/java/io/split/diffyreplayer/DiffyReplayerModule.java), if not you can take a look at [This blog](http://blog.dejavu.sk/2013/11/19/registering-resources-and-providers-in-jersey-2/) for some ideas on how to configure a filter)
* Add to your resources directory the [_diffyreplayer.properties.dev_](https://github.com/splitio/diffy-replayer/blob/master/src/main/resources/diffyreplayer.properties.dev) file. Here you will specify where the Diffy Server is running.
* Add the [_@DiffyReplay_](https://github.com/splitio/diffy-replayer/blob/master/src/main/java/io/split/diffyreplayer/DiffyReplay.java) annotation to the GET endpoint that you want to replay.

That is all, once deployed queries to the specified endpoint will be sent to Diffy Server!



