package io.github.erlds.quarkussocial.rest;

import io.github.erlds.quarkussocial.domain.model.Follower;
import io.github.erlds.quarkussocial.domain.model.User;
import io.github.erlds.quarkussocial.domain.repository.FollowerRepository;
import io.github.erlds.quarkussocial.domain.repository.UserRepository;
import io.github.erlds.quarkussocial.rest.dto.FollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;

    Long userId;
    Long followerId;

    @BeforeEach
    @Transactional
    public void setup() {
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");
        userRepository.persist(user);
        userId = user.getId();


        var follower = new User();
        follower.setAge(34);
        follower.setName("Cicrano");

        userRepository.persist(follower);

        followerId = follower.getId();

        var followerEntity = new Follower();
        followerEntity.setFollower(follower);
        followerEntity.setUser(user);
        followerRepository.persist(followerEntity);
    }

    @Test
    @DisplayName("should return 409 when followerId is equal to User id")
    public void sameUserAsFollowerTest() {
        var body = new FollowerRequest();
        body.setFollowerId(userId);
        given().contentType(ContentType.JSON)
                .body(body).pathParam("userId",userId)
                .when().put()
                .then().statusCode(Response.Status.CONFLICT.getStatusCode())
                .body(Matchers.is("You can't follow yourself !"));
    }

    @Test
    @DisplayName("should return 404 when User id doesn't exist when trying to follow user")
    public void userNotFoundWhenTryingToFollowTest() {
        var body = new FollowerRequest();
        body.setFollowerId(userId);

        var nonexistentUserId = 999;
        given().contentType(ContentType.JSON)
                .body(body).pathParam("userId",nonexistentUserId)
                .when().put()
                .then().statusCode(Response.Status.NOT_FOUND.getStatusCode());

    }

    @Test
    @DisplayName("should follow user")
    public void followUserTest() {
        var body = new FollowerRequest();
        body.setFollowerId(followerId);

        given().contentType(ContentType.JSON).body(body)
                .pathParam("userId",userId)
                .when().put()
                .then().statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DisplayName("should return 404 when list user followers and user doesn't exist")
    public void userNotFoundWhenListingFollowersTest() {
        var nonexistentUserId = 999;
        given().contentType(ContentType.JSON)
                .pathParam("userId",nonexistentUserId)
                .when().get()
                .then().statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should list a user's followers")
    public void listFollowersTest() {
        var response = given().contentType(ContentType.JSON)
                .pathParam("userId",userId)
                .when().get()
                .then().extract().response();

        var followersCount = response.jsonPath().get("followersCount");
        var followerContent = response.jsonPath().getList("content");

        assertEquals(Response.Status.OK.getStatusCode(),response.getStatusCode());
        assertEquals(1,followersCount);
        assertEquals(1,followerContent.size());
    }

    @Test
    @DisplayName("should return 404 on unfollow user and user id doesn't exist")
    public void userNotFoundWhenUnfollowingAUserTest() {
        var nonexistentUserId = 999;
        given().queryParam("followerId",followerId)
                .pathParam("userId",nonexistentUserId)
                .when().delete()
                .then().statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should unfollow an user")
    public void unfollowUserTest() {
        given().queryParam("followerId",followerId)
                .pathParam("userId",userId)
                .when().delete()
                .then().statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }
}