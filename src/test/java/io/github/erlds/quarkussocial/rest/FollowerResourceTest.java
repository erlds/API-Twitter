package io.github.erlds.quarkussocial.rest;

import io.github.erlds.quarkussocial.domain.model.User;
import io.github.erlds.quarkussocial.domain.repository.UserRepository;
import io.github.erlds.quarkussocial.rest.dto.FollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepository;

    Long userId;

    @BeforeEach
    @Transactional
    public void setup() {
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");
        userRepository.persist(user);
        userId = user.getId();
    }

    @Test
    @DisplayName("should return 409 when followerId is equal to User id")
    public void sameUserAsFollowerTest() {
        var body = new FollowerRequest();
        body.setFollowerId(userId);
        given().contentType(ContentType.JSON)
                .body(body).pathParam("userId",userId)
                .when().put()
                .then().statusCode(409)
                .body(Matchers.is("You can't follow yourself !"));
    }

    @Test
    @DisplayName("should return 404 when User id doesn't exist")
    public void userIdDoesntExist() {
        var body = new FollowerRequest();
        body.setFollowerId(userId);

        var inexistentUserId = 999;
        given().contentType(ContentType.JSON)
                .body(body).pathParam("userId",inexistentUserId)
                .when().put()
                .then().statusCode(404);

    }
}