/*
 * Copyright (c) 2020 掘艺网络(jueyi.co).
 * All rights reserved.
 */

package co.jueyi.geekshop.config.auth;

import co.jueyi.geekshop.common.RequestContext;
import co.jueyi.geekshop.common.utils.BeanMapper;
import co.jueyi.geekshop.entity.UserEntity;
import co.jueyi.geekshop.service.helper.ExternalAuthenticationService;
import co.jueyi.geekshop.service.helper.input.CreateCustomerAndUserInput;
import co.jueyi.geekshop.types.user.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created on Nov, 2020 by @author bobo
 */
public class TestAuthenticationStrategy implements AuthenticationStrategy<TestAuthPayload> {

    public static final String VALID_AUTH_TOKEN = "valid-auth-token";
    public static final String STRATEGY_NAME = "test_strategy";

    @Autowired
    private ExternalAuthenticationService externalAuthenticationService;

    @Override
    public String getName() {
        return STRATEGY_NAME;
    }

    @Override
    public TestAuthPayload convertInputToData(Map<String, String> inputMap) {
        TestAuthPayload testAuthPayload = new TestAuthPayload();
        testAuthPayload.setToken(inputMap.get("token"));
        UserData userData = new UserData();
        userData.setEmail(inputMap.get("userData.email"));
        userData.setFirstName(inputMap.get("userData.firstName"));
        userData.setLastName(inputMap.get("userData.lastName"));
        testAuthPayload.setUserData(userData);
        return testAuthPayload;
    }

    @Override
    public User authenticate(RequestContext ctx, TestAuthPayload data) {
        if (!VALID_AUTH_TOKEN.equals(data.getToken())) {
            return null;
        }
        UserEntity userEntity = this.externalAuthenticationService.findUserEntity(this.getName(), data.getToken());
        if (userEntity != null) return BeanMapper.map(userEntity, User.class);

        CreateCustomerAndUserInput input = new CreateCustomerAndUserInput();
        input.setStrategy(this.getName());
        input.setExternalIdentifier(data.getToken());
        input.setEmailAddress(data.getUserData().getEmail());
        input.setFirstName(data.getUserData().getFirstName());
        input.setLastName(data.getUserData().getLastName());
        input.setVerified(true);
        userEntity = this.externalAuthenticationService.createCustomerAndUser(ctx, input);
        return BeanMapper.map(userEntity, User.class);
    }

    @Override
    public void onLogOut(User user) {
        // nothing todo
    }
}
