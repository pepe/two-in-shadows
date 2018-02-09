# Shadow CLJS - Two In Shadows

This is sample project with both client and server application developed and
build with [shadow-cljs][1]. The client app uses [rum][2] React wrapper for
rendering, the server app uses [koa][3] http server. 

## Develop

Clone this repo and change dir to the created one.

In the terminal run in separate window/tabs/panes:

1. npm dependencies `npm install`
2. shadow-cljs helper server `shadow-cljs server start`
3. shadow-cljs client and server build watcher `shadow-cljs watch client server`
4. Koa server `node out/server.js`

This is enough to get two http server running for your needs as developer.
Server app runs on port 8270, client app server runs on port 8280.

### Repls

TBD


[1]: https://github.com/thheller/shadow-cljs
[2]: https://github.com/tonsky/rum
[3]: http://koajs.com/

Copyright © 2018 Josef Pospíšil

Distributed under the Eclipse Public License either version 1.0 or (at your
option) any later version.
