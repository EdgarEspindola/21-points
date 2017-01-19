package org.jhipster.health.web.rest;

import org.jhipster.health.Application;

import org.jhipster.health.domain.Preference;
import org.jhipster.health.repository.PreferenceRepository;
import org.jhipster.health.repository.search.PreferenceSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.jhipster.health.domain.enumeration.Units;
/**
 * Test class for the PreferenceResource REST controller.
 *
 * @see PreferenceResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class PreferenceResourceIntTest {

    private static final Integer DEFAULT_WEEKLY_GOAL = 10;
    private static final Integer UPDATED_WEEKLY_GOAL = 11;

    private static final Units DEFAULT_WEIGHT_UNITS = Units.kg;
    private static final Units UPDATED_WEIGHT_UNITS = Units.lb;

    @Inject
    private PreferenceRepository preferenceRepository;

    @Inject
    private PreferenceSearchRepository preferenceSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restPreferenceMockMvc;

    private Preference preference;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PreferenceResource preferenceResource = new PreferenceResource();
        ReflectionTestUtils.setField(preferenceResource, "preferenceSearchRepository", preferenceSearchRepository);
        ReflectionTestUtils.setField(preferenceResource, "preferenceRepository", preferenceRepository);
        this.restPreferenceMockMvc = MockMvcBuilders.standaloneSetup(preferenceResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Preference createEntity(EntityManager em) {
        Preference preference = new Preference()
                .weeklyGoal(DEFAULT_WEEKLY_GOAL)
                .weightUnits(DEFAULT_WEIGHT_UNITS);
        return preference;
    }

    @Before
    public void initTest() {
        preferenceSearchRepository.deleteAll();
        preference = createEntity(em);
    }

    @Test
    @Transactional
    public void createPreference() throws Exception {
        int databaseSizeBeforeCreate = preferenceRepository.findAll().size();

        // Create the Preference

        restPreferenceMockMvc.perform(post("/api/preferences")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(preference)))
            .andExpect(status().isCreated());

        // Validate the Preference in the database
        List<Preference> preferenceList = preferenceRepository.findAll();
        assertThat(preferenceList).hasSize(databaseSizeBeforeCreate + 1);
        Preference testPreference = preferenceList.get(preferenceList.size() - 1);
        assertThat(testPreference.getWeeklyGoal()).isEqualTo(DEFAULT_WEEKLY_GOAL);
        assertThat(testPreference.getWeightUnits()).isEqualTo(DEFAULT_WEIGHT_UNITS);

        // Validate the Preference in ElasticSearch
        Preference preferenceEs = preferenceSearchRepository.findOne(testPreference.getId());
        assertThat(preferenceEs).isEqualToComparingFieldByField(testPreference);
    }

    @Test
    @Transactional
    public void createPreferenceWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = preferenceRepository.findAll().size();

        // Create the Preference with an existing ID
        Preference existingPreference = new Preference();
        existingPreference.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPreferenceMockMvc.perform(post("/api/preferences")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingPreference)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Preference> preferenceList = preferenceRepository.findAll();
        assertThat(preferenceList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkWeeklyGoalIsRequired() throws Exception {
        int databaseSizeBeforeTest = preferenceRepository.findAll().size();
        // set the field null
        preference.setWeeklyGoal(null);

        // Create the Preference, which fails.

        restPreferenceMockMvc.perform(post("/api/preferences")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(preference)))
            .andExpect(status().isBadRequest());

        List<Preference> preferenceList = preferenceRepository.findAll();
        assertThat(preferenceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkWeightUnitsIsRequired() throws Exception {
        int databaseSizeBeforeTest = preferenceRepository.findAll().size();
        // set the field null
        preference.setWeightUnits(null);

        // Create the Preference, which fails.

        restPreferenceMockMvc.perform(post("/api/preferences")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(preference)))
            .andExpect(status().isBadRequest());

        List<Preference> preferenceList = preferenceRepository.findAll();
        assertThat(preferenceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPreferences() throws Exception {
        // Initialize the database
        preferenceRepository.saveAndFlush(preference);

        // Get all the preferenceList
        restPreferenceMockMvc.perform(get("/api/preferences?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(preference.getId().intValue())))
            .andExpect(jsonPath("$.[*].weeklyGoal").value(hasItem(DEFAULT_WEEKLY_GOAL)))
            .andExpect(jsonPath("$.[*].weightUnits").value(hasItem(DEFAULT_WEIGHT_UNITS.toString())));
    }

    @Test
    @Transactional
    public void getPreference() throws Exception {
        // Initialize the database
        preferenceRepository.saveAndFlush(preference);

        // Get the preference
        restPreferenceMockMvc.perform(get("/api/preferences/{id}", preference.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(preference.getId().intValue()))
            .andExpect(jsonPath("$.weeklyGoal").value(DEFAULT_WEEKLY_GOAL))
            .andExpect(jsonPath("$.weightUnits").value(DEFAULT_WEIGHT_UNITS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPreference() throws Exception {
        // Get the preference
        restPreferenceMockMvc.perform(get("/api/preferences/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePreference() throws Exception {
        // Initialize the database
        preferenceRepository.saveAndFlush(preference);
        preferenceSearchRepository.save(preference);
        int databaseSizeBeforeUpdate = preferenceRepository.findAll().size();

        // Update the preference
        Preference updatedPreference = preferenceRepository.findOne(preference.getId());
        updatedPreference
                .weeklyGoal(UPDATED_WEEKLY_GOAL)
                .weightUnits(UPDATED_WEIGHT_UNITS);

        restPreferenceMockMvc.perform(put("/api/preferences")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPreference)))
            .andExpect(status().isOk());

        // Validate the Preference in the database
        List<Preference> preferenceList = preferenceRepository.findAll();
        assertThat(preferenceList).hasSize(databaseSizeBeforeUpdate);
        Preference testPreference = preferenceList.get(preferenceList.size() - 1);
        assertThat(testPreference.getWeeklyGoal()).isEqualTo(UPDATED_WEEKLY_GOAL);
        assertThat(testPreference.getWeightUnits()).isEqualTo(UPDATED_WEIGHT_UNITS);

        // Validate the Preference in ElasticSearch
        Preference preferenceEs = preferenceSearchRepository.findOne(testPreference.getId());
        assertThat(preferenceEs).isEqualToComparingFieldByField(testPreference);
    }

    @Test
    @Transactional
    public void updateNonExistingPreference() throws Exception {
        int databaseSizeBeforeUpdate = preferenceRepository.findAll().size();

        // Create the Preference

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPreferenceMockMvc.perform(put("/api/preferences")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(preference)))
            .andExpect(status().isCreated());

        // Validate the Preference in the database
        List<Preference> preferenceList = preferenceRepository.findAll();
        assertThat(preferenceList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deletePreference() throws Exception {
        // Initialize the database
        preferenceRepository.saveAndFlush(preference);
        preferenceSearchRepository.save(preference);
        int databaseSizeBeforeDelete = preferenceRepository.findAll().size();

        // Get the preference
        restPreferenceMockMvc.perform(delete("/api/preferences/{id}", preference.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean preferenceExistsInEs = preferenceSearchRepository.exists(preference.getId());
        assertThat(preferenceExistsInEs).isFalse();

        // Validate the database is empty
        List<Preference> preferenceList = preferenceRepository.findAll();
        assertThat(preferenceList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPreference() throws Exception {
        // Initialize the database
        preferenceRepository.saveAndFlush(preference);
        preferenceSearchRepository.save(preference);

        // Search the preference
        restPreferenceMockMvc.perform(get("/api/_search/preferences?query=id:" + preference.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(preference.getId().intValue())))
            .andExpect(jsonPath("$.[*].weeklyGoal").value(hasItem(DEFAULT_WEEKLY_GOAL)))
            .andExpect(jsonPath("$.[*].weightUnits").value(hasItem(DEFAULT_WEIGHT_UNITS.toString())));
    }
}
