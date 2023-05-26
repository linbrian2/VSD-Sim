package com.iai.traffic.repository.traffic

import com.iai.traffic.domain.traffic.ReleaseNotes
import groovy.transform.CompileStatic
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@CompileStatic
@Repository
interface ReleaseNotesRepository extends CrudRepository<ReleaseNotes, Long> {
    Optional<ReleaseNotes> findByVersionNumber(String version)
}