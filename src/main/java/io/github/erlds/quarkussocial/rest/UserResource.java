package io.github.erlds.quarkussocial.rest;

import io.github.erlds.quarkussocial.rest.dto.CreateUserRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @POST
    @Consumes("application/json")
    public Response createUser(CreateUserRequest userRequest){
        return Response.ok(userRequest).build();
    }
}
