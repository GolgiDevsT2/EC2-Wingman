# EC2 Wingman
EC2 Wingman is an application for iOS and Android that monitors all your
AWS EC2 instances and generates alerts when certain issues occur (like
elevated CPU usage, status check failures and state changes). What you get
alerted about is configureable and filterable on the mobile.

The server portion of the soloution is a Node.js application that monitors
the state of all your EC2 instances and sends alerts to any interested
mobile devices when something of interest takes place.

This GitHub project contains all the source code needed to build and
run the application. All that is required to do this is the following:
### Preparation

* Register for a developer account (it's free) at [devs.golgi.io](https://devs.golgi.io)
* Generate a new Application Key on the developer portal
* Download and install the SDK from the developer portal.
* Clone this repository
* Create a file called ```Golgi.DevKey``` containing a single line (the Developer Key you were assigned)
* Create another file called ```Golgi.AppKey``` containing a single line (the Application Key assigned to your new application).

### Build the Server
TBD: Server side config

### Build the Android Application
TBD: Android application building

### Build the iOS Application
TBD: iOS Application building in Xcode

