[![Build Status](https://travis-ci.org/Sammers21/IRC.svg?branch=master)](https://travis-ci.org/Sammers21/IRC)
[![codecov](https://codecov.io/gh/Sammers21/IRC/branch/master/graph/badge.svg)](https://codecov.io/gh/Sammers21/IRC)

### Building

In order to build the jar, execute:
```bash
$ ./gradlew jar
```
### Running
In order to run the server, execute:
```bash
$ java -jar ./build/libs/IRC.jar 8000
```

Note, that 8000 is the port, the application is going to listen. Feel free to change this if you want.

### Usage example

<a href="https://ibb.co/HpSK1Fb"><img src="https://i.ibb.co/pr71G0M/photo-2020-10-27-19-30-13.jpg" alt="photo-2020-10-27-19-30-13" border="0"></a>

### List of commands

Command set for this server:
```txt
/login name password — if user not exists create profile else login
/join channel — try to join channel (max 1000 active clients per channel is allowed).
  If client’s limit exceeded - error is send, otherwise join channel and send last 100000 messages of activity.
  If room not exits - it is created first then try to join.
/leave - disconnect client
/users — show users in the channel
```
