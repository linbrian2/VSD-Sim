package com.iai.traffic

import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.runtime.Micronaut
import groovy.transform.CompileStatic
import io.micronaut.runtime.server.event.ServerStartupEvent

@CompileStatic
class Application implements ApplicationEventListener<ServerStartupEvent> {
    static void main(String[] args) {
        Micronaut.run(Application)
    }

    @Override
    public void onApplicationEvent(ServerStartupEvent event) {
        //print("${micronaut.security.token.jwt.bearer.enabled}")
    }
}