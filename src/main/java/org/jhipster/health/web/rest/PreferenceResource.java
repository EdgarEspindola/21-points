package org.jhipster.health.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.jhipster.health.domain.Preference;

import org.jhipster.health.domain.User;
import org.jhipster.health.repository.PreferenceRepository;
import org.jhipster.health.repository.UserRepository;
import org.jhipster.health.repository.search.PreferenceSearchRepository;
import org.jhipster.health.security.AuthoritiesConstants;
import org.jhipster.health.security.SecurityUtils;
import org.jhipster.health.web.rest.util.HeaderUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Preference.
 */
@RestController
@RequestMapping("/api")
public class PreferenceResource {

    private final Logger log = LoggerFactory.getLogger(PreferenceResource.class);

    @Inject
    private PreferenceRepository preferenceRepository;

    @Inject
    private PreferenceSearchRepository preferenceSearchRepository;

    @Inject
    private UserRepository userRepository;

    /**
     * POST  /preferences : Create a new preference.
     *
     * @param preference the preference to create
     * @return the ResponseEntity with status 201 (Created) and with body the new preference, or with status 400 (Bad Request) if the preference has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/preferences")
    @Timed
    public ResponseEntity<Preference> createPreference(@Valid @RequestBody Preference preference) throws URISyntaxException {
        log.debug("REST request to save Preference : {}", preference);
        if (preference.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("preference", "idexists", "A new preference cannot already have an ID")).body(null);
        }
        Preference result = preferenceRepository.save(preference);
        preferenceSearchRepository.save(result);

        log.debug("Settings preferences for current user: {}", SecurityUtils.getCurrentUserLogin());
        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
        user.setPreferences(result);
        userRepository.save(user);

        return ResponseEntity.created(new URI("/api/preferences/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("preference", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /preferences : Updates an existing preference.
     *
     * @param preference the preference to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated preference,
     * or with status 400 (Bad Request) if the preference is not valid,
     * or with status 500 (Internal Server Error) if the preference couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/preferences")
    @Timed
    public ResponseEntity<Preference> updatePreference(@Valid @RequestBody Preference preference) throws URISyntaxException {
        log.debug("REST request to update Preference : {}", preference);
        if (preference.getId() == null) {
            return createPreference(preference);
        }
        Preference result = preferenceRepository.save(preference);
        preferenceSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("preference", preference.getId().toString()))
            .body(result);
    }

    /**
     * GET  /preferences : get all the preferences.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of preferences in body
     */
    @GetMapping("/preferences")
    @Timed
    public List<Preference> getAllPreferences() {
        log.debug("REST request to get all Preferences");
        List<Preference> preferences = new ArrayList<>();
        if(!SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)) {
            preferences = preferenceRepository.findAll();
        } else {
            Preference userPreferences = getUserPreferences().getBody();
            // don't return default value of 10 points in this method
            if (userPreferences.getId() != null) {
                preferences.add(userPreferences);
            }
        }

        return preferences;
    }

    /**
     * GET  /preferences/:id : get the "id" preference.
     *
     * @param id the id of the preference to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the preference, or with status 404 (Not Found)
     */
    @GetMapping("/preferences/{id}")
    @Timed
    public ResponseEntity<Preference> getPreference(@PathVariable Long id) {
        log.debug("REST request to get Preference : {}", id);
        Preference preference = preferenceRepository.findOne(id);
        return Optional.ofNullable(preference)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET /my-preferences -> get the current user's preferences
     */
    @GetMapping(value = "/my-preferences",
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Preference> getUserPreferences() {
        String username = SecurityUtils.getCurrentUserLogin();
        log.debug("REST request to get Preferences : {}", username);
        User user = userRepository.findOneByLogin(username).get();

        if(user.getPreferences() != null) {
            return new ResponseEntity<Preference>(user.getPreferences(), HttpStatus.OK);
        } else {
            Preference defaultPreference = new Preference();
            defaultPreference.setWeeklyGoal(10); //default
            return new ResponseEntity<Preference>(defaultPreference, HttpStatus.OK);
        }
    }




    /**
     * DELETE  /preferences/:id : delete the "id" preference.
     *
     * @param id the id of the preference to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/preferences/{id}")
    @Timed
    public ResponseEntity<Void> deletePreference(@PathVariable Long id) {
        log.debug("REST request to delete Preference : {}", id);

        if(SecurityUtils.getCurrentUserLogin() != null) {
            User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
            user.setPreferences(null);
            userRepository.save(user);
        }

        preferenceRepository.delete(id);
        preferenceSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("preference", id.toString())).build();
    }

    /**
     * SEARCH  /_search/preferences?query=:query : search for the preference corresponding
     * to the query.
     *
     * @param query the query of the preference search
     * @return the result of the search
     */
    @GetMapping("/_search/preferences")
    @Timed
    public List<Preference> searchPreferences(@RequestParam String query) {
        log.debug("REST request to search Preferences for query {}", query);
        return StreamSupport
            .stream(preferenceSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }


}
