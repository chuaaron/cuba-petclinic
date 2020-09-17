/*
 * Copyright (c) 2020 Haulmont. All rights reserved.
 */

package com.haulmont.sample.petclinic.web.masquerade.ui

import com.codeborne.selenide.WebDriverRunner

import static com.codeborne.selenide.Condition.visible
import static com.codeborne.selenide.Selenide.$
import static com.codeborne.selenide.Selenide.$x
import static com.haulmont.masquerade.Components.wire
import static com.haulmont.masquerade.Selectors.$c
import static com.haulmont.masquerade.Selectors.byCubaId

class MainWindow {

    static <T > T switchTab(String cubaId, Class<T> clazz) {
        $c("$cubaId")
                .shouldBe(visible)
                .click()
        return wire(clazz)
    }

    /**
     * Resets current session cookie.
     */
    static void disconnect() {
        WebDriverRunner.getWebDriver().manage()
                .deleteCookieNamed('JSESSIONID')
    }

    static LoginWindow logout() {
        $(byCubaId('logoutButton'))
                .shouldBe(visible)
                .click()

        wire(LoginWindow)
    }
}