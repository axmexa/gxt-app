# gxt-app #

The application is written as the test work.

Implemented in java using GXT library on the client and server to use Spring Framevork.

The application allows you to move the lines from the left list to the right tree view.
The interface is clear and convenient displaced. It can be used as Drag-n-Drop, buttons and double-click.
The right tree can be saved in a database format MongoDB preserving the folder structure. You can load a previously saved tree in the right tree.

### Need to do ###
- whole refactoring (GxtappEntryPoint.class is tooooo fat and have HIGH Coupling)
- resolve the issue of exceptions (correct throwing & catching)
- add tests
- ...?

## Config ##
- DB connection config is in [GxtAppConfig.java](https://github.com/axmexa/gxt-app/blob/master/src/main/java/com/axmexa/gxtapp/server/config/GxtAppConfig.java)
- I use one property for fast compile. In  [Gxtapp.gwt.xml](https://github.com/axmexa/gxt-app/blob/master/src/main/java/com/axmexa/gxtapp/Gxtapp.gwt.xml) you can comment this line if required
```
< set-property name="user.agent" value="safari"/>
```


