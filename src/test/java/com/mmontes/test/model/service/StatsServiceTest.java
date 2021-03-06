package com.mmontes.test.model.service;

import com.mmontes.model.service.*;
import com.mmontes.util.GeometryUtils;
import com.mmontes.util.dto.MetricDto;
import com.mmontes.util.dto.StatsDto;
import com.mmontes.util.dto.TIPDetailsDto;
import com.mmontes.util.exception.InstanceNotFoundException;
import com.vividsolutions.jts.geom.Geometry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mmontes.test.util.Constants.*;
import static com.mmontes.util.Constants.SPRING_CONFIG_FILE;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {SPRING_CONFIG_FILE, SPRING_CONFIG_TEST_FILE})
@Transactional
@SuppressWarnings("all")
public class StatsServiceTest {

    private static TIPDetailsDto towerHercules;
    private static TIPDetailsDto alameda;
    private static TIPDetailsDto cathedral;
    private static Date initialDate;
    private static Date middleRatingDate;
    private static Date middleCommentDate;
    private static Date endDate;
    @Autowired
    private StatsService statsService;
    @Autowired
    private TIPService tipService;
    @Autowired
    private RatingService ratingService;
    @Autowired
    private CommentService commentService;

    @Before
    public void createData() {
        try {
            initialDate = new Date();

            String name = "Tower of Hercules";
            String description = "Human Patrimony";
            Geometry geom = GeometryUtils.geometryFromWKT(POINT_TORRE_HERCULES);
            towerHercules = tipService.create(MONUMENT_DISCRIMINATOR, name, description, VALID_TIP_PHOTO_URL, VALID_TIP_INFO_URL, geom, null, true, null);

            name = "Alameda Santiago de Compostela";
            description = "Sitio verde";
            geom = GeometryUtils.geometryFromWKT(POINT_ALAMEDA);
            alameda = tipService.create(NATURAL_SPACE_DISCRIMINATOR, name, description, VALID_TIP_PHOTO_URL, VALID_TIP_INFO_URL, geom, null, true, null);

            name = "Catedral Santiago de Compostela";
            description = "Sitio de peregrinacion";
            geom = GeometryUtils.geometryFromWKT(POINT_CATEDRAL_SANTIAGO);
            cathedral = tipService.create(MONUMENT_DISCRIMINATOR, name, description, VALID_TIP_PHOTO_URL, VALID_TIP_INFO_URL, geom, null, true, null);

            ratingService.rate(2D, towerHercules.getId(), EXISTING_FACEBOOK_USER_ID);
            ratingService.rate(3D, towerHercules.getId(), EXISTING_FACEBOOK_USER_ID2);

            ratingService.rate(3D, alameda.getId(), EXISTING_FACEBOOK_USER_ID);
            ratingService.rate(4D, alameda.getId(), EXISTING_FACEBOOK_USER_ID2);

            middleRatingDate = new Date();

            ratingService.rate(5D, cathedral.getId(), EXISTING_FACEBOOK_USER_ID);
            ratingService.rate(5D, cathedral.getId(), EXISTING_FACEBOOK_USER_ID2);

            commentService.comment("Bad", EXISTING_FACEBOOK_USER_ID, towerHercules.getId());
            commentService.comment("Bad", EXISTING_FACEBOOK_USER_ID2, towerHercules.getId());

            commentService.comment("More or Less", EXISTING_FACEBOOK_USER_ID, alameda.getId());
            commentService.comment("More or Less", EXISTING_FACEBOOK_USER_ID, alameda.getId());
            commentService.comment("More or Less", EXISTING_FACEBOOK_USER_ID2, alameda.getId());
            commentService.comment("More or Less", EXISTING_FACEBOOK_USER_ID2, alameda.getId());

            middleCommentDate = new Date();

            commentService.comment("Awesome", EXISTING_FACEBOOK_USER_ID, cathedral.getId());
            commentService.comment("Awesome", EXISTING_FACEBOOK_USER_ID, cathedral.getId());
            commentService.comment("Awesome", EXISTING_FACEBOOK_USER_ID, cathedral.getId());
            commentService.comment("Awesome", EXISTING_FACEBOOK_USER_ID2, cathedral.getId());
            commentService.comment("Awesome", EXISTING_FACEBOOK_USER_ID2, cathedral.getId());
            commentService.comment("Awesome", EXISTING_FACEBOOK_USER_ID2, cathedral.getId());

            endDate = new Date();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void getMetrics() {
        for (MetricDto metricDto : statsService.getAllMetrics()) {
            assertNotNull(metricDto.getId());
            assertNotNull(metricDto.getName());
        }
    }

    @Test(expected = InstanceNotFoundException.class)
    public void getNonExistingMetricStats() throws InstanceNotFoundException {
        statsService.getStats(NON_EXISTING_METRIC_ID,null,null,null);
    }

    @Test
    public void getBestRatedStats() {
        try {
            StatsDto statsDto = statsService.getStats(BEST_RATED_METRIC_ID,null,null,null);
            assertNotNull(statsDto);
            assertEquals(3,statsDto.getData().size());

            ArrayList<Long> tipIDs = new ArrayList<Long>(){{
                add(towerHercules.getId());
            }};
            statsDto = statsService.getStats(BEST_RATED_METRIC_ID,tipIDs,null,null);
            assertNotNull(statsDto);
            assertEquals(1,statsDto.getData().size());

            statsDto = statsService.getStats(BEST_RATED_METRIC_ID,null,initialDate,endDate);
            assertNotNull(statsDto);
            assertEquals(3,statsDto.getData().size());

            statsDto = statsService.getStats(BEST_RATED_METRIC_ID,null,initialDate,middleRatingDate);
            assertNotNull(statsDto);
            assertEquals(2,statsDto.getData().size());

            statsDto = statsService.getStats(BEST_RATED_METRIC_ID,tipIDs,initialDate,middleRatingDate);
            assertNotNull(statsDto);
            assertEquals(1,statsDto.getData().size());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void getMostCommentedStats() {
        try {
            StatsDto statsDto = statsService.getStats(MOST_COMMENTED_METRIC_ID,null,null,null);
            assertNotNull(statsDto);
            assertEquals(3,statsDto.getData().size());

            ArrayList<Long> tipIDs = new ArrayList<Long>(){{
                add(towerHercules.getId());
            }};
            statsDto = statsService.getStats(MOST_COMMENTED_METRIC_ID,tipIDs,null,null);
            assertNotNull(statsDto);
            assertEquals(1,statsDto.getData().size());

            statsDto = statsService.getStats(MOST_COMMENTED_METRIC_ID,null,initialDate,endDate);
            assertNotNull(statsDto);
            assertEquals(3,statsDto.getData().size());

            statsDto = statsService.getStats(MOST_COMMENTED_METRIC_ID,null,initialDate,middleCommentDate);
            assertNotNull(statsDto);
            assertEquals(2,statsDto.getData().size());

            statsDto = statsService.getStats(MOST_COMMENTED_METRIC_ID,tipIDs,initialDate,middleCommentDate);
            assertNotNull(statsDto);
            assertEquals(1,statsDto.getData().size());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
