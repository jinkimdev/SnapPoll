SnapPoll
========
A Mobile Polling Interaction with Visual Reference

** Development in progress ** Update 12/9/2014

Jinhyun Kim (dev.jinkim@gmail.com)



##Overview

Instead of writing out all the answer choices for a poll, attach a picture reference that participants can see and use to indicate their responses.

More info including possible use cases and architecture is [in the wiki](https://github.com/jinkim608/SnapPoll/wiki).

##Components

####Node.js REST API
API end point used in the demo: http://snappoll.herokuapp.com/api

API calls are documented [in this page](https://github.com/jinkim608/SnapPoll/wiki/REST-API-on-Node.js).

####PostgreSQL Backend Database
DB contains users, polls, responses tables

####Android Client
A user can create and upload a poll, with a visual reference (picture or screenshot) and a question attached. User's friends invited through social media platform can participate and upload their responses to the server. A user taps on the location of interest in the picture and sends the response.

A user can see the result of popular votes (aggregated responses from poll participants) in a type of visualization such as a heat map.
